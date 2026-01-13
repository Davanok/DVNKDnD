package com.davanok.dvnkdnd.ui.pages.characterFull

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveContent
import com.davanok.dvnkdnd.ui.components.adaptive.SupportEntry
import com.davanok.dvnkdnd.ui.components.adaptive.rememberAdaptiveContentState
import com.davanok.dvnkdnd.ui.model.isCritical
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterAttacksScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterFullAttributesScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.dialogs.CharacterHealthDialogContent
import com.davanok.dvnkdnd.ui.pages.characterFull.dialogs.CharacterMainEntitiesDialog
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterItemsScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterNotesScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterSpellSlotsScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterSpellsScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterStatesScreen
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.no_such_character_error
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun CharacterFullScreen(
    navigateBack: () -> Unit,
    navigateToEntityInfo: (DnDEntityMin) -> Unit,
    viewModel: CharacterFullViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    when {
        uiState.isLoading -> LoadingCard()
        uiState.error.isCritical() -> uiState.error?.let {
            ErrorCard(
                text = it.message,
                exception = it.exception,
                onBack = navigateBack
            )
        }
        else -> uiState.character.let { character ->
            if (character == null)
                ErrorCard(
                    text = stringResource(Res.string.no_such_character_error),
                    onBack = navigateBack
                )
            else
                Content(
                    navigateBack = navigateBack,
                    navigateToEntityInfo = navigateToEntityInfo,
                    character = character,
                    action = viewModel::action
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    navigateBack: () -> Unit,
    navigateToEntityInfo: (DnDEntityMin) -> Unit,
    character: CharacterFull,
    action: (CharacterFullScreenContract) -> Unit
) {
    val adaptiveContentState = rememberAdaptiveContentState<CharacterFullUiState.Dialog> { entry ->
        when (entry) {
            CharacterFullUiState.Dialog.HEALTH -> SupportEntry(
                titleGetter = { stringResource(entry.titleStringRes) },
                content = {
                    CharacterHealthDialogContent(
                        baseHealth = character.health,
                        updateHealth = { action(CharacterFullScreenContract.SetHealth(it)) },
                        healthModifiers = character.appliedModifiers
                            .getOrElse(DnDModifierTargetType.HEALTH, ::emptyList)
                    )
                }
            )
            CharacterFullUiState.Dialog.MAIN_ENTITIES -> SupportEntry(
                titleGetter = { stringResource(entry.titleStringRes) },
                ignoreWindows = true,
                content = {
                    CharacterMainEntitiesDialog(

                    )
                }
            )
            CharacterFullUiState.Dialog.NONE -> null
        }
    }

    val appBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            MediumTopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                title = {
                    Row(
                        modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)
                    ) {
                        Text(
                            text = character.character.name,
                            maxLines = 1,
                            modifier = Modifier.weight(1f)
                        )

                        VerticalDivider()

                        MainEntitiesWidget(
                            entities = character.mainEntities,
                            onClick = { adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.MAIN_ENTITIES) },
                            modifier = Modifier.padding(horizontal = 8.dp).fillMaxHeight()
                        )
                    }
                },
                scrollBehavior = appBarScrollBehavior
            )
        }
    ) { paddingValues ->
        AdaptiveContent(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .nestedScroll(appBarScrollBehavior.nestedScrollConnection),
            state = adaptiveContentState,
            panesSpacing = 8.dp,
            singlePaneContent = {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CharacterMainValuesWidget(
                        values = character.appliedValues,
                        states = character.states,
                        onInitiativeClick = { TODO() },
                        onArmorClassClick = { TODO() },
                        onHealthClick = { adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.HEALTH) },
                        onSpeedClick = { TODO() },
                        onStateClick = { TODO() },
                        onAddStateClick = { TODO() }
                    )
                    CharacterPages(
                        modifier = Modifier.weight(1f),
                        character = character,
                        skipAttributes = false,
                        onEntityClick = navigateToEntityInfo,
                        action = action
                    )
                }
            },
            twoPaneContent = Pair(
                {
                    Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        CharacterMainValuesWidget(
                            values = character.appliedValues,
                            states = character.states,
                            onInitiativeClick = { TODO() },
                            onArmorClassClick = { TODO() },
                            onHealthClick = { adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.HEALTH) },
                            onSpeedClick = { TODO() },
                            onStateClick = { TODO() },
                            onAddStateClick = { TODO() },
                            modifier = Modifier.fillMaxWidth()
                        )
                        CharacterFullAttributesScreen(
                            modifier = Modifier.weight(1f),
                            attributes = character.appliedValues.savingThrowModifiers,
                            savingThrows = character.appliedValues.savingThrowModifiers,
                            skills = character.appliedValues.skillModifiers,
                            onAttributeClick = { TODO() },
                            onSavingThrowClick = { TODO() },
                            onSkillClick = { TODO() }
                        )
                    }
                },
                {
                    CharacterPages(
                        modifier = Modifier.fillMaxSize(),
                        character = character,
                        skipAttributes = true,
                        onEntityClick = navigateToEntityInfo,
                        action = action
                    )
                }
            )
        )
    }
}

