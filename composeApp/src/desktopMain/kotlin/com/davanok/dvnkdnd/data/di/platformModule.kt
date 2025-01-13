package com.davanok.dvnkdnd.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import com.davanok.dvnkdnd.database.AppDatabase
import com.davanok.dvnkdnd.database.getDatabaseBuilder
import okio.Path.Companion.toPath
import org.koin.dsl.module
import java.io.File

fun platformModule() = module {
    single<RoomDatabase.Builder<AppDatabase>> {
        getDatabaseBuilder()
    }
    single<DataStore<Preferences>> {
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                File(System.getProperty("java.io.tmpdir"), "dvnkdnd.preferences_pb").absolutePath.toPath()
            }
        )
    }
}