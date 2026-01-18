package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSpellLink
import com.davanok.dvnkdnd.domain.entities.character.CharacterSpellLink
import kotlin.uuid.Uuid

fun CharacterSpellLink.toDbCharacterSpell(characterId: Uuid) = DbCharacterSpellLink(
    characterId = characterId,
    spellId = spellId,
    ready = ready
)