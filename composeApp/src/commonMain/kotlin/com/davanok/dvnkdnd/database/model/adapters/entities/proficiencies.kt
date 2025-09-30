package com.davanok.dvnkdnd.database.model.adapters.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.JoinProficiency
import com.davanok.dvnkdnd.data.model.entities.dndEntities.Proficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import kotlin.uuid.Uuid


fun Proficiency.toDnDProficiency() = DnDProficiency(
    id = id,
    userId = userId,
    type = type,
    name = name
)
fun DnDProficiency.toProficiency() = Proficiency(
    id = id,
    userId = userId,
    type = type,
    name = name
)
fun JoinProficiency.toEntityProficiency(entityId: Uuid) = EntityProficiency(
    entityId = entityId,
    proficiencyId = proficiency.id,
    level = level
)
