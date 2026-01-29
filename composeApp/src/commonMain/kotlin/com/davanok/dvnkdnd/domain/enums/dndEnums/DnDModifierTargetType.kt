package com.davanok.dvnkdnd.domain.enums.dndEnums

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
import org.jetbrains.compose.resources.StringResource

enum class ModifierValueTarget(val stringRes: StringResource) {
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

enum class ModifierRollTarget {
    ATTACK_ROLL,            // Key: "ALL", "MELEE", "SPELL", "RANGED"
    SAVING_THROW,           // Key: "STR", "DEX", "ALL"
    SKILL_CHECK,            // Key: "ATHLETICS", "ALL"
    DEATH_SAVE
}