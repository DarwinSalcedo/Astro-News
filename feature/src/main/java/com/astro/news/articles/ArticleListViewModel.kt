package com.astro.news.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.astro.news.domain.model.Article
import com.astro.news.domain.usecase.GetArticlesUseCase
import com.astro.news.domain.usecase.SearchArticlesUseCase
import com.astro.news.core.network.NetworkMonitor
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import timber.log.Timber

@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val searchArticlesUseCase: SearchArticlesUseCase,
    networkMonitor: NetworkMonitor
) : ViewModel() {
    private val _effects = Channel<ArticleListEffect>()
    val effects = _effects.receiveAsFlow()
    private val _state = MutableStateFlow(ArticleListState())
    val state: StateFlow<ArticleListState> = _state.asStateFlow()
    
    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline

    @OptIn(ExperimentalCoroutinesApi::class, FlowPreview::class)
    val articles: Flow<PagingData<Article>> = _state
        .map { it.searchQuery }
        .distinctUntilChanged()
        .debounce { query ->
            if (query.isBlank()) 0L else 500L
        }
        .flatMapLatest { query ->
            if (query.isBlank()) {
                getArticlesUseCase()
            } else {
                searchArticlesUseCase(query)
            }
        }
        .cachedIn(viewModelScope)


    fun onEvent(event: ArticleListEvent) {
        when (event) {
            is ArticleListEvent.OnSearchQueryChange -> {
                Timber.d("OnSearchQueryChange: ${event.query}")
                _state.update { it.copy(searchQuery = event.query) }
            }

            ArticleListEvent.OnRetry -> {
                viewModelScope.launch {
                    Timber.d("OnRetry triggered")
                    _effects.send(ArticleListEffect.Retry)
                }
            }

            is ArticleListEvent.OnArticleClick -> {
                viewModelScope.launch {
                    Timber.d("Navigating to article: ${event.articleId}")
                    _effects.send(ArticleListEffect.NavigateToDetail(event.articleId))
                }
            }
        }
    }
}
