package com.astro.news.data.repository


import com.astro.news.data.local.AstroDatabase
import com.astro.news.data.local.dao.ArticleDao
import com.astro.news.data.local.entity.ArticleEntity
import com.astro.news.data.remote.SpaceflightNewsApiService
import com.astro.news.domain.exception.NotFoundArticleException
import com.astro.news.domain.repository.ArticleRepository
import com.astro.news.domain.util.Result
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class ArticleRepositoryImplTest {

    private lateinit var repository: ArticleRepository
    private val api: SpaceflightNewsApiService = mockk()
    private val database: AstroDatabase = mockk()
    private val articleDao: ArticleDao = mockk()

    @Before
    fun setUp() {
        coEvery { database.articleDao } returns articleDao
        repository = ArticleRepositoryImpl(api, database)
    }

    @Test
    fun `Given an existing article id, When getArticle is called, Then return Success with article data`() =
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
        }

    @Test
    fun `Given a non-existing article id, When getArticle is called, Then return Error with NotFoundArticleException`() =
        runTest {

            val articleId = 921
            coEvery { articleDao.getArticleById(articleId) } returns null


            val result = repository.getArticle(articleId)

            assertTrue(result is Result.Error)
            val exception = (result as Result.Error).exception
            assertTrue(exception is NotFoundArticleException)
        }

}
