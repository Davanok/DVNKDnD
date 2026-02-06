package com.davanok.dvnkdnd.ui.pages.editCharacter.pages

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.ToggleFloatingActionButtonDefaults.animateIcon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.rememberVectorPainter
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityLink
import com.davanok.dvnkdnd.domain.entities.character.toCharacterMainEntityLink
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.ui.components.DefaultFloatingActionMenu
import com.davanok.dvnkdnd.ui.components.NavigationEventHandler
import com.davanok.dvnkdnd.ui.components.SwipeToActionBox
import com.davanok.dvnkdnd.ui.components.rememberCollapsingNestedScrollConnection
import com.davanok.dvnkdnd.ui.pages.editCharacter.EditCharacterScreenEvent
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.backgrounds
import dvnkdnd.composeapp.generated.resources.character_description
import dvnkdnd.composeapp.generated.resources.character_details
import dvnkdnd.composeapp.generated.resources.character_level_error
import dvnkdnd.composeapp.generated.resources.character_name
import dvnkdnd.composeapp.generated.resources.character_total_level
import dvnkdnd.composeapp.generated.resources.classes
import dvnkdnd.composeapp.generated.resources.decrease_level
import dvnkdnd.composeapp.generated.resources.entity_level
import dvnkdnd.composeapp.generated.resources.increase_level
import dvnkdnd.composeapp.generated.resources.no_backgrounds_added_yet
import dvnkdnd.composeapp.generated.resources.no_classes_added_yet
import dvnkdnd.composeapp.generated.resources.no_races_added_yet
import dvnkdnd.composeapp.generated.resources.races
import dvnkdnd.composeapp.generated.resources.remove
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid
@Immutable
data class EditCharacterMainPageUiState(
    val character: CharacterBase,
    val levelErrors: Map<DnDEntityTypes, Int>,
    val entities: Map<DnDEntityTypes, List<CharacterMainEntityInfo>>
)

private fun CharacterFull.toEditCharacterMainPageUiState(): EditCharacterMainPageUiState {
    val groupedEntities = mainEntities.groupBy { it.entity.entity.type }

    val errors = groupedEntities
        .mapValues { it.value.sumOf { entity -> entity.level } }
        .filter { (type, totalLevel) ->
            val isClass = type == DnDEntityTypes.CLASS
            val hasHighLevelEntity = groupedEntities[type]?.any { it.level > 1 } == true

            totalLevel != character.level && (isClass || hasHighLevelEntity)
        }

    return EditCharacterMainPageUiState(
        character = character,
        levelErrors = errors,
        entities = groupedEntities
    )
}

// --- Main Composable ---

@Composable
fun EditCharacterMainPage(
    character: CharacterFull,
    eventSink: (EditCharacterScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by remember(character) {
        derivedStateOf { character.toEditCharacterMainPageUiState() }
    }

    Content(
        uiState = uiState,
        onUpdateCharacterBase = { eventSink(EditCharacterScreenEvent.UpdateCharacterBase(it)) },
        setEntityLevel = { entityId, level ->
            eventSink(EditCharacterScreenEvent.SetCharacterEntityLevel(entityId, level))
        },
        showAddEntityDialog = { eventSink(EditCharacterScreenEvent.ShowAddEntityDialog(it)) },
        removeEntity = { eventSink(EditCharacterScreenEvent.RemoveCharacterEntity(it)) },
        modifier = modifier
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Content(
    uiState: EditCharacterMainPageUiState,
    onUpdateCharacterBase: (CharacterBase) -> Unit,
    setEntityLevel: (entityId: Uuid, level: Int) -> Unit,
    showAddEntityDialog: (DnDEntityTypes) -> Unit,
    removeEntity: (CharacterMainEntityLink) -> Unit,
    modifier: Modifier = Modifier
) {
    var fabVisible by remember { mutableStateOf(true) }

    val nestedScrollConnection = rememberCollapsingNestedScrollConnection { fabVisible = it }

    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            AddEntityFloatingActionButton(
                fabVisible = fabVisible,
                onAddNewClick = showAddEntityDialog
            )
        }
    ) { innerPadding ->
        LazyColumn(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .nestedScroll(nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            stickyHeader {
                Surface(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = stringResource(Res.string.character_details),
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.primary,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp)
                    )
                }
            }
            item {
                CharacterHeaderSection(
                    character = uiState.character,
                    levelErrors = uiState.levelErrors,
                    onUpdate = onUpdateCharacterBase,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
            }

            item {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 8.dp))
            }

            // Render sections
            val typesToRender = listOf(
                DnDEntityTypes.CLASS,
                DnDEntityTypes.RACE,
                DnDEntityTypes.BACKGROUND
            )

            typesToRender.forEach { entityType ->
                entitiesSection(
                    entityType = entityType,
                    entities = uiState.entities[entityType] ?: emptyList(),
                    setEntityLevel = setEntityLevel,
                    removeEntity = removeEntity
                )
            }

            // Bottom spacing
            item { Spacer(Modifier.height(80.dp)) }
        }
    }
}

// --- List Extension for Cleaner Sections ---

