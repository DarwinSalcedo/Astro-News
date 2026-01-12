package com.astro.news.domain.usecase

import androidx.paging.PagingData
import com.astro.news.domain.model.Article
import com.astro.news.domain.repository.ArticleRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Test
import kotlinx.coroutines.test.runTest

class SearchArticlesUseCaseTest {

    private val repository: ArticleRepository = mockk()
    private val useCase = SearchArticlesUseCase(repository)

    @Test
    fun `Given a query, When invoked, Then calls repository with query`() = runTest {
        val query = "test134"
        val pagingData = PagingData.empty<Article>()
        every { repository.getArticles(query) } returns flowOf(pagingData)

        useCase(query)

        verify(exactly = 1) { repository.getArticles(query) }

}


}
