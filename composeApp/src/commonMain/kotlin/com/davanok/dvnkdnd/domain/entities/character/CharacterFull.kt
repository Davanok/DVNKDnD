package com.davanok.dvnkdnd.domain.entities.character

import androidx.compose.runtime.Immutable
import com.davanok.dvnkdnd.core.utils.enumValueOfOrNull
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.dnd.calculateSpellDifficultyClass
import com.davanok.dvnkdnd.domain.dnd.proficiencyBonusByLevel
import com.davanok.dvnkdnd.domain.entities.DatabaseImage
import com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateSpellSlots
import com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateValueModifiers
import com.davanok.dvnkdnd.domain.entities.character.characterUtils.findAttacks
import com.davanok.dvnkdnd.domain.entities.character.characterUtils.getAppliedValues
import com.davanok.dvnkdnd.domain.entities.character.characterUtils.getEntitiesWithLevel
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.SkillsGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.CasterProgression
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
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

    val customModifiers: List<CharacterCustomModifier>,

    val selectedModifiers: CharacterSelectedModifiers,
    val selectedProficiencies: Set<Uuid> = emptySet(),

    val states: List<CharacterState> = emptyList(),

    val notes: List<CharacterNote> = emptyList()
) {
    val entitiesWithLevel: List<Pair<DnDFullEntity, Int>> by lazy { getEntitiesWithLevel() }

    val entities = entitiesWithLevel.map { it.first }

    val appliedValues: CharacterModifiedValues by lazy { getAppliedValues() }

    val proficiencyBonus: Int
        get() = optionalValues.proficiencyBonus ?: proficiencyBonusByLevel(character.level)

    fun resolveValueSource(
        source: ValueSourceType,
        valueSourceTarget: String?,
        entityId: Uuid?
    ): Int = when(source) {
        ValueSourceType.FLAT -> null
        ValueSourceType.CHARACTER_LEVEL -> character.level
        ValueSourceType.ENTITY_LEVEL -> entityId
            ?.let { id -> mainEntities.firstOrNull { id in it }?.level }
        ValueSourceType.PROFICIENCY_BONUS -> proficiencyBonus
        ValueSourceType.ATTRIBUTE_MODIFIER -> valueSourceTarget
            ?.let { enumValueOfOrNull<Attributes>(it) }
            ?.let { calculateModifier(attributes[it]) }
        ValueSourceType.ATTRIBUTE -> valueSourceTarget
            ?.let { enumValueOfOrNull<Attributes>(it) }
            ?.let { attributes[it] }
        ValueSourceType.SKILL_MODIFIER -> valueSourceTarget
            ?.let { enumValueOfOrNull<Skills>(it) }
            ?.let { attributes[it.attribute] }
    }.let { it ?: 0 }

    val appliedModifiers by lazy { calculateValueModifiers() }

    val spellSlots by lazy { calculateSpellSlots() }

    val attacks by lazy { findAttacks() }

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
    val derivedStats: CharacterDerivedValues,
    val speed: CharacterSpeed
)

@Serializable
data class CharacterSelectedModifiers(
    val valueModifiers: Set<Uuid> = emptySet(),
    val rollModifiers: Set<Uuid> = emptySet(),
    val damageModifiers: Set<Uuid> = emptySet()
) {
    operator fun contains(element: Uuid) =
        element in valueModifiers || element in rollModifiers || element in damageModifiers

    fun toSet(): Set<Uuid> = valueModifiers + rollModifiers + damageModifiers
}

data class SpellCastingValues(
    val attackBonus: Int,
    val saveDifficultyClass: Int
)
