package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.ui.components.sideSheet.ModalSideSheet
import com.davanok.dvnkdnd.ui.components.sideSheet.rememberModalSideSheetState
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.close_side_sheet
import dvnkdnd.composeapp.generated.resources.confirm
import dvnkdnd.composeapp.generated.resources.refresh
import dvnkdnd.composeapp.generated.resources.search_in_web
import dvnkdnd.composeapp.generated.resources.show_info_about
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun SearchSheet(
    onDismiss: (DnDEntityWithSubEntities?, DnDEntityMin?) -> Unit,
    onGetEntityInfo: (DnDEntityMin) -> Unit,
    viewModel: NewCharacterMainViewModel
) {
    val uiState by viewModel.searchSheetState.collectAsStateWithLifecycle()
    var selectedEntity: Pair<DnDEntityWithSubEntities?, DnDEntityMin?> by remember { mutableStateOf(Pair(null, null)) }
    val onDismissPair = {
        val (first, second) = selectedEntity
        onDismiss(first, second)
    }
    val sheetState = rememberModalSideSheetState()
    val scope = rememberCoroutineScope()
    ModalSideSheet(
        sheetState = sheetState,
        onDismissRequest = { onDismiss(null, null) },
    ) {
        TopAppBar(
            actions = {
                IconButton(
                    onClick = { scope.launch { sheetState.hide() }.invokeOnCompletion { onDismiss(null, null) } }
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
                value = uiState.query,
                onValueChange = viewModel::setSearchQuery
            )
            IconButton (
                onClick = { viewModel.loadSearchEntities(uiState.searchType) }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = stringResource(Res.string.refresh)
                )
            }
        }
        PullToRefreshBox(
            isRefreshing = uiState.isLoading,
            onRefresh = { viewModel.loadSearchEntities(uiState.searchType) }
        ) {
            Column (modifier = Modifier.fillMaxSize()) {
                Spacer(modifier = Modifier.height(10.dp))
                LazyColumn(
                    modifier = Modifier.fillMaxWidth().weight(1f)
                ) {
                    uiState.entitiesGroups.forEach { (groupName, entities) ->
                        stickyHeader {
                            Text(
                                text = groupName,
                                style = MaterialTheme.typography.titleLarge
                            )
                        }
                        items(
                            items = entities,
                            key = { it.id }
                        ) { entity ->
                            var showSubEntitiesList by remember { mutableStateOf(false) }
                            ListItem(
                                modifier = Modifier
                                    .clickable {
                                        if (entity.subEntities.isEmpty()) {
                                            if (selectedEntity != Pair(entity, null))
                                                selectedEntity = Pair(entity, null)
                                            else onDismissPair()
                                        }
                                        else showSubEntitiesList = !showSubEntitiesList
                                    }
                                    .fillMaxWidth(),
                                headlineContent = {
                                    Text(
                                        modifier = Modifier.weight(1f),
                                        text = entity.name
                                    )
                                },
                                trailingContent = {
                                    IconButton (
                                        onClick = { onGetEntityInfo(
                                            entity.toDnDEntityMin()
                                        ) }
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
                                supportingContent = if (showSubEntitiesList) {
                                    {
                                        Row(
                                            modifier = Modifier
                                                .horizontalScroll(rememberScrollState())
                                        ) {
                                            entity.subEntities.forEach { subEntity ->
                                                InputChip(
                                                    selected = selectedEntity.second == subEntity,
                                                    onClick = {
                                                        if (selectedEntity.second != subEntity)
                                                            selectedEntity = Pair(entity, subEntity)
                                                        else onDismissPair()
                                                              },
                                                    label = {
                                                        Text(text = subEntity.name)
                                                    }
                                                )
                                            }
                                        }
                                    }
                                } else null
                            )
                        }
                    }
                }
                HorizontalDivider(modifier = Modifier.fillMaxWidth())
                Row (
                    modifier = Modifier
                        .align(Alignment.Start)
                        .padding(vertical = 24.dp)
                ) {
                    Button(
                        onClick = onDismissPair,
                        enabled = selectedEntity.let { (entity, subEntity) ->
                            if (entity == null) false
                            else if (entity.subEntities.isEmpty()) true
                            else subEntity != null
                        }
                    ) {
                        Text(text = stringResource(Res.string.confirm))
                    }
                }
            }
        }
    }
}