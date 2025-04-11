package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterStats

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.util.fastFilter
import androidx.compose.ui.util.fastFirst
import androidx.compose.ui.util.fastFirstOrNull
import androidx.compose.ui.util.fastFlatMap
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastJoinToString
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.util.fastMapNotNull
import com.davanok.dvnkdnd.data.model.dnd_enums.DnDEntityTypes
import com.davanok.dvnkdnd.data.model.dnd_enums.Stats
import com.davanok.dvnkdnd.data.model.dnd_enums.stringRes
import com.davanok.dvnkdnd.data.model.entities.CharacterWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDEntityWithModifiers
import com.davanok.dvnkdnd.data.model.entities.DnDModifier
import com.davanok.dvnkdnd.data.model.entities.DnDModifiersGroup
import com.davanok.dvnkdnd.data.model.util.DnDConstants
import com.davanok.dvnkdnd.data.model.util.calculateBuyingModifiersSum
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.LocalColorScheme
import com.davanok.dvnkdnd.ui.components.SuffixVisualTransformation
import com.davanok.dvnkdnd.ui.components.Table
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.about_modifiers
import dvnkdnd.composeapp.generated.resources.decrease_modifier_value
import dvnkdnd.composeapp.generated.resources.increase_modifier_value
import dvnkdnd.composeapp.generated.resources.remove
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.uuid.Uuid
import com.davanok.dvnkdnd.ui.components.append
import com.dokar.sonner.Toaster
import com.dokar.sonner.rememberToasterState
import dvnkdnd.composeapp.generated.resources.no_modifiers_for_info
import kotlin.enums.enumEntries


@Composable
fun ModifiersSelector(
    selectedCreationOption: StatsCreationOptions,
    character: CharacterWithModifiers,
    modifiers: DnDModifiersGroup,
    onModifiersChange: (DnDModifiersGroup) -> Unit,
    onSelectModifiers: (DnDModifier) -> Unit
) {
    val commonModifiers = remember(character) {
        character.classes + listOfNotNull(
            character.race,
            character.subRace,
            character.background,
            character.subBackground
        )
    }
    var showInfoSheet by remember { mutableStateOf<Stats?>(null) }
    Crossfade(
        modifier = Modifier.fillMaxSize(),
        targetState = selectedCreationOption
    ) {
        Column {
            when (it) {
                StatsCreationOptions.POINT_BUY -> PointBuyModifiersSelector(
                    character = modifiers,
                    onChange = onModifiersChange,
                    onInfoClick = { showInfoSheet = it },
                    allModifiers = commonModifiers,
                    selectedModifiers = character.selectedModifiers,
                    onModifierSelected = onSelectModifiers
                )
                StatsCreationOptions.STANDARD_ARRAY -> TODO()
                StatsCreationOptions.MANUAL -> TODO()
            }
        }
    }
    showInfoSheet?.let { stat ->
        AdaptiveModalSheet(
            onDismissRequest = { showInfoSheet = null }
        ) {
            Text(buildAnnotatedString {
                commonModifiers.fastForEach {
                    if (it.modifiers.isEmpty()) return@fastForEach
                    withStyle(MaterialTheme.typography.labelLarge.toSpanStyle()) {
                        append(it.entity.type.stringRes())
                    }
                    append("\n\t")
                    it.modifiers.fastFilter { it.stat == stat }.forEach {
                        if (it.id in character.selectedModifiers)
                            withStyle(LocalTextStyle.current.copy(textDecoration = TextDecoration.LineThrough).toSpanStyle()) {
                                append(it.modifier.toSignedString())
                            }
                        else
                            append(it.modifier.toSignedString())
                        append(' ')
                    }
                    append('\n')
                }
            }.let {
                if (it.isBlank()) AnnotatedString(stringResource(Res.string.no_modifiers_for_info))
                else it
            })
        }
    }
}

@Composable
private fun ColumnScope.PointBuyModifiersSelector(
    character: DnDModifiersGroup,
    onChange: (DnDModifiersGroup) -> Unit,
    onInfoClick: (stat: Stats) -> Unit,
    allModifiers: List<DnDEntityWithModifiers>,
    selectedModifiers: List<Uuid>,
    onModifierSelected: (DnDModifier) -> Unit
) {
    val textWithRes: (StringResource) -> @Composable () -> Unit = {
        { Text(stringResource(it)) }
    }
    val modifiersSum = calculateBuyingModifiersSum(character.toModifiersList().fastMap { it.modifier })
    Text(
        modifier = Modifier.align(Alignment.CenterHorizontally),
        text = (DnDConstants.BUYING_BALANCE - modifiersSum).toString(),
        style = MaterialTheme.typography.bodyLarge
    )

    Table (
        rows = 7,
        columns = 1
    ) { row, column ->
        if (row == 0 && column == 0) return@Table
        val stat = Stats.entries[row - 1]
        if (column == 0)
            ModifierSelectorRow(
                value = character[stat],
                onValueChange = { onChange(character.set(stat, it)) },
                onInfoClick = { onInfoClick(stat) },
                label = textWithRes(stat.stringRes()),
                minValueCheck = { it >= DnDConstants.MIN_VALUE_TO_BUY },
                maxValueCheck = {
                    DnDConstants.BUYING_BALANCE >= calculateBuyingModifiersSum(
                        character
                            .set(stat, it)
                            .toModifiersList()
                            .fastMap { it.modifier }
                    )
                },
                additionalModifiers = allModifiers.fastFlatMap { it.modifiers }.fastMapNotNull {
                    when {
                        it.stat != stat -> null
                        it.id in selectedModifiers -> null
                        else -> it.modifier
                    }
                }
            )
        else {
            val (entity, _, modifiers) = allModifiers[column - 1]
            if (row == 0) Text(
                text = "${stringResource(entity.type.stringRes())}\n${entity.name}",
                textAlign = TextAlign.Center
            )
            else modifiers.fastFirstOrNull { it.stat == stat }?.let { modifier ->
                RadioButton(
                    selected = modifier.id in selectedModifiers,
                    onClick = { onModifierSelected(modifier) }
                )
            }
        }
    }
}

@Composable
private fun ModifierSelectorRow(
    value: Int,
    onValueChange: (Int) -> Unit,
    minValueCheck: (Int) -> Boolean,
    maxValueCheck: (Int) -> Boolean,
    onInfoClick: () -> Unit,
    additionalModifiers: List<Int> = emptyList(),
    modifier: Modifier = Modifier,
    label: @Composable (() -> Unit)? = null
) {
    Row(modifier = modifier) {
        IconButton(
            onClick = { onValueChange(value - 1) },
            enabled = minValueCheck(value - 1)
        ) {
            Icon(
                painter = painterResource(Res.drawable.remove),
                contentDescription = stringResource(Res.string.decrease_modifier_value)
            )
        }
        IconButton(
            onClick = { onValueChange(value + 1) },
            enabled = maxValueCheck(value + 1)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = stringResource(Res.string.increase_modifier_value)
            )
        }
        OutlinedTextField(
            modifier = Modifier
                .weight(1f),
            label = label,
            singleLine = true,
            value = value.toString(),
            onValueChange = { onValueChange(it.toIntOrNull() ?: value) },
            suffix = {
                Text(
                    text = (calculateModifier(value + additionalModifiers.sum())).toSignedString()
                )
            },
            visualTransformation = SuffixVisualTransformation(
                additionalModifiers.fastJoinToString("") { it.toSignedString() }
            ),
            trailingIcon = {
                IconButton (onClick = onInfoClick) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = stringResource(Res.string.about_modifiers)
                    )
                }
            }
        )
    }
}
