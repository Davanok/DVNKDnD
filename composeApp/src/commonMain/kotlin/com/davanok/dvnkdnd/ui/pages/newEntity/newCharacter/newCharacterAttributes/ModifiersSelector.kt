package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterAttributes

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
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
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
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
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
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFilteredMap
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMapIndexed
import androidx.window.core.layout.WindowWidthSizeClass
import com.davanok.dvnkdnd.data.model.dndEnums.Attributes
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierOperation
import com.davanok.dvnkdnd.data.model.dndEnums.applyForString
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDAttributesGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.applyOperation
import com.davanok.dvnkdnd.data.model.util.DnDConstants
import com.davanok.dvnkdnd.data.model.util.calculateBuyingModifiersSum
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.decrease_modifier_value
import dvnkdnd.composeapp.generated.resources.increase_modifier_value
import dvnkdnd.composeapp.generated.resources.remove
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid


private data class AppliedModifier(
    val value: Double,
    val operation: DnDModifierOperation
)

private fun applyModifiers(base: Int, modifiers: List<AppliedModifier>) =
    modifiers.fastFold(base) { acc, modifier -> applyOperation(acc, modifier.value, modifier.operation) }

private fun List<DnDModifiersGroup>.appliedModifiers(attribute: Attributes, selectedModifiers: Set<Uuid>) =
    fastFlatMap { group ->
        group.modifiers.fastFilteredMap(
            predicate = { it.targetAs<Attributes>() == attribute && it.id in selectedModifiers },
            transform =  { AppliedModifier(it.value, group.operation) }
        )
    }


