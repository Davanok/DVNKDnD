package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.Skills
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.database.entities.DnDProficiency
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@Entity(
    tableName = "entity_modifiers",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityModifier(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
    val selectable: Boolean,
    val stat: Stats,
    val modifier: Int,
)

@Serializable
@Entity(
    tableName = "entity_skills",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntitySkill(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
    val selectable: Boolean,
    val skill: Skills,
)

@Serializable
@Entity(
    tableName = "entity_saving_throws",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntitySavingThrow(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
    val selectable: Boolean,
    val stat: Stats,
)

@Serializable
@Entity(
    tableName = "entity_proficiencies",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDProficiency::class, ["id"], ["proficiencyId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityProficiencies(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
    @ColumnInfo(index = true) val proficiencyId: Uuid,
)

@Serializable
@Entity(
    tableName = "entity_abilities",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DnDAbility::class, ["id"], ["abilityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityAbility(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
    @ColumnInfo(index = true) val abilityId: Uuid,
    val level: Int,
)

@Serializable
@Entity(
    tableName = "entity_selection_limits",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entityId"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntitySelectionLimits(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo(index = true) val entityId: Uuid,
    val modifiers: Int?,
    val skills: Int?,
    val savingThrows: Int?,
    val proficiencies: Int?,
    val abilities: Int?
)