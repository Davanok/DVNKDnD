package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.carousel.CarouselState
import androidx.compose.material3.carousel.HorizontalMultiBrowseCarousel
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil3.compose.AsyncImage
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.entities.DnDEntityMin
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.ui.components.FiniteTextField
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.davanok.dvnkdnd.ui.components.ImageCropDialog
import com.davanok.dvnkdnd.ui.components.image.toByteArray
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.add_image
import dvnkdnd.composeapp.generated.resources.background
import dvnkdnd.composeapp.generated.resources.character_image
import dvnkdnd.composeapp.generated.resources.cls
import dvnkdnd.composeapp.generated.resources.continue_str
import dvnkdnd.composeapp.generated.resources.description
import dvnkdnd.composeapp.generated.resources.drop_image
import dvnkdnd.composeapp.generated.resources.empty_field_error
import dvnkdnd.composeapp.generated.resources.error
import dvnkdnd.composeapp.generated.resources.name
import dvnkdnd.composeapp.generated.resources.no_character_images_yet
import dvnkdnd.composeapp.generated.resources.race
import dvnkdnd.composeapp.generated.resources.set_image_to_main
import dvnkdnd.composeapp.generated.resources.sub_background
import dvnkdnd.composeapp.generated.resources.sub_class
import dvnkdnd.composeapp.generated.resources.sub_race
import io.github.vinceglb.filekit.compose.rememberFilePickerLauncher
import io.github.vinceglb.filekit.core.PickerType
import kotlinx.coroutines.launch
import okio.Path
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.viewmodel.koinViewModel
import kotlin.uuid.Uuid


@Composable
fun NewCharacterMainScreen(
    navigateToEntityInfo: (DnDEntityMin) -> Unit,
    onContinue: (characterId: Uuid) -> Unit,
    viewModel: NewCharacterMainViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    if (uiState.checkingDataState != NewCharacterMainUiState.CheckingDataStates.FINISH)
        LoadingDataCard(uiState.checkingDataState)
    else
        CreateCharacterContent(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            empties = uiState.emptyFields,
            onCreateCharacter = { viewModel.createCharacter(onContinue) },
            viewModel = viewModel
        )
    if (uiState.showSearchSheet)
        SearchSheet(
            onDismiss = viewModel::hideSearchSheet,
            onGetEntityInfo = navigateToEntityInfo,
            viewModel = viewModel
        )
}

@Composable
private fun CreateCharacterContent(
    viewModel: NewCharacterMainViewModel,
    empties: NewCharacterMainUiState.EmptyFields,
    onCreateCharacter: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val state by viewModel.newCharacterMain.collectAsStateWithLifecycle()
    val entities by viewModel.downloadableState.collectAsStateWithLifecycle()

    Column (
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        ImageContent(
            modifier = Modifier
                .height(300.dp)
                .widthIn(488.dp),
            images = state.images,
            mainImage = state.mainImage,
            onAddImage = viewModel::addCharacterImage,
            onRemoveImage = viewModel::removeCharacterImage,
            onSetImageMain = viewModel::setCharacterMainImage
        )
        Spacer(modifier = Modifier.height(24.dp))
        Content(
            state = state,
            empties = empties,
            entities = entities,
            onNameChange = viewModel::setCharacterName,
            onDescriptionChange = viewModel::setCharacterDescription,
            onClassChange = viewModel::setCharacterClass,
            onSubClassSelected = viewModel::setCharacterSubClass,
            onRaceSelected = viewModel::setCharacterRace,
            onSubRaceSelected = viewModel::setCharacterSubRace,
            onBackgroundSelected = viewModel::setCharacterBackground,
            onSubBackgroundSelected = viewModel::setCharacterSubBackground,
            onOpenExtendedSearch = viewModel::openSearchSheet
        )
        Button (
            modifier = Modifier
                .align(Alignment.End),
            onClick = onCreateCharacter
        ) {
            Text(
                text = stringResource(Res.string.continue_str)
            )
        }
    }
}

