package com.davanok.dvnkdnd.androidApp

import android.app.Application
import com.davanok.dvnkdnd.di.AndroidAppGraph
import dev.zacsweers.metro.createGraphFactory

class MyApp: Application() {
    val appGraph by lazy { createGraphFactory<AndroidAppGraph.Factory>().create(this) }
}