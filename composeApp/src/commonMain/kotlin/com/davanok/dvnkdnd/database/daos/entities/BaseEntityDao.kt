package com.davanok.dvnkdnd.database.daos.entities

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.database.model.EntityWithSub
import kotlin.uuid.Uuid

@Dao
interface BaseEntityDao {
    @Query("SELECT id, type, name, source FROM base_entities WHERE type == :type AND parent_id == :parentId")
    suspend fun getEntitiesMinList(type: DnDEntityTypes, parentId: Uuid? = null): List<DnDEntityMin>

    @Transaction
    @Query("SELECT * FROM base_entities WHERE id IN (:entityIds)")
    suspend fun getEntitiesWithSubList(entityIds: List<Uuid>): List<EntityWithSub>

    @Transaction
    @Query("SELECT * FROM base_entities WHERE type == :type")
    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): List<EntityWithSub>

    @Transaction
    @Query("SELECT * FROM base_entities WHERE type IN (:types)")
    suspend fun getEntitiesWithSubList(vararg types: DnDEntityTypes): List<EntityWithSub>

    @Query("SELECT id FROM base_entities WHERE id IN (:entitiesIds)")
    suspend fun getExistingEntities(entitiesIds: List<Uuid>): List<Uuid>

    @Query("SELECT exists(SELECT 1 FROM base_entities WHERE id == :entityId)")
    suspend fun getExistsEntity(entityId: Uuid): Boolean
}