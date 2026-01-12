package com.astro.news.data.di

import android.content.Context
import androidx.room.Room
import com.astro.news.data.local.AstroDatabase
import com.astro.news.data.local.dao.ArticleDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
private const val DATABASE_NAME = "astro_news.db"

    @Provides
    @Singleton
    fun provideAstroDatabase(@ApplicationContext context: Context): AstroDatabase {
        return Room.databaseBuilder(
            context,
            AstroDatabase::class.java,
            DATABASE_NAME
        ).build()
    }

    @Provides
    @Singleton
    fun provideArticleDao(database: AstroDatabase): ArticleDao = database.articleDao
}