@Composable
private fun Content(
    state: NewCharacterMain,
    empties: NewCharacterMainUiState.EmptyFields,
    entities: DownloadableValuesState,
    onNameChange: (String) -> Unit,
    onDescriptionChange: (String) -> Unit,
    onClassChange: (DnDEntityWithSubEntities?) -> Unit,
    onSubClassSelected: (DnDEntityMin?) -> Unit,
    onRaceSelected: (DnDEntityWithSubEntities?) -> Unit,
    onSubRaceSelected: (DnDEntityMin?) -> Unit,
    onBackgroundSelected: (DnDEntityWithSubEntities?) -> Unit,
    onSubBackgroundSelected: (DnDEntityMin?) -> Unit,
    onOpenExtendedSearch: (DnDEntityTypes, String) -> Unit
) {
    val textFieldModifier = Modifier
        .widthIn(488.dp)
    val errorText: (error: Boolean) -> @Composable (() -> Unit)? = { error ->
        if (error) { { Text(
            text = stringResource(Res.string.empty_field_error),
            color = MaterialTheme.colorScheme.error
        ) } }
        else null
    }
    OutlinedTextField(
        modifier = textFieldModifier,
        value = state.name,
        onValueChange = onNameChange,
        label = { Text(text = stringResource(Res.string.name)) },
        singleLine = true,
        isError = empties.name,
        supportingText = errorText(empties.name)
    )
    OutlinedTextField(
        modifier = textFieldModifier,
        value = state.description,
        onValueChange = onDescriptionChange,
        label = { Text(text = stringResource(Res.string.description)) },
        singleLine = true
    )

    FiniteTextField(
        modifier = textFieldModifier,
        value = state.cls,
        entities = entities.classes,
        toString = { it.name },
        onSelected = onClassChange,
        onNeedMore = { onOpenExtendedSearch(DnDEntityTypes.CLASS, it) },
        label = { Text(text = stringResource(Res.string.cls)) },
        isError = empties.cls,
        supportingText = errorText(empties.cls)
    )
    if (!state.cls?.subEntities.isNullOrEmpty())
        FiniteTextField(
            modifier = textFieldModifier,
            value = state.subCls,
            entities = state.cls.subEntities,
            toString = { it.name },
            onSelected = onSubClassSelected,
            label = { Text(text = stringResource(Res.string.sub_class)) },
            isError = empties.subCls,
            supportingText = errorText(empties.subCls)
        )
    FiniteTextField(
        modifier = textFieldModifier,
        value = state.race,
        entities = entities.races,
        toString = { it.name },
        onSelected = onRaceSelected,
        onNeedMore = { onOpenExtendedSearch(DnDEntityTypes.RACE, it) },
        label = { Text(text = stringResource(Res.string.race)) },
        isError = empties.race,
        supportingText = errorText(empties.race)
    )
    if (!state.race?.subEntities.isNullOrEmpty())
        FiniteTextField(
            modifier = textFieldModifier,
            value = state.subRace,
            entities = state.race.subEntities,
            toString = { it.name },
            onSelected = onSubRaceSelected,
            label = { Text(text = stringResource(Res.string.sub_race)) },
            isError = empties.subRace,
            supportingText = errorText(empties.subRace)
        )
    FiniteTextField(
        modifier = textFieldModifier,
        value = state.background,
        entities = entities.backgrounds,
        toString = { it.name },
        onSelected = onBackgroundSelected,
        onNeedMore = { onOpenExtendedSearch(DnDEntityTypes.BACKGROUND, it) },
        label = { Text(text = stringResource(Res.string.background)) },
        isError = empties.background,
        supportingText = errorText(empties.background)
    )
    if (!state.background?.subEntities.isNullOrEmpty())
        FiniteTextField(
            modifier = textFieldModifier,
            value = state.subRace,
            entities = state.background.subEntities,
            toString = { it.name },
            onSelected = onSubBackgroundSelected,
            label = { Text(text = stringResource(Res.string.sub_background)) },
            isError = empties.subBackground,
            supportingText = errorText(empties.subBackground)
        )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImageContent(
    images: List<Path>,
    mainImage: Path?,
    onAddImage: (ByteArray) -> Unit,
    onRemoveImage: (Path) -> Unit,
    onSetImageMain: (Path) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(false) }

    var pickedImage by remember { mutableStateOf<ByteArray?>(null) }
    val imagePicker = rememberFilePickerLauncher(
        type = PickerType.Image
    ) { image ->
        image?.let { image ->
            scope.launch {
                isLoading = true
                pickedImage = image.toByteArray()
                isLoading = false
            }
        }
    }

    if (isLoading) {
        Dialog (
            onDismissRequest = {  },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            )
        ) {
            CircularProgressIndicator()
        }
    }

    pickedImage?.let {
        ImageCropDialog(
            bytes = it,
            boxSize = 300.dp,
            onResult = { image ->
                pickedImage = null
                if (image != null) onAddImage(image)
            }
        )
    }
    if (images.isNotEmpty())
        ImagesList(
            modifier = modifier,
            images = images,
            mainImage = mainImage,
            onAddImage = { imagePicker.launch() },
            onRemoveImage = onRemoveImage,
            onSetImageMain = onSetImageMain
        )
    else
        FullScreenCard (
            modifier = Modifier.clickable(onClick = imagePicker::launch)
        ) {
            Icon(
                imageVector = Icons.Default.AccountBox,
                contentDescription = stringResource(Res.string.no_character_images_yet)
            )
            Spacer(modifier = Modifier.height(16.dp))
            Text(
                text = stringResource(Res.string.no_character_images_yet)
            )
        }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ImagesList(
    images: List<Path>,
    mainImage: Path?,
    onAddImage: () -> Unit,
    onRemoveImage: (Path) -> Unit,
    onSetImageMain: (Path) -> Unit,
    modifier: Modifier = Modifier,
) {
    val carouselState = rememberSaveable(images.size, saver = CarouselState.Saver) {
        CarouselState(0) { images.size + 1 }
    }
    HorizontalMultiBrowseCarousel(
        modifier = modifier,
        state = carouselState,
        preferredItemWidth = 300.dp,
        itemSpacing = 8.dp
    ) { index ->
        if (index != images.size) {
            val image = images[index]
            Box (
                modifier = Modifier
                    .size(300.dp)
                    .maskClip(MaterialTheme.shapes.extraLarge),
            ) {
                AsyncImage(
                    modifier = Modifier.fillMaxSize(),
                    model = image,
                    contentDescription = stringResource(Res.string.character_image),
                    contentScale = ContentScale.FillBounds
                )
                Row(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primaryContainer),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    IconButton(onClick = { onSetImageMain(image) }) {
                        val icon =
                            if (mainImage == image) Icons.Default.Favorite
                            else Icons.Default.FavoriteBorder
                        Icon(
                            imageVector = icon,
                            contentDescription = stringResource(Res.string.set_image_to_main)
                        )
                    }
                    IconButton(onClick = { onRemoveImage(image) }) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = stringResource(Res.string.drop_image)
                        )
                    }
                }
            }
        }
        else {
            FilledIconButton(
                onClick = onAddImage
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(Res.string.add_image)
                )
            }
        }
    }
}

@Composable
private fun LoadingDataCard(state: NewCharacterMainUiState.CheckingDataStates) {
    FullScreenCard {
        if (state == NewCharacterMainUiState.CheckingDataStates.ERROR) {
            Icon(
                painter = painterResource(Res.drawable.error),
                contentDescription = stringResource(state.text)
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
        Text(text = stringResource(state.text))

        if (state != NewCharacterMainUiState.CheckingDataStates.ERROR) {
            val stateValues = NewCharacterMainUiState.CheckingDataStates.entries
            LinearProgressIndicator(
                progress = {
                    stateValues.indexOf(state) / stateValues.size.toFloat()
                }
            )
        }
    }
}


