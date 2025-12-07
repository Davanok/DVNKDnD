package com.davanok.dvnkdnd.ui.pages.characterFull.contents

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.material3.carousel.rememberCarouselState
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import com.davanok.dvnkdnd.data.model.entities.character.CharacterSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.data.model.entities.dndEntities.FullSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.SpellSlotsType
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.davanok.dvnkdnd.ui.components.adaptive.alternativeClickable
import com.davanok.dvnkdnd.ui.components.rememberCollapsingNestedScrollConnection
import com.davanok.dvnkdnd.ui.components.text.buildAttacksString
import com.davanok.dvnkdnd.ui.components.text.buildString
import com.mikepenz.markdown.m3.Markdown
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.concentration
import dvnkdnd.composeapp.generated.resources.entity_image
import dvnkdnd.composeapp.generated.resources.multiclass_spell_slot_type_name
import dvnkdnd.composeapp.generated.resources.ritual
import dvnkdnd.composeapp.generated.resources.search
import dvnkdnd.composeapp.generated.resources.spell_cantrip
import dvnkdnd.composeapp.generated.resources.spell_level
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid


private const val SMALL_SPELL_SLOTS_COUNT = 3
private const val PREFERRED_IMAGE_WIDTH_DP = 300


@Composable
fun CharacterSpellsScreen(
    spells: List<CharacterSpell>,
    availableSpellSlots: Map<SpellSlotsType?, IntArray>,
    usedSpells: Map<Uuid?, IntArray>,
    onSpellClick: (DnDFullEntity) -> Unit,
    setUsedSpellsCount: (typeId: Uuid?, lvl: Int, count: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val preparedSpellSlots by remember(availableSpellSlots) {
        derivedStateOf {
            val namesCount = mutableMapOf<String, Int>()
            availableSpellSlots
                .mapKeys { (type, _) ->
                    if (type == null) null
                    else {
                        val name = type.name
                        val count = namesCount.getOrElse(name) { 0 }
                        namesCount[name] = count + 1
                        if (count == 0) type
                        else type.copy(name = "$name ($count)")
                    }
                }
        }
    }

    var filterWord by remember { mutableStateOf("") }
    val visibleSpells by remember(filterWord, spells) {
        derivedStateOf {
            val firstComparator: Comparator<CharacterSpell> =
                if (filterWord.isBlank()) compareByDescending { it.ready }
                else compareBy { it.spell.getDistance(filterWord) }
            val comparator = firstComparator
                .thenBy { it.spell.spell?.spell?.level }
                .thenBy { it.spell.entity.name }

            spells.sortedWith(comparator)
        }
    }

    val lazyColumnState = rememberLazyListState()

    var spellSlotsVisible by remember { mutableStateOf(true) }
    val nestedScrollConnection = rememberCollapsingNestedScrollConnection {
        spellSlotsVisible = it
    }

    var showSelectSpellSlotDialog: DnDFullEntity? by remember { mutableStateOf(null) }

    Column(
        modifier = modifier
    ) {
        AnimatedVisibility(
            visible = spellSlotsVisible
        ) {
            SpellSlots(
                availableSpellSlots = preparedSpellSlots,
                usedSpells = usedSpells,
                onMarkSpellSlotAsUsed = { typeId, lvl ->
                    val currentCount = usedSpells[typeId]?.get(lvl - 1) ?: 0
                    setUsedSpellsCount(typeId, lvl, currentCount + 1)
                                        },
                onMarkSpellSlotAsNotUsed = { typeId, lvl ->
                    val currentCount = usedSpells[typeId]?.get(lvl - 1) ?: 0
                    setUsedSpellsCount(typeId, lvl, currentCount - 1)
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp)
            )
        }
        LazyColumn(
            state = lazyColumnState,
            modifier = Modifier.fillMaxWidth().weight(1f).nestedScroll(nestedScrollConnection),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = visibleSpells,
                key = { it.spell.entity.id }
            ) { characterSpell ->
                SpellCard(
                    characterSpell = characterSpell,
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { showSelectSpellSlotDialog = it.spell },
                    onOpenInfo = onSpellClick
                )
            }
        }
        AnimatedVisibility(
            visible = spellSlotsVisible
        ) {
            OutlinedTextField(
                value = filterWord,
                onValueChange = {
                    filterWord = it
                    if (visibleSpells.isNotEmpty())
                        lazyColumnState.requestScrollToItem(0)
                },
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                label = { Text(text = stringResource(Res.string.search)) }
            )
        }
    }

    showSelectSpellSlotDialog?.let {
        SpellShortInfoDialog(
            entity = it,
            availableSlots = preparedSpellSlots,
            usedSpells = usedSpells,
            onDismissRequest = { showSelectSpellSlotDialog = null },
            onCast = { typeId, lvl ->
                val currentCount = usedSpells[typeId]?.get(lvl - 1) ?: 0
                setUsedSpellsCount(typeId, lvl, currentCount + 1)
            }
        )
    }
}

