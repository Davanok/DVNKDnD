package com.davanok.dvnkdnd.data.model.util

inline fun <reified T : Enum<T>>enumValueOfOrNull(name: String): T? =
    try { enumValueOf<T>(name) } catch (_: Exception) { null }