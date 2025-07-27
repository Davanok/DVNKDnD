package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import kotlin.uuid.Uuid


interface BrowseRepository {
    suspend fun loadEntityFullInfo(entityId: Uuid): Result<DnDFullEntity>
    suspend fun loadEntitiesFullInfo(entityIds: List<Uuid>): Result<List<DnDFullEntity>>
    suspend fun loadEntitiesWithSub(entityType: DnDEntityTypes): Result<List<DnDEntityWithSubEntities>>

    suspend fun getValue(key: String): Result<String>
}