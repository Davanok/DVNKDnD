package com.davanok.dvnkdnd.core.utils

inline fun <K, V> Map<K, V>.getByKeyPredicate(predicate: (K) -> Boolean): V? {
    return entries.firstOrNull { predicate(it.key) }?.value
}
inline fun <T, K : Any, M : MutableMap<in K, MutableList<T>>> Iterable<T>.groupByNotNullTo(destination: M, keySelector: (T) -> K?): M {
    for (element in this) {
        val key = keySelector(element) ?: continue
        val list = destination.getOrPut(key) { ArrayList<T>() }
        list.add(element)
    }
    return destination
}
inline fun <T, K : Any> Iterable<T>.groupByNotNull(keySelector: (T) -> K?): Map<K, List<T>> {
    return groupByNotNullTo(LinkedHashMap<K, MutableList<T>>(), keySelector)
}