package com.davanok.dvnkdnd.ui.pages.characterFull.pages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.LinkOff
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.outlined.Shield
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconToggleButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.davanok.dvnkdnd.core.CoinsConverter
import com.davanok.dvnkdnd.domain.entities.character.CharacterItem
import com.davanok.dvnkdnd.domain.entities.character.CoinsGroup
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItemActivation
import com.davanok.dvnkdnd.domain.entities.dndEntities.Item
import com.davanok.dvnkdnd.domain.entities.dndEntities.ItemProperty
import com.davanok.dvnkdnd.domain.enums.configs.MeasurementSystem
import com.davanok.dvnkdnd.domain.enums.dndEnums.Coins
import com.davanok.dvnkdnd.ui.components.BaseEntityImage
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.davanok.dvnkdnd.ui.components.rememberCollapsingNestedScrollConnection
import com.davanok.dvnkdnd.ui.components.text.MeasurementConverter
import com.davanok.dvnkdnd.ui.components.text.buildAnnotatedString
import com.davanok.dvnkdnd.ui.components.text.buildDamagesString
import com.davanok.dvnkdnd.ui.components.text.buildString
import com.davanok.dvnkdnd.ui.components.toSignedString
import com.davanok.dvnkdnd.ui.pages.characterFull.components.ImagesCarousel
import com.davanok.dvnkdnd.ui.providers.LocalMeasurementSystem
import com.mikepenz.markdown.m3.Markdown
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.active_items_filter_name
import dvnkdnd.composeapp.generated.resources.attack_bonus_short
import dvnkdnd.composeapp.generated.resources.attuned
import dvnkdnd.composeapp.generated.resources.attuned_items_filter_name
import dvnkdnd.composeapp.generated.resources.cost
import dvnkdnd.composeapp.generated.resources.decrement_count
import dvnkdnd.composeapp.generated.resources.equipped
import dvnkdnd.composeapp.generated.resources.equipped_items_filter_name
import dvnkdnd.composeapp.generated.resources.increment_count
import dvnkdnd.composeapp.generated.resources.item_count
import dvnkdnd.composeapp.generated.resources.not_attuned
import dvnkdnd.composeapp.generated.resources.not_equipped
import dvnkdnd.composeapp.generated.resources.properties
import dvnkdnd.composeapp.generated.resources.total_items_count_value
import dvnkdnd.composeapp.generated.resources.total_weight_value
import dvnkdnd.composeapp.generated.resources.weight
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.filter
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.pluralStringResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid


private const val ITEMS_COUNT_CHANGE_DEBOUNCE = 300L

private enum class CharacterItemsFilter(val stringRes: StringResource) {
    ACTIVE(Res.string.active_items_filter_name),
    ATTUNED(Res.string.attuned_items_filter_name),
    EQUIPPED(Res.string.equipped_items_filter_name)
}

private data class ItemsInfo(
    val itemsCount: Int,
    val activeCount: Int,
    val attunedCount: Int,
    val equippedCount: Int,
    val totalWeight: Int,
    val totalCost: CoinsGroup
) {
    operator fun get(filter: CharacterItemsFilter): Int = when (filter) {
        CharacterItemsFilter.ACTIVE -> activeCount
        CharacterItemsFilter.ATTUNED -> attunedCount
        CharacterItemsFilter.EQUIPPED -> equippedCount
    }
}

private fun calculateItemsInfo(items: List<CharacterItem>): ItemsInfo {
    var activeCount = 0
    var attunedCount = 0
    var equippedCount = 0
    var totalWeight = 0
    var totalCost = 0

    items.forEach { characterItem ->
        if (characterItem.active) activeCount++
        if (characterItem.attuned) attunedCount++
        if (characterItem.equipped) equippedCount++

        characterItem.item.item?.item?.run {
            if (weight != null) totalWeight += weight
            if (cost != null) totalCost += cost
        }
    }

    return ItemsInfo(
        itemsCount = items.size,
        activeCount = activeCount,
        attunedCount = attunedCount,
        equippedCount = equippedCount,
        totalWeight = totalWeight,
        totalCost = CoinsConverter.copperToGroup(totalCost)
    )
}

