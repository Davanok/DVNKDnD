package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityImage


data class DbEntityWithImages(
    @Embedded val entity: DbBaseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "entity_id"
    )
    val images: List<DbEntityImage>
)

data class DbEntityWithSub(
    @Embedded val entity: DbEntityWithImages,
    @Relation(
        entity = DbBaseEntity::class,
        parentColumn = "id",
        entityColumn = "parent_id"
    )
    val subEntities: List<DbEntityWithImages>,
)
