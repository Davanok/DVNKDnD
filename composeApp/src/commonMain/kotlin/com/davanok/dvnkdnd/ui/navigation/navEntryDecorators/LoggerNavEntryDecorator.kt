package com.davanok.dvnkdnd.ui.navigation.navEntryDecorators

import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavEntryDecorator
import co.touchlab.kermit.Logger
import com.davanok.dvnkdnd.ui.theme.LocalLogger

@Composable
fun <T: Any> rememberLoggerNavEntryDecorator(
    logger: Logger = LocalLogger.current
): LoggerNavEntryDecorator<T> = remember(logger) { LoggerNavEntryDecorator(logger) }

class LoggerNavEntryDecorator<T : Any>(
    logger: Logger
): NavEntryDecorator<T>(
    onPop = {  },
    decorate = { entry ->
        CompositionLocalProvider(LocalLogger provides logger.withTag(entry.contentKey.toString())) {
            entry.Content()
        }
    }
)

