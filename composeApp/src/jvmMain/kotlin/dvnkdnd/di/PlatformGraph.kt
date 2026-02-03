package com.davanok.dvnkdnd.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.room.Room
import androidx.room.RoomDatabase
import com.davanok.dvnkdnd.BuildConfig
import com.davanok.dvnkdnd.data.local.db.AppDatabase
import com.davanok.dvnkdnd.domain.DataDirectories
import com.davanok.dvnkdnd.domain.values.AppStorageNames
import dev.zacsweers.metro.Provides
import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath
import org.jetbrains.skiko.OS
import org.jetbrains.skiko.hostOs

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


    @Provides
    actual fun provideDataDirectories(): DataDirectories {
        val appId = BuildConfig.PACKAGE_NAME
        val getEnvPath: (String) -> Path = { System.getenv(it).toPath(normalize = true) }
        val appDataDirectory = when (hostOs) {
                OS.MacOS -> getEnvPath("HOME") / "Library" / "Application Support" / appId
                OS.Windows -> getEnvPath("LOCALAPPDATA") / appId
                OS.Linux -> getEnvPath("HOME") / ".local" / "share" / appId
                else -> getEnvPath("HOME") / ".$appId"
            }

        val appCacheDirectory = FileSystem.SYSTEM_TEMPORARY_DIRECTORY / BuildConfig.PACKAGE_NAME

        return DataDirectories(
            dataDirectory = appDataDirectory,
            cacheDirectory = appCacheDirectory
        )
    }
}