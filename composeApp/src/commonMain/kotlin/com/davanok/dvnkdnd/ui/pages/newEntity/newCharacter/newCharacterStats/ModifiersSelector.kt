package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastAny
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapIndexed
import androidx.compose.ui.util.fastMapNotNull
import androidx.compose.ui.util.fastSumBy
import com.davanok.dvnkdnd.data.model.dndEnums.Stats
import com.davanok.dvnkdnd.data.model.entities.character.DnDEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifierBonus
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.ui.WindowWidthSizeClass
import com.davanok.dvnkdnd.data.model.util.DnDConstants
import com.davanok.dvnkdnd.data.model.util.calculateBuyingModifiersSum
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.adaptive.LocalAdaptiveInfo
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.decrease_modifier_value
import dvnkdnd.composeapp.generated.resources.increase_modifier_value
import dvnkdnd.composeapp.generated.resources.remove
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid


private fun List<DnDEntityWithModifiers>.appliedModifiers(
    stat: Stats,
    selectedModifiers: Set<Uuid>,
) = fastFlatMap { it.modifiers }
    .fastMapNotNull {
        when {
            it.stat != stat -> null
            it.id !in selectedModifiers -> null
            else -> it.modifier
        }
    }


private fun approximateToDefault(input: DnDModifiersGroup): DnDModifiersGroup {
    val statsList = listOf(
        input.strength,
        input.dexterity,
        input.constitution,
        input.intelligence,
        input.wisdom,
        input.charisma
    ).withIndex()

    val assignment = statsList
        .sortedByDescending { it.value }
        .fastMapIndexed { idx, (key, _) ->
            key to DnDConstants.DEFAULT_ARRAY[idx]
        }
        .toMap()

    return DnDModifiersGroup(
        strength = assignment.getValue(0),
        dexterity = assignment.getValue(1),
        constitution = assignment.getValue(2),
        intelligence = assignment.getValue(3),
        wisdom = assignment.getValue(4),
        charisma = assignment.getValue(5)
    )
}


