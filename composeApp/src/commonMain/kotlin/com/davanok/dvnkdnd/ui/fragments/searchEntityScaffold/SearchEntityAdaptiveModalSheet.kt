package com.davanok.dvnkdnd.ui.fragments.searchEntityScaffold

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet

@Composable
fun SearchEntityAdaptiveModalSheet(
    entityType: DnDEntityTypes,
    onEntityClick: (SearchEntityResult) -> Unit,
    onEntityInfoClick: (DnDEntityMin) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AdaptiveModalSheet(onDismissRequest = onDismissRequest) {
        SearchEntityScaffold(
            entityType = entityType,
            onEntityClick = onEntityClick,
            onEntityInfoClick = onEntityInfoClick,
            modifier = modifier
        )
    }
}