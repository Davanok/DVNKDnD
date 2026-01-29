package com.davanok.dvnkdnd.core.utils

import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import kotlin.math.max
import kotlin.math.min

fun applyOperation(base: Int, value: Int, operation: ValueOperation): Int =
    when(operation) {
        ValueOperation.ADD -> base + value
        ValueOperation.SET -> value
        ValueOperation.SET_MIN -> min(base, value)
        ValueOperation.SET_MAX -> max(base, value)
    }

fun ValueOperation.apply(base: Int, value: Int) = applyOperation(base, value, this)
