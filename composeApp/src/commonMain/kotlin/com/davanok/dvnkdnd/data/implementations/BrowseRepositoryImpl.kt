package com.davanok.dvnkdnd.data.implementations

import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityFullInfo
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.uuid.Uuid

private const val FULL_ENTITY_REQUEST = """
*,
modifiers:entity_modifiers(*),
skills:entity_skills(*),
saving_throws:entity_saving_throws(*),
proficiencies:entity_proficiencies(*),
abilities:entity_abilities(*),
selection_limits:entity_selection_limits(*)
"""

class BrowseRepositoryImpl(
    private val postgrest: Postgrest,
    private val storage: Storage,
) : BrowseRepository {
    override suspend fun loadEntityFullInfo(entityId: Uuid): DnDEntityFullInfo {
        return postgrest.from("base_entities").select(Columns.raw(FULL_ENTITY_REQUEST)) {
            filter { DnDEntityFullInfo::id eq "id" }
            single()
        }.decodeSingle()
    }

    override suspend fun loadEntitiesFullInfo(entityIds: List<Uuid>): List<DnDEntityFullInfo> {
        val result = postgrest.from("base_entities").select(Columns.raw(FULL_ENTITY_REQUEST)) {
            filter { DnDEntityFullInfo::id isIn entityIds }
        }
        return result.decodeList()
    }

    override suspend fun loadEntitiesWithSub(entityType: DnDEntityTypes): List<DnDEntityWithSubEntities> {
        val result = postgrest.from("base_entities").select(
            Columns.raw("*, sub_entities:base_entities(*)")
        ) {
            filter { DnDEntityWithSubEntities::type eq entityType.name }
        }.decodeList<DnDEntityWithSubEntities>()
        return result
    }

    override suspend fun getValue(key: String): String {
        val result = postgrest.from("key_value").select(Columns.list("value")) {
            filter { eq("key", key) }
        }.data
        return Json.parseToJsonElement(result).jsonArray.first().jsonObject["value"]!!.jsonPrimitive.content
    }
}