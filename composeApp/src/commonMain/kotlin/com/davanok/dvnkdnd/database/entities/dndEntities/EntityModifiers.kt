package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dndEnums.Skills
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Entity(
    tableName = "entity_modifier_bonuses",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityModifierBonus(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("entity_id", index = true) val entityId: Uuid,
    val selectable: Boolean,
    val stat: Stats,
    val modifier: Int,
)

@Entity(
    tableName = "entity_skills",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntitySkill(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("entity_id", index = true) val entityId: Uuid,
    val selectable: Boolean,
    val skill: Skills,
)

@Serializable
@Entity(
    tableName = "entity_saving_throws",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntitySavingThrow(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @SerialName("entity_id")
    @ColumnInfo("entity_id", index = true) val entityId: Uuid,
    val selectable: Boolean,
    val stat: Stats,
)

@Entity(
    tableName = "entity_proficiencies",
    primaryKeys = ["entity_id", "proficiency_id"],
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDProficiency::class, ["id"], ["proficiency_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityProficiency(
    @ColumnInfo("entity_id", index = true) val entityId: Uuid,
    @ColumnInfo("proficiency_id", index = true) val proficiencyId: Uuid,
    val level: Int
)

@Entity(
    tableName = "entity_abilities",
    primaryKeys = ["entity_id", "ability_id"],
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDAbility::class, ["id"], ["ability_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityAbility(
    @ColumnInfo("entity_id", index = true) val entityId: Uuid,
    @ColumnInfo("ability_id", index = true) val abilityId: Uuid,
    val level: Int
)

@Serializable
@Entity(
    tableName = "entity_selection_limits",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntitySelectionLimits( // selection limit sum also includes non selectable items
    @PrimaryKey val id: Uuid = Uuid.random(),
    val modifiers: Int?,
    val skills: Int?,
    @SerialName("saving_throws")
    @ColumnInfo("saving_throws") val savingThrows: Int?,
    val proficiencies: Int?,
    val abilities: Int?,
)