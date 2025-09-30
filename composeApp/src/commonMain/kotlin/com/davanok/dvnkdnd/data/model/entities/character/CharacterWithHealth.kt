package com.davanok.dvnkdnd.data.model.entities.character

import com.davanok.dvnkdnd.data.model.dndEnums.Dices

data class CharacterWithHealth (
    val character: CharacterBase,
    val healthDice: Dices?,
    val constitution: Int,
    val baseHealth: Int,
)