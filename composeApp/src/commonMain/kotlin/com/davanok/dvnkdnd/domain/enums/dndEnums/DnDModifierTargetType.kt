package com.davanok.dvnkdnd.domain.enums.dndEnums

import com.davanok.dvnkdnd.core.UiEntry
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.modifier_value_target_attribute
import dvnkdnd.composeapp.generated.resources.modifier_value_target_carry_weight
import dvnkdnd.composeapp.generated.resources.modifier_value_target_derived_stat
import dvnkdnd.composeapp.generated.resources.modifier_value_target_health
import dvnkdnd.composeapp.generated.resources.modifier_value_target_saving_throw
import dvnkdnd.composeapp.generated.resources.modifier_value_target_skill
import dvnkdnd.composeapp.generated.resources.modifier_value_target_speed
import dvnkdnd.composeapp.generated.resources.modifier_value_target_spell_attack
import dvnkdnd.composeapp.generated.resources.modifier_value_target_spell_dc
import dvnkdnd.composeapp.generated.resources.roll_modifier_target_attack
import dvnkdnd.composeapp.generated.resources.roll_modifier_target_death_save
import dvnkdnd.composeapp.generated.resources.roll_modifier_target_saving_throw
import dvnkdnd.composeapp.generated.resources.roll_modifier_target_skill_check
import org.jetbrains.compose.resources.StringResource
import kotlin.enums.EnumEntries

enum class ModifierValueTarget(override val stringRes: StringResource): UiEntry {
    ATTRIBUTE       (Res.string.modifier_value_target_attribute),       // Target Key: "STRENGTH", "DEXTERITY"
    SAVING_THROW    (Res.string.modifier_value_target_saving_throw),    // Target Key: "STRENGTH", "DEXTERITY"
    SKILL           (Res.string.modifier_value_target_skill),           // Target Key: "ATHLETICS", "STEALTH"
    DERIVED_STAT    (Res.string.modifier_value_target_derived_stat),    // Target Key: "ARMOR_CLASS", "INITIATIVE", "PASSIVE_PERCEPTION"
    SPEED           (Res.string.modifier_value_target_speed),           // Target Key: "WALK", "FLY", "SWIM", "CLIMB", null for all
    SPELL_ATTACK    (Res.string.modifier_value_target_spell_attack),    // Target Key: "INTELLECT", "WISDOM", null for all
    SPELL_DC        (Res.string.modifier_value_target_spell_dc),        // Target Key: "INTELLECT", "WISDOM", null for all
    CARRY_WEIGHT    (Res.string.modifier_value_target_carry_weight),    // Target Key is null
    HEALTH          (Res.string.modifier_value_target_health)           // Target Key: "MAX", "CURRENT"
}

enum class ModifierRollTarget(override val stringRes: StringResource): UiEntry {
    ATTACK_ROLL     (Res.string.roll_modifier_target_attack),                 // Key: "MELEE", "SPELL", "RANGED"
    SAVING_THROW    (Res.string.roll_modifier_target_saving_throw),           // Key: "STR", "DEX", "ALL"
    SKILL_CHECK     (Res.string.roll_modifier_target_skill_check),            // Key: "ATHLETICS", "ALL"
    DEATH_SAVE      (Res.string.roll_modifier_target_death_save)
}

fun ModifierValueTarget.targetKeys(): EnumEntries<*>? = when(this) {
    ModifierValueTarget.ATTRIBUTE -> Attributes.entries
    ModifierValueTarget.SAVING_THROW -> Attributes.entries
    ModifierValueTarget.SKILL -> Skills.entries
    ModifierValueTarget.DERIVED_STAT -> DnDModifierDerivedValuesTargets.entries
    ModifierValueTarget.SPEED -> CharacterMovementType.entries
    ModifierValueTarget.SPELL_ATTACK -> null
    ModifierValueTarget.SPELL_DC -> null
    ModifierValueTarget.CARRY_WEIGHT -> null
    ModifierValueTarget.HEALTH -> DnDModifierHealthTargets.entries
}
fun ModifierRollTarget.targetKeys(): EnumEntries<*>? = when(this) {
    ModifierRollTarget.ATTACK_ROLL -> RollModifierAttackTargets.entries
    ModifierRollTarget.SAVING_THROW -> Attributes.entries
    ModifierRollTarget.SKILL_CHECK -> Skills.entries
    ModifierRollTarget.DEATH_SAVE -> null
}