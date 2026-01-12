package com.astro.news.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.astro.news.ui.ArticleDetailScreen
import com.astro.news.ui.ArticleListScreen

@Composable
fun AstroNewsNavigation(navController: NavHostController) {
    NavHost(
        navController = navController,
        startDestination = Screen.ArticleList.route
    ) {
        composable(Screen.ArticleList.route) {
            ArticleListScreen(
                onNavigateToDetail = { articleId ->
                    navController.navigate(Screen.ArticleDetail.createRoute(articleId))
                }
            )
        }

        composable(
            route = Screen.ArticleDetail.route,
            arguments = listOf(
                navArgument("articleId") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val articleId = backStackEntry.arguments?.getInt("articleId") ?: 0
            ArticleDetailScreen(
                articleId = articleId,
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}
