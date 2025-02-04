package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.davanok.dvnkdnd.data.model.DnDEntityMin
import com.davanok.dvnkdnd.data.model.ui.WindowWidthSizeClass
import com.davanok.dvnkdnd.ui.components.FiniteTextField
import com.davanok.dvnkdnd.ui.components.ImageCropDialog
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.davanok.dvnkdnd.ui.components.adaptive.LocalAdaptiveNavigationInfo
import com.davanok.dvnkdnd.ui.components.image.toByteArray
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.add_photo_alternate
import dvnkdnd.composeapp.generated.resources.character_image
import dvnkdnd.composeapp.generated.resources.description
import dvnkdnd.composeapp.generated.resources.drop_image
import dvnkdnd.composeapp.generated.resources.name
import dvnkdnd.composeapp.generated.resources.select_image
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.FileKitPlatformSettings
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

@Composable
fun NewCharacterScreen(
    viewModel: NewCharacterViewModel = koinInject()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    CreateCharacterContent(
        modifier = Modifier.fillMaxSize(),
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
    val adaptiveInfo = LocalAdaptiveNavigationInfo.current
    val state by viewModel.newCharacterState.collectAsStateWithLifecycle()
    val entities by viewModel.downloadableState.collectAsStateWithLifecycle()

    if (adaptiveInfo.windowSizeClass.widthSizeClass == WindowWidthSizeClass.Compact) Column (
        modifier = modifier
            .verticalScroll(rememberScrollState())
    ) {
        ImageContent(
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f),
            image = state.image,
            onImageChange = viewModel::setCharacterImage
        )
        Spacer(modifier = Modifier.width(24.dp))
        Content(
            state = state,
            entities = entities,
            onNameChange = viewModel::setCharacterName,
            onDescriptionChange = viewModel::setCharacterDescription,
            onClassChange = viewModel::setCharacterClass
        )
    }
    else Row (
        modifier = modifier
    ) {
        ImageContent(
            modifier = Modifier
                .weight(.5f)
                .aspectRatio(1f),
            image = state.image,
            onImageChange = viewModel::setCharacterImage
        )
        Column (
            modifier = Modifier
                .weight(.5f)
                .verticalScroll(rememberScrollState())
        ) {
            Content(
                state = state,
                entities = entities,
                onNameChange = viewModel::setCharacterName,
                onDescriptionChange = viewModel::setCharacterDescription,
                onClassChange = viewModel::setCharacterClass
            )
        }
    }
}

@Composable
private fun Content(
    state: NewCharacterState,
    entities: DownloadableValuesState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onClassChange: (DnDEntityMin?) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = state.name,
        onValueChange = onNameChange,
        label = { Text(text = stringResource(Res.string.name)) },
        singleLine = true
    )
    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth(),
        value = state.description,
        onValueChange = onDescriptionChange,
        label = { Text(text = stringResource(Res.string.description)) },
        singleLine = true
    )
    FiniteTextField(
        modifier = Modifier.fillMaxWidth(),
        entities = entities.classes,
        toString = { it.name },
        onSelected = onClassChange
    )
}

@Composable
private fun ImageContent(
    image: ByteArray?,
    onImageChange: (ByteArray?) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()

    var pickedImage by remember { mutableStateOf<ByteArray?>(null) }
    val imagePicker = rememberFilePickerLauncher(
        type = PickerType.Image
    ) { image ->
        scope.launch { pickedImage = image?.toByteArray() }
    }

    pickedImage?.let {
        ImageCropDialog(
            bytes = it,
            boxSize = 300.dp,
            onResult = { image ->
                pickedImage = null
                onImageChange(image)
            }
        )
    }

    Box(
        modifier = modifier
    ) {
        AsyncImage(
            modifier = Modifier
                .clip(RoundedCornerShape(24.dp))
                .fillMaxSize(),
            model = image,
            contentDescription = stringResource(Res.string.character_image)
        )
        Row(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer)
        ) {
            IconButton(
                onClick = {
                    imagePicker.launch()
                }
            ) {
                Icon(
                    painter = painterResource(Res.drawable.add_photo_alternate),
                    contentDescription = stringResource(Res.string.select_image)
                )
            }
            IconButton(
                onClick = { onImageChange(null) }
            ) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = stringResource(Res.string.drop_image)
                )
            }
        }
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


