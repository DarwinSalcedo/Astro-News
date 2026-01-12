package com.astro.news.detail

import com.astro.news.domain.model.Article
import com.astro.news.domain.usecase.GetArticleDetailUseCase
import com.astro.news.domain.util.Result
import com.astro.news.util.MainDispatcherRule
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.runTest
import com.astro.feature.R
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleDetailViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val getArticleDetailUseCase: GetArticleDetailUseCase = mockk()
    private lateinit var viewModel: ArticleDetailViewModel

    @Test
    fun `Given article ID, When LoadArticle event, Then state updates with article on success`() =
        runTest {

            val articleId = 1
            val article = Article(
                id = articleId,
                title = "Test Article",
                url = "http://test.com",
                imageUrl = "http://image.com",
                newsSite = "Test Site",
                summary = "Summary",
                publishedAt = "2023-01-01"
            )
            coEvery { getArticleDetailUseCase(articleId) } returns Result.Success(article)
            viewModel = ArticleDetailViewModel(getArticleDetailUseCase)


            viewModel.onEvent(ArticleDetailEvent.LoadArticle(articleId))


            val state = viewModel.state.value
            assertEquals(article, state.article)
            assertEquals(false, state.isLoading)
            assertEquals(null, state.error)
        }



    @Test
    fun `Given article ID, When LoadArticle event, Then state updates with error on failure`() =
        runTest {

            val articleId = 1
            val errorMessage = "Network Error"
            coEvery { getArticleDetailUseCase(articleId) } returns Result.Error(
                Exception(
                    errorMessage
                )
            )
            viewModel = ArticleDetailViewModel(getArticleDetailUseCase)


            viewModel.onEvent(ArticleDetailEvent.LoadArticle(articleId))


            val state = viewModel.state.value
            assertEquals(null, state.article)
            assertEquals(false, state.isLoading)
            assertEquals(R.string.unknown_error, state.error)

        }

    @Test
    fun `Given loaded article, When OnOpenBrowserClick event, Then emits OpenBrowser effect`() =
        runTest {

            val articleId = 1
            val url = "http://test.com"
            val article = Article(
                id = articleId,
                title = "Test Article",
                url = url,
                imageUrl = "http://image.com",
                newsSite = "Test Site",
                summary = "Summary",
                publishedAt = "2023-01-01"
            )
            coEvery { getArticleDetailUseCase(articleId) } returns Result.Success(article)
            viewModel = ArticleDetailViewModel(getArticleDetailUseCase)
            viewModel.onEvent(ArticleDetailEvent.LoadArticle(articleId))


            viewModel.onEvent(ArticleDetailEvent.OnOpenBrowserClick)


            val effect = viewModel.effects.first()
            assertTrue(effect is ArticleDetailEffect.OpenBrowser)
            assertEquals(url, (effect as ArticleDetailEffect.OpenBrowser).url)
        }

    @Test
    fun `When OnBackClick event, Then emits NavigateBack effect`() = runTest {

        viewModel = ArticleDetailViewModel(getArticleDetailUseCase)


        viewModel.onEvent(ArticleDetailEvent.OnBackClick)


        val effect = viewModel.effects.first()
        assertEquals(ArticleDetailEffect.NavigateBack, effect)
    }
}
