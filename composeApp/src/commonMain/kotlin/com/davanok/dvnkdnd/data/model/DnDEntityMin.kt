package com.davanok.dvnkdnd.data.model

import kotlinx.serialization.Serializable

@Serializable
data class DnDEntityMin (
    val id: Long,
    val name: String,
    val source: String,
)
