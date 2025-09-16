package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFilteredMap
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.character.CharacterClassInfo
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.character.CharacterShortInfo
import com.davanok.dvnkdnd.data.model.entities.character.CharacterWithAllModifiers
import com.davanok.dvnkdnd.data.model.entities.character.CharacterWithHealth
import com.davanok.dvnkdnd.data.model.entities.character.DnDCharacterHealth
import com.davanok.dvnkdnd.data.model.entities.character.toEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.util.proficiencyBonusByLevel
import com.davanok.dvnkdnd.data.repositories.BrowseRepository
import com.davanok.dvnkdnd.data.repositories.CharactersRepository
import com.davanok.dvnkdnd.data.repositories.FullEntitiesRepository
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.NewCharacterMain
import kotlinx.coroutines.launch
import okio.Path
import kotlin.uuid.Uuid

class NewCharacterViewModel(
    private val fullEntitiesRepository: FullEntitiesRepository,
    private val browseRepository: BrowseRepository,
    private val charactersRepository: CharactersRepository
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
        val dbEntitiesIds = entities.fastMap { it.id }
        val comparison = entitiesIds.subtract(dbEntitiesIds)
        if (comparison.isNotEmpty()) {
            val loaded = browseRepository.loadEntitiesFullInfo(comparison.toList()).getOrThrow()
            fullEntitiesRepository.insertFullEntities(loaded).getOrThrow()
            entities.addAll(loaded)
        }
        return entities
    }

    private fun setCharacterEntities(character: NewCharacterMain) = viewModelScope.launch {
        val entities = loadEntities(character.getEntitiesIds())
        val fullCharacter = character.run {
            NewCharacterWithFullEntities(
                images = images,
                mainImage = mainImage,
                name = name,
                description = description,
                cls = cls?.let { e -> entities.fastFirst { it.id == e.id } },
                subCls = subCls?.let { e -> entities.fastFirst { it.id == e.id } },
                race = race?.let { e -> entities.fastFirst { it.id == e.id } },
                subRace = subRace?.let { e -> entities.fastFirst { it.id == e.id } },
                background = background?.let { e -> entities.fastFirst { it.id == e.id } },
                subBackground = subBackground?.let { e -> entities.fastFirst { it.id == e.id } }
            )
        }
        newCharacterState = newCharacterState.copy(
            character = fullCharacter,
            selectedModifiers = fullCharacter.getNotSelectableModifiers()
        )
    }

    fun getCharacterShortInfo() = newCharacterState.character.run {
        CharacterShortInfo(
            name = name,
            image = mainImage,
            className = cls?.name,
            subClassName = subCls?.name,
            raceName = race?.name,
            subRaceName = subRace?.name,
            backgroundName = background?.name,
            subBackgroundName = subBackground?.name
        )
    }

    fun getCharacterMain() = newCharacterState.character.toNewCharacterMain()
    fun setCharacterMain(
        character: NewCharacterMain
    ): Result<Unit> = runCatching {
        val oldIds = newCharacterState.character.entities.fastMap { it.id }
        val newIds = character.getEntitiesIds()

        if (oldIds != newIds) {
            clear()
            setCharacterEntities(character)
        }
    }

    fun getCharacterWithAllModifiers() = newCharacterState.run {
        CharacterWithAllModifiers(
            character = character.toCharacterShortInfo(),
            proficiencyBonus = proficiencyBonus,
            characterAttributes = characterAttributes,
            selectedModifiers = selectedModifiers,
            entities = character.entities
                .fastFilteredMap(
                    predicate = { it.modifiersGroups.isNotEmpty() },
                    transform = { it.toEntityWithModifiers() }
                )
        )
    }

    fun setCharacterModifiers(attributes: DnDAttributesGroup, selectedModifiers: List<Uuid>) = runCatching {
        newCharacterState = newCharacterState.copy(
            characterAttributes = attributes,
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
            character = character.toCharacterShortInfo(),
            healthDice = character.cls?.cls?.hitDice,
            constitution = characterAttributes.constitution,
            baseHealth = baseHealth
        )
    }
    fun setCharacterHealth(baseHealth: Int) = runCatching {
        newCharacterState = newCharacterState.copy(
            baseHealth = baseHealth
        )
    }

    suspend fun saveCharacter() =
        charactersRepository.saveCharacter(newCharacterState.toCharacterFull()).onSuccess {
            newCharacterState = NewCharacter()
        }
}

