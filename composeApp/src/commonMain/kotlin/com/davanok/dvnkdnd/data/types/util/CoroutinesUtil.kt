package com.davanok.dvnkdnd.data.types.util

import kotlinx.coroutines.flow.SharingStarted

private const val StopTimeoutMillis: Long = 5000

val WhileUiSubscribed: SharingStarted = SharingStarted.WhileSubscribed(StopTimeoutMillis)