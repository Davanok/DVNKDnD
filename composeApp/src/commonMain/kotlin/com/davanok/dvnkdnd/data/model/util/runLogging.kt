package com.davanok.dvnkdnd.data.model.util

import io.github.aakira.napier.Napier

inline fun <R> runLogging(name: String, tag: String? = null, block: () -> R): Result<R> {
    Napier.i(message = name, tag = tag)
    return runCatching(block)
        .onFailure { Napier.e(it, tag) { "failure on $name" } }
        .onSuccess { Napier.d(tag = tag) { "success on $name: ($it)" } }
}