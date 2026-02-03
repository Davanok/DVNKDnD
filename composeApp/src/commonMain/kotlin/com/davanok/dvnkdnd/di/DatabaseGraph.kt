package com.davanok.dvnkdnd.di

import androidx.room.RoomDatabase
import com.davanok.dvnkdnd.data.local.db.AppDatabase
import com.davanok.dvnkdnd.data.local.db.daos.character.CharactersDao
import com.davanok.dvnkdnd.data.local.db.daos.entities.BaseEntityDao
import com.davanok.dvnkdnd.data.local.db.daos.entities.FullEntitiesDao
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.Provides
import dev.zacsweers.metro.SingleIn


interface DatabaseGraph {
    @Provides
    @SingleIn(AppScope::class)
    fun provideDatabase(builder: RoomDatabase.Builder<AppDatabase>): AppDatabase = AppDatabase.buildDatabase(builder)

    @Provides
    @SingleIn(AppScope::class)
    fun provideCharactersDao(database: AppDatabase): CharactersDao = database.getCharactersDao()

    @Provides
    @SingleIn(AppScope::class)
    fun provideBaseEntityDao(database: AppDatabase): BaseEntityDao = database.getBaseEntityDao()

    @Provides
    @SingleIn(AppScope::class)
    fun provideFullEntitiesDao(database: AppDatabase): FullEntitiesDao = database.getFullEntityDao()
}