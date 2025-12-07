package com.davanok.dvnkdnd.ui.presentation

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierOperation
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.base_value_replace
import dvnkdnd.composeapp.generated.resources.operation_sign_sqrt
import org.jetbrains.compose.resources.stringResource

@Composable
fun DnDModifierOperation.applyForString(
    base: String,
    value: Double
): String {
    fun wrapIfExpr(s: String): String = if (s.toDoubleOrNull() == null) "($s)" else s
    val operation = this
    val valueStr = if (value.toInt().toDouble() == value) value.toInt().toString() else value.toString()

    return when (operation) {
        DnDModifierOperation.SUM,
        DnDModifierOperation.SUB -> "$base ${stringResource(operation.stringRes, valueStr)}"

        DnDModifierOperation.MUL,
        DnDModifierOperation.DIV,
        DnDModifierOperation.MOD,
        DnDModifierOperation.POW,
        DnDModifierOperation.OTHER -> "${wrapIfExpr(base)} ${stringResource(operation.stringRes, valueStr)}"

        DnDModifierOperation.MIN,
        DnDModifierOperation.MAX,
        DnDModifierOperation.AVG -> stringResource(operation.stringRes, base, valueStr)

        DnDModifierOperation.ROOT -> {
            val radicand = wrapIfExpr(base)
            if (value == 2.0) stringResource(Res.string.operation_sign_sqrt, radicand)
            else stringResource(operation.stringRes, radicand, valueStr)
        }

        DnDModifierOperation.ABS,
        DnDModifierOperation.ROUND,
        DnDModifierOperation.CEIL,
        DnDModifierOperation.FLOOR -> {
            val expr = if (value == 0.0) base else "$base ${stringResource(DnDModifierOperation.SUM.stringRes, valueStr)}"
            stringResource(operation.stringRes, expr)
        }

        DnDModifierOperation.FACT -> {
            var expr = if (value == 0.0) base else "$base ${stringResource(DnDModifierOperation.SUM.stringRes, valueStr)}"
            expr = wrapIfExpr(expr)
            stringResource(operation.stringRes, expr)
        }
    }
}
@Composable
fun DnDModifierOperation.applyForStringPreview(
    value: String?
): String {
    fun wrapIfExpr(s: String): String = if (s.toDoubleOrNull() == null) "($s)" else s

    return when (this) {
        DnDModifierOperation.SUM,
        DnDModifierOperation.SUB -> stringResource(stringRes, value.toString())

        DnDModifierOperation.MUL,
        DnDModifierOperation.DIV,
        DnDModifierOperation.MOD,
        DnDModifierOperation.POW,
        DnDModifierOperation.OTHER -> stringResource(stringRes, value.toString())

        DnDModifierOperation.MIN,
        DnDModifierOperation.MAX,
        DnDModifierOperation.AVG,
        DnDModifierOperation.ROOT -> stringResource(
            stringRes,
            stringResource(Res.string.base_value_replace),
            value.toString()
        )

        DnDModifierOperation.ABS,
        DnDModifierOperation.ROUND,
        DnDModifierOperation.CEIL,
        DnDModifierOperation.FLOOR -> {
            val base = stringResource(Res.string.base_value_replace)
            val expr = if (value == null) base else "$base ${stringResource(DnDModifierOperation.SUM.stringRes, value)}"
            stringResource(stringRes, expr)
        }

        DnDModifierOperation.FACT -> {
            val base = stringResource(Res.string.base_value_replace)
            var expr = if (value == null) base else "$base ${stringResource(DnDModifierOperation.SUM.stringRes, value)}"
            expr = wrapIfExpr(expr)
            stringResource(stringRes, expr)
        }
    }
}