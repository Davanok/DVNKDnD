package com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers

import com.davanok.dvnkdnd.core.utils.apply
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import com.davanok.dvnkdnd.ui.model.UiSelectableState

fun CharacterFull.resolveRawValueModifiersInfo(
    resultValues: MutableMap<ModifierValueTarget, MutableMap<String?, Int>>,
    modifiers: Map<ModifierValueTarget, List<RawModifierWrapper>>
): Map<ModifierValueTarget, List<ValueModifierInfo>> =
    modifiers
        .toList()
        .sortedBy { it.first } // Relies on ATTRIBUTE being early in the Enum declaration
        .associate { (type, modifiers) ->
            type to modifiers.map { modifier ->
                val modifierTarget = modifier.modifier.targetScope
                val targetKey = modifier.modifier.targetKey // Use targetKey for the update
                val sourceType = modifier.modifier.sourceType
                val sourceKey = modifier.modifier.sourceKey

                // 1. Resolve the raw source value
                val sourceValue = sourceType.toModifierTarget().let { targetScope ->
                    if (targetScope == null) resolveValueSource(sourceType, sourceKey, modifier.entityId)
                    else {
                        val rawValue = resultValues.getValue(targetScope, sourceKey)
                        // 2. Convert to D&D modifier if the source type demands it
                        if (sourceType == ValueSourceType.ATTRIBUTE_MODIFIER) {
                            calculateModifier(rawValue)
                        } else {
                            rawValue
                        }
                    }
                }

                val calculatedValue =
                    (sourceValue * modifier.modifier.multiplier).toInt() + modifier.modifier.flatValue

                // 3. Apply the operation to the CURRENT value of the target (for accumulation)
                val currentTargetValue = resultValues.getValue(modifierTarget, targetKey)
                val appliedValue = modifier.modifier.operation.apply(currentTargetValue, calculatedValue)

                // 4. Update the target key (e.g., ATHLETICS) instead of the source key (e.g., STRENGTH)
                resultValues.update(modifierTarget, targetKey, appliedValue)

                modifier.toValueModifierInfo(calculatedValue)
            }
        }

private fun ValueSourceType.toModifierTarget(): ModifierValueTarget? = when (this) {
    ValueSourceType.ATTRIBUTE,
    ValueSourceType.ATTRIBUTE_MODIFIER -> ModifierValueTarget.ATTRIBUTE // Both look at Attribute scores
    ValueSourceType.SKILL_MODIFIER -> ModifierValueTarget.SKILL
    else -> null
}

private fun RawModifierWrapper.toValueModifierInfo(
    calculatedValue: Int
) = ValueModifierInfo(
    isCustom = isCustom,
    modifier = modifier,
    group = group,
    resolvedValue = calculatedValue,
    state = UiSelectableState(selectable = isSelectable, selected = isSelected)
)

fun MutableMap<ModifierValueTarget, MutableMap<String?, Int>>.getValue(type: ModifierValueTarget, key: String?) =
    getOrPut(type, ::mutableMapOf).getOrPut(key) { 0 }
fun MutableMap<ModifierValueTarget, MutableMap<String?, Int>>.update(type: ModifierValueTarget, key: String?, value: Int) =
    getOrPut(type, ::mutableMapOf).set(key, value)