@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.daos

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.model.EntityWithSub
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Dao
interface EntitiesDao {
    @Query("SELECT id, type, name, source FROM base_entities WHERE type == :type AND parentId == :parentId")
    suspend fun getEntitiesMinList(type: DnDEntityTypes, parentId: Uuid? = null): List<DnDEntityMin>
    @Transaction
    @Query("SELECT * FROM base_entities WHERE type == :type")
    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): List<EntityWithSub>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntity(entity: DnDBaseEntity)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertModifiers(vararg modifiers: EntityModifier)

    @Query("SELECT id FROM base_entities WHERE id IN (:entitiesWebIds)")
    suspend fun getExistingEntities(entitiesIds: List<Uuid>): List<Uuid>
}