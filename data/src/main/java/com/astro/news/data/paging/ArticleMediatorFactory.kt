package com.astro.news.data.paging

import com.astro.news.data.local.AstroDatabase
import com.astro.news.data.remote.SpaceflightNewsApiService
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ArticleMediatorFactory @Inject constructor(
    private val api: SpaceflightNewsApiService,
    private val database: AstroDatabase
) {
    private val mediators = mutableMapOf<String?, ArticleRemoteMediator>()

    fun getMediator(query: String?): ArticleRemoteMediator {
        return mediators.getOrPut(query) {
            Timber.d("Creating new RemoteMediator for query: ${query ?: "null"}")
            ArticleRemoteMediator(
                api = api,
                database = database,
                query = query
            )
        }
    }

}
