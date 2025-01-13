package com.davanok.dvnkdnd.database.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBackground
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDClass
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDRace
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDSubclass

@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(DnDRace::class, ["id"], ["race"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDClass::class, ["id"], ["cls"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDSubclass::class, ["id"], ["subCls"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDBackground::class, ["id"], ["background"], onDelete = ForeignKey.SET_NULL),
    ]
)
data class Character(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    @ColumnInfo(index = true) val race: Long?,
    @ColumnInfo(index = true) val subRace: Long?,
    @ColumnInfo(index = true) val cls: Long?,
    @ColumnInfo(index = true) val subCls: Long?,
    @ColumnInfo(index = true) val background: Long?,
    val level: Int,
    val proficiencyBonus: Int,
    val image: String?,
    val source: String?
)