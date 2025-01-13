package com.davanok.dvnkdnd.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import com.davanok.dvnkdnd.database.AppDatabase
import com.davanok.dvnkdnd.database.getDatabaseBuilder
import com.davanok.dvnkdnd.database.getDatastorePath
import okio.Path.Companion.toPath
import org.koin.dsl.module

fun platformModule() = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { getDatastorePath().toPath() }
        )
    }
}