package com.astro.news.domain.repository

import com.astro.news.domain.model.Article

import androidx.paging.PagingData
import kotlinx.coroutines.flow.Flow
import com.astro.news.domain.util.Result

interface ArticleRepository {
    fun getArticles(
        query: String? = null
    ): Flow<PagingData<Article>>

    suspend fun getArticle(id: Int): Result<Article>
}
