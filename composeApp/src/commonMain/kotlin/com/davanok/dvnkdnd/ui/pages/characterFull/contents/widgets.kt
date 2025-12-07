package com.davanok.dvnkdnd.ui.pages.characterFull.contents

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.entities.character.CharacterHealth
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.domain.entities.character.CharacterModifiedValues
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.character_armor_class
import dvnkdnd.composeapp.generated.resources.character_initiative
import dvnkdnd.composeapp.generated.resources.outline_bolt
import dvnkdnd.composeapp.generated.resources.outline_shield
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun MainEntitiesWidget(
    entities: List<CharacterMainEntityInfo>,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val (classes, races, backgrounds) = remember(entities) {
        val groups = entities.groupBy { it.entity.entity.type }

        Triple(
            groups.getOrElse(DnDEntityTypes.CLASS, ::emptyList),
            groups.getOrElse(DnDEntityTypes.RACE, ::emptyList),
            groups.getOrElse(DnDEntityTypes.BACKGROUND, ::emptyList)
        )
    }

    fun List<CharacterMainEntityInfo>.buildString() = buildString stringBuilder@ {
        this@buildString.forEach { entity ->
            append(entity.entity.entity.name)
            append(" (")
            append(entity.level)
            append(")\n")
        }
    }.trimEnd()

    OutlinedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column {
            Text(text = classes.buildString(), modifier = Modifier.padding(8.dp))
            HorizontalDivider(Modifier.fillMaxWidth())
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = races.buildString(),
                    modifier = Modifier.weight(1f).padding(8.dp)
                )

                VerticalDivider()

                Text(
                    text = backgrounds.buildString(),
                    modifier = Modifier.weight(1f).padding(8.dp)
                )
            }
        }
    }
}

@Composable
fun CharacterMainValuesWidget(
    values: CharacterModifiedValues,
    onInitiativeClick: () -> Unit,
    onArmorClassClick: () -> Unit,
    onHealthClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedCard(modifier) {
        FlowRow(
            modifier = Modifier.padding(horizontal = 8.dp).fillMaxSize(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalArrangement = Arrangement.Center
        ) {
            InitiativeWidget(values.initiative, onClick = onInitiativeClick)
            ArmorClassWidget(values.armorClass, onClick = onArmorClassClick)
            CharacterHealthWidget(values.health, onClick = onHealthClick)
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
private fun CharacterHealthWidget(
    health: CharacterHealth,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val currentHealth = health.current + health.temp
    val healthPercent =
        if (currentHealth == 0) 0f else health.max / currentHealth.toFloat().coerceIn(0f, 1f)

    AssistChip(
        modifier = modifier,
        onClick = onClick,
        label = {
            Text(
                text = buildString {
                    append(currentHealth)
                    append('/')
                    append(health.max)
                }
            )
        },
        border = AssistChipDefaults.assistChipBorder(enabled = true, borderColor = when {
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
        })
    )
}