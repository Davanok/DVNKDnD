package com.davanok.dvnkdnd.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.entities.ListSpellAdapter
import com.davanok.dvnkdnd.database.entities.items.DnDItem

@Database(
    entities = [
        DnDItem::class
    ],
    version = 1
)
@TypeConverters(ListIntAdapter::class, ListSpellAdapter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getCharactersDao(): CharactersDao
}


