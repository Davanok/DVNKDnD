package com.davanok.dvnkdnd.domain.entities.dndModifiers

import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageInteractionType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierRollTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.RollOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Serializable
data class ModifiersGroup(
    val id: Uuid,

    val name: String,
    val description: String,

    // LOGIC FOR CHOICES
    // If > 0, the user must select this many modifiers from this feature.
    // If 0, all modifiers in this feature are automatically active.
    @SerialName("selection_limit")
    val selectionLimit: Int = 0,

    val modifiers: List<DnDModifier>
)

@Serializable
sealed interface DnDModifier {
    val id: Uuid
    val condition: String?
}


@Serializable
@SerialName("value") // Optional: customizes the "type" field in JSON
data class DnDValueModifier(
    override val id: Uuid,
    val priority: Int,
    @SerialName("target_scope")
    val targetScope: ModifierValueTarget,
    @SerialName("target_key")
    val targetKey: String?,
    val operation: ValueOperation,
    @SerialName("source_type")
    val sourceType: ValueSourceType,
    @SerialName("source_key")
    val sourceKey: String?,
    // Dynamic Math: value = (Source * Multiplier) + Flat
    val multiplier: Double = 1.0,
    @SerialName("flat_value")
    val flatValue: Int = 0,
    override val condition: String?
): DnDModifier

@Serializable
@SerialName("roll")
data class DnDRollModifier(
    override val id: Uuid,
    @SerialName("target_scope")
    val targetScope: ModifierRollTarget,
    @SerialName("target_key")
    val targetKey: String?,
    val operation: RollOperation,
    override val condition: String?
): DnDModifier

@Serializable
@SerialName("damage")
data class DnDDamageModifier(
    override val id: Uuid,
    @SerialName("damage_type")
    val damageType: DamageTypes,
    val interaction: DamageInteractionType,
    override val condition: String?
): DnDModifier