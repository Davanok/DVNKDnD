package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class JoinProficiency(
    val level: Int,
    val proficiency: DnDProficiency
)
fun JoinProficiency.toEntityProficiency(entityId: Uuid) = EntityProficiency(
    entityId = entityId,
    proficiencyId = proficiency.id,
    level = level
)