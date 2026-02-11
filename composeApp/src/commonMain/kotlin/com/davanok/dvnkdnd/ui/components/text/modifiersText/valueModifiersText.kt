package com.davanok.dvnkdnd.ui.components.text.modifiersText

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.core.utils.enumValueOfOrNull
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierDerivedValuesTargets
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierHealthTargets
import com.davanok.dvnkdnd.domain.enums.dndEnums.CharacterMovementType
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import com.davanok.dvnkdnd.ui.components.toCompactString
import com.davanok.dvnkdnd.ui.components.toSignedSpacedString
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.modifier_operation_place_bonus
import dvnkdnd.composeapp.generated.resources.modifier_operation_place_set
import dvnkdnd.composeapp.generated.resources.modifier_operation_place_set_max
import dvnkdnd.composeapp.generated.resources.modifier_operation_place_set_min
import dvnkdnd.composeapp.generated.resources.modifier_operation_with_target_place_bonus
import dvnkdnd.composeapp.generated.resources.modifier_operation_with_target_place_set
import dvnkdnd.composeapp.generated.resources.modifier_operation_with_target_place_set_max
import dvnkdnd.composeapp.generated.resources.modifier_operation_with_target_place_set_min
import org.jetbrains.compose.resources.stringResource


@Composable
fun ValueModifierInfo.buildPreview() =
    modifier.buildPreview(resolvedValue)
@Composable
fun DnDValueModifier.buildPreview(sourceResolvedValue: Int? = null) =
    buildValueModifierPreview(
        sourceType = sourceType,
        sourceKey = sourceKey,
        operation = operation,
        multiplier = multiplier,
        flatValue = flatValue,
        sourceResolvedValue = sourceResolvedValue
    )

@Composable
fun ValueModifierInfo.buildPreviewWithTarget() =
    modifier.buildPreviewWithTarget(resolvedValue)
@Composable
fun DnDValueModifier.buildPreviewWithTarget(sourceResolvedValue: Int? = null) =
    buildValueModifierPreviewWithTarget(
        targetScope = targetScope,
        targetKey = targetKey,
        sourceType = sourceType,
        sourceKey = sourceKey,
        operation = operation,
        multiplier = multiplier,
        flatValue = flatValue,
        sourceResolvedValue = sourceResolvedValue
    )

@Composable
fun buildValueModifierPreview(
    sourceType: ValueSourceType,
    sourceKey: String?,
    operation: ValueOperation,
    multiplier: Double,
    flatValue: Int,
    sourceResolvedValue: Int?
): String {
    val valueString = buildValueString(
        sourceType = sourceType,
        sourceKey = sourceKey,
        multiplier = multiplier,
        flatValue = flatValue,
        sourceResolvedValue = sourceResolvedValue
    )

    // 5. Build Final Phrase based on Operation
    val stringRes = when (operation) {
        ValueOperation.ADD -> Res.string.modifier_operation_place_bonus
        ValueOperation.SET -> Res.string.modifier_operation_place_set
        ValueOperation.SET_MIN -> Res.string.modifier_operation_place_set_min
        ValueOperation.SET_MAX -> Res.string.modifier_operation_place_set_max
    }

    return stringResource(stringRes, valueString)
}

@Composable
fun buildValueModifierPreviewWithTarget(
    targetScope: ModifierValueTarget,
    targetKey: String?,
    sourceType: ValueSourceType,
    sourceKey: String?,
    operation: ValueOperation,
    multiplier: Double,
    flatValue: Int,
    sourceResolvedValue: Int?
): String {
    val valueString = buildValueString(
        sourceType = sourceType,
        sourceKey = sourceKey,
        multiplier = multiplier,
        flatValue = flatValue,
        sourceResolvedValue = sourceResolvedValue
    )
    val targetString = buildTargetString(
        targetScope = targetScope,
        targetKey = targetKey
    )

    // 5. Build Final Phrase based on Operation
    val stringRes = when (operation) {
        ValueOperation.ADD -> Res.string.modifier_operation_with_target_place_bonus
        ValueOperation.SET -> Res.string.modifier_operation_with_target_place_set
        ValueOperation.SET_MIN -> Res.string.modifier_operation_with_target_place_set_min
        ValueOperation.SET_MAX -> Res.string.modifier_operation_with_target_place_set_max
    }

    return stringResource(stringRes, valueString, targetString)
}

