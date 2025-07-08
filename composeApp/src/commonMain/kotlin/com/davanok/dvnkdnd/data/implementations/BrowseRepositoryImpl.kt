package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.DnDFullEntity
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
    override suspend fun loadEntityFullInfo(entityId: Uuid): DnDFullEntity {
        val result: DnDFullEntity =
            postgrest.from("base_entities").select(Columns.raw(FULL_ENTITY_REQUEST)) {
                filter { eq("id", entityId) }
                single()
            }.decodeSingle()

        return result.copy(
            companionEntities = loadEntitiesFullInfo(result.getSubEntitiesIds())
        )
    }

    override suspend fun loadEntitiesFullInfo(entityIds: List<Uuid>): List<DnDFullEntity> {
        val result: List<DnDFullEntity> =
            postgrest.from("base_entities").select(Columns.raw(FULL_ENTITY_REQUEST)) {
                filter { isIn("id", entityIds) }
            }.decodeList()

        return result.fastMap { entity ->
            entity.copy(
                companionEntities = loadEntitiesFullInfo(entity.getSubEntitiesIds())
            )
        }
    }

    override suspend fun loadEntitiesWithSub(entityType: DnDEntityTypes): List<DnDEntityWithSubEntities> {
        return postgrest.from("base_entities").select(
            Columns.raw("*, sub_entities:base_entities(*)")
        ) {
            filter { DnDEntityWithSubEntities::type eq entityType.name }
        }.decodeList()
    }

    override suspend fun getValue(key: String): String {
        val result = postgrest.from("key_value").select(Columns.list("value")) {
            filter { eq("key", key) }
            single()
        }.data
        return Json.parseToJsonElement(result).jsonObject["value"]!!.jsonPrimitive.content
    }
}