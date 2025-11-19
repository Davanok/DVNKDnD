package com.davanok.dvnkdnd.database.model.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.DbEntityModifiersGroup
import com.davanok.dvnkdnd.database.model.adapters.entities.toDnDModifier

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
        modifiers = modifiers.fastMap(DbEntityModifier::toDnDModifier)
    )
}