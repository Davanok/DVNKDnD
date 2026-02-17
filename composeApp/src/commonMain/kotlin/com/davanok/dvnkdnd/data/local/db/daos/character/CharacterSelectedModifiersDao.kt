package com.davanok.dvnkdnd.data.local.db.daos.character

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedDamageModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedRollModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSelectedValueModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterSelectedModifiers
import kotlin.uuid.Uuid

@Dao
interface CharacterSelectedModifiersDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacterSelectedValueModifiers(modifiers: List<DbCharacterSelectedValueModifier>)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacterSelectedRollModifiers(modifiers: List<DbCharacterSelectedRollModifier>)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacterSelectedDamageModifiers(modifiers: List<DbCharacterSelectedDamageModifier>)

    @Transaction
    suspend fun insertCharacterSelectedModifiers(characterId: Uuid, modifiers: CharacterSelectedModifiers) {
        modifiers.valueModifiers
            .takeIf { it.isNotEmpty() }
            ?.map { DbCharacterSelectedValueModifier(characterId, it) }
            ?.let { insertCharacterSelectedValueModifiers(it) }
        modifiers.rollModifiers
            .takeIf { it.isNotEmpty() }
            ?.map { DbCharacterSelectedRollModifier(characterId, it) }
            ?.let { insertCharacterSelectedRollModifiers(it) }
        modifiers.rollModifiers
            .takeIf { it.isNotEmpty() }
            ?.map { DbCharacterSelectedDamageModifier(characterId, it) }
            ?.let { insertCharacterSelectedDamageModifiers(it) }
    }

    @Delete
    suspend fun deleteCharacterSelectedValueModifier(modifier: DbCharacterSelectedValueModifier)
    @Delete
    suspend fun deleteCharacterSelectedRollModifier(modifier: DbCharacterSelectedRollModifier)
    @Delete
    suspend fun deleteCharacterSelectedDamageModifier(modifier: DbCharacterSelectedDamageModifier)

    suspend fun deleteCharacterSelectedModifier(modifier: DbCharacterSelectedModifier) {
        when (modifier) {
            is DbCharacterSelectedValueModifier -> deleteCharacterSelectedValueModifier(modifier)
            is DbCharacterSelectedRollModifier -> deleteCharacterSelectedRollModifier(modifier)
            is DbCharacterSelectedDamageModifier -> deleteCharacterSelectedDamageModifier(modifier)
        }
    }

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacterSelectedValueModifier(modifier: DbCharacterSelectedValueModifier)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacterSelectedRollModifier(modifier: DbCharacterSelectedRollModifier)
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCharacterSelectedDamageModifier(modifier: DbCharacterSelectedDamageModifier)

    suspend fun insertCharacterSelectedModifier(modifier: DbCharacterSelectedModifier) {
        when (modifier) {
            is DbCharacterSelectedValueModifier -> insertCharacterSelectedValueModifier(modifier)
            is DbCharacterSelectedRollModifier -> insertCharacterSelectedRollModifier(modifier)
            is DbCharacterSelectedDamageModifier -> insertCharacterSelectedDamageModifier(modifier)
        }
    }
}