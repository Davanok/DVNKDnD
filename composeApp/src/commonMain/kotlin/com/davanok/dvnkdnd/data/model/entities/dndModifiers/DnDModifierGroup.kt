package com.davanok.dvnkdnd.data.model.entities.dndModifiers

import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierType
import com.davanok.dvnkdnd.data.model.dndEnums.DnDTargetType
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifiersGroup
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerialName
import kotlin.uuid.Uuid


@Serializable
data class DnDModifiersGroup(
    val id: Uuid,

    val type: DnDModifierType,
    val target: DnDTargetType,
    val operation: DnDModifierOperation,

    val name: String,
    val description: String?,
    @SerialName("selection_limit") val selectionLimit: Int,
    val priority: Int,

    @SerialName("clamp_min") val clampMin: Double?,
    @SerialName("clamp_max") val clampMax: Double?,

    @SerialName("min_base_value") val minBaseValue: Double?,
    @SerialName("max_base_value") val maxBaseValue: Double?,

    val modifiers: List<DnDModifier>
)
fun DnDModifiersGroup.toEntityModifiersGroup(entityId: Uuid) = EntityModifiersGroup(
    id = id,
    entityId = entityId,
    type = type,
    target = target,
    operation = operation,
    name = name,
    description = description,
    selectionLimit = selectionLimit,
    priority = priority,
    clampMax = clampMax,
    clampMin = clampMin,
    minBaseValue = minBaseValue,
    maxBaseValue = maxBaseValue
)

@Serializable
sealed interface DnDModifier {
    val id: Uuid
    val selectable: Boolean
    val value: Float
    val target: String
}

fun DnDModifier.toEntityModifier(groupId: Uuid) = EntityModifier(
    id = id,
    groupId = groupId,
    selectable = selectable,
    value = value,
    target = target
)

@Serializable
@SerialName("attribute")
data class DnDAttributeModifier(
    override val id: Uuid,
    override val selectable: Boolean,
    override val value: Float,
    val attribute: Attributes
) : DnDModifier {
    override val target: String
        get() = attribute.name
}
@Serializable
@SerialName("skill")
data class DnDSkillModifier(
    override val id: Uuid,
    override val selectable: Boolean,
    override val value: Float,
    val skill: Skills
) : DnDModifier {
    override val target: String
        get() = skill.name
}
@Serializable
@SerialName("generic")
data class DnDGenericModifier(
    override val id: Uuid,
    override val selectable: Boolean,
    override val value: Float,
    override val target: String
) : DnDModifier