package com.davanok.dvnkdnd.data.local.db.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalSpeedValues
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalValues
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterOptionalSpeedValues
import com.davanok.dvnkdnd.data.local.mappers.character.toDbCharacterOptionalValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
import kotlin.uuid.Uuid

@Dao
interface CharacterOptionalValuesDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptionalValues(optionalValues: DbCharacterOptionalValues)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOptionalSpeedValues(speedValues: DbCharacterOptionalSpeedValues)

    @Transaction
    suspend fun insertFullOptionalValues(characterId: Uuid, optionalValues: CharacterOptionalValues) {
        insertOptionalValues(optionalValues.toDbCharacterOptionalValues(characterId))
        optionalValues.speedValues?.let {
            insertOptionalSpeedValues(it.toDbCharacterOptionalSpeedValues(characterId))
        }
    }
}