package com.davanok.dvnkdnd.domain.entities.dndEntities

import com.davanok.dvnkdnd.domain.enums.dndEnums.ProficiencyTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class JoinProficiency(
    val level: Int,
    val proficiency: Proficiency
)

@Serializable
data class Proficiency(
    val id: Uuid = Uuid.random(),
    @SerialName("user_id")
    val userId: Uuid? = null,
    @SerialName("item_property_id")
    val itemPropertyId: Uuid?,
    val type: ProficiencyTypes,
    val name: String
)