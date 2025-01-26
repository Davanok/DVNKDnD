package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.util.fastFilter
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.DnDEntityMin
import com.davanok.dvnkdnd.ui.components.SelectableTextField
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.cls
import dvnkdnd.composeapp.generated.resources.description
import dvnkdnd.composeapp.generated.resources.name
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject
import kotlin.text.startsWith

@Composable
fun NewCharacterScreen(
    viewModel: NewCharacterViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateCharacterContent(
        viewModel = viewModel
    )

    uiState.sheetContent?.let {
        ModalSheetContent(
            onDismiss = {
                viewModel.hideSheet()
                // TODO: set custom entity
            },
            viewModel = viewModel,
            sheetContent = it
        )
    }
}

@Composable
private fun CreateCharacterContent(
    viewModel: NewCharacterViewModel,
    modifier: Modifier = Modifier
) {
    val state by viewModel.newCharacterState.collectAsStateWithLifecycle()
    val entities by viewModel.downloadableState.collectAsState()
    Column(
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
//        AsyncImage(
//            modifier  = Modifier
//                .fillMaxWidth()
//                .aspectRatio(1f)
//                .clip(RoundedCornerShape(12.dp)),
//            model = state.image,
//            contentDescription = stringResource(Res.string.image)
//        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = state.name,
            onValueChange = viewModel::setCharacterName,
            label = { Text(text = stringResource(Res.string.name)) },
            singleLine = true
        )
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            value = state.description,
            onValueChange = viewModel::setCharacterDescription,
            label = { Text(text = stringResource(Res.string.description)) },
            singleLine = true
        )
        FiniteTextField(
            modifier = Modifier.fillMaxWidth(),
            entities = entities.classes,
            toString = { it.name },
            onSelected = viewModel::setCharacterClass
        )
    }
}

@Composable
private fun ModalSheetContent(
    sheetContent: SheetContent,
    onDismiss: (DnDEntityMin?) -> Unit,
    viewModel: NewCharacterViewModel // TODO: replace to BrowseViewModel
) {
    AdaptiveModalSheet(
        onDismissRequest = { onDismiss(null) },
        header = {
            Text("SELECT ${sheetContent.name}")
        }
    ) {
        Text("coming soon")
    }
}

@Composable
private fun <T> FiniteTextField(
    entities: List<T>,
    toString: (T) -> String,
    onSelected: (T?) -> Unit,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    val entitiesMap = remember(entities) {
        entities.associateWith(toString)
    }
    val filteredItems by remember(entities, text) {
        derivedStateOf {
            entitiesMap.filterValues { it.startsWith(text, ignoreCase = true) }
        }
    }
    var prevSelected by remember { mutableStateOf<T?>(null) }

    SelectableTextField(
        modifier = modifier,
        value = text,
        onValueChange = { newText ->
            text = newText
            val match = entitiesMap.entries
                .firstOrNull { it.value.equals(newText, ignoreCase = true) }
                ?.key
            if (match != prevSelected) {
                prevSelected = match
                onSelected(match)
            }
        },
        label = { Text(text = stringResource(Res.string.cls)) }
    ) {
        filteredItems.forEach { (key, value) ->
            item(
                text = { Text(value) },
                onClick = {
                    text = value
                    if (prevSelected != key) {
                        prevSelected = key
                        onSelected(key)
                    }
                }
            )
        }
    }
}


