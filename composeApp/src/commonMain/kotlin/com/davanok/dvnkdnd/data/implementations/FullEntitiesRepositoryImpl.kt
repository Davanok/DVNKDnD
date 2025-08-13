package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.repositories.FullEntitiesRepository
import com.davanok.dvnkdnd.database.daos.entities.FullEntitiesDao
import io.github.aakira.napier.Napier
import kotlin.uuid.Uuid

class FullEntitiesRepositoryImpl(
    private val dao: FullEntitiesDao
) : FullEntitiesRepository {
    override suspend fun getFullEntity(entityId: Uuid): Result<DnDFullEntity?> = runCatching {
        Napier.d { "getFullEntity entityId: $entityId" }
        dao.getFullEntity(entityId)?.toDnDFullEntity()
    }.onFailure { Napier.e("getFullEntity failed for entityId: $entityId", it) }

    override suspend fun getFullEntities(entityIds: List<Uuid>): Result<List<DnDFullEntity>> =
        runCatching {
            Napier.d { "getFullEntities entityIds count: ${entityIds.size}" }
            dao.getFullEntities(entityIds).fastMap { it.toDnDFullEntity() }
        }.onFailure { Napier.e("getFullEntities failed", it) }


    override suspend fun insertFullEntity(fullEntity: DnDFullEntity): Result<Unit> = runCatching {
        Napier.d { "insertFullEntity fullEntity id: ${fullEntity.id}" }
        dao.insertFullEntity(fullEntity)
    }.onFailure { Napier.e("insertFullEntity failed for id: ${fullEntity.id}", it) }

    override suspend fun insertFullEntities(fullEntities: List<DnDFullEntity>): Result<Unit> =
        runCatching {
            Napier.d { "insertFullEntities fullEntities count: ${fullEntities.size}" }
            fullEntities.partition { it.parentId == null }.let { (withoutParent, withParent) ->
                withoutParent.fastForEach { insertFullEntity(it).getOrThrow() }
                withParent.fastForEach { insertFullEntity(it).getOrThrow() }
            }
            // false is upper then true
        }.onFailure { Napier.e("insertFullEntities failed", it) }
}