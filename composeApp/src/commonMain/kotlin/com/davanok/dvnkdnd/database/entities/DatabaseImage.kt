package com.davanok.dvnkdnd.database.entities

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DatabaseImage(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val path: String,
    val description: String?
)
