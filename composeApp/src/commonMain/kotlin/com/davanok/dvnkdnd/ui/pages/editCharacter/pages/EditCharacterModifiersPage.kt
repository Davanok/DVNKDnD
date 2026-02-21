package com.davanok.dvnkdnd.ui.pages.editCharacter.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.selection.toggleable
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ListItem
import androidx.compose.material3.ListItemDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SecondaryScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.core.UiEntry
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomDamageModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomRollModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomValueModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.dndEntities.EntityBase
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDDamageModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDRollModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierType
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierRollTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.ui.components.DeleteWithConfirmationButton
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.components.text.modifiersText.buildModifierPreviewWithTarget
import com.davanok.dvnkdnd.ui.pages.editCharacter.EditCharacterScreenEvent
import com.davanok.dvnkdnd.ui.pages.editCharacter.pages.editCustomModifierDialog.EditCustomModifierDialog
import com.mikepenz.markdown.m3.Markdown
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.add_character_custom_modifier
import dvnkdnd.composeapp.generated.resources.modifiers_type_all_title
import dvnkdnd.composeapp.generated.resources.modifiers_type_custom_title
import dvnkdnd.composeapp.generated.resources.modifiers_type_damage_title
import dvnkdnd.composeapp.generated.resources.modifiers_type_mixed_title
import dvnkdnd.composeapp.generated.resources.modifiers_type_roll_title
import dvnkdnd.composeapp.generated.resources.modifiers_type_value_title
import dvnkdnd.composeapp.generated.resources.no_modifiers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Immutable
private data class SelectableModifiersGroup(
    val id: Uuid,
    val name: String,
    val description: String,
    val selectionLimit: Int,
    val isSelectionMaxedOut: Boolean,
    val modifiers: List<SelectableModifier>
)

@Immutable
private data class SelectableModifier(
    val modifier: DnDModifier,
    val isSelected: Boolean,
    val isEnabled: Boolean
)

@Immutable
private data class ModifiersPageUiState(
    val customModifiers: Map<DnDModifierType, List<CustomModifiersGroup>> = emptyMap(),
    val modifierGroups: Map<EntityBase, List<SelectableModifiersGroup>> = emptyMap(),
    val isEmpty: Boolean = true
)

@Immutable
sealed interface CustomModifiersGroup {
    val target: Enum<*>
    val modifiers: Map<String?, List<CharacterCustomModifier>>
}

data class CustomValueModifiersGroup(
    override val target: ModifierValueTarget,
    override val modifiers: Map<String?, List<CharacterCustomValueModifier>>
) : CustomModifiersGroup

data class CustomRollModifiersGroup(
    override val target: ModifierRollTarget,
    override val modifiers: Map<String?, List<CharacterCustomRollModifier>>
) : CustomModifiersGroup

data class CustomDamageModifiersGroup(
    override val target: DamageTypes,
    override val modifiers: Map<String?, List<CharacterCustomDamageModifier>>
) : CustomModifiersGroup

private enum class ModifierPageType(val stringRes: StringResource) {
    ALL(Res.string.modifiers_type_all_title),
    VALUE(Res.string.modifiers_type_value_title),
    ROLL(Res.string.modifiers_type_roll_title),
    DAMAGE(Res.string.modifiers_type_damage_title),
    MIXED(Res.string.modifiers_type_mixed_title),
    CUSTOM(Res.string.modifiers_type_custom_title)
}

// -----------------------------------------------------------------------------
// Logic & Mappers
// -----------------------------------------------------------------------------

private fun prepareCustomModifiers(
    customModifiers: List<CharacterCustomModifier>
): Map<DnDModifierType, List<CustomModifiersGroup>> {

    val valueGroups = customModifiers.filterIsInstance<CharacterCustomValueModifier>()
        .groupBy { it.targetScope }
        .map { (target, mods) ->
            CustomValueModifiersGroup(
                target = target,
                modifiers = mods.groupBy { it.targetKey }
                    .mapValues { (_, m) -> m.sortedWith(compareBy<CharacterCustomValueModifier> { it.priority }.thenBy { it.operation }) }
            )
        }

    val rollGroups = customModifiers.filterIsInstance<CharacterCustomRollModifier>()
        .groupBy { it.targetScope }
        .map { (target, mods) ->
            CustomRollModifiersGroup(
                target = target,
                modifiers = mods.groupBy { it.targetKey }
                    .mapValues { (_, m) -> m.sortedBy { it.operation } }
            )
        }

    val damageGroups = customModifiers.filterIsInstance<CharacterCustomDamageModifier>()
        .groupBy { it.damageType }
        .map { (target, mods) ->
            CustomDamageModifiersGroup(
                target = target,
                modifiers = mapOf(null to mods.sortedBy { it.interaction })
            )
        }

    return mapOf(
        DnDModifierType.VALUE to valueGroups,
        DnDModifierType.ROLL to rollGroups,
        DnDModifierType.DAMAGE to damageGroups
    ).filterValues { it.isNotEmpty() }
}

