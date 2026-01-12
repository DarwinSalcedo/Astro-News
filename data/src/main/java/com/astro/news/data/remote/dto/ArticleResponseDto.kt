package com.astro.news.data.remote.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ArticleResponseDto(
    @SerialName("id")
    val id: Int,
    @SerialName("title")
    val title: String,
    @SerialName("url")
    val url: String,
    @SerialName("image_url")
    val imageUrl: String?,
    @SerialName("news_site")
    val newsSite: String?,
    @SerialName("summary")
    val summary: String?,
    @SerialName("published_at")
    val publishedAt: String
)
