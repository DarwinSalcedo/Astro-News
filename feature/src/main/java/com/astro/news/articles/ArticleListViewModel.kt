package com.astro.news.articles

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.astro.news.domain.model.Article
import com.astro.news.domain.usecase.GetArticlesUseCase
import com.astro.news.domain.usecase.SearchArticlesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.channels.Channel
import javax.inject.Inject

@HiltViewModel
class ArticleListViewModel @Inject constructor(
    private val getArticlesUseCase: GetArticlesUseCase,
    private val searchArticlesUseCase: SearchArticlesUseCase
) : ViewModel() {
    private val _effects = Channel<ArticleListEffect>()
    val effects = _effects.receiveAsFlow()
    private val _state = MutableStateFlow(ArticleListState())
    val state: StateFlow<ArticleListState> = _state.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    val articles: Flow<PagingData<Article>> = _state
        .flatMapLatest { state ->
            if (state.searchQuery.isEmpty()) {
                getArticlesUseCase()
            } else {
                searchArticlesUseCase(state.searchQuery)
            }
        }
        .cachedIn(viewModelScope)


    fun onEvent(event: ArticleListEvent) {
        when (event) {
            is ArticleListEvent.OnSearchQueryChange -> {
                _state.update { it.copy(searchQuery = event.query) }
            }
            ArticleListEvent.OnRetry -> {
                viewModelScope.launch {
                    _effects.send(ArticleListEffect.Retry)
                }
            }
            is ArticleListEvent.OnArticleClick -> {
                viewModelScope.launch {
                     _effects.send(ArticleListEffect.NavigateToDetail(event.articleId))
                }
            }
        }
    }
}