private fun CharacterFull.toUiState(pageType: ModifierPageType): ModifiersPageUiState {
    if (pageType == ModifierPageType.CUSTOM) {
        val mappedCustoms = prepareCustomModifiers(customModifiers)
        return ModifiersPageUiState(
            customModifiers = mappedCustoms,
            isEmpty = mappedCustoms.isEmpty()
        )
    }

    val selectedSet = selectedModifiers.toSet()

    val filteredGroups = entities.associate { entityWithGroups ->
        val validGroups = entityWithGroups.modifiersGroups.mapNotNull { group ->
            val filtered = group.filterModifiersForPage(pageType)
            if (filtered.isNullOrEmpty()) null else group.toSelectable(filtered, selectedSet)
        }
        entityWithGroups.entity to validGroups
    }.filterValues { it.isNotEmpty() }

    return ModifiersPageUiState(
        modifierGroups = filteredGroups,
        isEmpty = filteredGroups.isEmpty()
    )
}

private fun ModifiersGroup.filterModifiersForPage(pageType: ModifierPageType): List<DnDModifier>? {
    return when (pageType) {
        ModifierPageType.ALL -> modifiers
        ModifierPageType.VALUE -> modifiers.filterIsInstance<DnDValueModifier>()
        ModifierPageType.ROLL -> modifiers.filterIsInstance<DnDRollModifier>()
        ModifierPageType.DAMAGE -> modifiers.filterIsInstance<DnDDamageModifier>()
        ModifierPageType.MIXED -> {
            val hasMultipleTypes = modifiers.map { it::class }.distinct().size > 1
            if (hasMultipleTypes) modifiers else null
        }
        ModifierPageType.CUSTOM -> null
    }
}

private fun ModifiersGroup.toSelectable(
    filteredModifiers: List<DnDModifier>,
    selectedIds: Set<Uuid>
): SelectableModifiersGroup {
    val currentSelectionCount = filteredModifiers.count { it.id in selectedIds }
    val isMaxedOut = selectionLimit in 1..currentSelectionCount

    return SelectableModifiersGroup(
        id = id,
        name = name,
        description = description,
        selectionLimit = selectionLimit,
        isSelectionMaxedOut = isMaxedOut,
        modifiers = filteredModifiers.map { mod ->
            val isSelected = mod.id in selectedIds
            SelectableModifier(
                modifier = mod,
                isSelected = isSelected,
                isEnabled = !isMaxedOut || isSelected
            )
        }
    )
}

/** Helper to resolve icons across different modifier types */
private fun getModifierIcon(modifier: DnDModifier): DrawableResource? = when (modifier) {
    is DnDValueModifier -> modifier.operation.iconRes
    is DnDRollModifier -> modifier.operation.iconRes
    is DnDDamageModifier -> modifier.interaction.iconRes
}
private fun getModifierIcon(modifier: CharacterCustomModifier): DrawableResource? = when (modifier) {
    is CharacterCustomValueModifier -> modifier.operation.iconRes
    is CharacterCustomRollModifier -> modifier.operation.iconRes
    is CharacterCustomDamageModifier -> modifier.interaction.iconRes
}

// -----------------------------------------------------------------------------
// Composables
// -----------------------------------------------------------------------------

