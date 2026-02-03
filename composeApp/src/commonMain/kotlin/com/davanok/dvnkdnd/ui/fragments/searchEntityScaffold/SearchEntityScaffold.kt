package com.davanok.dvnkdnd.ui.fragments.searchEntityScaffold

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.ui.components.BaseEntityImage
import com.davanok.dvnkdnd.ui.components.PagedLazyColumn
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.hide_description
import dvnkdnd.composeapp.generated.resources.search_entity_type
import dvnkdnd.composeapp.generated.resources.show_description
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

data class SearchEntityResult(
    val parentEntity: DnDEntityWithSubEntities,
    val childEntity: DnDEntityMin?
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchEntityScaffold(
    entityType: DnDEntityTypes,
    onEntityClick: (SearchEntityResult) -> Unit,
    onEntityInfoClick: (DnDEntityMin) -> Unit,
    viewModel: SearchEntityViewModel = assistedMetroViewModel<SearchEntityViewModel, SearchEntityViewModel.Factory>(key = entityType.name) { create(entityType) },
    modifier: Modifier = Modifier
) {
    val entities = viewModel.entitiesFlow.collectAsLazyPagingItems()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    var expandedCard: Uuid? by remember { mutableStateOf(null) }
    var searchBarExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
    ) {
        SearchBar(
            modifier = Modifier.align(Alignment.CenterHorizontally),
            inputField = {
                SearchBarDefaults.InputField(
                    query = query,
                    onQueryChange = viewModel::setSearchQuery,
                    onSearch = {  },
                    expanded = searchBarExpanded,
                    onExpandedChange = { searchBarExpanded = it },
                    placeholder = {
                        Text(
                            text = stringResource(
                                Res.string.search_entity_type,
                                stringResource(entityType.stringRes)
                            )
                        )
                    }
                )
            },
            state = rememberSearchBarState()
        )
        Spacer(Modifier.height(8.dp))
        EntitiesColumn(
            entities = entities,
            expandedCard = expandedCard,
            onExpandClick = { entityId ->
                expandedCard = entityId.takeIf { it != expandedCard }
            },
            onEntityInfoClick = onEntityInfoClick,
            onEntityClick = onEntityClick
        )
    }
}

@Composable
private fun EntitiesColumn(
    entities: LazyPagingItems<DnDEntityWithSubEntities>,
    expandedCard: Uuid?,
    onExpandClick: (entityId: Uuid) -> Unit,
    onEntityInfoClick: (DnDEntityMin) -> Unit,
    onEntityClick: (SearchEntityResult) -> Unit,
) {
    PagedLazyColumn(entities = entities) {
        items(
            count = entities.itemCount,
            key = { entities[it]?.id ?: Uuid.random() }
        ) { index ->
            entities[index]?.let { entity ->
                EntityCard(
                    entity = entity,
                    expanded = entity.id == expandedCard,
                    onExpandClick = { onExpandClick(entity.id) },
                    onInfoClick = onEntityInfoClick,
                    onParentClick = { onEntityClick(SearchEntityResult(entity, null)) },
                    onChildClick = { onEntityClick(SearchEntityResult(entity, it)) },
                    modifier = Modifier.fillMaxWidth()
                )
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun EntityCard(
    entity: DnDEntityWithSubEntities,
    expanded: Boolean,
    onExpandClick: () -> Unit,
    onInfoClick: (DnDEntityMin) -> Unit,
    onParentClick: () -> Unit,
    onChildClick: (DnDEntityMin) -> Unit,
    modifier: Modifier = Modifier
) {
    val entityMin = entity.toDnDEntityMin()
    Column(
        modifier = modifier.clickable(onClick = onParentClick)
    ) {
        Column(
            modifier = Modifier.padding(vertical = 8.dp, horizontal = 12.dp)
        ) {
            Row {
                BaseEntityImage(
                    entity = entityMin,
                    onClick = { onInfoClick(entityMin) },
                    modifier = Modifier
                        .size(56.dp)
                )

                Spacer(Modifier.width(8.dp))

                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = entity.name,
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = entity.source,
                        style = MaterialTheme.typography.bodySmall
                    )
                }

                IconButton(
                    onClick = onExpandClick
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription =
                            if (expanded) stringResource(Res.string.hide_description)
                            else stringResource(Res.string.show_description),
                        modifier = Modifier.rotate(if (expanded) 180f else 0f)
                    )
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = entity.description,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
            if (entity.subEntities.isNotEmpty()) {
                Spacer(Modifier.height(8.dp))
                LazyRow {
                    items(
                        items = entity.subEntities,
                        key = { it.id }
                    ) { subEntity ->
                        Row(
                            modifier = Modifier.clickable { onChildClick(subEntity) }
                        ) {
                            BaseEntityImage(
                                entity = subEntity,
                                modifier = Modifier.size(24.dp)
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(text = subEntity.name)

                            VerticalDivider()
                        }
                    }
                }
            }
        }
        HorizontalDivider()
    }
}