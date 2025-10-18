package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFilteredMap
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.character.CharacterBase
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.data.model.entities.character.CharacterWithAllModifiers
import com.davanok.dvnkdnd.data.model.entities.character.CharacterWithHealth
import com.davanok.dvnkdnd.data.model.entities.character.CoinsGroup
import com.davanok.dvnkdnd.data.model.entities.character.DnDCharacterHealth
import com.davanok.dvnkdnd.data.model.entities.character.toEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
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

        val mainTypes = setOf(DnDEntityTypes.CLASS, DnDEntityTypes.RACE, DnDEntityTypes.BACKGROUND)
        val mainEntities = entities.fastFilter { it.type in mainTypes }.fastMap { entity ->
            CharacterMainEntityInfo(
                level = newCharacterState.level,
                entity = entity,
                subEntity = entities.fastFirstOrNull { it.parentId == entity.id }
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
            character = toCharacterBase(),
            attributes = characterAttributes,
            selectedModifiers = selectedModifiers,
            entitiesWithLevel = character.entitiesWithLevel
                .fastFilteredMap(
                    predicate = { it.first.modifiersGroups.isNotEmpty() },
                    transform = { (e, l) -> e.toEntityWithModifiers() to l }
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
            character = toCharacterBase(),
            healthDice = character.mainEntities.fastFirstOrNull { it.entity.type == DnDEntityTypes.CLASS }?.entity?.cls?.hitDice,
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
    val mainEntities: List<CharacterMainEntityInfo> = emptyList()
) {
    val entities: List<DnDFullEntity>
        get() = mainEntities.fastFlatMap { listOfNotNull(it.entity, it.subEntity) }
    val entitiesWithLevel: List<Pair<DnDFullEntity, Int>>
        get() = mainEntities.fastFlatMap {
            listOfNotNull(it.entity, it.subEntity)
                .fastMap { e -> e to it.level }
        }

    fun toNewCharacterMain(): NewCharacterMain {
        val clsInfo = mainEntities.fastFirstOrNull { it.entity.type == DnDEntityTypes.CLASS }
        val raceInfo = mainEntities.fastFirstOrNull { it.entity.type == DnDEntityTypes.RACE }
        val backgroundInfo = mainEntities.fastFirstOrNull { it.entity.type == DnDEntityTypes.BACKGROUND }

        fun CharacterMainEntityInfo.toEntityWithSubEntities() =
            entity.toEntityWithSubEntities(listOfNotNull(subEntity?.toDnDEntityMin()))

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

    fun getNotSelectableModifiers(): Set<Uuid> =
        entities
            .fastFlatMap { it.modifiersGroups }
            .fastFlatMap { it.modifiers }
            .fastFilteredMap(
                predicate = { !it.selectable },
                transform = { it.id }
            )
            .toSet()

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

    val baseHealth: Int = 0 // without constitution bonus
) {
    fun toCharacterFull(): CharacterFull {
        val characterBase = toCharacterBase()

        val dbImages = character.images.map { path -> DatabaseImage(Uuid.random(), path) }


        val totalHealth = DnDCharacterHealth(
            max = baseHealth,
            current = baseHealth,
            temp = 0
        )

        return CharacterFull(
            character = characterBase,
            images = dbImages,
            coins = CoinsGroup(),
            attributes = characterAttributes,
            health = totalHealth,
            usedSpells = emptyList(),
            mainEntities = character.mainEntities,
            feats = emptyList(),
            selectedModifiers = selectedModifiers,
            selectedProficiencies = emptySet()
        )
    }

    fun toCharacterBase() = CharacterBase(
        id = Uuid.NIL,
        userId = null,
        name = character.name,
        description = character.description,
        level = level,
    )
}