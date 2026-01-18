package com.davanok.dvnkdnd.data.local.db.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSpellLink
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterUsedSpellSlots

@Dao
interface CharacterSpellsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterUsedSpells(usedSpells: List<DbCharacterUsedSpellSlots>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCharacterUsedSpells(usedSpells: DbCharacterUsedSpellSlots)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun setCharacterSpell(spell: DbCharacterSpellLink)
}