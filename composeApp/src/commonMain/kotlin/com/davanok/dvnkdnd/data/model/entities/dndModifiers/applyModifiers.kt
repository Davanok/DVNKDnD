package com.davanok.dvnkdnd.data.model.entities.dndModifiers

import androidx.compose.ui.util.fastFilter
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
    fun resolvePreValue(group: DnDModifiersGroup): Double? =
        when (group.valueSource) {
            DnDModifierValueSource.CONST -> null
            DnDModifierValueSource.CHARACTER_LEVEL -> character.level.toDouble()
            DnDModifierValueSource.ENTITY_LEVEL -> TODO()
            DnDModifierValueSource.PROFICIENCY_BONUS -> character.getProfBonus().toDouble()
        }

    // generic processor for ATTRIBUTE / SKILL groups
    inline fun <reified K : Enum<K>> processGroups(
        targetType: DnDModifierTargetType,
        baseValues: MutableMap<K, Int>
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

    // apply attribute groups
    processGroups(
        DnDModifierTargetType.ATTRIBUTE,
        attributeValues
    )

    val attributes = attributeValues.toAttributesGroup()
    val skillValues = attributes.toSkillsGroup().toMap().toMutableMap()

    // apply skill groups
    processGroups(
        DnDModifierTargetType.SKILL,
        skillValues
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
    valueGetter: (DnDModifierValueSource) -> Double?
): Int {
    check(groups.groupBy { it.target }.size == 1)

    var resultValue = baseValue

    groups.sortedBy { it.priority }.fastForEach { group ->
        val preValue = valueGetter(group.valueSource)

        group.modifiers.fastForEach modifier@{ modifier ->
            if (!modifierFilter(modifier)) return@modifier

            val value = preValue ?: modifier.value

            if (group.minBaseValue?.let { resultValue < it } == true) return@modifier
            if (group.maxBaseValue?.let { resultValue > it } == true) return@modifier

            resultValue = applyOperation(resultValue, value, group.operation).coerceIn(group.clampMin, group.clampMax)
        }
    }

    return resultValue
}
