package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.database.daos.entities.BaseEntityDao
import kotlin.uuid.Uuid

class EntitiesRepositoryImpl(
    private val dao: BaseEntityDao,
) : EntitiesRepository {
    override suspend fun getExistingEntities(entityIds: List<Uuid>): Result<List<Uuid>> =
        runCatching {
            dao.getExistingEntities(entityIds)
        }

    override suspend fun getEntitiesWithSubList(entityIds: List<Uuid>) = runCatching {
        dao.getEntitiesWithSubList(entityIds).fastMap { it.toEntityWithSubEntities() }
    }

    override suspend fun getEntitiesWithSubList(type: DnDEntityTypes) = runCatching {
        dao.getEntitiesWithSubList(type).fastMap { it.toEntityWithSubEntities() }
    }

    override suspend fun getEntitiesWithSubList(
        vararg types: DnDEntityTypes
    ): Result<Map<DnDEntityTypes, List<DnDEntityWithSubEntities>>> = runCatching {
        dao.getEntitiesWithSubList(*types)
            .fastMap { it.toEntityWithSubEntities() }
            .groupBy { it.type }
    }
}