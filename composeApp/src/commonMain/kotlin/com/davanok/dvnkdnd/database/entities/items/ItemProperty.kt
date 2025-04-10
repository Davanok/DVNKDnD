package com.davanok.dvnkdnd.database.entities.items

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlin.uuid.Uuid

// properties for items like heavy, two-handed, graze
@Entity(tableName = "properties")
data class ItemProperty(
    @PrimaryKey val id: Uuid = Uuid.random(),
    val name: String,
    val description: String
)