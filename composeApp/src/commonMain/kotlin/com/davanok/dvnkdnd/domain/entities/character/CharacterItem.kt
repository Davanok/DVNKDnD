package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import kotlinx.serialization.Serializable

@Serializable
data class CharacterItem(
    val equipped: Boolean,
    val active: Boolean,
    val attuned: Boolean,
    val item: DnDFullEntity
)
