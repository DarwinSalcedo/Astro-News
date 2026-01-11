package com.astro.news.data.mapper

import com.astro.news.data.local.entity.ArticleEntity
import com.astro.news.data.remote.dto.ArticleResponseDto
import com.astro.news.domain.model.Article

fun ArticleResponseDto.toEntity(): ArticleEntity {
    return ArticleEntity(
        id = id,
        title = title,
        url = url,
        imageUrl = imageUrl ?: "",
        newsSite = newsSite ?: "",
        summary = summary ?: "",
        publishedAt = publishedAt,
        updatedAt = updatedAt ?: "",
        featured = featured
    )
}

fun ArticleEntity.toDomain(): Article {
    return Article(
        id = id,
        title = title,
        url = url,
        imageUrl = imageUrl,
        newsSite = newsSite,
        summary = summary,
        publishedAt = publishedAt,
        updatedAt = updatedAt,
        featured = featured
    )
}