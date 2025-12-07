package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices

data class CharacterWithHealth (
    val character: CharacterBase,
    val healthDice: Dices?,
    val constitution: Int,
    val baseHealth: Int,
)