package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastJoinToString
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapNotNull
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.data.model.dnd_enums.stringRes
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.util.DnDConstants
import com.davanok.dvnkdnd.data.model.util.calculateBuyingModifiersSum
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.SelectableTextField
import com.davanok.dvnkdnd.ui.components.SuffixVisualTransformation
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.davanok.dvnkdnd.ui.components.append
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.about_modifiers
import dvnkdnd.composeapp.generated.resources.decrease_modifier_value
import dvnkdnd.composeapp.generated.resources.increase_modifier_value
import dvnkdnd.composeapp.generated.resources.no_modifiers_for_info
import dvnkdnd.composeapp.generated.resources.not_selected
import dvnkdnd.composeapp.generated.resources.remove
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Composable
fun ModifiersSelector(
    selectedCreationOption: StatsCreationOptions,
    character: CharacterWithModifiers,
    modifiers: DnDModifiersGroup,
    onModifiersChange: (DnDModifiersGroup) -> Unit,
    onSelectModifiers: (DnDModifier) -> Unit,
) {
    val commonModifiers = remember(character) {
        character.classes + listOfNotNull(
            character.race,
            character.subRace,
            character.background,
            character.subBackground
        ).fastFilter { it.modifiers.isNotEmpty() }
    }
    var showInfoSheet by remember { mutableStateOf<Stats?>(null) }
    Crossfade(
        modifier = Modifier.fillMaxSize(),
        targetState = selectedCreationOption
    ) {
        Column {
            when (it) {
                StatsCreationOptions.POINT_BUY -> PointBuyModifiersSelector(
                    character = modifiers,
                    onChange = onModifiersChange,
                    onInfoClick = { showInfoSheet = it },
                    allModifiers = commonModifiers,
                    selectedModifiers = character.selectedModifiers,
                    onModifierSelected = onSelectModifiers
                )

                StatsCreationOptions.STANDARD_ARRAY -> StandardArrayModifiersSelector(
                    character = modifiers,
                    onChange = onModifiersChange,
                    onInfoClick = { showInfoSheet = it },
                    allModifiers = commonModifiers,
                    selectedModifiers = character.selectedModifiers,
                    onModifierSelected = onSelectModifiers
                )

                StatsCreationOptions.MANUAL -> ManualModifiersSelector(
                    character = modifiers,
                    onChange = onModifiersChange,
                    onInfoClick = { showInfoSheet = it },
                    allModifiers = commonModifiers,
                    selectedModifiers = character.selectedModifiers,
                    onModifierSelected = onSelectModifiers
                )
            }
        }
    }
    showInfoSheet?.let { stat ->
        AdaptiveModalSheet(
            onDismissRequest = { showInfoSheet = null }
        ) {
            Text(buildAnnotatedString {
                commonModifiers.fastForEach {
                    if (it.modifiers.isEmpty()) return@fastForEach
                    withStyle(MaterialTheme.typography.labelLarge.toSpanStyle()) {
                        append(it.entity.type.stringRes())
                    }
                    append("\n\t")
                    it.modifiers.fastFilter { it.stat == stat }.forEach {
                        if (it.id in character.selectedModifiers)
                            withStyle(
                                LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough)
                                    .toSpanStyle()
                            ) {
                                append(it.modifier.toSignedString())
                            }
                        else
                            append(it.modifier.toSignedString())
                        append(' ')
                    }
                    append('\n')
                }
            }.let {
                if (it.isBlank()) AnnotatedString(stringResource(Res.string.no_modifiers_for_info))
                else it
            })
        }
    }
}

@Composable
private fun ColumnScope.PointBuyModifiersSelector(
    character: DnDModifiersGroup,
    onChange: (DnDModifiersGroup) -> Unit,
    onInfoClick: (stat: Stats) -> Unit,
    allModifiers: List<DnDEntityWithModifiers>,
    selectedModifiers: List<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
) {
    LaunchedEffect(Unit) {
        onChange(DnDModifiersGroup.Default)
    }
    val modifiersSum =
        calculateBuyingModifiersSum(character.toModifiersList().fastMap { it.modifier })
    Text(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        text = (DnDConstants.BUYING_BALANCE - modifiersSum).toString(),
        style = MaterialTheme.typography.bodyLarge
    )
    ModifiersSelectionGroup(
        modifier = Modifier.fillMaxSize(),
        allModifiers = allModifiers,
        selectedModifiers = selectedModifiers,
        onModifierSelected = onModifierSelected
    ) { stat ->
        ModifierSelectorRow(
            value = character[stat],
            onValueChange = { onChange(character.set(stat, it)) },
            onInfoClick = { onInfoClick(stat) },
            label = { Text(stringResource(stat.stringRes())) },
            minValueCheck = { it >= DnDConstants.MIN_VALUE_TO_BUY },
            maxValueCheck = {
                it <= DnDConstants.MAX_VALUE_TO_BUY &&
                        DnDConstants.BUYING_BALANCE >= calculateBuyingModifiersSum(
                    character
                        .set(stat, it)
                        .toModifiersList()
                        .fastMap { it.modifier }
                )
            },
            additionalModifiers = allModifiers
                .fastFlatMap { it.modifiers }
                .fastMapNotNull {
                    when {
                        it.stat != stat -> null
                        it.id !in selectedModifiers -> null
                        else -> it.modifier
                    }
                }
        )
    }
}

