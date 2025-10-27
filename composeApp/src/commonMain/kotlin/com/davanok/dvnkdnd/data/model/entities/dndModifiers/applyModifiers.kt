package com.davanok.dvnkdnd.data.model.util

import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierHealthTargets
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.CharacterModifiedValues
import com.davanok.dvnkdnd.data.model.entities.character.CustomModifier
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toSkillsGroup
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt


// generic processor for ATTRIBUTE / SKILL groups
private inline fun <reified K : Enum<K>> processGroups(
    targetGroups: Map<DnDModifierTargetType, List<DnDModifiersGroup>>,
    targetType: DnDModifierTargetType,
    baseValues: MutableMap<K, Int>,
    groupValueProvider: (DnDModifiersGroup) -> Double
) {
    targetGroups[targetType]?.fastForEach { group ->
        val value = groupValueProvider(group)
        group.modifiers.fastForEach modifier@{ modifier ->
            val target = modifier.targetAs<K>()
            if (target == null) return@modifier
            val current = baseValues[target] ?: return@modifier

            if (group.minBaseValue?.let { current < it } == true) return@modifier
            if (group.maxBaseValue?.let { current > it } == true) return@modifier

            val result = applyOperation(current, value, group.operation)

            baseValues[target] = result.coerceIn(group.clampMin, group.clampMax)
        }
    }
}

private inline fun <reified T : Enum<T>> applyCustomModifiers(
    grouped: Map<DnDModifierTargetType, List<CustomModifier>>,
    targetType: DnDModifierTargetType,
    values: MutableMap<T, Int>,
    valueSources: Map<DnDModifierValueSource, Double?>
) {
    grouped[targetType]
        ?.fastForEach { modifier ->
            val target = modifier.targetAs<T>() ?: return@fastForEach
            val current = values[target] ?: return@fastForEach

            val value = valueSources[modifier.valueSource] ?: modifier.value
            val result = applyOperation(
                base = current,
                value = value,
                operation = modifier.operation
            )

            values[target] = result
        }
}


fun applyOperation(base: Int, value: Double, operation: DnDModifierOperation): Int =
    when (operation) {
        DnDModifierOperation.SUM -> base + value.toInt()
        DnDModifierOperation.SUB -> base - value.toInt()
        DnDModifierOperation.MUL -> (base * value).toInt()
        DnDModifierOperation.DIV -> (base / value).toInt()
        DnDModifierOperation.ROOT -> base.toDouble().pow(1/value).toInt()
        DnDModifierOperation.FACT -> (2..value.toInt()).fold(1) { acc, i -> acc * i }
        DnDModifierOperation.AVG -> ((base + value) / 2f).toInt()
        DnDModifierOperation.MAX -> max(base, value.toInt())
        DnDModifierOperation.MIN -> min(base, value.toInt())
        DnDModifierOperation.MOD -> base % value.toInt()
        DnDModifierOperation.POW -> base.toDouble().pow(value).toInt()
        DnDModifierOperation.ABS -> abs(base + value).toInt()
        DnDModifierOperation.ROUND -> (base + value).roundToInt()
        DnDModifierOperation.CEIL -> ceil(base + value).toInt()
        DnDModifierOperation.FLOOR -> floor(base + value).toInt()
        DnDModifierOperation.OTHER -> base
    }

fun CharacterFull.withAppliedModifiers(): CharacterFull {
    val targetGroups = entities
        .fastFlatMap { it.modifiersGroups }
        .groupBy { it.target }
        .mapValues { (_, value) -> value.sortedBy { it.priority } }

    val customModifiersGrouped = customModifiers
        .groupBy { it.targetGlobal }
        .mapValues { (_, value) -> value.sortedBy { it.targetGlobal } }

    val valueSources = mapOf(
        DnDModifierValueSource.CHARACTER_LEVEL to character.level.toDouble(),
        DnDModifierValueSource.PROFICIENCY_BONUS to character.getProfBonus().toDouble()
    )

    val attributeValues = attributes.toMap().toMutableMap()
    processGroups(
        targetGroups,
        DnDModifierTargetType.ATTRIBUTE,
        attributeValues,
        ::resolveGroupValue
    )
    applyCustomModifiers(customModifiersGrouped, DnDModifierTargetType.ATTRIBUTE, attributeValues, valueSources)
    val modifiedAttributes = attributeValues.toAttributesGroup()

    val savingThrowsValues = attributes
        .toMap()
        .mapValues { calculateModifier(it.value) }
        .toMutableMap()
    processGroups(
        targetGroups,
        DnDModifierTargetType.SAVING_THROW,
        savingThrowsValues,
        ::resolveGroupValue
    )
    applyCustomModifiers(customModifiersGrouped, DnDModifierTargetType.SAVING_THROW, savingThrowsValues, valueSources)
    val modifiedSavingThrows = savingThrowsValues.toAttributesGroup()

    val skillValues = attributes
        .toSkillsGroup()
        .toMap()
        .mapValues { calculateModifier(it.value) }
        .toMutableMap()
    processGroups(
        targetGroups,
        DnDModifierTargetType.SKILL,
        skillValues,
        ::resolveGroupValue
    )
    applyCustomModifiers(customModifiersGrouped, DnDModifierTargetType.SKILL, skillValues, valueSources)
    val modifiedSkills = skillValues.toSkillsGroup()

    val currentMaxHealth = health.max + calculateModifier(attributes[Attributes.CONSTITUTION])
    val healthValues = mutableMapOf(
        DnDModifierHealthTargets.CURRENT to health.current,
        DnDModifierHealthTargets.MAX to currentMaxHealth
    )
    processGroups(
        targetGroups,
        DnDModifierTargetType.HEALTH,
        healthValues,
        ::resolveGroupValue
    )
    applyCustomModifiers(customModifiersGrouped, DnDModifierTargetType.HEALTH, healthValues, valueSources)
    val modifiedHealth = health.copy(
        current = healthValues.getOrElse(DnDModifierHealthTargets.CURRENT) { health.current },
        max = healthValues.getOrElse(DnDModifierHealthTargets.MAX) { health.max }
    )


    val appliedValues = CharacterModifiedValues(
        attributes = modifiedAttributes,
        savingThrowModifiers = modifiedSavingThrows,
        skillModifiers = modifiedSkills,
        health = modifiedHealth
    )
    return copy(
        appliedValues = appliedValues
    )
}


fun calculateModifierSum(
    baseValue: Int,
    groups: List<DnDModifiersGroup>,
    modifierFilter: (DnDModifier) -> Boolean,
    groupValueProvider: (DnDModifiersGroup) -> Double
): Int {
    require(groups.groupBy { it.target }.size == 1) {
        "All groups must have the same target"
    }

    var result = baseValue

    groups
        .sortedBy { it.priority }
        .fastForEach { group ->
            // compute group-level value once (nullable => use modifier.value when null)
            val value = groupValueProvider(group)

            group.modifiers.fastForEach modifier@{ modifier ->
                if (!modifierFilter(modifier)) return@modifier

                // skip modifier if current base (result) not in group's base range
                if (group.minBaseValue?.let { result < it } == true) return@modifier
                if (group.maxBaseValue?.let { result > it } == true) return@modifier

                // apply operation and clamp to group's limits
                result = applyOperation(result, value, group.operation).coerceIn(group.clampMin, group.clampMax)
            }
        }

    return result
}
