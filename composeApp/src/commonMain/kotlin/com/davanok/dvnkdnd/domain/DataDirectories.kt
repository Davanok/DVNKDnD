package com.davanok.dvnkdnd.domain

import okio.Path
import okio.Path.Companion.toPath

data class DataDirectories(
    val dataDirectory: Path,
    val cacheDirectory: Path
) {
    constructor(dataDirectory: String, cacheDirectory: String) : this(
        dataDirectory.toPath(true),
        cacheDirectory.toPath(true)
    )
}
