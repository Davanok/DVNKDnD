package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.FeatInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.RaceInfo
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbRace
import kotlin.uuid.Uuid

fun DbFeat.toFeatInfo() = FeatInfo(
    repeatable = repeatable
)
fun FeatInfo.toDbFeat(entityId: Uuid) = DbFeat(
    id = entityId,
    repeatable = repeatable
)

fun DbRace.toRaceInfo() = RaceInfo(
    speed = speed,
    size = size
)
fun RaceInfo.toDbRace(entityId: Uuid) = DbRace(
    id = entityId,
    speed = speed,
    size = size
)