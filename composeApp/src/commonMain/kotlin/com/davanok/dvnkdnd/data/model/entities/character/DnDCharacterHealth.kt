package com.davanok.dvnkdnd.data.model.entities.character

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DnDCharacterHealth(
    val max: Int,
    val current: Int,
    val temp: Int,
    @SerialName("max_modifier")
    val maxModified: Int // max health value with applied modifiers
)
