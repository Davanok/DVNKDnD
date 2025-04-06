@file:OptIn(ExperimentalUuidApi::class)

package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.ExperimentalUuidApi
import kotlin.uuid.Uuid

@Composable
fun NewCharacterStatsScreen(
    characterId: Uuid,
    onContinue: (Uuid) -> Unit,
    viewModel: NewCharacterStatsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth()
        ) {
            StatsCreationOptions.entries.forEachIndexed { index, option ->
                SegmentedButton(
                    selected = uiState.selectedCreationOptions == option,
                    onClick = { viewModel.selectCreationOption(option) },
                    shape = SegmentedButtonDefaults.itemShape(
                        index, StatsCreationOptions.entries.size
                    ),
                    label = { Text(text = stringResource(option.title)) }
                )
            }
        }
    }
}