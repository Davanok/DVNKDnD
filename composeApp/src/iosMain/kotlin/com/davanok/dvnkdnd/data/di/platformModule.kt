package com.davanok.dvnkdnd.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.davanok.dvnkdnd.data.platform.appDataDirectory
import com.davanok.dvnkdnd.database.AppDatabase
import org.koin.dsl.module

actual fun platformModule() = module {
    factory<RoomDatabase.Builder<AppDatabase>> {
        val path = appDataDirectory() / "database" / "dvnkdnd.db"
        Room.databaseBuilder<AppDatabase>(
            name = path.toString(),
        )
    }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { appDataDirectory() / "database" / "dvnkdnd.preferences_pb" }
        )
    }
}