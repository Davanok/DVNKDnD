package com.davanok.dvnkdnd.domain.entities.character.characterUtils

import com.davanok.dvnkdnd.core.utils.applyOperation
import com.davanok.dvnkdnd.domain.dnd.calculateArmorClass
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterModifiedValues
import com.davanok.dvnkdnd.domain.entities.character.CustomModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toSkillsGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierHealthTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierValueSource


fun CharacterFull.getAppliedValues(): CharacterModifiedValues {
    val groupIdToEntityId =
        entities.flatMap { e -> e.modifiersGroups.map { it.id to e.entity.id } }.toMap()

    val resolveGroupValue: (group: ModifiersGroup) -> Double = { group ->
        resolveValueSource(
            group.valueSource,
            group.valueSourceTarget,
            groupIdToEntityId[group.id],
            group.value
        )
    }

    // 1. Pre-calculate and group modifiers once to avoid repeated iteration
    val targetGroups = entities.asSequence()
        .flatMap { it.modifiersGroups }
        .filter { it.id in selectedModifiers }
        .sortedBy { it.priority }
        .groupBy { it.target }

    val customModifiersGrouped = customModifiers.asSequence()
        .sortedBy { it.priority }
        .groupBy { it.targetGlobal }

    // Helper to resolve dynamic values for CustomModifiers
    val valueSourceProvider: (DnDModifierValueSource, String?, Double) -> Double =
        { source, target, value ->
            resolveValueSource(source, target, null, value)
        }

    // --- Attributes ---
    // Use EnumMap for performance when keys are Enums
    val attributeValues = attributes
        .toMap()
        .toMutableMap()

    applyModifiers(
        targetType = DnDModifierTargetType.ATTRIBUTE,
        values = attributeValues,
        modifierGroups = targetGroups,
        customModifiers = customModifiersGrouped,
        groupValueProvider = resolveGroupValue,
        customValueProvider = valueSourceProvider
    )
    val modifiedAttributes = attributeValues.toAttributesGroup()

    // --- Saving Throws ---
    // Base saves are the modifiers of the base attributes
    val savingThrowsValues = attributes
        .toMap()
        .mapValues { calculateModifier(it.value) }
        .toMutableMap()

    applyModifiers(
        targetType = DnDModifierTargetType.SAVING_THROW,
        values = savingThrowsValues,
        modifierGroups = targetGroups,
        customModifiers = customModifiersGrouped,
        groupValueProvider = resolveGroupValue,
        customValueProvider = valueSourceProvider
    )
    val modifiedSavingThrows = savingThrowsValues.toAttributesGroup()

    // --- Skills ---
    // Base skills are the modifiers of the specific attribute they rely on
    val skillValues = attributes
        .toSkillsGroup()
        .toMap()
        .mapValues { calculateModifier(it.value) }
        .toMutableMap()

    applyModifiers(
        targetType = DnDModifierTargetType.SKILL,
        values = skillValues,
        modifierGroups = targetGroups,
        customModifiers = customModifiersGrouped,
        groupValueProvider = resolveGroupValue,
        customValueProvider = valueSourceProvider
    )
    val modifiedSkills = skillValues.toSkillsGroup()

    // --- Health ---
    val defaultMaxHealth = health.max + calculateModifier(attributes[Attributes.CONSTITUTION])
    val healthValues = mutableMapOf(
        DnDModifierHealthTarget.CURRENT to health.current,
        DnDModifierHealthTarget.MAX to defaultMaxHealth
    )

    applyModifiers(
        targetType = DnDModifierTargetType.HEALTH,
        values = healthValues,
        modifierGroups = targetGroups,
        customModifiers = customModifiersGrouped,
        groupValueProvider = resolveGroupValue,
        customValueProvider = valueSourceProvider
    )
    val modifiedHealth = health.copy(
        current = healthValues.getOrElse(DnDModifierHealthTarget.CURRENT) { health.current },
        max = healthValues.getOrElse(DnDModifierHealthTarget.MAX) { health.max }
    )

    // --- Armor Class ---
    val baseArmor = optionalValues.armorClass ?: calculateArmorClass(
        attributes[Attributes.DEXTERITY],
        items.firstOrNull { it.equipped && it.item.item?.armor != null }?.item?.item?.armor
    )

    val finalArmorClass = applySingleValueModifiers(
        targetType = DnDModifierTargetType.ARMOR_CLASS,
        baseValue = baseArmor,
        modifierGroups = targetGroups,
        customModifiers = customModifiersGrouped,
        groupValueProvider = resolveGroupValue,
        customValueProvider = valueSourceProvider
    )

    // --- Initiative ---
    val baseInitiative =
        optionalValues.initiative ?: calculateModifier(attributes[Attributes.DEXTERITY])

    val finalInitiative = applySingleValueModifiers(
        targetType = DnDModifierTargetType.INITIATIVE,
        baseValue = baseInitiative,
        modifierGroups = targetGroups,
        customModifiers = customModifiersGrouped,
        groupValueProvider = resolveGroupValue,
        customValueProvider = valueSourceProvider
    )

    // --- Speed ---
    val baseSpeed = optionalValues.speed ?: mainEntities
        .filter { it.entity.entity.type == DnDEntityTypes.RACE }
        .maxOfOrNull { it.entity.race?.speed ?: 0 } ?: 0

    val finalSpeed = applySingleValueModifiers(
        targetType = DnDModifierTargetType.SPEED,
        baseValue = baseSpeed,
        modifierGroups = targetGroups,
        customModifiers = customModifiersGrouped,
        groupValueProvider = resolveGroupValue,
        customValueProvider = valueSourceProvider
    )

    return CharacterModifiedValues(
        attributes = modifiedAttributes,
        savingThrowModifiers = modifiedSavingThrows,
        skillModifiers = modifiedSkills,
        health = modifiedHealth,
        armorClass = finalArmorClass,
        initiative = finalInitiative,
        speed = finalSpeed
    )
}

