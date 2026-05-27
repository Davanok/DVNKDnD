package com.davanok.dvnkdnd.di

import co.touchlab.kermit.Logger
import com.davanok.dvnkdnd.AppClass
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

interface AppGraph : UiGraph, LocalDataGraph, ExternalDataGraph, PlatformGraph, ViewModelGraph {
    val app: AppClass

    @Provides
    @SingleIn(AppScope::class)
    fun provideLogger(): Logger = Logger
}