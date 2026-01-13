package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.runtime.Composable
import androidx.compose.ui.util.fastJoinToString
import com.davanok.dvnkdnd.domain.dnd.DnDConstants
import com.davanok.dvnkdnd.domain.entities.dndEntities.ItemProperty
import com.davanok.dvnkdnd.domain.enums.dndEnums.ItemPropertyType
import com.davanok.dvnkdnd.ui.providers.LocalMeasurementSystem

@Composable
fun ItemProperty.parseValue(): String? = when (type) {
    ItemPropertyType.THROWN,
    ItemPropertyType.AMMUNITION -> value
        ?.split('/')
        ?.mapNotNull(String::toIntOrNull)
        .let { if (it.isNullOrEmpty()) DnDConstants.RANGED_ITEM_DEFAULT_RANGE else it }
        .map { MeasurementConverter.convertLength(it, LocalMeasurementSystem.current.length) }
        .fastJoinToString("/")

    ItemPropertyType.REACH -> value
        ?.toIntOrNull()
        .let { it ?: DnDConstants.REACH_ITEM_DEFAULT_RANGE_MODIFIER }
        .let { MeasurementConverter.convertLength(it, LocalMeasurementSystem.current.length) }

    else -> value
}

@Composable
fun ItemProperty.buildName() = buildString {
    val propertyValue = parseValue()
    append(name)
    if (propertyValue != null) {
        append(" (")
        append(propertyValue)
        append(')')
    }
}