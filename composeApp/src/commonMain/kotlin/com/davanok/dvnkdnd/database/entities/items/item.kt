package com.davanok.dvnkdnd.database.entities.items

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Skills
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.database.entities.DatabaseImage
import com.davanok.dvnkdnd.database.entities.Proficiency


@Entity(
    tableName = "items"
)
data class DnDItem(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String?,
    val pinned: Boolean = false,
    val cost: Int?, // in copper pieces
    val weight: Int?,
    val count: Int,
    val source: String?
)

@Entity(
    tableName = "item_proficiencies",
    foreignKeys = [
        ForeignKey(DnDItem::class, ["id"], ["itemId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Proficiency::class, ["id"], ["proficiencyId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ItemProficiency(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val itemId: Long,
    @ColumnInfo(index = true) val proficiencyId: Long
)
@Entity(
    tableName = "item_modifiers",
    foreignKeys = [
        ForeignKey(DnDItem::class, ["id"], ["itemId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ItemModifier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val itemId: Long,
    val stat: Stats?,
    val skill: Skills?,
    val modifier: Int
)
@Entity(
    tableName = "item_skills",
    foreignKeys = [
        ForeignKey(DnDItem::class, ["id"], ["itemId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ItemSkill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val itemId: Long,
    val skill: Skills
)

@Entity(
    tableName = "item_images",
    foreignKeys = [
        ForeignKey(DnDItem::class, ["id"], ["itemId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DatabaseImage::class, ["id"], ["imageId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ItemImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val itemId: Long,
    @ColumnInfo(index = true) val imageId: Long
)