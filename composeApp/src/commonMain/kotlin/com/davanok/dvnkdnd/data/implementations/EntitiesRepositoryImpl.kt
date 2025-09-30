package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.util.runLogging
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.database.daos.entities.BaseEntityDao
import kotlin.uuid.Uuid

class EntitiesRepositoryImpl(
    private val dao: BaseEntityDao,
) : EntitiesRepository {
    override suspend fun getExistingEntities(entityIds: List<Uuid>): Result<List<Uuid>> =
        runLogging("getExistingEntities") {
            dao.getExistingEntities(entityIds)
        }

    override suspend fun getEntitiesWithSubList(entityIds: List<Uuid>) =
        runLogging("getEntitiesWithSubList by ids") {
            dao.getEntitiesWithSubList(entityIds).fastMap { it.toEntityWithSubEntities() }
        }

    override suspend fun getEntitiesWithSubList(type: DnDEntityTypes) =
        runLogging("getEntitiesWithSubList by type") {
            dao.getEntitiesWithSubList(type).fastMap { it.toEntityWithSubEntities() }
        }

    override suspend fun getEntitiesWithSubList(
        vararg types: DnDEntityTypes
    ): Result<Map<DnDEntityTypes, List<DnDEntityWithSubEntities>>> =
        runLogging("getEntitiesWithSubList by types") {
            dao.getEntitiesWithSubList(*types)
                .fastMap { it.toEntityWithSubEntities() }
                .groupBy { it.type }
        }
}