package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities

interface NewCharacterRepository {
    suspend fun getEntitiesWithSubList(type: DnDEntityTypes): List<DnDEntityWithSubEntities>
}