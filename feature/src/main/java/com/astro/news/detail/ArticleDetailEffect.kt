package com.astro.news.detail

sealed interface ArticleDetailEffect {
    data class OpenBrowser(val url: String) : ArticleDetailEffect
    data object NavigateBack : ArticleDetailEffect
    data class ShowError(val message: String) : ArticleDetailEffect
}
