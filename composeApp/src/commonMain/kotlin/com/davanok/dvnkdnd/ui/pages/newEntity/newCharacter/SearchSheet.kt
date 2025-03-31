package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMedium
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.confirm
import dvnkdnd.composeapp.generated.resources.search_in_web
import dvnkdnd.composeapp.generated.resources.show_info_about
import org.jetbrains.compose.resources.stringResource
import kotlin.collections.component1
import kotlin.collections.component2

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchSheet(
    onDismiss: (DnDEntityMin?, DnDEntityMin?) -> Unit,
    onGetEntityInfo: (DnDEntityTypes, DnDEntityMin) -> Unit,
    viewModel: NewCharacterViewModel
) {
    val uiState by viewModel.searchSheetState.collectAsStateWithLifecycle()
    var selectedEntity by remember { mutableStateOf(Pair<DnDEntityMedium?, DnDEntityMin?>(null, null)) }
    AdaptiveModalSheet(
        onDismissRequest = { onDismiss(null, null) },
        title = {
            Text(text = stringResource(Res.string.search_in_web))
        }
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = uiState.query,
            onValueChange = viewModel::setSearchQuery
        )
        if (uiState.isLoading) CircularProgressIndicator()
        else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth()
            ) {
                uiState.entitiesGroups.forEach { (groupName, entities) ->
                    stickyHeader {
                        Text(
                            text = groupName,
                            style = MaterialTheme.typography.titleMedium
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
                                    if (entity.subEntities.isEmpty()) selectedEntity = Pair(entity, null)
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
                                        when (uiState.searchType) {
                                            SearchSheetContent.CLASS -> DnDEntityTypes.CLASS
                                            SearchSheetContent.RACE -> DnDEntityTypes.RACE
                                            SearchSheetContent.BACKGROUND -> DnDEntityTypes.BACKGROUND
                                            else -> DnDEntityTypes.NONE
                                        },
                                        entity.asDnDEntityMin()
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
                                            SuggestionChip(
                                                onClick = { selectedEntity = Pair(entity, subEntity) },
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
                    onClick = { onDismiss(selectedEntity.first!!.asDnDEntityMin(), selectedEntity.second) },
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