package com.davanok.dvnkdnd.domain.entities.character

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.domain.entities.dndEntities.WeaponDamageInfo

@Immutable
data class CharacterAttack(
    val rangeValues: List<Int>,
    val attackBonus: Int,
    val damages: List<WeaponDamageInfo>,
    val source: CharacterItem
)
