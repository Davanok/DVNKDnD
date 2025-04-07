package com.davanok.dvnkdnd.database.entities

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.ProficiencyTypes
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlin.uuid.Uuid

// proficiencies like heavy armor, weapons
@Entity(
    tableName = "proficiencies",
    foreignKeys = [
        ForeignKey(DnDBaseEntity::class, ["id"], ["id"], onDelete = ForeignKey.CASCADE)
    ]
)
data class DnDProficiency(
    @PrimaryKey val id: Uuid,
    val type: ProficiencyTypes,
)
