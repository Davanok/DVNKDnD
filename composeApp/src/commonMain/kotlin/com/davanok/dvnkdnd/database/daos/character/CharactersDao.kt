package com.davanok.dvnkdnd.database.daos.character

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.model.DbFullCharacter
import kotlin.uuid.Uuid

@Dao
interface CharactersDao {
    @Transaction
    @Query("SELECT * FROM characters WHERE id == :characterId")
    suspend fun getFullCharacter(characterId: Uuid): DbFullCharacter?

    @Query("SELECT * FROM characters")
    suspend fun getCharactersMinList(): List<Character>

    @Transaction
    suspend fun saveCharacter(character: CharacterFull): Uuid {
        TODO()
        return Uuid.Companion.NIL
    }
}