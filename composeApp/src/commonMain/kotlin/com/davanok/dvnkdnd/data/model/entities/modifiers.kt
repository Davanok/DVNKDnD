package com.davanok.dvnkdnd.data.model.entities

import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
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
    fun toModifiersList(): List<DnDModifierBonus> {
        return listOf(
            DnDModifierBonus(Uuid.NIL, false, Stats.STRENGTH, strength),
            DnDModifierBonus(Uuid.NIL, false, Stats.DEXTERITY, dexterity),
            DnDModifierBonus(Uuid.NIL, false, Stats.CONSTITUTION, constitution),
            DnDModifierBonus(Uuid.NIL, false, Stats.INTELLIGENCE, intelligence),
            DnDModifierBonus(Uuid.NIL, false, Stats.WISDOM, wisdom),
            DnDModifierBonus(Uuid.NIL, false, Stats.CHARISMA, charisma),
        )
    }
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
    companion object {
        val Default = DnDModifiersGroup(10, 10, 10, 10, 10, 10)
    }
}
fun CharacterStats.toModifiersGroup() = DnDModifiersGroup(
    strength = strength,
    dexterity = dexterity,
    constitution = constitution,
    intelligence = intelligence,
    wisdom = wisdom,
    charisma = charisma
)