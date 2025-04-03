package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity


fun DnDBaseEntity.toDnDEntityMin() = DnDEntityMin(
    id = id,
    type = type,
    name = name,
    source = source
)

data class EntityWithSub(
    @Embedded val entity: DnDBaseEntity,
    @Relation(
        parentColumn = "id",
        entityColumn = "parentId"
    )
    val subEntities: List<DnDBaseEntity>
)

fun EntityWithSub.toEntityWithSubEntities() = DnDEntityWithSubEntities(
    id = entity.id,
    type = entity.type,
    name = entity.name,
    source = entity.source,
    subEntities = subEntities.fastMap { it.toDnDEntityMin() }
)
