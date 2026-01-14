package com.davanok.dvnkdnd.data.platform

import okio.Path.Companion.toPath

lateinit var appDataDir: String

lateinit var appCacheDir: String

actual fun appDataDirectory() = appDataDir.toPath(normalize = true)
actual fun appCacheDirectory() = appCacheDir.toPath(normalize = true)
