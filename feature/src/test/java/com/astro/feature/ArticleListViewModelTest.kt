package com.astro.feature

import androidx.paging.PagingData
import com.astro.news.articles.ArticleListEffect
import com.astro.news.articles.ArticleListEvent
import com.astro.news.articles.ArticleListViewModel
import com.astro.news.domain.usecase.GetArticlesUseCase
import com.astro.news.domain.usecase.SearchArticlesUseCase
import com.astro.news.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var getArticlesUseCase: GetArticlesUseCase
    private lateinit var searchArticlesUseCase: SearchArticlesUseCase
    private lateinit var viewModel: ArticleListViewModel

    @Before
    fun setUp() {
        getArticlesUseCase = mockk()
        searchArticlesUseCase = mockk()

        every { getArticlesUseCase() } returns flowOf(PagingData.empty())
        every { searchArticlesUseCase(any()) } returns flowOf(PagingData.empty())

        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase)
    }


    @Test
    fun `Given search query, When OnSearchQueryChange event, Then state updates and calls SearchArticlesUseCase`() =
        runTest {
            val query = "query"

            viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(query))

            Assert.assertEquals(query, viewModel.state.value.searchQuery)

            viewModel.articles.first()

            verify(exactly = 1) { searchArticlesUseCase(query) }

        }

    @Test
    fun `When OnRetry event, Then emits Retry effect`() = runTest {
        viewModel.onEvent(ArticleListEvent.OnRetry)

        val effect = viewModel.effects.first()
        Assert.assertEquals(ArticleListEffect.Retry, effect)
    }

    @Test
    fun `When OnArticleClick event, Then emits NavigateToDetail effect`() = runTest {
        val articleId = 101
        viewModel.onEvent(ArticleListEvent.OnArticleClick(articleId))

        val effect = viewModel.effects.first()
        Assert.assertTrue(effect is ArticleListEffect.NavigateToDetail)
        Assert.assertEquals(articleId, (effect as ArticleListEffect.NavigateToDetail).articleId)
    }
}