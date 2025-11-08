package com.davanok.dvnkdnd.database.model.adapters.character

import com.davanok.dvnkdnd.data.model.entities.character.CharacterOptionalValues
import com.davanok.dvnkdnd.database.entities.character.DbCharacterOptionalValues
import kotlin.uuid.Uuid

fun DbCharacterOptionalValues.toCharacterOptionalValues() = CharacterOptionalValues(
    proficiencyBonus = proficiencyBonus,
    initiative = initiative,
    armorClass = armorClass
)
fun CharacterOptionalValues.toDbCharacterOptionalValues(characterId: Uuid) = DbCharacterOptionalValues(
    id = characterId,
    proficiencyBonus = proficiencyBonus,
    initiative = initiative,
    armorClass = armorClass
)