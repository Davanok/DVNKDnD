package com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers

import com.davanok.dvnkdnd.domain.dnd.calculateArmorClass
import com.davanok.dvnkdnd.domain.entities.character.CharacterDerivedValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.SpeedValues
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes

fun CharacterFull.calculateDerivedValues(
    dexterityModifier: Int,
    perceptionSkill: Int
) = CharacterDerivedValues(
    initiative = optionalValues.initiative ?: dexterityModifier,
    armorClass = optionalValues.armorClass ?: calculateBaseArmorClass(dexterityModifier),
    passivePerception = 10 + perceptionSkill
)

fun CharacterFull.calculateSpeedValues(): SpeedValues {
    return mainEntities
        .filter { it.entity.entity.type == DnDEntityTypes.RACE }
        .mapNotNull { it.entity.race?.speedValues }
        .maxByOrNull { it.walk }
        ?: SpeedValues(
            walk = 0,
            swim = 0,
            fly = 0,
            climb = 0
        )
}

fun CharacterFull.calculateBaseArmorClass(dexterityModifier: Int): Int {
    val equippedArmor = items
        .firstOrNull { it.equipped && it.item.item?.armor != null }
        ?.item?.item?.armor

    return calculateArmorClass(dexterityModifier, equippedArmor)
}