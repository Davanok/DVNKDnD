package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterHealth
import kotlin.uuid.Uuid


fun DbCharacterHealth.toDnDCharacterHealth() = CharacterHealth(
    max = max,
    current = current,
    temp = temp
)
fun CharacterHealth.toDbCharacterHealth(characterId: Uuid) = DbCharacterHealth(
    id = characterId,
    max = max,
    current = current,
    temp = temp
)