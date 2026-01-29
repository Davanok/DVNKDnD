package com.davanok.dvnkdnd.domain.entities.dndEntities

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


@Serializable
data class FeatureLink(
    @SerialName("feature_id")
    val featureId: Uuid,
    val level: Int
)