package com.davanok.dvnkdnd.domain.repositories.local

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import kotlin.uuid.Uuid

interface FullEntitiesRepository {
    suspend fun getFullEntity(entityId: Uuid): Result<DnDFullEntity?>
    suspend fun getFullEntities(entityIds: List<Uuid>): Result<List<DnDFullEntity>>

    suspend fun insertFullEntity(fullEntity: DnDFullEntity): Result<Unit>
    suspend fun insertFullEntities(fullEntities: List<DnDFullEntity>): Result<Unit>
}