package com.davanok.dvnkdnd.data.repositories

import com.davanok.dvnkdnd.data.model.entities.DnDEntityFullInfo
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier

interface EntitiesRepository {
    suspend fun insertFullEntities(fullEntities: List<DnDEntityFullInfo>)

    suspend fun insertEntity(entity: DnDBaseEntity)
    suspend fun insertModifiers(vararg modifiers: EntityModifier)
}