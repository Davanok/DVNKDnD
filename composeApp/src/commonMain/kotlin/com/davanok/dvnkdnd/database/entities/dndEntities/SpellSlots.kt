package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

@Entity(
    tableName = "spell_slots",
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE),
    ]
)
data class SpellSlots(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val classId: Uuid?,
    val level: Int,
    val preparedSpells: Int?,
    val cantrips: Int,
    val spellSlots: List<Int>
)