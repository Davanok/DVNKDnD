package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.character.CharacterShortInfo
import com.davanok.dvnkdnd.data.model.entities.character.CharacterWithAllModifiers
import com.davanok.dvnkdnd.data.model.entities.character.CharacterWithAllSkills
import com.davanok.dvnkdnd.data.model.entities.character.toEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.character.toEntityWithSkills
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
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
            selectedModifierBonuses = emptyList(),
            selectedSkills = emptyList()
        )
    }

    private suspend fun loadEntities(entitiesIds: List<Uuid>): List<DnDFullEntity> {
        val entities = fullEntitiesRepository.getFullEntities(entitiesIds).getOrThrow().toMutableList()
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
            selectedModifierBonuses = fullCharacter.getNotSelectableModifiers(),
            selectedSkills = fullCharacter.getNotSelectableSkills()
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
        val oldIds = newCharacterState.character.run {
            listOf(
                cls?.id,
                subCls?.id,
                race?.id,
                subRace?.id,
                background?.id,
                subBackground?.id
            )
        }
        val newIds = character.run {
            listOf(
                cls?.id,
                subCls?.id,
                race?.id,
                subRace?.id,
                background?.id,
                subBackground?.id
            )
        }
        if (oldIds != newIds) {
            clear()
            setCharacterEntities(character)
        }
    }

    fun getCharacterWithAllModifiers() = newCharacterState.run {
        CharacterWithAllModifiers(
            character = character.toCharacterMin(),
            characterStats = characterStats,
            selectedModifierBonuses = selectedModifierBonuses,
            classes = listOfNotNull(character.cls, character.subCls)
                .fastMap { it.toEntityWithModifiers() },
            race = character.race?.toEntityWithModifiers(),
            subRace = character.subRace?.toEntityWithModifiers(),
            background = character.background?.toEntityWithModifiers(),
            subBackground = character.subBackground?.toEntityWithModifiers(),
        )
    }

    fun setCharacterModifiers(stats: DnDModifiersGroup, selectedBonuses: List<Uuid>) = runCatching {
        newCharacterState = newCharacterState.copy(
            characterStats = stats,
            selectedModifierBonuses = selectedBonuses
        )
    }

    fun getCharacterWithAllSkills() = newCharacterState.run {
        CharacterWithAllSkills(
            character = character.toCharacterMin(),
            selectedSkills = selectedSkills,
            classes = listOfNotNull(character.cls, character.subCls)
                .fastMap { it.toEntityWithSkills() },
            race = character.race?.toEntityWithSkills(),
            subRace = character.subRace?.toEntityWithSkills(),
            background = character.background?.toEntityWithSkills(),
            subBackground = character.subBackground?.toEntityWithSkills(),
        )
    }

    fun setCharacterSkills(selectedSkills: List<Uuid>) = runCatching {
        newCharacterState = newCharacterState.copy(
            selectedSkills = selectedSkills
        )
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

    fun toCharacterMin(id: Uuid = Uuid.NIL) = CharacterMin(
        id = id,
        userId = null,
        name = name,
        level = 1,
        image = mainImage
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

    fun getNotSelectableModifiers(): List<Uuid> =
        entities
            .fastFlatMap { it.modifierBonuses }
            .fastFilter { !it.selectable }
            .fastMap { it.id }

    fun getNotSelectableSkills(): List<Uuid> =
        entities
            .fastFlatMap { it.skills }
            .fastFilter { !it.selectable }
            .fastMap { it.id }

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

    val characterStats: DnDModifiersGroup = DnDModifiersGroup.Default,
    val selectedModifierBonuses: List<Uuid> = emptyList(),

    val selectedSkills: List<Uuid> = emptyList()
)