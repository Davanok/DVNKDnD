package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class CharacterSpell(
    val ready: Boolean,
    val spell: DnDFullEntity
)
@Serializable
data class CharacterSpellLink(
    val ready: Boolean,
    val spellId: Uuid
)
