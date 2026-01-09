package com.davanok.dvnkdnd.domain.entities.character

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Serializable
data class CharacterBase(
    val id: Uuid,
    val userId: Uuid?,
    val name: String,
    val description: String,
    val level: Int
)

@Serializable
@Immutable
data class CharacterMin(
    val id: Uuid,
    val userId: Uuid?,
    val name: String,
    val description: String,
    val level: Int,
    val image: String?
)