package com.davanok.dvnkdnd.ui.pages.characterFull.pages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.FilterChip
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.window.Dialog
import androidx.window.core.layout.WindowSizeClass
import com.davanok.dvnkdnd.domain.entities.character.CharacterNote
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import com.mikepenz.markdown.m3.Markdown
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.add_tag
import dvnkdnd.composeapp.generated.resources.cancel
import dvnkdnd.composeapp.generated.resources.cancel_delete_note
import dvnkdnd.composeapp.generated.resources.character_has_no_notes
import dvnkdnd.composeapp.generated.resources.confirm_delete_note
import dvnkdnd.composeapp.generated.resources.delete
import dvnkdnd.composeapp.generated.resources.delete_note
import dvnkdnd.composeapp.generated.resources.delete_tag
import dvnkdnd.composeapp.generated.resources.edit_note_dialog_title
import dvnkdnd.composeapp.generated.resources.new_note
import dvnkdnd.composeapp.generated.resources.new_note_dialog_title
import dvnkdnd.composeapp.generated.resources.no_tags_added
import dvnkdnd.composeapp.generated.resources.note_not_pinned
import dvnkdnd.composeapp.generated.resources.note_pinned
import dvnkdnd.composeapp.generated.resources.note_text
import dvnkdnd.composeapp.generated.resources.outline_keep
import dvnkdnd.composeapp.generated.resources.outline_keep_off
import dvnkdnd.composeapp.generated.resources.save
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid

@Composable
fun CharacterNotesScreen(
    notes: List<CharacterNote>,
    onUpdateOrNewNote: (CharacterNote) -> Unit,
    onDeleteNote: (CharacterNote) -> Unit,
    modifier: Modifier = Modifier,
) {
    var filterTag by remember { mutableStateOf<String?>(null) }

    val allTags by remember(notes) {
        derivedStateOf { notes.fastFlatMap { it.tags }.distinct() }
    }

    val filtered by remember(filterTag, notes) {
        derivedStateOf {
            val base = if (filterTag == null) notes else notes.filter { filterTag in it.tags }
            base.sortedByDescending { it.pinned }
        }
    }

    var editNoteDialog by remember { mutableStateOf<CharacterNote?>(null) }

    val tagsListState = rememberLazyListState()

    LaunchedEffect(filterTag) {
        if (filterTag != null) {
            val index = allTags.indexOf(filterTag)
            if (index in 0..allTags.size)
                tagsListState.animateScrollToItem(index, scrollOffset = -100)
        }
    }
    Column(modifier = modifier) {
        NotesTopBar(
            tagsListState = tagsListState,
            allTags = allTags,
            filterTag = filterTag,
            onTagClick = { filterTag = it.takeIf { it != filterTag } },
            onAddTagClick = { editNoteDialog = CharacterNote(id = Uuid.NIL) },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider(Modifier.fillMaxWidth().padding(top = 8.dp, bottom = 16.dp))

        if (notes.isEmpty())
            FullScreenCard(modifier = modifier) {
                Text(text = stringResource(Res.string.character_has_no_notes))
            }
        else
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(items = filtered, key = { it.id }) { note ->
                    CharacterNoteCard(
                        note = note,
                        onTagClick = { filterTag = it.takeIf { filterTag != it } },
                        onEdit = { editNoteDialog = it },
                        onDelete = onDeleteNote
                    )
                }
            }
    }

    editNoteDialog?.let { note ->
        UpdateNoteDialog(
            note = note,
            onDismiss = { editNoteDialog = null },
            onUpdate = {
                editNoteDialog = null
                onUpdateOrNewNote(it)
            },
            onDelete = onDeleteNote
        )
    }
}

@Composable
private fun NotesTopBar(
    tagsListState: LazyListState,
    allTags: List<String>,
    filterTag: String?,
    onTagClick: (String) -> Unit,
    onAddTagClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current

    var buttonWidth by remember { mutableStateOf(48.dp) }

    Box(
        modifier = modifier
    ) {
        LazyRow(
            state = tagsListState,
            modifier = Modifier.align(Alignment.CenterStart),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(
                items = allTags,
                key = { it }
            ) { tag ->
                FilterChip(
                    selected = filterTag == tag,
                    onClick = { onTagClick(tag) },
                    label = { Text(tag, maxLines = 1) }
                )
            }
            item {
                Spacer(Modifier.width(buttonWidth))
            }
        }

        FilledIconButton(
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .onSizeChanged {
                    with(density) { buttonWidth = it.width.toDp() }
                },
            onClick = onAddTagClick
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.new_note)
            )
        }
    }
}


@Composable
private fun CharacterNoteCard(
    note: CharacterNote,
    onTagClick: (String) -> Unit,
    onEdit: (CharacterNote) -> Unit,
    onDelete: (CharacterNote) -> Unit,
    modifier: Modifier = Modifier
) {
    var deleteConfirm by remember { mutableStateOf(false) }

    OutlinedCard(
        modifier = modifier,
        border = if (note.pinned)
            CardDefaults.outlinedCardBorder()
                .copy(brush = SolidColor(MaterialTheme.colorScheme.primaryContainer))
        else CardDefaults.outlinedCardBorder(),
        onClick = { onEdit(note) }
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Box(
                modifier = Modifier.fillMaxWidth(),
            ) {
                Box(modifier = Modifier.align(Alignment.CenterStart)) {
                    if (note.tags.isNotEmpty())
                        LazyRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                note.tags.toList(),
                                key = { it }
                            ) { tag ->
                                AssistChip(label = { Text(tag) }, onClick = { onTagClick(tag) })
                            }
                            item {
                                Spacer(Modifier.width(96.dp))
                            }
                        }
                }

                AnimatedContent(
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .background(MaterialTheme.colorScheme.primaryContainer, CircleShape),
                    targetState = deleteConfirm,
                    transitionSpec = { slideInHorizontally{ -it } + fadeIn() togetherWith slideOutHorizontally { it } + fadeOut() }
                ) {
                    Row {
                        if (deleteConfirm) {
                            IconButton(onClick = { deleteConfirm = false }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = stringResource(Res.string.cancel_delete_note)
                                )
                            }
                            IconButton(onClick = { onDelete(note) }) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = stringResource(Res.string.confirm_delete_note)
                                )
                            }
                        } else {
                            IconButton(onClick = { deleteConfirm = true }) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = stringResource(Res.string.delete_note)
                                )
                            }
                        }
                    }
                }
            }

            HorizontalDivider(Modifier.fillMaxWidth().padding(vertical = 8.dp))

            note.title?.let {
                Text(text = it, style = MaterialTheme.typography.titleLarge)
            }
            Markdown(
                content = note.body,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}

