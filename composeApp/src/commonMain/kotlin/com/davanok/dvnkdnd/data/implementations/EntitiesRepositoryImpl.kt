package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.database.daos.entities.BaseEntityDao
import io.github.aakira.napier.Napier
import kotlin.uuid.Uuid

class EntitiesRepositoryImpl(
    private val dao: BaseEntityDao,
) : EntitiesRepository {
    override suspend fun getExistingEntities(entityIds: List<Uuid>): Result<List<Uuid>> =
        runCatching {
            Napier.d { "getExistingEntities: entityIds: $entityIds" }
            dao.getExistingEntities(entityIds)
        }.onFailure {
            Napier.e("Error in getExistingEntities", it)
        }

    override suspend fun getEntitiesWithSubList(entityIds: List<Uuid>) = runCatching {
        Napier.d { "getEntitiesWithSubList: entityIds: $entityIds" }
        dao.getEntitiesWithSubList(entityIds).fastMap { it.toEntityWithSubEntities() }
    }.onFailure {
        Napier.e("Error in getEntitiesWithSubList", it)
    }

    override suspend fun getEntitiesWithSubList(type: DnDEntityTypes) = runCatching {
        Napier.d { "getEntitiesWithSubList: type: ${type.name}" }
        dao.getEntitiesWithSubList(type).fastMap { it.toEntityWithSubEntities() }
    }.onFailure {
        Napier.e("Error in getEntitiesWithSubList", it)
    }

    override suspend fun getEntitiesWithSubList(
        vararg types: DnDEntityTypes
    ): Result<Map<DnDEntityTypes, List<DnDEntityWithSubEntities>>> = runCatching {
        Napier.d { "getEntitiesWithSubList: types: $types" }
        dao.getEntitiesWithSubList(*types)
            .fastMap { it.toEntityWithSubEntities() }
            .groupBy { it.type }
    }.onFailure {
        Napier.e("Error in getEntitiesWithSubList", it)
    }
}