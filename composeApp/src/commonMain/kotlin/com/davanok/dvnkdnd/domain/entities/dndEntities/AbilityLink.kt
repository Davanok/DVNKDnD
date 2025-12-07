package com.davanok.dvnkdnd.domain.entities.dndEntities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Serializable
data class AbilityLink(
    @SerialName("ability_id")
    val abilityId: Uuid,
    val level: Int
)