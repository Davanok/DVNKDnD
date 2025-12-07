package com.davanok.dvnkdnd.core.utils

inline fun <K, V> Map<K, V>.getByKeyPredicate(predicate: (K) -> Boolean): V? {
    return entries.firstOrNull { predicate(it.key) }?.value
}
