package com.astro.news

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.astro.news.ui.ArticleListScreen
import com.astro.news.ui.theme.AstroNewsTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
@OptIn(ExperimentalMaterial3Api::class)
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AstroNewsTheme {
                val navController = rememberNavController()

                NavHost(
                    navController = navController,
                    startDestination = "articles"
                ) {
                    composable("articles") {
                        ArticleListScreen(
                            onNavigateToDetail = { articleId ->
                                navController.navigate("articles/$articleId")
                            }
                        )
                    }

                    composable(
                        route = "articles/{articleId}",
                        arguments = listOf(
                            androidx.navigation.navArgument("articleId") {
                                type = androidx.navigation.NavType.IntType
                            }
                        )
                    ) { backStackEntry ->
                        val articleId = backStackEntry.arguments?.getInt("articleId") ?: 0
                        com.astro.news.ui.ArticleDetailScreen(
                            articleId = articleId,
                            onBack = {
                                navController.popBackStack()
                            }
                        )
                    }
                }
            }
        }
    }
}