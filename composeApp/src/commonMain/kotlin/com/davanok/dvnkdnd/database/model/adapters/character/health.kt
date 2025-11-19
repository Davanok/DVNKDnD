package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.CharacterHealth
import com.davanok.dvnkdnd.database.entities.character.DbCharacterHealth
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