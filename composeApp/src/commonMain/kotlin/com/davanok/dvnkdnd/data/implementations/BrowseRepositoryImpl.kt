package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.types.PagedResult
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import io.github.aakira.napier.Napier
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
    override suspend fun loadEntityFullInfo(entityId: Uuid): Result<DnDFullEntity?> =
        runCatching {
            Napier.d { "loadEntityFullInfo: entityId: $entityId" }
            postgrest.rpc(
                "get_full_entity_with_companion",
                mapOf("entity_id" to entityId)
            ).decodeAsOrNull<DnDFullEntity>()
        }.onFailure {
            Napier.e("Error in loadEntityFullInfo", it)
        }

    override suspend fun loadEntitiesFullInfo(entityIds: List<Uuid>): Result<List<DnDFullEntity>> =
        runCatching {
            Napier.d { "loadEntitiesFullInfo: entityIds: $entityIds" }
            postgrest.rpc(
                "get_full_entities_with_companion",
                mapOf("entity_ids" to entityIds)
            ).decodeList<DnDFullEntity>()
        }.onFailure {
            Napier.e("Error in loadEntitiesFullInfo", it)
        }

    override suspend fun loadEntitiesWithSub(entityType: DnDEntityTypes): Result<List<DnDEntityWithSubEntities>> =
        runCatching {
            Napier.d { "loadEntitiesWithSub: entityType: $entityType" }
            postgrest.from("base_entities").select(
                Columns.raw("*, sub_entities:base_entities(*)")
            ) {
                filter { DnDEntityWithSubEntities::type eq entityType.name }
            }.decodeList<DnDEntityWithSubEntities>()
        }.onFailure {
            Napier.e("Error in loadEntitiesWithSub", it)
        }

    override suspend fun loadEntitiesWithSubPaged(
        entityType: DnDEntityTypes,
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): Result<PagedResult<DnDEntityWithSubEntities>> =
        runCatching {
            Napier.d { "loadEntitiesWithSubPaged: type=$entityType page=$page pageSize=$pageSize query=$searchQuery" }

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
        }.onFailure {
            Napier.e("Error in loadEntitiesWithSubPaged", it)
        }

    override suspend fun getPropertyValue(key: String): Result<String> =
        runCatching {
            Napier.d { "getPropertyValue: key: $key" }
            val raw = postgrest.rpc(
                "get_property",
                mapOf("field" to key)
            ).data
            Json.decodeFromString<String>(raw)
        }.onFailure {
            Napier.e("Error in getPropertyValue", it)
        }
}
