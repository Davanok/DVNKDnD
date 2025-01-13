package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.types.dnd_enums.Dices
import com.davanok.dvnkdnd.data.types.dnd_enums.Skills
import com.davanok.dvnkdnd.data.types.dnd_enums.Stats
import com.davanok.dvnkdnd.database.entities.Proficiency

@Entity(tableName = "classes")
data class DnDClass(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val source: String?,
    val skillsSelectLimit: Int?,
    val proficienciesSelectLimit: Int?,
    val hitDice: Dices
)

@Entity(
    tableName = "class_skills",
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ClassSkill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val classId: Long,
    val selectable: Boolean,
    val skill: Skills
)
@Entity(
    tableName = "class_saving_throws",
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ClassSavingThrows(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val classId: Long,
    val stat: Stats
)
@Entity(
    tableName = "сlass_proficiencies",
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Proficiency::class, ["id"], ["proficiencyId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ClassProficiency(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val classId: Long,
    @ColumnInfo(index = true) val proficiencyId: Long,
    val selectable: Boolean
)
@Entity(
    tableName = "сlass_abilities",
    foreignKeys = [
        ForeignKey(DnDClass::class, ["id"], ["classId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDAbility::class, ["id"], ["abilityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class ClassAbility(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val classId: Long,
    @ColumnInfo(index = true) val abilityId: Long,
    val level: Int
)

