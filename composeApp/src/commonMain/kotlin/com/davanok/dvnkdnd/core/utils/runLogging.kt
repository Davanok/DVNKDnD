package com.davanok.dvnkdnd.core.utils

import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onStart

inline fun <R> Logger.runLogging(name: String, block: () -> R): Result<R> {
    this.i { "call $name" }
    return runCatching(block)
        .onFailure { this.e(it) { "failure on $name" } }
        .onSuccess { result ->
            if (result == Unit) {
                val msg = result.toString().replace(Regex("[\n\r]+"), " ")
                this.d { "success on $name: ($msg)" }
            }
            else {
                this.d { "success on $name" }
            }
        }
}

fun <T> Flow<T>.toResultFlow(name: String, logger: Logger) = this
    .onStart { logger.i { "start $name flow" } }
    .map {
        logger.d { "$name updated: ($it)" }
        Result.success(it)
    }
    .catch {
        logger.e(it) { "failure on $name" }
        emit(Result.failure(it))
    }