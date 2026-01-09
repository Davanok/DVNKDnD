package com.davanok.dvnkdnd.data.local.db.daos.entities

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Transaction
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.local.db.model.DbEntityWithSub
import kotlin.uuid.Uuid

@Dao
interface BaseEntityDao {
    @Transaction
    @Query("SELECT * FROM base_entities WHERE id IN (:entityIds)")
    suspend fun getEntitiesWithSubList(entityIds: List<Uuid>): List<DbEntityWithSub>

    @Transaction
    @Query("SELECT * FROM base_entities WHERE type == :type")
    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): List<DbEntityWithSub>

    @Transaction
    @Query("SELECT * FROM base_entities WHERE type IN (:types)")
    suspend fun getEntitiesWithSubList(vararg types: DnDEntityTypes): List<DbEntityWithSub>

    @Query("SELECT id FROM base_entities WHERE id IN (:entitiesIds)")
    suspend fun getExistingEntities(entitiesIds: List<Uuid>): List<Uuid>

    @Query("SELECT exists(SELECT 1 FROM base_entities WHERE id == :entityId)")
    suspend fun getExistsEntity(entityId: Uuid): Boolean
}