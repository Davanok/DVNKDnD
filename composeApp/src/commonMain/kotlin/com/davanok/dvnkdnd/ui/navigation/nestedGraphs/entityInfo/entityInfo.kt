package com.davanok.dvnkdnd.ui.navigation.nestedGraphs.entityInfo

import com.davanok.dvnkdnd.ui.navigation.Route
import com.davanok.dvnkdnd.ui.navigation.RouterEntryProvider
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfo
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfoViewModel
import dev.zacsweers.metrox.viewmodel.assistedMetroViewModel


fun RouterEntryProvider.entityInfoDestinations(
    onBack: () -> Unit,
    navigate: (route: Route) -> Unit
) {
    entry<Route.EntityInfoDialog> { route ->
        DnDEntityInfo(
            viewModel = assistedMetroViewModel<DnDEntityInfoViewModel, DnDEntityInfoViewModel.Factory> { create(route.entityId) },
            navigateBack = onBack
        )
    }
}