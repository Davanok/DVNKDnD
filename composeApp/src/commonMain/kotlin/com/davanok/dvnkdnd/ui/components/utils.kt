package com.davanok.dvnkdnd.ui.components

import androidx.compose.ui.text.AnnotatedString

fun Int.toSignedString() = if (this > 0) "+$this" else toString()
fun Int.toSignedSpacedString() = if (this > 0) "+ $this" else toString()
fun Float.toSignedString() = if (this > 0f) "+$this" else toString()


const val DEFAULT_PARTS_SEP = " • "

fun StringBuilder.appendSep() = append(DEFAULT_PARTS_SEP)
fun AnnotatedString.Builder.appendSep() = append(DEFAULT_PARTS_SEP)