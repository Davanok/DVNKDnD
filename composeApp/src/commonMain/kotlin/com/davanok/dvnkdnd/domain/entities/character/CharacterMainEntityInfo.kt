package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CharacterMainEntityInfo(
    val level: Int,
    val entity: DnDFullEntity,
    val subEntity: DnDFullEntity?
) {
    operator fun contains(element: Uuid) = entity.entity.id == element || subEntity?.entity?.id == element
}

data class CharacterMainEntityLink(
    val level: Int,
    val entityId: Uuid,
    val subEntityId: Uuid?
)

fun CharacterMainEntityInfo.toCharacterMainEntityLink() = CharacterMainEntityLink(
    level = level,
    entityId = entity.entity.id,
    subEntityId = subEntity?.entity?.id
)