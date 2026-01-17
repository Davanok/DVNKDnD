package com.davanok.dvnkdnd.data.usecase.implementations

import com.davanok.dvnkdnd.core.CheckingDataStates
import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.domain.repositories.local.EntitiesRepository
import com.davanok.dvnkdnd.domain.repositories.local.FullEntitiesRepository
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository
import com.davanok.dvnkdnd.domain.usecases.entities.EntitiesBootstrapper
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.uuid.Uuid

class EntitiesBootstrapperImpl(
    private val browseRepository: BrowseRepository,
    private val entitiesRepository: EntitiesRepository,
    private val fullEntitiesRepository: FullEntitiesRepository
) : EntitiesBootstrapper {
    override fun checkAndLoadEntities(entitiesIds: List<Uuid>): Flow<CheckingDataStates> =
        flow {
            Napier.d { "checkAndLoadEntities called with entitiesIds: $entitiesIds" }
            emit(CheckingDataStates.LOAD_FROM_DATABASE)
            Napier.d { "Getting existing entities for ids: $entitiesIds" }
            val existingEntities = entitiesRepository
                .getExistingEntities(entitiesIds)
                .onFailure {
                    Napier.e("Error getting existing entities", it)
                }
                .getOrThrow()

            emit(CheckingDataStates.CHECKING)
            val notExistingEntities = entitiesIds.subtract(existingEntities.toSet())

            if (notExistingEntities.isEmpty()) {
                emit(CheckingDataStates.FINISH)
                return@flow
            }

            emit(CheckingDataStates.LOADING_DATA)
            Napier.d { "Loading full info for entities: $notExistingEntities" }
            val entities = browseRepository
                .loadEntitiesFullInfo(notExistingEntities.toList())
                .onFailure {
                    Napier.e("Error loading full info for entities", it)
                }
                .getOrThrow()

            emit(CheckingDataStates.UPDATING)
            Napier.d { "Inserting full entities: $entities" }
            fullEntitiesRepository.insertFullEntities(entities)
                .onFailure {
                    Napier.e("Error inserting full entities", it)
                }
                .getOrThrow()
            emit(CheckingDataStates.FINISH)
        }

    override suspend fun checkAndLoadEntity(entityId: Uuid): Result<Unit> =
        runLogging("checkAndLoadEntity (entityId: $entityId)") {
            if (entitiesRepository.getExistsEntity(entityId).getOrThrow())
                return@runLogging

            val entity = browseRepository.loadEntityFullInfo(entityId).getOrThrow()
            requireNotNull(entity) { "entity from external storage not found" }

            fullEntitiesRepository.insertFullEntity(entity).getOrThrow()
        }
}