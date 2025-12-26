package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifiersGroup

data class DbModifiersGroups(
    @Embedded val group: DbEntityModifiersGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val modifiers: List<DbEntityModifier>
)