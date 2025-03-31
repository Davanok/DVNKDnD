package com.davanok.dvnkdnd.data.di

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.davanok.dvnkdnd.database.AppDatabase
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.daos.ClassesDao
import com.davanok.dvnkdnd.database.daos.NewCharacterDao
import com.davanok.dvnkdnd.database.daos.ProficienciesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

fun databaseModule() = module {
    single<AppDatabase> {
        get<RoomDatabase.Builder<AppDatabase>>()
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .build()
    }

    single<CharactersDao> { get<AppDatabase>().getCharactersDao() }
    single<NewCharacterDao> { get<AppDatabase>().getNewCharacterDao() }
    single<ClassesDao> { get<AppDatabase>().getClassesDao() }
    single<ProficienciesDao> { get<AppDatabase>().getProficienciesDao() }
}