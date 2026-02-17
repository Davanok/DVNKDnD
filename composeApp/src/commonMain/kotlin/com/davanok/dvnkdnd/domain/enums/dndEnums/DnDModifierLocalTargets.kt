package com.davanok.dvnkdnd.domain.enums.dndEnums

import com.davanok.dvnkdnd.core.UiEntry
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

enum class DnDModifierHealthTargets(override val stringRes: StringResource): UiEntry {
    CURRENT(Res.string.modifier_health_value_target_current),
    MAX(Res.string.modifier_health_value_target_max)
}

enum class DnDModifierDerivedValuesTargets(override val stringRes: StringResource): UiEntry {
    ARMOR_CLASS(Res.string.modifier_derived_value_target_armor_class),
    INITIATIVE(Res.string.modifier_derived_value_target_initiative),
    PASSIVE_PERCEPTION(Res.string.modifier_derived_value_target_passive_perception)
}

enum class RollModifierAttackTargets(override val stringRes: StringResource): UiEntry {
    MELEE   (Res.string.roll_modifier_attack_target_melee),
    SPELL   (Res.string.roll_modifier_attack_target_spell),
    RANGED  (Res.string.roll_modifier_attack_target_ranged)
}