package com.davanok.dvnkdnd.database.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
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
interface CharacterEntitiesSettersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: Character)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptionalValues(optionalValues: DbCharacterOptionalValues)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterImages(images: List<CharacterImage>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterCoins(coins: CharacterCoins)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterItemLinks(items: List<DbCharacterItemLink>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterAttributes(attributes: CharacterAttributes)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterHealth(health: CharacterHealth)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterUsedSpells(usedSpells: CharacterSpellSlots)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterMainEntities(entities: List<CharacterMainEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterFeats(feats: List<CharacterFeat>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterSelectedModifiers(modifiers: List<CharacterSelectedModifier>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterSelectedProficiencies(proficiencies: List<CharacterProficiency>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterCustomModifiers(modifiers: List<CharacterCustomModifier>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterNotes(notes: List<DbCharacterNote>)
}