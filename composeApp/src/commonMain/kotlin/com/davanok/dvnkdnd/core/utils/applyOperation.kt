package com.davanok.dvnkdnd.core.utils

import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierOperation
import io.github.aakira.napier.Napier
import kotlin.math.abs
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.roundToInt

fun applyOperation(base: Int, value: Double, operation: DnDModifierOperation): Int {
    return runCatching {
        val vInt = value.toInt()

        when (operation) {
            DnDModifierOperation.SUM -> base + vInt
            DnDModifierOperation.SUB -> base - vInt
            DnDModifierOperation.MUL -> (base * value).toInt()
            DnDModifierOperation.DIV -> if (value == 0.0) base else (base / value).toInt()

            DnDModifierOperation.MOD -> if (value == 0.0) base else base % vInt
            DnDModifierOperation.POW -> base.toDouble().pow(value).toInt()
            DnDModifierOperation.ROOT -> if (value == 0.0) base else base.toDouble().pow(1.0 / value).toInt()

            DnDModifierOperation.MIN -> min(base, vInt)
            DnDModifierOperation.MAX -> max(base, vInt)
            DnDModifierOperation.AVG -> ((base + value) / 2.0).toInt()

            // Operations that treat 'value' as an offset to the base before calculating
            DnDModifierOperation.ABS -> abs(base + value).toInt()
            DnDModifierOperation.ROUND -> (base + value).roundToInt()
            DnDModifierOperation.CEIL -> ceil(base + value).toInt()
            DnDModifierOperation.FLOOR -> floor(base + value).toInt()

            DnDModifierOperation.FACT -> (2..base.coerceIn(0, 12)).fold(1) { acc, i -> acc * i } + vInt

            DnDModifierOperation.OTHER -> base
        }
    }.getOrElse {
        Napier.w(it) { "Error applying DnD operation: $operation on base: $base, value: $value" }
        base
    }
}
