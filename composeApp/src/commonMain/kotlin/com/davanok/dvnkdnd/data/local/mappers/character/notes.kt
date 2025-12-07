package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.domain.entities.character.CharacterNote
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterNote
import kotlin.uuid.Uuid

fun DbCharacterNote.toCharacterNote() = CharacterNote(
    id = id,
    pinned = pinned,
    tags = tags.split(';').filter { it.isNotEmpty() }.toSet(),
    text = text
)
fun CharacterNote.toDbCharacterNote(characterId: Uuid) = DbCharacterNote(
    id = id,
    characterId = characterId,
    pinned = pinned,
    tags = tags.joinToString(";"),
    text = text
)