@Composable
private fun UpdateNoteDialog(
    note: CharacterNote,
    onDismiss: () -> Unit,
    onUpdate: (CharacterNote) -> Unit,
    onDelete: (CharacterNote) -> Unit
) {
    var pinned by remember { mutableStateOf(note.pinned) }
    val tagsState = remember { note.tags.toMutableSet() }
    var text by remember { mutableStateOf(note.text) }
    var newTag by remember { mutableStateOf("") }

    val isNewNote = note.id == Uuid.NIL

    val buildNote = {
        if (newTag.isNotBlank()) tagsState.add(newTag)
        CharacterNote(
            id = if (isNewNote) Uuid.random() else note.id,
            pinned = pinned,
            tags = tagsState,
            text = text
        )
    }

    Dialog(onDismissRequest = onDismiss) {
        Card {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = stringResource(
                            if (isNewNote) Res.string.new_note_dialog_title
                            else Res.string.edit_note_dialog_title
                        ),
                        style = MaterialTheme.typography.titleLarge
                    )

                    FilledIconToggleButton(checked = pinned, onCheckedChange = { pinned = it }) {
                        if (pinned)
                            Icon(
                                painter = painterResource(Res.drawable.outline_keep),
                                contentDescription = stringResource(Res.string.note_pinned)
                            )
                        else
                            Icon(
                                painter = painterResource(Res.drawable.outline_keep_off),
                                contentDescription = stringResource(Res.string.note_not_pinned)
                            )
                    }
                }
                if (tagsState.isEmpty())
                    Text(text = stringResource(Res.string.no_tags_added))
                else
                    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        tagsState.forEach { tag ->
                            key(tag) {
                                AssistChip(
                                    onClick = { tagsState.remove(tag) },
                                    label = { Text(tag) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Icons.Default.Close,
                                            contentDescription = stringResource(Res.string.delete_tag)
                                        )
                                    }
                                )
                            }
                        }
                    }
                val onDone = {
                    val normalized = newTag.trim()
                    if (normalized.isNotBlank() && !tagsState.contains(normalized))
                        tagsState.add(normalized)
                    newTag = ""
                }
                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = newTag,
                    onValueChange = { newTag = it },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = { onDone() }),
                    label = { Text(text = stringResource(Res.string.add_tag)) },
                    trailingIcon = {
                        IconButton(onClick = onDone) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = stringResource(Res.string.add_tag)
                            )
                        }
                    },
                    maxLines = 1
                )

                OutlinedTextField(
                    modifier = Modifier.fillMaxWidth(),
                    value = text,
                    onValueChange = { text = it },
                    singleLine = false,
                    label = { Text(text = stringResource(Res.string.note_text)) },
                )

                EditNoteDialogButtons(
                    onDelete = { onDelete(note) },
                    onCancel = onDismiss,
                    onSave = { onUpdate(buildNote()) },
                    deleteVisible = !isNewNote,
                    saveEnabled = text.isNotBlank()
                )
            }
        }
    }
}

@Composable
private fun EditNoteDialogButtons(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    deleteVisible: Boolean,
    saveEnabled: Boolean
) {
    val showFullSize = currentWindowAdaptiveInfo()
        .windowSizeClass
        .isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    if (showFullSize)
        EditNoteDialogFullSizeButtons(
            onDelete = onDelete,
            onCancel = onCancel,
            onSave = onSave,
            deleteVisible = deleteVisible,
            saveEnabled = saveEnabled
        )
    else
        EditNoteDialogCompactSizeButtons(
            onDelete = onDelete,
            onCancel = onCancel,
            onSave = onSave,
            deleteVisible = deleteVisible,
            saveEnabled = saveEnabled
        )
}

@Composable
private fun EditNoteDialogFullSizeButtons(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    deleteVisible: Boolean,
    saveEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (deleteVisible)
            Button(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.delete_note)
                )
                Text(text = stringResource(Res.string.delete))
            }

        Button(onClick = onCancel) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.cancel)
            )
            Text(text = stringResource(Res.string.cancel))
        }

        Button(
            onClick = onSave,
            enabled = saveEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(Res.string.save)
            )
            Text(text = stringResource(Res.string.save))
        }
    }
}

@Composable
private fun EditNoteDialogCompactSizeButtons(
    onDelete: () -> Unit,
    onCancel: () -> Unit,
    onSave: () -> Unit,
    deleteVisible: Boolean,
    saveEnabled: Boolean
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        if (deleteVisible)
            FilledIconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = stringResource(Res.string.delete_note)
                )
            }

        FilledIconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Default.Close,
                contentDescription = stringResource(Res.string.cancel)
            )
        }

        FilledIconButton(
            onClick = onSave,
            enabled = saveEnabled
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(Res.string.save)
            )
        }
    }
}