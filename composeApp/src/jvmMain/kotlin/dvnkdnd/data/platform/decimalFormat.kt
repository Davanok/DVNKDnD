package com.davanok.dvnkdnd.data.platform

import java.math.RoundingMode


actual fun Float.toString(precision: Int): String =
    toBigDecimal()
        .setScale(precision, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()


actual fun Double.toString(precision: Int): String =
    toBigDecimal()
        .setScale(precision, RoundingMode.HALF_UP)
        .stripTrailingZeros()
        .toPlainString()