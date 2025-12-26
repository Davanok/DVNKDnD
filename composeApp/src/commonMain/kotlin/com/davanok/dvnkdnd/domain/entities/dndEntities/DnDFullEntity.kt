package com.davanok.dvnkdnd.domain.entities.dndEntities

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.domain.entities.DatabaseImage
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.core.utils.wordInTextLevenshtein
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
    val state: FullState? = null,

    @SerialName("companion_entities")
    val companionEntities: List<DnDFullEntity> = emptyList(),
) {
    fun toDnDEntityMin() = entity.toEntityMin()

    fun getSubEntitiesIds() =
        abilities.map { it.abilityId } +
                cls?.spells.orEmpty() +
                ability?.let { listOfNotNull(it.givesStateSelf, it.givesStateTarget) }.orEmpty() +
                spell?.attacks?.mapNotNull { it.givesState }.orEmpty() +
                item?.let { fullItem ->
                    fullItem.effects.map { it.givesState } + fullItem.activations.map { it.givesState }
                }.orEmpty()

    fun getDistance(s: String) = minOf(
        wordInTextLevenshtein(s, entity.name),
        wordInTextLevenshtein(s, entity.description)
    )
}
