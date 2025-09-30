package com.davanok.dvnkdnd.data.model.entities.character

import com.davanok.dvnkdnd.database.entities.character.CharacterHealth
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DnDCharacterHealth(
    val max: Int,
    val current: Int,
    val temp: Int,
    @SerialName("max_modifier")
    val maxModified: Int // max health value with applied modifiers
)
fun CharacterHealth.toDnDCharacterHealth() = DnDCharacterHealth(
    max = max,
    current = current,
    temp = temp,
    maxModified = maxModified
)
fun DnDCharacterHealth.toCharacterHealth(characterId: Uuid) = CharacterHealth(
    id = characterId,
    max = max,
    current = current,
    temp = temp,
    maxModified = maxModified
)