@Composable
fun CharacterItemsScreen(
    characterCoins: CoinsGroup,
    items: List<CharacterItem>,
    usedActivations: Map<Uuid, Int>,
    onClick: (DnDFullEntity) -> Unit,
    modifier: Modifier = Modifier,
) {
    var characterItemInfoDialog: CharacterItem? by remember { mutableStateOf(null) }
    var itemsInfoExpanded by remember { mutableStateOf(true) }
    val nestedScrollConnection = rememberCollapsingNestedScrollConnection {
        itemsInfoExpanded = it
    }

    var currentFilter: CharacterItemsFilter? by remember { mutableStateOf(null) }
    val itemsInfo = remember(items) { calculateItemsInfo(items) }
    val preparedItems = remember(items, currentFilter) {
        when (currentFilter) {
            CharacterItemsFilter.ACTIVE -> items.filter { it.active }
            CharacterItemsFilter.ATTUNED -> items.filter { it.attuned }
            CharacterItemsFilter.EQUIPPED -> items.filter { it.equipped }
            null -> items
        }
    }

    Column(modifier = modifier) {
        ItemsInfoHeader(
            characterCoins = characterCoins,
            info = itemsInfo,
            currentFilter = currentFilter,
            onFilterChange = { currentFilter = it },
            expanded = itemsInfoExpanded,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        LazyColumn(
            modifier = Modifier.nestedScroll(nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = preparedItems,
                key = { it.item.entity.id }
            ) { item ->
                CharacterItemCard(
                    modifier = Modifier.fillMaxWidth(),
                    characterItem = item,
                    onClick = { characterItemInfoDialog = item },
                    onOpenInfo = { onClick(item.item) },
                    setItemEquipped = { TODO() },
                    setItemAttuned = { TODO() },
                )
            }
        }
    }

    characterItemInfoDialog?.let {
        ItemShortInfoDialog(
            characterItem = it,
            usedActivations = usedActivations,
            onDismissRequest = { characterItemInfoDialog = null },
            setItemEquipped = { TODO() },
            setItemAttuned = { TODO() },
            onCountChange = { TODO() },
            activate = { TODO() },
        )
    }
}

@Composable
private fun ItemsInfoHeader(
    characterCoins: CoinsGroup,
    info: ItemsInfo,
    currentFilter: CharacterItemsFilter?,
    onFilterChange: (CharacterItemsFilter?) -> Unit,
    expanded: Boolean,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        Text(text = characterCoins.buildAnnotatedString())
        Row(
            modifier = Modifier.horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            CharacterItemsFilter.entries.forEach {
                val selected = currentFilter == it
                FilterChip(
                    selected = selected,
                    onClick = { onFilterChange(if (selected) null else it) },
                    label = {
                        Text(
                            text = "${stringResource(it.stringRes)}: ${info[it]}"
                        )
                    }
                )
            }
        }
        AnimatedVisibility(
            visible = expanded
        ) {
            Column {
                Text(
                    text = pluralStringResource(
                        Res.plurals.total_items_count_value,
                        info.itemsCount,
                        info.itemsCount
                    )
                )

                WeightTextWithTooltip(
                    weight = info.totalWeight
                ) { weight ->
                    Text(
                        text = stringResource(Res.string.total_weight_value, weight)
                    )
                }
            }
        }
    }
}

