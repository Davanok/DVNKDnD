package com.davanok.dvnkdnd.database.entities.items

import androidx.room.Entity
import androidx.room.PrimaryKey

// properties for items like heavy, two-handed, graze
@Entity(tableName = "properties")
data class Property(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val name: String,
    val description: String
)