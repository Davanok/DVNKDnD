@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.model.DbCharacterWithModifiers
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
interface CharactersDao {
    @Query("SELECT id, name, level, mainImage FROM characters")
    suspend fun loadCharactersMinList(): List<CharacterMin>

    @Insert
    suspend fun insertCharacter(character: Character): Uuid

    @Transaction
    @Query("SELECT * FROM characters")
    suspend fun getCharacterWithModifiers(): DbCharacterWithModifiers
}