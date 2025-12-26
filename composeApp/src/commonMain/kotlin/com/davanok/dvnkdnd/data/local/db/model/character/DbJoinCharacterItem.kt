package com.davanok.dvnkdnd.data.local.db.model.character

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterItemLink
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.model.DbFullEntity

data class DbJoinCharacterItem(
    @Embedded
    val link: DbCharacterItemLink,
    @Relation(
        DbBaseEntity::class,
        parentColumn = "item_id",
        entityColumn = "id"
    )
    val item: DbFullEntity
)