package com.davanok.dvnkdnd.domain.entities.character

import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierDerivedValuesTargets


data class CharacterDerivedValues(
    val initiative: Int,
    val armorClass: Int,
    val passivePerception: Int
)

fun CharacterDerivedValues.toMap() = mapOf(
    DnDModifierDerivedValuesTargets.INITIATIVE to initiative,
    DnDModifierDerivedValuesTargets.ARMOR_CLASS to armorClass,
    DnDModifierDerivedValuesTargets.PASSIVE_PERCEPTION to passivePerception
)
fun Map<DnDModifierDerivedValuesTargets, Int>.toCharacterDerivedValues() = CharacterDerivedValues(
    initiative = get(DnDModifierDerivedValuesTargets.INITIATIVE) ?: 0,
    armorClass = get(DnDModifierDerivedValuesTargets.ARMOR_CLASS) ?: 0,
    passivePerception = get(DnDModifierDerivedValuesTargets.PASSIVE_PERCEPTION) ?: 0
)