package com.davanok.dvnkdnd.domain.entities.dndEntities

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Immutable
@Serializable
data class DnDEntityMin(
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
)

@Immutable
@Serializable
data class DnDEntityWithSubEntities(
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
    @SerialName("sub_entities")
    val subEntities: List<DnDEntityMin>,
) {
    fun toDnDEntityMin() = DnDEntityMin(id, type, name, source)
}
