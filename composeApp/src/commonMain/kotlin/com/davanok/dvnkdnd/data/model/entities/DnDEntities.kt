package com.davanok.dvnkdnd.data.model.entities

import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityAbility
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityModifier
import com.davanok.dvnkdnd.database.entities.dndEntities.EntityProficiency
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySavingThrow
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySelectionLimits
import com.davanok.dvnkdnd.database.entities.dndEntities.EntitySkill
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
data class DnDEntityMin(
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
)
@Serializable
data class DnDEntityWithSubEntities (
    val id: Uuid,
    val type: DnDEntityTypes,
    val name: String,
    val source: String,
    @SerialName("sub_entities")
    val subEntities: List<DnDEntityMin>
) {
    fun asDnDEntityMin() = DnDEntityMin(id, type, name, source)
}
@Serializable
data class DnDEntityFullInfo(
    val id: Uuid,
    @SerialName("user_id")
    val userId: Uuid?,
    @SerialName("parent_id")
    val parentId: Uuid?,
    val type: DnDEntityTypes,
    val shared: Boolean = true,
    val name: String,
    val description: String,
    val source: String,

    val modifiers: List<EntityModifier>,
    val skills: List<EntitySkill>,
    @SerialName("saving_throws")
    val savingThrows: List<EntitySavingThrow>,
    val proficiencies: List<EntityProficiency>,
    val abilities: List<EntityAbility>,

    @SerialName("selection_limits")
    val selectionLimits: EntitySelectionLimits?,

    @SerialName("sub_entities")
    val subEntities: List<Uuid> = emptyList()
)
fun DnDEntityFullInfo.toBaseEntity() = DnDBaseEntity(
    id = id,
    parentId = parentId,
    type = type,
    shared = shared,
    name = name,
    description = description,
    source = source
)

data class DnDEntityWithModifiers(
    val entity: DnDEntityMin,
    val selectionLimit: Int? = null,
    val modifiers: List<DnDModifier> = emptyList()
)