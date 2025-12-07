package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.core.utils.enumValueOfOrNull
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CustomModifier(
    val id: Uuid,

    @SerialName("target_global")
    val targetGlobal: DnDModifierTargetType,
    val operation: DnDModifierOperation,
    @SerialName("value_source")
    val valueSource: DnDModifierValueSource,
    @SerialName("value_source_target")
    val valueSourceTarget: String?,

    val name: String,
    val description: String?,
    @SerialName("selection_limit")
    val selectionLimit: Int,
    val priority: Int,

    val target: String,
    val value: Double
) {
    inline fun <reified E: Enum<E>>targetAs(): E? = enumValueOfOrNull<E>(target)
}
