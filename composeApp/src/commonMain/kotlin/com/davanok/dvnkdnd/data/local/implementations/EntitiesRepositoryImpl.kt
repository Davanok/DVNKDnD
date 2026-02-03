package com.davanok.dvnkdnd.data.local.implementations

import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.data.local.db.daos.entities.BaseEntityDao
import com.davanok.dvnkdnd.data.local.mappers.entities.toEntityWithSubEntities
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.repositories.local.EntitiesRepository
import dev.zacsweers.metro.Inject
import kotlin.uuid.Uuid

@Inject
class EntitiesRepositoryImpl(
    private val dao: BaseEntityDao,
) : EntitiesRepository {
    override suspend fun getExistingEntities(entityIds: List<Uuid>): Result<List<Uuid>> =
        runLogging("getExistingEntities") {
            dao.getExistingEntities(entityIds)
        }

    override suspend fun getExistsEntity(entityId: Uuid): Result<Boolean> =
        runLogging("getExistsEntity") {
            dao.getExistsEntity(entityId)
        }

    override suspend fun getEntitiesWithSubList(entityIds: List<Uuid>) =
        runLogging("getEntitiesWithSubList by ids") {
            dao.getEntitiesWithSubList(entityIds).map { it.toEntityWithSubEntities() }
        }

    override suspend fun getEntitiesWithSubList(type: DnDEntityTypes) =
        runLogging("getEntitiesWithSubList by type") {
            dao.getEntitiesWithSubList(type).map { it.toEntityWithSubEntities() }
        }

    override suspend fun getEntitiesWithSubList(
        vararg types: DnDEntityTypes
    ): Result<Map<DnDEntityTypes, List<DnDEntityWithSubEntities>>> =
        runLogging("getEntitiesWithSubList by types") {
            dao.getEntitiesWithSubList(*types)
                .map { it.toEntityWithSubEntities() }
                .groupBy { it.type }
        }
}