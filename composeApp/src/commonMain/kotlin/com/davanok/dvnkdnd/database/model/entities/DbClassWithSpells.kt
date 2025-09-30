package com.davanok.dvnkdnd.database.model.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.ClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DnDClass
import com.davanok.dvnkdnd.database.model.adapters.entities.toSpellSlots
import kotlin.uuid.Uuid

data class DbClassWithSpells(
    @Embedded
    val cls: DnDClass,
    @Relation(
        ClassSpell::class,
        parentColumn = "id",
        entityColumn = "class_id",
        projection = ["spell_id"]
    )
    val spells: List<Uuid>,
    @Relation(
        parentColumn = "id",
        entityColumn = "class_id"
    )
    val slots: List<ClassSpellSlots>
) {
    fun toClassWithSpells() = ClassWithSpells(
        primaryStats = cls.primaryStats,
        hitDice = cls.hitDice,
        spells = spells,
        slots = slots.fastMap(ClassSpellSlots::toSpellSlots)
    )
}