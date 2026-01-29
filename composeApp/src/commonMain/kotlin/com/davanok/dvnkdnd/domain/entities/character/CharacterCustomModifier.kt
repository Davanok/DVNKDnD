package com.davanok.dvnkdnd.domain.entities.character

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
sealed interface CharacterCustomModifier {
    val id: Uuid
    val name: String
    val description: String

    val condition: String?
}

@Serializable
data class CharacterCustomValueModifier(
    override val id: Uuid,
    override val name: String,
    override val description: String,

    val priority: Int,
    // What are we modifying? (e.g., ATTRIBUTE -> STR)
    @SerialName("target_scope")
    val targetScope: ModifierValueTarget,
    @SerialName("target_key")
    val targetKey: String?, // Enum name stored as string (e.g., "STR", "ATHLETICS")

    // How do we modify it? (ADD, SET)
    val operation: ValueOperation,

    // CALCULATION LOGIC: (Source * Multiplier) + Flat
    // Example: "Add Half Proficiency" -> Source=PB, Multiplier=0.5, Flat=0
    // Example: "Str +2" -> Source=FLAT, Multiplier=0, Flat=2
    @SerialName("source_type")
    val sourceType: ValueSourceType,
    @SerialName("source_key")
    val sourceKey: String?,         // If source is ATTRIBUTE, which one?

    val multiplier: Double = 1.0,   // Double allows for "Half Proficiency" logic
    @SerialName("flat_value")
    val flatValue: Int = 0,

    override val condition: String?
) : CharacterCustomModifier

@Serializable
data class CharacterCustomRollModifier(
    override val id: Uuid,
    override val name: String,
    override val description: String,

    @SerialName("target_scope")
    val targetScope: ModifierRollTarget,    // SAVING_THROW
    @SerialName("target_key")
    val targetKey: String?,                 // "DEXTERITY"

    val operation: RollOperation,           // ADVANTAGE

    override val condition: String?
) : CharacterCustomModifier

@Serializable
data class CharacterCustomDamageModifier(
    override val id: Uuid,
    override val name: String,
    override val description: String,

    @SerialName("damage_type")
    val damageType: DamageTypes,            // "FIRE", "SLASHING"
    val interaction: DamageInteractionType, // RESISTANCE, IMMUNITY

    override val condition: String?
) : CharacterCustomModifier

