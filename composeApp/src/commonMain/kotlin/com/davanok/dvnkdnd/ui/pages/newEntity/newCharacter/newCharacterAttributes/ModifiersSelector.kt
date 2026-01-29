package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterAttributes

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastMapIndexed
import com.davanok.dvnkdnd.core.utils.applyOperation
import com.davanok.dvnkdnd.core.utils.asEnum
import com.davanok.dvnkdnd.domain.dnd.DnDConstants
import com.davanok.dvnkdnd.domain.dnd.calculateBuyingModifiersSum
import com.davanok.dvnkdnd.domain.entities.character.DnDValueModifierWithResolvedValue
import com.davanok.dvnkdnd.domain.entities.character.ValueModifiersGroupWithResolvedValues
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.modifiers
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.ui.model.UiSelectableState
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.decrease_modifier_value
import dvnkdnd.composeapp.generated.resources.increase_modifier_value
import dvnkdnd.composeapp.generated.resources.remove
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.min
import kotlin.uuid.Uuid

private val ATTRIBUTE_FIELD_MAX_WIDTH = 488.dp

private fun approximateToDefault(
    primaryAttributes: List<Attributes>, // TODO
    input: AttributesGroup
): AttributesGroup {
    val statsList = input.modifiers().withIndex()

    val assignment = statsList
        .sortedByDescending { it.value }
        .fastMapIndexed { idx, (key, _) ->
            key to DnDConstants.DEFAULT_ARRAY[idx]
        }
        .toMap()

    return AttributesGroup(
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
    allModifiersGroups: List<ValueModifiersGroupWithResolvedValues>,
    selectedAttributeModifiers: Set<Uuid>,
    attributes: AttributesGroup,
    onModifiersChange: (AttributesGroup) -> Unit,
    onSelectModifiers: (DnDModifier) -> Unit,
) {
    val appliedModifiers = remember(allModifiersGroups, selectedAttributeModifiers) {
        allModifiersGroups
            .flatMap { it.modifiers }
            .filter { it.modifier.id in selectedAttributeModifiers }
            .sortedBy { it.modifier.priority }
    }

    val resultValues = remember(attributes, appliedModifiers) {
        val baseValues = attributes.toMap().toMutableMap()

        appliedModifiers
            .forEach { (modifier, resolvedValue) ->
                val key = modifier.targetKey?.asEnum<Attributes>() ?: return@forEach
                val baseValue = baseValues[key]!!
                val applied = applyOperation(baseValue, resolvedValue, modifier.operation)
                baseValues[key] = applied
            }

        baseValues.toAttributesGroup()
    }

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
                    attributes = attributes,
                    onChange = onModifiersChange,
                    allModifiersGroups = allModifiersGroups,
                    selectedAttributeModifiers = selectedAttributeModifiers,
                    onModifierSelected = onSelectModifiers,
                    appliedModifiers = appliedModifiers,
                    resultValues = resultValues
                )

                AttributesSelectorType.STANDARD_ARRAY -> DefaultArrayModifiersSelector(
                    attributes = attributes,
                    onChange = onModifiersChange,
                    allModifiersGroups = allModifiersGroups,
                    selectedAttributeModifiers = selectedAttributeModifiers,
                    onModifierSelected = onSelectModifiers,
                    appliedModifiers = appliedModifiers,
                    resultValues = resultValues
                )

                AttributesSelectorType.MANUAL -> ManualModifiersSelector(
                    attributes = attributes,
                    onChange = onModifiersChange,
                    allModifiersGroups = allModifiersGroups,
                    selectedAttributeModifiers = selectedAttributeModifiers,
                    onModifierSelected = onSelectModifiers,
                    appliedModifiers = appliedModifiers,
                    resultValues = resultValues
                )
            }
        }
    }
}

