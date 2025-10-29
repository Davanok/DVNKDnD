package com.davanok.dvnkdnd.database.entities.dndEntities.concept

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSpell
import kotlin.uuid.Uuid

@Entity(
    tableName = "classes",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDClass( // also subclass
    @PrimaryKey val id: Uuid, // in same time is entity id
    val primaryStats: List<Attributes>,
    val hitDice: Dices,
)
@Entity(
    tableName = "class_spells",
    primaryKeys = ["class_id", "spell_id"],
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["class_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDSpell::class, ["id"], ["spell_id"], onDelete = ForeignKey.CASCADE),
    ]
)
data class ClassSpell( // spells that available for class
    @ColumnInfo("class_id", index = true) val classId: Uuid,
    @ColumnInfo("spell_id", index = true) val spellId: Uuid,
)
@Entity(
    tableName = "spell_slots",
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["class_id"], onDelete = ForeignKey.CASCADE),
    ]
)
data class ClassSpellSlots(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("class_id", index = true) val classId: Uuid,
    val level: Int,
    val preparedSpells: Int?,
    val cantrips: Int?,
    val spellSlots: List<Int>
)

