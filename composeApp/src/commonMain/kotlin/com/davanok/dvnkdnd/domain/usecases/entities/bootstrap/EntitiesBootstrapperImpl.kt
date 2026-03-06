package com.davanok.dvnkdnd.domain.usecases.entities.bootstrap

import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.domain.repositories.local.EntitiesRepository
import com.davanok.dvnkdnd.domain.repositories.local.FullEntitiesRepository
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository
import dev.zacsweers.metro.AppScope
import dev.zacsweers.metro.ContributesBinding
import dev.zacsweers.metro.Inject
import dev.zacsweers.metro.SingleIn
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlin.uuid.Uuid

@Inject
@SingleIn(AppScope::class)
@ContributesBinding(scope = AppScope::class)
class EntitiesBootstrapperImpl(
    private val browseRepository: BrowseRepository,
    private val entitiesRepository: EntitiesRepository,
    private val fullEntitiesRepository: FullEntitiesRepository
) : EntitiesBootstrapper {
    override fun checkAndLoadEntities(entitiesIds: List<Uuid>): Flow<EntitiesBootstrapEvent> =
        flow {
            emit(EntitiesBootstrapEvent.Started)
            val existingEntities = entitiesRepository
                .getExistingEntities(entitiesIds)
                .getOrThrow()

            val notExistingEntities = entitiesIds.subtract(existingEntities.toSet())

            emit(
                EntitiesBootstrapEvent.LocalChecked(
                    existingEntities.size,
                    notExistingEntities.size
                )
            )

            if (notExistingEntities.isEmpty()) {
                emit(EntitiesBootstrapEvent.Finished)
                return@flow
            }

            val entities = browseRepository
                .loadEntitiesFullInfo(notExistingEntities.toList())
                .getOrThrow()
            emit(EntitiesBootstrapEvent.RemoteLoaded(entities.size))

            fullEntitiesRepository.insertFullEntities(entities)
                .getOrThrow()
            emit(EntitiesBootstrapEvent.Saved)

            emit(EntitiesBootstrapEvent.Finished)
        }

    override suspend fun checkAndLoadEntity(entityId: Uuid): Result<Boolean> =
        runLogging("checkAndLoadEntity (entityId: $entityId)") {
            if (entitiesRepository.getExistsEntity(entityId).getOrThrow())
                return@runLogging false

            val entity = browseRepository.loadEntityFullInfo(entityId).getOrThrow()
            requireNotNull(entity) { "entity from external storage not found" }

            fullEntitiesRepository.insertFullEntity(entity).getOrThrow()

            true
        }
}