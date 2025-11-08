package com.davanok.dvnkdnd.database.model.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.EntityBase
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity

fun DnDBaseEntity.toEntityBase() = EntityBase(
    id = id,
    parentId = parentId,
    userId = userId,
    type = type,
    name = name,
    description = description,
    source = source,
    image = image
)
fun EntityBase.toDnDBaseEntity() = DnDBaseEntity(
    id = id,
    parentId = parentId,
    userId = userId,
    type = type,
    name = name,
    description = description,
    source = source,
    image = image
)