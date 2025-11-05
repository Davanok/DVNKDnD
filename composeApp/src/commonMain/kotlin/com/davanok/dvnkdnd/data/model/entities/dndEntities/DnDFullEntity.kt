package com.davanok.dvnkdnd.data.model.entities.dndEntities

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.util.wordInTextLevenshtein
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Immutable
@Serializable
data class DnDFullEntity(
    val id: Uuid = Uuid.random(),
    @SerialName("parent_id")
    val parentId: Uuid?,
    @SerialName("user_id")
    val userId: Uuid?,
    val type: DnDEntityTypes,
    val name: String,
    val description: String,
    val source: String,

    @SerialName("modifier_groups")
    val modifiersGroups: List<DnDModifiersGroup>,

    val proficiencies: List<JoinProficiency>,
    val abilities: List<AbilityLink>,

    val cls: ClassWithSpells? = null,
    val race: RaceInfo? = null,
    val background: Unit? = null,
    val feat: FeatInfo? = null,
    val ability: AbilityInfo? = null,
    val spell: FullSpell? = null,
    val item: FullItem? = null,

    @SerialName("companion_entities")
    val companionEntities: List<DnDFullEntity> = emptyList(),
) {
    fun toBaseEntity() = DnDBaseEntity(
        id = id,
        parentId = parentId,
        userId = userId,
        type = type,
        name = name,
        description = description,
        source = source
    )

    fun toDnDEntityMin() = DnDEntityMin(
        id = id,
        type = type,
        name = name,
        source = source
    )

    fun getSubEntitiesIds() =
        abilities.fastMap { it.abilityId } + cls?.spells.orEmpty()

    fun getDistance(s: String) = minOf(
        wordInTextLevenshtein(s, name),
        wordInTextLevenshtein(s, description)
    )
}
