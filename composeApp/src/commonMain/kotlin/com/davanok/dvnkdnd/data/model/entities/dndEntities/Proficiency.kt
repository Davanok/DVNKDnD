package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.data.model.dndEnums.ProficiencyTypes
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class JoinProficiency(
    val level: Int,
    val proficiency: Proficiency
)
fun JoinProficiency.toEntityProficiency(entityId: Uuid) = EntityProficiency(
    entityId = entityId,
    proficiencyId = proficiency.id,
    level = level
)
@Serializable
data class Proficiency(
    val id: Uuid = Uuid.random(),
    @SerialName("user_id")
    val userId: Uuid? = null,
    val type: ProficiencyTypes,
    val name: String
)
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