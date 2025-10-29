package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
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

@Serializable
data class ClassWithSpells(
    @SerialName("primary_stats")
    val primaryStats: List<Attributes>,
    @SerialName("hit_dice")
    val hitDice: Dices,

    val spells: List<Uuid>,
    val slots: List<SpellSlots>,
)
