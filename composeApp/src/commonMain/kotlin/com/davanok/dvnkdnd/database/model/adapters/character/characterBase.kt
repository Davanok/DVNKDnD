package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.CharacterBase
import com.davanok.dvnkdnd.database.entities.character.DbCharacter

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