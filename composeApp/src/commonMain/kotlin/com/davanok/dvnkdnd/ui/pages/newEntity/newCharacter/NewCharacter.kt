package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import dvnkdnd.composeapp.generated.resources.Res
import org.jetbrains.compose.resources.stringResource
import dvnkdnd.composeapp.generated.resources.image
import dvnkdnd.composeapp.generated.resources.name
import dvnkdnd.composeapp.generated.resources.description
import org.koin.compose.koinInject
import androidx.compose.runtime.getValue

@Composable
fun NewCharacter(
    viewModel: NewCharacterViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateCharacterContent(
        viewModel = viewModel
    )
}

@Composable
private fun CreateCharacterContent(
    viewModel: NewCharacterViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.createCharacterState.collectAsStateWithLifecycle()
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            modifier  = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(12.dp)),
            model = state.image,
            contentDescription = stringResource(Res.string.image)
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.name,
            onValueChange = viewModel::setCharacterName,
            label = { Text(text = stringResource(Res.string.name)) },
            singleLine = true
        )
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.description,
            onValueChange = viewModel::setCharacterDescription,
            label = { Text(text = stringResource(Res.string.description)) }
        )
    }
}