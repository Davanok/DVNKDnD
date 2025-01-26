package com.davanok.dvnkdnd.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.database.daos.CharactersDao
import com.davanok.dvnkdnd.database.daos.NewCharacterDao
import com.davanok.dvnkdnd.database.entities.ListSpellAdapter
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDRace
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSubRace
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSubclass

@Database(
    entities = [
        Character::class,
        DnDRace::class,
        DnDSubRace::class,
        DnDClass::class,
        DnDSubclass::class,
        DnDBackground::class
    ],
    version = 1
)
@TypeConverters(ListIntAdapter::class, ListSpellAdapter::class)
abstract class AppDatabase: RoomDatabase() {
    abstract fun getCharactersDao(): CharactersDao
    abstract fun getNewCharacterDao(): NewCharacterDao
}


