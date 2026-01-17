package com.davanok.dvnkdnd.data.local.db.daos.character

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCoins
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemActivationsCount
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemLink
import kotlin.uuid.Uuid

@Dao
interface CharacterItemsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterCoins(coins: DbCharacterCoins)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCharacterItemLinks(items: List<DbCharacterItemLink>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCharacterItemLink(item: DbCharacterItemLink)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun setCharacterUsedActivations(activations: DbCharacterItemActivationsCount)

    @Query("SELECT count FROM character_item_activation_count WHERE character_id = :characterId AND activation_id = :activationId")
    suspend fun getCharacterActivationsCount(characterId: Uuid, activationId: Uuid): Int?

    @Transaction
    suspend fun addCharacterUsedActivations(characterId: Uuid, activationId: Uuid, add: Int) {
        val usedActivations = getCharacterActivationsCount(characterId, activationId) ?: 0
        val modifiedValue = (usedActivations + add).coerceAtLeast(0)
        if (usedActivations != modifiedValue)
            setCharacterUsedActivations(DbCharacterItemActivationsCount(characterId, activationId, modifiedValue))
    }
}