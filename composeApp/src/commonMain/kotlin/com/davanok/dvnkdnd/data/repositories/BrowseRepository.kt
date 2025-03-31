package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.DnDEntityFullInfo
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities

interface BrowseRepository {
    suspend fun loadEntityFullInfo(entityType: DnDEntityTypes, entityId: Long): DnDEntityFullInfo
    suspend fun loadEntities(entityType: DnDEntityTypes): List<DnDEntityWithSubEntities>
}