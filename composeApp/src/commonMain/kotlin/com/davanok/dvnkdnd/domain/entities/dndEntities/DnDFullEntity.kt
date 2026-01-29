package com.davanok.dvnkdnd.domain.entities.dndEntities

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.core.getMainImage
import com.davanok.dvnkdnd.domain.entities.DatabaseImage
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
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
    val features: List<FeatureLink>,

    val cls: ClassWithSpells? = null,
    val race: RaceInfo? = null,
    val background: Unit? = null,
    val feat: FeatInfo? = null,
    val feature: FullFeature? = null,
    val spell: FullSpell? = null,
    val item: FullItem? = null,
    val state: FullState? = null,

    @SerialName("companion_entities")
    val companionEntities: List<DnDFullEntity> = emptyList(),
) {
    fun toDnDEntityMin() = entity.toEntityMin(images.getMainImage()?.path)

    fun getCompanionEntitiesIds() =
        features.map { it.featureId } +
                cls?.spells.orEmpty() +
                feature?.let { listOfNotNull(it.givesStateSelf, it.givesStateTarget) }.orEmpty() +
                spell?.attacks?.mapNotNull { it.givesState }.orEmpty() +
                item?.let { fullItem ->
                    fullItem.effects.map { it.givesState } +
                            fullItem.activations.flatMap { listOfNotNull(it.givesState, it.castsSpell?.spellId) }
                }.orEmpty()
}
