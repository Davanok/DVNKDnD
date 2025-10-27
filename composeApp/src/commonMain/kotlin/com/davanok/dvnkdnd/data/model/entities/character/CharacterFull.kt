package com.davanok.dvnkdnd.data.model.entities.character

import androidx.compose.ui.util.fastFilteredMap
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDSkillsGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toSkillsGroup
import com.davanok.dvnkdnd.data.model.types.ModifierExtendedInfo
import com.davanok.dvnkdnd.data.model.types.UiSelectableState
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import kotlinx.serialization.Serializable
import kotlinx.serialization.Transient
import kotlin.uuid.Uuid

@Serializable
data class CharacterFull(
    val character: CharacterBase,

    @Transient
    val images: List<DatabaseImage> = emptyList(),
    val coins: CoinsGroup = CoinsGroup(),

    val attributes: DnDAttributesGroup = DnDAttributesGroup.Default,
    val health: DnDCharacterHealth = DnDCharacterHealth(),
    val usedSpells: List<Int> = emptyList(),

    val mainEntities: List<CharacterMainEntityInfo> = emptyList(),

    val feats: List<DnDFullEntity> = emptyList(),

    val selectedModifiers: Set<Uuid> = emptySet(),
    val selectedProficiencies: Set<Uuid> = emptySet(),

    val customModifiers: List<CustomModifier> = emptyList(),

    @Transient
    val appliedValues: CharacterModifiedValues = CharacterModifiedValues(
        attributes = attributes,
        savingThrowModifiers = attributes
            .toMap()
            .mapValues { (_, value) -> calculateModifier(value) }
            .toAttributesGroup(),
        skillModifiers = attributes
            .toMap()
            .mapValues { (_, value) -> calculateModifier(value) }
            .toAttributesGroup()
            .toSkillsGroup(),
        health = health
    )
) {
    val entities: List<DnDFullEntity>
        get() = mainEntities.fastFlatMap { listOfNotNull(it.entity, it.subEntity) } + feats
    val groupIdToEntityId by lazy {
        entities.flatMap { e -> e.modifiersGroups.map { it.id to e.id } }.toMap()
    }

    fun resolveValueSource(source: DnDModifierValueSource, entityId: Uuid?, modifierValue: Double): Double =
        when(source) {
            DnDModifierValueSource.CONSTANT -> modifierValue
            DnDModifierValueSource.CHARACTER_LEVEL -> character.level + modifierValue
            DnDModifierValueSource.ENTITY_LEVEL -> (entityId?.let { id -> mainEntities.fastFirstOrNull { id in it }?.level } ?: 0) + modifierValue
            DnDModifierValueSource.PROFICIENCY_BONUS -> character.getProfBonus() + modifierValue
        }

    fun resolveGroupValue(group: DnDModifiersGroup): Double =
        resolveValueSource(group.valueSource, groupIdToEntityId[group.id], group.value)

    val appliedModifiers: Map<DnDModifierTargetType, List<ModifierExtendedInfo>> by lazy {
        val result = mutableMapOf<DnDModifierTargetType, MutableList<Pair<Int, ModifierExtendedInfo>>>()
        entities.fastForEach { entity ->
            entity.modifiersGroups.fastForEach { group ->
                val modifiers = group.modifiers.fastFilteredMap(
                    predicate = { it.id in selectedModifiers },
                    transform = { modifier ->
                        group.priority to ModifierExtendedInfo(
                            groupId = group.id,
                            groupName = group.name,
                            modifier = modifier,
                            operation = group.operation,
                            valueSource = group.valueSource,
                            value = group.value,
                            state = UiSelectableState(
                                selectable = modifier.selectable,
                                selected = true
                            ),
                            resolvedValue = resolveValueSource(
                                group.valueSource,
                                entity.id,
                                group.value
                            )
                        )
                    }
                )
                result
                    .getOrPut(group.target, ::mutableListOf)
                    .addAll(modifiers)
            }
        }
        result.mapValues { modifiers -> modifiers.value.sortedBy { it.first }.map { it.second } }
    }
}

data class CharacterModifiedValues(
    val attributes: DnDAttributesGroup,
    val savingThrowModifiers: DnDAttributesGroup,
    val skillModifiers: DnDSkillsGroup,
    val health: DnDCharacterHealth,
)

@Serializable
data class CharacterMainEntityInfo(
    val level: Int,
    val entity: DnDFullEntity,
    val subEntity: DnDFullEntity?
) {
    operator fun contains(element: Uuid) = entity.id == element || subEntity?.id == element
}
