package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.util.fastJoinToString
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.util.DnDConstants
import com.davanok.dvnkdnd.data.model.util.calculateBuyingModifiersSum
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.SuffixVisualTransformation
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.about_modifiers
import dvnkdnd.composeapp.generated.resources.charisma
import dvnkdnd.composeapp.generated.resources.constitution
import dvnkdnd.composeapp.generated.resources.decrease_modifier_value
import dvnkdnd.composeapp.generated.resources.dexterity
import dvnkdnd.composeapp.generated.resources.increase_modifier_value
import dvnkdnd.composeapp.generated.resources.intelligence
import dvnkdnd.composeapp.generated.resources.remove
import dvnkdnd.composeapp.generated.resources.strength
import dvnkdnd.composeapp.generated.resources.wisdom
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource


@Composable
fun ModifiersSelector(
    selectedCreationOption: StatsCreationOptions,
    character: CharacterWithModifiers,
    modifiers: DnDModifiersGroup,
    onModifiersChange: (DnDModifiersGroup) -> Unit,
) {
    val commonModifiers = remember(character) {
        character.classesWithModifiers.flatMap { it.value } +
                character.raceModifiers +
                character.subRaceModifiers +
                character.backgroundModifiers +
                character.subBackgroundModifiers
    }
    var showInfoSheet by remember { mutableStateOf<Stats?>(null) }
    Crossfade(
        targetState = selectedCreationOption
    ) {
        Column {
            when (it) {
                StatsCreationOptions.POINT_BUY -> PointBuyModifiersSelector(
                    commonModifiers = commonModifiers,
                    modifiers = modifiers,
                    onModifiersChange = onModifiersChange,
                    onInfoClick = { showInfoSheet = it }
                )
                StatsCreationOptions.STANDARD_ARRAY -> TODO()
                StatsCreationOptions.MANUAL -> TODO()
            }
        }
    }

    showInfoSheet?.let {
        AdaptiveModalSheet(
            onDismissRequest = { showInfoSheet = null }
        ) {

        }
    }
}

@Composable
private fun ColumnScope.PointBuyModifiersSelector(
    commonModifiers: List<DnDModifier>,
    modifiers: DnDModifiersGroup,
    onModifiersChange: (DnDModifiersGroup) -> Unit,
    onInfoClick: (stat: Stats) -> Unit
) {
    val textWithRes: (StringResource) -> @Composable () -> Unit = {
        { Text(stringResource(it)) }
    }
    val modifiersSum = calculateBuyingModifiersSum(modifiers.toModifiersList().map { it.modifier })
    Text(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        text = (DnDConstants.BUYING_BALANCE - modifiersSum).toString(),
        style = MaterialTheme.typography.bodyLarge
    )
    ModifierSelectorRow(
        value = modifiers.strength,
        onValueChange = { onModifiersChange(modifiers.copy(strength = it)) },
        minValue = DnDConstants.MIN_VALUE_TO_BUY,
        maxValue = DnDConstants.MAX_VALUE_TO_BUY,
        additionalModifiers = commonModifiers.filter { it.stat == Stats.STRENGTH }.map { it.modifier },
        onInfoClick = { onInfoClick(Stats.STRENGTH) },
        label = textWithRes(Res.string.strength)
    )
    ModifierSelectorRow(
        value = modifiers.dexterity,
        onValueChange = { onModifiersChange(modifiers.copy(dexterity = it)) },
        minValue = DnDConstants.MIN_VALUE_TO_BUY,
        maxValue = DnDConstants.MAX_VALUE_TO_BUY,
        additionalModifiers = commonModifiers.filter { it.stat == Stats.DEXTERITY }.map { it.modifier },
        onInfoClick = { onInfoClick(Stats.DEXTERITY) },
        label = textWithRes(Res.string.dexterity)
    )
    ModifierSelectorRow(
        value = modifiers.constitution,
        onValueChange = { onModifiersChange(modifiers.copy(constitution = it)) },
        minValue = DnDConstants.MIN_VALUE_TO_BUY,
        maxValue = DnDConstants.MAX_VALUE_TO_BUY,
        additionalModifiers = commonModifiers.filter { it.stat == Stats.CONSTITUTION }.map { it.modifier },
        onInfoClick = { onInfoClick(Stats.CONSTITUTION) },
        label = textWithRes(Res.string.constitution)
    )
    ModifierSelectorRow(
        value = modifiers.intelligence,
        onValueChange = { onModifiersChange(modifiers.copy(intelligence = it)) },
        minValue = DnDConstants.MIN_VALUE_TO_BUY,
        maxValue = DnDConstants.MAX_VALUE_TO_BUY,
        additionalModifiers = commonModifiers.filter { it.stat == Stats.INTELLIGENCE }.map { it.modifier },
        onInfoClick = { onInfoClick(Stats.INTELLIGENCE) },
        label = textWithRes(Res.string.intelligence)
    )
    ModifierSelectorRow(
        value = modifiers.wisdom,
        onValueChange = { onModifiersChange(modifiers.copy(wisdom = it)) },
        minValue = DnDConstants.MIN_VALUE_TO_BUY,
        maxValue = DnDConstants.MAX_VALUE_TO_BUY,
        additionalModifiers = commonModifiers.filter { it.stat == Stats.WISDOM }.map { it.modifier },
        onInfoClick = { onInfoClick(Stats.WISDOM) },
        label = textWithRes(Res.string.wisdom)
    )
    ModifierSelectorRow(
        value = modifiers.charisma,
        onValueChange = { onModifiersChange(modifiers.copy(charisma = it)) },
        minValue = DnDConstants.MIN_VALUE_TO_BUY,
        maxValue = DnDConstants.MAX_VALUE_TO_BUY,
        additionalModifiers = commonModifiers.filter { it.stat == Stats.CHARISMA }.map { it.modifier },
        onInfoClick = { onInfoClick(Stats.CHARISMA) },
        label = textWithRes(Res.string.charisma)
    )
}

@Composable
private fun ModifierSelectorRow(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    minValue: Int,
    maxValue: Int,
    additionalModifiers: List<Int> = emptyList(),
    onInfoClick: () -> Unit,
    label: @Composable (() -> Unit)? = null
) {
    Row(modifier = modifier) {
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            singleLine = true,
            value = value.toString(),
            onValueChange = {
                onValueChange(it.toIntOrNull() ?: value)
            },
            suffix = {
                Text(
                    text = (calculateModifier(value + additionalModifiers.sum())).toSignedString()
                )
            },
            visualTransformation = SuffixVisualTransformation(
                additionalModifiers.fastJoinToString("") { it.toSignedString() }
            ),
            trailingIcon = {
                IconButton (
                    onClick = onInfoClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(Res.string.about_modifiers)
                    )
                }
            },
            label = label
        )
        IconButton(
            onClick = { onValueChange(value - 1) },
            enabled = value > minValue
        ) {
            Icon(
                painter = painterResource(Res.drawable.remove),
                contentDescription = stringResource(Res.string.decrease_modifier_value)
            )
        }
        IconButton(
            onClick = { onValueChange(value + 1) },
            enabled = value < maxValue
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.increase_modifier_value)
            )
        }
    }
}
