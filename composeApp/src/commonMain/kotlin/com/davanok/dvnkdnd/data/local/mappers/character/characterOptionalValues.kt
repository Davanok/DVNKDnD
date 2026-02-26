package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalValues
import kotlin.uuid.Uuid

fun DbCharacterFullOptionalValues.toCharacterOptionalValues() = CharacterOptionalValues(
    proficiencyBonus = base.proficiencyBonus,
    initiative = base.initiative,
    armorClass = base.armorClass,
    speedValues = speedValues?.toSpeedValues()
)
fun CharacterOptionalValues.toDbCharacterOptionalValues(characterId: Uuid) = DbCharacterOptionalValues(
    id = characterId,
    proficiencyBonus = proficiencyBonus,
    initiative = initiative,
    armorClass = armorClass
)