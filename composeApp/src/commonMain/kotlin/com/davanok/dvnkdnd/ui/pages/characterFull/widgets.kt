package com.davanok.dvnkdnd.ui.pages.characterFull

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.domain.entities.character.CharacterModifiedValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterState
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.ui.components.text.MeasurementConverter
import com.davanok.dvnkdnd.ui.components.toSignedString
import com.davanok.dvnkdnd.ui.providers.LocalMeasurementSystem
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.add_character_state
import dvnkdnd.composeapp.generated.resources.character_armor_class
import dvnkdnd.composeapp.generated.resources.character_health
import dvnkdnd.composeapp.generated.resources.character_initiative
import dvnkdnd.composeapp.generated.resources.character_speed
import dvnkdnd.composeapp.generated.resources.no_character_states
import dvnkdnd.composeapp.generated.resources.outline_bolt
import dvnkdnd.composeapp.generated.resources.outline_health_cross
import dvnkdnd.composeapp.generated.resources.outline_shield
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainEntitiesWidget(
    entities: List<CharacterMainEntityInfo>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val classes = remember(entities) {
        entities.filter { it.entity.entity.type == DnDEntityTypes.CLASS }
    }

    Column(
        modifier = Modifier
            .clickable(onClick = onClick)
            .then(modifier)
            .verticalScroll(rememberScrollState())
    ) {
        classes.forEach { mainEntity ->
            Text(
                text =
                    if (mainEntity.level < 2) mainEntity.entity.entity.name
                    else "${mainEntity.entity.entity.name} (${mainEntity.level})",
                maxLines = 1,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun CharacterMainValuesWidget(
    values: CharacterModifiedValues,
    states: List<CharacterState>,
    onInitiativeClick: () -> Unit,
    onArmorClassClick: () -> Unit,
    onHealthClick: () -> Unit,
    onSpeedClick: () -> Unit,
    onAddStateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier) {
        Column(
            modifier = Modifier.padding(8.dp).fillMaxWidth()
        ) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalArrangement = Arrangement.Center
            ) {
                InitiativeWidget(values.initiative, onClick = onInitiativeClick)
                ArmorClassWidget(values.armorClass, onClick = onArmorClassClick)
                HealthWidget(values.health, onClick = onHealthClick)
                SpeedWidget(values.speed, onClick = onSpeedClick)
            }

            CharacterStatesWidget(
                states = states,
                onAddStateClick = onAddStateClick,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun InitiativeWidget(
    initiative: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SuggestionChip(
        modifier = modifier,
        onClick = onClick,
        label = { Text(text = initiative.toSignedString()) },
        icon = {
            Icon(
                painter = painterResource(Res.drawable.outline_bolt),
                contentDescription = stringResource(Res.string.character_initiative)
            )
        }
    )
}
@Composable
private fun ArmorClassWidget(
    armorClass: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    SuggestionChip(
        modifier = modifier,
        onClick = onClick,
        label = { Text(text = armorClass.toString()) },
        icon = {
            Icon(
                painter = painterResource(Res.drawable.outline_shield),
                contentDescription = stringResource(Res.string.character_armor_class)
            )
        }
    )
}

@Composable
private fun HealthWidget(
    health: CharacterHealth,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentHealth = health.current + health.temp
    val healthPercent =
        if (currentHealth == 0) 0f else (health.max / currentHealth.toFloat()).coerceIn(0f, 1f)

    SuggestionChip(
        modifier = modifier,
        onClick = onClick,
        label = {
            Text(text = "$currentHealth/${health.max}")
        },
        icon = {
            Icon(
                painter = painterResource(Res.drawable.outline_health_cross),
                contentDescription = stringResource(Res.string.character_health)
            )
        },
        border = AssistChipDefaults.assistChipBorder(
            enabled = true,
            borderColor = when {
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
        )
    )
}

@Composable
private fun SpeedWidget(
    speed: Int,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val speed = MeasurementConverter.convertLength(speed, LocalMeasurementSystem.current.length)
    SuggestionChip(
        modifier = modifier,
        onClick = onClick,
        label = {
            Text(text = speed)
        },
        icon = {
            Icon(
                imageVector = Icons.Default.Speed,
                contentDescription = stringResource(Res.string.character_speed)
            )
        }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterStatesWidget(
    states: List<CharacterState>,
    onAddStateClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (states.isEmpty())
            CharacterStateChip(
            modifier = modifier,
            label = stringResource(Res.string.no_character_states),
            onClick = onAddStateClick
        )
    else
        CharacterStateChip(
            label = states.joinToString { it.state.entity.name },
            onClick = onAddStateClick,
            modifier = modifier
        )
}

@Composable
private fun CharacterStateChip(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    AssistChip(
        modifier = modifier,
        onClick = onClick,
        label = {
            Text(
                text = label,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
                },
        trailingIcon = {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.add_character_state)
            )
        }
    )
}