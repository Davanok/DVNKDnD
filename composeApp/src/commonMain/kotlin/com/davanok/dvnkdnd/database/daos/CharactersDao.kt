package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMin
import com.davanok.dvnkdnd.database.model.DbFullCharacter
import kotlin.uuid.Uuid

@Dao
interface CharactersDao {
    @Transaction
    @Query("SELECT * FROM characters WHERE id == :characterId")
    suspend fun getFullCharacter(characterId: Uuid): DbFullCharacter?

    @Query("SELECT id, user_id as userId, name, level, image FROM characters")
    suspend fun getCharactersMinList(): List<CharacterMin>
}