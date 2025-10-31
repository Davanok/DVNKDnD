package com.davanok.dvnkdnd.data.model.util

import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierArmorClassTarget
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierHealthTarget
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierInitiativeTarget
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
import io.github.aakira.napier.Napier
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt
import kotlin.text.toInt


private inline fun <reified K : Enum<K>> processGroups(
    modifierGroups: List<DnDModifiersGroup>,
    baseValues: MutableMap<K, Int>,
    groupValueProvider: (DnDModifiersGroup) -> Double
) {
    modifierGroups.fastForEach { group ->
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
    modifiers: List<CustomModifier>,
    values: MutableMap<T, Int>,
    valueSourcesProvider: (DnDModifierValueSource, String?, Double) -> Double
) {
    modifiers.fastForEach { modifier ->
        val target = modifier.targetAs<T>() ?: return@fastForEach
        val current = values[target] ?: return@fastForEach

        val value = valueSourcesProvider(modifier.valueSource, modifier.valueSourceTarget, modifier.value)
        val result = applyOperation(
            base = current,
            value = value,
            operation = modifier.operation
        )

        values[target] = result
    }
}


fun applyOperation(base: Int, value: Double, operation: DnDModifierOperation): Int =
    runCatching {
        when (operation) {
            DnDModifierOperation.SUM -> base + value.toInt()
            DnDModifierOperation.SUB -> base - value.toInt()
            DnDModifierOperation.MUL -> (base * value).toInt()
            DnDModifierOperation.DIV -> if (value == 0.0) base else (base / value).toInt()
            DnDModifierOperation.ROOT -> if (value == 0.0) base else base.toDouble().pow(1 / value).toInt()
            DnDModifierOperation.FACT -> (2..base.coerceIn(0, 12)).fold(1) { acc, i -> acc * i } + value.toInt()
            DnDModifierOperation.AVG -> ((base + value) / 2.0).toInt()
            DnDModifierOperation.MAX -> max(base, value.toInt())
            DnDModifierOperation.MIN -> min(base, value.toInt())
            DnDModifierOperation.MOD -> if (value == 0.0) base else base % value.toInt()
            DnDModifierOperation.POW -> base.toDouble().pow(value).toInt()
            DnDModifierOperation.ABS -> abs(base + value).toInt()
            DnDModifierOperation.ROUND -> (base + value).roundToInt()
            DnDModifierOperation.CEIL -> ceil(base + value).toInt()
            DnDModifierOperation.FLOOR -> floor(base + value).toInt()
            DnDModifierOperation.OTHER -> base
        }
    }.onFailure { Napier.w(it) { "apply operation exception" } }.getOrNull() ?: base

fun CharacterFull.withAppliedModifiers(): CharacterFull {
    val targetGroups = entities
        .fastFlatMap { it.modifiersGroups }
        .groupBy { it.target }
        .mapValues { (_, value) -> value.sortedBy { it.priority } }

    val customModifiersGrouped = customModifiers
        .groupBy { it.targetGlobal }
        .mapValues { (_, value) -> value.sortedBy { it.targetGlobal } }

    val customModifierValueSourceProvider: (DnDModifierValueSource, String?, Double) -> Double = { source, target, value ->
        resolveValueSource(source, target, null, value)
    }

    val attributeValues = attributes.toMap().toMutableMap()
    targetGroups[DnDModifierTargetType.ATTRIBUTE]?.let {
        processGroups(
            it,
            attributeValues,
            ::resolveGroupValue
        )
    }
    customModifiersGrouped[DnDModifierTargetType.ATTRIBUTE]?.let {
        applyCustomModifiers(it, attributeValues, customModifierValueSourceProvider)
    }
    val modifiedAttributes = attributeValues.toAttributesGroup()

    val savingThrowsValues = attributes
        .toMap()
        .mapValues { calculateModifier(it.value) }
        .toMutableMap()

    targetGroups[DnDModifierTargetType.SAVING_THROW]?.let {
        processGroups(
            it,
            savingThrowsValues,
            ::resolveGroupValue
        )
    }
    customModifiersGrouped[DnDModifierTargetType.SAVING_THROW]?.let {
        applyCustomModifiers(it, savingThrowsValues, customModifierValueSourceProvider)
    }
    val modifiedSavingThrows = savingThrowsValues.toAttributesGroup()

    val skillValues = attributes
        .toSkillsGroup()
        .toMap()
        .mapValues { calculateModifier(it.value) }
        .toMutableMap()

    targetGroups[DnDModifierTargetType.SKILL]?.let {
        processGroups(
            it,
            skillValues,
            ::resolveGroupValue
        )
    }
    customModifiersGrouped[DnDModifierTargetType.SKILL]?.let {
        applyCustomModifiers(it, skillValues, customModifierValueSourceProvider)
    }
    val modifiedSkills = skillValues.toSkillsGroup()

    val currentMaxHealth = health.max + calculateModifier(attributes[Attributes.CONSTITUTION])
    val healthValues = mutableMapOf(
        DnDModifierHealthTarget.CURRENT to health.current,
        DnDModifierHealthTarget.MAX to currentMaxHealth
    )

    targetGroups[DnDModifierTargetType.HEALTH]?.let {
        processGroups(
            it,
            healthValues,
            ::resolveGroupValue
        )
    }
    customModifiersGrouped[DnDModifierTargetType.HEALTH]?.let {
        applyCustomModifiers(it, healthValues, customModifierValueSourceProvider)
    }
    val modifiedHealth = health.copy(
        current = healthValues.getOrElse(DnDModifierHealthTarget.CURRENT) { health.current },
        max = healthValues.getOrElse(DnDModifierHealthTarget.MAX) { health.max }
    )

    val armorClassValues = mutableMapOf(
        DnDModifierArmorClassTarget.ARMOR_CLASS to appliedValues.armorClass
    )
    targetGroups[DnDModifierTargetType.ARMOR_CLASS]?.let {
        processGroups(
            it,
            armorClassValues,
            ::resolveGroupValue
        )
    }
    val armorClass = armorClassValues[DnDModifierArmorClassTarget.ARMOR_CLASS] ?: 0

    val initiativeValues = mutableMapOf(
        DnDModifierInitiativeTarget.INITIATIVE to appliedValues.initiative
    )
    targetGroups[DnDModifierTargetType.INITIATIVE]?.let {
        processGroups(
            it,
            initiativeValues,
            ::resolveGroupValue
        )
    }
    val initiative = initiativeValues[DnDModifierInitiativeTarget.INITIATIVE] ?: 0

    val appliedValues = CharacterModifiedValues(
        attributes = modifiedAttributes,
        savingThrowModifiers = modifiedSavingThrows,
        skillModifiers = modifiedSkills,
        health = modifiedHealth,
        armorClass = armorClass,
        initiative = initiative
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
