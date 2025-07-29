package com.davanok.dvnkdnd.data.model.entities.character

import okio.Path
import kotlin.uuid.Uuid


data class CharacterMin(
    val id: Uuid,
    val name: String,
    val level: Int,
    val image: Path?
)
