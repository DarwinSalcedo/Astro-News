package com.astro.news.detail

import ArticleDetailState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.astro.news.domain.usecase.GetArticleDetailUseCase
import com.astro.news.domain.util.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import com.astro.news.util.asStringRes
import javax.inject.Inject

@HiltViewModel
class ArticleDetailViewModel @Inject constructor(
    private val getArticleDetailUseCase: GetArticleDetailUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ArticleDetailState())
    val state: StateFlow<ArticleDetailState> = _state.asStateFlow()

    private val _effects = Channel<ArticleDetailEffect>()
    val effects = _effects.receiveAsFlow()

    fun onEvent(event: ArticleDetailEvent) {
        when (event) {

            is ArticleDetailEvent.LoadArticle -> {
                loadArticle(event.articleId)
            }
            ArticleDetailEvent.OnOpenBrowserClick -> {
                _state.value.article?.url?.let { url ->
                    sendEffect(ArticleDetailEffect.OpenBrowser(url))
                }
            }
            ArticleDetailEvent.OnBackClick -> {
                sendEffect(ArticleDetailEffect.NavigateBack)
            }


        }
    }

    private fun loadArticle(id: Int) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true, error = null) }

            when (val result = getArticleDetailUseCase(id)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, article = result.data) }
                }


                is Result.Error -> {
                     val error = result.exception
                    _state.update {
                        it.copy(
                            isLoading = false,
                            error = error.asStringRes()
                        )
                    }
                    sendEffect(ArticleDetailEffect.ShowError(error.asStringRes()))
                }
                Result.Loading -> {}
            }
        }
    }

    private fun sendEffect(effect: ArticleDetailEffect) {
        viewModelScope.launch {
            _effects.send(effect)
        }
    }
}
