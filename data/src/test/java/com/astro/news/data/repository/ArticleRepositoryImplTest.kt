package com.astro.news.data.repository


import com.astro.news.data.local.AstroDatabase
import com.astro.news.data.local.dao.ArticleDao
import com.astro.news.data.local.entity.ArticleEntity
import com.astro.news.data.paging.ArticleMediatorFactory
import com.astro.news.data.remote.SpaceflightNewsApiService
import com.astro.news.data.remote.dto.ArticleResponseDto
import com.astro.news.domain.exception.NotFoundArticleException
import com.astro.news.domain.repository.ArticleRepository
import com.astro.news.domain.util.Result
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException


class ArticleRepositoryImplTest {

    private lateinit var repository: ArticleRepository
    private val api: SpaceflightNewsApiService = mockk()
    private val database: AstroDatabase = mockk()
    private val articleDao: ArticleDao = mockk()
    private val mediatorFactory: ArticleMediatorFactory = mockk()

    @Before
    fun setUp() {
        coEvery { database.articleDao } returns articleDao
        repository = ArticleRepositoryImpl(api, database, mediatorFactory)
    }

    @Test
    fun `Given article in cache, When getArticle is called, Then return Success from cache without API call`() =
        runTest {
            val articleId = 1
            val articleEntity = ArticleEntity(
                id = 1,
                title = "Test Article",
                url = "url",
                imageUrl = "image",
                newsSite = "site",
                summary = "summary",
                publishedAt = "2023-01-01"
            )
            coEvery { articleDao.getArticleById(articleId) } returns articleEntity

            val result = repository.getArticle(articleId)

            assertTrue(result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(articleEntity.id, data.id)
            assertEquals(articleEntity.title, data.title)
            
            coVerify(exactly = 0) { api.getArticleById(any()) }
        }

    @Test
    fun `Given article NOT in cache, When getArticle is called, Then fetch from API and cache it`() =
        runTest {
            val articleId = 921
            val articleDto = ArticleResponseDto(
                id = 921,
                title = "API Article",
                url = "api-url",
                imageUrl = "api-image",
                newsSite = "api-site",
                summary = "api-summary",
                publishedAt = "2023-01-01T00:00:00Z"
            )
            
            coEvery { articleDao.getArticleById(articleId) } returns null
            coEvery { api.getArticleById(articleId) } returns articleDto
            coEvery { articleDao.insertAll(any()) } just runs

            val result = repository.getArticle(articleId)

            assertTrue(result is Result.Success)
            val data = (result as Result.Success).data
            assertEquals(articleDto.id, data.id)
            assertEquals(articleDto.title, data.title)
            
            coVerify(exactly = 1) { api.getArticleById(articleId) }
            coVerify(exactly = 1) { articleDao.insertAll(any()) }
        }

    @Test
    fun `Given article NOT in cache and API fails, When getArticle is called, Then return Error`() =
        runTest {
            val articleId = 123
            
            coEvery { articleDao.getArticleById(articleId) } returns null
            coEvery { api.getArticleById(articleId) } throws IOException("Network error")

            val result = repository.getArticle(articleId)

            assertTrue(result is Result.Error)
            val exception = (result as Result.Error).exception
            assertTrue(exception is NotFoundArticleException)
            
            coVerify(exactly = 1) { api.getArticleById(articleId) }
            coVerify(exactly = 0) { articleDao.insertAll(any()) }
        }

}
