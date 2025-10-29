package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.data.model.dndEnums.TimeUnits
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class AbilityInfo(
    @SerialName("usage_limit_by_level")
    val usageLimitByLevel: List<Int>,
    val regains: List<AbilityRegain>
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