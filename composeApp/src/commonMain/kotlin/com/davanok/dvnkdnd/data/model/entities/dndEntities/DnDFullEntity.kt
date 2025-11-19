package com.davanok.dvnkdnd.data.model.entities.dndEntities

import androidx.compose.runtime.Immutable
import androidx.compose.ui.util.fastMap
import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.data.model.util.wordInTextLevenshtein
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Immutable
@Serializable
data class DnDFullEntity(
    val entity: EntityBase,

    val images: List<DatabaseImage> = emptyList(),

    @SerialName("modifier_groups")
    val modifiersGroups: List<ModifiersGroup>,

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
    fun toDnDEntityMin() = entity.toEntityMin()

    fun getSubEntitiesIds() =
        abilities.fastMap { it.abilityId } + cls?.spells.orEmpty()

    fun getDistance(s: String) = minOf(
        wordInTextLevenshtein(s, entity.name),
        wordInTextLevenshtein(s, entity.description)
    )
}