private data class NewCharacterWithFullEntities(
    val images: List<Path> = emptyList(),
    val mainImage: Path? = null,
    val name: String = "",
    val description: String = "",
    val cls: DnDFullEntity? = null,
    val subCls: DnDFullEntity? = null,
    val race: DnDFullEntity? = null,
    val subRace: DnDFullEntity? = null,
    val background: DnDFullEntity? = null,
    val subBackground: DnDFullEntity? = null
) {
    val entities: List<DnDFullEntity>
        get() = listOfNotNull(
            cls,
            subCls,
            race,
            subRace,
            background,
            subBackground
        )

    fun toNewCharacterMain() = NewCharacterMain(
        images = images,
        mainImage = mainImage,
        name = name,
        description = description,
        cls = cls?.toEntityWithSubEntities(listOfNotNull(subCls?.toDnDEntityMin())),
        subCls = subCls?.toDnDEntityMin(),
        race = race?.toEntityWithSubEntities(listOfNotNull(subRace?.toDnDEntityMin())),
        subRace = subRace?.toDnDEntityMin(),
        background = background?.toEntityWithSubEntities(listOfNotNull(subBackground?.toDnDEntityMin())),
        subBackground = subBackground?.toDnDEntityMin()
    )

    fun getNotSelectableModifiers(): Set<Uuid> =
        entities
            .fastFlatMap { it.modifiersGroups }
            .fastFlatMap { it.modifiers }
            .fastFilteredMap(
                predicate = { !it.selectable },
                transform = { it.id }
            )
            .toSet()

    fun toCharacterShortInfo() = CharacterShortInfo(
        name = name,
        image = mainImage,
        className = cls?.name,
        subClassName = subCls?.name,
        raceName = race?.name,
        subRaceName = subRace?.name,
        backgroundName = background?.name,
        subBackgroundName = subBackground?.name
    )

    companion object {
        private fun DnDFullEntity.toEntityWithSubEntities(subEntities: List<DnDEntityMin>) =
            DnDEntityWithSubEntities(
                id = id,
                type = type,
                name = name,
                source = source,
                subEntities = subEntities
            )
    }
}

private data class NewCharacter(
    val character: NewCharacterWithFullEntities = NewCharacterWithFullEntities(),

    val characterAttributes: DnDAttributesGroup = DnDAttributesGroup.Default,
    val selectedModifiers: Set<Uuid> = emptySet(),

    val level: Int = 1,
    val proficiencyBonus: Int = proficiencyBonusByLevel(level),

    val baseHealth: Int = 0 // without constitution bonus
) {
    fun toCharacterFull(): CharacterFull {
        val characterMin = CharacterMin(
            id = Uuid.random(),
            userId = null,
            name = character.name,
            level = 1,
            image = character.mainImage,
        )

        val dbImages = character.images.map { path -> DatabaseImage(Uuid.random(), path) }

        val classes = character.cls?.let {
            listOf(
                CharacterClassInfo(
                    level = 1,
                    cls = it,
                    subCls = character.subCls
                )
            )
        } ?: emptyList()


        val totalHealth = DnDCharacterHealth(
            max = baseHealth,
            current = baseHealth,
            temp = 0,
            maxModifier = 0
        )

        return CharacterFull(
            character = characterMin,
            images = dbImages,
            coins = null,
            attributes = characterAttributes,
            health = totalHealth,
            usedSpells = emptyList(),
            classes = classes,
            race = character.race,
            subRace = character.subRace,
            background = character.background,
            subBackground = character.subBackground,
            feats = emptyList(),
            selectedModifiers = selectedModifiers.toList(),
            selectedProficiencies = emptyList()
        )
    }
}