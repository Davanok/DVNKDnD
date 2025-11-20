package com.davanok.dvnkdnd.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.davanok.dvnkdnd.database.AppDatabase
import okio.Path.Companion.toPath
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

actual fun platformModule() = module {
    factory<RoomDatabase.Builder<AppDatabase>> {
        val context = androidContext()
        val dbFile = context.getDatabasePath("database.db")
        Room.databaseBuilder(
            context = context,
            name = dbFile.absolutePath
        )
    }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                androidContext().getDatabasePath("dvnkdnd.preferences_pb").absolutePath.toPath()
            }
        )
    }
}