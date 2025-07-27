package com.davanok.dvnkdnd.database.entities.dndEntities.companion

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dndEnums.ProficiencyTypes
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

// proficiencies like heavy armor, weapons
@Serializable
@Entity(
    tableName = "proficiencies",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.Companion.CASCADE)
    ]
)
data class DnDProficiency(
    @PrimaryKey val id: Uuid,
    val type: ProficiencyTypes,
)