package com.davanok.dvnkdnd.data.local.db.model

import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifier
import com.davanok.dvnkdnd.data.local.db.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.data.local.mappers.entities.toDnDModifier

data class DbModifiersGroups(
    @Embedded val group: DbEntityModifiersGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val modifiers: List<DbEntityModifier>
) {
    fun toDnDModifiersGroup() = ModifiersGroup(
        id = group.id,
        target = group.target,
        operation = group.operation,
        valueSource = group.valueSource,
        valueSourceTarget = group.valueSourceTarget,
        value = group.value,
        name = group.name,
        description = group.description,
        selectionLimit = group.selectionLimit,
        priority = group.priority,
        clampMax = group.clampMax,
        clampMin = group.clampMin,
        minBaseValue = group.minBaseValue,
        maxBaseValue = group.maxBaseValue,
        modifiers = modifiers.map(DbEntityModifier::toDnDModifier)
    )
}