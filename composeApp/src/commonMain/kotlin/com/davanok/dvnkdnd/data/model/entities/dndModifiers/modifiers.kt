package com.davanok.dvnkdnd.data.model.entities.dndModifiers

import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifierBonus
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DnDModifierBonus(
    val id: Uuid,
    val selectable: Boolean,
    val stat: Stats,
    val modifier: Int
)
fun EntityModifierBonus.toDnDModifier() = DnDModifierBonus(
    id = id,
    selectable = selectable,
    stat = stat,
    modifier = modifier
)
fun DnDModifierBonus.toEntityModifierBonus(entityId: Uuid) = EntityModifierBonus(
    id = id,
    entityId = entityId,
    selectable = selectable,
    stat = stat,
    modifier = modifier
)
data class DnDModifiersGroup(
    val strength: Int,
    val dexterity: Int,
    val constitution: Int,
    val intelligence: Int,
    val wisdom: Int,
    val charisma: Int
) {
    fun toMap() = mapOf(
        Stats.STRENGTH to strength,
        Stats.DEXTERITY to dexterity,
        Stats.CONSTITUTION to constitution,
        Stats.INTELLIGENCE to intelligence,
        Stats.WISDOM to wisdom,
        Stats.CHARISMA to charisma,
    )
    fun modifiers() = listOf(
        strength,
        dexterity,
        constitution,
        intelligence,
        wisdom,
        charisma
    )

    operator fun get(stat: Stats) = when(stat) {
        Stats.STRENGTH -> strength
        Stats.DEXTERITY -> dexterity
        Stats.CONSTITUTION -> constitution
        Stats.INTELLIGENCE -> intelligence
        Stats.WISDOM -> wisdom
        Stats.CHARISMA -> charisma
    }
    fun set(stat: Stats, value: Int) = when (stat) {
        Stats.STRENGTH -> copy(strength = value)
        Stats.DEXTERITY -> copy(dexterity = value)
        Stats.CONSTITUTION -> copy(constitution = value)
        Stats.INTELLIGENCE -> copy(intelligence = value)
        Stats.WISDOM -> copy(wisdom = value)
        Stats.CHARISMA -> copy(charisma = value)
    }
    operator fun plus(other: DnDModifiersGroup) = DnDModifiersGroup(
        strength = strength + other.strength,
        dexterity = dexterity + other.dexterity,
        constitution = constitution + other.constitution,
        intelligence = intelligence + other.intelligence,
        wisdom = wisdom + other.wisdom,
        charisma = charisma + other.charisma
    )
    companion object {
        val Default = DnDModifiersGroup(10, 10, 10, 10, 10, 10)
    }
}
fun List<DnDModifierBonus>.toDnDModifiersGroup(): DnDModifiersGroup {
    val counts = IntArray(Stats.entries.size)
    fastForEach { bonus ->
        counts[bonus.stat.ordinal] += bonus.modifier
    }
    return DnDModifiersGroup(
        strength = counts[Stats.STRENGTH.ordinal],
        dexterity = counts[Stats.DEXTERITY.ordinal],
        constitution = counts[Stats.CONSTITUTION.ordinal],
        intelligence = counts[Stats.INTELLIGENCE.ordinal],
        wisdom = counts[Stats.WISDOM.ordinal],
        charisma = counts[Stats.CHARISMA.ordinal]
    )
}
fun CharacterStats.toModifiersGroup() = DnDModifiersGroup(
    strength = strength,
    dexterity = dexterity,
    constitution = constitution,
    intelligence = intelligence,
    wisdom = wisdom,
    charisma = charisma
)