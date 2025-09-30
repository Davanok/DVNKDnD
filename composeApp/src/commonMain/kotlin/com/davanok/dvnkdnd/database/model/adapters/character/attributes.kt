package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.database.entities.character.CharacterAttributes
import kotlin.uuid.Uuid

fun CharacterAttributes.toAttributesGroup() = DnDAttributesGroup(
    strength = strength,
    dexterity = dexterity,
    constitution = constitution,
    intelligence = intelligence,
    wisdom = wisdom,
    charisma = charisma
)
fun DnDAttributesGroup.toCharacterAttributes(characterId: Uuid) = CharacterAttributes(
    id = characterId,
    strength = strength,
    dexterity = dexterity,
    constitution = constitution,
    intelligence = intelligence,
    wisdom = wisdom,
    charisma = charisma
)