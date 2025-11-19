package com.davanok.dvnkdnd.database.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.database.entities.character.DbCharacter
import com.davanok.dvnkdnd.database.entities.character.DbCharacterAttributes
import com.davanok.dvnkdnd.database.entities.character.DbCharacterCoins
import com.davanok.dvnkdnd.database.entities.character.DbCharacterCustomModifier
import com.davanok.dvnkdnd.database.entities.character.DbCharacterFeat
import com.davanok.dvnkdnd.database.entities.character.DbCharacterHealth
import com.davanok.dvnkdnd.database.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.database.entities.character.DbCharacterMainEntity
import com.davanok.dvnkdnd.database.entities.character.DbCharacterProficiency
import com.davanok.dvnkdnd.database.entities.character.DbCharacterSelectedModifier
import com.davanok.dvnkdnd.database.entities.character.DbCharacterUsedSpellSlots
import com.davanok.dvnkdnd.database.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.database.entities.character.DbCharacterNote
import com.davanok.dvnkdnd.database.entities.character.DbCharacterOptionalValues

@Dao
interface CharacterEntitiesSettersDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: DbCharacter)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptionalValues(optionalValues: DbCharacterOptionalValues)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterImages(images: List<DbCharacterImage>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterCoins(coins: DbCharacterCoins)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterItemLinks(items: List<DbCharacterItemLink>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterAttributes(attributes: DbCharacterAttributes)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterHealth(health: DbCharacterHealth)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterUsedSpells(usedSpells: List<DbCharacterUsedSpellSlots>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterMainEntities(entities: List<DbCharacterMainEntity>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterFeats(feats: List<DbCharacterFeat>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterSelectedModifiers(modifiers: List<DbCharacterSelectedModifier>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterSelectedProficiencies(proficiencies: List<DbCharacterProficiency>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterCustomModifiers(modifiers: List<DbCharacterCustomModifier>)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterNotes(notes: List<DbCharacterNote>)
}