package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.DnDEntityFullInfo
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes

interface BrowseRepository {
    suspend fun loadEntityFullInfo(entityType: DnDEntityTypes, entityId: Long): DnDEntityFullInfo
}