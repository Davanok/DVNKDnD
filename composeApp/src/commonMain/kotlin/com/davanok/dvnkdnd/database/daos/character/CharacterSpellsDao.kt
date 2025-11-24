package com.davanok.dvnkdnd.database.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.database.entities.character.DbCharacterUsedSpellSlots

@Dao
interface CharacterSpellsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterUsedSpells(usedSpells: List<DbCharacterUsedSpellSlots>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCharacterUsedSpells(usedSpells: DbCharacterUsedSpellSlots)
}