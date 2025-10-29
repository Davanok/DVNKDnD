package com.davanok.dvnkdnd.ui.navigation.nestedGraphs.entityInfo

import com.davanok.dvnkdnd.ui.navigation.Route
import com.davanok.dvnkdnd.ui.navigation.RouterEntryProvider
import com.davanok.dvnkdnd.ui.pages.dndEntityInfo.DnDEntityInfo


fun RouterEntryProvider.entityInfoDestinations(
    onBack: () -> Unit,
    navigate: (route: Route) -> Unit
) {
    entry<Route.EntityInfo> { route ->
        DnDEntityInfo(
            route.entityId,
            navigateBack = onBack
        )
    }
}