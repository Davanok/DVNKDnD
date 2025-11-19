package com.davanok.dvnkdnd.data.model.entities.character

import kotlinx.serialization.Serializable

@Serializable
data class CharacterHealth(
    val max: Int = 0,
    val current: Int = max,
    val temp: Int = 0
)
