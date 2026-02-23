package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbRaceSpeedValues
import com.davanok.dvnkdnd.domain.entities.SpeedValues
import kotlin.uuid.Uuid

fun DbRaceSpeedValues.toSpeedValues() = SpeedValues(
    walk = walk,
    swim = swim,
    fly = fly,
    climb = climb
)
fun SpeedValues.toDbRaceSpeedValues(raceId: Uuid) = DbRaceSpeedValues(
    id = raceId,
    walk = walk,
    swim = swim,
    fly = fly,
    climb = climb
)