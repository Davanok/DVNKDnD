package com.davanok.dvnkdnd.ui.components.text.modifiersText

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDDamageModifier
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageInteractionType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageTypes
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.damage_modifier_interaction_immunity
import dvnkdnd.composeapp.generated.resources.damage_modifier_interaction_resistance
import dvnkdnd.composeapp.generated.resources.damage_modifier_interaction_vulnerability
import dvnkdnd.composeapp.generated.resources.damage_modifier_interaction_with_target_immunity
import dvnkdnd.composeapp.generated.resources.damage_modifier_interaction_with_target_resistance
import dvnkdnd.composeapp.generated.resources.damage_modifier_interaction_with_target_vulnerability
import org.jetbrains.compose.resources.stringResource


@Composable
fun DnDDamageModifier.buildPreview() = buildDamageModifierPreview(interaction)
@Composable
fun DnDDamageModifier.buildPreviewWithTarget() = buildDamageModifierPreviewWithTarget(
    damageType = damageType,
    interaction = interaction
)
@Composable
fun buildDamageModifierPreview(
    interaction: DamageInteractionType
): String {
    val stringRes = when (interaction) {
        DamageInteractionType.RESISTANCE -> Res.string.damage_modifier_interaction_resistance
        DamageInteractionType.IMMUNITY -> Res.string.damage_modifier_interaction_immunity
        DamageInteractionType.VULNERABILITY -> Res.string.damage_modifier_interaction_vulnerability
    }
    return stringResource(stringRes)
}

@Composable
fun buildDamageModifierPreviewWithTarget(
    damageType: DamageTypes,
    interaction: DamageInteractionType
): String {
    val damageTypeString = stringResource(damageType.stringRes)

    val stringRes = when (interaction) {
        DamageInteractionType.RESISTANCE -> Res.string.damage_modifier_interaction_with_target_resistance
        DamageInteractionType.IMMUNITY -> Res.string.damage_modifier_interaction_with_target_immunity
        DamageInteractionType.VULNERABILITY -> Res.string.damage_modifier_interaction_with_target_vulnerability
    }
    return stringResource(stringRes, damageTypeString)
}