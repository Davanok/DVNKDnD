package com.davanok.dvnkdnd.data.local.mappers.entities

import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityImage
import com.davanok.dvnkdnd.domain.entities.DatabaseImage
import kotlin.uuid.Uuid

fun DbEntityImage.toDatabaseImage() = DatabaseImage(
    id = id,
    path = path,
    isMain = isMain
)
fun DatabaseImage.toDbEntityImage(entityId: Uuid) = DbEntityImage(
    id = id,
    entityId = entityId,
    path = path,
    isMain = isMain
)