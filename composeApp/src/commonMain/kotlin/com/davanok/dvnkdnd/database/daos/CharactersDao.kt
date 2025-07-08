package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.CharacterMin
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterClass
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifierBonus
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import com.davanok.dvnkdnd.database.model.DbCharacterWithAllModifiers
import com.davanok.dvnkdnd.database.model.DbCharacterWithAllSkills
import kotlin.uuid.Uuid

@Dao
interface CharactersDao {
    @Query("SELECT id, name, level, image FROM characters")
    suspend fun getCharactersMinList(): List<CharacterMin>

    @Insert
    suspend fun insertCharacter(character: Character)
    @Insert
    suspend fun insertCharacterClass(cls: CharacterClass)

    @Transaction
    @Query("SELECT * FROM characters WHERE id == :characterId")
    suspend fun getCharacterWithAllModifiers(characterId: Uuid): DbCharacterWithAllModifiers

    @Transaction
    @Query("SELECT * FROM characters WHERE id == :characterId")
    suspend fun getCharacterWithAllSkills(characterId: Uuid): DbCharacterWithAllSkills

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterStats(stats: CharacterStats)

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacterSelectedModifierBonuses(items: List<CharacterSelectedModifierBonus>)
}