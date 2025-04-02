package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin

interface NewCharacterRepository {
    suspend fun getEntitiesMinList(type: DnDEntityTypes, parentId: Long? = null): List<DnDEntityMin>
}