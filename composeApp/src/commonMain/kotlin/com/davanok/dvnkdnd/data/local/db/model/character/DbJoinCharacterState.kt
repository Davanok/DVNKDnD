package com.davanok.dvnkdnd.data.local.db.model.character

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterStateLink
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.model.DbFullEntity


data class DbJoinCharacterState(
    @Embedded
    val link: DbCharacterStateLink,
    @Relation(
        DbBaseEntity::class,
        parentColumn = "state_id",
        entityColumn = "id"
    )
    val state: DbFullEntity,
    @Relation(
        DbBaseEntity::class,
        parentColumn = "source_id",
        entityColumn = "id"
    )
    val source: DbFullEntity?
)