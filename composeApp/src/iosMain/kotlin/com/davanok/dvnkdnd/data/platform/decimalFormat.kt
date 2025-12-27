package com.davanok.dvnkdnd.data.platform

import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter


actual fun Float.toString(precision: Int): String =
    NSNumberFormatter().apply {
        minimumFractionDigits = 0u
        maximumFractionDigits = precision.toULong()
        numberStyle = 1u
    }.stringFromNumber(NSNumber(this))!!


actual fun Double.toString(precision: Int): String =
    NSNumberFormatter().apply {
        minimumFractionDigits = 0u
        maximumFractionDigits = precision.toULong()
        numberStyle = 1u
    }.stringFromNumber(NSNumber(this))!!