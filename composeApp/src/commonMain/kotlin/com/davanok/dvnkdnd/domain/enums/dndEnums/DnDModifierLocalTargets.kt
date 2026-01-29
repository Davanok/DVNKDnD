package com.davanok.dvnkdnd.domain.enums.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.modifier_derived_value_target_armor_class
import dvnkdnd.composeapp.generated.resources.modifier_derived_value_target_initiative
import dvnkdnd.composeapp.generated.resources.modifier_derived_value_target_passive_perception
import dvnkdnd.composeapp.generated.resources.modifier_health_value_target_current
import dvnkdnd.composeapp.generated.resources.modifier_health_value_target_max
import dvnkdnd.composeapp.generated.resources.modifier_speed_value_target_climb
import dvnkdnd.composeapp.generated.resources.modifier_speed_value_target_fly
import dvnkdnd.composeapp.generated.resources.modifier_speed_value_target_swim
import dvnkdnd.composeapp.generated.resources.modifier_speed_value_target_walk
import org.jetbrains.compose.resources.StringResource

enum class DnDModifierHealthTargets(val stringRes: StringResource) {
    CURRENT(Res.string.modifier_health_value_target_current),
    MAX(Res.string.modifier_health_value_target_max)
}

enum class DnDModifierDerivedStatTargets(val stringRes: StringResource) {
    ARMOR_CLASS(Res.string.modifier_derived_value_target_armor_class),
    INITIATIVE(Res.string.modifier_derived_value_target_initiative),
    PASSIVE_PERCEPTION(Res.string.modifier_derived_value_target_passive_perception)
}

enum class DnDModifierSpeedTargets(val stringRes: StringResource) {
    WALK(Res.string.modifier_speed_value_target_walk),
    FLY(Res.string.modifier_speed_value_target_fly),
    SWIM(Res.string.modifier_speed_value_target_swim),
    CLIMB(Res.string.modifier_speed_value_target_climb)
}