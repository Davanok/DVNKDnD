package com.davanok.dvnkdnd.data.local.db.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacter
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterAttributes
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterHealth
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterMainEntity
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalValues

@Dao
interface CharacterMainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: DbCharacter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptionalValues(optionalValues: DbCharacterOptionalValues)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterImages(images: List<DbCharacterImage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterAttributes(attributes: DbCharacterAttributes)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterHealth(health: DbCharacterHealth)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterMainEntities(entities: List<DbCharacterMainEntity>)
}