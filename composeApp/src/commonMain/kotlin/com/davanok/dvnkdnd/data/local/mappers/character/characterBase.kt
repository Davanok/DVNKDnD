package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacter
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.data.local.db.model.character.DbCharacterWithImages
import com.davanok.dvnkdnd.domain.entities.character.CharacterMin

fun DbCharacter.toCharacterBase() = CharacterBase(
    id = id,
    userId = userId,
    name = name,
    description = description,
    level = level
)
fun CharacterBase.toDbCharacter() = DbCharacter(
    id = id,
    userId = userId,
    name = name,
    description = description,
    level = level
)

fun List<DbCharacterImage>.getMainImage() = firstOrNull { it.isMain } ?: firstOrNull()

fun DbCharacterWithImages.toCharacterMin() = CharacterMin(
    id = character.id,
    userId = character.userId,
    name = character.name,
    description = character.description,
    level = character.level,
    image = images.getMainImage()?.path
)