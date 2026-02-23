package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbRaceSpeedValues
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.concept.DbRace

data class DbFullRace(
    @Embedded
    val race: DbRace,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val speedValues: DbRaceSpeedValues
)
