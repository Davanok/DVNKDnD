package com.davanok.dvnkdnd.domain.repositories.local

import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import kotlin.uuid.Uuid

interface EntitiesRepository {
    suspend fun getExistingEntities(entityIds: List<Uuid>): Result<List<Uuid>>
    suspend fun getExistsEntity(entityId: Uuid): Result<Boolean>

    suspend fun getEntitiesWithSubList(entityIds: List<Uuid>): Result<List<DnDEntityWithSubEntities>>
    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): Result<List<DnDEntityWithSubEntities>>
    suspend fun getEntitiesWithSubList(vararg types: DnDEntityTypes): Result<Map<DnDEntityTypes, List<DnDEntityWithSubEntities>>>
}