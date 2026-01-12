package com.astro.news.data.local.dao

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.astro.news.data.local.entity.ArticleEntity

@Dao
interface ArticleDao {
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(articles: List<ArticleEntity>)

    @Query("SELECT * FROM articles ORDER BY publishedAt DESC")
    fun pagingSource(): PagingSource<Int, ArticleEntity>

    @Query("SELECT * FROM articles WHERE title LIKE '%' || :query || '%' OR summary LIKE '%' || :query || '%' ORDER BY publishedAt DESC")
    fun pagingSourceFiltered(query: String): PagingSource<Int, ArticleEntity>

    @Query("DELETE FROM articles")
    suspend fun clearAll()

    @Query("SELECT * FROM articles WHERE id = :id")
    suspend fun getArticleById(id: Int): ArticleEntity?
}
