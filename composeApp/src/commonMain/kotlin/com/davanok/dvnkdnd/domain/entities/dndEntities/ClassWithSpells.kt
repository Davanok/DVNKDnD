package com.davanok.dvnkdnd.domain.entities.dndEntities

import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.CasterProgression
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices
import com.davanok.dvnkdnd.domain.enums.dndEnums.TimeUnits
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class SpellSlotsType(
    val id: Uuid,
    @SerialName("user_id")
    val userId: Uuid? = null,
    val name: String,
    val regain: TimeUnits?,
    val spell: Boolean
)

@Serializable
data class SpellSlots(
    val id: Uuid,
    val type: SpellSlotsType,
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
    val caster: CasterProgression,

    val spells: List<Uuid>,
    val slots: List<SpellSlots>
)