@Composable
fun EditCharacterModifiersPage(
    character: CharacterFull,
    eventSink: (EditCharacterScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = remember { ModifierPageType.entries }
    val pagerState = rememberPagerState { pages.size }
    val scope = rememberCoroutineScope()

    // Simplified dialog state: null means hidden, non-null is the item to edit (null content = "Add")
    var activeCustomModifier by remember { mutableStateOf<CharacterCustomModifier?>(null) }
    var showAddDialog by remember { mutableStateOf(false) }

    Column(modifier = modifier.fillMaxSize()) {
        SecondaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = pagerState.currentPage == index,
                    onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(stringResource(page.stringRes)) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { pageIndex ->
            val pageType = pages[pageIndex]
            val uiState = remember(character, pageType) { character.toUiState(pageType) }

            if (pageType == ModifierPageType.CUSTOM) {
                CustomModifiersPage(
                    uiState = uiState,
                    onAddClick = { showAddDialog = true },
                    onDeleteClick = { eventSink(EditCharacterScreenEvent.DeleteCharacterCustomModifier(it)) },
                    onEditClick = { activeCustomModifier = it }
                )
            } else {
                StandardModifiersList(
                    uiState = uiState,
                    onModifierSelection = { mod, selected ->
                        eventSink(EditCharacterScreenEvent.SetModifierSelection(mod, selected))
                    }
                )
            }
        }
    }

    // Handle "Add" or "Edit" dialogs
    if (showAddDialog || activeCustomModifier != null) {
        EditCustomModifierDialog(
            customModifier = activeCustomModifier,
            onUpdate = {
                eventSink(EditCharacterScreenEvent.SetCharacterCustomModifier(it))
                showAddDialog = false
                activeCustomModifier = null
                       },
            onDelete = {
                eventSink(EditCharacterScreenEvent.DeleteCharacterCustomModifier(it))
                showAddDialog = false
                activeCustomModifier = null
                       },
            onDismiss = {
                showAddDialog = false
                activeCustomModifier = null
            }
        )
    }
}


@Composable
private fun CustomModifiersPage(
    uiState: ModifiersPageUiState,
    onAddClick: () -> Unit,
    onDeleteClick: (CharacterCustomModifier) -> Unit,
    onEditClick: (CharacterCustomModifier) -> Unit
) {
    Scaffold(
        floatingActionButton = { AddCustomModifierFab(onClick = onAddClick) }
    ) { padding ->
        ModifierListContainer(
            isEmpty = uiState.isEmpty,
            modifier = Modifier.padding(padding)
        ) {
            uiState.customModifiers.forEach { (type, groups) ->
                stickyHeader {
                    ModifierHeader(title = type.name)
                }

                items(groups, key = { it.target }) { group ->
                    val groupName = (group.target as? UiEntry)?.let { stringResource(it.stringRes) }
                        ?: group.target.name

                    ModifierCard(title = groupName) {
                        group.modifiers.toList().forEachIndexed { index, (targetKey, modifiers) ->
                            if (index > 0) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                            CustomModifierSubGroup(targetKey, modifiers, onDeleteClick, onEditClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StandardModifiersList(
    uiState: ModifiersPageUiState,
    onModifierSelection: (DnDModifier, Boolean) -> Unit
) {
    ModifierListContainer(isEmpty = uiState.isEmpty) {
        items(
            items = uiState.modifierGroups.toList(),
            key = { it.first.id }
        ) { (entity, groups) ->
            ModifierCard(title = entity.name) {
                groups.forEachIndexed { index, group ->
                    if (index > 0) HorizontalDivider(Modifier.padding(horizontal = 16.dp))
                    StandardGroupItem(group, onModifierSelection)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AddCustomModifierFab(onClick: () -> Unit) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = { PlainTooltip { Text(stringResource(Res.string.add_character_custom_modifier)) } },
        state = rememberTooltipState()
    ) {
        FloatingActionButton(onClick = onClick) {
            Icon(Icons.Default.Add, contentDescription = null)
        }
    }
}

@Composable
private fun CustomModifierItem(
    mod: CharacterCustomModifier,
    onDeleteClick: () -> Unit,
    onEditClick: () -> Unit
) {
    ModifierBaseItem(
        iconRes = getModifierIcon(mod),
        headline = mod.buildModifierPreviewWithTarget(),
        supportingText = "${mod.name}\n${mod.description}".trim(),
        modifier = Modifier.clickable(onClick = onEditClick),
        trailingContent = {
            DeleteWithConfirmationButton(
                onDeleteClick = onDeleteClick
            )
        }
    )
}

@Composable
private fun StandardGroupItem(
    group: SelectableModifiersGroup,
    onModifierClick: (DnDModifier, Boolean) -> Unit
) {
    val expandable = group.description.isNotBlank()
    var isExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.fillMaxWidth()) {
        GroupHeader(
            name = group.name,
            currentCount = group.modifiers.count { it.isSelected },
            limit = group.selectionLimit,
            totalAvailable = group.modifiers.size,
            isExpandable = expandable,
            isExpanded = isExpanded,
            modifier = Modifier
                .fillMaxWidth()
                .toggleable(
                    enabled = expandable,
                    role = Role.Switch,
                    value = isExpanded
                ) { isExpanded = it }
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        AnimatedVisibility(visible = isExpanded && expandable) {
            Markdown(content = group.description, modifier = Modifier.padding(16.dp, 8.dp))
        }

        group.modifiers.forEach { selectable ->
            ModifierBaseItem(
                iconRes = getModifierIcon(selectable.modifier),
                headline = selectable.modifier.buildModifierPreviewWithTarget(),
                supportingText = selectable.modifier.condition?.ifBlank { null },
                modifier = Modifier.toggleable(
                    value = selectable.isSelected,
                    enabled = selectable.isEnabled,
                    role = Role.Checkbox,
                    onValueChange = { onModifierClick(selectable.modifier, it) }
                ),
                trailingContent = {
                    Checkbox(
                        checked = selectable.isSelected,
                        onCheckedChange = null,
                        enabled = selectable.isEnabled
                    )
                }
            )
        }
    }
}

@Composable
private fun GroupHeader(
    name: String,
    currentCount: Int,
    limit: Int,
    totalAvailable: Int,
    isExpandable: Boolean,
    isExpanded: Boolean,
    modifier: Modifier = Modifier
) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        Text(
            text = name,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.primary,
            maxLines = 1,
            modifier = Modifier.weight(1f)
        )

        val effectiveLimit = if (limit > 0) limit else totalAvailable
        val isFull = currentCount == effectiveLimit

        Text(
            text = "$currentCount/$effectiveLimit",
            style = MaterialTheme.typography.labelLarge,
            color = if (isFull) MaterialTheme.colorScheme.onSurfaceVariant
            else MaterialTheme.colorScheme.error
        )

        if (isExpandable) {
            Icon(
                imageVector = Icons.Filled.ArrowDropDown,
                contentDescription = null,
                modifier = Modifier
                    .padding(start = 8.dp)
                    .rotate(if (isExpanded) 180f else 0f)
            )
        }
    }
}

@Composable
private fun ModifierBaseItem(
    iconRes: DrawableResource?,
    headline: String,
    supportingText: String? = null,
    modifier: Modifier = Modifier,
    trailingContent: @Composable (() -> Unit)? = null
) {
    ListItem(
        modifier = modifier,
        colors = ListItemDefaults.colors(containerColor = Color.Transparent),
        headlineContent = { Text(text = headline) },
        supportingContent = supportingText?.let { { Text(text = it) } },
        leadingContent = iconRes?.let {
            {
                Icon(
                    painter = painterResource(iconRes),
                    contentDescription = null
                )
            }
        },
        trailingContent = trailingContent
    )
}

@Composable
private fun CustomModifierSubGroup(
    targetKey: String?,
    modifiers: List<CharacterCustomModifier>,
    onDeleteClick: (CharacterCustomModifier) -> Unit,
    onEditClick: (CharacterCustomModifier) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        if (targetKey != null) {
            Text(
                text = targetKey,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 1,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
            )
        }
        modifiers.forEach { mod ->
            CustomModifierItem(
                mod = mod,
                onDeleteClick = { onDeleteClick(mod) },
                onEditClick = { onEditClick(mod) },
            )
        }
    }
}

@Composable
private fun ModifierHeader(title: String) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleLarge,
            modifier = Modifier.padding(16.dp, 8.dp)
        )
    }
}

@Composable
private fun ModifierCard(
    title: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    Surface(
        modifier = modifier.padding(horizontal = 16.dp),
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.extraLarge
    ) {
        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(16.dp, 16.dp, 16.dp, 8.dp)
            )
            content()
        }
    }
}

@Composable
private fun ModifierListContainer(
    isEmpty: Boolean,
    modifier: Modifier = Modifier,
    content: LazyListScope.() -> Unit
) {
    if (isEmpty) {
        FullScreenCard(modifier = modifier) {
            Text(text = stringResource(Res.string.no_modifiers))
        }
    } else {
        LazyColumn(
            modifier = modifier.fillMaxSize(),
            contentPadding = PaddingValues(top = 16.dp, bottom = 88.dp), // Space for FAB
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}