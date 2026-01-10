package com.davanok.dvnkdnd.domain.enums.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.damage_condition_critical_hit
import dvnkdnd.composeapp.generated.resources.damage_condition_off_hand
import dvnkdnd.composeapp.generated.resources.damage_condition_sneak_attack
import dvnkdnd.composeapp.generated.resources.damage_condition_specific_property
import dvnkdnd.composeapp.generated.resources.damage_condition_target_type
import dvnkdnd.composeapp.generated.resources.damage_condition_versatile
import org.jetbrains.compose.resources.StringResource

enum class DamageConditionType(val stringRes: StringResource) {
    CRITICAL_HIT(Res.string.damage_condition_critical_hit),
    TARGET_TYPE(Res.string.damage_condition_target_type),
    VERSATILE(Res.string.damage_condition_versatile),
    SNEAK_ATTACK(Res.string.damage_condition_sneak_attack),
    OFF_HAND(Res.string.damage_condition_off_hand),
    SPECIFIC_PROPERTY(Res.string.damage_condition_specific_property)
}