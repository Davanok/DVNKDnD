package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.util.runLogging
import com.davanok.dvnkdnd.data.repositories.FullEntitiesRepository
import com.davanok.dvnkdnd.database.daos.entities.FullEntitiesDao
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
            dao.getFullEntities(entityIds).fastMap { it.toDnDFullEntity() }
        }


    override suspend fun insertFullEntity(fullEntity: DnDFullEntity): Result<Unit> =
        runLogging("insertFullEntity") {
            dao.insertFullEntity(fullEntity)
        }

    override suspend fun insertFullEntities(fullEntities: List<DnDFullEntity>): Result<Unit> =
        runLogging("insertFullEntities") {
            fullEntities.partition { it.parentId == null }.let { (withoutParent, withParent) ->
                withoutParent.fastForEach { insertFullEntity(it).getOrThrow() }
                withParent.fastForEach { insertFullEntity(it).getOrThrow() }
            }
            // false is upper then true
        }
}