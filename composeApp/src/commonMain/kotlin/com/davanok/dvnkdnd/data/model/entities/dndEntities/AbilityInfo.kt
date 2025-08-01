package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.data.model.dndEnums.TimeUnits
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbilityRegain
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class AbilityInfo(
    @SerialName("usage_limit_by_level")
    val usageLimitByLevel: List<Int>?,
    val regains: List<AbilityRegain>
)
fun AbilityInfo.toDnDAbility(entityId: Uuid) = DnDAbility(
    id = entityId,
    usageLimitByLevel = usageLimitByLevel
)
@Serializable
data class AbilityRegain(
    val id: Uuid = Uuid.random(),
    @SerialName("regains_count")
    val regainsCount: Int?,
    @SerialName("time_unit")
    val timeUnit: TimeUnits,
    @SerialName("time_unit_count")
    val timeUnitCount: Int
)
fun AbilityRegain.toDnDAbilityRegain(abilityId: Uuid) = DnDAbilityRegain(
    id = id,
    abilityId = abilityId,
    regainsCount = regainsCount,
    timeUnit = timeUnit,
    timeUnitCount = timeUnitCount
)
fun DnDAbilityRegain.toAbilityRegain() = AbilityRegain(
    id = id,
    regainsCount = regainsCount,
    timeUnit = timeUnit,
    timeUnitCount = timeUnitCount
)