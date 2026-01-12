package com.astro.news.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import androidx.room.withTransaction
import com.astro.news.data.local.AstroDatabase
import com.astro.news.data.local.entity.ArticleEntity
import com.astro.news.data.local.entity.RemoteKeysEntity
import com.astro.news.data.mapper.toEntity
import com.astro.news.data.remote.SpaceflightNewsApiService
import com.astro.news.domain.exception.mapToDomainError
import timber.log.Timber

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediator(
    private val api: SpaceflightNewsApiService,
    private val database: AstroDatabase,
    private val query: String? = null
) : RemoteMediator<Int, ArticleEntity>() {

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ArticleEntity>
    ): MediatorResult {
        return try {
            Timber.tag("ArticleRemoteMediator").d("Loading data: LoadType = $loadType")
            val offset = when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                    remoteKeys?.nextKey?.minus(state.config.pageSize) ?: 0
                }

                LoadType.PREPEND -> {
                    val remoteKeys = getRemoteKeyForFirstItem(state)
                    val prevKey = remoteKeys?.prevKey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )
                    prevKey
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey = remoteKeys?.nextKey ?: return MediatorResult.Success(
                        endOfPaginationReached = remoteKeys != null
                    )

                    if (remoteKeys.prevKey == null && state.pages.sumOf { it.data.size } > state.config.pageSize) {
                        state.pages.sumOf { it.data.size }
                    } else {
                        nextKey
                    }
                }
            }

            val response = api.getArticles(
                limit = state.config.pageSize,
                offset = offset,
                search = query
            )

            val articles = response.results
            val endOfPaginationReached = articles.isEmpty()

            database.withTransaction {
                if (loadType == LoadType.REFRESH) {
                    Timber.tag("ArticleRemoteMediator").d("Clearing database for Refresh")
                    database.remoteKeysDao.clearRemoteKeys()
                    database.articleDao.clearAll()
                }

                val prevKey = if (offset == 0) null else offset - state.config.pageSize
                val nextKey = if (endOfPaginationReached) null else offset + state.config.pageSize

                val keys = articles.map { article ->
                    RemoteKeysEntity(
                        articleId = article.id,
                        prevKey = prevKey,
                        nextKey = nextKey
                    )
                }

                val entities = articles.map { it.toEntity() }

                database.remoteKeysDao.insertAll(keys)
                database.articleDao.insertAll(entities)
                Timber.tag("ArticleRemoteMediator").d("Inserted ${articles.size} articles successfully")
            }

            MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
        } catch (e: Exception) {
            Timber.tag("ArticleRemoteMediator").e(e, "Error during load")
            MediatorResult.Error(mapToDomainError(e))
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ArticleEntity>): RemoteKeysEntity? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { article ->
            database.remoteKeysDao.remoteKeysArticleId(article.id)
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ArticleEntity>): RemoteKeysEntity? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()
            ?.let { article ->
                database.remoteKeysDao.remoteKeysArticleId(article.id)
            }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ArticleEntity>): RemoteKeysEntity? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { articleId ->
                database.remoteKeysDao.remoteKeysArticleId(articleId)
            }
        }
    }
}
