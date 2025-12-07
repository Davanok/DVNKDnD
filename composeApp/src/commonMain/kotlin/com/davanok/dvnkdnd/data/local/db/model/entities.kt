package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity

fun DbBaseEntity.toDnDEntityMin() = DnDEntityMin(
    id = id,
    type = type,
    name = name,
    source = source
)

data class DbEntityWithSub(
    @Embedded val entity: DbBaseEntity,
    @Relation(parentColumn = "id", entityColumn = "parent_id")
    val subEntities: List<DbBaseEntity>,
) {
    fun toEntityWithSubEntities() = DnDEntityWithSubEntities(
        id = entity.id,
        type = entity.type,
        name = entity.name,
        source = entity.source,
        subEntities = subEntities.map(DbBaseEntity::toDnDEntityMin)
    )
}
