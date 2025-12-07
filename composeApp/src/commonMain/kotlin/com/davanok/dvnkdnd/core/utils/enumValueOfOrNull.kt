package com.davanok.dvnkdnd.core.utils

inline fun <reified T : Enum<T>>enumValueOfOrNull(name: String): T? =
    try { enumValueOf<T>(name) } catch (_: Exception) { null }