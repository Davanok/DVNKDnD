package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.domain.entities.dndEntities.FeatInfo
import com.davanok.dvnkdnd.domain.entities.dndEntities.RaceInfo
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbFeat
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbRace
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