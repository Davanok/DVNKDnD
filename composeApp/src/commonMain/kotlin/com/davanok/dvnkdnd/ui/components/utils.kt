package com.davanok.dvnkdnd.ui.components

import androidx.compose.ui.text.AnnotatedString
import com.davanok.dvnkdnd.data.platform.toString
import kotlin.math.absoluteValue

fun Int.toSignedString() = if (this > 0) "+$this" else toString()
fun Int.toSignedSpacedString() = if (this > 0) "+ $this" else "- $absoluteValue"
fun Float.toSignedString() = if (this > 0f) "+$this" else toString()

fun Double.toCompactString(): String {
    return if (this % 1.0 == 0.0) this.toInt().toString() else this.toString(2)
}


const val DEFAULT_PARTS_SEP = " • "

fun StringBuilder.appendSep() = append(DEFAULT_PARTS_SEP)
fun AnnotatedString.Builder.appendSep() = append(DEFAULT_PARTS_SEP)