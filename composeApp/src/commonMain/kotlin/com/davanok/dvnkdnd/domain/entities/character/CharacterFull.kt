package com.davanok.dvnkdnd.domain.entities.character

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.core.utils.enumValueOfOrNull
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.dnd.calculateSpellDifficultyClass
import com.davanok.dvnkdnd.domain.dnd.proficiencyBonusByLevel
import com.davanok.dvnkdnd.domain.entities.DatabaseImage
import com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers
import com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateSpellSlots
import com.davanok.dvnkdnd.domain.entities.character.characterUtils.getAppliedValues
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.SkillsGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.CasterProgression
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierValueSource
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid

@Serializable
@Immutable
data class CharacterFull(
    val character: CharacterBase,

    val optionalValues: CharacterOptionalValues = CharacterOptionalValues(),

    val images: List<DatabaseImage> = emptyList(),
    val coins: CoinsGroup = CoinsGroup(),

    val items: List<CharacterItem> = emptyList(),
    val spells: List<CharacterSpell> = emptyList(),

    val attributes: AttributesGroup = AttributesGroup.Default,
    val health: CharacterHealth = CharacterHealth(),
    val usedItemActivations: Map<Uuid, Int> = emptyMap(),
    val usedSpells: Map<Uuid?, IntArray> = emptyMap(),

    val mainEntities: List<CharacterMainEntityInfo> = emptyList(),

    val feats: List<DnDFullEntity> = emptyList(),

    val selectedModifiers: Set<Uuid> = emptySet(),
    val selectedProficiencies: Set<Uuid> = emptySet(),

    val customModifiers: List<CustomModifier> = emptyList(),

    val states: List<CharacterState> = emptyList(),

    val notes: List<CharacterNote> = emptyList()
) {
    val entities: List<DnDFullEntity>
        get() = mainEntities
            .flatMap { listOfNotNull(it.entity, it.subEntity) } + feats + states.map { it.state }
    private val groupIdToEntityId by lazy {
        entities.flatMap { e -> e.modifiersGroups.map { it.id to e.entity.id } }.toMap()
    }

    val appliedValues: CharacterModifiedValues by lazy { getAppliedValues() }

    val proficiencyBonus: Int
        get() = optionalValues.proficiencyBonus ?: proficiencyBonusByLevel(character.level)

    fun resolveValueSource(source: DnDModifierValueSource, valueSourceTarget: String?, entityId: Uuid?, modifierValue: Double): Double =
        when(source) {
            DnDModifierValueSource.CONSTANT -> 0
            DnDModifierValueSource.CHARACTER_LEVEL -> character.level
            DnDModifierValueSource.ENTITY_LEVEL -> entityId
                ?.let { id -> mainEntities.firstOrNull { id in it }?.level }
            DnDModifierValueSource.PROFICIENCY_BONUS -> proficiencyBonus
            DnDModifierValueSource.ATTRIBUTE -> valueSourceTarget
                ?.let { enumValueOfOrNull<Attributes>(it) }
                ?.let { appliedValues.attributes[it] }
            DnDModifierValueSource.ATTRIBUTE_MODIFIER -> valueSourceTarget
                ?.let { enumValueOfOrNull<Attributes>(it) }
                ?.let { calculateModifier(appliedValues.attributes[it]) }
            DnDModifierValueSource.SAVING_THROW -> valueSourceTarget
                ?.let { enumValueOfOrNull<Attributes>(it) }
                ?.let { appliedValues.savingThrowModifiers[it] }
            DnDModifierValueSource.SKILL -> valueSourceTarget
                ?.let { enumValueOfOrNull<Skills>(it) }
                ?.let { appliedValues.skillModifiers[it] }
        }.let { it ?: 0 } + modifierValue

    fun resolveGroupValue(group: ModifiersGroup): Double =
        resolveValueSource(group.valueSource, group.valueSourceTarget, groupIdToEntityId[group.id], group.value)

    val appliedModifiers by lazy { calculateModifiers() }

    val spellSlots by lazy { calculateSpellSlots() }

    fun getSpellCastingValues(): SpellCastingValues {
        val spellCastingAttribute = mainEntities
            .mapNotNull { entityInfo ->
                val entity = when {
                    entityInfo.entity.entity.type != DnDEntityTypes.CLASS -> null
                    entityInfo.subEntity?.cls != null && entityInfo.subEntity.cls.caster != CasterProgression.NONE -> entityInfo.subEntity
                    entityInfo.entity.cls?.caster != CasterProgression.NONE -> entityInfo.entity
                    else -> null
                }
                entity?.cls?.primaryStats?.firstOrNull()
            }
            .maxByOrNull { attr -> appliedValues.attributes[attr] }
            ?: return SpellCastingValues(
                attackBonus = proficiencyBonus,
                saveDifficultyClass = calculateSpellDifficultyClass(proficiencyBonus, 10)
            )

        val attributeValue = appliedValues.attributes[spellCastingAttribute]
        val modifier = calculateModifier(attributeValue)
        val attackBonus = proficiencyBonus + modifier
        val saveDC = calculateSpellDifficultyClass(proficiencyBonus, attributeValue)

        return SpellCastingValues(
            attackBonus = attackBonus,
            saveDifficultyClass = saveDC
        )
    }
}

data class CharacterModifiedValues(
    val attributes: AttributesGroup,
    val savingThrowModifiers: AttributesGroup,
    val skillModifiers: SkillsGroup,
    val health: CharacterHealth,
    val initiative: Int,
    val armorClass: Int
)

@Serializable
data class CharacterMainEntityInfo(
    val level: Int,
    val entity: DnDFullEntity,
    val subEntity: DnDFullEntity?
) {
    operator fun contains(element: Uuid) = entity.entity.id == element || subEntity?.entity?.id == element
}

data class SpellCastingValues(
    val attackBonus: Int,
    val saveDifficultyClass: Int
)
