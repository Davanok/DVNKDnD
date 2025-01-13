package com.davanok.dvnkdnd.database.entities.dndEntities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(
    tableName = "abilities"
)
data class DnDAbility(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String,
    val source: String
)
