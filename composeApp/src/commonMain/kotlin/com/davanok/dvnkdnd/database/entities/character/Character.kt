package com.davanok.dvnkdnd.database.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity

@Entity(
    tableName = "characters",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["cls"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDBaseEntity::class, ["id"], ["subCls"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDBaseEntity::class, ["id"], ["race"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDBaseEntity::class, ["id"], ["subRace"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDBaseEntity::class, ["id"], ["background"], onDelete = ForeignKey.SET_NULL),
        ForeignKey(DnDBaseEntity::class, ["id"], ["subBackground"], onDelete = ForeignKey.SET_NULL),
    ]
)
data class Character(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    @ColumnInfo(index = true) val cls: Long?,
    @ColumnInfo(index = true) val subCls: Long?,
    @ColumnInfo(index = true) val race: Long?,
    @ColumnInfo(index = true) val subRace: Long?,
    @ColumnInfo(index = true) val background: Long?,
    @ColumnInfo(index = true) val subBackground: Long?,
    val level: Int = 1,
    val proficiencyBonus: Int = 2,
    val source: String? = null,
    val mainImage: String? = null
)