package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import kotlin.uuid.Uuid

interface EntitiesRepository {
    suspend fun getExistingEntities(entityIds: List<Uuid>): Result<List<Uuid>>

    suspend fun getEntitiesWithSubList(entityIds: List<Uuid>): Result<List<DnDEntityWithSubEntities>>
    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): Result<List<DnDEntityWithSubEntities>>
    suspend fun getEntitiesWithSubList(vararg types: DnDEntityTypes): Result<Map<DnDEntityTypes, List<DnDEntityWithSubEntities>>>
}