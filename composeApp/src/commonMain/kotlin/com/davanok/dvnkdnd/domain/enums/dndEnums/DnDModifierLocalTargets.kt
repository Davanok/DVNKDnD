package com.davanok.dvnkdnd.domain.enums.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.modifier_derived_value_target_armor_class
import dvnkdnd.composeapp.generated.resources.modifier_derived_value_target_initiative
import dvnkdnd.composeapp.generated.resources.modifier_derived_value_target_passive_perception
import dvnkdnd.composeapp.generated.resources.modifier_health_value_target_current
import dvnkdnd.composeapp.generated.resources.modifier_health_value_target_max
import dvnkdnd.composeapp.generated.resources.roll_modifier_attack_target_melee
import dvnkdnd.composeapp.generated.resources.roll_modifier_attack_target_ranged
import dvnkdnd.composeapp.generated.resources.roll_modifier_attack_target_spell
import org.jetbrains.compose.resources.StringResource

enum class DnDModifierHealthTargets(val stringRes: StringResource) {
    CURRENT(Res.string.modifier_health_value_target_current),
    MAX(Res.string.modifier_health_value_target_max)
}

enum class DnDModifierDerivedValuesTargets(val stringRes: StringResource) {
    ARMOR_CLASS(Res.string.modifier_derived_value_target_armor_class),
    INITIATIVE(Res.string.modifier_derived_value_target_initiative),
    PASSIVE_PERCEPTION(Res.string.modifier_derived_value_target_passive_perception)
}

enum class RollModifierAttackTargets(val stringRes: StringResource) {
    MELEE   (Res.string.roll_modifier_attack_target_melee),
    SPELL   (Res.string.roll_modifier_attack_target_spell),
    RANGED  (Res.string.roll_modifier_attack_target_ranged)
}