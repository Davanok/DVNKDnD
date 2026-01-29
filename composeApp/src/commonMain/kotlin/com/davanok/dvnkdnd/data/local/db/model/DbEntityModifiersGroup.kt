package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityDamageModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityRollModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityValueModifier

data class DbEntityFullModifiersGroup(
    @Embedded val group: DbEntityModifiersGroup,

    @Relation(parentColumn = "id", entityColumn = "group_id")
    val valueModifiers: List<DbEntityValueModifier>,
    @Relation(parentColumn = "id", entityColumn = "group_id")
    val rollModifiers: List<DbEntityRollModifier>,
    @Relation(parentColumn = "id", entityColumn = "group_id")
    val damageModifiers: List<DbEntityDamageModifier>
)