package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.database.MainAdapters

@Entity(
    tableName = "spell_slots",
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDSubclass::class, ["id"], ["subclassId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SpellSlots(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val classId: Long?,
    @ColumnInfo(index = true) val subclassId: Long?,
    val level: Int,
    val preparedSpells: Int?,
    val cantrips: Int,
    @TypeConverters(MainAdapters::class) val spellSlots: List<Int>
)