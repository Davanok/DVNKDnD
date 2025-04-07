@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.DnDEntityFullInfo
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

interface BrowseRepository {
    suspend fun loadEntityFullInfo(entityId: Uuid): DnDEntityFullInfo
    suspend fun loadEntitiesFullInfo(entityIds: List<Uuid>): List<DnDEntityFullInfo>
    suspend fun loadEntitiesWithSub(entityType: DnDEntityTypes): List<DnDEntityWithSubEntities>

    suspend fun getValue(key: String): String
}