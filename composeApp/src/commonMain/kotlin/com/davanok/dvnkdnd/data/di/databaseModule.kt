package com.davanok.dvnkdnd.data.di

import androidx.room.RoomDatabase
import androidx.sqlite.SQLiteConnection
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import androidx.sqlite.execSQL
import com.davanok.dvnkdnd.database.AppDatabase
import com.davanok.dvnkdnd.database.daos.character.CharactersDao
import com.davanok.dvnkdnd.database.daos.entities.BaseEntityDao
import com.davanok.dvnkdnd.database.daos.entities.FullEntitiesDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import org.koin.dsl.module

fun databaseModule() = module {
    single<AppDatabase> {
        get<RoomDatabase.Builder<AppDatabase>>()
            .fallbackToDestructiveMigration(true)
            .setDriver(BundledSQLiteDriver())
            .setQueryCoroutineContext(Dispatchers.IO)
            .addCallback(object : RoomDatabase.Callback() {
                override fun onCreate(connection: SQLiteConnection) {
                    super.onCreate(connection)

                    AppDatabase.initialExecs.forEach {
                        connection.execSQL(it)
                    }
                }
            })
            .build()
    }

    single<CharactersDao> { get<AppDatabase>().getCharactersDao() }
    single<BaseEntityDao> { get<AppDatabase>().getBaseEntityDao() }
    single<FullEntitiesDao> { get<AppDatabase>().getFullEntityDao() }
}