@Composable
private fun CharacterItemCard(
    characterItem: CharacterItem,
    onClick: () -> Unit,
    onOpenInfo: () -> Unit,
    setItemEquipped: (Boolean) -> Unit,
    setItemAttuned: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    val entity = characterItem.item.entity
    val fullItem = characterItem.item.item

    OutlinedCard(
        modifier = modifier,
        onClick = onClick
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.Top) {
                BadgedBox(
                    badge = {
                        if (characterItem.count != null && characterItem.count > 1)
                            Badge {
                                Text(
                                    characterItem.count.toString()
                                )
                            }
                    }
                ) {
                    BaseEntityImage(
                        entity = entity,
                        onClick = onOpenInfo,
                        modifier = Modifier
                            .size(56.dp)
                            .border(
                                width = 2.dp,
                                color = fullItem?.item?.rarity?.color ?: Color.Transparent,
                                shape = MaterialTheme.shapes.medium
                            )
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entity.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = fullItem?.item?.rarity?.color ?: Color.Unspecified
                    )

                    if (fullItem != null) {
                        fullItem.armor?.let { armor ->
                            Text(
                                text = armor.buildString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        fullItem.weapon?.let { weapon ->
                            if (weapon.atkBonus != 0)
                                Text(
                                    text = stringResource(
                                        Res.string.attack_bonus_short,
                                        weapon.atkBonus.toSignedString()
                                    ),
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.primary
                                )
                            Text(
                                text = weapon.buildDamagesString(),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        ItemMetaInfo(fullItem.item)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (fullItem?.requiresAttunement() == true)
                        CharacterItemStateIconButton(
                            checked = characterItem.attuned,
                            onCheckedChange = setItemAttuned,
                            iconChecked = Icons.Default.Link,
                            iconNotChecked = Icons.Default.LinkOff,
                            descriptionChecked = stringResource(Res.string.attuned),
                            descriptionNotChecked = stringResource(Res.string.not_attuned),
                        )
                    if (fullItem?.item?.equippable == true)
                        CharacterItemStateIconButton(
                            checked = characterItem.equipped,
                            onCheckedChange = setItemEquipped,
                            iconChecked = Icons.Filled.Shield,
                            iconNotChecked = Icons.Outlined.Shield,
                            descriptionChecked = stringResource(Res.string.equipped),
                            descriptionNotChecked = stringResource(Res.string.not_equipped),
                        )
                }
            }

            if (!fullItem?.properties.isNullOrEmpty())
                ItemPropertiesRow(
                    properties = fullItem.properties
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CharacterItemStateIconButton(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    iconChecked: ImageVector,
    iconNotChecked: ImageVector,
    descriptionChecked: String,
    descriptionNotChecked: String,
    modifier: Modifier = Modifier
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Above
        ),
        tooltip = {
            PlainTooltip {
                Text(text = if (checked) descriptionChecked else descriptionNotChecked)
            }
        },
        state = rememberTooltipState()
    ) {
        IconToggleButton(
            checked = checked,
            onCheckedChange = onCheckedChange,
            modifier = modifier
        ) {
            if (checked)
                Icon(
                    imageVector = iconChecked,
                    contentDescription = descriptionChecked
                )
            else
                Icon(
                    imageVector = iconNotChecked,
                    contentDescription = descriptionNotChecked
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemPropertiesRow(
    properties: List<ItemProperty>,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        properties.forEach { property ->
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Above
                ),
                tooltip = {
                    RichTooltip(
                        title = { Text(text = property.name) }
                    ) {
                        Text(text = property.description)
                    }
                },
                state = rememberTooltipState()
            ) {
                AssistChip(
                    onClick = { /* noop */ },
                    label = {
                        Text(
                            text = property.name,
                            style = MaterialTheme.typography.labelMedium
                        )
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ItemMetaInfo(
    item: Item,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
    ) {
        if (item.cost != null) {
            val baseGroup = remember(item.cost) {
                CoinsConverter.copperToGroup(item.cost)
            }

            val tooltipEntries = remember(item.cost) {
                Coins.entries
                    .asReversed()
                    .associateWith {
                        CoinsConverter.copperToGroup(item.cost, maxCoin = it)
                    }.filter { it.value[it.key] > 0 }
            }
            TooltipBox(
                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                    TooltipAnchorPosition.Above
                ),
                tooltip = {
                    RichTooltip(
                        title = { Text(stringResource(Res.string.cost)) },
                        text = {
                            Text(text = tooltipEntries.map { (coin, group) ->
                                "${stringResource(coin.stringRes)}: ${group.buildString()}"
                            }.joinToString("\n"))
                        }
                    )
                },
                state = rememberTooltipState()
            ) {
                Text(
                    text = "${stringResource(Res.string.cost)}: ${baseGroup.buildString()}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

        if (item.weight != null)
            WeightTextWithTooltip(
                weight = item.weight
            ) { weight ->
                Text(
                    text = "${stringResource(Res.string.weight)}: $weight",
                    style = MaterialTheme.typography.bodySmall
                )
            }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun WeightTextWithTooltip(
    weight: Int,
    modifier: Modifier = Modifier,
    text: @Composable (weight: String) -> Unit
) {
    val measurementSystem = LocalMeasurementSystem.current.weight
    TooltipBox(
        modifier = modifier,
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
            TooltipAnchorPosition.Above
        ),
        tooltip = {
            RichTooltip(
                title = { Text(stringResource(Res.string.weight)) },
                text = {
                    Text(
                        text = MeasurementConverter.convertWeight(
                            weight,
                            if (measurementSystem == MeasurementSystem.METRIC)
                                MeasurementSystem.IMPERIAL
                            else
                                MeasurementSystem.METRIC
                        )
                    )
                }
            )
        },
        state = rememberTooltipState()
    ) {
        val weight = MeasurementConverter.convertWeight(weight, measurementSystem)
        text(weight)
    }
}

@OptIn(FlowPreview::class)
@Composable
fun ItemShortInfoDialog(
    characterItem: CharacterItem,
    usedActivations: Map<Uuid, Int>,
    onDismissRequest: () -> Unit,
    setItemEquipped: (Boolean) -> Unit,
    setItemAttuned: (Boolean) -> Unit,
    onCountChange: (Int) -> Unit,
    activate: (FullItemActivation) -> Unit
) {
    val entity = characterItem.item
    val fullItem = entity.item

    AdaptiveModalSheet(
        onDismissRequest = onDismissRequest,
        title = {
            Text(
                text = entity.entity.name,
                color = characterItem.item.item?.item?.rarity?.color ?: Color.Unspecified
            )
        }
    ) {
        var additionalContentExpanded by remember { mutableStateOf(true) }
        val nestedScrollConnection = rememberCollapsingNestedScrollConnection {
            additionalContentExpanded = it
        }

        Column(modifier = Modifier.padding(bottom = 8.dp)) {
            if (entity.images.isNotEmpty()) {
                AnimatedVisibility(visible = additionalContentExpanded) {
                    ImagesCarousel(
                        images = entity.images.map { it.path },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .nestedScroll(nestedScrollConnection)
            ) {
                // Item Quick Stats Card
                fullItem?.let {
                    ItemShortInfoCard(
                        characterItem = characterItem,
                        setItemEquipped = setItemEquipped,
                        setItemAttuned = setItemAttuned,
                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                    )
                }

                if (characterItem.count != null) {
                    var countTextFieldValue by remember { mutableStateOf(characterItem.count) }

                    LaunchedEffect(countTextFieldValue) {
                        if (countTextFieldValue > 0)
                            snapshotFlow { countTextFieldValue }
                                .debounce(ITEMS_COUNT_CHANGE_DEBOUNCE)
                                .filter { it != characterItem.count }
                                .collect { onCountChange(it) }
                    }

                    OutlinedTextField(
                        modifier = Modifier.fillMaxWidth(),
                        value = countTextFieldValue.toString(),
                        onValueChange = { value ->
                            value.toIntOrNull()
                                ?.takeIf { it >= 0 }
                                ?.let { countTextFieldValue = it }
                        },
                        label = {
                            Text(text = stringResource(Res.string.item_count))
                        },
                        leadingIcon = {
                            IconButton(
                                onClick = { countTextFieldValue-- },
                                enabled = countTextFieldValue > 1
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = stringResource(Res.string.decrement_count)
                                )
                            }
                        },
                        trailingIcon = {
                            IconButton(
                                onClick = { countTextFieldValue++ }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = stringResource(Res.string.increment_count)
                                )
                            }
                        }
                    )
                    Spacer(Modifier.height(8.dp))
                }

                // Description
                Markdown(entity.entity.description)

                // Item Properties (Tags)
                fullItem?.properties?.takeIf { it.isNotEmpty() }?.let { properties ->
                    Text(
                        text = stringResource(Res.string.properties),
                        style = MaterialTheme.typography.titleSmall,
                        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp)
                    )
                    ItemPropertiesRow(properties)
                }
            }

            // Action Section: Activations (like "Cast a spell from staff")
            fullItem?.activations?.takeIf { it.isNotEmpty() }?.let { activations ->
                AnimatedVisibility(visible = additionalContentExpanded) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        activations.forEach { activation ->

                            val text = remember(activation, usedActivations) {
                                buildString {
                                    append(activation.name)
                                    if (activation.count != null) {
                                        val usedCount = usedActivations[activation.id] ?: 0

                                        append(' ')
                                        append(activation.count - usedCount)
                                        append('/')
                                        append(activation.count)
                                    }
                                }
                            }

                            AssistChip(
                                onClick = { activate(activation) },
                                enabled = !activation.requiresAttunement || characterItem.attuned,
                                label = { Text(text = text) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ItemShortInfoCard(
    characterItem: CharacterItem,
    setItemEquipped: (Boolean) -> Unit,
    setItemAttuned: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val fullItem = characterItem.item.item
    Card(modifier = modifier) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                fullItem?.let {
                    Column(modifier = Modifier.weight(1f)) {
                        // Rarity and Type
                        Text(
                            text = stringResource(fullItem.item.rarity.stringRes),
                            style = MaterialTheme.typography.labelLarge,
                            color = MaterialTheme.colorScheme.secondary
                        )
                        Spacer(Modifier.height(8.dp))
                        // Weight and Cost
                        ItemMetaInfo(fullItem.item)
                    }
                }

                Column {
                    fullItem?.armor?.let { armor ->
                        Text(
                            text = armor.buildString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    fullItem?.weapon?.let { weapon ->
                        if (weapon.atkBonus != 0)
                            Text(
                                text = stringResource(
                                    Res.string.attack_bonus_short,
                                    weapon.atkBonus.toSignedString()
                                ),
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.primary
                            )
                        Text(
                            text = weapon.buildDamagesString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

            // Attunement / Equipped Status Chips
            Row(
                modifier = Modifier.padding(top = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (fullItem?.requiresAttunement() == true) {
                    InputChip(
                        selected = characterItem.attuned,
                        onClick = { setItemAttuned(!characterItem.attuned) },
                        label = {
                            Text(
                                text = if (characterItem.attuned) stringResource(Res.string.attuned)
                                else stringResource(Res.string.not_attuned),
                                maxLines = 1
                            )
                        },
                        trailingIcon = {
                            Icon(
                                if (characterItem.attuned) Icons.Default.Link else Icons.Default.LinkOff,
                                contentDescription = null
                            )
                        }
                    )
                }
                if (fullItem?.item?.equippable == true) {
                    InputChip(
                        selected = characterItem.equipped,
                        onClick = { setItemEquipped(!characterItem.equipped) },
                        label = {
                            Text(
                                text = if (characterItem.equipped) stringResource(Res.string.equipped)
                                else stringResource(Res.string.not_equipped),
                                maxLines = 1
                            )
                        },
                        trailingIcon = {
                            Icon(
                                if (characterItem.equipped) Icons.Filled.Shield else Icons.Outlined.Shield,
                                contentDescription = null
                            )
                        }
                    )
                }
            }
        }
    }
}
