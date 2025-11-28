package com.davanok.dvnkdnd.ui.pages.characterFull

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
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
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEachIndexed
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.dndEnums.DnDModifierTargetType
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.CharacterHealth
import com.davanok.dvnkdnd.data.model.entities.character.CharacterNote
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.ui.isCritical
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveContent
import com.davanok.dvnkdnd.ui.components.adaptive.SupportEntry
import com.davanok.dvnkdnd.ui.components.adaptive.rememberAdaptiveContentState
import com.davanok.dvnkdnd.ui.components.customToolBar.CollapsingTitle
import com.davanok.dvnkdnd.ui.components.customToolBar.CustomTopAppBar
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.CharacterAttacksScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.CharacterFullAttributesScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.CharacterHealthDialogContent
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.CharacterItemsScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.CharacterMainValuesWidget
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.CharacterNotesScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.CharacterSpellSlotsScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.CharacterSpellsScreen
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.MainEntitiesWidget
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.no_such_character_error
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

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
                    updateHealth = viewModel::updateHealth,
                    onUpdateOrNewNote = viewModel::updateOrNewNote,
                    onDeleteNote = viewModel::deleteNote,
                    setUsedSpellsCount = viewModel::setUsedSpellsCount
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
    updateHealth: (CharacterHealth) -> Unit,
    onUpdateOrNewNote: (CharacterNote) -> Unit,
    onDeleteNote: (CharacterNote) -> Unit,
    setUsedSpellsCount: (typeId: Uuid?, lvl: Int, count: Int) -> Unit,
) {
    val adaptiveContentState = rememberAdaptiveContentState<CharacterFullUiState.Dialog> { entry ->
        when (entry) {
            CharacterFullUiState.Dialog.HEALTH -> SupportEntry(
                titleGetter = { stringResource(entry.titleStringRes) },
                content = {
                    CharacterHealthDialogContent(
                        baseHealth = character.health,
                        updateHealth = updateHealth,
                        healthModifiers = character.appliedModifiers
                            .getOrElse(DnDModifierTargetType.HEALTH, ::emptyList)
                    )
                }
            )
            CharacterFullUiState.Dialog.NONE -> null
        }
    }
    Scaffold(
        topBar = {
            CustomTopAppBar(
                navigationIcon = {
                    IconButton(onClick = navigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                collapsingTitle = CollapsingTitle.medium(character.character.name)
            )
        }
    ) { paddingValues ->
        Column(modifier = Modifier.padding(paddingValues)) {
            Row(
                modifier = Modifier
                    .padding(horizontal = 8.dp)
                    .fillMaxWidth()
                    .height(IntrinsicSize.Max),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                MainEntitiesWidget(
                    entities = character.mainEntities,
                    onClick = { TODO() },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
                CharacterMainValuesWidget(
                    values = character.appliedValues,
                    onInitiativeClick = { TODO() },
                    onArmorClassClick = { TODO() },
                    onHealthClick = { adaptiveContentState.showContent(CharacterFullUiState.Dialog.HEALTH) },
                    modifier = Modifier.weight(1f).fillMaxHeight()
                )
            }
            Spacer(Modifier.height(8.dp))
            AdaptiveContent(
                modifier = Modifier.weight(1f),
                state = adaptiveContentState,
                singlePaneContent = {
                    CharacterPages(
                        character = character,
                        skipAttributes = false,
                        onEntityClick = navigateToEntityInfo,
                        onUpdateOrNewNote = onUpdateOrNewNote,
                        onDeleteNote = onDeleteNote,
                        setUsedSpellsCount = setUsedSpellsCount
                    )
                },
                twoPaneContent = Pair(
                    {
                        CharacterFullAttributesScreen(
                            attributes = character.appliedValues.savingThrowModifiers,
                            savingThrows = character.appliedValues.savingThrowModifiers,
                            skills = character.appliedValues.skillModifiers
                        )
                    },
                    {
                        CharacterPages(
                            character = character,
                            skipAttributes = true,
                            onEntityClick = navigateToEntityInfo,
                            onUpdateOrNewNote = onUpdateOrNewNote,
                            onDeleteNote = onDeleteNote,
                            setUsedSpellsCount = setUsedSpellsCount
                        )
                    }
                )
            )
        }
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
    onUpdateOrNewNote: (CharacterNote) -> Unit,
    onDeleteNote: (CharacterNote) -> Unit,
    setUsedSpellsCount: (typeId: Uuid?, lvl: Int, count: Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val pages = remember(skipAttributes) {
        if (skipAttributes) CharacterFullUiState.Page.entries.drop(1)
        else CharacterFullUiState.Page.entries
    }

    Column {
        val pagerState = rememberPagerState { pages.size }
        CharacterFullTabsRow(pagerState, pages)
        HorizontalPager(
            modifier = modifier,
            state = pagerState
        ) { index ->
            val page = pages[index]
            when (page) {
                CharacterFullUiState.Page.ATTRIBUTES -> CharacterFullAttributesScreen(
                    attributes = character.appliedValues.savingThrowModifiers,
                    savingThrows = character.appliedValues.savingThrowModifiers,
                    skills = character.appliedValues.skillModifiers
                )
                CharacterFullUiState.Page.ATTACKS -> CharacterAttacksScreen(
                    items = character.items,
                    onClick = { onEntityClick(it.toDnDEntityMin()) }
                )
                CharacterFullUiState.Page.ITEMS -> CharacterItemsScreen(
                    items = character.items,
                    onClick = { onEntityClick(it.toDnDEntityMin()) }
                )
                CharacterFullUiState.Page.SPELLS -> CharacterSpellsScreen(
                    spells = character.spells,
                    availableSpellSlots = character.spellSlots,
                    usedSpells = character.usedSpells,
                    onSpellClick = { onEntityClick(it.toDnDEntityMin()) },
                    setUsedSpellsCount = setUsedSpellsCount
                )
                CharacterFullUiState.Page.SPELL_SLOTS -> CharacterSpellSlotsScreen(

                )
                CharacterFullUiState.Page.NOTES -> CharacterNotesScreen(
                    notes = character.notes,
                    onUpdateOrNewNote = onUpdateOrNewNote,
                    onDeleteNote = onDeleteNote,
                    modifier = Modifier.fillMaxSize()
                )
            }
        }
    }
}
