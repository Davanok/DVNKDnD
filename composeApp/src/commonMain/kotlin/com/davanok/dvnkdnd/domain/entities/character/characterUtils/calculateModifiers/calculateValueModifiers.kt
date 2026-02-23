package com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers

import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterModifiedValues
import com.davanok.dvnkdnd.domain.entities.character.toCharacterDerivedValues
import com.davanok.dvnkdnd.domain.entities.character.toCharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.toMap
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.entities.dndModifiers.mapValues
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toSkillsGroup
import com.davanok.dvnkdnd.domain.entities.toMap
import com.davanok.dvnkdnd.domain.entities.toSpeedValues
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.CharacterMovementType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierDerivedValuesTargets
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierHealthTargets
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills


fun CharacterFull.calculateValueModifiers(): CalculatedModifiersResult {
    val comparator = compareBy<RawModifierWrapper> { it.modifier.priority }
        .thenBy { it.modifier.operation }

    val baseModifiers = getBaseValueModifiers()
    val rawModifiers = collectRawValueModifiers()

    val allRawModifiers = (baseModifiers + rawModifiers)
        .groupBy { it.modifier.targetScope }
        .mapValues { it.value.sortedWith(comparator) }

    val resultValues = buildBaseMap()

    // This call now resolves Attributes -> then Skills -> then AC/Speed
    // because of the Enum order and priorities.
    val resolvedModifiers = resolveRawValueModifiersInfo(resultValues, allRawModifiers)

    // These defaults now only act as fallbacks if no modifiers exist at all
    val defaultValues = buildDefaultValues()

    return CalculatedModifiersResult(
        values = resultValues.toCharacterModifiedValues(defaultValues),
        modifiers = resolvedModifiers
    )
}

private fun CharacterFull.buildBaseMap(): MutableMap<ModifierValueTarget, MutableMap<String?, Int>> {
    val attributeValues: MutableMap<String?, Int> = attributes.toMap().mapKeys { it.key.name }.toMutableMap()
    val speedValues: MutableMap<String?, Int> = calculateSpeedValues().toMap().mapKeys { it.key.name }.toMutableMap()

    return mutableMapOf(
        ModifierValueTarget.ATTRIBUTE to attributeValues,
        ModifierValueTarget.SPEED to speedValues
    )
}

data class CalculatedModifiersResult(
    val values: CharacterModifiedValues,
    val modifiers: Map<ModifierValueTarget, List<ValueModifierInfo>>
)

private fun CharacterFull.buildDefaultValues(): CharacterModifiedValues {
    val attributeModifiers = attributes.mapValues { calculateModifier(it) }

    return CharacterModifiedValues(
        attributes = attributes,
        attributeThrows = attributeModifiers,
        savingThrowModifiers = attributeModifiers,
        skillModifiers = attributeModifiers.toSkillsGroup(),
        health = health,
        derivedValues = calculateDerivedValues(
            attributeModifiers.dexterity,
            attributeModifiers.wisdom
        ),
        speed = calculateSpeedValues(),
    )
}

private fun Map<ModifierValueTarget, Map<String?, Int>>.toCharacterModifiedValues(
    defaultValues: CharacterModifiedValues
) = CharacterModifiedValues(
    attributes = get(ModifierValueTarget.ATTRIBUTE).merge(
        defaultValues.attributes.toMap(),
        Map<Attributes, Int>::toAttributesGroup
    ),

    attributeThrows = get(ModifierValueTarget.ATTRIBUTE_THROW).merge(
        defaultValues.attributeThrows.toMap(),
        Map<Attributes, Int>::toAttributesGroup
    ),

    savingThrowModifiers = get(ModifierValueTarget.SAVING_THROW).merge(
        defaultValues.savingThrowModifiers.toMap(),
        Map<Attributes, Int>::toAttributesGroup
    ),

    skillModifiers = get(ModifierValueTarget.SKILL).merge(
        defaultValues.skillModifiers.toMap(),
        Map<Skills, Int>::toSkillsGroup
    ),

    health = get(ModifierValueTarget.HEALTH).merge(
        defaultValues.health.toMap(),
        Map<DnDModifierHealthTargets, Int>::toCharacterHealth
    ),

    derivedValues = get(ModifierValueTarget.DERIVED_STAT).merge(
        defaultValues.derivedValues.toMap(),
        Map<DnDModifierDerivedValuesTargets, Int>::toCharacterDerivedValues
    ),

    speed = get(ModifierValueTarget.SPEED).merge(
        defaultValues.speed.toMap(),
        Map<CharacterMovementType, Int>::toSpeedValues
    )
)

private inline fun <K : Enum<K>, V, R> Map<String?, V>?.merge(
    defaults: Map<K, V>,
    crossinline toResult: Map<K, V>.() -> R
): R = this
    ?.let { overrides ->
        defaults.mapValues { (key, value) ->
            overrides[key.name] ?: value
        }.toResult()
    }
    ?: defaults.toResult()
