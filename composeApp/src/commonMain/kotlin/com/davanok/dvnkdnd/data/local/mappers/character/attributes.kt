package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterAttributes
import kotlin.uuid.Uuid

fun DbCharacterAttributes.toAttributesGroup() = AttributesGroup(
    strength = strength,
    dexterity = dexterity,
    constitution = constitution,
    intelligence = intelligence,
    wisdom = wisdom,
    charisma = charisma
)
fun AttributesGroup.toDbCharacterAttributes(characterId: Uuid) = DbCharacterAttributes(
    id = characterId,
    strength = strength,
    dexterity = dexterity,
    constitution = constitution,
    intelligence = intelligence,
    wisdom = wisdom,
    charisma = charisma
)