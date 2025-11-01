package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.CharacterNote
import com.davanok.dvnkdnd.database.entities.character.DbCharacterNote
import kotlin.uuid.Uuid

fun DbCharacterNote.toCharacterNote() = CharacterNote(
    id = id,
    tags = tags.split(';').filter { it.isNotEmpty() },
    text = text
)
fun CharacterNote.toDbCharacterNote(characterId: Uuid) = DbCharacterNote(
    id = id,
    characterId = characterId,
    tags = tags.joinToString(";"),
    text = text
)