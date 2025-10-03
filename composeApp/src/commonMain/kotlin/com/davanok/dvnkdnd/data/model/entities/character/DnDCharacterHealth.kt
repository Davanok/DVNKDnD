package com.davanok.dvnkdnd.data.model.entities.character

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DnDCharacterHealth(
    val max: Int = 0,
    val current: Int = max,
    val temp: Int = 0,
    @SerialName("max_modifier")
    val maxModified: Int = max // max health value with applied modifiers
)
