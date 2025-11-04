package com.davanok.dvnkdnd.ui.navigation.nestedGraphs.entityInfo

import com.davanok.dvnkdnd.ui.navigation.Route
import com.davanok.dvnkdnd.ui.navigation.RouterEntryProvider
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfo
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf


fun RouterEntryProvider.entityInfoDestinations(
    onBack: () -> Unit,
    navigate: (route: Route) -> Unit
) {
    entry<Route.EntityInfoDialog> { route ->
        DnDEntityInfo(
            viewModel = koinViewModel { parametersOf(route.entityId) },
            navigateBack = onBack
        )
    }
}