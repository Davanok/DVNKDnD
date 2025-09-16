package com.davanok.dvnkdnd.data.model.entities.dndModifiers

import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
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
    resolvePreValue: (DnDModifiersGroup) -> Double?
) {
    targetGroups[targetType]?.fastForEach { group ->
        val preValue = resolvePreValue(group)
        group.modifiers.fastForEach modifier@{ modifier ->
            val target = modifier.targetAs<K>()
            val current = baseValues[target] ?: return@modifier

            if (group.minBaseValue?.let { current < it } == true) return@modifier
            if (group.maxBaseValue?.let { current > it } == true) return@modifier

            val value = preValue ?: modifier.value
            val result = applyOperation(current, value, group.operation)

            baseValues[target] = result.coerceIn(group.clampMin, group.clampMax)
        }
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
    val attributeValues = attributes.toMap().toMutableMap()

    val targetGroups = entities
        .fastFlatMap { it.modifiersGroups }
        .groupBy { it.target }
        .mapValues { (_, value) -> value.sortedBy { it.priority } }

    // helper: resolve group-level "preValue" once
    val resolvePreValue: (group: DnDModifiersGroup) -> Double? = { group ->
        when (group.valueSource) {
            DnDModifierValueSource.CONST -> null
            DnDModifierValueSource.CHARACTER_LEVEL -> character.level.toDouble()
            DnDModifierValueSource.ENTITY_LEVEL -> TODO()
            DnDModifierValueSource.PROFICIENCY_BONUS -> character.getProfBonus().toDouble()
        }
    }
    // apply attribute groups
    processGroups(
        targetGroups,
        DnDModifierTargetType.ATTRIBUTE,
        attributeValues,
        resolvePreValue
    )

    val attributes = attributeValues.toAttributesGroup()
    val skillValues = attributes.toSkillsGroup().toMap().toMutableMap()

    // apply skill groups
    processGroups(
        targetGroups,
        DnDModifierTargetType.SKILL,
        skillValues,
        resolvePreValue
    )

    val skills = skillValues.toSkillsGroup()

    return copy(
        attributes = attributes,
        skills = skills
    )
}

fun calculateModifierSum(
    baseValue: Int,
    groups: List<DnDModifiersGroup>,
    modifierFilter: (DnDModifier) -> Boolean,
    groupValueProvider: (DnDModifiersGroup) -> Double?
): Int {
    require(groups.groupBy { it.target }.size == 1) {
        "All groups must have the same target"
    }

    var result = baseValue

    groups
        .sortedBy { it.priority }
        .forEach { group ->
            // compute group-level value once (nullable => use modifier.value when null)
            val groupValue = groupValueProvider(group)

            group.modifiers.forEach { modifier ->
                if (!modifierFilter(modifier)) return@forEach

                val value = groupValue ?: modifier.value

                // skip modifier if current base (result) not in group's base range
                if (group.minBaseValue?.let { result < it } == true) return@forEach
                if (group.maxBaseValue?.let { result > it } == true) return@forEach

                // apply operation and clamp to group's limits
                result = applyOperation(result, value, group.operation).coerceIn(group.clampMin, group.clampMax)
            }
        }

    return result
}
