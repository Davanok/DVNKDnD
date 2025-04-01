package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Skills
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.database.entities.Proficiency

@Entity(
    tableName = "feats",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDFeat(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val entityId: Long,
    val repeatable: Boolean,
    val modifiersSelectLimit: Int?,
    val skillsSelectLimit: Int?,
    val proficienciesSelectLimit: Int?
)

@Entity(
    tableName = "feat_modifiers",
    foreignKeys = [
        ForeignKey(DnDFeat::class, ["id"], ["featId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class FeatModifier(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val featId: Long,
    val selectable: Boolean, // true, в случаях, когда можно выбрать модификатор
    val stat: Stats?,
    val skill: Skills?,
    val modifier: Int
)
@Entity(
    tableName = "feat_skills",
    foreignKeys = [
        ForeignKey(DnDFeat::class, ["id"], ["featId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class FeatSkill(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val featId: Long,
    val selectable: Boolean,
    val skill: Skills
)
@Entity(
    tableName = "feat_proficiencies",
    foreignKeys = [
        ForeignKey(DnDFeat::class, ["id"], ["featId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(Proficiency::class, ["id"], ["proficiencyId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class FeatProficiency(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    @ColumnInfo(index = true) val featId: Long,
    @ColumnInfo(index = true) val proficiencyId: Long,
    val selectable: Boolean
)