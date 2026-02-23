package com.davanok.dvnkdnd.domain.entities.dndEntities

import com.davanok.dvnkdnd.domain.entities.SpeedValues
import com.davanok.dvnkdnd.domain.enums.dndEnums.Size
import kotlinx.serialization.Serializable

@Serializable
data class FullRace(
    val size: Size,
    val speedValues: SpeedValues,
)
@Serializable
data class FeatInfo(
    val repeatable: Boolean
)