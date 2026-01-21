package com.davanok.dvnkdnd.androidApp

import android.app.Application
import com.davanok.dvnkdnd.data.platform.appCacheDir
import com.davanok.dvnkdnd.data.platform.appDataDir

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        appDataDir = filesDir.absolutePath
        appCacheDir = cacheDir.absolutePath
    }
}