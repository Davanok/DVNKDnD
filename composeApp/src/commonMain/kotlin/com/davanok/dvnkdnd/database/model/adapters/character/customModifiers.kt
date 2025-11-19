package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.CustomModifier
import com.davanok.dvnkdnd.database.entities.character.DbCharacterCustomModifier
import kotlin.uuid.Uuid

fun DbCharacterCustomModifier.toCustomModifier() = CustomModifier(
    id = id,
    targetGlobal = targetGlobal,
    operation = operation,
    valueSource = valueSource,
    valueSourceTarget = valueSourceTarget,
    name = name,
    description = description,
    selectionLimit = selectionLimit,
    priority = priority,
    target = target,
    value = value
)
fun CustomModifier.toDbCharacterCustomModifier(characterId: Uuid) = DbCharacterCustomModifier(
    id = id,
    characterId = characterId,
    targetGlobal = targetGlobal,
    operation = operation,
    valueSource = valueSource,
    valueSourceTarget = valueSourceTarget,
    name = name,
    description = description,
    selectionLimit = selectionLimit,
    priority = priority,
    target = target,
    value = value
)