package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalSpeedValues
import com.davanok.dvnkdnd.domain.entities.SpeedValues
import kotlin.uuid.Uuid

fun DbCharacterOptionalSpeedValues.toSpeedValues() = SpeedValues(
    walk = walk,
    swim = swim,
    fly = fly,
    climb = climb
)
fun SpeedValues.toDbCharacterOptionalSpeedValues(optionalValuesId: Uuid) = DbCharacterOptionalSpeedValues(
    id = optionalValuesId,
    walk = walk,
    swim = swim,
    fly = fly,
    climb = climb
)