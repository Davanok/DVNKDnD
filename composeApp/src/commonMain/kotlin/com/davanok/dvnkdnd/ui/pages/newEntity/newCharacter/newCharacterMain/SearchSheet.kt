package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.davanok.dvnkdnd.ui.fragments.searchEntityScaffold.SearchEntityResult
import com.davanok.dvnkdnd.ui.fragments.searchEntityScaffold.SearchEntityScaffold

@Composable
fun SearchSheet(
    entityType: DnDEntityTypes,
    onSelectEntity: (SearchEntityResult) -> Unit,
    onEntityInfo: (DnDEntityMin) -> Unit,
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
) {
    AdaptiveModalSheet(onDismissRequest = onDismissRequest) {
        SearchEntityScaffold(
            entityType = entityType,
            onEntityClick = onSelectEntity,
            onEntityInfoClick = onEntityInfo,
            modifier = modifier
        )
    }
}