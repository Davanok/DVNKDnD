package com.davanok.dvnkdnd.data.model.entities

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DatabaseImage(
    val id: Uuid,
    val path: String
)
