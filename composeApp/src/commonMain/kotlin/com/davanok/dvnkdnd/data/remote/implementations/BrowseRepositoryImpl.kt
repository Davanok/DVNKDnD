package com.davanok.dvnkdnd.data.remote.implementations

import com.davanok.dvnkdnd.core.InternetConnectionException
import com.davanok.dvnkdnd.core.PagedResult
import com.davanok.dvnkdnd.core.utils.runLogging
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.rpc
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.uuid.Uuid

class BrowseRepositoryImpl(
    private val postgrest: Postgrest,
    private val storage: Storage,
) : BrowseRepository {
    private fun <T> Result<T>.handleFailure() = recoverCatching { exception ->
        throw InternetConnectionException(exception.message, exception.cause)
    }

    @Serializable
    private data class LoadEntitiesWithSubPagedParams(
        @SerialName("p_entity_type")
        val entityType: DnDEntityTypes,
        @SerialName("p_limit")
        val limit: Int,
        @SerialName("p_offset") val offset: Int,
        @SerialName("p_search") val search: String?
    )

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

    override suspend fun loadEntitiesWithSubPaged(
        entityType: DnDEntityTypes,
        page: Int,
        pageSize: Int,
        searchQuery: String?
    ): Result<PagedResult<DnDEntityWithSubEntities>> =
        runLogging("loadEntitiesWithSubPaged") {
            val offset = (page * pageSize)

            val fetched = postgrest.rpc(
                "get_entity_min_with_sub_entities",
                LoadEntitiesWithSubPagedParams(
                    entityType = entityType,
                    limit = pageSize + 1,
                    offset = offset,
                    search = searchQuery
                )
            ).decodeList<DnDEntityWithSubEntities>()

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