private fun approximateToDefault(input: DnDAttributesGroup): DnDAttributesGroup {
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

    return DnDAttributesGroup(
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
    selectedCreationOption: AttributesSelectorType,
    allModifiersGroups: List<DnDModifiersGroup>,
    selectedAttributeModifiers: Set<Uuid>,
    modifiers: DnDAttributesGroup,
    onModifiersChange: (DnDAttributesGroup) -> Unit,
    onSelectModifiers: (DnDModifier) -> Unit,
) {
    Crossfade(
        modifier = Modifier.fillMaxSize(),
        targetState = selectedCreationOption
    ) { selectorType ->
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (selectorType) {
                AttributesSelectorType.POINT_BUY -> PointBuyModifiersSelector(
                    character = modifiers,
                    onChange = onModifiersChange,
                    allModifiersGroups = allModifiersGroups,
                    selectedAttributeModifiers = selectedAttributeModifiers,
                    onModifierSelected = onSelectModifiers
                )

                AttributesSelectorType.STANDARD_ARRAY -> StandardArrayModifiersSelector(
                    character = modifiers,
                    onChange = onModifiersChange,
                    allModifiersGroups = allModifiersGroups,
                    selectedAttributeModifiers = selectedAttributeModifiers,
                    onModifierSelected = onSelectModifiers
                )

                AttributesSelectorType.MANUAL -> ManualModifiersSelector(
                    character = modifiers,
                    onChange = onModifiersChange,
                    allModifiersGroups = allModifiersGroups,
                    selectedAttributeModifiers = selectedAttributeModifiers,
                    onModifierSelected = onSelectModifiers
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.PointBuyModifiersSelector(
    character: DnDAttributesGroup,
    onChange: (DnDAttributesGroup) -> Unit,
    allModifiersGroups: List<DnDModifiersGroup>,
    selectedAttributeModifiers: Set<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
) {
    val modifiersSum =
        calculateBuyingModifiersSum(character.modifiers())
    Text(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        text = (DnDConstants.BUYING_BALANCE - modifiersSum).toString(),
        style = MaterialTheme.typography.bodyLarge
    )
    ModifiersSelectionGroup(
        modifier = Modifier.fillMaxSize(),
        allModifiersGroups = allModifiersGroups,
        selectedAttributeModifiers = selectedAttributeModifiers,
        onModifierSelected = onModifierSelected,
    ) { attribute ->
        ModifierSelectorRow(
            value = character[attribute],
            onValueChange = { onChange(character.set(attribute, it)) },
            label = stringResource(attribute.stringRes),
            minValueCheck = { it >= DnDConstants.MIN_VALUE_TO_BUY },
            maxValueCheck = {
                it <= DnDConstants.MAX_VALUE_TO_BUY && DnDConstants.BUYING_BALANCE >=
                        calculateBuyingModifiersSum(
                            character.set(attribute, it).modifiers()
                        )
            },
            additionalModifiers = allModifiersGroups
                .appliedModifiers(attribute, selectedAttributeModifiers)
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun StandardArrayModifiersSelector(
    character: DnDAttributesGroup,
    onChange: (DnDAttributesGroup) -> Unit,
    allModifiersGroups: List<DnDModifiersGroup>,
    selectedAttributeModifiers: Set<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
) {
    LaunchedEffect(Unit) {
        onChange(approximateToDefault(character))
    }
    val modifiers = remember(character) { character.modifiers() }
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
        allModifiersGroups = allModifiersGroups,
        selectedAttributeModifiers = selectedAttributeModifiers,
        onModifierSelected = onModifierSelected,
    ) { attribute ->
        Column {
            Text(
                text = stringResource(attribute.stringRes),
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
                                    selected = value == character[attribute],
                                    onClick = {
                                        if (value == character[attribute])
                                            return@InputChip
                                        if (value !in modifiers)
                                            onChange(character.set(attribute, value))
                                        else {
                                            val overlap = character
                                                .toMap()
                                                .filterValues { it == value }
                                                .keys.firstOrNull()
                                            var tmp = character
                                            if (overlap != null) {
                                                tmp = tmp.set(overlap, character[attribute])
                                            }
                                            onChange(tmp.set(attribute, value))
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
                val attributeValue = applyModifiers(
                    character[attribute], 
                    allModifiersGroups.appliedModifiers(attribute, selectedAttributeModifiers)
                )
                val modifier = calculateModifier(attributeValue)
                val text = buildString {
                    append(attributeValue)
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
    character: DnDAttributesGroup,
    onChange: (DnDAttributesGroup) -> Unit,
    allModifiersGroups: List<DnDModifiersGroup>,
    selectedAttributeModifiers: Set<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
) {
    ModifiersSelectionGroup(
        modifier = Modifier.fillMaxSize(),
        allModifiersGroups = allModifiersGroups,
        selectedAttributeModifiers = selectedAttributeModifiers,
        onModifierSelected = onModifierSelected,
    ) { attribute ->
        ModifierSelectorRow(
            value = character[attribute],
            onValueChange = { onChange(character.set(attribute, it)) },
            label = stringResource(attribute.stringRes),
            minValueCheck = { true },
            maxValueCheck = { true },
            additionalModifiers = allModifiersGroups.appliedModifiers(attribute, selectedAttributeModifiers)
        )
    }
}

@Composable
fun ModifiersSelectionGroup(
    allModifiersGroups: List<DnDModifiersGroup>,
    selectedAttributeModifiers: Set<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
    modifier: Modifier = Modifier,
    modifierField: @Composable (attribute: Attributes) -> Unit,
) {
    Row(modifier = modifier) {
        Column {
            Spacer(Modifier.height(48.dp))
            Attributes.entries.fastForEach { attribute ->
                Box(
                    modifier = Modifier.height(48.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    modifierField(attribute)
                }
            }
        }
        allModifiersGroups.fastForEach { group ->
            Column {
                Text(
                    modifier = Modifier.size(48.dp),
                    text = group.name,
                    textAlign = TextAlign.Center
                )
            }
            Attributes.entries.fastForEach { attribute ->
                Box(
                    Modifier.size(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    group.modifiers
                        .fastFilter { it.targetAs<Attributes>() == attribute }
                        .fastForEach { modifier ->
                            RadioButton(
                                selected = modifier.id in selectedAttributeModifiers,
                                onClick = { onModifierSelected(modifier) },
                                enabled = modifier.selectable && (group.modifiers.count { it.id in selectedAttributeModifiers } < group.selectionLimit || modifier.id in selectedAttributeModifiers)
                            )
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
    additionalModifiers: List<AppliedModifier>,
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
            appliedModifiers = additionalModifiers,
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
    appliedModifiers: List<AppliedModifier>
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    Column(
        modifier = modifier
    ) {
        Text(
            text = label,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
        val sum = remember(value, appliedModifiers) {
            applyModifiers(value, appliedModifiers)
        }
        val text =
            if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT)
                sum.toString()
            else
                buildString {
                    appliedModifiers.fastFold(value.toString()) { acc, modifier ->
                        modifier.operation.applyForString(acc, modifier.value)
                    }
                    append(" (")
                    append(calculateModifier(sum).toSignedString())
                    append(')')
                }
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1
        )
    }
}
