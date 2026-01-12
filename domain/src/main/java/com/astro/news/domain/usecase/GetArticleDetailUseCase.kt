package com.astro.news.domain.usecase

import com.astro.news.domain.model.Article
import com.astro.news.domain.repository.ArticleRepository
import com.astro.news.domain.util.Result
import javax.inject.Inject

class GetArticleDetailUseCase @Inject constructor(
    private val repository: ArticleRepository
) {
    suspend operator fun invoke(id: Int): Result<Article> {
        return repository.getArticle(id)
    }
}
