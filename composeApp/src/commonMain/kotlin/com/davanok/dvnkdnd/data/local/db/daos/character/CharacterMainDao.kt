package com.davanok.dvnkdnd.data.local.db.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacter
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterAttributes
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterHealth
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterMainEntity
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSettings
import kotlin.uuid.Uuid

@Dao
interface CharacterMainDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacter(character: DbCharacter)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterImages(images: List<DbCharacterImage>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterAttributes(attributes: DbCharacterAttributes)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterHealth(health: DbCharacterHealth)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterMainEntities(entities: List<DbCharacterMainEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun setCharacterMainEntity(entity: DbCharacterMainEntity)

    @Query("UPDATE character_main_entities SET level = :level WHERE character_id = :characterId AND entity_id = :parentEntityId")
    suspend fun setCharacterMainEntityLevel(characterId: Uuid, parentEntityId: Uuid, level: Int)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCharacterSettings(settings: DbCharacterSettings)
}