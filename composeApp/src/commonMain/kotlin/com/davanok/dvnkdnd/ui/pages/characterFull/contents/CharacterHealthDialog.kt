package com.davanok.dvnkdnd.ui.pages.characterFull.contents

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.data.model.entities.character.CharacterHealth
import com.davanok.dvnkdnd.data.model.types.ModifierExtendedInfo
import com.davanok.dvnkdnd.ui.components.text.buildPreviewString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.character_health_damage_button
import dvnkdnd.composeapp.generated.resources.character_health_heal_button
import dvnkdnd.composeapp.generated.resources.character_health_temp_health_button
import org.jetbrains.compose.resources.stringResource

@Composable
fun CharacterHealthWidget(
    health: CharacterHealth,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentHealth = health.current + health.temp

    val healthPercent =
        if (currentHealth == 0) 0f else health.max / currentHealth.toFloat().coerceIn(0f, 1f)
    TextButton(
        modifier = modifier,
        onClick = onClick
    ) {
        val color = when {
            healthPercent < 0.5 -> Color(
                red = 1f,
                green = healthPercent - 0.5f,
                blue = 0f
            )

            else -> Color(
                1f - (healthPercent - 0.5f) / 0.5f,
                green = 1f,
                blue = 0f
            )

        }
        Text(
            text = buildString {
                append(currentHealth)
                append('/')
                append(health.max)
            },
            style = MaterialTheme.typography.bodyLarge,
            color = color
        )
    }
}

@Composable
fun CharacterHealthDialogContent(
    baseHealth: CharacterHealth,
    updateHealth: (CharacterHealth) -> Unit,
    healthModifiers: List<ModifierExtendedInfo>,
) {
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
                healthModifierValue?.let {
                    updateHealth(baseHealth.copy(temp = it))
                }
            },
            enabled = healthModifierValue != null
        ) {
            Text(
                text = stringResource(Res.string.character_health_temp_health_button)
            )
        }
    }
    val modifiersText =
        buildString {
            healthModifiers.fastForEach { modifier ->
                append(modifier.groupName)

                append(modifier.buildPreviewString())

                append(" (")
                append(modifier.resolvedValue)
                append(')')
                appendLine()
            }
        }
    Text(
        text = modifiersText
    )
}