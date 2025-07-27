package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlot
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class ClassWithSpells(
    val id: Uuid,
    @SerialName("main_stat")
    val mainStat: Stats,
    @SerialName("hit_dice")
    val hitDice: Dices,

    val spells: List<ClassSpell>,
    val slots: List<ClassSpellSlot>,
) {
    fun toDnDClass() = DnDClass(id, mainStat, hitDice)
}
