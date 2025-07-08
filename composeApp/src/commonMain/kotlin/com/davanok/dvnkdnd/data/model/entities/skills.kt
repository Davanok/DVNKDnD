package com.davanok.dvnkdnd.data.model.entities

import com.davanok.dvnkdnd.data.model.dnd_enums.Skills
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DnDSkill(
    val id: Uuid,
    val selectable: Boolean,
    val skill: Skills,
)
fun EntitySkill.toDnDSkill() = DnDSkill(
    id = id,
    selectable = selectable,
    skill = skill
)