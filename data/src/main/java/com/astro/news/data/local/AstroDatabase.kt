package com.astro.news.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.astro.news.data.local.dao.ArticleDao
import com.astro.news.data.local.dao.RemoteKeysDao
import com.astro.news.data.local.entity.ArticleEntity
import com.astro.news.data.local.entity.RemoteKeysEntity

@Database(
    entities = [ArticleEntity::class, RemoteKeysEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AstroDatabase : RoomDatabase() {
    abstract val articleDao: ArticleDao
    abstract val remoteKeysDao: RemoteKeysDao
}
