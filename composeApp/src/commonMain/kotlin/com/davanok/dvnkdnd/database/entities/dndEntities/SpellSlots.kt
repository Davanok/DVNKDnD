@file:kotlin.OptIn(kotlin.uuid.ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.davanok.dvnkdnd.database.MainAdapters
import kotlin.uuid.Uuid

@Entity(
    tableName = "spell_slots",
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDSubclass::class, ["id"], ["subclassId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SpellSlots(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val classId: Uuid?,
    @ColumnInfo(index = true) val subclassId: Uuid?,
    val level: Int,
    val preparedSpells: Int?,
    val cantrips: Int,
    @TypeConverters(MainAdapters::class) val spellSlots: List<Int>
)