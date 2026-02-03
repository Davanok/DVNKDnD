package com.davanok.dvnkdnd.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.davanok.dvnkdnd.data.local.db.AppDatabase
import com.davanok.dvnkdnd.domain.DataDirectories
import com.davanok.dvnkdnd.domain.values.AppStorageNames
import dev.zacsweers.metro.Provides
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSCachesDirectory
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual interface PlatformGraph {
    actual val dataDirectories: DataDirectories

    @Provides
    actual fun provideRoomDatabaseBuilder(): RoomDatabase.Builder<AppDatabase> =
        Room.databaseBuilder<AppDatabase>(
            name = (dataDirectories.dataDirectory / AppStorageNames.DB_DIR / AppStorageNames.DB_NAME).toString(),
        )

    @Provides
    actual fun providePreferencesDatastore(): DataStore<Preferences> =
        PreferenceDataStoreFactory.createWithPath(
            produceFile = { dataDirectories.dataDirectory / AppStorageNames.DB_DIR / AppStorageNames.PREFS_NAME }
        )

    @OptIn(ExperimentalForeignApi::class)
    @Provides
    actual fun provideDataDirectories(): DataDirectories {
        val dataDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )!!.path!!

        val cacheDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSCachesDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )!!.path!!

        return DataDirectories(
            dataDirectory = dataDirectory,
            cacheDirectory = cacheDirectory
        )
    }
}