@Composable
private fun SpellSlots(
    availableSpellSlots: Map<SpellSlotsType?, IntArray>,
    usedSpells: Map<Uuid?, IntArray>,
    onMarkSpellSlotAsUsed: (typeId: Uuid?, lvl: Int) -> Unit,
    onMarkSpellSlotAsNotUsed: (typeId: Uuid?, lvl: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        val isExpandedSlotsView = availableSpellSlots.all { slot -> slot.value.count { it > 0 } <= SMALL_SPELL_SLOTS_COUNT }
        availableSpellSlots.forEach { (spellSlotType, slots) ->
            val usedSpellsOnType = usedSpells.getOrElse(spellSlotType?.id, ::intArrayOf)

            Text(
                text = spellSlotType?.name ?: stringResource(Res.string.multiclass_spell_slot_type_name),
                style = MaterialTheme.typography.titleMedium
            )

            if (isExpandedSlotsView)
                ExpandedSpellSlotsRow(
                    slots = slots,
                    usedSlots = usedSpellsOnType,
                    onMarkSpellSlotAsUsed = { onMarkSpellSlotAsUsed(spellSlotType?.id, it) },
                    onMarkSpellSlotAsNotUsed = { onMarkSpellSlotAsNotUsed(spellSlotType?.id, it) }
                )
            else
                CompactSpellSlotsRow(
                    slots = slots,
                    usedSlots = usedSpellsOnType,
                    onMarkSpellSlotAsUsed = { onMarkSpellSlotAsUsed(spellSlotType?.id, it) },
                    onMarkSpellSlotAsNotUsed = { onMarkSpellSlotAsNotUsed(spellSlotType?.id, it) }
                )
        }
    }
}

@Composable
private fun ExpandedSpellSlotsRow(
    slots: IntArray,
    usedSlots: IntArray,
    onMarkSpellSlotAsUsed: (Int) -> Unit,
    onMarkSpellSlotAsNotUsed: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        slots.forEachIndexed { index, slotCount ->
            val level = index + 1
            val activeSlotsCount = slotCount - usedSlots.getOrElse(index) { 0 }
            Row(
                modifier = Modifier.alternativeClickable(
                    onClick = { onMarkSpellSlotAsUsed(level) },
                    onAlternativeClick = { onMarkSpellSlotAsNotUsed(level) }
                )
            ) {
                Text(text = stringResource(Res.string.spell_level, level))

                repeat(slotCount) {
                    RadioButton(
                        selected = it < activeSlotsCount,
                        onClick = null
                    )
                }
            }
        }
    }
}

