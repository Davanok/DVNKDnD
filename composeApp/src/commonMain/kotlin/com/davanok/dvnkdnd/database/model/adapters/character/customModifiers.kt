package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.CustomModifier
import com.davanok.dvnkdnd.database.entities.character.CharacterCustomModifier
import kotlin.uuid.Uuid

fun CharacterCustomModifier.toCustomModifier() = CustomModifier(
    id = id,
    targetGlobal = targetGlobal,
    operation = operation,
    valueSource = valueSource,
    name = name,
    description = description,
    selectionLimit = selectionLimit,
    priority = priority,
    target = target,
    value = value
)
fun CustomModifier.toCharacterCustomModifier(characterId: Uuid) = CharacterCustomModifier(
    id = id,
    characterId = characterId,
    targetGlobal = targetGlobal,
    operation = operation,
    valueSource = valueSource,
    name = name,
    description = description,
    selectionLimit = selectionLimit,
    priority = priority,
    target = target,
    value = value
)