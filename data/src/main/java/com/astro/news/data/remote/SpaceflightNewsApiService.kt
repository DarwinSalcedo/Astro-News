package com.astro.news.data.remote

import com.astro.news.data.remote.dto.ArticleListResponseDto
import com.astro.news.data.remote.dto.ArticleResponseDto
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface SpaceflightNewsApiService {

    @GET("articles/")
    suspend fun getArticles(
        @Query("limit") limit: Int,
        @Query("offset") offset: Int,
        @Query("search") search: String? = null
    ): ArticleListResponseDto

    @GET("articles/{id}/")
    suspend fun getArticleById(
        @Path("id") id: Int
    ): ArticleResponseDto
}
