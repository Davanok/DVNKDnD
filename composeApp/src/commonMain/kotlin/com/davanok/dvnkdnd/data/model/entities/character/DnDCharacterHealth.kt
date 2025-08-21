package com.davanok.dvnkdnd.data.model.entities.character

import com.davanok.dvnkdnd.database.entities.character.CharacterHealth

data class DnDCharacterHealth(
    val max: Int,
    val current: Int,
    val temp: Int,
    val maxModifier: Int
)
fun CharacterHealth.toDnDCharacterHealth() = DnDCharacterHealth(
    max = max,
    current = current,
    temp = temp,
    maxModifier = maxModifier
)
