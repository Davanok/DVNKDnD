package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.types.dnd_enums.Skills
import com.davanok.dvnkdnd.data.types.dnd_enums.Stats

@Entity(
    tableName = "subclasses",
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE),
    ]
)
data class DnDSubclass(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val classId: Long,
    val name: String,
    val description: String,
    val source: String?
)


@Entity(
    tableName = "subclass_modifiers",
    foreignKeys = [
        ForeignKey(DnDSubclass::class, ["id"], ["subclassId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SubclassModifier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val subclassId: Long,
    val stat: Stats?,
    val skill: Skills?,
    val modifier: Int
)
@Entity(
    tableName = "subclass_skills",
    foreignKeys = [
        ForeignKey(DnDSubclass::class, ["id"], ["subclassId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class SubclassSkill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val subclassId: Long,
    val skill: Skills
)