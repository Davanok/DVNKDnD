package com.davanok.dvnkdnd.ui.pages.editCharacter

import androidx.compose.animation.AnimatedContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.PrimaryScrollableTabRow
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.adaptive.layout.AnimatedPane
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffold
import androidx.compose.material3.adaptive.layout.ListDetailPaneScaffoldRole
import androidx.compose.material3.adaptive.navigation.rememberListDetailPaneScaffoldNavigator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.window.core.layout.WindowSizeClass
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.model.isCritical
import com.davanok.dvnkdnd.ui.pages.editCharacter.pages.EditCharacterAttributesPage
import com.davanok.dvnkdnd.ui.pages.editCharacter.pages.EditCharacterHealthPage
import com.davanok.dvnkdnd.ui.pages.editCharacter.pages.EditCharacterMainPage
import com.davanok.dvnkdnd.ui.pages.editCharacter.pages.EditCharacterModifiersPage
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.no_such_character_error
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditCharacterScreen(
    navigateBack: () -> Unit,
    navigateToEntityInfo: (DnDEntityMin) -> Unit,
    viewModel: EditCharacterViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UiToaster(
        messages = uiState.messages,
        onRemoveMessage = viewModel::removeMessage
    )

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
                    onBack = navigateBack,
                    navigateToEntityInfo = navigateToEntityInfo,
                    character = character,
                    currentPage = uiState.currentPage,
                    onPageChanged = viewModel::setPage,
                    eventSink = viewModel::eventSink,
                    modifier = Modifier.fillMaxSize()
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    onBack: () -> Unit,
    navigateToEntityInfo: (DnDEntityMin) -> Unit,
    character: CharacterFull,
    currentPage: EditCharacterUiState.Page,
    onPageChanged: (EditCharacterUiState.Page) -> Unit,
    eventSink: (EditCharacterScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val expandedView by remember(windowSizeClass) {
        derivedStateOf {
            windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)
        }
    }

    Scaffold(
        modifier = modifier,
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                title = { Text(text = character.character.name) }
            )
        }
    ) { paddingValues ->
        ContentNavigationWrapper(
            currentPage = currentPage,
            onPageChanged = onPageChanged,
            expandedView = expandedView,
            modifier = Modifier.padding(paddingValues)
        ) { page ->
            val modifier = Modifier.fillMaxSize()
            when (page) {
                EditCharacterUiState.Page.MAIN -> EditCharacterMainPage(
                    character = character,
                    eventSink = eventSink,
                    modifier = modifier
                )
                EditCharacterUiState.Page.ATTRIBUTES -> EditCharacterAttributesPage(
                    character = character,
                    eventSink = eventSink,
                    modifier = modifier
                )
                EditCharacterUiState.Page.MODIFIERS -> EditCharacterModifiersPage(
                    character = character,
                    eventSink = eventSink,
                    modifier = modifier
                )
                EditCharacterUiState.Page.HEALTH -> EditCharacterHealthPage(
                    character = character,
                    eventSink = eventSink,
                    modifier = modifier
                )
            }
        }
    }
}

@Composable
private fun ContentNavigationWrapper(
    currentPage: EditCharacterUiState.Page,
    onPageChanged: (EditCharacterUiState.Page) -> Unit,
    expandedView: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable (page: EditCharacterUiState.Page) -> Unit
) {
    AnimatedContent(expandedView) { expandedView ->
        if (expandedView) {
            ExpandedContent(
                currentPage = currentPage,
                onPageChanged = onPageChanged,
                modifier = modifier,
                content = content
            )
        } else {
            CompactContent(
                currentPage = currentPage,
                onPageChanged = onPageChanged,
                modifier = modifier,
                content = content
            )
        }
    }
}

@Composable
private fun CompactContent(
    currentPage: EditCharacterUiState.Page,
    onPageChanged: (EditCharacterUiState.Page) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (page: EditCharacterUiState.Page) -> Unit
) {
    val pages = EditCharacterUiState.Page.entries

    val pagerState = rememberPagerState(pages.indexOf(currentPage)) { pages.size }
    val currentPageIndex = pagerState.currentPage

    LaunchedEffect(pagerState.currentPage) {
        val newPage = pages[pagerState.currentPage]
        if (newPage != currentPage) {
            onPageChanged(newPage)
        }
    }

    val coroutineScope = rememberCoroutineScope()

    Column(modifier = modifier) {
        PrimaryScrollableTabRow(selectedTabIndex = currentPageIndex) {
            pages.forEachIndexed { index, page ->
                Tab(
                    selected = index == currentPageIndex,
                    onClick = { coroutineScope.launch { pagerState.animateScrollToPage(index) } },
                    text = { Text(text = stringResource(page.stringRes)) }
                )
            }
        }

        HorizontalPager(
            state = pagerState,
            modifier = Modifier.weight(1f)
        ) { index ->
            content(pages[index])
        }
    }
}

@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
private fun ExpandedContent(
    currentPage: EditCharacterUiState.Page,
    onPageChanged: (EditCharacterUiState.Page) -> Unit,
    modifier: Modifier = Modifier,
    content: @Composable (page: EditCharacterUiState.Page) -> Unit
) {
    val navigator = rememberListDetailPaneScaffoldNavigator()

    LaunchedEffect(currentPage) {
        navigator.navigateTo(ListDetailPaneScaffoldRole.Detail, currentPage)
    }

    ListDetailPaneScaffold(
        modifier = modifier,
        directive = navigator.scaffoldDirective,
        scaffoldState = navigator.scaffoldState,
        listPane = {
            MenuListContent(
                currentPage = currentPage,
                onClick = onPageChanged
            )
        },
        detailPane = {
            AnimatedPane {
                (navigator.currentDestination?.contentKey as? EditCharacterUiState.Page)?.let {
                    content(it)
                }
            }
        }
    )
}
@Composable
private fun MenuListContent(
    currentPage: EditCharacterUiState.Page,
    onClick: (EditCharacterUiState.Page) -> Unit
) {
    Column(
        modifier = Modifier.verticalScroll(rememberScrollState())
    ) {
        EditCharacterUiState.Page.entries.forEach { page ->
            NavigationDrawerItem(
                label = { Text(text = stringResource(page.stringRes)) },
                selected = page == currentPage,
                onClick = { onClick(page) }
            )
        }
    }
}