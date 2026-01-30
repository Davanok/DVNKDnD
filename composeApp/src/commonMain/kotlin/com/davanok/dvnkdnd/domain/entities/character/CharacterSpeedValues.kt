package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.enums.dndEnums.CharacterMovementType


data class CharacterSpeed(
    val walk: Int,
    val swim: Int,
    val fly: Int,
    val climb: Int
)

fun CharacterSpeed.toMap() = mapOf(
    CharacterMovementType.WALK to walk,
    CharacterMovementType.SWIM to swim,
    CharacterMovementType.FLY to fly,
    CharacterMovementType.CLIMB to climb,
)
fun Map<CharacterMovementType, Int>.toCharacterSpeed() = CharacterSpeed(
    walk = get(CharacterMovementType.WALK) ?: 0,
    swim = get(CharacterMovementType.SWIM) ?: 0,
    fly = get(CharacterMovementType.FLY) ?: 0,
    climb = get(CharacterMovementType.CLIMB) ?: 0
)