package com.davanok.dvnkdnd.ui.pages.characterFull

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumTopAppBar
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.TopAppBarScrollBehavior
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
import com.davanok.dvnkdnd.domain.entities.character.toCharacterStateLink
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import com.davanok.dvnkdnd.ui.components.DescriptionIconButton
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.UiStateHandler
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveContent
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveContentState
import com.davanok.dvnkdnd.ui.components.adaptive.SupportEntry
import com.davanok.dvnkdnd.ui.components.adaptive.rememberAdaptiveContentState
import com.davanok.dvnkdnd.ui.components.diceRoller.rememberDiceRollerState
import com.davanok.dvnkdnd.ui.fragments.searchEntityScaffold.SearchEntityResult
import com.davanok.dvnkdnd.ui.fragments.searchEntityScaffold.SearchEntityScaffold
import com.davanok.dvnkdnd.ui.pages.characterFull.components.CharacterThrowsDiceRoller
import com.davanok.dvnkdnd.ui.pages.characterFull.components.ThrowsDiceRollerModifier
import com.davanok.dvnkdnd.ui.pages.characterFull.dialogs.CharacterHealthDialogContent
import com.davanok.dvnkdnd.ui.pages.characterFull.dialogs.CharacterMainEntitiesDialog
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterAttacksScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterFullAttributesScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterItemsScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterNotesScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterSpellSlotsScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterSpellsScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.pages.CharacterStatesScreen
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.character
import dvnkdnd.composeapp.generated.resources.edit_character
import dvnkdnd.composeapp.generated.resources.no_such_character_error
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun CharacterFullScreen(
    navigateBack: () -> Unit,
    navigateToEditCharacter: () -> Unit,
    navigateToEntityInfo: (DnDEntityMin) -> Unit,
    viewModel: CharacterFullViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val character = uiState.character
    val adaptiveContentState = character?.let {
        rememberAdaptiveCharacterContentState(
            character = it,
            eventSink = viewModel::eventSink,
            navigateToEntityInfo = navigateToEntityInfo
        )
    }
    val appBarScrollBehavior = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold(
        topBar = {
            CharacterTopAppBar(
                character = character,
                adaptiveContentState = adaptiveContentState,
                scrollBehavior = appBarScrollBehavior,
                navigateBack = navigateBack,
                navigateToEditCharacter = navigateToEditCharacter
            )
        }
    ) { paddingValues ->
        UiStateHandler(
            isLoading = uiState.isLoading,
            error = uiState.error,
            modifier = Modifier.padding(paddingValues)
        ) {
            if (character == null || adaptiveContentState == null) {
                ErrorCard(
                    text = stringResource(Res.string.no_such_character_error),
                    onBack = navigateBack
                )
            } else {
                Content(
                    adaptiveContentState = adaptiveContentState,
                    navigateToEntityInfo = navigateToEntityInfo,
                    character = character,
                    eventSink = viewModel::eventSink,
                    modifier = Modifier
                        .fillMaxSize()
                        .nestedScroll(appBarScrollBehavior.nestedScrollConnection)
                )
            }
        }
    }
}

@Composable
private fun CharacterTopAppBar(
    character: CharacterFull?,
    adaptiveContentState: AdaptiveContentState<CharacterFullUiState.Dialog>?,
    scrollBehavior: TopAppBarScrollBehavior,
    navigateBack: () -> Unit,
    navigateToEditCharacter: () -> Unit,
) {
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
            Row(modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min)) {
                Text(
                    text = character?.character?.name ?: stringResource(Res.string.character),
                    maxLines = 1,
                    modifier = Modifier.weight(1f)
                )
                if (character != null && adaptiveContentState != null) {
                    VerticalDivider()
                    MainEntitiesWidget(
                        entities = character.mainEntities,
                        onClick = {
                            adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.MAIN_ENTITIES)
                        },
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                            .fillMaxHeight()
                    )
                }
            }
        },
        actions = {
            DescriptionIconButton(
                imageVector = Icons.Default.Edit,
                contentDescription = stringResource(Res.string.edit_character),
                onClick = navigateToEditCharacter
            )
        },
        scrollBehavior = scrollBehavior
    )
}

