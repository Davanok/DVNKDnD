package com.davanok.dvnkdnd.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.ProficiencyTypes

// proficiencies like heavy armor, weapons
@Entity(tableName = "proficiencies")
data class Proficiency(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val type: ProficiencyTypes,
    val source: String?
)
