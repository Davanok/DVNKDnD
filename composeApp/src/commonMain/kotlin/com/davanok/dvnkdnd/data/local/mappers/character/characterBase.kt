package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacter

fun DbCharacter.toCharacterBase() = CharacterBase(
    id = id,
    userId = userId,
    name = name,
    description = description,
    level = level,
    image = image
)
fun CharacterBase.toDbCharacter() = DbCharacter(
    id = id,
    userId = userId,
    name = name,
    description = description,
    level = level,
    image = image
)