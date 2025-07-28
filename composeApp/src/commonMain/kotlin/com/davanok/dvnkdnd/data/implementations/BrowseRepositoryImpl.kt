package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import io.github.jan.supabase.postgrest.Postgrest
import io.github.jan.supabase.postgrest.query.Columns
import io.github.jan.supabase.storage.Storage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.uuid.Uuid

private const val FULL_ENTITY_REQUEST = """
*,
modifier_bonuses:entity_modifier_bonuses!entity_modifiers_entity_id_fkey(*),
skills:entity_skills(*),
saving_throws:entity_saving_throws(*),
proficiencies:entity_proficiencies(*),
abilities:entity_abilities(*),
selection_limits:entity_selection_limits(*),
cls:classes(
    *,
    spells:class_spells(*),
    slots:class_spell_slots(*)
),
race:races(*),
background:backgrounds(*),
feat:feats(*),
ability:abilities!abilities_id_fkey(*),
proficiency:proficiencies!dnd_proficiencies_id_fkey(*),
spell:spells(
    *,
    area:spell_areas(*),
    attacks:spell_attacks(*)
),
item:items(
    *,
    properties:item_properties(
        *,
        property:properties(*)
    ),
    armor:armors(*),
    weapon:weapons(
        *,
        damages:weapon_damages(*)
    )
)
"""

class BrowseRepositoryImpl(
    private val postgrest: Postgrest,
    private val storage: Storage,
) : BrowseRepository {
    override suspend fun loadEntityFullInfo(entityId: Uuid): Result<DnDFullEntity> =
        runCatching {
            postgrest.from("base_entities").select(Columns.raw(FULL_ENTITY_REQUEST)) {
                filter { eq("id", entityId) }
                single()
            }.decodeSingle<DnDFullEntity>()
        }.mapCatching { entity ->
            val companionIds = entity.getSubEntitiesIds()
            if (companionIds.isNotEmpty()) {
                val companionEntities = loadEntitiesFullInfo(companionIds).getOrThrow()
                entity.copy(
                    companionEntities = companionEntities
                )
            }
            else entity
        }

    override suspend fun loadEntitiesFullInfo(entityIds: List<Uuid>): Result<List<DnDFullEntity>> =
        runCatching {
            postgrest.from("base_entities").select(Columns.raw(FULL_ENTITY_REQUEST)) {
                filter { isIn("id", entityIds) }
            }.decodeList<DnDFullEntity>()
        }.mapCatching { entities ->
            val companionIds = entities.fastFlatMap { it.getSubEntitiesIds() }
            if (companionIds.isNotEmpty()) {
                val companionEntities = loadEntitiesFullInfo(companionIds).getOrThrow()
                val groups = companionEntities.groupBy { it.parentId }
                entities.fastMap {
                    it.copy(
                        companionEntities = groups[it.id] ?: emptyList()
                    )
                }
            }
            else entities
        }

    override suspend fun loadEntitiesWithSub(entityType: DnDEntityTypes): Result<List<DnDEntityWithSubEntities>> =
        runCatching {
            postgrest.from("base_entities").select(
                Columns.raw("*, sub_entities:base_entities(*)")
            ) {
                filter { DnDEntityWithSubEntities::type eq entityType.name }
            }.decodeList()
        }

    override suspend fun getValue(key: String): Result<String> =
        runCatching {
            postgrest.from("key_value").select(Columns.list("value")) {
                filter { eq("key", key) }
                single()
            }.data
        }.mapCatching { raw ->
            Json.parseToJsonElement(raw).jsonObject["value"]!!.jsonPrimitive.content
        }
}