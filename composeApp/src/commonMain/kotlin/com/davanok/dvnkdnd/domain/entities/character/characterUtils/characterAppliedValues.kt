package com.davanok.dvnkdnd.domain.entities.character.characterUtils

import com.davanok.dvnkdnd.domain.dnd.calculateArmorClass
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterDerivedValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterModifiedValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterSpeed
import com.davanok.dvnkdnd.domain.entities.character.toCharacterDerivedValues
import com.davanok.dvnkdnd.domain.entities.character.toCharacterSpeed
import com.davanok.dvnkdnd.domain.entities.character.toMap
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.entities.dndModifiers.map
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toSkillsGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierHealthTargets
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget

/**
 * Computes the final, modified values for the character by applying all active modifiers
 * to their respective base stats.
 *
 * This includes primary attributes, derived stats (saving throws/skills), health,
 * and secondary combat stats like AC, Speed, and Initiative.
 *
 * @return A [CharacterModifiedValues] object containing the fully resolved state.
 */
fun CharacterFull.getAppliedValues(): CharacterModifiedValues {
    val characterModifiersByTarget = calculatedValueModifiers
        .groupBy { it.modifier.targetScope }

    // 1. Resolve Map-based Groups (Attributes, Skills)
    val modifiedAttributes = calculateAttributeGroup(characterModifiersByTarget[ModifierValueTarget.ATTRIBUTE].orEmpty())
    val modifiedSavingThrows = calculateSavingThrows(modifiedAttributes, characterModifiersByTarget[ModifierValueTarget.SAVING_THROW].orEmpty())
    val modifiedSkills = calculateSkills(modifiedAttributes, characterModifiersByTarget[ModifierValueTarget.SKILL].orEmpty())

    // 2. Resolve other
    val modifiedHealth = calculateModifiedHealth(characterModifiersByTarget[ModifierValueTarget.HEALTH].orEmpty())
    val derivedStats = calculateModifiedDerivedStats(
        dexterityAttribute = modifiedAttributes.dexterity,
        perceptionSkill = modifiedSkills.perception,
        modifiers = characterModifiersByTarget[ModifierValueTarget.DERIVED_STAT].orEmpty()
    )
    val speed = calculateSpeedValues(characterModifiersByTarget[ModifierValueTarget.SPEED].orEmpty())

    // TODO: implement other modifier value targets

    return CharacterModifiedValues(
        attributes = modifiedAttributes,
        savingThrowModifiers = modifiedSavingThrows,
        skillModifiers = modifiedSkills,
        health = modifiedHealth,
        derivedStats = derivedStats,
        speed = speed
    )
}

// --- Internal Helper Extensions ---

private fun CharacterFull.calculateAttributeGroup(modifiers: List<ValueModifierInfo>) = applyModifiersInfo(
    values = attributes.toMap(),
    modifiers = modifiers
).toAttributesGroup()

private fun calculateSavingThrows(modifiedAttributes: AttributesGroup, modifiers: List<ValueModifierInfo>) = applyModifiersInfo(
    values = modifiedAttributes.map { calculateModifier(it) }.toMap(),
    modifiers = modifiers
).toAttributesGroup()

private fun calculateSkills(modifiedAttributes: AttributesGroup, modifiers: List<ValueModifierInfo>) = applyModifiersInfo(
    values = modifiedAttributes.toSkillsGroup().toMap().mapValues { calculateModifier(it.value) },
    modifiers = modifiers
).toSkillsGroup()

private fun CharacterFull.calculateModifiedHealth(modifiers: List<ValueModifierInfo>): CharacterHealth {
    val healthMap = mapOf(
        DnDModifierHealthTargets.CURRENT to health.current,
        DnDModifierHealthTargets.MAX to health.max
    )
    val modified = applyModifiersInfo(
        values = healthMap,
        modifiers = modifiers
    )
    return health.copy(
        current = modified.getValue(DnDModifierHealthTargets.CURRENT),
        max = modified.getValue(DnDModifierHealthTargets.MAX)
    )
}

private fun CharacterFull.calculateModifiedDerivedStats(
    dexterityAttribute: Int,
    perceptionSkill: Int,
    modifiers: List<ValueModifierInfo>
): CharacterDerivedValues {
    val derivedValuesMap = CharacterDerivedValues(
        initiative = optionalValues.initiative ?: calculateModifier(dexterityAttribute),
        armorClass = optionalValues.armorClass ?: calculateBaseArmorClass(),
        passivePerception = 10 + perceptionSkill
    ).toMap()
    val modified = applyModifiersInfo(
        values = derivedValuesMap,
        modifiers = modifiers
    )
    return modified.toCharacterDerivedValues()
}
private fun CharacterFull.calculateSpeedValues(modifiers: List<ValueModifierInfo>): CharacterSpeed {
    val baseSpeed = calculateBaseSpeed()
    val speedMap = CharacterSpeed(
        walk = baseSpeed,
        swim = baseSpeed / 2,
        fly = 0,
        climb = 0
    ).toMap()
    val modified = applyModifiersInfo(
        values = speedMap,
        modifiers = modifiers
    )
    return modified.toCharacterSpeed()
}

private fun CharacterFull.calculateBaseArmorClass(): Int {
    val equippedArmor =
        items.firstOrNull { it.equipped && it.item.item?.armor != null }?.item?.item?.armor
    return calculateArmorClass(attributes[Attributes.DEXTERITY], equippedArmor)
}

private fun CharacterFull.calculateBaseSpeed(): Int {
    return mainEntities
        .filter { it.entity.entity.type == DnDEntityTypes.RACE }
        .maxOfOrNull { it.entity.race?.speed ?: 0 } ?: 0
}

// --- Logic Processors ---

inline fun <reified K : Enum<K>> applyModifiersInfo(
    values: Map<K, Int>,
    modifiers: List<ValueModifierInfo>
): Map<K, Int> {
    val current = values.toMutableMap()
    modifiers.forEach { modifier ->
        val key = modifier.targetAs<K>() ?: return@forEach
        val base = current[key] ?: return@forEach

        current[key] = modifier.applyForValue(base)
    }
    return current
}
