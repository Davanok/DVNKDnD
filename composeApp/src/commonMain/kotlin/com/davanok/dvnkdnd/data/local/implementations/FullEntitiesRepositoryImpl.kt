package com.davanok.dvnkdnd.data.local.implementations

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.data.local.db.daos.entities.FullEntitiesDao
import com.davanok.dvnkdnd.data.local.mappers.entities.toDnDFullEntity
import com.davanok.dvnkdnd.domain.repositories.local.FullEntitiesRepository
import kotlin.uuid.Uuid

class FullEntitiesRepositoryImpl(
    private val dao: FullEntitiesDao
) : FullEntitiesRepository {
    override suspend fun getFullEntity(entityId: Uuid): Result<DnDFullEntity?> =
        runLogging("getFullEntity") {
            dao.getFullEntity(entityId)?.toDnDFullEntity()
        }

    override suspend fun getFullEntities(entityIds: List<Uuid>): Result<List<DnDFullEntity>> =
        runLogging("getFullEntities") {
            dao.getFullEntities(entityIds).map { it.toDnDFullEntity() }
        }


    override suspend fun insertFullEntity(fullEntity: DnDFullEntity): Result<Unit> =
        runLogging("insertFullEntity") {
            dao.insertFullEntity(fullEntity)
        }

    override suspend fun insertFullEntities(fullEntities: List<DnDFullEntity>): Result<Unit> =
        runLogging("insertFullEntities") {
            fullEntities.partition { it.entity.parentId == null }.let { (withoutParent, withParent) ->
                withoutParent.forEach { insertFullEntity(it).getOrThrow() }
                withParent.forEach { insertFullEntity(it).getOrThrow() }
            }
            // false is upper then true
        }
}