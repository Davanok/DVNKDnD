package com.davanok.dvnkdnd.domain.entities.character.characterUtils

import com.davanok.dvnkdnd.core.utils.applyOperation
import com.davanok.dvnkdnd.domain.dnd.calculateArmorClass
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterModifiedValues
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifierExtendedInfo
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toSkillsGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierHealthTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierTargetType

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
    // 1. Resolve Map-based Groups (Attributes, Saves, Skills)
    val modifiedAttributes = calculateAttributeGroup()
    val modifiedSavingThrows = calculateSavingThrows()
    val modifiedSkills = calculateSkills()

    // 2. Resolve Health
    val modifiedHealth = calculateModifiedHealth()

    // 3. Resolve Single Values (AC, Initiative, Speed)
    val armorClass = calculateTargetValue(
        target = DnDModifierTargetType.ARMOR_CLASS,
        base = optionalValues.armorClass ?: calculateBaseArmorClass()
    )

    val initiative = calculateTargetValue(
        target = DnDModifierTargetType.INITIATIVE,
        base = optionalValues.initiative ?: calculateModifier(attributes[Attributes.DEXTERITY])
    )

    val speed = calculateTargetValue(
        target = DnDModifierTargetType.SPEED,
        base = optionalValues.speed ?: calculateBaseSpeed()
    )

    return CharacterModifiedValues(
        attributes = modifiedAttributes,
        savingThrowModifiers = modifiedSavingThrows,
        skillModifiers = modifiedSkills,
        health = modifiedHealth,
        armorClass = armorClass,
        initiative = initiative,
        speed = speed
    )
}

// --- Internal Helper Extensions ---

private fun CharacterFull.calculateAttributeGroup() = applyModifiersInfo(
    values = attributes.toMap(),
    modifiers = appliedModifiers[DnDModifierTargetType.ATTRIBUTE].orEmpty()
).toAttributesGroup()

private fun CharacterFull.calculateSavingThrows() = applyModifiersInfo(
    values = attributes.toMap().mapValues { calculateModifier(it.value) },
    modifiers = appliedModifiers[DnDModifierTargetType.SAVING_THROW].orEmpty()
).toAttributesGroup()

private fun CharacterFull.calculateSkills() = applyModifiersInfo(
    values = attributes.toSkillsGroup().toMap().mapValues { calculateModifier(it.value) },
    modifiers = appliedModifiers[DnDModifierTargetType.SKILL].orEmpty()
).toSkillsGroup()

private fun CharacterFull.calculateModifiedHealth(): CharacterHealth {
    val healthMap = mapOf(
        DnDModifierHealthTarget.CURRENT to health.current,
        DnDModifierHealthTarget.MAX to health.max
    )
    val modified = applyModifiersInfo(
        values = healthMap,
        modifiers = appliedModifiers[DnDModifierTargetType.HEALTH].orEmpty()
    )
    return health.copy(
        current = modified.getOrElse(DnDModifierHealthTarget.CURRENT) { health.current },
        max = modified.getOrElse(DnDModifierHealthTarget.MAX) { health.max }
    )
}

private fun CharacterFull.calculateTargetValue(target: DnDModifierTargetType, base: Int): Int {
    return applySingleValueModifiersInfo(base, appliedModifiers[target].orEmpty())
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

private inline fun <reified K : Enum<K>> applyModifiersInfo(
    values: Map<K, Int>,
    modifiers: List<ModifierExtendedInfo>
): Map<K, Int> {
    val current = values.toMutableMap()
    modifiers.forEach { modifier ->
        val key = modifier.targetAs<K>() ?: return@forEach
        val base = current[key] ?: return@forEach

        if (modifier.shouldSkip(base)) return@forEach

        val result = applyOperation(base, modifier.resolvedValue, modifier.operation)
        current[key] = result.coerceIn(modifier.clampMin, modifier.clampMax)
    }
    return current
}

private fun applySingleValueModifiersInfo(
    baseValue: Int,
    modifiers: List<ModifierExtendedInfo>
): Int {
    return modifiers.fold(baseValue) { acc, mod ->
        if (mod.shouldSkip(acc)) acc
        else applyOperation(acc, mod.resolvedValue, mod.operation).coerceIn(
            mod.clampMin,
            mod.clampMax
        )
    }
}

private fun ModifierExtendedInfo.shouldSkip(currentValue: Int): Boolean =
    (minBaseValue != null && currentValue < minBaseValue) ||
            (maxBaseValue != null && currentValue > maxBaseValue)
