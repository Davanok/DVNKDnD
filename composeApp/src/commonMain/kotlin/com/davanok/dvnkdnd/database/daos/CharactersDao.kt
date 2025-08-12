package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMin
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterClass
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifierBonus
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedSkill
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import com.davanok.dvnkdnd.database.model.DbCharacterWithAllModifiers
import com.davanok.dvnkdnd.database.model.DbCharacterWithAllSkills
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