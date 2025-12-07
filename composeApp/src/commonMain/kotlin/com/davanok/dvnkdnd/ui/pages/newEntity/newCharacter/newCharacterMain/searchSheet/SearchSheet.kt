package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain.searchSheet

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.ui.model.UiError
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.sideSheet.ModalSideSheet
import com.davanok.dvnkdnd.ui.components.sideSheet.rememberModalSideSheetState
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.close_side_sheet
import dvnkdnd.composeapp.generated.resources.confirm
import dvnkdnd.composeapp.generated.resources.refresh
import dvnkdnd.composeapp.generated.resources.search_in_web
import dvnkdnd.composeapp.generated.resources.show_info_about
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.mapLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchSheet(
    onDismiss: (DnDEntityWithSubEntities?, DnDEntityMin?) -> Unit,
    onGetEntityInfo: (DnDEntityMin) -> Unit,
    viewModel: SearchSheetViewModel
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var selectedEntity: Pair<DnDEntityWithSubEntities, DnDEntityMin?>? by remember { mutableStateOf(null) }
    val sheetState = rememberModalSideSheetState()
    val scope = rememberCoroutineScope()

    val dismiss: (apply: Boolean) -> Unit = { apply ->
        scope.launch { sheetState.hide() }
            .invokeOnCompletion {
                if(apply) onDismiss(selectedEntity?.first, selectedEntity?.second)
                else onDismiss(null, null)
            }
    }

    ModalSideSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss(null, null) },
    ) {
        TopAppBar(
            actions = {
                IconButton(
                    onClick = { dismiss(false) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = stringResource(Res.string.close_side_sheet)
                    )
                }
            },
            title = {
                Text(text = stringResource(Res.string.search_in_web))
            }
        )
        Row {
            OutlinedTextField(
                modifier = Modifier.weight(1f),
                value = viewModel.query.collectAsStateWithLifecycle().value,
                onValueChange = viewModel::setSearchQuery
            )
            IconButton (
                onClick = viewModel::refreshCurrent
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(Res.string.refresh)
                )
            }
        }
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = viewModel::refreshCurrent
        ) {
            Column (modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(10.dp))
                ListContent(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    entitiesGroups = uiState.entitiesGroups,
                    selectedEntity = selectedEntity,
                    onSelectEntity = { ent, sub ->
                        val pair = Pair(ent, sub)
                        if (selectedEntity == pair) dismiss(true)
                        else selectedEntity = pair
                                     },
                    onInfoClick = onGetEntityInfo,
                    onLoadNextPage = viewModel::loadNextIfAvailable,
                    isLoading = uiState.isLoading,
                    error = uiState.error
                )
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Row (
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(vertical = 24.dp)
                ) {
                    Button(
                        onClick = { dismiss(true) },
                        enabled = selectedEntity?.let { (entity, subEntity) ->
                            if (entity.subEntities.isEmpty()) true
                            else subEntity != null
                        } ?: false
                    ) {
                        Text(text = stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalCoroutinesApi::class)
@Composable
private fun ListContent(
    entitiesGroups: Map<String, List<DnDEntityWithSubEntities>>,
    selectedEntity: Pair<DnDEntityWithSubEntities, DnDEntityMin?>?,
    onSelectEntity: (DnDEntityWithSubEntities, DnDEntityMin?) -> Unit,
    onInfoClick: (DnDEntityMin) -> Unit,
    onLoadNextPage: () -> Unit,
    isLoading: Boolean,
    error: UiError?,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()
    LazyColumn(
        state = listState,
        modifier = modifier
    ) {
        entitiesGroups.forEach { (source, entities) ->
            stickyHeader(
                key = "$source header"
            ) {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = MaterialTheme.colorScheme.surfaceContainer
                ) {
                    Text(
                        text = source,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            items(
                items = entities,
                key = { it.id }
            ) { entity ->
                ListElement(
                    entity = entity,
                    selectedEntity = selectedEntity,
                    onSelect = onSelectEntity,
                    onInfoClick = onInfoClick
                )
            }
            if (isLoading) {
                item(
                    key = "loading indicator"
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
            }
            error?.let {
                item (
                    key = "error card"
                ) {
                    ErrorCard(
                        text = it.message,
                        exception = it.exception,
                        onRefresh = onLoadNextPage
                    )
                }
            }
        }
    }
    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .mapLatest { lastVisibleIndex ->
                val total = listState.layoutInfo.totalItemsCount
                if (lastVisibleIndex != null && lastVisibleIndex >= total - 3 && !isLoading) {
                    onLoadNextPage()
                }
            }
            .collect()
    }
}
@Composable
private fun ListElement(
    entity: DnDEntityWithSubEntities,
    selectedEntity: Pair<DnDEntityWithSubEntities, DnDEntityMin?>?,
    onSelect: (DnDEntityWithSubEntities, DnDEntityMin?) -> Unit,
    onInfoClick: (DnDEntityMin) -> Unit
) {
    var showSubEntitiesList by remember { mutableStateOf(false) }
    ListItem(
        modifier = Modifier
            .clickable {
                if (entity.subEntities.isEmpty()) onSelect(entity, null)
                else showSubEntitiesList = !showSubEntitiesList
            }
            .fillMaxWidth(),
        headlineContent = {
            Text(
                text = entity.name
            )
        },
        trailingContent = {
            IconButton (
                onClick = { onInfoClick(entity.toDnDEntityMin()) }
            ) {
                Icon(
                    imageVector = Icons.Default.Info,
                    contentDescription = stringResource(
                        Res.string.show_info_about,
                        entity.name
                    )
                )
            }
        },
        supportingContent = {
            AnimatedVisibility(
                visible = showSubEntitiesList,
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut()
            ) {
                Row(
                    modifier = Modifier
                        .horizontalScroll(rememberScrollState())
                ) {
                    entity.subEntities.forEach { subEntity ->
                        InputChip(
                            selected = selectedEntity?.second == subEntity,
                            onClick = { onSelect(entity, subEntity) },
                            label = { Text(text = subEntity.name) }
                        )
                    }
                }
            }
        }
    )
}