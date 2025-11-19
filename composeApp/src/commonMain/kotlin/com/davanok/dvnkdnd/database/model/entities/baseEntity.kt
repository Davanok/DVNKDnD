package com.davanok.dvnkdnd.database.model.entities

import com.davanok.dvnkdnd.data.model.entities.dndEntities.EntityBase
import com.davanok.dvnkdnd.database.entities.dndEntities.DbBaseEntity

fun DbBaseEntity.toEntityBase() = EntityBase(
    id = id,
    parentId = parentId,
    userId = userId,
    type = type,
    name = name,
    description = description,
    source = source,
    image = image
)
fun EntityBase.toDbBaseEntity() = DbBaseEntity(
    id = id,
    parentId = parentId,
    userId = userId,
    type = type,
    name = name,
    description = description,
    source = source,
    image = image
)