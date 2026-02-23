package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbRace
import com.davanok.dvnkdnd.data.local.db.model.DbFullRace
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullRace
import kotlin.uuid.Uuid


fun DbFullRace.toFullRace() = FullRace(
    size = race.size,
    speedValues = speedValues.toSpeedValues(),
)
fun FullRace.toDbRace(entityId: Uuid) = DbRace(
    id = entityId,
    size = size
)