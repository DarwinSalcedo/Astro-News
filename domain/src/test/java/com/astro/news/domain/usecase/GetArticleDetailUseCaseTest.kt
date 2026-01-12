package com.astro.news.domain.usecase

import com.astro.news.domain.exception.NetworkException
import com.astro.news.domain.exception.NotFoundArticleException
import com.astro.news.domain.model.Article
import com.astro.news.domain.repository.ArticleRepository
import com.astro.news.domain.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import org.junit.Assert.assertEquals
import org.junit.Test
import kotlinx.coroutines.test.runTest

class GetArticleDetailUseCaseTest {

    private val repository: ArticleRepository = mockk()
    private val useCase = GetArticleDetailUseCase(repository)

    @Test
    fun `Given an id, When invoked, Then calls repository and returns result`() = runTest {
        val id = 123
        val article = mockk<Article>()
        val expectedResult = Result.Success(article)
        coEvery { repository.getArticle(id) } returns expectedResult

        val result = useCase(id)

        coVerify(exactly = 1) { repository.getArticle(id) }
        assertEquals(expectedResult, result)
    }

    @Test
    fun `Given an id that fails, When invoked, Then returns Error result`() = runTest {
        val id = 123
        val exception = NetworkException()
        val expectedResult = Result.Error(exception)
        coEvery { repository.getArticle(id) } returns expectedResult

        val result = useCase(id)

        coVerify(exactly = 1) { repository.getArticle(id) }
        assertEquals(expectedResult, result)
    }

    @Test
    fun `Given an id that not match, When invoked, Then returns Not found Error result`() = runTest {
        val id = 123
        val exception = NotFoundArticleException(id.toString())
        val expectedResult = Result.Error(exception)
        coEvery { repository.getArticle(id) } returns expectedResult

        val result = useCase(id)

        coVerify(exactly = 1) { repository.getArticle(id) }
        assertEquals(expectedResult, result)
    }
}
