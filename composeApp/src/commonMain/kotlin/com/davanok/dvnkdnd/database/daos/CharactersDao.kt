package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.model.DbCharacterWithModifiers
import kotlinx.coroutines.flow.Flow

@Dao
interface CharactersDao {
    @Query("SELECT id, name, level, mainImage FROM characters")
    fun getCharactersFlow(): Flow<List<CharacterMin>>

    @Insert
    suspend fun insertCharacter(character: Character): Long

    @Query("SELECT id, name FROM characters")
    suspend fun getCharacterWithModifiers(): DbCharacterWithModifiers
}