@Composable
private fun CharacterFullTabsRow(
    pagerState: PagerState,
    pages: List<CharacterFullUiState.Page>
) {
    val scope = rememberCoroutineScope()
    PrimaryScrollableTabRow(
        selectedTabIndex = pagerState.currentPage,
    ) {
        pages.fastForEachIndexed { index, page ->
            Tab(
                selected = pagerState.currentPage == index,
                onClick = { scope.launch { pagerState.animateScrollToPage(index) } },
                text = { Text(text = stringResource(page.stringRes)) }
            )
        }
    }
}

@Composable
private fun CharacterPages(
    character: CharacterFull,
    skipAttributes: Boolean,
    onEntityClick: (DnDEntityMin) -> Unit,
    action: (CharacterFullScreenContract) -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = remember(skipAttributes) {
        if (skipAttributes) CharacterFullUiState.Page.entries.drop(1)
        else CharacterFullUiState.Page.entries
    }

    Column(modifier = modifier) {
        val pagerState = rememberPagerState { pages.size }
        CharacterFullTabsRow(pagerState, pages)
        Spacer(Modifier.height(8.dp))
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState,
            pageSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { index ->
            when (pages[index]) {
                CharacterFullUiState.Page.ATTRIBUTES -> CharacterFullAttributesScreen(
                    attributes = character.appliedValues.savingThrowModifiers,
                    savingThrows = character.appliedValues.savingThrowModifiers,
                    skills = character.appliedValues.skillModifiers,
                    modifier = Modifier.fillMaxSize(),
                    onAttributeClick = { TODO() },
                    onSavingThrowClick = { TODO() },
                    onSkillClick = { TODO() }
                )
                CharacterFullUiState.Page.ATTACKS -> CharacterAttacksScreen(
                    attacks = character.attacks,
                    modifier = Modifier.fillMaxSize()
                )
                CharacterFullUiState.Page.ITEMS -> CharacterItemsScreen(
                    characterCoins = character.coins,
                    items = character.items,
                    usedActivations = character.usedItemActivations,
                    onClick = { onEntityClick(it.toDnDEntityMin()) },
                    onUpdateCharacterItem = {
                        action(CharacterFullScreenContract.UpdateCharacterItem(it))
                    },
                    onActivateItem = { item, activation ->
                        action(CharacterFullScreenContract.ActivateCharacterItem(item, activation))
                    },
                    modifier = Modifier.fillMaxSize()
                )
                CharacterFullUiState.Page.SPELLS -> CharacterSpellsScreen(
                    spells = character.spells,
                    spellCastingValues = character.getSpellCastingValues(),
                    availableSpellSlots = character.spellSlots,
                    usedSpells = character.usedSpells,
                    onSpellClick = { onEntityClick(it.toDnDEntityMin()) },
                    setUsedSpellsCount = { typeId, lvl, count ->
                        action(CharacterFullScreenContract.SetUsedSpellsCount(typeId, lvl, count))
                                         },
                    modifier = Modifier.fillMaxSize()
                )
                CharacterFullUiState.Page.SPELL_SLOTS -> CharacterSpellSlotsScreen(
                    availableSpellSlots = character.spellSlots,
                    usedSpells = character.usedSpells,
                    setUsedSpellsCount = { typeId, lvl, count ->
                        action(CharacterFullScreenContract.SetUsedSpellsCount(typeId, lvl, count))
                    },
                    modifier = Modifier.fillMaxSize()
                )
                CharacterFullUiState.Page.STATES -> CharacterStatesScreen(
                    states = character.states,
                    onClick = { onEntityClick(it.toDnDEntityMin()) },
                    modifier = Modifier.fillMaxSize()
                )
                CharacterFullUiState.Page.NOTES -> CharacterNotesScreen(
                    notes = character.notes,
                    onUpdateOrNewNote = {
                        action(CharacterFullScreenContract.UpdateOrNewNote(it))
                    },
                    onDeleteNote = {
                        action(CharacterFullScreenContract.DeleteNote(it))
                    },
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
