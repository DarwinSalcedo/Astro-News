package com.astro.news.articles

import androidx.paging.PagingData
import com.astro.news.core.network.NetworkMonitor
import com.astro.news.domain.model.Article
import com.astro.news.domain.usecase.GetArticlesUseCase
import com.astro.news.domain.usecase.SearchArticlesUseCase
import com.astro.news.util.MainDispatcherRule
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.advanceTimeBy
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ArticleListViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private lateinit var viewModel: ArticleListViewModel
    private lateinit var getArticlesUseCase: GetArticlesUseCase
    private lateinit var searchArticlesUseCase: SearchArticlesUseCase
    private lateinit var networkMonitor: NetworkMonitor

    private val mockArticles = listOf(
        Article(
            id = 1,
            title = "Test Article 1",
            url = "http://test1.com",
            imageUrl = "http://image1.com",
            newsSite = "Test Site",
            summary = "Summary 1",
            publishedAt = "2024-01-01T00:00:00Z"
        ),
        Article(
            id = 2,
            title = "Test Article 2",
            url = "http://test2.com",
            imageUrl = "http://image2.com",
            newsSite = "Test Site",
            summary = "Summary 2",
            publishedAt = "2024-01-02T00:00:00Z"
        )
    )

    @Before
    fun setup() {
        getArticlesUseCase = mockk(relaxed = true)
        searchArticlesUseCase = mockk(relaxed = true)
        networkMonitor = mockk(relaxed = true)

        every { getArticlesUseCase() } returns flowOf(PagingData.from(mockArticles))
        every { searchArticlesUseCase(any()) } returns flowOf(PagingData.from(mockArticles))
        every { networkMonitor.isOnline } returns flowOf(true)

    }


    @Test
    fun `Given initial state, When ViewModel is created, Then state has empty search query`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        val state = viewModel.state.value

        assertEquals("", state.searchQuery)
    }

    @Test
    fun `Given search query, When OnSearchQueryChange event, Then state updates with new query`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)
        val searchQuery = "TestNews"

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(searchQuery))

        val state = viewModel.state.value
        assertEquals(searchQuery, state.searchQuery)
    }

    @Test
    fun `Given multiple search queries, When OnSearchQueryChange events, Then state updates sequentially`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange("TestNews"))
        assertEquals("TestNews", viewModel.state.value.searchQuery)

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange("Jupiter"))
        assertEquals("Jupiter", viewModel.state.value.searchQuery)

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(""))
        assertEquals("", viewModel.state.value.searchQuery)
    }


    @Test
    fun `Given empty query, When articles flow is collected, Then calls getArticlesUseCase`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        viewModel.articles.first()
        advanceTimeBy(100)

        verify { getArticlesUseCase() }
        verify(exactly = 0) { searchArticlesUseCase(any()) }
    }

    @Test
    fun `Given non-empty query, When articles flow is collected, Then calls searchArticlesUseCase`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)
        val query = "TestNews"

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(query))
        advanceTimeBy(ArticleListViewModel.MEDIUM_DELAY + 100)
        viewModel.articles.first()

        verify { searchArticlesUseCase(query) }
    }

    @Test
    fun `Given blank query with spaces, When articles flow is collected, Then calls getArticlesUseCase`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange("   "))
        viewModel.articles.first()
        advanceTimeBy(100)

        verify { getArticlesUseCase() }
        verify(exactly = 0) { searchArticlesUseCase(any()) }
    }


    @Test
    fun `Given empty query, When debouncing, Then uses NO_DELAY`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(""))
        advanceTimeBy(ArticleListViewModel.NO_DELAY)
        viewModel.articles.first()

        verify { getArticlesUseCase() }
    }

    @Test
    fun `Given query with less than 3 characters, When debouncing, Then uses LONG_DELAY`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)
        val shortQuery = "Ma"

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(shortQuery))
        
        advanceTimeBy(ArticleListViewModel.MEDIUM_DELAY)
        verify(exactly = 0) { searchArticlesUseCase(shortQuery) }

        advanceTimeBy(ArticleListViewModel.LONG_DELAY - ArticleListViewModel.MEDIUM_DELAY + 100)
        viewModel.articles.first()
        verify { searchArticlesUseCase(shortQuery) }
    }

    @Test
    fun `Given query with 3 or more characters, When debouncing, Then uses MEDIUM_DELAY`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)
        val longQuery = "TestNews"

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(longQuery))
        
        advanceTimeBy(ArticleListViewModel.MEDIUM_DELAY + 100)
        viewModel.articles.first()
        verify { searchArticlesUseCase(longQuery) }
    }

    @Test
    fun `Given rapid query changes, When debouncing, Then only last query is processed`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange("T"))
        advanceTimeBy(100)
        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange("Te"))
        advanceTimeBy(100)
        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange("Tes"))
        advanceTimeBy(100)
        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange("TestNews"))

        advanceTimeBy(ArticleListViewModel.MEDIUM_DELAY + 100)
        viewModel.articles.first()

        verify(exactly = 1) { searchArticlesUseCase("TestNews") }
        verify(exactly = 0) { searchArticlesUseCase("T") }
        verify(exactly = 0) { searchArticlesUseCase("Te") }
        verify(exactly = 0) { searchArticlesUseCase("Tes") }
    }

    @Test
    fun `Given same query twice, When distinctUntilChanged, Then only processes once`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)
        val query = "TestNews"

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(query))
        advanceTimeBy(ArticleListViewModel.MEDIUM_DELAY + 100)
        viewModel.articles.first()

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(query))
        advanceTimeBy(ArticleListViewModel.MEDIUM_DELAY + 100)
        viewModel.articles.first()

        verify(exactly = 1) { searchArticlesUseCase(query) }
    }


    @Test
    fun `Given OnRetry event, When event is triggered, Then emits Retry effect`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        viewModel.onEvent(ArticleListEvent.OnRetry)

        val effect = viewModel.effects.first()
        assertEquals(ArticleListEffect.Retry, effect)
    }

    @Test
    fun `Given OnArticleClick event, When event is triggered, Then emits NavigateToDetail effect`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)
        val articleId = 123

        viewModel.onEvent(ArticleListEvent.OnArticleClick(articleId))

        val effect = viewModel.effects.first()
        assertTrue(effect is ArticleListEffect.NavigateToDetail)
        assertEquals(articleId, (effect as ArticleListEffect.NavigateToDetail).articleId)
    }

    @Test
    fun `Given multiple article clicks, When events are triggered, Then emits multiple NavigateToDetail effects`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        viewModel.onEvent(ArticleListEvent.OnArticleClick(1))
        viewModel.onEvent(ArticleListEvent.OnArticleClick(2))
        viewModel.onEvent(ArticleListEvent.OnArticleClick(3))

        val effects = mutableListOf<ArticleListEffect>()
        repeat(3) {
            effects.add(viewModel.effects.first())
        }

        assertEquals(3, effects.size)
        assertTrue(effects.all { it is ArticleListEffect.NavigateToDetail })
        assertEquals(1, (effects[0] as ArticleListEffect.NavigateToDetail).articleId)
        assertEquals(2, (effects[1] as ArticleListEffect.NavigateToDetail).articleId)
        assertEquals(3, (effects[2] as ArticleListEffect.NavigateToDetail).articleId)
    }


    @Test
    fun `Given network monitor, When isOnline is true, Then exposes online state`() = runTest {
        every { networkMonitor.isOnline } returns flowOf(true)
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        val isOnline = viewModel.isOnline.first()

        assertTrue(isOnline)
    }

    @Test
    fun `Given network monitor, When isOnline is false, Then exposes offline state`() = runTest {
        every { networkMonitor.isOnline } returns flowOf(false)
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        val isOnline = viewModel.isOnline.first()

        assertEquals(false, isOnline)
    }


    @Test
    fun `Given query transition from empty to non-empty, When search changes, Then switches from getArticles to searchArticles`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        viewModel.articles.first()
        advanceTimeBy(100)
        verify { getArticlesUseCase() }

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange("TestNews"))
        advanceTimeBy(ArticleListViewModel.MEDIUM_DELAY + 100)
        viewModel.articles.first()
        verify { searchArticlesUseCase("TestNews") }
    }

    @Test
    fun `Given query transition from non-empty to empty, When search changes, Then switches from searchArticles to getArticles`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)
        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange("TestNews"))
        advanceTimeBy(ArticleListViewModel.MEDIUM_DELAY + 100)
        viewModel.articles.first()
        verify { searchArticlesUseCase("TestNews") }

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(""))
        advanceTimeBy(ArticleListViewModel.NO_DELAY + 100)
        viewModel.articles.first()
        verify(atLeast = 1) { getArticlesUseCase() }
    }

    @Test
    fun `Given special characters in query, When OnSearchQueryChange event, Then updates state correctly`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)
        val specialQuery = "TestNews & Jupiter!"

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(specialQuery))

        assertEquals(specialQuery, viewModel.state.value.searchQuery)
    }

    @Test
    fun `Given very long query, When OnSearchQueryChange event, Then updates state correctly`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)
        val longQuery = "a".repeat(1000)

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(longQuery))

        assertEquals(longQuery, viewModel.state.value.searchQuery)
    }

    @Test
    fun `Given unicode characters in query, When OnSearchQueryChange event, Then updates state correctly`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)
        val unicodeQuery = "火星  ..., TestNews"

        viewModel.onEvent(ArticleListEvent.OnSearchQueryChange(unicodeQuery))

        assertEquals(unicodeQuery, viewModel.state.value.searchQuery)
    }

    @Test
    fun `Given article ID of 0, When OnArticleClick event, Then emits NavigateToDetail with ID 0`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        viewModel.onEvent(ArticleListEvent.OnArticleClick(0))

        val effect = viewModel.effects.first()
        assertEquals(0, (effect as ArticleListEffect.NavigateToDetail).articleId)
    }

    @Test
    fun `Given negative article ID, When OnArticleClick event, Then emits NavigateToDetail with negative ID`() = runTest {
        viewModel = ArticleListViewModel(getArticlesUseCase, searchArticlesUseCase, networkMonitor)

        viewModel.onEvent(ArticleListEvent.OnArticleClick(-1))

        val effect = viewModel.effects.first()
        assertEquals(-1, (effect as ArticleListEffect.NavigateToDetail).articleId)
    }
}
