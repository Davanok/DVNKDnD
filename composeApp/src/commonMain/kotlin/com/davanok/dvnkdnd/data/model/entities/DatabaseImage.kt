package com.davanok.dvnkdnd.data.model.entities

import okio.Path
import kotlin.uuid.Uuid

data class DatabaseImage(
    val id: Uuid,
    val path: Path
)
