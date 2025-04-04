package com.davanok.dvnkdnd.data.model.entities

import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.database.entities.character.CharacterStats
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier

data class DnDModifier(
    val selectable: Boolean,
    val stat: Stats,
    val modifier: Int
)
fun EntityModifier.toDnDModifier() = DnDModifier(
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
    fun toModifiersList(): List<DnDModifier> {
        return listOf(
            DnDModifier(false, Stats.STRENGTH, strength),
            DnDModifier(false, Stats.DEXTERITY, dexterity),
            DnDModifier(false, Stats.CONSTITUTION, constitution),
            DnDModifier(false, Stats.INTELLIGENCE, intelligence),
            DnDModifier(false, Stats.WISDOM, wisdom),
            DnDModifier(false, Stats.CHARISMA, charisma),
        )
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