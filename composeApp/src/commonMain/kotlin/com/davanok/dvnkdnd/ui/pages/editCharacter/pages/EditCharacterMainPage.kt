package com.davanok.dvnkdnd.ui.pages.editCharacter.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityLink
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.ui.pages.editCharacter.EditCharacterScreenEvent
import kotlin.uuid.Uuid

@Immutable
private data class EditCharacterMainPageUiState(
    val character: CharacterBase,
    val entities: List<CharacterMainEntityInfo>
)
private fun CharacterFull.toEditCharacterMainPageUiState() = EditCharacterMainPageUiState(
    character = character,
    entities = mainEntities
)


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
        setEntityLevel = { entityId, level -> eventSink(EditCharacterScreenEvent.SetCharacterEntityLevel(entityId, level)) },
        addEntity = { eventSink(EditCharacterScreenEvent.AddCharacterEntity(it)) },
        removeEntity = { eventSink(EditCharacterScreenEvent.RemoveCharacterEntity(it)) },
        modifier = modifier
    )
}

@Composable
private fun Content(
    uiState: EditCharacterMainPageUiState,
    onUpdateCharacterBase: (CharacterBase) -> Unit,
    setEntityLevel: (entityId: Uuid, level: Int) -> Unit,
    addEntity: (CharacterMainEntityLink) -> Unit,
    removeEntity: (CharacterMainEntityLink) -> Unit,
    modifier: Modifier = Modifier
) {
    Scaffold(
        modifier = modifier,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    // TODO: Implement the logic to create a CharacterMainEntityLink
                    // and pass it to addEntity. usually opens a bottom sheet or dialog.
                }
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Class/Entity"
                )
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Section 1: Basic Character Info
            CharacterHeaderSection(
                character = uiState.character,
                onUpdate = onUpdateCharacterBase
            )

            HorizontalDivider()

            // Section 2: Entities (Classes, Races, etc.)
            EntitiesSection(
                entities = uiState.entities,
                onLevelChange = setEntityLevel,
                onRemove = {
                    // TODO: Construct the specific CharacterMainEntityLink required to remove this entity
                    // removeEntity(it)
                }
            )

            // Spacer to avoid FAB overlap at bottom
            Spacer(modifier = Modifier.height(80.dp))
        }
    }
}
@Composable
private fun CharacterHeaderSection(
    character: CharacterBase,
    onUpdate: (CharacterBase) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Character Details",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        OutlinedTextField(
            value = character.name,
            onValueChange = { onUpdate(character.copy(name = it)) },
            label = { Text("Character Name") },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )

        OutlinedTextField(
            value = character.description,
            onValueChange = { onUpdate(character.copy(description = it)) },
            label = { Text("Description/Backstory") },
            modifier = Modifier.fillMaxWidth(),
            minLines = 3,
            maxLines = 5
        )

        // Character Level (if separate from class levels)
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = "Total Level", style = MaterialTheme.typography.bodyLarge)
            LevelStepper(
                currentLevel = character.level,
                onLevelChange = { newLevel ->
                    onUpdate(character.copy(level = newLevel))
                }
            )
        }
    }
}
@Composable
private fun EntitiesSection(
    entities: List<CharacterMainEntityInfo>,
    onLevelChange: (Uuid, Int) -> Unit,
    onRemove: (CharacterMainEntityInfo) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text(
            text = "Classes & Abilities",
            style = MaterialTheme.typography.titleLarge,
            color = MaterialTheme.colorScheme.primary
        )

        if (entities.isEmpty()) {
            Text(
                text = "No classes added yet.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(vertical = 8.dp)
            )
        } else {
            entities.forEach { info ->
                EntityCard(
                    info = info,
                    onLevelChange = { newLvl -> onLevelChange(info.entity.entity.id, newLvl) },
                    onRemove = { onRemove(info) }
                )
            }
        }
    }
}
@Composable
private fun EntityCard(
    info: CharacterMainEntityInfo,
    onLevelChange: (Int) -> Unit,
    onRemove: () -> Unit
) {
    Card {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // Main Entity Name (e.g., "Wizard")
                    Text(
                        text = info.entity.entity.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    // Source (e.g., "Player's Handbook")
                    Text(
                        text = info.entity.entity.source,
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                IconButton(onClick = onRemove) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Remove Class",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Sub Entity Name (e.g., "School of Evocation")
            if (info.subEntity != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Subclass: ${info.subEntity.entity.name}",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.secondary
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Level Control
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Class Level",
                    style = MaterialTheme.typography.labelLarge
                )
                LevelStepper(
                    currentLevel = info.level,
                    onLevelChange = onLevelChange,
                    minLevel = 1,
                    maxLevel = 20
                )
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
            onClick = { if (currentLevel > minLevel) onLevelChange(currentLevel - 1) },
            enabled = currentLevel > minLevel
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Decrease Level"
            )
        }

        Text(
            text = "$currentLevel",
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp)
        )

        IconButton(
            onClick = { if (currentLevel < maxLevel) onLevelChange(currentLevel + 1) },
            enabled = currentLevel < maxLevel
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Increase Level"
            )
        }
    }
}