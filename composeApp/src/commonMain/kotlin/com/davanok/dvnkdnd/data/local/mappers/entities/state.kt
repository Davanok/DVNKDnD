package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbState
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbStateDuration
import com.davanok.dvnkdnd.data.local.db.model.DbFullState
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullState
import com.davanok.dvnkdnd.domain.entities.dndEntities.StateDuration
import kotlin.uuid.Uuid

fun DbFullState.toFullState() = FullState(
    duration = duration?.toStateDuration()
)

fun DbStateDuration.toStateDuration() = StateDuration(
    timeUnit = timeUnit,
    timeUnitsCount = timeUnitsCount
)

fun StateDuration.toDbStateDuration(stateId: Uuid) = DbStateDuration(
    id = stateId,
    timeUnit = timeUnit,
    timeUnitsCount = timeUnitsCount
)

fun FullState.toDbState(entityId: Uuid) = DbState(entityId)