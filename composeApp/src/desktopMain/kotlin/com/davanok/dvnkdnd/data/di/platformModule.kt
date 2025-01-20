package com.davanok.dvnkdnd.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.davanok.dvnkdnd.data.platform.appDataDirectory
import com.davanok.dvnkdnd.database.AppDatabase
import org.koin.dsl.module

fun platformModule() = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        val path = appDataDirectory() / "database.db"
        Room.databaseBuilder(
            name = path.toString()
        )
    }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                appDataDirectory() / "dvnkdnd.preferences_pb"
            }
        )
    }
}