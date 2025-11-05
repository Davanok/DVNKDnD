package com.davanok.dvnkdnd.data.model.entities.character

import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import kotlinx.serialization.Serializable

@Serializable
data class CharacterSpell(
    val ready: Boolean,
    val spell: DnDFullEntity
)
