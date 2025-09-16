package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.companion.DnDProficiency
import kotlin.uuid.Uuid

@Entity(
    tableName = "entity_modifiers_groups",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["entity_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class EntityModifiersGroup(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("entity_id", index = true) val entityId: Uuid,

    val target: DnDModifierTargetType,
    val operation: DnDModifierOperation,
    @ColumnInfo("value_source")
    val valueSource: DnDModifierValueSource,

    val name: String,
    val description: String?,
    @ColumnInfo("selection_limit") val selectionLimit: Int,
    val priority: Int,

    @ColumnInfo(name = "clamp_min") val clampMin: Int? = null,
    @ColumnInfo(name = "clamp_max") val clampMax: Int? = null,

    @ColumnInfo(name = "min_base_value") val minBaseValue: Int? = null,
    @ColumnInfo(name = "max_base_value") val maxBaseValue: Int? = null,
)

@Entity(
    tableName = "entity_modifiers",
    foreignKeys = [
        ForeignKey(EntityModifiersGroup::class, ["id"], ["group_id"], onDelete = ForeignKey.CASCADE)
    ],
    indices = [Index(value = ["group_id", "target"], unique = true)]
)
data class EntityModifier(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("group_id", index = true) val groupId: Uuid,
    val selectable: Boolean,
    val target: String,
    val value: Double
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