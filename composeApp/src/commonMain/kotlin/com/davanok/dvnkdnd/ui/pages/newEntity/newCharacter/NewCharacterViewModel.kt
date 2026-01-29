package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import com.davanok.dvnkdnd.core.utils.enumValueOfOrNull
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.dnd.proficiencyBonusByLevel
import com.davanok.dvnkdnd.domain.entities.DatabaseImage
import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.domain.entities.character.CharacterSelectedModifiers
import com.davanok.dvnkdnd.domain.entities.character.CharacterWithAllModifiers
import com.davanok.dvnkdnd.domain.entities.character.CharacterWithHealth
import com.davanok.dvnkdnd.domain.entities.character.CoinsGroup
import com.davanok.dvnkdnd.domain.entities.character.DnDValueModifierWithResolvedValue
import com.davanok.dvnkdnd.domain.entities.character.ValueModifiersGroupWithResolvedValues
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.domain.entities.dndEntities.EntityBase
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import com.davanok.dvnkdnd.domain.repositories.local.CharactersRepository
import com.davanok.dvnkdnd.domain.repositories.local.FilesRepository
import com.davanok.dvnkdnd.domain.repositories.local.FullEntitiesRepository
import com.davanok.dvnkdnd.domain.repositories.remote.BrowseRepository
import com.davanok.dvnkdnd.domain.values.FilePaths
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMain
import okio.Path
import kotlin.uuid.Uuid

class NewCharacterViewModel(
    private val fullEntitiesRepository: FullEntitiesRepository,
    private val browseRepository: BrowseRepository,
    private val charactersRepository: CharactersRepository,
    private val filesRepository: FilesRepository
) : ViewModel() {
    private var newCharacterState = NewCharacter()

    fun clear() {
        newCharacterState = newCharacterState.copy(
            character = NewCharacterWithFullEntities(),
            selectedModifiers = emptySet()
        )
    }

    private suspend fun loadEntities(entitiesIds: List<Uuid>): List<DnDFullEntity> {
        val entities =
            fullEntitiesRepository.getFullEntities(entitiesIds).getOrThrow().toMutableList()
        val dbEntitiesIds = entities.fastMap { it.entity.id }.toSet()
        val comparison = entitiesIds.subtract(dbEntitiesIds)
        if (comparison.isNotEmpty()) {
            val loaded = browseRepository.loadEntitiesFullInfo(comparison.toList()).getOrThrow()
            fullEntitiesRepository.insertFullEntities(loaded).getOrThrow()
            entities.addAll(loaded)
        }
        return entities
    }

    private suspend fun setCharacterEntities(character: NewCharacterMain) {
        val entities = loadEntities(character.getEntitiesIds())

        val mainTypes = setOf(DnDEntityTypes.CLASS, DnDEntityTypes.RACE, DnDEntityTypes.BACKGROUND)
        val mainEntities = entities.fastFilter { it.entity.type in mainTypes }.fastMap { entity ->
            CharacterMainEntityInfo(
                level = newCharacterState.level,
                entity = entity,
                subEntity = entities.fastFirstOrNull { it.entity.parentId == entity.entity.id }
            )
        }

        val fullCharacter = character.run {
            NewCharacterWithFullEntities(
                images = images,
                mainImage = mainImage,
                name = name,
                description = description,
                mainEntities = mainEntities
            )
        }
        newCharacterState = newCharacterState.copy(
            character = fullCharacter,
            selectedModifiers = fullCharacter.getNotSelectableModifiers()
        )
    }

    fun getCharacterMain() = newCharacterState.character.toNewCharacterMain()
    suspend fun setCharacterMain(
        character: NewCharacterMain
    ): Result<Unit> = runCatching {
        val oldIds = newCharacterState.character.entities.fastMap { it.entity.id }
        val newIds = character.getEntitiesIds()

        if (oldIds != newIds) {
            clear()
            setCharacterEntities(character)
        }
    }

    fun getCharacterWithAllModifiers() = newCharacterState.run {
        CharacterWithAllModifiers(
            character = toCharacterBase(),
            attributes = attributes,
            selectedModifiers = selectedModifiers,
            modifierGroups = character.entities.flatMap { entity ->
                entity.modifiersGroups.map { group ->
                    ValueModifiersGroupWithResolvedValues(
                        id = group.id,
                        name = group.name,
                        description = group.description,
                        selectionLimit = group.selectionLimit,
                        modifiers = group.modifiers
                            .filterIsInstance<DnDValueModifier>()
                            .map { modifier ->
                                DnDValueModifierWithResolvedValue(
                                    modifier = modifier,
                                    resolvedValue = resolveModifierValueSource(
                                        source = modifier.sourceType,
                                        valueSourceTarget = modifier.sourceKey,
                                        entityId = entity.entity.id
                                    )
                                )
                            }
                    )
                }
            }
        )
    }

    fun setCharacterModifiers(attributes: AttributesGroup, selectedModifiers: List<Uuid>) = runCatching {
        newCharacterState = newCharacterState.copy(
            attributes = attributes,
            selectedModifiers = newCharacterState.selectedModifiers + selectedModifiers
        )
    }
    fun setCharacterSelectedModifiers(selectedModifiers: Set<Uuid>) = runCatching {
        newCharacterState = newCharacterState.copy(
            selectedModifiers = newCharacterState.selectedModifiers + selectedModifiers
        )
    }

    fun getCharacterWithHealth() = newCharacterState.run {
        CharacterWithHealth(
            character = toCharacterBase(),
            healthDice = character.mainEntities.fastFirstOrNull { it.entity.entity.type == DnDEntityTypes.CLASS }?.entity?.cls?.hitDice,
            constitution = attributes.constitution,
            baseHealth = baseHealth
        )
    }
    fun setCharacterHealth(baseHealth: Int) = runCatching {
        newCharacterState = newCharacterState.copy(
            baseHealth = baseHealth
        )
    }

    suspend fun saveCharacter() = runCatching {
        var mainImage: Path? = null
        val images = newCharacterState.character.images.map {
            val newFilename = filesRepository.getFilename(
                FilePaths.images,
                "png",
                false
            )
            filesRepository.move(
                from = it,
                to = newFilename
            )
            if (it == newCharacterState.character.mainImage)
                mainImage = newFilename
            newFilename
        }
        charactersRepository.saveCharacter(
            newCharacterState
                .copy(character = newCharacterState.character.copy(images = images, mainImage = mainImage))
                .toCharacterFull(Uuid.random())
        ).getOrThrow()
    }
}

