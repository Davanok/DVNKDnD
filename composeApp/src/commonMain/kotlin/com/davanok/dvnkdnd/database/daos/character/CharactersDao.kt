package com.davanok.dvnkdnd.database.daos.character

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.database.entities.character.DbCharacter
import com.davanok.dvnkdnd.database.entities.character.DbCharacterFeat
import com.davanok.dvnkdnd.database.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.database.entities.character.DbCharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.DbCharacterSelectedModifier
import com.davanok.dvnkdnd.database.entities.character.DbCharacterUsedSpellSlots
import com.davanok.dvnkdnd.database.model.DbFullCharacter
import com.davanok.dvnkdnd.database.model.adapters.character.toDbCharacter
import com.davanok.dvnkdnd.database.model.adapters.character.toDbCharacterAttributes
import com.davanok.dvnkdnd.database.model.adapters.character.toDbCharacterCoins
import com.davanok.dvnkdnd.database.model.adapters.character.toDbCharacterCustomModifier
import com.davanok.dvnkdnd.database.model.adapters.character.toDbCharacterHealth
import com.davanok.dvnkdnd.database.model.adapters.character.toDbCharacterItemLink
import com.davanok.dvnkdnd.database.model.adapters.character.toDbCharacterMainEntity
import com.davanok.dvnkdnd.database.model.adapters.character.toDbCharacterNote
import com.davanok.dvnkdnd.database.model.adapters.character.toDbCharacterOptionalValues
import kotlinx.coroutines.flow.Flow
import kotlin.uuid.Uuid

@Dao
interface CharactersDao: CharacterMainDao,
        CharacterModifiersDao,
        CharacterSelectableEntitiesDao,
        CharacterSpellsDao,
        CharacterItemsDao,
        CharacterNotesDao
{
    @Transaction
    @Query("SELECT * FROM characters WHERE id == :characterId")
    suspend fun getFullCharacter(characterId: Uuid): DbFullCharacter
    @Transaction
    @Query("SELECT * FROM characters WHERE id == :characterId")
    fun getFullCharacterFlow(characterId: Uuid): Flow<DbFullCharacter>

    @Query("SELECT * FROM characters")
    fun getCharactersMinListFlow(): Flow<List<DbCharacter>>

    @Transaction
    suspend fun saveCharacter(character: CharacterFull): Uuid {
        val characterId = character.character.id

        insertCharacter(character.character.toDbCharacter())

        insertOptionalValues(character.optionalValues.toDbCharacterOptionalValues(characterId))

        character.images
            .map { DbCharacterImage(id = it.id, characterId = characterId, path = it.path) }
            .let { insertCharacterImages(it) }
        character.coins.toDbCharacterCoins(characterId).let { insertCharacterCoins(it) }
        character.items
            .map { it.toDbCharacterItemLink(characterId) }
            .let { insertCharacterItemLinks(it) }
        character.attributes.toDbCharacterAttributes(characterId)
            .let { insertCharacterAttributes(it) }
        character.health.toDbCharacterHealth(characterId)
            .let { insertCharacterHealth(it) }

        character.usedSpells
            .map { DbCharacterUsedSpellSlots(characterId = characterId, spellSlotTypeId = it.key, usedSpells = it.value.toList()) }
            .let { insertCharacterUsedSpells(it) }

        character.mainEntities
            .map { it.toDbCharacterMainEntity(characterId) }
            .let { insertCharacterMainEntities(it) }
        character.feats
            .map { DbCharacterFeat(characterId, it.entity.id) }
            .let { insertCharacterFeats(it) }

        character.selectedModifiers
            .map { DbCharacterSelectedModifier(characterId, it) }
            .let { insertCharacterSelectedModifiers(it) }

        character.selectedProficiencies
            .map { DbCharacterProficiency(characterId, it) }
            .let { insertCharacterSelectedProficiencies(it) }

        character.customModifiers
            .map { it.toDbCharacterCustomModifier(characterId) }
            .let { insertCharacterCustomModifiers(it) }
        character.notes
            .map { it.toDbCharacterNote(characterId) }
            .let { insertCharacterNotes(it) }

        return characterId
    }
}