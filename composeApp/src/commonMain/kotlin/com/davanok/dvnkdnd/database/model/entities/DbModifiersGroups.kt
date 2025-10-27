package com.davanok.dvnkdnd.database.model.entities

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifiersGroup
import com.davanok.dvnkdnd.database.model.adapters.entities.toDnDModifier

data class DbModifiersGroups(
    @Embedded val group: EntityModifiersGroup,
    @Relation(
        parentColumn = "id",
        entityColumn = "group_id"
    )
    val modifiers: List<EntityModifier>
) {
    fun toDnDModifiersGroup() = DnDModifiersGroup(
        id = group.id,
        target = group.target,
        operation = group.operation,
        valueSource = group.valueSource,
        value = group.value,
        name = group.name,
        description = group.description,
        selectionLimit = group.selectionLimit,
        priority = group.priority,
        clampMax = group.clampMax,
        clampMin = group.clampMin,
        minBaseValue = group.minBaseValue,
        maxBaseValue = group.maxBaseValue,
        modifiers = modifiers.fastMap(EntityModifier::toDnDModifier)
    )
}