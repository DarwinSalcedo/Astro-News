package com.astro.news.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.astro.news.domain.model.Article

@Entity(tableName = "articles")
data class ArticleEntity(
    @PrimaryKey
    val id: Int,
    val title: String,
    val url: String,
    val imageUrl: String,
    val newsSite: String,
    val summary: String,
    val publishedAt: String,
    val updatedAt: String,
    val featured: Boolean
) {
    fun toDomain(): Article {
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
}

fun Article.toEntity(): ArticleEntity {
    return ArticleEntity(
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
