package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClass
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClassSpell
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbClassSpellSlots
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbSpellSlotType
import kotlin.uuid.Uuid

data class DbClassSpellSlotWithType(
    @Embedded val slot: DbClassSpellSlots,
    @Relation(
        parentColumn = "type_id",
        entityColumn = "id"
    )
    val type: DbSpellSlotType
)

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
)