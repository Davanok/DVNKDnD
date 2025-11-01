package com.davanok.dvnkdnd.database.daos.character

import androidx.room.Dao
import androidx.room.Insert
import com.davanok.dvnkdnd.database.entities.character.Character
import com.davanok.dvnkdnd.database.entities.character.CharacterAttributes
import com.davanok.dvnkdnd.database.entities.character.CharacterCoins
import com.davanok.dvnkdnd.database.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.database.entities.character.CharacterFeat
import com.davanok.dvnkdnd.database.entities.character.CharacterHealth
import com.davanok.dvnkdnd.database.entities.character.CharacterImage
import com.davanok.dvnkdnd.database.entities.character.CharacterMainEntity
import com.davanok.dvnkdnd.database.entities.character.CharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.CharacterSelectedModifier
import com.davanok.dvnkdnd.database.entities.character.CharacterSpellSlots
import com.davanok.dvnkdnd.database.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.database.entities.character.DbCharacterNote
import com.davanok.dvnkdnd.database.entities.character.DbCharacterOptionalValues

@Dao
interface CharacterEntitiesDao {
    @Insert
    suspend fun insertCharacter(character: Character)
    @Insert
    suspend fun insertOptionalValues(optionalValues: DbCharacterOptionalValues)
    @Insert
    suspend fun insertCharacterImages(images: List<CharacterImage>)
    @Insert
    suspend fun insertCharacterCoins(coins: CharacterCoins)
    @Insert
    suspend fun insertCharacterItemLinks(items: List<DbCharacterItemLink>)
    @Insert
    suspend fun insertCharacterAttributes(attributes: CharacterAttributes)
    @Insert
    suspend fun insertCharacterHealth(health: CharacterHealth)
    @Insert
    suspend fun insertCharacterUsedSpells(usedSpells: CharacterSpellSlots)
    @Insert
    suspend fun insertCharacterMainEntities(entities: List<CharacterMainEntity>)
    @Insert
    suspend fun insertCharacterFeats(feats: List<CharacterFeat>)
    @Insert
    suspend fun insertCharacterSelectedModifiers(modifiers: List<CharacterSelectedModifier>)
    @Insert
    suspend fun insertCharacterSelectedProficiencies(proficiencies: List<CharacterProficiency>)
    @Insert
    suspend fun insertCharacterCustomModifiers(modifiers: List<CharacterCustomModifier>)
    @Insert
    suspend fun insertCharacterNotes(notes: List<DbCharacterNote>)
}