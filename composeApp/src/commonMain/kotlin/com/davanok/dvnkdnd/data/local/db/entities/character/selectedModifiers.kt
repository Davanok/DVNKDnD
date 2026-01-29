package com.davanok.dvnkdnd.data.local.db.entities.character

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityDamageModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityRollModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityValueModifier
import kotlin.uuid.Uuid


@Entity(
    tableName = "character_selected_value_modifier",
    primaryKeys = ["character_id", "modifier_id"],
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbEntityValueModifier::class, ["id"], ["modifier_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterSelectedValueModifier(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("modifier_id", index = true) val modifierId: Uuid
)


@Entity(
    tableName = "character_selected_roll_modifier",
    primaryKeys = ["character_id", "modifier_id"],
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbEntityRollModifier::class, ["id"], ["modifier_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterSelectedRollModifier(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("modifier_id", index = true) val modifierId: Uuid
)


@Entity(
    tableName = "character_damage_modifier",
    primaryKeys = ["character_id", "modifier_id"],
    foreignKeys = [
        ForeignKey(DbCharacter::class, ["id"], ["character_id"], onDelete = ForeignKey.CASCADE),
        ForeignKey(DbEntityDamageModifier::class, ["id"], ["modifier_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbCharacterSelectedDamageModifier(
    @ColumnInfo("character_id", index = true) val characterId: Uuid,
    @ColumnInfo("modifier_id", index = true) val modifierId: Uuid
)