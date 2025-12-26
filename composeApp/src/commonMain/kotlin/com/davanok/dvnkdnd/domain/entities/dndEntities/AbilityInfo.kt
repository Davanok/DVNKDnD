package com.davanok.dvnkdnd.domain.entities.dndEntities

import com.davanok.dvnkdnd.domain.enums.dndEnums.TimeUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class AbilityInfo(
    @SerialName("usage_limit_by_level")
    val usageLimitByLevel: List<Int>,
    val regains: List<AbilityRegain>,
    @SerialName("gives_state_self")
    val givesStateSelf: Uuid?,
    @SerialName("gives_state_target")
    val givesStateTarget: Uuid?
)
@Serializable
data class AbilityRegain(
    val id: Uuid = Uuid.random(),
    @SerialName("regains_count")
    val regainsCount: Int?,
    @SerialName("time_unit")
    val timeUnit: TimeUnit,
    @SerialName("time_unit_count")
    val timeUnitCount: Int
)