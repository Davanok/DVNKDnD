package com.davanok.dvnkdnd

import android.app.Application
import com.davanok.dvnkdnd.data.di.initKoin

class MyApp: Application() {
    override fun onCreate() {
        super.onCreate()
        initKoin(this@MyApp)
    }
}