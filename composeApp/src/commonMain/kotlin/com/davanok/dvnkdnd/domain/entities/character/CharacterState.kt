package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import kotlinx.serialization.Serializable

@Serializable
data class CharacterState(
    val state: DnDFullEntity,
    val source: DnDFullEntity?
)
