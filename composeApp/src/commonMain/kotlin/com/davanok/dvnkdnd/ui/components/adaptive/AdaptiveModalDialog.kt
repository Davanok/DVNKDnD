package com.davanok.dvnkdnd.ui.components.adaptive

import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import com.davanok.dvnkdnd.data.model.ui.isCompact
import com.davanok.dvnkdnd.ui.components.sideSheet.ModalSideSheet
import com.davanok.dvnkdnd.ui.components.sideSheet.rememberModalSideSheetState
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.close_side_sheet
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdaptiveModalSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    title: @Composable () -> Unit = { },
    content: @Composable ColumnScope.() -> Unit
) {
    val scope = rememberCoroutineScope()
    val adaptiveInfo = LocalAdaptiveInfo.current

    if (adaptiveInfo.windowSizeClass.isCompact()) {
        ModalBottomSheet(
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            content = content
        )
    }
    else {
        val state = rememberModalSideSheetState()
        ModalSideSheet(
            sheetState = state,
            modifier = modifier,
            onDismissRequest = onDismissRequest,
            content = {
                TopAppBar(
                    title = title,
                    actions = {
                        IconButton(
                            onClick = { scope.launch { state.hide() }.invokeOnCompletion { onDismissRequest() } }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = stringResource(Res.string.close_side_sheet)
                            )
                        }
                    }
                )
                content()
            }
        )
    }
}