package com.davanok.dvnkdnd.data.remote.implementations

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.core.InternetConnectionException
import com.davanok.dvnkdnd.core.PagedResult
import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.postgrest.query.Order
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class BrowseRepositoryImpl(
    private val postgrest: Postgrest,
    private val storage: Storage,
) : BrowseRepository {
    private fun <T>Result<T>.handleFailure() = recoverCatching { exception ->
        throw InternetConnectionException(exception.message, exception.cause)
    }

    override suspend fun loadEntityFullInfo(entityId: Uuid): Result<DnDFullEntity?> =
        runLogging("loadEntityFullInfo") {
            postgrest.rpc(
                "get_full_entity_with_companion",
                mapOf("entity_id" to entityId)
            ).decodeAsOrNull<DnDFullEntity>()
        }.handleFailure()

    override suspend fun loadEntitiesFullInfo(entityIds: List<Uuid>): Result<List<DnDFullEntity>> =
        runLogging("loadEntitiesFullInfo") {
            postgrest.rpc(
                "get_full_entities_with_companion",
                mapOf("entity_ids" to entityIds)
            ).decodeList<DnDFullEntity>()
        }.handleFailure()

    override suspend fun loadEntitiesWithSub(entityType: DnDEntityTypes): Result<List<DnDEntityWithSubEntities>> =
        runLogging("loadEntitiesWithSub") {
            postgrest.from("base_entities").select(
                Columns.raw("*, sub_entities:base_entities(*)")
            ) {
                filter { DnDEntityWithSubEntities::type eq entityType.name }
            }.decodeList<DnDEntityWithSubEntities>()
        }.handleFailure()

    override suspend fun loadEntitiesWithSubPaged(
        entityType: DnDEntityTypes,
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): Result<PagedResult<DnDEntityWithSubEntities>> =
        runLogging("loadEntitiesWithSubPaged") {
            val offset = (page * pageSize).toLong()

            val fetched = postgrest
                .from("base_entities")
                .select(Columns.raw("*, sub_entities:base_entities(*)")) {
                    filter { DnDEntityWithSubEntities::type eq entityType.name }

                    if (!searchQuery.isNullOrBlank()) {
                        val safe = searchQuery.replace("%", "\\%").replace("_", "\\_")
                        filter { DnDEntityWithSubEntities::name ilike "$safe%" }
                    }

                    order("id", Order.ASCENDING)
                    range(offset, offset + pageSize)
                }.decodeList<DnDEntityWithSubEntities>()

            val hasNext = fetched.size > pageSize
            val items = if (hasNext) fetched.subList(0, pageSize) else fetched
            val hasPrevious = page > 0

            PagedResult(
                items = items,
                page = page,
                pageSize = pageSize,
                hasNext = hasNext,
                hasPrevious = hasPrevious
            )
        }.handleFailure()

    override suspend fun getPropertyValue(key: String): Result<String> =
        runLogging("getPropertyValue") {
            val raw = postgrest.rpc(
                "get_property",
                mapOf("field" to key)
            ).data
            Json.decodeFromString<String>(raw)
        }.handleFailure()
}
