package com.astro.news.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.astro.news.data.local.dao.ArticleDao
import com.astro.news.data.local.entity.ArticleEntity

@Database(
    entities = [ArticleEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AstroDatabase : RoomDatabase() {
    abstract val articleDao: ArticleDao
}
