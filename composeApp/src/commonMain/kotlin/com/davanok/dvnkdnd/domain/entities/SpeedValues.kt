package com.davanok.dvnkdnd.domain.entities

import com.davanok.dvnkdnd.domain.enums.dndEnums.CharacterMovementType
import kotlinx.serialization.Serializable

@Serializable
data class SpeedValues(
    val walk: Int,
    val swim: Int,
    val fly: Int,
    val climb: Int
) {
    companion object {
        val Default = SpeedValues(0, 0, 0, 0)
    }
}

fun SpeedValues.toMap() = mapOf(
    CharacterMovementType.WALK to walk,
    CharacterMovementType.SWIM to swim,
    CharacterMovementType.CLIMB to climb,
    CharacterMovementType.FLY to fly
)

fun Map<CharacterMovementType, Int>.toSpeedValues() = SpeedValues(
    walk = get(CharacterMovementType.WALK) ?: 0,
    swim = get(CharacterMovementType.SWIM) ?: 0,
    fly = get(CharacterMovementType.FLY) ?: 0,
    climb = get(CharacterMovementType.CLIMB) ?: 0
)

fun SpeedValues.update(type: CharacterMovementType, value: Int) = when (type) {
    CharacterMovementType.WALK -> copy(walk = value)
    CharacterMovementType.SWIM -> copy(swim = value)
    CharacterMovementType.CLIMB -> copy(climb = value)
    CharacterMovementType.FLY -> copy(fly = value)
}