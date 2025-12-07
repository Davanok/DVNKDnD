package com.davanok.dvnkdnd.domain.entities.dndEntities

import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class EntityBase(
    val id: Uuid = Uuid.random(),
    @SerialName("parent_id")
    val parentId: Uuid? = null,
    @SerialName("user_id")
    val userId: Uuid? = null,
    val type: DnDEntityTypes,
    val name: String,
    val description: String,
    val source: String,
    @SerialName("image")
    val image: String? = null
) {
    fun toEntityMin() = DnDEntityMin(
        id = id,
        type = type,
        name = name,
        source = source
    )
}
