package com.davanok.dvnkdnd.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.room.RoomDatabase
import com.davanok.dvnkdnd.data.local.db.AppDatabase
import com.davanok.dvnkdnd.domain.DataDirectories

expect interface PlatformGraph {
    val dataDirectories: DataDirectories

    open fun provideRoomDatabaseBuilder(): RoomDatabase.Builder<AppDatabase>
    open fun providePreferencesDatastore(): DataStore<Preferences>

    open fun provideDataDirectories(): DataDirectories
}