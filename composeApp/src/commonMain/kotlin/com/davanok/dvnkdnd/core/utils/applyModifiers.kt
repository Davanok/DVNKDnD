package com.davanok.dvnkdnd.core.utils

import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierOperation
import io.github.aakira.napier.Napier
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

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


fun calculateModifierSum(
    baseValue: Int,
    groups: List<ModifiersGroup>,
    modifierFilter: (DnDModifier) -> Boolean,
    groupValueProvider: (ModifiersGroup) -> Double
): Int {
    require(groups.groupBy { it.target }.size == 1) {
        "All groups must have the same target"
    }

    var result = baseValue

    groups
        .sortedBy { it.priority }
        .forEach { group ->
            // compute group-level value once (nullable => use modifier.value when null)
            val value = groupValueProvider(group)

            group.modifiers.forEach modifier@{ modifier ->
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
