package com.astro.news.detail

sealed interface ArticleDetailEvent {
    data class LoadArticle(val articleId: Int) : ArticleDetailEvent
    data object OnOpenBrowserClick : ArticleDetailEvent
    data object OnBackClick : ArticleDetailEvent
}
