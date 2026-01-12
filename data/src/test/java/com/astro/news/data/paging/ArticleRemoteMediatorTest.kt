package com.astro.news.data.paging

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingConfig
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.astro.news.data.local.AstroDatabase
import com.astro.news.data.local.dao.ArticleDao
import com.astro.news.data.local.dao.RemoteKeysDao
import com.astro.news.data.local.entity.ArticleEntity
import com.astro.news.data.remote.SpaceflightNewsApiService
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException

@OptIn(ExperimentalPagingApi::class)
class ArticleRemoteMediatorTest {

    private val api: SpaceflightNewsApiService = mockk()
    private val database: AstroDatabase = mockk()
    private val remoteKeysDao: RemoteKeysDao = mockk()
    private val articleDao: ArticleDao = mockk()

    private lateinit var mediator: ArticleRemoteMediator

    @Before
    fun setup() {
        every { database.remoteKeysDao } returns remoteKeysDao
        every { database.articleDao } returns articleDao
        mediator = ArticleRemoteMediator(api, database)
    }

    @Test

    fun `Given error by Network, When  it loads, Then db its not cleared`() = runTest {
        val exception = IOException("Network error")
        coEvery { api.getArticles(any(), any(), any()) } throws exception
        coEvery { remoteKeysDao.remoteKeysArticleId(any()) } returns null

        val pagingState = PagingState<Int, ArticleEntity>(
            pages = listOf(),
            anchorPosition = null,
            config = PagingConfig(pageSize = 10),
            leadingPlaceholderCount = 0
        )

        val result = mediator.load(LoadType.REFRESH, pagingState)

        assertTrue(result is RemoteMediator.MediatorResult.Error)

        coVerify(exactly = 0) { remoteKeysDao.clearRemoteKeys() }
        coVerify(exactly = 0) { articleDao.clearAll() }

        coVerify(exactly = 1) { api.getArticles(any(), any(), any()) }
    }

}
