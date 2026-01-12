package com.astro.news.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.astro.news.articles.ArticleListEffect
import com.astro.news.articles.ArticleListEvent
import com.astro.news.articles.ArticleListViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(
    viewModel: ArticleListViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
        val articles = viewModel.articles.collectAsLazyPagingItems()
        val state = viewModel.state.collectAsStateWithLifecycle()

        val listState = androidx.compose.foundation.lazy.rememberLazyListState()

        androidx.compose.runtime.LaunchedEffect(Unit) {
            viewModel.effects.collect { effect ->
                when (effect) {
                    ArticleListEffect.Retry -> {
                        articles.retry()
                    }

                    is ArticleListEffect.NavigateToDetail -> {
                        onNavigateToDetail(effect.articleId)
                    }
                }
            }
        }


        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            Column {
                SearchBarUi(
                    query = state.value.searchQuery,
                    onQueryChange = {
                        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(it))
                    },
                    onSearch = {
                        viewModel.onEvent(
                            ArticleListEvent.OnSearchQueryChange("")
                        )
                    })

                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                ) {
                    items(articles.itemCount) { index ->
                        val article = articles[index]
                        article?.let {
                            ArticleItem(
                                article = it,
                                modifier = Modifier
                                    .clickable {
                                        viewModel.onEvent(
                                            ArticleListEvent.OnArticleClick(
                                                it.id
                                            )
                                        )
                                    }
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    articles.apply {
                        when {
                            loadState.refresh is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            loadState.append is LoadState.Loading -> {
                                item {
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }
                            }

                            loadState.refresh is LoadState.Error -> {
                                item {
                                    Column(
                                        modifier = Modifier.fillParentMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = "Error loading articles")
                                        androidx.compose.material3.Button(
                                            onClick = {
                                                viewModel.onEvent(ArticleListEvent.OnRetry)
                                            }
                                        ) {
                                            Text(text = "Retry")
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

            if (articles.loadState.refresh is LoadState.Loading && articles.itemCount == 0) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}