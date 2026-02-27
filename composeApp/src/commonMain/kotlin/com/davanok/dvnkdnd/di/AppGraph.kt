package com.davanok.dvnkdnd.di

import com.davanok.dvnkdnd.AppClass
import dev.zacsweers.metrox.viewmodel.ViewModelGraph

interface AppGraph : LocalDataGraph, ExternalDataGraph, PlatformGraph, ViewModelGraph {
    val app: AppClass
}