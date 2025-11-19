package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinProficiency
import com.davanok.dvnkdnd.data.model.entities.dndEntities.Proficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DbProficiency
import kotlin.uuid.Uuid


fun Proficiency.toDbProficiency() = DbProficiency(
    id = id,
    userId = userId,
    type = type,
    name = name
)
fun DbProficiency.toProficiency() = Proficiency(
    id = id,
    userId = userId,
    type = type,
    name = name
)
fun JoinProficiency.toDbEntityProficiency(entityId: Uuid) = DbEntityProficiency(
    entityId = entityId,
    proficiencyId = proficiency.id,
    level = level
)
