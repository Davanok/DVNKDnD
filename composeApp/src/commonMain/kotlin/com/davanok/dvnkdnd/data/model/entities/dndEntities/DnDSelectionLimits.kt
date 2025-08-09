package com.davanok.dvnkdnd.data.model.entities.dndEntities

import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DnDSelectionLimits(
    val modifiers: Int? = null,
    val skills: Int? = null,
    @SerialName("saving_throws")
    val savingThrows: Int? = null,
    val proficiencies: Int? = null,
    val abilities: Int? = null,
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