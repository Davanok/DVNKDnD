package com.davanok.dvnkdnd.domain.entities.dndEntities

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.core.getMainImage
import com.davanok.dvnkdnd.domain.entities.DatabaseImage
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

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

    fun getCompanionEntitiesIds(): List<Uuid> = buildSet {
        if (entity.parentId != null) add(entity.parentId)

        features.forEach {
            add(it.featureId)
        }

        if (!cls?.spells.isNullOrEmpty())
            addAll(cls.spells)

        feature?.run {
            if (givesStateSelf != null) add(givesStateSelf)
            if (givesStateTarget != null) add(givesStateTarget)
        }

        spell?.attacks?.forEach {
            if (it.givesState != null) add(it.givesState)
        }

        item?.effects?.forEach {
            add(it.givesState)
        }
        item?.activations?.forEach {
            if (it.givesState != null) add(it.givesState)
            if (it.castsSpell != null) add(it.castsSpell.spellId)
        }
    }.toList()
}
