package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import kotlin.uuid.Uuid

interface FullEntitiesRepository {
    suspend fun getFullEntity(entityId: Uuid): Result<DnDFullEntity?>
    suspend fun getFullEntities(entityIds: List<Uuid>): Result<List<DnDFullEntity>>

    suspend fun insertFullEntity(fullEntity: DnDFullEntity): Result<Unit>
    suspend fun insertFullEntities(fullEntities: List<DnDFullEntity>): Result<Unit>
}