package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.database.daos.EntitiesDao
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier

class EntitiesRepositoryImpl(
    private val dao: EntitiesDao
): EntitiesRepository {
    override suspend fun insertEntity(entity: DnDBaseEntity) =
        dao.insertEntity(entity)

    override suspend fun insertModifiers(vararg modifiers: EntityModifier) =
        dao.insertModifiers(*modifiers)
}