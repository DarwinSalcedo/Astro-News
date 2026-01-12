package com.astro.news.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.astro.news.data.local.AstroDatabase
import com.astro.news.data.mapper.toDomain
import com.astro.news.data.mapper.toEntity
import com.astro.news.data.paging.ArticleMediatorFactory
import com.astro.news.data.remote.SpaceflightNewsApiService
import com.astro.news.domain.exception.NotFoundArticleException
import com.astro.news.domain.model.Article
import com.astro.news.domain.repository.ArticleRepository
import com.astro.news.domain.util.Result
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import timber.log.Timber

class ArticleRepositoryImpl @Inject constructor(
    private val api: SpaceflightNewsApiService,
    private val database: AstroDatabase,
    private val mediatorFactory: ArticleMediatorFactory
) : ArticleRepository {
    companion object {
        const val PAGE_SIZE = 20
        const val PREFETCH_DISTANCE = 5
    }



    @OptIn(ExperimentalPagingApi::class)
    override fun getArticles(
        query: String?
    ): Flow<PagingData<Article>> {
        return Pager(
            config = PagingConfig(
                pageSize = PAGE_SIZE,
                prefetchDistance = PREFETCH_DISTANCE,
                enablePlaceholders = false
            ),
            remoteMediator = mediatorFactory.getMediator(query),
            pagingSourceFactory = {
                if (query.isNullOrEmpty()) {
                    database.articleDao.pagingSource()
                } else {
                    database.articleDao.pagingSourceFiltered(query)
                }
            }
        ).flow.map { pagingData ->
            pagingData.map { it.toDomain() }
        }
    }



    override suspend fun getArticle(id: Int): Result<Article> = withContext(Dispatchers.IO) {
        Timber.tag("ArticleRepository").d("Fetching article details for ID: $id")
        
        val cachedArticle = database.articleDao.getArticleById(id)
        if (cachedArticle != null) {
            Timber.tag("ArticleRepository").d("Article $id found in cache")
            return@withContext Result.Success(cachedArticle.toDomain())
        }
        
        try {
            Timber.tag("ArticleRepository").d("Article $id not in cache, fetching from API")
            val articleDto = api.getArticleById(id)
            val entity = articleDto.toEntity()
            
            database.articleDao.insertAll(listOf(entity))
            Timber.tag("ArticleRepository").d("Article $id fetched from API and cached")
            
            Result.Success(entity.toDomain())
        } catch (e: Exception) {
            Timber.tag("ArticleRepository").e(e, "Failed to fetch article $id from API")
            Result.Error(NotFoundArticleException(id.toString()))
        }
    }

}
