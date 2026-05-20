package com.davanok.dvnkdnd.core.utils

import co.touchlab.kermit.Logger

inline fun <R> Logger.runLogging(name: String, block: () -> R): Result<R> {
    this.d { "call $name" }
    return runCatching(block)
        .onFailure { this.e(it) { "failure on $name" } }
        .onSuccess {
            val msg = it.toString().replace(Regex("[\n\r]+"), " ")
            this.d { "success on $name: ($msg)" }
        }
}