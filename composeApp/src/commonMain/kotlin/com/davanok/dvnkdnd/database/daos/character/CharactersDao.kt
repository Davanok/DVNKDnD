package com.davanok.dvnkdnd.database.daos.character

import androidx.compose.ui.util.fastMap
import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.toCharacter
import com.davanok.dvnkdnd.data.model.entities.character.toCharacterCoins
import com.davanok.dvnkdnd.data.model.entities.character.toCharacterHealth
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toCharacterAttributes
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterFeat
import com.davanok.dvnkdnd.database.entities.character.CharacterImage
import com.davanok.dvnkdnd.database.entities.character.CharacterMainEntity
import com.davanok.dvnkdnd.database.entities.character.CharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifier
import com.davanok.dvnkdnd.database.entities.character.CharacterSpellSlots
import com.davanok.dvnkdnd.database.model.DbFullCharacter
import kotlin.uuid.Uuid

@Dao
interface CharactersDao: CharacterEntitiesDao {
    @Transaction
    @Query("SELECT * FROM characters WHERE id == :characterId")
    suspend fun getFullCharacter(characterId: Uuid): DbFullCharacter?

    @Query("SELECT * FROM characters")
    suspend fun getCharactersMinList(): List<Character>

    @Transaction
    suspend fun saveCharacter(character: CharacterFull): Uuid {
        val characterId = character.character.id

        insertCharacter(character.character.toCharacter())

        character.images
            .fastMap { CharacterImage(id = it.id, characterId = characterId, path = it.path) }
            .let { insertCharacterImages(it) }
        character.coins?.toCharacterCoins(characterId)?.let { insertCharacterCoins(it) }
        character.attributes.toCharacterAttributes(characterId).let { insertCharacterAttributes(it) }
        character.health?.toCharacterHealth(characterId)?.let { insertCharacterHealth(it) }

        insertCharacterUsedSpells(CharacterSpellSlots(characterId, character.usedSpells))

        character.mainEntities.fastMap { CharacterMainEntity(characterId, it.entity.id, it.subEntity?.id, it.level) }.let { insertCharacterMainEntities(it) }
        character.feats.fastMap { CharacterFeat(characterId, it.id) }.let { insertCharacterFeats(it) }

        character.selectedModifiers
            .fastMap { CharacterSelectedModifier(characterId, it) }
            .let { insertCharacterSelectedModifiers(it) }

        character.selectedProficiencies
            .fastMap { CharacterProficiency(characterId, it) }
            .let { insertCharacterSelectedProficiencies(it) }

        return characterId
    }
}