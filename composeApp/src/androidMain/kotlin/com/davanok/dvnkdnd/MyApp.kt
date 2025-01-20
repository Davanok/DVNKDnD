package com.davanok.dvnkdnd

import android.app.Application
import com.davanok.dvnkdnd.data.di.initKoin

lateinit var appDataDir: String
    private set

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        appDataDir = filesDir.path
        initKoin(this@MyApp)
    }
}