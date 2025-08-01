package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.types.CheckingDataStates
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.data.repositories.FullEntitiesRepository
import com.davanok.dvnkdnd.data.repositories.UtilsDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.uuid.Uuid

class UtilsDataRepositoryImpl(
    private val browseRepository: BrowseRepository,
    private val entitiesRepository: EntitiesRepository,
    private val fullEntitiesRepository: FullEntitiesRepository
): UtilsDataRepository {
    override fun checkAndLoadEntities(entitiesIds: List<Uuid>): Flow<CheckingDataStates> =
        flow {
            emit(CheckingDataStates.LOAD_FROM_DATABASE)
            val existingEntities = entitiesRepository.getExistingEntities(entitiesIds).getOrThrow()

            emit(CheckingDataStates.CHECKING)
            val notExistingEntities = entitiesIds.subtract(existingEntities)

            if (notExistingEntities.isEmpty()) {
                emit(CheckingDataStates.FINISH)
                return@flow
            }

            emit(CheckingDataStates.LOADING_DATA)
            val entities = browseRepository.loadEntitiesFullInfo(notExistingEntities.toList()).getOrThrow()

            emit(CheckingDataStates.UPDATING)
            fullEntitiesRepository.insertFullEntities(entities)
            emit(CheckingDataStates.FINISH)
        }
}