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
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierArmorClassTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierHealthTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierInitiativeTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierValueSource




private inline fun <reified K : Enum<K>> processGroups(
    modifierGroups: List<ModifiersGroup>,
    baseValues: MutableMap<K, Int>,
    groupValueProvider: (ModifiersGroup) -> Double
) {
    modifierGroups.forEach { group ->
        val value = groupValueProvider(group)
        group.modifiers.forEach modifier@{ modifier ->
            val target = modifier.targetAs<K>() ?: return@modifier
            val current = baseValues[target] ?: return@modifier

            if (group.minBaseValue?.let { current < it } == true) return@modifier
            if (group.maxBaseValue?.let { current > it } == true) return@modifier

            val result = applyOperation(current, value, group.operation)

            baseValues[target] = result.coerceIn(group.clampMin, group.clampMax)
        }
    }
}

private inline fun <reified T : Enum<T>> applyCustomModifiers(
    modifiers: List<CustomModifier>,
    values: MutableMap<T, Int>,
    valueSourcesProvider: (DnDModifierValueSource, String?, Double) -> Double
) {
    modifiers.forEach { modifier ->
        val target = modifier.targetAs<T>() ?: return@forEach
        val current = values[target] ?: return@forEach

        val value = valueSourcesProvider(modifier.valueSource, modifier.valueSourceTarget, modifier.value)
        val result = applyOperation(
            base = current,
            value = value,
            operation = modifier.operation
        )

        values[target] = result
    }
}


fun CharacterFull.getAppliedValues(): CharacterModifiedValues {
    val targetGroups = entities
        .flatMap { it.modifiersGroups }
        .groupBy { it.target }
        .mapValues { (_, value) -> value.sortedBy { it.priority } }

    val customModifiersGrouped = customModifiers
        .groupBy { it.targetGlobal }
        .mapValues { (_, value) -> value.sortedBy { it.targetGlobal } }

    val customModifierValueSourceProvider: (DnDModifierValueSource, String?, Double) -> Double = { source, target, value ->
        resolveValueSource(source, target, null, value)
    }

    val attributeValues = attributes.toMap().toMutableMap()
    targetGroups[DnDModifierTargetType.ATTRIBUTE]?.let {
        processGroups(
            it,
            attributeValues,
            ::resolveGroupValue
        )
    }
    customModifiersGrouped[DnDModifierTargetType.ATTRIBUTE]?.let {
        applyCustomModifiers(it, attributeValues, customModifierValueSourceProvider)
    }
    val modifiedAttributes = attributeValues.toAttributesGroup()

    val savingThrowsValues = attributes
        .toMap()
        .mapValues { calculateModifier(it.value) }
        .toMutableMap()

    targetGroups[DnDModifierTargetType.SAVING_THROW]?.let {
        processGroups(
            it,
            savingThrowsValues,
            ::resolveGroupValue
        )
    }
    customModifiersGrouped[DnDModifierTargetType.SAVING_THROW]?.let {
        applyCustomModifiers(it, savingThrowsValues, customModifierValueSourceProvider)
    }
    val modifiedSavingThrows = savingThrowsValues.toAttributesGroup()

    val skillValues = attributes
        .toSkillsGroup()
        .toMap()
        .mapValues { calculateModifier(it.value) }
        .toMutableMap()

    targetGroups[DnDModifierTargetType.SKILL]?.let {
        processGroups(
            it,
            skillValues,
            ::resolveGroupValue
        )
    }
    customModifiersGrouped[DnDModifierTargetType.SKILL]?.let {
        applyCustomModifiers(it, skillValues, customModifierValueSourceProvider)
    }
    val modifiedSkills = skillValues.toSkillsGroup()

    val currentMaxHealth = health.max + calculateModifier(attributes[Attributes.CONSTITUTION])
    val healthValues = mutableMapOf(
        DnDModifierHealthTarget.CURRENT to health.current,
        DnDModifierHealthTarget.MAX to currentMaxHealth
    )

    targetGroups[DnDModifierTargetType.HEALTH]?.let {
        processGroups(
            it,
            healthValues,
            ::resolveGroupValue
        )
    }
    customModifiersGrouped[DnDModifierTargetType.HEALTH]?.let {
        applyCustomModifiers(it, healthValues, customModifierValueSourceProvider)
    }
    val modifiedHealth = health.copy(
        current = healthValues.getOrElse(DnDModifierHealthTarget.CURRENT) { health.current },
        max = healthValues.getOrElse(DnDModifierHealthTarget.MAX) { health.max }
    )

    val defaultArmorClass = optionalValues.armorClass ?: calculateArmorClass(
        attributes[Attributes.DEXTERITY],
        items.firstOrNull { it.equipped && it.item.item?.armor != null }?.item?.item?.armor
    )
    val armorClassValues = mutableMapOf(
        DnDModifierArmorClassTarget.ARMOR_CLASS to defaultArmorClass
    )
    targetGroups[DnDModifierTargetType.ARMOR_CLASS]?.let {
        processGroups(
            it,
            armorClassValues,
            ::resolveGroupValue
        )
    }
    val armorClass = armorClassValues[DnDModifierArmorClassTarget.ARMOR_CLASS] ?: 0

    val defaultInitiative = optionalValues.initiative ?: calculateModifier(attributes[Attributes.DEXTERITY])
    val initiativeValues = mutableMapOf(
        DnDModifierInitiativeTarget.INITIATIVE to defaultInitiative
    )
    targetGroups[DnDModifierTargetType.INITIATIVE]?.let {
        processGroups(
            it,
            initiativeValues,
            ::resolveGroupValue
        )
    }
    val initiative = initiativeValues[DnDModifierInitiativeTarget.INITIATIVE] ?: 0

    return CharacterModifiedValues(
        attributes = modifiedAttributes,
        savingThrowModifiers = modifiedSavingThrows,
        skillModifiers = modifiedSkills,
        health = modifiedHealth,
        armorClass = armorClass,
        initiative = initiative
    )
}