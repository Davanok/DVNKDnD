@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Skills
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.database.entities.Proficiency
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Entity(
    tableName = "feats",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDFeat(
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
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
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val featId: Uuid,
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
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val featId: Uuid,
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
    @PrimaryKey(autoGenerate = true) val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val featId: Uuid,
    @ColumnInfo(index = true) val proficiencyId: Uuid,
    val selectable: Boolean
)