@Composable
private fun CompactSpellSlotsRow(
    slots: IntArray,
    usedSlots: IntArray,
    onMarkSpellSlotAsUsed: (Int) -> Unit,
    onMarkSpellSlotAsNotUsed: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.horizontalScroll(rememberScrollState()),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        slots.forEachIndexed { index, slotCount ->
            val level = index + 1
            val activeSlotsCount = slotCount - usedSlots.getOrElse(index) { 0 }

            Column(
                modifier = Modifier.alternativeClickable(
                    onClick = { onMarkSpellSlotAsUsed(level) },
                    onAlternativeClick = { onMarkSpellSlotAsNotUsed(level) }
                ),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = stringResource(Res.string.spell_level, level))

                Text(
                    text = buildString {
                        append(activeSlotsCount)
                        append('/')
                        append(slotCount)
                    },
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpellCard(
    characterSpell: CharacterSpell,
    onClick: (CharacterSpell) -> Unit,
    onOpenInfo: (DnDFullEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    val entity = characterSpell.spell.entity
    val spell = characterSpell.spell.spell

    OutlinedCard(
        modifier = modifier,
        onClick = { onClick(characterSpell) }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.primaryContainer)
                        .clickable { onOpenInfo(characterSpell.spell) },
                    contentAlignment = Alignment.Center
                ) {
                    characterSpell.spell.entity.image?.let { image ->
                        AsyncImage(
                            model = image,
                            contentDescription = null
                        )
                    } ?: Text(
                        text = entity.name.firstOrNull()?.toString() ?: "*",
                        style = MaterialTheme.typography.titleLarge
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = entity.name,
                        style = MaterialTheme.typography.titleMedium
                    )

                    if (spell != null) {
                        val levelText = spell.spell.level.let {
                            if (it == 0) stringResource(Res.string.spell_cantrip)
                            else stringResource(Res.string.spell_level, it)
                        }
                        Text(
                            text = "${stringResource(spell.spell.school.stringRes)} • $levelText",
                            style = MaterialTheme.typography.bodySmall,
                            modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                        )

                        Text(
                            text = buildString {
                                if (spell.spell.components.isNotEmpty()) {
                                    spell.spell.components.sortedBy { it.ordinal }.forEach {
                                        append(stringResource(it.stringResShort))
                                    }
                                }
                                spell.spell.materialComponent?.let {
                                    if (isNotEmpty()) append(" — $it")
                                }
                            },
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 2
                        )
                    }
                }

                Column {
                    spell?.let {
                        if (it.spell.ritual) AssistChip(
                            onClick = {},
                            label = { Text(stringResource(Res.string.ritual)) },
                            enabled = false
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = if (it.spell.concentration) stringResource(Res.string.concentration) else it.spell.duration,
                            style = MaterialTheme.typography.labelSmall,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }

            spell?.let { s ->
                if (s.attacks.isNotEmpty()) {
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        spell.attacks.forEach { attack ->
                            TooltipBox(
                                positionProvider = TooltipDefaults.rememberTooltipPositionProvider(
                                    TooltipAnchorPosition.Above
                                ),
                                tooltip = { PlainTooltip { Text(text = stringResource(attack.damageType.stringRes)) } },
                                state = rememberTooltipState()
                            ) {
                                AssistChip(
                                    onClick = { /* noop */ },
                                    label = { Text(text = attack.buildString()) },
                                    leadingIcon = attack.damageType.drawableRes?.let {
                                        {
                                            Icon(
                                                painter = painterResource(it),
                                                contentDescription = stringResource(attack.damageType.stringRes),
                                                tint = attack.damageType.color ?: LocalContentColor.current
                                            )
                                        }
                                    }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SpellShortInfoDialog(
    entity: DnDFullEntity,
    availableSlots: Map<SpellSlotsType?, IntArray>,
    usedSpells: Map<Uuid?, IntArray>,
    onDismissRequest: () -> Unit,
    onCast: (spellSlotType: Uuid?, level: Int) -> Unit
) {
    AdaptiveModalSheet(
        onDismissRequest = onDismissRequest,
        title = { Text(text = entity.entity.name) }
    ) {
        var additionalContentExpanded by remember { mutableStateOf(true) }
        val nestedScrollConnection = rememberCollapsingNestedScrollConnection {
            additionalContentExpanded = it
        }
        Column(
            modifier = Modifier.padding(bottom = 8.dp)
        ) {
            if (entity.images.isNotEmpty())
                AnimatedVisibility(
                    visible = additionalContentExpanded
                ) {
                    HorizontalMultiBrowseCarousel(
                        modifier = Modifier.fillMaxWidth().height(300.dp),
                        state = rememberCarouselState { entity.images.size },
                        preferredItemWidth = PREFERRED_IMAGE_WIDTH_DP.dp,
                        itemSpacing = 12.dp
                    ) { index ->
                        AsyncImage(
                            modifier = Modifier
                                .fillMaxSize()
                                .maskClip(MaterialTheme.shapes.extraLarge),
                            model = entity.images[index].path,
                            contentDescription = stringResource(Res.string.entity_image),
                        )
                    }
                }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .nestedScroll(nestedScrollConnection)
            ) {
                entity.spell?.let { spell ->
                    SpellShortInfoCard(
                        spell = spell,
                        modifier = Modifier.padding(vertical = 8.dp).fillMaxWidth()
                    )
                }

                Markdown(entity.entity.description)
            }

            entity.spell?.let { spell ->
                AnimatedVisibility(visible = additionalContentExpanded) {
                    Column(
                        modifier = Modifier.heightIn(max = 400.dp).verticalScroll(rememberScrollState())
                    ) {
                        availableSlots.filter { it.value.size >= spell.spell.level }.forEach { (slotType, slots) ->
                            Text(
                                text = slotType?.name ?: stringResource(Res.string.multiclass_spell_slot_type_name),
                                style = MaterialTheme.typography.titleMedium
                            )

                            Row(
                                modifier = Modifier.horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                slots.forEachIndexed { index, count ->
                                    val level = index + 1
                                    if (level < spell.spell.level) return@forEachIndexed
                                    val availableCount = count - (usedSpells[slotType?.id]?.getOrNull(index) ?: 0)

                                    AssistChip(
                                        onClick = { onCast(slotType?.id, level) },
                                        label = {
                                            Column(
                                                verticalArrangement = Arrangement.spacedBy(8.dp)
                                            ) {
                                                Text(text = buildString {
                                                    append(stringResource(Res.string.spell_level, level))
                                                    append(" (")
                                                    append(availableCount)
                                                    append(')')
                                                })

                                                if (spell.attacks.isNotEmpty())
                                                    Text(text = spell.buildAttacksString(level))
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun SpellShortInfoCard(
    spell: FullSpell,
    modifier: Modifier = Modifier,
) {
    Card(
        modifier = modifier
    ) {
        Row(modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                val levelText = spell.spell.level.let {
                    if (it == 0) stringResource(Res.string.spell_cantrip)
                    else stringResource(Res.string.spell_level, it)
                }
                Text(
                    text = "${stringResource(spell.spell.school.stringRes)} • $levelText",
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 2.dp, bottom = 6.dp)
                )

                Text(
                    text = buildString {
                        if (spell.spell.components.isNotEmpty()) {
                            spell.spell.components.sortedBy { it.ordinal }.forEach {
                                append(stringResource(it.stringResShort))
                            }
                        }
                        spell.spell.materialComponent?.let {
                            if (isNotEmpty()) append(" — $it")
                        }
                    },
                    style = MaterialTheme.typography.bodySmall,
                    maxLines = 2
                )
            }
            Column {
                spell.let {
                    if (it.spell.ritual) AssistChip(
                        onClick = {},
                        label = { Text(stringResource(Res.string.ritual)) },
                        enabled = false
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (it.spell.concentration) stringResource(Res.string.concentration) else it.spell.duration,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }
        }
    }
}