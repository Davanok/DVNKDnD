package com.davanok.dvnkdnd.database.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.database.entities.character.DbCharacterCustomModifier
import com.davanok.dvnkdnd.database.entities.character.DbCharacterSelectedModifier

@Dao
interface CharacterModifiersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterSelectedModifiers(modifiers: List<DbCharacterSelectedModifier>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterCustomModifiers(modifiers: List<DbCharacterCustomModifier>)
}