// -----------------------------------------------------------------------------
// Private Helper Functions (Generic & Consolidated)
// -----------------------------------------------------------------------------

/**
 * Applies both standard modifiers and custom modifiers to a Map of values (e.g. Attributes, Skills).
 */
private inline fun <reified K : Enum<K>> applyModifiers(
    targetType: DnDModifierTargetType,
    values: MutableMap<K, Int>,
    modifierGroups: Map<DnDModifierTargetType, List<ModifiersGroup>>,
    customModifiers: Map<DnDModifierTargetType, List<CustomModifier>>,
    crossinline groupValueProvider: (ModifiersGroup) -> Double,
    crossinline customValueProvider: (DnDModifierValueSource, String?, Double) -> Double
) {
    // 1. Standard Groups
    modifierGroups[targetType]?.forEach { group ->
        val groupValue = groupValueProvider(group)
        group.modifiers.forEach { modifier ->
            val targetKey = modifier.targetAs<K>() ?: return@forEach
            val currentVal = values[targetKey] ?: return@forEach

            if (shouldSkip(group, currentVal)) return@forEach

            val result = applyOperation(currentVal, groupValue, group.operation)
            values[targetKey] = result.coerceIn(group.clampMin, group.clampMax)
        }
    }

    // 2. Custom Modifiers
    customModifiers[targetType]?.forEach { modifier ->
        val targetKey = modifier.targetAs<K>() ?: return@forEach
        val currentVal = values[targetKey] ?: return@forEach

        val modValue =
            customValueProvider(modifier.valueSource, modifier.valueSourceTarget, modifier.value)
        val result = applyOperation(currentVal, modValue, modifier.operation)
        values[targetKey] = result
    }
}

/**
 * Applies both standard modifiers and custom modifiers to a single Integer value (e.g. Speed, AC).
 */
private inline fun applySingleValueModifiers(
    targetType: DnDModifierTargetType,
    baseValue: Int,
    modifierGroups: Map<DnDModifierTargetType, List<ModifiersGroup>>,
    customModifiers: Map<DnDModifierTargetType, List<CustomModifier>>,
    crossinline groupValueProvider: (ModifiersGroup) -> Double,
    crossinline customValueProvider: (DnDModifierValueSource, String?, Double) -> Double
): Int {
    var current = baseValue

    // 1. Standard Groups
    modifierGroups[targetType]?.forEach { group ->
        val groupValue = groupValueProvider(group)
        group.modifiers.forEach { _ ->
            // Note: Single value modifiers usually don't have a specific 'target' string to parse
            if (shouldSkip(group, current)) return@forEach

            val result = applyOperation(current, groupValue, group.operation)
            current = result.coerceIn(group.clampMin, group.clampMax)
        }
    }

    // 2. Custom Modifiers
    customModifiers[targetType]?.forEach { modifier ->
        val modValue =
            customValueProvider(modifier.valueSource, modifier.valueSourceTarget, modifier.value)
        current = applyOperation(current, modValue, modifier.operation)
    }

    return current
}

private fun shouldSkip(group: ModifiersGroup, currentValue: Int): Boolean =
    (group.minBaseValue != null && currentValue < group.minBaseValue) || (group.maxBaseValue != null && currentValue > group.maxBaseValue)
