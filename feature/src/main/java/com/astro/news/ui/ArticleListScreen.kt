package com.astro.news.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.collectAsLazyPagingItems
import androidx.paging.compose.itemKey
import com.astro.feature.R
import com.astro.news.articles.ArticleListEffect
import com.astro.news.articles.ArticleListEvent
import com.astro.news.articles.ArticleListViewModel
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ArticleListScreen(
    viewModel: ArticleListViewModel = hiltViewModel(),
    onNavigateToDetail: (Int) -> Unit
) {
    val articles = viewModel.articles.collectAsLazyPagingItems()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val isOnline by viewModel.isOnline.collectAsStateWithLifecycle()

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

        val pullToRefreshState = rememberPullToRefreshState()
        if (pullToRefreshState.isRefreshing) {
            LaunchedEffect(true) {
                articles.refresh()
            }
        }

        LaunchedEffect(articles.loadState.refresh) {
            if (articles.loadState.refresh !is LoadState.Loading) {
                pullToRefreshState.endRefresh()
            } else if (articles.loadState.refresh is LoadState.Loading && articles.itemCount > 0) {
                pullToRefreshState.startRefresh()
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
                AnimatedVisibility(
                    visible = !isOnline,
                    enter = slideInVertically(initialOffsetY = { -it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { -it }) + fadeOut()
                ) {
                    Surface(
                        modifier = Modifier.fillMaxWidth(),
                        color = MaterialTheme.colorScheme.errorContainer,
                        tonalElevation = 4.dp
                    ) {
                        Text(
                            text = stringResource(R.string.showing_cached_data),
                            modifier = Modifier.padding(12.dp),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onErrorContainer
                        )
                    }
                }


                LazyColumn(
                    state = listState,
                    modifier = Modifier
                        .weight(1f)
                        .nestedScroll(pullToRefreshState.nestedScrollConnection)
                ) {
                    items(
                        count = articles.itemCount,
                        key = articles.itemKey { it.id }
                    ) { index ->
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
                            loadState.refresh is LoadState.Loading && articles.itemCount == 0 -> {
                                items(5) {
                                    ArticleItemShimmer()
                                }
                            }

                            loadState.refresh is LoadState.Error && articles.itemCount == 0 -> {
                                item {
                                    ErrorRefreshUi({ viewModel.onEvent(ArticleListEvent.OnRetry) })
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

                            loadState.append is LoadState.Error -> {
                                item {
                                    ErrorUi({ viewModel.onEvent(ArticleListEvent.OnRetry) })
                                }
                            }
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = articles.loadState.refresh is LoadState.NotLoading &&
                        articles.itemCount == 0 &&
                        articles.loadState.append.endOfPaginationReached,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                EmptyUi()
            }

            if (articles.loadState.refresh is LoadState.Loading && articles.itemCount > 0) {
                LinearProgressIndicator(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.TopCenter)
                )
            }

            PullToRefreshContainer(
                state = pullToRefreshState,
                modifier = Modifier.align(Alignment.TopCenter)
            )

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