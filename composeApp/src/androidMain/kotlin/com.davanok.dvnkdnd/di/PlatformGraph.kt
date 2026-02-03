package com.davanok.dvnkdnd.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.davanok.dvnkdnd.data.local.db.AppDatabase
import com.davanok.dvnkdnd.domain.DataDirectories
import com.davanok.dvnkdnd.domain.values.AppStorageNames
import dev.zacsweers.metro.Provides
import okio.Path.Companion.toPath

actual interface PlatformGraph {
    val context: Context
    actual val dataDirectories: DataDirectories

    @Provides
    actual fun provideRoomDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> =
        Room.databaseBuilder(context, context.getDatabasePath(AppStorageNames.DB_NAME).absolutePath)

    @Provides
    actual fun providePreferencesDatastore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            produceFile = {
                context.getDatabasePath(AppStorageNames.PREFS_NAME).absolutePath.toPath()
            }
        )

    @Provides
    actual fun provideDataDirectories(): DataDirectories = DataDirectories(
        dataDirectory = context.filesDir.absolutePath,
        cacheDirectory = context.cacheDir.absolutePath
    )
}