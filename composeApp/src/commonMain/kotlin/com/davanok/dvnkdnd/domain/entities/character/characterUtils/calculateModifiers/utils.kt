package com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers

import com.davanok.dvnkdnd.domain.dnd.calculateArmorClass
import com.davanok.dvnkdnd.domain.entities.character.CharacterDerivedValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterSpeed
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes

fun CharacterFull.calculateDerivedValues(
    dexterityModifier: Int,
    perceptionSkill: Int
) = CharacterDerivedValues(
    initiative = optionalValues.initiative ?: dexterityModifier,
    armorClass = optionalValues.armorClass ?: calculateBaseArmorClass(dexterityModifier),
    passivePerception = 10 + perceptionSkill
)

fun CharacterFull.calculateSpeedValues(): CharacterSpeed {
    val baseSpeed = calculateBaseSpeed()
    return CharacterSpeed(
        walk = baseSpeed,
        swim = baseSpeed / 2,
        fly = 0,
        climb = 0
    )
}

fun CharacterFull.calculateBaseSpeed(): Int {
    return mainEntities
        .filter { it.entity.entity.type == DnDEntityTypes.RACE }
        .maxOfOrNull { it.entity.race?.speed ?: 0 } ?: 0
}

fun CharacterFull.calculateBaseArmorClass(dexterityModifier: Int): Int {
    val equippedArmor = items
        .firstOrNull { it.equipped && it.item.item?.armor != null }
        ?.item?.item?.armor

    return calculateArmorClass(dexterityModifier, equippedArmor)
}