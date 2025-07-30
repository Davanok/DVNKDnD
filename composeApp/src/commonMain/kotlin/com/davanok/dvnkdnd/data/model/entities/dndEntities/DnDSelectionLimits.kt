package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DnDSelectionLimits(
    val modifiers: Int?,
    val skills: Int?,
    @SerialName("saving_throws")
    val savingThrows: Int?,
    val proficiencies: Int?,
    val abilities: Int?,
)

fun EntitySelectionLimits.toDnDSelectionLimits() = DnDSelectionLimits(
    modifiers = modifiers,
    skills = skills,
    savingThrows = savingThrows,
    proficiencies = proficiencies,
    abilities = abilities
)
fun DnDSelectionLimits.toEntitySelectionLimits(entityId: Uuid) = EntitySelectionLimits(
    id = entityId,
    modifiers = modifiers,
    skills = skills,
    savingThrows = savingThrows,
    proficiencies = proficiencies,
    abilities = abilities
)