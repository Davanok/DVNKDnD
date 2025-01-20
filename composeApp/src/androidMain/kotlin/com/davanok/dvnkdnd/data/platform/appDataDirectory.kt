package com.davanok.dvnkdnd.data.platform

import com.davanok.dvnkdnd.appDataDir
import okio.Path.Companion.toPath

actual fun appDataDirectory() = appDataDir.toPath(normalize = true)
