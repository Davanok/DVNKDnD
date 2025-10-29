package com.davanok.dvnkdnd.data.model.dndEnums

import androidx.compose.runtime.Composable
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.base_value_replace
import dvnkdnd.composeapp.generated.resources.operation_sign_abs
import dvnkdnd.composeapp.generated.resources.operation_sign_avg
import dvnkdnd.composeapp.generated.resources.operation_sign_ceil
import dvnkdnd.composeapp.generated.resources.operation_sign_div
import dvnkdnd.composeapp.generated.resources.operation_sign_fact
import dvnkdnd.composeapp.generated.resources.operation_sign_floor
import dvnkdnd.composeapp.generated.resources.operation_sign_max
import dvnkdnd.composeapp.generated.resources.operation_sign_min
import dvnkdnd.composeapp.generated.resources.operation_sign_mod
import dvnkdnd.composeapp.generated.resources.operation_sign_mul
import dvnkdnd.composeapp.generated.resources.operation_sign_other
import dvnkdnd.composeapp.generated.resources.operation_sign_pow
import dvnkdnd.composeapp.generated.resources.operation_sign_root
import dvnkdnd.composeapp.generated.resources.operation_sign_round
import dvnkdnd.composeapp.generated.resources.operation_sign_sqrt
import dvnkdnd.composeapp.generated.resources.operation_sign_sub
import dvnkdnd.composeapp.generated.resources.operation_sign_sum
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

enum class DnDModifierOperation(val stringRes: StringResource) {
    SUM     (Res.string.operation_sign_sum),
    SUB     (Res.string.operation_sign_sub),
    MUL     (Res.string.operation_sign_mul),
    DIV     (Res.string.operation_sign_div),
    ROOT    (Res.string.operation_sign_root),
    FACT    (Res.string.operation_sign_fact),
    AVG     (Res.string.operation_sign_avg),
    MAX     (Res.string.operation_sign_max),
    MIN     (Res.string.operation_sign_min),
    MOD     (Res.string.operation_sign_mod),
    POW     (Res.string.operation_sign_pow),
    ABS     (Res.string.operation_sign_abs),
    ROUND   (Res.string.operation_sign_round),
    CEIL    (Res.string.operation_sign_ceil),
    FLOOR   (Res.string.operation_sign_floor),
    OTHER   (Res.string.operation_sign_other)
}


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
    val operation = this

    return when (operation) {
        DnDModifierOperation.SUM,
        DnDModifierOperation.SUB -> stringResource(operation.stringRes, value.toString())

        DnDModifierOperation.MUL,
        DnDModifierOperation.DIV,
        DnDModifierOperation.MOD,
        DnDModifierOperation.POW,
        DnDModifierOperation.OTHER -> stringResource(operation.stringRes, value.toString())

        DnDModifierOperation.MIN,
        DnDModifierOperation.MAX,
        DnDModifierOperation.AVG,
        DnDModifierOperation.ROOT -> stringResource(
            operation.stringRes,
            stringResource(Res.string.base_value_replace),
            value.toString()
        )

        DnDModifierOperation.ABS,
        DnDModifierOperation.ROUND,
        DnDModifierOperation.CEIL,
        DnDModifierOperation.FLOOR -> {
            val base = stringResource(Res.string.base_value_replace)
            val expr = if (value == null) base else "$base ${stringResource(DnDModifierOperation.SUM.stringRes, value)}"
            stringResource(operation.stringRes, expr)
        }

        DnDModifierOperation.FACT -> {
            val base = stringResource(Res.string.base_value_replace)
            var expr = if (value == null) base else "$base ${stringResource(DnDModifierOperation.SUM.stringRes, value)}"
            expr = wrapIfExpr(expr)
            stringResource(operation.stringRes, expr)
        }
    }
}