package com.davanok.dvnkdnd

import android.app.Application

lateinit var appDataDir: String
    private set
lateinit var appCacheDir: String
    private set

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        appDataDir = filesDir.absolutePath
        appCacheDir = cacheDir.absolutePath
    }
}