package com.davanok.dvnkdnd.ui.components.text.modifiersText

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.core.utils.enumValueOfOrNull
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDRollModifier
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierRollTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.RollModifierAttackTargets
import com.davanok.dvnkdnd.domain.enums.dndEnums.RollOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_advantage
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_crit_threshold_reduce
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_disadvantage
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_reroll
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_with_target_advantage
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_with_target_crit_threshold_reduce
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_with_target_disadvantage
import dvnkdnd.composeapp.generated.resources.roll_modifier_operation_with_target_reroll
import org.jetbrains.compose.resources.stringResource

@Composable
fun DnDRollModifier.buildPreview() = buildRollModifierPreview(operation)
@Composable
fun DnDRollModifier.buildPreviewWithTarget() = buildRollModifierPreviewWithTarget(
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation
)
@Composable
fun buildRollModifierPreview(
    operation: RollOperation
): String {
    val stringRes = when (operation) {
        RollOperation.ADVANTAGE -> Res.string.roll_modifier_operation_advantage
        RollOperation.DISADVANTAGE -> Res.string.roll_modifier_operation_disadvantage
        RollOperation.REROLL -> Res.string.roll_modifier_operation_reroll
        RollOperation.CRIT_THRESHOLD_REDUCE -> Res.string.roll_modifier_operation_crit_threshold_reduce
    }
    return stringResource(stringRes)
}

@Composable
fun buildRollModifierPreviewWithTarget(
    targetScope: ModifierRollTarget,
    targetKey: String?,
    operation: RollOperation
): String {
    val targetString = buildTargetString(
        targetScope = targetScope,
        targetKey = targetKey
    )

    val stringRes = when (operation) {
        RollOperation.ADVANTAGE -> Res.string.roll_modifier_operation_with_target_advantage
        RollOperation.DISADVANTAGE -> Res.string.roll_modifier_operation_with_target_disadvantage
        RollOperation.REROLL -> Res.string.roll_modifier_operation_with_target_reroll
        RollOperation.CRIT_THRESHOLD_REDUCE -> Res.string.roll_modifier_operation_with_target_crit_threshold_reduce
    }
    return stringResource(stringRes, targetString)
}

@Composable
private fun buildTargetString(
    targetScope: ModifierRollTarget,
    targetKey: String?
) = buildString {
    append(stringResource(targetScope.stringRes))
    getTargetKeyName(targetScope, targetKey)?.let {
        append(" (")
        append(it)
        append(')')
    }
}

@Composable
private fun getTargetKeyName(
    targetScope: ModifierRollTarget,
    targetKey: String?
) = targetKey?.let {
    when(targetScope) {
        ModifierRollTarget.ATTACK_ROLL ->
            enumValueOfOrNull<RollModifierAttackTargets>(targetKey)
            ?.let { stringResource(it.stringRes) }
        ModifierRollTarget.SAVING_THROW ->
            enumValueOfOrNull<Attributes>(targetKey)
            ?.let { stringResource(it.stringRes) }
        ModifierRollTarget.SKILL_CHECK ->
            enumValueOfOrNull<Skills>(targetKey)
            ?.let { stringResource(it.stringRes) }
        ModifierRollTarget.DEATH_SAVE -> null
    } ?: targetKey
}