package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.domain.entities.dndEntities.JoinProficiency
import com.davanok.dvnkdnd.domain.entities.dndEntities.Proficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion.DbProficiency
import kotlin.uuid.Uuid


fun Proficiency.toDbProficiency() = DbProficiency(
    id = id,
    userId = userId,
    itemPropertyId = itemPropertyId,
    type = type,
    name = name
)
fun DbProficiency.toProficiency() = Proficiency(
    id = id,
    userId = userId,
    itemPropertyId = itemPropertyId,
    type = type,
    name = name
)
fun JoinProficiency.toDbEntityProficiency(entityId: Uuid) = DbEntityProficiency(
    entityId = entityId,
    proficiencyId = proficiency.id,
    level = level
)
