package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.DnDCharacterHealth
import com.davanok.dvnkdnd.database.entities.character.CharacterHealth
import kotlin.uuid.Uuid


fun CharacterHealth.toDnDCharacterHealth() = DnDCharacterHealth(
    max = max,
    current = current,
    temp = temp
)
fun DnDCharacterHealth.toCharacterHealth(characterId: Uuid) = CharacterHealth(
    id = characterId,
    max = max,
    current = current,
    temp = temp
)