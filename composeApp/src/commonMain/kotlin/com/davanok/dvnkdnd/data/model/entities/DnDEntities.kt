@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.data.model.entities

import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiencies
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class DnDEntityMin(
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
)
@Serializable
data class DnDEntityWithSubEntities (
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
    val subEntities: List<DnDEntityMin>
) {
    fun asDnDEntityMin() = DnDEntityMin(id, type, name, source)
}
@Serializable
data class DnDEntityFullInfo(
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val description: String,
    val source: String,

    val modifiers: List<EntityModifier>,
    val skills: List<EntitySkill>,
    val savingThrows: List<EntitySavingThrow>,
    val proficiencies: List<EntityProficiencies>,
    val abilities: List<EntityAbility>,

    val selectionLimits: EntitySelectionLimits,

    val subEntities: List<DnDEntityFullInfo>
)
