package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.domain.entities.dndEntities.EntityBase
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbBaseEntity

fun DbBaseEntity.toEntityBase() = EntityBase(
    id = id,
    parentId = parentId,
    userId = userId,
    type = type,
    name = name,
    description = description,
    source = source
)
fun EntityBase.toDbBaseEntity() = DbBaseEntity(
    id = id,
    parentId = parentId,
    userId = userId,
    type = type,
    name = name,
    description = description,
    source = source
)