package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalValues
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