@Composable
private fun Content(
    adaptiveContentState: AdaptiveContentState<CharacterFullUiState.Dialog>,
    navigateToEntityInfo: (DnDEntityMin) -> Unit,
    character: CharacterFull,
    eventSink: (CharacterFullScreenUiEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val diceRollerState = rememberDiceRollerState {  }
    val onAttributeClick: (Attributes) -> Unit = remember(diceRollerState) {
        {
            diceRollerState.roll(
                Dices.D20,
                modifier = ThrowsDiceRollerModifier.AttributesModifier(it)
            )
        }
    }
    val onSavingThrowClick: (Attributes) -> Unit = remember(diceRollerState) {
        {
            diceRollerState.roll(
                Dices.D20,
                modifier = ThrowsDiceRollerModifier.SavingThrowsModifier(it)
            )
        }
    }
    val onSkillClick: (Skills) -> Unit = remember(diceRollerState) {
        {
            diceRollerState.roll(
                Dices.D20,
                modifier = ThrowsDiceRollerModifier.SkillsModifier(it)
            )
        }
    }

    AdaptiveContent(
        modifier = modifier,
        state = adaptiveContentState,
        panesSpacing = 8.dp,
        singlePaneContent = {
            SinglePaneContent(
                character = character,
                adaptiveContentState = adaptiveContentState,
                navigateToEntityInfo = navigateToEntityInfo,
                eventSink = eventSink,
                onAttributeClick = onAttributeClick,
                onSavingThrowClick = onSavingThrowClick,
                onSkillClick = onSkillClick
            )
        },
        twoPaneContent = Pair(
            {
                TwoPaneStartContent(
                    character = character,
                    adaptiveContentState = adaptiveContentState,
                    onAttributeClick = onAttributeClick,
                    onSavingThrowClick = onSavingThrowClick,
                    onSkillClick = onSkillClick
                )
            },
            {
                CharacterPages(
                    modifier = Modifier.fillMaxSize(),
                    character = character,
                    skipAttributes = true,
                    onEntityClick = navigateToEntityInfo,
                    action = eventSink,
                    onAddItemClick = {
                        adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.ADD_ITEM)
                    },
                    onAddSpellClick = {
                        adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.ADD_SPELL)
                    },
                    onAddStateClick = {
                        adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.ADD_STATE)
                    },
                    onAttributeClick = onAttributeClick,
                    onSavingThrowClick = onSavingThrowClick,
                    onSkillClick = onSkillClick
                )
            }
        )
    )

    CharacterThrowsDiceRoller(
        state = diceRollerState,
        characterModifiers = character.calculatedValueModifiers
    )
}

@Composable
private fun CharacterMainValuesColumn(
    character: CharacterFull,
    adaptiveContentState: AdaptiveContentState<CharacterFullUiState.Dialog>,
    modifier: Modifier = Modifier,
    bottomContent: @Composable ColumnScope.() -> Unit
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        CharacterMainValuesWidget(
            values = character.appliedValues,
            states = character.states,
            onInitiativeClick = {},   // TODO
            onArmorClassClick = {},   // TODO
            onHealthClick = { adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.HEALTH) },
            onSpeedClick = {},        // TODO
            onAddStateClick = { adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.ADD_STATE) },
            modifier = Modifier.fillMaxWidth()
        )
        bottomContent()
    }
}

@Composable
private fun SinglePaneContent(
    character: CharacterFull,
    adaptiveContentState: AdaptiveContentState<CharacterFullUiState.Dialog>,
    navigateToEntityInfo: (DnDEntityMin) -> Unit,
    eventSink: (CharacterFullScreenUiEvent) -> Unit,
    onAttributeClick: (Attributes) -> Unit,
    onSavingThrowClick: (Attributes) -> Unit,
    onSkillClick: (Skills) -> Unit,
) {
    CharacterMainValuesColumn(
        character = character,
        adaptiveContentState = adaptiveContentState,
        modifier = Modifier.fillMaxSize()
    ) {
        CharacterPages(
            modifier = Modifier.weight(1f),
            character = character,
            skipAttributes = false,
            onEntityClick = navigateToEntityInfo,
            action = eventSink,
            onAddItemClick = { adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.ADD_ITEM) },
            onAddSpellClick = { adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.ADD_SPELL) },
            onAddStateClick = { adaptiveContentState.toggleContent(CharacterFullUiState.Dialog.ADD_STATE) },
            onAttributeClick = onAttributeClick,
            onSavingThrowClick = onSavingThrowClick,
            onSkillClick = onSkillClick
        )
    }
}

