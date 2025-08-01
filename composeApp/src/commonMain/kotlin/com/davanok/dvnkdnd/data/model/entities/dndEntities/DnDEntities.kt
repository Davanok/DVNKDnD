package com.davanok.dvnkdnd.data.model.entities.dndEntities

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifierBonus
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSkill
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Immutable
@Serializable
data class DnDEntityMin(
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
)

@Immutable
@Serializable
data class DnDEntityWithSubEntities(
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
    @SerialName("sub_entities")
    val subEntities: List<DnDEntityMin>,
) {
    fun toDnDEntityMin() = DnDEntityMin(id, type, name, source)
}

@Immutable
data class DnDEntityWithModifiers(
    val entity: DnDEntityMin,
    val selectionLimit: Int?,
    val modifiers: List<DnDModifierBonus>,
)

@Immutable
data class DnDEntityWithSkills(
    val entity: DnDEntityMin,
    val selectionLimit: Int?,
    val skills: List<DnDSkill>,
)
