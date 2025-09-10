package com.davanok.dvnkdnd.data.model.entities.dndModifiers

import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.database.entities.character.CharacterStats

data class DnDAttributesGroup(
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int
) {
    fun toMap() = mapOf(
        Attributes.STRENGTH to strength,
        Attributes.DEXTERITY to dexterity,
        Attributes.CONSTITUTION to constitution,
        Attributes.INTELLIGENCE to intelligence,
        Attributes.WISDOM to wisdom,
        Attributes.CHARISMA to charisma,
    )
    fun modifiers() = listOf(
        strength,
        dexterity,
        constitution,
        intelligence,
        wisdom,
        charisma
    )

    operator fun get(stat: Attributes) = when(stat) {
        Attributes.STRENGTH -> strength
        Attributes.DEXTERITY -> dexterity
        Attributes.CONSTITUTION -> constitution
        Attributes.INTELLIGENCE -> intelligence
        Attributes.WISDOM -> wisdom
        Attributes.CHARISMA -> charisma
    }
    fun set(stat: Attributes, value: Int) = when (stat) {
        Attributes.STRENGTH -> copy(strength = value)
        Attributes.DEXTERITY -> copy(dexterity = value)
        Attributes.CONSTITUTION -> copy(constitution = value)
        Attributes.INTELLIGENCE -> copy(intelligence = value)
        Attributes.WISDOM -> copy(wisdom = value)
        Attributes.CHARISMA -> copy(charisma = value)
    }
    operator fun plus(other: DnDAttributesGroup) = DnDAttributesGroup(
        strength = strength + other.strength,
        dexterity = dexterity + other.dexterity,
        constitution = constitution + other.constitution,
        intelligence = intelligence + other.intelligence,
        wisdom = wisdom + other.wisdom,
        charisma = charisma + other.charisma
    )
    companion object {
        val Default = DnDAttributesGroup(10, 10, 10, 10, 10, 10)
    }
}
fun List<DnDAttributeModifier>.toDnDAttributesGroup(): DnDAttributesGroup {
    val floatCounts = FloatArray(Attributes.entries.size)
    fastForEach { bonus ->
        floatCounts[bonus.attribute.ordinal] += bonus.value
    }
    val counts = floatCounts.map { it.toInt() }
    return DnDAttributesGroup(
        strength = counts[Attributes.STRENGTH.ordinal],
        dexterity = counts[Attributes.DEXTERITY.ordinal],
        constitution = counts[Attributes.CONSTITUTION.ordinal],
        intelligence = counts[Attributes.INTELLIGENCE.ordinal],
        wisdom = counts[Attributes.WISDOM.ordinal],
        charisma = counts[Attributes.CHARISMA.ordinal]
    )
}
fun CharacterStats.toAttributesGroup() = DnDAttributesGroup(
    strength = strength,
    dexterity = dexterity,
    constitution = constitution,
    intelligence = intelligence,
    wisdom = wisdom,
    charisma = charisma
)