@Composable
fun buildValueString(
    sourceType: ValueSourceType,
    sourceKey: String?,
    multiplier: Double,
    flatValue: Int,
    sourceResolvedValue: Int?
) = buildValueString(
    sourceType = sourceType,
    sourceTypeName = stringResource(sourceType.stringRes),
    sourceKeyName = getSourceTypeKeyName(sourceType, sourceKey),
    multiplier = multiplier,
    flatValue = flatValue,
    sourceResolvedValue = sourceResolvedValue
)

@Composable
private fun getSourceTypeKeyName(
    sourceType: ValueSourceType,
    sourceKey: String?
) = when (sourceType) {
    ValueSourceType.ATTRIBUTE,
    ValueSourceType.ATTRIBUTE_MODIFIER -> sourceKey
        ?.let { enumValueOfOrNull<Attributes>(it) }
        ?.let { stringResource(it.stringRes) }

    ValueSourceType.SKILL_MODIFIER -> sourceKey
        ?.let { enumValueOfOrNull<Skills>(it) }
        ?.let { stringResource(it.stringRes) }

    else -> null
} ?: sourceKey

private fun buildValueString(
    sourceType: ValueSourceType,
    sourceTypeName: String,
    sourceKeyName: String?,
    multiplier: Double,
    flatValue: Int,
    sourceResolvedValue: Int?
): String = buildString {
    // 1. Handle Source (Ignore if FLAT)
    if (sourceType != ValueSourceType.FLAT) {
        // e.g., "Strength" or "Proficiency Bonus"
        val label = sourceKeyName ?: sourceTypeName

        append(label)
        // Multiplier prefix (e.g., " * 2")
        if (multiplier != 1.0) {
            append(" * ")
            append(multiplier.toCompactString())
            append(' ')
        }

        // Show the current math result (e.g., " (+3)")
        if (sourceResolvedValue != null) {
            append(" (")
            append(sourceResolvedValue.toSignedString())
            append(')')
        }
    }

    // 2. Handle Flat Value
    if (flatValue != 0) {
        // If we have a source, we need a spaced sign ( + 2)
        // If we DON'T have a source (FLAT), we just show the number (2)
        if (sourceType != ValueSourceType.FLAT) {
            append(' ')
            append(flatValue.toSignedSpacedString())
        } else {
            append(flatValue)
        }
    }
}

@Composable
private fun buildTargetString(
    targetScope: ModifierValueTarget,
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
    targetScope: ModifierValueTarget,
    targetKey: String?
) = targetKey?.let {
    when (targetScope) {
        ModifierValueTarget.ATTRIBUTE,
        ModifierValueTarget.SAVING_THROW ->
            enumValueOfOrNull<Attributes>(targetKey)
            ?.let { stringResource(it.stringRes) }

        ModifierValueTarget.SKILL ->
            enumValueOfOrNull<Skills>(targetKey)
            ?.let { stringResource(it.stringRes) }

        ModifierValueTarget.DERIVED_STAT ->
            enumValueOfOrNull<DnDModifierDerivedValuesTargets>(targetKey)
            ?.let { stringResource(it.stringRes) }

        ModifierValueTarget.SPEED ->
            enumValueOfOrNull<CharacterMovementType>(targetKey)
            ?.let { stringResource(it.stringRes) }

        ModifierValueTarget.SPELL_ATTACK -> null
        ModifierValueTarget.SPELL_DC -> null
        ModifierValueTarget.CARRY_WEIGHT -> null
        ModifierValueTarget.HEALTH ->
            enumValueOfOrNull<DnDModifierHealthTargets>(targetKey)
            ?.let { stringResource(it.stringRes) }
    } ?: targetKey
}