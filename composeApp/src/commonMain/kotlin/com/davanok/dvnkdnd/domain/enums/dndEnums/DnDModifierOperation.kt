package com.davanok.dvnkdnd.domain.enums.dndEnums

import com.davanok.dvnkdnd.core.UiEntry
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.damage_modifier_immunity
import dvnkdnd.composeapp.generated.resources.damage_modifier_interaction_immunity
import dvnkdnd.composeapp.generated.resources.damage_modifier_interaction_resistance
import dvnkdnd.composeapp.generated.resources.damage_modifier_interaction_vulnerability
import dvnkdnd.composeapp.generated.resources.damage_modifier_resistance
import dvnkdnd.composeapp.generated.resources.damage_modifier_vulnerability
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_advantage
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_crit_threshold_reduce
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_disadvantage
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_reroll
import dvnkdnd.composeapp.generated.resources.roll_operation_advantage
import dvnkdnd.composeapp.generated.resources.roll_operation_crit_threshold_reduce
import dvnkdnd.composeapp.generated.resources.roll_operation_disadvantage
import dvnkdnd.composeapp.generated.resources.roll_operation_reroll
import dvnkdnd.composeapp.generated.resources.value_operation_add
import dvnkdnd.composeapp.generated.resources.value_operation_set
import dvnkdnd.composeapp.generated.resources.value_operation_set_max
import dvnkdnd.composeapp.generated.resources.value_operation_set_min
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource

enum class ValueOperation(
    override val stringRes: StringResource,
    val iconRes: DrawableResource
) : UiEntry {
    ADD(
        Res.string.value_operation_add,
        Res.drawable.value_operation_add
    ),       // Standard bonuses (+1 Sword)
    SET(
        Res.string.value_operation_set,
        Res.drawable.value_operation_set
    ),       // Overrides (Gauntlets of Ogre Power set STR to 19)
    SET_MIN(
        Res.string.value_operation_set_min,
        Res.drawable.value_operation_set_min
    ),   // "Your AC cannot be bigger than 16"
    SET_MAX(
        Res.string.value_operation_set_max,
        Res.drawable.value_operation_set_max
    )    // "Your AC cannot be less than 16"
}

enum class RollOperation(
    override val stringRes: StringResource,
    val iconRes: DrawableResource
) : UiEntry {
    ADVANTAGE(
        Res.string.roll_modifier_operation_advantage,
        Res.drawable.roll_operation_advantage
    ),
    DISADVANTAGE(
        Res.string.roll_modifier_operation_disadvantage,
        Res.drawable.roll_operation_disadvantage
    ),
    REROLL(
        Res.string.roll_modifier_operation_reroll,
        Res.drawable.roll_operation_reroll
    ),                 // Halfling Luck
    CRIT_THRESHOLD_REDUCE(
        Res.string.roll_modifier_operation_crit_threshold_reduce,
        Res.drawable.roll_operation_crit_threshold_reduce
    )   // Fighter Champion (Crit on 19)
}

enum class DamageInteractionType(
    override val stringRes: StringResource,
    val iconRes: DrawableResource
) : UiEntry {
    RESISTANCE(
        Res.string.damage_modifier_interaction_resistance,
        Res.drawable.damage_modifier_resistance
    ),             // Half damage
    IMMUNITY(
        Res.string.damage_modifier_interaction_immunity,
        Res.drawable.damage_modifier_immunity
    ),               // Zero damage
    VULNERABILITY(
        Res.string.damage_modifier_interaction_vulnerability,
        Res.drawable.damage_modifier_vulnerability
    )        // Double damage
}