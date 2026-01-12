package com.astro.news

import android.app.Application
import com.astro.news.data.BuildConfig
import dagger.hilt.android.HiltAndroidApp

import timber.log.Timber

@HiltAndroidApp
class AstroNewsApp : Application() {
    override fun onCreate() {
        super.onCreate()
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
    }
}