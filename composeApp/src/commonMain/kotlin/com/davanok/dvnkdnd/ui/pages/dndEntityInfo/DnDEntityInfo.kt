package com.davanok.dvnkdnd.ui.pages.dndEntityInfo

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.ui.model.isCritical
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.error
import dvnkdnd.composeapp.generated.resources.loading
import dvnkdnd.composeapp.generated.resources.loading_entity_error
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DnDEntityInfo(
    navigateBack: () -> Unit,
    viewModel: DnDEntityInfoViewModel,
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
        topBar = {
            TopAppBar(
                navigationIcon = {
                    IconButton(
                        onClick = navigateBack
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Default.ArrowBack,
                            contentDescription = stringResource(Res.string.back)
                        )
                    }
                },
                title = {
                    Text(
                        text = when {
                            uiState.isLoading -> stringResource(Res.string.loading)
                            uiState.entity != null -> uiState.entity!!.entity.name
                            else -> stringResource(Res.string.error)
                        }
                    )
                },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        }
    ) {
        when {
            uiState.isLoading -> LoadingCard()
            uiState.error.isCritical() -> uiState.error?.let {
                ErrorCard(
                    text = it.message,
                    exception = it.exception
                )
            }
            uiState.entity == null -> ErrorCard(
                text = stringResource(Res.string.loading_entity_error)
            )
            else -> Content()
        }
    }
}

@Composable
private fun Content() {

}