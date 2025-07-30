package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SpellSlots(
    val id: Uuid,
    val level: Int,
    @SerialName("prepared_spells")
    val preparedSpells: Int?,
    val cantrips: Int?,
    @SerialName("spell_slots")
    val spellSlots: List<Int>
)
fun SpellSlots.toClassSpellSlots(classId: Uuid) = ClassSpellSlots(
    id = id,
    classId = classId,
    level = level,
    preparedSpells = preparedSpells,
    cantrips = cantrips,
    spellSlots = spellSlots
)
fun ClassSpellSlots.toSpellSlots() = SpellSlots(
    id = id,
    level = level,
    preparedSpells = preparedSpells,
    cantrips = cantrips,
    spellSlots = spellSlots
)

@Serializable
data class ClassWithSpells(
    @SerialName("main_stat")
    val mainStats: List<Stats>,
    @SerialName("hit_dice")
    val hitDice: Dices,

    val spells: List<Uuid>,
    val slots: List<SpellSlots>,
) {
    fun toDnDClass(entityId: Uuid) = DnDClass(entityId, mainStats, hitDice)
}
