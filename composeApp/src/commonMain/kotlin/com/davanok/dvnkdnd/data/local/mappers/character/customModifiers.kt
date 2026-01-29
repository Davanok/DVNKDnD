package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomDamageModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomRollModifier
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterCustomValueModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomDamageModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomRollModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomValueModifier
import kotlin.uuid.Uuid

fun DbCharacterCustomValueModifier.toCharacterCustomModifier() = CharacterCustomValueModifier(
    id = id,
    name = name,
    description = description,
    priority = priority,
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation,
    sourceType = sourceType,
    sourceKey = sourceKey,
    multiplier = multiplier,
    flatValue = flatValue,
    condition = condition
)
fun DbCharacterCustomRollModifier.toCharacterCustomModifier() = CharacterCustomRollModifier(
    id = id,
    name = name,
    description = description,
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation,
    condition = condition
)
fun DbCharacterCustomDamageModifier.toCharacterCustomModifier() = CharacterCustomDamageModifier(
    id = id,
    name = name,
    description = description,
    damageType = damageType,
    interaction = interaction,
    condition = condition
)

fun CharacterCustomValueModifier.toDbCharacterCustomModifier(characterId: Uuid) = DbCharacterCustomValueModifier(
    id = id,
    characterId = characterId,
    name = name,
    description = description,
    priority = priority,
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation,
    sourceType = sourceType,
    sourceKey = sourceKey,
    multiplier = multiplier,
    flatValue = flatValue,
    condition = condition
)
fun CharacterCustomRollModifier.toDbCharacterCustomModifier(characterId: Uuid) = DbCharacterCustomRollModifier(
    id = id,
    characterId = characterId,
    name = name,
    description = description,
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation,
    condition = condition
)
fun CharacterCustomDamageModifier.toDbCharacterCustomModifier(characterId: Uuid) = DbCharacterCustomDamageModifier(
    id = id,
    characterId = characterId,
    name = name,
    description = description,
    damageType = damageType,
    interaction = interaction,
    condition = condition
)