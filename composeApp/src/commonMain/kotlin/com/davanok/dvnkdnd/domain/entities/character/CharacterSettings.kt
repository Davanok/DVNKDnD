package com.davanok.dvnkdnd.domain.entities.character

import kotlinx.serialization.Serializable

@Serializable
data class CharacterSettings(
    val valueModifiersCompactView: Boolean,
    val autoLevelChange: Boolean
)
