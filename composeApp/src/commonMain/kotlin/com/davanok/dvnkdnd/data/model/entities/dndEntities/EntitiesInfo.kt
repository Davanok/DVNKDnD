package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.data.model.dndEnums.Size
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDFeat
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDRace
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class RaceInfo(
    val speed: Int,
    val size: Size,
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
@Serializable
data class FeatInfo(
    val repeatable: Boolean
)
fun DnDFeat.toFeatInfo() = FeatInfo(
    repeatable = repeatable
)
fun FeatInfo.toDnDFeat(entityId: Uuid) = DnDFeat(
    entityId,
    repeatable
)