package com.davanok.dvnkdnd.domain.entities.dndEntities

import com.davanok.dvnkdnd.domain.enums.dndEnums.TimeUnit
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullState(
    val duration: StateDuration?
)

@Serializable
data class StateDuration(
    @SerialName("time_unit")
    val timeUnit: TimeUnit,
    @SerialName("time_units_count")
    val timeUnitsCount: Int
)
