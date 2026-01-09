package com.davanok.dvnkdnd.domain.entities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DatabaseImage(
    val id: Uuid,
    val path: String,
    @SerialName("is_main")
    val isMain: Boolean
)
