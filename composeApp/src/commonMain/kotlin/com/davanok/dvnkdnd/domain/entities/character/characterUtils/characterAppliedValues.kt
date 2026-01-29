package com.davanok.dvnkdnd.domain.entities.character.characterUtils

import com.davanok.dvnkdnd.domain.dnd.calculateArmorClass
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterDerivedStats
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterModifiedValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterSpeed
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.entities.dndModifiers.map
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toSkillsGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierDerivedStatTargets
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierHealthTargets
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierSpeedTargets
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import io.github.aakira.napier.Napier

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
    // 1. Resolve Map-based Groups (Attributes, Skills)
    val modifiedAttributes = calculateAttributeGroup()
    val modifiedSavingThrows = calculateSavingThrows(modifiedAttributes)
    val modifiedSkills = calculateSkills(modifiedAttributes)

    // 2. Resolve other
    val modifiedHealth = calculateModifiedHealth()
    val derivedStats = calculateModifiedDerivedStats(
        dexterityAttribute = modifiedAttributes.dexterity,
        perceptionSkill = modifiedSkills.perception
    )
    val speed = calculateSpeedValues()

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

private fun CharacterFull.calculateAttributeGroup() = applyModifiersInfo(
    values = attributes.toMap(),
    modifiers = appliedModifiers[ModifierValueTarget.ATTRIBUTE].orEmpty()
).toAttributesGroup()

private fun CharacterFull.calculateSavingThrows(modifiedAttributes: AttributesGroup) = applyModifiersInfo(
    values = modifiedAttributes.map { calculateModifier(it) }.toMap(),
    modifiers = appliedModifiers[ModifierValueTarget.SAVING_THROW].orEmpty()
).toAttributesGroup()

private fun CharacterFull.calculateSkills(modifiedAttributes: AttributesGroup) = applyModifiersInfo(
    values = modifiedAttributes.toSkillsGroup().toMap().mapValues { calculateModifier(it.value) },
    modifiers = appliedModifiers[ModifierValueTarget.SKILL].orEmpty()
).toSkillsGroup()

private fun CharacterFull.calculateModifiedHealth(): CharacterHealth {
    val healthMap = mapOf(
        DnDModifierHealthTargets.CURRENT to health.current,
        DnDModifierHealthTargets.MAX to health.max
    )
    val modified = applyModifiersInfo(
        values = healthMap,
        modifiers = appliedModifiers[ModifierValueTarget.HEALTH].orEmpty()
    )
    return health.copy(
        current = modified.getValue(DnDModifierHealthTargets.CURRENT),
        max = modified.getValue(DnDModifierHealthTargets.MAX)
    )
}

private fun CharacterFull.calculateModifiedDerivedStats(
    dexterityAttribute: Int,
    perceptionSkill: Int
): CharacterDerivedStats {
    val statsMap = mapOf(
        DnDModifierDerivedStatTargets.ARMOR_CLASS to (optionalValues.armorClass ?: calculateBaseArmorClass()),
        DnDModifierDerivedStatTargets.INITIATIVE to (optionalValues.initiative ?: calculateModifier(dexterityAttribute)),
        DnDModifierDerivedStatTargets.PASSIVE_PERCEPTION to (10 + perceptionSkill)
    )
    val modified = applyModifiersInfo(
        values = statsMap,
        modifiers = appliedModifiers[ModifierValueTarget.DERIVED_STAT].orEmpty()
    )
    return CharacterDerivedStats(
        initiative = modified.getValue(DnDModifierDerivedStatTargets.ARMOR_CLASS),
        armorClass = modified.getValue(DnDModifierDerivedStatTargets.INITIATIVE),
        passivePerception = modified.getValue(DnDModifierDerivedStatTargets.PASSIVE_PERCEPTION)
    )
}
private fun CharacterFull.calculateSpeedValues(): CharacterSpeed {
    val baseSpeed = calculateBaseSpeed()
    val speedMap = mapOf(
        DnDModifierSpeedTargets.WALK to baseSpeed,
        DnDModifierSpeedTargets.FLY to 0,
        DnDModifierSpeedTargets.SWIM to baseSpeed / 2,
        DnDModifierSpeedTargets.CLIMB to 0,
    )
    val modified = applyModifiersInfo(
        values = speedMap,
        modifiers = appliedModifiers[ModifierValueTarget.SPEED].orEmpty()
    )
    return CharacterSpeed(
        walk = modified.getValue(DnDModifierSpeedTargets.WALK),
        fly = modified.getValue(DnDModifierSpeedTargets.FLY),
        swim = modified.getValue(DnDModifierSpeedTargets.SWIM),
        climb = modified.getValue(DnDModifierSpeedTargets.CLIMB)
    )
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
        Napier.d { current.toString() }
        val key = modifier.targetAs<K>() ?: return@forEach
        val base = current[key] ?: return@forEach

        current[key] = modifier.applyForValue(base)
    }
    return current
}