@Composable
private fun PointBuyModifiersSelector(
    attributes: AttributesGroup,
    onChange: (AttributesGroup) -> Unit,
    allModifiersGroups: List<ValueModifiersGroupWithResolvedValues>,
    selectedAttributeModifiers: Set<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
    appliedModifiers: List<DnDValueModifierWithResolvedValue>,
    resultValues: AttributesGroup
) {
    val modifiersSum = calculateBuyingModifiersSum(attributes.modifiers())
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
            value = attributes[attribute],
            onValueChange = { onChange(attributes.set(attribute, it)) },
            label = stringResource(attribute.stringRes),
            minValueCheck = { it >= DnDConstants.MIN_VALUE_TO_BUY },
            maxValueCheck = {
                it <= DnDConstants.MAX_VALUE_TO_BUY && DnDConstants.BUYING_BALANCE >=
                        calculateBuyingModifiersSum(attributes.set(attribute, it).modifiers())
            },
            appliedModifiers = appliedModifiers,
            resultValue = resultValues[attribute]
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun DefaultArrayModifiersSelector(
    attributes: AttributesGroup,
    onChange: (AttributesGroup) -> Unit,
    allModifiersGroups: List<ValueModifiersGroupWithResolvedValues>,
    selectedAttributeModifiers: Set<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
    appliedModifiers: List<DnDValueModifierWithResolvedValue>,
    resultValues: AttributesGroup
) {
    LaunchedEffect(Unit) {
        onChange(
            approximateToDefault(
                emptyList(), // TODO
                attributes
            )
        )
    }

    val attributesList = remember(attributes) { attributes.modifiers() }
    val copies = attributesList.fastFilter { a -> attributesList.count { it == a } > 1 }
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 6.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp, alignment = Alignment.CenterHorizontally)
    ) {
        DnDConstants.DEFAULT_ARRAY.forEach { value ->
            SuggestionChip(
                onClick = { /* noop */ },
                enabled = value !in attributesList || value in copies,
                label = { Text(text = value.toString()) },
                colors = if (value in copies) SuggestionChipDefaults.suggestionChipColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer,
                    labelColor = MaterialTheme.colorScheme.error,
                    iconContentColor = MaterialTheme.colorScheme.error
                ) else SuggestionChipDefaults.suggestionChipColors()
            )
        }
    }

    ModifiersSelectionTable(
        allModifiersGroups = allModifiersGroups,
        selectedAttributeModifiers = selectedAttributeModifiers,
        onModifierSelected = onModifierSelected
    ) { attribute ->
        var selectorExpanded by remember { mutableStateOf(false) }

        ExposedDropdownMenuBox(
            expanded = selectorExpanded,
            onExpandedChange = { selectorExpanded = it },
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { selectorExpanded = !selectorExpanded },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                ModifiersText(
                    modifier = Modifier.padding(start = 16.dp),
                    label = stringResource(attribute.stringRes),
                    baseValue = attributes[attribute],
                    appliedModifiers = appliedModifiers,
                    resultValue = resultValues[attribute]
                )
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = selectorExpanded)
            }
            ExposedDropdownMenu(
                expanded = selectorExpanded,
                onDismissRequest = { selectorExpanded = false }
            ) {
                DnDConstants.DEFAULT_ARRAY.forEach { value ->
                    DropdownMenuItem(
                        text = { Text(value.toString()) },
                        onClick = {
                            onChange(attributes.set(attribute, value))
                            selectorExpanded = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ManualModifiersSelector(
    attributes: AttributesGroup,
    onChange: (AttributesGroup) -> Unit,
    allModifiersGroups: List<ValueModifiersGroupWithResolvedValues>,
    selectedAttributeModifiers: Set<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
    appliedModifiers: List<DnDValueModifierWithResolvedValue>,
    resultValues: AttributesGroup
) {
    ModifiersSelectionTable(
        allModifiersGroups = allModifiersGroups,
        selectedAttributeModifiers = selectedAttributeModifiers,
        onModifierSelected = onModifierSelected
    ) { attribute ->
        ModifierSelectorRow(
            value = attributes[attribute],
            onValueChange = { onChange(attributes.set(attribute, it)) },
            label = stringResource(attribute.stringRes),
            minValueCheck = { true },
            maxValueCheck = { true },
            appliedModifiers = appliedModifiers,
            resultValue = resultValues[attribute]
        )
    }
}

@Composable
fun ModifiersSelectionTable(
    allModifiersGroups: List<ValueModifiersGroupWithResolvedValues>,
    selectedAttributeModifiers: Set<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit,
    modifier: Modifier = Modifier,
    cellContent: @Composable (attribute: Attributes) -> Unit
) {
    // 1. Optimized Data Structure: Map<Attribute, List<ModifierState>>
    // This avoids nested loops inside the LazyColumn rows.
    val tableData = remember(allModifiersGroups, selectedAttributeModifiers) {
        Attributes.entries.associateWith { attribute ->
            allModifiersGroups.map { group ->
                val groupModifiers = group.modifiers.filter {
                    it.modifier.targetKey?.asEnum<Attributes>() == attribute
                }

                val limitExceeded = group.modifiers
                    .count { it.modifier.id in selectedAttributeModifiers } >= group.selectionLimit

                // If limit is 0 or less, we assume unlimited
                val isUnlimited = group.selectionLimit <= 0

                groupModifiers.map { mod ->
                    val isSelected = mod.modifier.id in selectedAttributeModifiers
                    val isSelectable = isUnlimited || isSelected || !limitExceeded

                    mod.modifier to UiSelectableState(isSelectable, isSelected)
                }
            }
        }
    }

    BoxWithConstraints(modifier = modifier) {
        val groupsCount = allModifiersGroups.size
        val isWideLayout = maxWidth > ATTRIBUTE_FIELD_MAX_WIDTH + (48.dp * groupsCount)

        val columnWidth = remember(maxWidth, groupsCount) {
            if (groupsCount == 0) 48.dp
            else {
                val available = if (isWideLayout) (maxWidth - ATTRIBUTE_FIELD_MAX_WIDTH)
                else (48.dp * min(groupsCount, 3))
                (available / groupsCount).coerceAtLeast(48.dp)
            }
        }

        val scrollState = rememberLazyListState()

        LazyColumn(modifier = Modifier.fillMaxSize()) {
            // 2. Sticky Header for Group Names
            stickyHeader {
                Surface(color = MaterialTheme.colorScheme.surface) {
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val labelModifier = if (isWideLayout) Modifier.width(ATTRIBUTE_FIELD_MAX_WIDTH)
                        else Modifier.weight(1f)

                        Spacer(modifier = labelModifier)

                        SyncLazyRow(
                            state = scrollState,
                            count = groupsCount,
                            columnWidth = columnWidth,
                            isWideLayout = isWideLayout
                        ) { index ->
                            Text(
                                text = allModifiersGroups[index].name,
                                style = MaterialTheme.typography.labelSmall,
                                textAlign = TextAlign.Center,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                modifier = Modifier.width(columnWidth).padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
                HorizontalDivider()
            }

            // 3. Attribute Rows
            items(Attributes.entries) { attribute ->
                val rowData = tableData[attribute] ?: emptyList()

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val labelModifier = if (isWideLayout) Modifier.width(ATTRIBUTE_FIELD_MAX_WIDTH)
                    else Modifier.weight(1f)

                    Box(modifier = labelModifier.padding(8.dp)) {
                        cellContent(attribute)
                    }

                    SyncLazyRow(
                        state = scrollState,
                        count = groupsCount,
                        columnWidth = columnWidth,
                        isWideLayout = isWideLayout
                    ) { groupIndex ->
                        val modifiersInCell = rowData.getOrNull(groupIndex) ?: emptyList()

                        Box(
                            modifier = Modifier.width(columnWidth).fillMaxHeight(),
                            contentAlignment = Alignment.Center
                        ) {
                            modifiersInCell.forEach { (mod, state) ->
                                RadioButton(
                                    selected = state.selected,
                                    onClick = { if (state.selectable) onModifierSelected(mod) },
                                    enabled = state.selectable
                                )
                            }
                        }
                    }
                }
                HorizontalDivider()
            }
        }
    }
}

/**
 * Reusable Row that handles the conditional width and scroll sync
 */
@Composable
private fun SyncLazyRow(
    state: LazyListState,
    count: Int,
    columnWidth: Dp,
    isWideLayout: Boolean,
    content: @Composable (Int) -> Unit
) {
    val rowModifier = if (isWideLayout) Modifier.fillMaxWidth()
    else Modifier.width(columnWidth * min(count, 3))

    LazyRow(
        state = state,
        modifier = rowModifier,
        userScrollEnabled = !isWideLayout, // Disable scroll if everything fits
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        items(count) { index ->
            content(index)
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
    appliedModifiers: List<DnDValueModifierWithResolvedValue>,
    resultValue: Int,
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
            baseValue = value,
            appliedModifiers = appliedModifiers,
            modifier = Modifier.weight(1f),
            resultValue = resultValue
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
    baseValue: Int,
    modifier: Modifier = Modifier,
    appliedModifiers: List<DnDValueModifierWithResolvedValue>,
    resultValue: Int
) {
    Column(modifier = modifier) {
        Text(
            text = label,
            maxLines = 1,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.primary
        )

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(text = baseValue.toString())

            Text(text = resultValue.toString())
        }
    }
}