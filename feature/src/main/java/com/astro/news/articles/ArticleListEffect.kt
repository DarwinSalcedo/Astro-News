package com.astro.news.articles

sealed interface ArticleListEffect {
    data object Retry : ArticleListEffect
    data class NavigateToDetail(val articleId: Int) : ArticleListEffect
}
