package com.astro.news.navigation

sealed class Screen(val route: String) {
    data object ArticleList : Screen("articles")
    data object ArticleDetail : Screen("articles/{articleId}") {
        fun createRoute(articleId: Int) = "articles/$articleId"
    }
}
