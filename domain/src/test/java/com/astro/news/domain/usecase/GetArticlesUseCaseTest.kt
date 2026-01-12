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

class GetArticlesUseCaseTest {

    private val repository: ArticleRepository = mockk()
    private val useCase = GetArticlesUseCase(repository)

    @Test
    fun `Given no query, When invoked, Then calls repository with null`() = runTest {
        val pagingData = PagingData.empty<Article>()
        every { repository.getArticles(null) } returns flowOf(pagingData)

        useCase()

        verify(exactly = 1) { repository.getArticles(null) }
    }


}
