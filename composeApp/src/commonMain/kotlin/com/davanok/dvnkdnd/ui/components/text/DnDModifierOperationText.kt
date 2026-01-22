package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.ui.components.toCompactString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.base_value_replace
import dvnkdnd.composeapp.generated.resources.operation_sign_sqrt
import org.jetbrains.compose.resources.stringResource


private fun String.wrapInParensIfNeeded(): String {
    return if (this.toDoubleOrNull() == null) "($this)" else this
}

val DnDModifierOperation.isUnaryOffset: Boolean
    get() = this in setOf(
        DnDModifierOperation.ABS,
        DnDModifierOperation.ROUND,
        DnDModifierOperation.CEIL,
        DnDModifierOperation.FLOOR,
        DnDModifierOperation.FACT
    )

@Composable
fun DnDModifierOperation.applyForString(
    base: String,
    value: Double
): String {
    // Convert the double to a string and delegate to the unified formatter
    val valueStr = value.toCompactString()
    return formatOperationString(base, valueStr, value)
}

@Composable
fun DnDModifierOperation.applyForStringPreview(
    value: String?,
    base: String = stringResource(Res.string.base_value_replace)
): String = formatOperationString(base, value, null)

/**
 * Unified logic for generating the operation string.
 * @param base The base string (e.g., "10" or "Str").
 * @param valueStr The string representation of the modifier value (e.g., "2" or "Proficiency").
 * @param numericValue The actual numeric value if available (used for specific checks like val == 0).
 */
@Composable
private fun DnDModifierOperation.formatOperationString(
    base: String,
    valueStr: String?,
    numericValue: Double?
): String {
    // 1. Handle Unary/Offset Operations (ABS, ROUND, FACT, etc.)
    if (isUnaryOffset) {
        val isZeroOffset = numericValue == 0.0
        // If valueStr is null, we assume we just show the base, unless logic dictates otherwise
        val innerExpr = if (valueStr == null || isZeroOffset) {
            base
        } else {
            "$base ${stringResource(DnDModifierOperation.SUM.stringRes, valueStr)}"
        }

        // FACT requires wrapping complex expressions before the factorial sign
        val finalInner =
            if (this == DnDModifierOperation.FACT) innerExpr.wrapInParensIfNeeded() else innerExpr

        return stringResource(this.stringRes, finalInner)
    }

    // 2. Handle Binary Operations (Standard Math)
    // Safe fallback if valueStr is missing in a binary context
    val safeValue = valueStr.orEmpty()

    return when (this) {
        DnDModifierOperation.SUM,
        DnDModifierOperation.SUB -> {
            "$base ${stringResource(stringRes, safeValue)}"
        }

        DnDModifierOperation.MUL,
        DnDModifierOperation.DIV,
        DnDModifierOperation.MOD,
        DnDModifierOperation.POW,
        DnDModifierOperation.OTHER -> {
            "${base.wrapInParensIfNeeded()} ${stringResource(stringRes, safeValue)}"
        }

        DnDModifierOperation.MIN,
        DnDModifierOperation.MAX,
        DnDModifierOperation.AVG -> {
            stringResource(stringRes, base, safeValue)
        }

        DnDModifierOperation.ROOT -> {
            val radicand = base.wrapInParensIfNeeded()
            // Special case for Square Root (value 2.0)
            if (numericValue == 2.0) {
                stringResource(Res.string.operation_sign_sqrt, radicand)
            } else {
                stringResource(stringRes, radicand, safeValue)
            }
        }
        // Unary/Offset cases are handled in the `if` block above
        else -> base
    }
}