@Composable
fun ModifiersSelector(
    selectedCreationOption: StatsCreationOptions,
    allEntitiesWithModifiers: List<DnDEntityWithModifiers>,
    selectedModifiersBonuses: Set<Uuid>,
    modifiers: DnDModifiersGroup,
    onModifiersChange: (DnDModifiersGroup) -> Unit,
    onSelectModifiers: (DnDModifierBonus) -> Unit,
) {
    Crossfade(
        modifier = Modifier.fillMaxSize(),
        targetState = selectedCreationOption
    ) { selectorType ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (selectorType) {
                StatsCreationOptions.POINT_BUY -> PointBuyModifiersSelector(
                    character = modifiers,
                    onChange = onModifiersChange,
                    entitiesWithModifiers = allEntitiesWithModifiers,
                    selectedModifiersBonuses = selectedModifiersBonuses,
                    onModifierSelected = onSelectModifiers
                )

                StatsCreationOptions.STANDARD_ARRAY -> StandardArrayModifiersSelector(
                    character = modifiers,
                    onChange = onModifiersChange,
                    entitiesWithModifiers = allEntitiesWithModifiers,
                    selectedModifiersBonuses = selectedModifiersBonuses,
                    onModifierSelected = onSelectModifiers
                )

                StatsCreationOptions.MANUAL -> ManualModifiersSelector(
                    character = modifiers,
                    onChange = onModifiersChange,
                    entitiesWithModifiers = allEntitiesWithModifiers,
                    selectedModifiersBonuses = selectedModifiersBonuses,
                    onModifierSelected = onSelectModifiers
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.PointBuyModifiersSelector(
    character: DnDModifiersGroup,
    onChange: (DnDModifiersGroup) -> Unit,
    entitiesWithModifiers: List<DnDEntityWithModifiers>,
    selectedModifiersBonuses: Set<Uuid>,
    onModifierSelected: (DnDModifierBonus) -> Unit,
) {
    val modifiersSum =
        calculateBuyingModifiersSum(character.toModifiersList().fastMap { it.modifier })
    Text(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        text = (DnDConstants.BUYING_BALANCE - modifiersSum).toString(),
        style = MaterialTheme.typography.bodyLarge
    )
    ModifiersSelectionGroup(
        modifier = Modifier.fillMaxSize(),
        entitiesWithModifiers = entitiesWithModifiers,
        selectedModifiersBonuses = selectedModifiersBonuses,
        onModifierSelected = onModifierSelected,
    ) { stat ->
        ModifierSelectorRow(
            value = character[stat],
            onValueChange = { onChange(character.set(stat, it)) },
            label = stringResource(stat.stringRes),
            minValueCheck = { it >= DnDConstants.MIN_VALUE_TO_BUY },
            maxValueCheck = {
                it <= DnDConstants.MAX_VALUE_TO_BUY &&
                        DnDConstants.BUYING_BALANCE >= calculateBuyingModifiersSum(
                    character
                        .set(stat, it)
                        .toModifiersList()
                        .fastMap { mod -> mod.modifier }
                )
            },
            additionalModifiers = entitiesWithModifiers
                .appliedModifiers(stat, selectedModifiersBonuses)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StandardArrayModifiersSelector(
    character: DnDModifiersGroup,
    onChange: (DnDModifiersGroup) -> Unit,
    entitiesWithModifiers: List<DnDEntityWithModifiers>,
    selectedModifiersBonuses: Set<Uuid>,
    onModifierSelected: (DnDModifierBonus) -> Unit,
) {
    LaunchedEffect(Unit) {
        onChange(approximateToDefault(character))
    }
    val modifiers = remember(character) { character.toModifiersList().fastMap { it.modifier } }
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
    var maxTextWidth by remember { mutableStateOf(10) }
    val density = LocalDensity.current
    val commonScrollState = rememberScrollState()
    ModifiersSelectionGroup(
        modifier = Modifier.fillMaxSize(),
        entitiesWithModifiers = entitiesWithModifiers,
        selectedModifiersBonuses = selectedModifiersBonuses,
        onModifierSelected = onModifierSelected,
    ) { stat ->
        Column {
            Text(
                text = stringResource(stat.stringRes),
                maxLines = 1,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(IntrinsicSize.Min)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(commonScrollState)
                    ) {
                        DnDConstants.DEFAULT_ARRAY.forEach { value ->
                            key(value) {
                                InputChip(
                                    selected = value == character[stat],
                                    onClick = {
                                        if (value == character[stat])
                                            return@InputChip
                                        if (value !in modifiers)
                                            onChange(character.set(stat, value))
                                        else {
                                            val overlap = character
                                                .toModifiersList()
                                                .fastFirst { it.modifier == value }
                                                .stat
                                            onChange(
                                                character
                                                    .set(overlap, character[stat])
                                                    .set(stat, value)
                                            )
                                        }
                                    },
                                    label = { Text(text = value.toString()) }
                                )
                            }
                        }
                    }
                    if (commonScrollState.canScrollBackward)
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(24.dp)
                                .align(Alignment.CenterStart)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Black.copy(alpha = 0.4f),
                                            Color.Transparent
                                        )
                                    )
                                )
                        )
                    if (commonScrollState.canScrollForward)
                        Box(
                            modifier = Modifier
                                .fillMaxHeight()
                                .width(24.dp)
                                .align(Alignment.CenterEnd)
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            Color.Black.copy(alpha = 0.4f)
                                        )
                                    )
                                )
                        )
                }
                val windowSizeClass = LocalAdaptiveInfo.current.windowSizeClass
                val statValue = character[stat] + entitiesWithModifiers.appliedModifiers(
                    stat,
                    selectedModifiersBonuses
                ).sum()
                val modifier = calculateModifier(statValue)
                val text =
                    if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Small)
                        modifier.toSignedString()
                    else
                        buildString {
                            append(statValue)
                            append(" (")
                            append(modifier.toSignedString())
                            append(')')
                        }

                Text(
                    text = text,
                    modifier = Modifier
                        .onSizeChanged {
                            if (it.width > maxTextWidth)
                                maxTextWidth = it.width
                        }
                        .widthIn(min = density.run { maxTextWidth.toDp() })
                        .padding(horizontal = 4.dp),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
private fun ManualModifiersSelector(
    character: DnDModifiersGroup,
    onChange: (DnDModifiersGroup) -> Unit,
    entitiesWithModifiers: List<DnDEntityWithModifiers>,
    selectedModifiersBonuses: Set<Uuid>,
    onModifierSelected: (DnDModifierBonus) -> Unit,
) {
    ModifiersSelectionGroup(
        modifier = Modifier.fillMaxSize(),
        entitiesWithModifiers = entitiesWithModifiers,
        selectedModifiersBonuses = selectedModifiersBonuses,
        onModifierSelected = onModifierSelected,
    ) { stat ->
        ModifierSelectorRow(
            value = character[stat],
            onValueChange = { onChange(character.set(stat, it)) },
            label = stringResource(stat.stringRes),
            minValueCheck = { true },
            maxValueCheck = { true },
            additionalModifiers = entitiesWithModifiers
                .appliedModifiers(stat, selectedModifiersBonuses)
        )
    }
}

@Composable
fun ModifiersSelectionGroup(
    entitiesWithModifiers: List<DnDEntityWithModifiers>,
    selectedModifiersBonuses: Set<Uuid>,
    onModifierSelected: (DnDModifierBonus) -> Unit,
    modifier: Modifier = Modifier,
    modifierField: @Composable (stat: Stats) -> Unit,
) {
    val (
        modBonusGroups,
        selectionLimitsSum
    ) = remember(entitiesWithModifiers) {
        val modBonusGroups = entitiesWithModifiers
            .fastFlatMap { it.modifiers }
            .groupBy { it.modifier }
            .toList()
            .sortedBy { it.first }
            .toMap()
        val selectionLimitsSum = entitiesWithModifiers.fastSumBy { it.selectionLimit }

        Pair(modBonusGroups, selectionLimitsSum)
    }

    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            Spacer(Modifier.weight(1f))
            modBonusGroups.keys.forEach { bonus ->
                Text(
                    text = bonus.toSignedString(),
                    modifier = Modifier.width(48.dp),
                    textAlign = TextAlign.Center
                )
            }
        }
        Spacer(Modifier.height(8.dp))


        Stats.entries.forEach { stat ->
            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box(
                    Modifier
                        .weight(1f),
                    contentAlignment = Alignment.CenterStart
                ) {
                    modifierField(stat)
                }

                modBonusGroups.values.forEach { modifiers ->
                    Box(
                        Modifier
                            .width(48.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        modifiers
                            .firstOrNull { it.stat == stat }
                            ?.let { mod ->
                                RadioButton(
                                    selected = mod.id in selectedModifiersBonuses,
                                    onClick = { onModifierSelected(mod) },
                                    enabled = mod.selectable &&
                                            (selectedModifiersBonuses.size < selectionLimitsSum ||
                                                    mod.id in selectedModifiersBonuses)
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
    additionalModifiers: List<Int>,
    modifier: Modifier = Modifier,
    label: String,
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        IconButton(
            onClick = { onValueChange(value - 1) },
            enabled = minValueCheck(value - 1)
        ) {
            Icon(
                painter = painterResource(Res.drawable.remove),
                contentDescription = stringResource(Res.string.decrease_modifier_value)
            )
        }
        ModifiersText(
            label = label,
            value = value,
            additionalModifiers = additionalModifiers,
            modifier = Modifier.weight(1f)
        )
        IconButton(
            onClick = { onValueChange(value + 1) },
            enabled = maxValueCheck(value + 1)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.increase_modifier_value)
            )
        }
    }
}

@Composable
private fun ModifiersText(
    label: String,
    value: Int,
    modifier: Modifier = Modifier,
    additionalModifiers: List<Int>
) {
    val windowSizeClass = LocalAdaptiveInfo.current.windowSizeClass
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
        val text =
            if (windowSizeClass.widthSizeClass == WindowWidthSizeClass.Small)
                (value + additionalModifiers.sum()).toString()
            else
                buildString {
                    append(value)
                    additionalModifiers.fastForEach {
                        append(it.toSignedString())
                    }
                    append(" (")
                    append(calculateModifier(value + additionalModifiers.sum()).toSignedString())
                    append(')')
                }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1
        )
    }
}
