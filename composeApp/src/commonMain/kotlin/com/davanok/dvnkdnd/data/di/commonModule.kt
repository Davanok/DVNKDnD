package com.davanok.dvnkdnd.data.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.davanok.dvnkdnd.data.implementations.CharactersListRepositoryImpl
import com.davanok.dvnkdnd.data.repositories.CharactersListRepository
import com.davanok.dvnkdnd.database.AppDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

fun commonModule() = module {
    single<AppDatabase> {
        get<RoomDatabase.Builder<AppDatabase>>()
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }
    single<CharactersListRepository> { CharactersListRepositoryImpl(get()) }
}