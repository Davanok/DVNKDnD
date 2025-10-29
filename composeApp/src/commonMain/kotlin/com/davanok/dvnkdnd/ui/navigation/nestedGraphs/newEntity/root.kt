package com.davanok.dvnkdnd.ui.navigation.nestedGraphs.newEntity

import com.davanok.dvnkdnd.ui.navigation.Route
import com.davanok.dvnkdnd.ui.navigation.RouterEntryProvider
import com.davanok.dvnkdnd.ui.pages.newEntity.NewEntityScreen
import com.davanok.dvnkdnd.ui.pages.newEntity.newItem.NewItemScreen


fun RouterEntryProvider.newEntityDestinations(
    onBack: () -> Unit,
    navigate: (route: Route) -> Unit,
    replace: (route: Route) -> Unit
) {
    entry<Route.New> {
        NewEntityScreen(
            onNavigateBack = onBack,
            onNavigate = navigate
        )
    }
    entry<Route.New.Item> {
        NewItemScreen()
    }
    characterCreationFlow(onBack, replace)
}