private data class NewCharacterWithFullEntities(
    val images: List<Path> = emptyList(),
    val mainImage: Path? = null,
    val name: String = "",
    val description: String = "",
    val mainEntities: List<CharacterMainEntityInfo> = emptyList()
) {
    val entities: List<DnDFullEntity>
        get() = mainEntities.fastFlatMap { listOfNotNull(it.entity, it.subEntity) }

    fun toNewCharacterMain(): NewCharacterMain {
        val clsInfo = mainEntities.fastFirstOrNull { it.entity.entity.type == DnDEntityTypes.CLASS }
        val raceInfo = mainEntities.fastFirstOrNull { it.entity.entity.type == DnDEntityTypes.RACE }
        val backgroundInfo = mainEntities.fastFirstOrNull { it.entity.entity.type == DnDEntityTypes.BACKGROUND }

        fun CharacterMainEntityInfo.toEntityWithSubEntities() =
            entity.entity.toEntityWithSubEntities(listOfNotNull(subEntity?.toDnDEntityMin()))

        return NewCharacterMain(
            images = images,
            mainImage = mainImage,
            name = name,
            description = description,
            cls = clsInfo?.toEntityWithSubEntities(),
            subCls = clsInfo?.subEntity?.toDnDEntityMin(),
            race = raceInfo?.toEntityWithSubEntities(),
            subRace = raceInfo?.subEntity?.toDnDEntityMin(),
            background = backgroundInfo?.toEntityWithSubEntities(),
            subBackground = backgroundInfo?.subEntity?.toDnDEntityMin()
        )
    }

    fun getNotSelectableModifiers(): Set<Uuid> = buildSet {
        entities.flatMap { it.modifiersGroups }.forEach { group ->
            if (group.selectionLimit <= 1 || group.selectionLimit > group.modifiers.size)
                addAll(group.modifiers.map { it.id })
        }
    }

    companion object {
        private fun EntityBase.toEntityWithSubEntities(subEntities: List<DnDEntityMin>) =
            DnDEntityWithSubEntities(
                id = id,
                type = type,
                name = name,
                description = description,
                source = source,
                subEntities = subEntities
            )
    }
}

private data class NewCharacter(
    val character: NewCharacterWithFullEntities = NewCharacterWithFullEntities(),

    val attributes: AttributesGroup = AttributesGroup.Default,
    val selectedModifiers: Set<Uuid> = emptySet(),

    val level: Int = 1,
    val proficiencyBonus: Int = proficiencyBonusByLevel(level),

    val baseHealth: Int = 0 // without constitution bonus
) {
    fun toCharacterFull(id: Uuid = Uuid.NIL): CharacterFull {
        val characterBase = toCharacterBase(id)

        val dbImages = character.images
            .map { path -> DatabaseImage(Uuid.random(), path.toString(), false) }


        val totalHealth = CharacterHealth(
            max = baseHealth,
            current = baseHealth,
            temp = 0
        )

        return CharacterFull(
            character = characterBase,
            images = dbImages,
            coins = CoinsGroup(),
            attributes = attributes,
            health = totalHealth,
            usedSpells = emptyMap(),
            mainEntities = character.mainEntities,
            feats = emptyList(),
            selectedModifiers = CharacterSelectedModifiers(valueModifiers = selectedModifiers),
            selectedProficiencies = emptySet(),
            customModifiers = emptyList()
        )
    }

    fun toCharacterBase(id: Uuid = Uuid.NIL) = CharacterBase(
        id = id,
        userId = null,
        name = character.name,
        description = character.description,
        level = level
    )

    fun resolveModifierValueSource(
        source: ValueSourceType,
        valueSourceTarget: String?,
        entityId: Uuid?
    ): Int = when(source) {
        ValueSourceType.FLAT -> null
        ValueSourceType.CHARACTER_LEVEL -> level
        ValueSourceType.ENTITY_LEVEL -> entityId
            ?.let { id -> character.mainEntities.firstOrNull { id in it }?.level }
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
    }.let { it ?: 0 }}