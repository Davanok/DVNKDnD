package com.davanok.dvnkdnd.data.local.mappers.character

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalSpeedValues
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterOptionalValues

data class DbCharacterFullOptionalValues(
    @Embedded
    val base: DbCharacterOptionalValues,
    @Relation(
        parentColumn = "id",
        entityColumn = "id"
    )
    val speedValues: DbCharacterOptionalSpeedValues
)