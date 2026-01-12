package com.astro.news.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import com.astro.feature.R
import com.astro.news.articles.ArticleListEffect
import com.astro.news.articles.ArticleListEvent
import com.astro.news.articles.ArticleListViewModel
import com.astro.news.util.asStringRes
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(
    viewModel: ArticleListViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val articles = viewModel.articles.collectAsLazyPagingItems()
    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            AstroNewsTopBar(
                query = state.value.searchQuery,
                onQueryChange = {
                    viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(it))
                },
                onSearchClosed = {
                    viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(""))
                }
            )
        }
    ) { innerPadding ->


        val listState = androidx.compose.foundation.lazy.rememberLazyListState()
        val coroutineScope = rememberCoroutineScope()
        val showScrollToTop by remember {
            derivedStateOf {
                listState.firstVisibleItemIndex > 0
            }
        }

        LaunchedEffect(Unit) {
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
                                val errorState = loadState.refresh as LoadState.Error
                                item {
                                    Column(
                                        modifier = Modifier.fillParentMaxSize(),
                                        horizontalAlignment = Alignment.CenterHorizontally,
                                        verticalArrangement = Arrangement.Center
                                    ) {
                                        Text(text = stringResource(errorState.error.asStringRes()))
                                        Button(
                                            onClick = {
                                                viewModel.onEvent(ArticleListEvent.OnRetry)
                                            }
                                        ) {
                                            Text(text = stringResource(R.string.retry))
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

            if (articles.loadState.refresh is LoadState.NotLoading && articles.itemCount == 0) {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = null,
                        modifier = Modifier.padding(bottom = 8.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(
                        text = stringResource(R.string.no_results_found),
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }

            AnimatedVisibility(
                visible = showScrollToTop,
                enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                exit = slideOutVertically(targetOffsetY = { it }) + fadeOut(),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(16.dp)
            ) {
                SmallFloatingActionButton(
                    onClick = {
                        coroutineScope.launch {
                            listState.animateScrollToItem(0)
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = stringResource(R.string.scroll_to_top)
                    )
                }
            }
        }
    }
}