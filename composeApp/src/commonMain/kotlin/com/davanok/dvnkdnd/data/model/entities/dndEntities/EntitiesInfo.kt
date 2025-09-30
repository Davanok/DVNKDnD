package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.data.model.dndEnums.Size
import kotlinx.serialization.Serializable

@Serializable
data class RaceInfo(
    val speed: Int,
    val size: Size,
)
@Serializable
data class FeatInfo(
    val repeatable: Boolean
)