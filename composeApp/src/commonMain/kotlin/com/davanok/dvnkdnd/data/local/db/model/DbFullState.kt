package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbState
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbStateDuration

data class DbFullState(
    @Embedded
    val state: DbState,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val duration: DbStateDuration?
)