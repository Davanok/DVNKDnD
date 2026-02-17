package com.davanok.dvnkdnd.data.local.db.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomDamageModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomRollModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomValueModifier
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomDamageModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomRollModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomValueModifier
import kotlin.uuid.Uuid

@Dao
interface CharacterCustomModifiersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterCustomValueModifiers(modifiers: List<DbCharacterCustomValueModifier>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterCustomRollModifiers(modifiers: List<DbCharacterCustomRollModifier>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterCustomDamageModifiers(modifiers: List<DbCharacterCustomDamageModifier>)

    @Transaction
    suspend fun insertCharacterCustomModifiers(characterId: Uuid, modifiers: List<CharacterCustomModifier>) {
        val valueModifiers = mutableListOf<DbCharacterCustomValueModifier>()
        val rollModifiers = mutableListOf<DbCharacterCustomRollModifier>()
        val damageModifiers = mutableListOf<DbCharacterCustomDamageModifier>()

        modifiers.forEach { modifier ->
            when (modifier) {
                is CharacterCustomValueModifier -> valueModifiers.add(modifier.toDbCharacterCustomModifier(characterId))
                is CharacterCustomRollModifier -> rollModifiers.add(modifier.toDbCharacterCustomModifier(characterId))
                is CharacterCustomDamageModifier -> damageModifiers.add(modifier.toDbCharacterCustomModifier(characterId))
            }
        }

        if (valueModifiers.isNotEmpty()) insertCharacterCustomValueModifiers(valueModifiers)
        if (rollModifiers.isNotEmpty()) insertCharacterCustomRollModifiers(rollModifiers)
        if (damageModifiers.isNotEmpty()) insertCharacterCustomDamageModifiers(damageModifiers)
    }
}