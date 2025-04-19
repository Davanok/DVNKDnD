package com.davanok.dvnkdnd.data.implementations

import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDFullEntity
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
modifiers:entity_modifiers!entity_modifiers_entity_id_fkey(*),
skills:entity_skills(*),
saving_throws:entity_saving_throws(*),
proficiencies:entity_proficiencies(
    *,
    proficiency:proficiencies!entity_proficiencies_proficiency_id_fkey(
        *,
        base:base_entities!dnd_proficiencies_id_fkey(*)
    )
),
abilities:entity_abilities(
    *,
    ability:abilities!entity_abilities_ability_id_fkey(
        *,
        base:base_entities!abilities_id_fkey(*)
    )
),
selection_limits:entity_selection_limits(*),
cls:classes(
    *,
    base:base_entities(*),
    spells:class_spells(*),
    slots:class_spell_slots(*)
),
race:races(
    *,
    base:base_entities(*),
    sizes:race_sizes(*)
),
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
    item_properties(
        *,
        item_property(*)
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
        val result: DnDFullEntity = postgrest.from("base_entities").select(Columns.raw(FULL_ENTITY_REQUEST)) {
            filter { eq("id", entityId) }
            single()
        }.decodeSingle()

        return result.copy(
            companionEntities = loadEntitiesFullInfo(
                result.abilities.fastMap { it.abilityId } + result.proficiencies.fastMap { it.proficiencyId }
            )
        )
    }

    override suspend fun loadEntitiesFullInfo(entityIds: List<Uuid>): List<DnDFullEntity> {
        val result: List<DnDFullEntity> = postgrest.from("base_entities").select(Columns.raw(FULL_ENTITY_REQUEST)) {
            filter { isIn("id", entityIds) }
        }.decodeList()

        return result.fastMap { entity ->
            entity.copy(
                companionEntities = loadEntitiesFullInfo(
                    entity.abilities.fastMap { it.abilityId } + entity.proficiencies.fastMap { it.proficiencyId }
                )
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
        }.decodeAs<String>()
        return Json.parseToJsonElement(result).jsonObject["value"]!!.jsonPrimitive.content
    }
}