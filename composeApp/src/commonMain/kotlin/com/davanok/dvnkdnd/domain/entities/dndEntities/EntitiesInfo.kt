package com.davanok.dvnkdnd.domain.entities.dndEntities

import com.davanok.dvnkdnd.domain.entities.SpeedValues
import com.davanok.dvnkdnd.domain.enums.dndEnums.Size
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FullRace(
    val size: Size,
    @SerialName("speed_values")
    val speedValues: SpeedValues,
)
@Serializable
data class FeatInfo(
    val repeatable: Boolean
)