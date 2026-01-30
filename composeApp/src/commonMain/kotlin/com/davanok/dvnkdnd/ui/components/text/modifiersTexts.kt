package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.core.utils.enumValueOfOrNull
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierDerivedStatTargets
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
import org.jetbrains.compose.resources.stringResource


@Composable
fun ValueModifierInfo.buildPreview() =
    modifier.buildPreview(resolvedValue)
@Composable
fun DnDValueModifier.buildPreview(sourceResolvedValue: Int? = null) =
    buildModifierPreview(
        sourceType = sourceType,
        sourceKey = sourceKey,
        operation = operation,
        multiplier = multiplier,
        flatValue = flatValue,
        sourceResolvedValue = sourceResolvedValue
    )

@Composable
fun buildModifierPreview(
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
fun buildTargetString(
    targetScope: ModifierValueTarget,
    targetKey: String?
) = buildTargetString(
    targetScopeName = stringResource(targetScope.stringRes),
    targetKeyName = getTargetKeyName(targetScope, targetKey)
)

@Composable
private fun getTargetKeyName(
    targetScope: ModifierValueTarget,
    targetKey: String?
) = when (targetScope) {
    ModifierValueTarget.ATTRIBUTE,
    ModifierValueTarget.SAVING_THROW -> targetKey
        ?.let { enumValueOfOrNull<Attributes>(it) }
        ?.let { stringResource(it.stringRes) }

    ModifierValueTarget.SKILL -> targetKey
        ?.let { enumValueOfOrNull<Skills>(it) }
        ?.let { stringResource(it.stringRes) }

    ModifierValueTarget.DERIVED_STAT -> targetKey
        ?.let { enumValueOfOrNull<DnDModifierDerivedStatTargets>(it) }
        ?.let { stringResource(it.stringRes) }

    ModifierValueTarget.SPEED -> targetKey
        ?.let { enumValueOfOrNull<CharacterMovementType>(it) }
        ?.let { stringResource(it.stringRes) }

    ModifierValueTarget.SPELL_ATTACK -> null
    ModifierValueTarget.SPELL_DC -> null
    ModifierValueTarget.CARRY_WEIGHT -> null
    ModifierValueTarget.HEALTH -> targetKey
        ?.let { enumValueOfOrNull<DnDModifierHealthTargets>(it) }
        ?.let { stringResource(it.stringRes) }
} ?: targetKey

private fun buildTargetString(
    targetScopeName: String,
    targetKeyName: String?
): String = buildString {
    append(targetScopeName)
    if (targetKeyName != null) {
        append(" (")
        append(targetKeyName)
        append(')')
    }
}