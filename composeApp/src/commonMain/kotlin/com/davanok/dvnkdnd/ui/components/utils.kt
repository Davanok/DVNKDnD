package com.davanok.dvnkdnd.ui.components

fun Int.toSignedString() = if (this < 0) toString() else "+$this"