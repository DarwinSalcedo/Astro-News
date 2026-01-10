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

    @Query("SELECT * FROM articles")
    fun pagingSource(): PagingSource<Int, ArticleEntity>

    @Query("DELETE FROM articles")
    suspend fun clearAll()
}
