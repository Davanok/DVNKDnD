package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
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
            postgrest.rpc(
                "get_full_entity_with_companion",
                mapOf("entity_id" to entityId)
            ).decodeSingleOrNull<DnDFullEntity>()
        }

    override suspend fun loadEntitiesFullInfo(entityIds: List<Uuid>): Result<List<DnDFullEntity>> =
        runCatching {
            postgrest.rpc(
                "get_full_entities_with_companion",
                mapOf("entity_ids" to entityIds)
            ).decodeList<DnDFullEntity>()
        }

    override suspend fun loadEntitiesWithSub(entityType: DnDEntityTypes): Result<List<DnDEntityWithSubEntities>> =
        runCatching {
            postgrest.from("base_entities").select(
                Columns.raw("*, sub_entities:base_entities(*)")
            ) {
                filter { DnDEntityWithSubEntities::type eq entityType.name }
            }.decodeList()
        }

    override suspend fun getPropertyValue(key: String): Result<String> =
        runCatching {
            val raw = postgrest.rpc(
                "get_property",
                mapOf("field" to key)
            ).data
            Json.decodeFromString<String>(raw)
        }
}