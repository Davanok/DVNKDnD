package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import com.davanok.dvnkdnd.data.repositories.CheckingDataStates
import com.davanok.dvnkdnd.data.repositories.EntitiesRepository
import com.davanok.dvnkdnd.data.repositories.UtilsDataRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlin.uuid.Uuid

class UtilsDataRepositoryImpl(
    private val browseRepository: BrowseRepository,
    private val entitiesRepository: EntitiesRepository
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
            entitiesRepository.insertFullEntities(entities)
            emit(CheckingDataStates.FINISH)
        }.catch {
            emit(CheckingDataStates.ERROR)
        }
}