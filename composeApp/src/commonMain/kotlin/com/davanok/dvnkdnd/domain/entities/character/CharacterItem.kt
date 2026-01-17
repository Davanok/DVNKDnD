package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CharacterItem(
    val equipped: Boolean,
    val attuned: Boolean,
    val count: Int?,
    val item: DnDFullEntity
) {
    fun toCharacterItemLink() = CharacterItemLink(
        equipped = equipped,
        attuned = attuned,
        count = count,
        item = item.entity.id
    )
}
@Serializable
data class CharacterItemLink(
    val equipped: Boolean,
    val attuned: Boolean,
    val count: Int?,
    val item: Uuid
)