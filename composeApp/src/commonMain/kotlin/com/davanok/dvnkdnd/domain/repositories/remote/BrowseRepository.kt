package com.davanok.dvnkdnd.domain.repositories.remote

import com.davanok.dvnkdnd.core.PagedResult
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import kotlin.uuid.Uuid

interface BrowseRepository {
    suspend fun loadEntityFullInfo(entityId: Uuid): Result<DnDFullEntity?>
    suspend fun loadEntitiesFullInfo(entityIds: List<Uuid>): Result<List<DnDFullEntity>>
    suspend fun loadEntitiesWithSub(entityType: DnDEntityTypes): Result<List<DnDEntityWithSubEntities>>
    suspend fun loadEntitiesWithSubPaged(
        entityType: DnDEntityTypes,
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): Result<PagedResult<DnDEntityWithSubEntities>>

    suspend fun getPropertyValue(key: String): Result<String>
}