@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.data.model.entities

import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import kotlinx.serialization.Serializable
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Serializable
data class DnDEntityMin(
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
)
@Serializable
data class DnDEntityWithSubEntities (
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
    val subEntities: List<DnDEntityMin>
) {
    fun asDnDEntityMin() = DnDEntityMin(id, type, name, source)
}
@Serializable
data class DnDEntityFullInfo(
    val type: DnDEntityTypes,
    val id: Uuid,
    val name: String,
    val source: String,
    // TODO ...
)
