package com.davanok.dvnkdnd.data.di

import org.koin.core.context.startKoin

fun initKoin() = startKoin {
    modules(platformModule(), commonModule(), databaseModule(), viewModelsModule())
}