package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterAttributes

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastFold
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
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
import com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterThrowsScreen.UiSelectableState
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.decrease_modifier_value
import dvnkdnd.composeapp.generated.resources.increase_modifier_value
import dvnkdnd.composeapp.generated.resources.remove
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min
import kotlin.uuid.Uuid

private val ATTRIBUTE_FIELD_MAX_WIDTH = 488.dp

private data class AppliedModifier(
    val value: Double,
    val operation: DnDModifierOperation
)

private fun applyModifiers(base: Int, modifiers: List<AppliedModifier>) =
    modifiers.fastFold(base) { acc, modifier -> applyOperation(acc, modifier.value, modifier.operation) }

private fun List<DnDModifiersGroup>.appliedModifiers(attribute: Attributes, selectedModifiers: Set<Uuid>) =
    fastFlatMap { group ->
        group.modifiers.fastFilter { it.targetAs<Attributes>() == attribute && it.id in selectedModifiers }
            .map { AppliedModifier(it.value, group.operation) }
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
            modifier = Modifier.fillMaxSize(),
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
    val modifiersSum = calculateBuyingModifiersSum(character.modifiers())
    Text(
        modifier = Modifier.padding(top = 8.dp),
        text = (DnDConstants.BUYING_BALANCE - modifiersSum).toString(),
        style = MaterialTheme.typography.bodyLarge
    )

    ModifiersSelectionTable(
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
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally)
    ) {
        DnDConstants.DEFAULT_ARRAY.forEach { value ->
            key(value) {
                SuggestionChip(
                    onClick = { /* noop */ },
                    enabled = value !in modifiers,
                    label = { Text(text = value.toString()) }
                )
            }
        }
    }

    ModifiersSelectionTable(
        allModifiersGroups = allModifiersGroups,
        selectedAttributeModifiers = selectedAttributeModifiers,
        onModifierSelected = onModifierSelected
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
private fun ManualModifiersSelector(
    character: DnDAttributesGroup,
    onChange: (DnDAttributesGroup) -> Unit,
    allModifiersGroups: List<DnDModifiersGroup>,
    selectedAttributeModifiers: Set<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
) {
    ModifiersSelectionTable(
        allModifiersGroups = allModifiersGroups,
        selectedAttributeModifiers = selectedAttributeModifiers,
        onModifierSelected = onModifierSelected
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
fun ModifiersSelectionTable(
    allModifiersGroups: List<DnDModifiersGroup>,
    selectedAttributeModifiers: Set<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
    modifier: Modifier = Modifier,
    cellContent: @Composable (attribute: Attributes) -> Unit
) {
    val modifierGroupsByAttributes = remember(allModifiersGroups, selectedAttributeModifiers) {
        allModifiersGroups.fastMap { group ->
            val attributesMap = mutableMapOf<Attributes, MutableList<Pair<DnDModifier, UiSelectableState>>>()

            val limitNotExceeded = group.modifiers.count { it.id in selectedAttributeModifiers } < group.selectionLimit

            group.modifiers.fastForEach { mod ->
                val selected = mod.id in selectedAttributeModifiers
                val selectable = mod.selectable && (limitNotExceeded || selected)

                attributesMap
                    .getOrPut(mod.targetAs(), ::mutableListOf)
                    .add(mod to UiSelectableState(selectable, selected))
            }

            attributesMap.mapValues { it.value.toList() }
        }
    }
    BoxWithConstraints {
        val groupsCount = allModifiersGroups.size
        val isWideLayout = remember(maxWidth, groupsCount) {
            maxWidth > ATTRIBUTE_FIELD_MAX_WIDTH + 48.dp * groupsCount
        }

        val columnWidth = remember(maxWidth, groupsCount) {
            if (groupsCount == 0) 48.dp
            else {
                val available = if (isWideLayout) (maxWidth - ATTRIBUTE_FIELD_MAX_WIDTH) else (48.dp * min(groupsCount, 3))
                val per = if (available > 0.dp) available / groupsCount else 48.dp
                maxOf(48.dp, per)
            }
        }

        val commonLazyRowState = rememberLazyListState()

        Column(modifier = modifier) {
            // Header row (labels for groups)
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                val attributeFieldModifier = if (isWideLayout) Modifier.width(ATTRIBUTE_FIELD_MAX_WIDTH) else Modifier.weight(1f)
                val groupsModifier = if (isWideLayout) Modifier.fillMaxWidth() else Modifier.width(columnWidth * min(groupsCount, 3))

                Box(modifier = attributeFieldModifier)

                LazyRow(
                    modifier = groupsModifier,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    state = commonLazyRowState
                ) {
                    items(allModifiersGroups, key = { it.id }) { group ->
                        Box(
                            modifier = Modifier
                                .width(columnWidth)
                                .padding(horizontal = 4.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = group.name,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = TextAlign.Center,
                                maxLines = 2
                            )
                        }
                    }
                }
            }

            HorizontalDivider()

            Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
                Attributes.entries.fastForEach { attribute ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val attributeFieldModifier = if (isWideLayout) Modifier.width(ATTRIBUTE_FIELD_MAX_WIDTH) else Modifier.weight(1f)
                        val groupsModifier = if (isWideLayout) Modifier.fillMaxWidth() else Modifier.width(columnWidth * min(groupsCount, 3))

                        Box(
                            modifier = attributeFieldModifier,
                            contentAlignment = Alignment.CenterStart
                        ) { cellContent(attribute) }

                        LazyRow(
                            modifier = groupsModifier,
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            state = commonLazyRowState
                        ) {
                            items(modifierGroupsByAttributes) { attrModifiers ->
                                Box(
                                    modifier = Modifier
                                        .width(columnWidth),
                                    contentAlignment = Alignment.Center
                                ) {
                                    val modsForAttr = remember(attrModifiers) { attrModifiers[attribute].orEmpty() }
                                    if (modsForAttr.isEmpty())
                                        Box(modifier = Modifier.fillMaxSize())
                                    else
                                        modsForAttr.fastForEach { (mod, state) ->
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                            ) {
                                                RadioButton(
                                                    selected = state.selected,
                                                    onClick = { if (state.selectable) onModifierSelected(mod) },
                                                    enabled = state.selectable
                                                )
                                            }
                                        }
                                }
                            }
                        } // end groups LazyRow
                    } // end attribute Row
                    HorizontalDivider()
                }
            } // end LazyColumn
        }
    }
}


/* ---------- helpers ---------- */

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
    Row(modifier = modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
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
    val sum = remember(value, appliedModifiers) {
        applyModifiers(value, appliedModifiers)
    }

    Column(modifier = modifier) {
        Text(
            text = label,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )
        val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
        val text = if (windowSizeClass.windowWidthSizeClass == WindowWidthSizeClass.COMPACT) {
            sum.toString()
        } else {
            buildString {
                appliedModifiers.fastFold(value.toString()) { acc, modifier ->
                    modifier.operation.applyForString(acc, modifier.value)
                }
                append(sum)
                append(" (")
                append(calculateModifier(sum).toSignedString())
                append(')')
            }
        }

        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            maxLines = 1
        )
    }
}
