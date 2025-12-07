package com.davanok.dvnkdnd.data.di

import com.davanok.dvnkdnd.data.local.db.AppDatabase
import com.davanok.dvnkdnd.data.local.db.daos.character.CharactersDao
import com.davanok.dvnkdnd.data.local.db.daos.entities.BaseEntityDao
import com.davanok.dvnkdnd.data.local.db.daos.entities.FullEntitiesDao
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

fun databaseModule() = module {
    singleOf(AppDatabase::buildDatabase)

    single<CharactersDao> { get<AppDatabase>().getCharactersDao() }
    single<BaseEntityDao> { get<AppDatabase>().getBaseEntityDao() }
    single<FullEntitiesDao> { get<AppDatabase>().getFullEntityDao() }
}