package com.davanok.dvnkdnd.database.entities.dndEntities.companion

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dndEnums.ProficiencyTypes
import kotlin.uuid.Uuid

// proficiencies like heavy armor, weapons
@Entity(
    tableName = "proficiencies",
    indices = [Index(value = ["type", "name"], unique = true)]
)
data class DnDProficiency(
    @PrimaryKey val id: Uuid = Uuid.random(),
    val type: ProficiencyTypes,
    val name: String
)