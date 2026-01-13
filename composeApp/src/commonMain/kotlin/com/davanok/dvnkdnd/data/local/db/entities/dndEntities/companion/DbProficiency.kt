package com.davanok.dvnkdnd.data.local.db.entities.dndEntities.companion

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.local.db.entities.items.DbItemProperty
import com.davanok.dvnkdnd.domain.enums.dndEnums.ProficiencyTypes
import kotlin.uuid.Uuid

// proficiencies like heavy armor, weapons
@Entity(
    tableName = "proficiencies",
    foreignKeys = [
        ForeignKey(DbItemProperty::class, ["id"], ["item_property_id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DbProficiency(
    @PrimaryKey val id: Uuid = Uuid.random(),
    @ColumnInfo("user_id") val userId: Uuid?,
    @ColumnInfo("item_property_id", index = true)
    val itemPropertyId: Uuid?,
    val type: ProficiencyTypes,
    val name: String
)