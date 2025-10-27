package com.davanok.dvnkdnd.data.model.entities.dndModifiers

import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.data.model.util.enumValueOfOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Serializable
data class DnDModifiersGroup(
    val id: Uuid = Uuid.random(),

    val target: DnDModifierTargetType,
    val operation: DnDModifierOperation,
    @SerialName("value_source")
    val valueSource: DnDModifierValueSource,
    val value: Double,

    val name: String,
    val description: String?,
    @SerialName("selection_limit")
    val selectionLimit: Int,
    val priority: Int,

    @SerialName("clamp_min") val clampMin: Int? = null,
    @SerialName("clamp_max") val clampMax: Int? = null,

    @SerialName("min_base_value") val minBaseValue: Int? = null,
    @SerialName("max_base_value") val maxBaseValue: Int? = null,

    val modifiers: List<DnDModifier>
)

@Serializable
data class DnDModifier(
    val id: Uuid,
    val selectable: Boolean,
//    val value: Double,
    val target: String
) {
    inline fun <reified E: Enum<E>>targetAs(): E? = enumValueOfOrNull<E>(target)
}