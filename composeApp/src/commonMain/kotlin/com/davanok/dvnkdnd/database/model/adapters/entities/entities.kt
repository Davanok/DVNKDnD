package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.FeatInfo
import com.davanok.dvnkdnd.data.model.entities.dndEntities.RaceInfo
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import kotlin.uuid.Uuid

fun DnDFeat.toFeatInfo() = FeatInfo(
    repeatable = repeatable
)
fun FeatInfo.toDnDFeat(entityId: Uuid) = DnDFeat(
    entityId,
    repeatable
)

fun DnDRace.toRaceInfo() = RaceInfo(
    speed = speed,
    size = size
)
fun RaceInfo.toDnDRace(entityId: Uuid) = DnDRace(
    entityId,
    speed,
    size
)