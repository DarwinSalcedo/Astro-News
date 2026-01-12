package com.astro.news.detail

import com.astro.news.domain.model.Article

data class ArticleDetailState(
    val isLoading: Boolean = false,
    val article: Article? = null,
    val error: String? = null
)
