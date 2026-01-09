package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterImage
import com.davanok.dvnkdnd.domain.entities.DatabaseImage
import kotlin.uuid.Uuid

fun DbCharacterImage.toDatabaseImage() = DatabaseImage(
    id = id,
    path = path,
    isMain = isMain
)
fun DatabaseImage.toDbCharacterImage(characterId: Uuid) = DbCharacterImage(
    id = id,
    characterId = characterId,
    path = path,
    isMain = isMain
)