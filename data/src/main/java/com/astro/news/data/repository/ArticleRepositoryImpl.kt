package com.astro.news.data.repository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.astro.news.data.local.AstroDatabase
import com.astro.news.data.mapper.toDomain
import com.astro.news.data.paging.ArticleRemoteMediator
import com.astro.news.data.remote.SpaceflightNewsApiService
import com.astro.news.domain.exception.NotFoundArticleException
import com.astro.news.domain.model.Article
import com.astro.news.domain.repository.ArticleRepository
import com.astro.news.domain.util.Result
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ArticleRepositoryImpl @Inject constructor(
    private val api: SpaceflightNewsApiService,
    private val database: AstroDatabase,
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
            remoteMediator = ArticleRemoteMediator(
                api = api,
                database = database,
                query = query
            ),
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

    override suspend fun getArticle(id: Int): Result<Article> {
        val article = database.articleDao.getArticleById(id)
        return if(article != null) Result.Success(article.toDomain())
        else
            Result.Error(NotFoundArticleException(id.toString()))
    }

}