@Composable
private fun TwoPaneStartContent(
    character: CharacterFull,
    adaptiveContentState: AdaptiveContentState<CharacterFullUiState.Dialog>,
    onAttributeClick: (Attributes) -> Unit,
    onSavingThrowClick: (Attributes) -> Unit,
    onSkillClick: (Skills) -> Unit,
) {
    CharacterMainValuesColumn(
        character = character,
        adaptiveContentState = adaptiveContentState,
        modifier = Modifier.fillMaxSize()
    ) {
        CharacterFullAttributesScreen(
            modifier = Modifier.weight(1f),
            calculationsResult = character.calculatedModifiersResult,
            onAttributeClick = onAttributeClick,
            onSavingThrowClick = onSavingThrowClick,
            onSkillClick = onSkillClick
        )
    }
}

// ---- CharacterPages.kt ----

@Composable
private fun CharacterFullTabsRow(
    pagerState: PagerState,
    pages: List<CharacterFullUiState.Page>
) {
    val scope = rememberCoroutineScope()
    PrimaryScrollableTabRow(selectedTabIndex = pagerState.currentPage) {
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
    action: (CharacterFullScreenUiEvent) -> Unit,
    onAddItemClick: () -> Unit,
    onAddSpellClick: () -> Unit,
    onAddStateClick: () -> Unit,
    onAttributeClick: (Attributes) -> Unit,
    onSavingThrowClick: (Attributes) -> Unit,
    onSkillClick: (Skills) -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = remember(skipAttributes) {
        if (skipAttributes) CharacterFullUiState.Page.entries.drop(1)
        else CharacterFullUiState.Page.entries
    }
    val pagerState = rememberPagerState { pages.size }

    Column(modifier = modifier) {
        CharacterFullTabsRow(pagerState, pages)
        Spacer(Modifier.height(8.dp))
        HorizontalPager(
            modifier = Modifier.weight(1f),
            state = pagerState,
            pageSpacing = 8.dp,
            contentPadding = PaddingValues(horizontal = 8.dp)
        ) { index ->
            CharacterPage(
                page = pages[index],
                character = character,
                onEntityClick = onEntityClick,
                action = action,
                onAddItemClick = onAddItemClick,
                onAddSpellClick = onAddSpellClick,
                onAddStateClick = onAddStateClick,
                onAttributeClick = onAttributeClick,
                onSavingThrowClick = onSavingThrowClick,
                onSkillClick = onSkillClick
            )
        }
    }
}

// Extracted from the when-block inside HorizontalPager to reduce lambda size
// and allow Compose to skip recomposition of unchanged pages independently
@Composable
private fun CharacterPage(
    page: CharacterFullUiState.Page,
    character: CharacterFull,
    onEntityClick: (DnDEntityMin) -> Unit,
    action: (CharacterFullScreenUiEvent) -> Unit,
    onAddItemClick: () -> Unit,
    onAddSpellClick: () -> Unit,
    onAddStateClick: () -> Unit,
    onAttributeClick: (Attributes) -> Unit,
    onSavingThrowClick: (Attributes) -> Unit,
    onSkillClick: (Skills) -> Unit,
) {
    when (page) {
        CharacterFullUiState.Page.ATTRIBUTES -> CharacterFullAttributesScreen(
            calculationsResult = character.calculatedModifiersResult,
            modifier = Modifier.fillMaxSize(),
            onAttributeClick = onAttributeClick,
            onSavingThrowClick = onSavingThrowClick,
            onSkillClick = onSkillClick
        )

        CharacterFullUiState.Page.ATTACKS -> CharacterAttacksScreen(
            attacks = character.attacks,
            modifier = Modifier.fillMaxSize()
        )

        CharacterFullUiState.Page.ITEMS -> CharacterItemsScreen(
            characterCoins = character.coins,
            items = character.items,
            usedActivations = character.usedItemActivations,
            onOpenInfo = onEntityClick,
            onUpdateCharacterItem = { action(CharacterFullScreenUiEvent.UpdateCharacterItem(it)) },
            onActivateItem = { item, activation ->
                action(CharacterFullScreenUiEvent.ActivateCharacterItem(item, activation))
            },
            onAddCharacterItemClick = onAddItemClick,
            modifier = Modifier.fillMaxSize()
        )

        CharacterFullUiState.Page.SPELLS -> CharacterSpellsScreen(
            spells = character.spells,
            spellCastingValues = character.getSpellCastingValues(),
            availableSpellSlots = character.spellSlots,
            usedSpells = character.usedSpells,
            onSpellClick = { onEntityClick(it.toDnDEntityMin()) },
            setUsedSpellsCount = { typeId, lvl, count ->
                action(CharacterFullScreenUiEvent.SetUsedSpellsCount(typeId, lvl, count))
            },
            onAddSpellClick = onAddSpellClick,
            modifier = Modifier.fillMaxSize()
        )

        CharacterFullUiState.Page.SPELL_SLOTS -> CharacterSpellSlotsScreen(
            availableSpellSlots = character.spellSlots,
            usedSpells = character.usedSpells,
            setUsedSpellsCount = { typeId, lvl, count ->
                action(CharacterFullScreenUiEvent.SetUsedSpellsCount(typeId, lvl, count))
            },
            modifier = Modifier.fillMaxSize()
        )

        CharacterFullUiState.Page.STATES -> CharacterStatesScreen(
            states = character.states,
            onClick = { onEntityClick(it.toDnDEntityMin()) },
            onAddStateClick = onAddStateClick,
            onDeleteStateClick = {
                action(CharacterFullScreenUiEvent.DeleteCharacterState(it.toCharacterStateLink()))
            },
            modifier = Modifier.fillMaxSize()
        )

        CharacterFullUiState.Page.NOTES -> CharacterNotesScreen(
            notes = character.notes,
            onUpdateOrNewNote = { action(CharacterFullScreenUiEvent.UpdateOrNewNote(it)) },
            onDeleteNote = { action(CharacterFullScreenUiEvent.DeleteNote(it)) },
            modifier = Modifier.fillMaxSize()
        )
    }
}

// ---- CharacterAdaptiveContent.kt ----

@Composable
private fun rememberAdaptiveCharacterContentState(
    character: CharacterFull,
    eventSink: (CharacterFullScreenUiEvent) -> Unit,
    navigateToEntityInfo: (DnDEntityMin) -> Unit
) = rememberAdaptiveContentState<CharacterFullUiState.Dialog>(useWindows = false) { entry ->
    when (entry) {
        CharacterFullUiState.Dialog.HEALTH -> SupportEntry(
            titleGetter = { stringResource(entry.titleStringRes) }
        ) {
            CharacterHealthDialogContent(
                baseHealth = character.health,
                updateHealth = { eventSink(CharacterFullScreenUiEvent.SetHealth(it)) },
                healthModifiers = character.calculatedValueModifiers[ModifierValueTarget.HEALTH].orEmpty()
            )
        }

        CharacterFullUiState.Dialog.MAIN_ENTITIES -> SupportEntry(
            titleGetter = { stringResource(entry.titleStringRes) }
        ) {
            CharacterMainEntitiesDialog()
        }

        CharacterFullUiState.Dialog.ADD_ITEM -> searchSupportEntry(entry) {
            SearchEntityScaffold(
                entityType = DnDEntityTypes.ITEM,
                onEntityClick = { eventSink(CharacterFullScreenUiEvent.AddItem(it.resolvedEntity)) },
                onEntityInfoClick = navigateToEntityInfo,
                modifier = Modifier.fillMaxSize()
            )
        }

        CharacterFullUiState.Dialog.ADD_STATE -> searchSupportEntry(entry) {
            SearchEntityScaffold(
                entityType = DnDEntityTypes.STATE,
                onEntityClick = { eventSink(CharacterFullScreenUiEvent.AddState(it.resolvedEntity)) },
                onEntityInfoClick = navigateToEntityInfo,
                modifier = Modifier.fillMaxSize()
            )
        }

        CharacterFullUiState.Dialog.ADD_SPELL -> searchSupportEntry(entry) {
            SearchEntityScaffold(
                entityType = DnDEntityTypes.SPELL,
                onEntityClick = { eventSink(CharacterFullScreenUiEvent.AddSpell(it.resolvedEntity)) },
                onEntityInfoClick = navigateToEntityInfo,
                modifier = Modifier.fillMaxSize()
            )
        }

        CharacterFullUiState.Dialog.NONE -> null
    }
}

// Deduplicates the three identical ADD_* SupportEntry wrappers
private fun searchSupportEntry(
    entry: CharacterFullUiState.Dialog,
    content: @Composable () -> Unit
) = SupportEntry(titleGetter = { stringResource(entry.titleStringRes) }, content = content)


// Extension to reduce repeated child-or-parent entity resolution pattern
private val SearchEntityResult.resolvedEntity: DnDEntityMin
    get() = childEntity ?: parentEntity.toDnDEntityMin()