@Composable
private fun ColumnScope.StandardArrayModifiersSelector(
    character: DnDModifiersGroup,
    onChange: (DnDModifiersGroup) -> Unit,
    onInfoClick: (stat: Stats) -> Unit,
    allModifiers: List<DnDEntityWithModifiers>,
    selectedModifiers: List<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
) {
    LaunchedEffect(Unit) {
        onChange(DnDModifiersGroup(-1, -1, -1, -1, -1, -1))
    }
    val modifiers = character.toModifiersList().fastMap { it.modifier }
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally)
    ) {
        DnDConstants.DEFAULT_ARRAY.forEach { value ->
            key(value) {
                SuggestionChip(
                    onClick = { /* nothing */ },
                    enabled = value !in modifiers,
                    label = {
                        Text(text = value.toString())
                    }
                )
            }
        }
    }
    ModifiersSelectionGroup(
        modifier = Modifier.fillMaxSize(),
        allModifiers = allModifiers,
        selectedModifiers = selectedModifiers,
        onModifierSelected = onModifierSelected
    ) { stat ->
        SelectableTextField(
            value = character[stat].let {
                if (it > 0) it.toString()
                else stringResource(Res.string.not_selected)
            },
            onValueChange = { },
            label = { Text(stringResource(stat.stringRes())) },
        ) {
            item(
                text = { Text(stringResource(Res.string.not_selected)) },
                onClick = { onChange(character.set(stat, -1)) }
            )
            DnDConstants.DEFAULT_ARRAY.filterNot { it in modifiers }.forEach {
                item(
                    text = { Text(text = it.toString()) },
                    onClick = { onChange(character.set(stat, it.toInt())) }
                )
            }
        }
        IconButton(onClick = { onInfoClick(stat) }) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(Res.string.about_modifiers)
            )
        }
    }
}

@Composable
private fun ColumnScope.ManualModifiersSelector(
    character: DnDModifiersGroup,
    onChange: (DnDModifiersGroup) -> Unit,
    onInfoClick: (stat: Stats) -> Unit,
    allModifiers: List<DnDEntityWithModifiers>,
    selectedModifiers: List<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
) {
    ModifiersSelectionGroup(
        modifier = Modifier.fillMaxSize(),
        allModifiers = allModifiers,
        selectedModifiers = selectedModifiers,
        onModifierSelected = onModifierSelected
    ) { stat ->
        ModifierSelectorRow(
            value = character[stat],
            onValueChange = { onChange(character.set(stat, it)) },
            onInfoClick = { onInfoClick(stat) },
            label = { Text(stringResource(stat.stringRes())) },
            minValueCheck = { true },
            maxValueCheck = { true },
            additionalModifiers = allModifiers
                .fastFlatMap { it.modifiers }
                .fastMapNotNull {
                    when {
                        it.stat != stat -> null
                        it.id !in selectedModifiers -> null
                        else -> it.modifier
                    }
                }
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun ModifiersSelectionGroup(
    allModifiers: List<DnDEntityWithModifiers>,
    selectedModifiers: List<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
    modifier: Modifier = Modifier,
    modifierField: @Composable (stat: Stats) -> Unit
) {
    val modifierColumnWeight = .1f
    val firstColumnWeight = 1 - modifierColumnWeight * allModifiers.size
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState()).then(modifier)
    ) {
        Row(Modifier.fillMaxWidth()) {
            Spacer(Modifier.weight(firstColumnWeight))
            allModifiers.forEach { modGroup ->
                Box(
                    modifier = Modifier
                        .weight(modifierColumnWeight)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${stringResource(modGroup.entity.type.stringRes())}\n${modGroup.entity.name}",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodySmall,
                        softWrap = false
                    )
                }
            }
        }
        Stats.entries.fastForEach { stat ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(Modifier.weight(firstColumnWeight)) {
                    modifierField(stat)
                }

                allModifiers.forEach { modGroup ->
                    Box(
                        modifier = Modifier
                            .weight(modifierColumnWeight),
                        contentAlignment = Alignment.Center
                    ) {
                        modGroup.modifiers.fastFirstOrNull { it.stat == stat }?.let { modifier ->
                            RadioButton(
                                selected = !modifier.selectable || modifier.id in selectedModifiers,
                                onClick = { onModifierSelected(modifier) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ModifierSelectorRow(
    value: Int,
    onValueChange: (Int) -> Unit,
    minValueCheck: (Int) -> Boolean,
    maxValueCheck: (Int) -> Boolean,
    onInfoClick: () -> Unit,
    additionalModifiers: List<Int> = emptyList(),
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null,
) {
    Row(modifier = modifier) {
        IconButton(
            onClick = { onValueChange(value - 1) },
            enabled = minValueCheck(value - 1)
        ) {
            Icon(
                painter = painterResource(Res.drawable.remove),
                contentDescription = stringResource(Res.string.decrease_modifier_value)
            )
        }
        IconButton(
            onClick = { onValueChange(value + 1) },
            enabled = maxValueCheck(value + 1)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.increase_modifier_value)
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            label = label,
            singleLine = true,
            value = value.toString(),
            onValueChange = { onValueChange(it.toIntOrNull() ?: value) },
            suffix = {
                Text(
                    text = (calculateModifier(value + additionalModifiers.sum())).toSignedString()
                )
            },
            visualTransformation = SuffixVisualTransformation(
                additionalModifiers.fastJoinToString("") { it.toSignedString() }
            )
        )
        IconButton(onClick = onInfoClick) {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = stringResource(Res.string.about_modifiers)
            )
        }
    }
}
