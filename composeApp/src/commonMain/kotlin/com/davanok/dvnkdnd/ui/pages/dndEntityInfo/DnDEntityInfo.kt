@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.ui.pages.dndEntityInfo

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import dvnkdnd.composeapp.generated.resources.error
import dvnkdnd.composeapp.generated.resources.error_when_loading_entity
import dvnkdnd.composeapp.generated.resources.loading
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DnDEntityInfo(
    entityId: Uuid,
    navigateBack: () -> Unit,
    viewModel: DnDEntityInfoViewModel = koinViewModel(),
) {
    LaunchedEffect(entityId) {
        viewModel.loadEntityInfo(entityId)
    }
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
                            uiState.entity != null -> uiState.entity!!.name
                            else -> stringResource(Res.string.error)
                        }
                    )
                },
                scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior()
            )
        }
    ) {
        when {
            uiState.isLoading -> Box(modifier = Modifier.fillMaxSize()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
            uiState.entity == null -> FullScreenCard {
                val description = stringResource(Res.string.error_when_loading_entity)
                Icon(
                    painter = painterResource(Res.drawable.error),
                    contentDescription = description
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = description
                )
            }
            else -> Content()
        }
    }
}

@Composable
private fun Content() {

}