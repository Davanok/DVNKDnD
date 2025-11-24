package com.davanok.dvnkdnd.data.model.util

inline fun <K, V> Map<K, V>.getByKeyPredicate(predicate: (K) -> Boolean): V? {
    return entries.firstOrNull { predicate(it.key) }?.value
}
