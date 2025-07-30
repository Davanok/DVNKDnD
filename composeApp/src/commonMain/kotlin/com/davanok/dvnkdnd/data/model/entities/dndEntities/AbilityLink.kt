package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Serializable
data class AbilityLink(
    @SerialName("ability_id")
    val abilityId: Uuid,
    val level: Int
)
fun EntityAbility.toAbilityLink() = AbilityLink(
    abilityId = abilityId,
    level = level
)
fun AbilityLink.toEntityAbility(entityId: Uuid) = EntityAbility(
    entityId = entityId,
    abilityId = abilityId,
    level = level
)