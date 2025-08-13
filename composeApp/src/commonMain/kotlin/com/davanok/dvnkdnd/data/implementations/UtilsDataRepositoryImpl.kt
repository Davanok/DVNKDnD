package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.types.CheckingDataStates
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.data.repositories.FullEntitiesRepository
import com.davanok.dvnkdnd.data.repositories.UtilsDataRepository
import io.github.aakira.napier.Napier
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.uuid.Uuid

class UtilsDataRepositoryImpl(
    private val browseRepository: BrowseRepository,
    private val entitiesRepository: EntitiesRepository,
    private val fullEntitiesRepository: FullEntitiesRepository
) : UtilsDataRepository {
    override fun checkAndLoadEntities(entitiesIds: List<Uuid>): Flow<CheckingDataStates> =
        flow {
            Napier.d { "checkAndLoadEntities called with entitiesIds: $entitiesIds" }
            emit(CheckingDataStates.LOAD_FROM_DATABASE)
            val existingEntities = runCatching {
                Napier.d { "Getting existing entities for ids: $entitiesIds" }
                entitiesRepository.getExistingEntities(entitiesIds).getOrThrow()
            }.onFailure {
                Napier.e("Error getting existing entities", it)
            }.getOrThrow()

            emit(CheckingDataStates.CHECKING)
            val notExistingEntities = entitiesIds.subtract(existingEntities)

            if (notExistingEntities.isEmpty()) {
                emit(CheckingDataStates.FINISH)
                return@flow
            }

            emit(CheckingDataStates.LOADING_DATA)
            val entities = runCatching {
                Napier.d { "Loading full info for entities: $notExistingEntities" }
                browseRepository.loadEntitiesFullInfo(notExistingEntities.toList()).getOrThrow()
            }.onFailure {
                Napier.e("Error loading full info for entities", it)
            }.getOrThrow()

            emit(CheckingDataStates.UPDATING)
            runCatching {
                Napier.d { "Inserting full entities: $entities" }
                fullEntitiesRepository.insertFullEntities(entities)
            }.onFailure {
                Napier.e("Error inserting full entities", it)
            }.getOrThrow()
            emit(CheckingDataStates.FINISH)
        }
}