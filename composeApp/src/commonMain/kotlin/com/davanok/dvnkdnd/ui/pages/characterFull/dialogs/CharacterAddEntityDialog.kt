package com.davanok.dvnkdnd.ui.pages.characterFull.dialogs

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
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
import androidx.paging.compose.collectAsLazyPagingItems
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.ui.components.BaseEntityImage
import com.davanok.dvnkdnd.ui.pages.characterFull.CharacterAddEntityViewModel
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.hide_description
import dvnkdnd.composeapp.generated.resources.search_entity_type
import dvnkdnd.composeapp.generated.resources.show_description
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf
import kotlin.uuid.Uuid

@Composable
fun CharacterAddEntityDialogContent(
    entityType: DnDEntityTypes,
    onSelectEntityClick: (DnDEntityMin) -> Unit,
    onEntityInfoClick: (DnDEntityMin) -> Unit,
    viewModel: CharacterAddEntityViewModel = koinViewModel { parametersOf(entityType) }
) {
    val entities = viewModel.entitiesFlow.collectAsLazyPagingItems()
    val query by viewModel.searchQuery.collectAsStateWithLifecycle()

    var expandedCard: Uuid? by remember { mutableStateOf(null) }

    Column {
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = query,
            onValueChange = viewModel::setSearchQuery,
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
            label = {
                Text(
                    text = stringResource(
                        Res.string.search_entity_type,
                        stringResource(entityType.stringRes))
                )
            }
        )

        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(top = 16.dp)
        ) {
            // TODO: add placeholders
            items(
                count = entities.itemCount,
                key = { entities[it]?.id ?: Uuid.random() }
            ) { index ->
                entities[index]?.let { entity ->
                    EntityCard(
                        entity = entity,
                        expanded = entity.id == expandedCard,
                        onExpandClick = {
                            expandedCard =
                                if (expandedCard == entity.id) null
                                else entity.id
                        },
                        onInfoClick = onEntityInfoClick,
                        onAddClick = onSelectEntityClick,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
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
    onAddClick: (DnDEntityMin) -> Unit,
    modifier: Modifier = Modifier
) {
    val entityMin = entity.toDnDEntityMin()
    OutlinedCard(
        modifier = modifier,
        onClick = { onAddClick(entityMin) }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth()
            ) {
                BaseEntityImage(
                    entity = entityMin,
                    onClick = { onInfoClick(entityMin) },
                    modifier = Modifier
                        .size(56.dp)
                )

                Spacer(modifier = Modifier.width(12.dp))

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
                    onClick = onExpandClick,
                    modifier = Modifier.align(Alignment.Bottom)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription =
                            if (expanded) stringResource(Res.string.hide_description)
                            else stringResource(Res.string.show_description),
                        modifier = Modifier.rotate(if (expanded) 180f else 0f))
                }
            }
            AnimatedVisibility(visible = expanded) {
                Column {
                    Spacer(Modifier.height(12.dp))
                    Text(text = entity.description)
                }
            }

            entity.subEntities.forEach { subEntity ->
                HorizontalDivider(Modifier.padding(vertical = 8.dp))

                Row(
                    modifier = Modifier.clickable { onAddClick(subEntity) }
                ) {
                    BaseEntityImage(
                        entity = subEntity,
                        modifier = Modifier.size(24.dp)
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    Text(text = subEntity.name)
                }
            }
        }
    }
}