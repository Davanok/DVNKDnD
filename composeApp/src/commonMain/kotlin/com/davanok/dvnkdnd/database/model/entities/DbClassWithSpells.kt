package com.davanok.dvnkdnd.database.model.entities

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndEntities.ClassWithSpells
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbClass
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbClassSpell
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbClassSpellSlots
import com.davanok.dvnkdnd.database.entities.dndEntities.concept.DbSpellSlotType
import com.davanok.dvnkdnd.database.model.adapters.entities.toSpellSlotType
import kotlin.uuid.Uuid

data class DbClassSpellSlotWithType(
    @Embedded val slot: DbClassSpellSlots,
    @Relation(
        parentColumn = "type_id",
        entityColumn = "id"
    )
    val type: DbSpellSlotType
) {
    fun toSpellSlots() = SpellSlots(
        id = slot.id,
        level = slot.level,
        preparedSpells = slot.preparedSpells,
        cantrips = slot.cantrips,
        spellSlots = slot.spellSlots,
        type = type.toSpellSlotType()
    )
}

data class DbClassWithSpells(
    @Embedded
    val cls: DbClass,
    @Relation(
        DbClassSpell::class,
        parentColumn = "id",
        entityColumn = "class_id",
        projection = ["spell_id"]
    )
    val spells: List<Uuid>,
    @Relation(
        entity = DbClassSpellSlots::class,
        parentColumn = "id",
        entityColumn = "class_id"
    )
    val slots: List<DbClassSpellSlotWithType>
) {
    fun toClassWithSpells() = ClassWithSpells(
        primaryStats = cls.primaryStats,
        hitDice = cls.hitDice,
        caster = cls.caster,
        spells = spells,
        slots = slots.map(DbClassSpellSlotWithType::toSpellSlots),
    )
}