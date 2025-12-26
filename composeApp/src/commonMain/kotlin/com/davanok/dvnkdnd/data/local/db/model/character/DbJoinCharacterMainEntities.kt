package com.davanok.dvnkdnd.data.local.db.model.character

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterMainEntity
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.model.DbFullEntity

data class DbJoinCharacterMainEntities(
    @Embedded
    val link: DbCharacterMainEntity,
    @Relation(
        DbBaseEntity::class,
        parentColumn = "entity_id",
        entityColumn = "id"
    )
    val entity: DbFullEntity,
    @Relation(
        DbBaseEntity::class,
        parentColumn = "sub_entity_id",
        entityColumn = "id"
    )
    val subEntity: DbFullEntity?
)