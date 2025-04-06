@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.davanok.dvnkdnd.data.model.dnd_enums.ProficiencyTypes
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

// proficiencies like heavy armor, weapons
@Entity(tableName = "proficiencies")
data class Proficiency(
    @PrimaryKey val id: Uuid = Uuid.random(),
    val name: String,
    val type: ProficiencyTypes,
    val source: String?
)
