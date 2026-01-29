package com.davanok.dvnkdnd.ui.pages.characterFull.dialogs

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.ui.components.text.buildPreview
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.character_health_damage_button
import dvnkdnd.composeapp.generated.resources.character_health_heal_button
import dvnkdnd.composeapp.generated.resources.character_health_temp_health_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun CharacterHealthDialogContent(
    baseHealth: CharacterHealth,
    updateHealth: (CharacterHealth) -> Unit,
    healthModifiers: List<ValueModifierInfo>,
) {
    Column {
        var inputValue by remember { mutableStateOf("") }
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = inputValue,
            onValueChange = { inputValue = it },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            maxLines = 1,
            placeholder = { Text(text = "0") }
        )
        val healthModifierValue = inputValue.toIntOrNull()
        Row {
            Button(
                onClick = {
                    healthModifierValue?.let {
                        updateHealth(baseHealth.copy(current = baseHealth.current + it))
                    }
                },
                enabled = healthModifierValue != null
            ) {
                Text(
                    text = stringResource(Res.string.character_health_heal_button)
                )
            }
            Button(
                onClick = {
                    healthModifierValue?.let {
                        updateHealth(baseHealth.copy(current = baseHealth.current - it))
                    }
                },
                enabled = healthModifierValue != null
            ) {
                Text(
                    text = stringResource(Res.string.character_health_damage_button)
                )
            }
            Button(
                onClick = {
                    healthModifierValue?.let { updateHealth(baseHealth.copy(temp = it)) }
                },
                enabled = healthModifierValue != null
            ) {
                Text(
                    text = stringResource(Res.string.character_health_temp_health_button)
                )
            }
        }

        ModifiersText(healthModifiers)
    }
}

@Composable
fun ModifiersText(
    modifiers: List<ValueModifierInfo>,
    modifier: Modifier = Modifier
) {
    // 1. Grouping logic
    // Ideally, pass this pre-grouped from your ViewModel, but remember works here.
    val groupedModifiers = remember(modifiers) {
        modifiers.groupBy { it.group.id }
    }

    // 2. Use a Column (or LazyColumn if the list is long)
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(16.dp) // Space between groups
    ) {
        groupedModifiers.forEach { (_, groupItems) ->
            val firstItem = groupItems.first()

            ModifierGroupItem(
                title = firstItem.group.name,
                description = firstItem.group.description,
                items = groupItems.map { it.buildPreview() }
            )
        }
    }
}

@Composable
private fun ModifierGroupItem(
    title: String,
    description: String?,
    items: List<String>
) {
    Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
        // Title
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary
        )

        // Description
        if (!description.isNullOrBlank()) {
            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }

        // List Items
        Column(modifier = Modifier.padding(top = 4.dp)) {
            items.forEach { text ->
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = "• ", style = MaterialTheme.typography.bodyMedium)
                    Text(text = text, style = MaterialTheme.typography.bodyMedium)
                }
            }
        }
    }
}