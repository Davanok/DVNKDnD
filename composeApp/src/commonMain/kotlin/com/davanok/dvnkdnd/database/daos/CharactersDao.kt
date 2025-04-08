package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.model.DbCharacterWithModifiers
import kotlin.uuid.Uuid

@Dao
interface CharactersDao {
    @Query("SELECT id, name, level, main_image FROM characters")
    suspend fun loadCharactersMinList(): List<CharacterMin>

    @Insert
    suspend fun insertCharacter(character: Character)

    @Transaction
    @Query("SELECT * FROM characters WHERE id == :characterId")
    suspend fun getCharacterWithModifiers(characterId: Uuid): DbCharacterWithModifiers
}