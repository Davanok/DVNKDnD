package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.entities.SpeedValues
import kotlinx.serialization.Serializable

@Serializable
data class CharacterOptionalValues(
    val proficiencyBonus: Int? = null,
    val initiative: Int? = null,
    val armorClass: Int? = null,
    val speedValues: SpeedValues? = null
)
