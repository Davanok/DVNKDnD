package com.davanok.dvnkdnd.data.model.entities.dndModifiers

import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.database.entities.character.CharacterAttributes
import kotlinx.serialization.Serializable

@Serializable
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

    operator fun get(attribute: Attributes) = when(attribute) {
        Attributes.STRENGTH -> strength
        Attributes.DEXTERITY -> dexterity
        Attributes.CONSTITUTION -> constitution
        Attributes.INTELLIGENCE -> intelligence
        Attributes.WISDOM -> wisdom
        Attributes.CHARISMA -> charisma
    }
    fun set(attribute: Attributes, value: Int) = when (attribute) {
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
fun CharacterAttributes.toAttributesGroup() = DnDAttributesGroup(
    strength = strength,
    dexterity = dexterity,
    constitution = constitution,
    intelligence = intelligence,
    wisdom = wisdom,
    charisma = charisma
)
fun Map<Attributes, Int>.toAttributesGroup() = DnDAttributesGroup(
    strength = get(Attributes.STRENGTH) ?: 0,
    dexterity = get(Attributes.DEXTERITY) ?: 0,
    constitution = get(Attributes.CONSTITUTION) ?: 0,
    intelligence = get(Attributes.INTELLIGENCE) ?: 0,
    wisdom = get(Attributes.WISDOM) ?: 0,
    charisma = get(Attributes.CHARISMA) ?: 0,
)