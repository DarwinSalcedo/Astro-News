package com.astro.news.domain.usecase

import androidx.paging.PagingData
import com.astro.news.domain.model.Article
import com.astro.news.domain.repository.ArticleRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchArticlesUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    operator fun invoke(query: String): Flow<PagingData<Article>> {
        return repository.getArticles(query = query)
    }
}
