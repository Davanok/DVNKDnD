package com.davanok.dvnkdnd.data.model.entities.character

import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CharacterNote(
    val id: Uuid,
    val tags: List<String>,
    val text: String
)
