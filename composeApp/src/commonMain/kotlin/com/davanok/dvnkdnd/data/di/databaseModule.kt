package com.davanok.dvnkdnd.data.di

import com.davanok.dvnkdnd.database.AppDatabase
import com.davanok.dvnkdnd.database.daos.CharactersDao
import org.koin.dsl.module

fun databaseModule() = module {
    single<CharactersDao> { get<AppDatabase>().getCharactersDao() }
}