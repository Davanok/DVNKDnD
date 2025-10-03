package com.davanok.dvnkdnd.data.model.entities.character

import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
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

    val name: String,
    val description: String?,
    @SerialName("selection_limit")
    val selectionLimit: Int,
    val priority: Int,

    val target: String,
    val value: Double
)
