package com.davanok.dvnkdnd.data.local.implementations

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.data.local.db.daos.entities.FullEntitiesDao
import com.davanok.dvnkdnd.data.local.mappers.entities.toDnDFullEntity
import com.davanok.dvnkdnd.domain.repositories.local.FullEntitiesRepository
import dev.zacsweers.metro.Inject
import kotlin.uuid.Uuid

@Inject
class FullEntitiesRepositoryImpl(
    private val dao: FullEntitiesDao
) : FullEntitiesRepository {
    override suspend fun getFullEntity(entityId: Uuid): Result<DnDFullEntity?> =
        getFullEntityRecursive(entityId, currentDepth = 0)

    override suspend fun getFullEntities(entityIds: List<Uuid>): Result<List<DnDFullEntity>> =
        getFullEntitiesRecursive(entityIds, currentDepth = 0)

    private suspend fun getFullEntityRecursive(entityId: Uuid, currentDepth: Int): Result<DnDFullEntity?> =
        runLogging("getFullEntityWithCompanion") {
            // 1. Fetch the root entity
            val entity = dao.getFullEntity(entityId)?.toDnDFullEntity() ?: return@runLogging null

            // 2. Check depth limit
            if (currentDepth >= MAX_RECURSION_DEPTH) {
                return@runLogging entity
            }

            // 3. Get companion IDs
            val companionIds = entity.getCompanionEntitiesIds()

            // 4. Optimization: Only recurse if there are actual companions to fetch
            if (companionIds.isEmpty()) {
                entity
            } else {
                // Delegate to the list function to handle batch fetching of companions
                val companions = getFullEntitiesRecursive(companionIds, currentDepth + 1).getOrThrow()
                entity.copy(companionEntities = companions)
            }
        }

    private suspend fun getFullEntitiesRecursive(entityIds: List<Uuid>, currentDepth: Int): Result<List<DnDFullEntity>> =
        runLogging("getFullEntitiesWithCompanion") {
            // 1. Batch fetch current level
            val entities = dao.getFullEntities(entityIds).map { it.toDnDFullEntity() }

            // 2. Stop recursion if limit reached or no entities found
            if (currentDepth >= MAX_RECURSION_DEPTH || entities.isEmpty()) {
                throw IllegalStateException("getFullEntitiesRecursive max depth exceeded")
            }

            // 3. Aggregate ALL companion IDs for this entire level (Batching Optimization)
            val allCompanionIds = entities.flatMap { it.getCompanionEntitiesIds() }.distinct()

            // 4. If no companions exist at this level, return early
            if (allCompanionIds.isEmpty()) {
                return@runLogging entities
            }

            // 5. Batch fetch the next level of companions
            val nextLevelEntities = getFullEntitiesRecursive(allCompanionIds, currentDepth + 1).getOrThrow()

            // 6. Create a lookup map for faster re-assembly
            val companionMap = nextLevelEntities.associateBy { it.entity.id }

            // 7. Stitch companions back to their parent entities
            entities.map { entity ->
                val neededIds = entity.getCompanionEntitiesIds()
                if (neededIds.isEmpty()) {
                    entity
                } else {
                    entity.copy(
                        companionEntities = neededIds.mapNotNull { id -> companionMap[id] }
                    )
                }
            }
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

    companion object {
        private const val MAX_RECURSION_DEPTH = 3
    }
}