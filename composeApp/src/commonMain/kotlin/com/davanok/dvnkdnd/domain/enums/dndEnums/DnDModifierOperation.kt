package com.davanok.dvnkdnd.domain.enums.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
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
import dvnkdnd.composeapp.generated.resources.operation_sign_sub
import dvnkdnd.composeapp.generated.resources.operation_sign_sum
import org.jetbrains.compose.resources.StringResource

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
