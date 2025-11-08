package com.davanok.dvnkdnd.data.model.entities.character

import kotlinx.serialization.Serializable

@Serializable
data class CharacterOptionalValues(
    val proficiencyBonus: Int? = null,
    val initiative: Int? = null,
    val armorClass: Int? = null,
)
