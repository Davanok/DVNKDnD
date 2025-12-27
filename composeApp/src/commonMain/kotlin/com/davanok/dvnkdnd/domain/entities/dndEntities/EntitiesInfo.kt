package com.davanok.dvnkdnd.domain.entities.dndEntities

import com.davanok.dvnkdnd.domain.enums.dndEnums.Size
import kotlinx.serialization.Serializable

@Serializable
data class RaceInfo(
    val speed: Int, // in cm
    val size: Size,
)
@Serializable
data class FeatInfo(
    val repeatable: Boolean
)