private fun LazyListScope.entitiesSection(
    entityType: DnDEntityTypes,
    entities: List<CharacterMainEntityInfo>,
    setEntityLevel: (Uuid, Int) -> Unit,
    removeEntity: (CharacterMainEntityLink) -> Unit
) {
    stickyHeader {
        Surface(modifier = Modifier.fillMaxWidth()) {
            val stringRes = when (entityType) {
                DnDEntityTypes.CLASS -> Res.string.classes
                DnDEntityTypes.RACE -> Res.string.races
                DnDEntityTypes.BACKGROUND -> Res.string.backgrounds
                else -> throw IllegalStateException("Unknown entity type")
            }
            Text(
                text = stringResource(stringRes),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
    }

    if (entities.isEmpty()) {
        item {
            val stringRes = when (entityType) {
                DnDEntityTypes.CLASS -> Res.string.no_classes_added_yet
                DnDEntityTypes.RACE -> Res.string.no_races_added_yet
                DnDEntityTypes.BACKGROUND -> Res.string.no_backgrounds_added_yet
                else -> throw IllegalStateException()
            }
            Text(
                text = stringResource(stringRes),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 8.dp)
            )
        }
    } else {
        items(
            items = entities,
            key = { it.entity.entity.id } // Optimization: Unique Key
        ) { entity ->
            EntityCard(
                entity = entity,
                onLevelChange = { setEntityLevel(entity.entity.entity.id, it) },
                onRemove = { removeEntity(entity.toCharacterMainEntityLink()) },
                modifier = Modifier.padding(horizontal = 8.dp)
            )
        }
    }
}

// --- Components ---

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun AddEntityFloatingActionButton(
    fabVisible: Boolean,
    onAddNewClick: (DnDEntityTypes) -> Unit,
) {
    // Assuming DefaultFloatingActionMenu and NavigationEventHandler are custom/library components
    var fabMenuExpanded by rememberSaveable { mutableStateOf(false) }

    NavigationEventHandler(fabMenuExpanded) { fabMenuExpanded = false }

    val items = listOf(
        DnDEntityTypes.CLASS,
        DnDEntityTypes.RACE,
        DnDEntityTypes.BACKGROUND
    )

    DefaultFloatingActionMenu(
        fabVisible = fabVisible,
        fabIcon = {
            val imageVector by remember {
                derivedStateOf {
                    if (checkedProgress > 0.5f) Icons.Filled.Close else Icons.Filled.Add
                }
            }
            Icon(
                painter = rememberVectorPainter(imageVector),
                contentDescription = null,
                modifier = Modifier.animateIcon({ checkedProgress }),
            )
        }
    ) {
        items.forEach { item ->
            menuItem(
                onClick = { onAddNewClick(item) },
                text = { Text(text = stringResource(item.stringRes)) },
                icon = {
                    Icon(
                        imageVector = Icons.Default.Add, // Changed to a generic 'add' style icon
                        contentDescription = null
                    )
                }
            )
        }
    }
}

@Composable
private fun CharacterHeaderSection(
    character: CharacterBase,
    levelErrors: Map<DnDEntityTypes, Int>,
    onUpdate: (CharacterBase) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(8.dp),
        modifier = modifier
    ) {
        OutlinedTextField(
            value = character.name,
            onValueChange = { onUpdate(character.copy(name = it)) },
            label = { Text(text = stringResource(Res.string.character_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = character.description,
            onValueChange = { onUpdate(character.copy(description = it)) },
            label = { Text(text = stringResource(Res.string.character_description)) },
            modifier = Modifier.fillMaxWidth()
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(Res.string.character_total_level),
                style = MaterialTheme.typography.bodyLarge
            )

            LevelStepper(
                currentLevel = character.level,
                onLevelChange = { onUpdate(character.copy(level = it)) }
            )
        }

        levelErrors.forEach { (entityType, totalLevel) ->
            val entityStringRes = when (entityType) {
                DnDEntityTypes.CLASS -> Res.string.classes
                DnDEntityTypes.RACE -> Res.string.races
                DnDEntityTypes.BACKGROUND -> Res.string.backgrounds
                else -> return@forEach
            }

            Text(
                text = stringResource(
                    Res.string.character_level_error,
                    stringResource(entityStringRes),
                    totalLevel,
                    character.level
                ),
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun EntityCard(
    entity: CharacterMainEntityInfo,
    onLevelChange: (Int) -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier
) {
    SwipeToActionBox(
        modifier = modifier,
        onDismiss = { onRemove() },
        actionIcon = {
            Icon(imageVector = Icons.Default.Delete, null)
        }
    ) {
        OutlinedCard {
            Column(modifier = Modifier.padding(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    val entityBase = entity.entity.entity
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = entityBase.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = entityBase.source,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    IconButton(onClick = onRemove) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = stringResource(Res.string.remove),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }

                if (entity.subEntity != null) {
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Subclass: ${entity.subEntity.entity.name}",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(Res.string.entity_level),
                        style = MaterialTheme.typography.labelLarge
                    )
                    LevelStepper(
                        currentLevel = entity.level,
                        onLevelChange = onLevelChange,
                        minLevel = 1,
                        maxLevel = 20
                    )
                }
            }
        }
    }
}

@Composable
private fun LevelStepper(
    currentLevel: Int,
    onLevelChange: (Int) -> Unit,
    minLevel: Int = 1,
    maxLevel: Int = 30
) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { onLevelChange(currentLevel - 1) },
            enabled = currentLevel > minLevel
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = stringResource(Res.string.decrease_level)
            )
        }

        Text(
            text = currentLevel.toString(),
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        IconButton(
            onClick = { onLevelChange(currentLevel + 1) },
            enabled = currentLevel < maxLevel
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = stringResource(Res.string.increase_level)
            )
        }
    }
}