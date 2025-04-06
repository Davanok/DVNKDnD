@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import okio.Path
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

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
    @PrimaryKey val id: Uuid = Uuid.random(),
    val name: String,
    val description: String,
    @ColumnInfo(index = true) val cls: Uuid?,
    @ColumnInfo(index = true) val subCls: Uuid?,
    @ColumnInfo(index = true) val race: Uuid?,
    @ColumnInfo(index = true) val subRace: Uuid?,
    @ColumnInfo(index = true) val background: Uuid?,
    @ColumnInfo(index = true) val subBackground: Uuid?,
    val level: Int = 1,
    val proficiencyBonus: Int = 2,
    val source: String? = null,
    val mainImage: Path? = null
)