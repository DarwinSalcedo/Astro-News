package com.astro.news.articles

sealed interface ArticleListEvent {
    data class OnSearchQueryChange(val query: String) : ArticleListEvent
    data object OnRetry : ArticleListEvent
    data class OnArticleClick(val articleId: Int) : ArticleListEvent
}
