package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.database.entities.character.DbCharacterAttributes
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