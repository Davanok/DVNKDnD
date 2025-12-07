package com.davanok.dvnkdnd.domain.entities.character

import kotlinx.serialization.Serializable

@Serializable
data class CharacterHealth(
    val max: Int = 0,
    val current: Int = max,
    val temp: Int = 0
)
