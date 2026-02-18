package com.davanok.dvnkdnd.ui.pages.editCharacter.pages.editCustomModifierDialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ButtonGroup
import androidx.compose.material3.ButtonGroupDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.core.UiEntry
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageInteractionType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierType
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierRollTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.RollOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import com.davanok.dvnkdnd.domain.enums.dndEnums.targetKeys
import com.davanok.dvnkdnd.ui.components.EntriesDropdown
import com.davanok.dvnkdnd.ui.components.OutlinedDoubleTextField
import com.davanok.dvnkdnd.ui.components.OutlinedIntTextField
import com.davanok.dvnkdnd.ui.components.UiEntriesDropdown
import com.davanok.dvnkdnd.ui.components.adaptive.AdaptiveModalSheet
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.add_character_custom_modifier
import dvnkdnd.composeapp.generated.resources.description
import dvnkdnd.composeapp.generated.resources.edit_character_custom_modifier
import dvnkdnd.composeapp.generated.resources.name
import dvnkdnd.composeapp.generated.resources.save
import dvnkdnd.composeapp.generated.resources.value_modifiers_priority_hint_text
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditCustomModifierDialog(
    customModifier: CharacterCustomModifier?,
    onUpdate: (CharacterCustomModifier) -> Unit,
    onDelete: (CharacterCustomModifier) -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    val state = rememberSaveable(saver = EditCharacterCustomModifierState.Saver) {
        EditCharacterCustomModifierState(customModifier)
    }
    AdaptiveModalSheet(
        onDismissRequest = onDismiss,
        modifier = modifier,
        title = {
            val stringRes =
                if (customModifier == null) Res.string.add_character_custom_modifier
                else Res.string.edit_character_custom_modifier
            Text(text = stringResource(stringRes))
        }
    ) {
        if (customModifier == null) {
            NewDialogContent(
                state = state,
                onUpdate = onUpdate,
            )
        } else {
            EditDialogContent(
                state = state,
                onUpdate = onUpdate,
                onDelete = onDelete
            )
        }
    }
}

@Composable
private fun EditDialogContent(
    state: EditCharacterCustomModifierState,
    onUpdate: (CharacterCustomModifier) -> Unit,
    onDelete: (CharacterCustomModifier) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        CommonContent(
            state = state,
            modifier = Modifier.weight(1f)
        )

        Button(
            modifier = Modifier.align(Alignment.End),
            onClick = { if (state.resultAvailable) onUpdate(state.getResult()) },
            enabled = state.resultAvailable
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(Res.string.save),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.save))
        }
    }
}
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun NewDialogContent(
    state: EditCharacterCustomModifierState,
    onUpdate: (CharacterCustomModifier) -> Unit,
    modifier: Modifier = Modifier
) {
    val modifierTypeLabels = DnDModifierType.entries.associateWith {
        stringResource(it.stringRes)
    }

    Column(modifier = modifier) {
        ButtonGroup(
            overflowIndicator = { ButtonGroupDefaults.OverflowIndicator(it) },
            modifier = Modifier.fillMaxWidth()
        ) {
            DnDModifierType.entries.forEach { type ->
                toggleableItem(
                    checked = state.activeType == type,
                    label = modifierTypeLabels.getValue(type),
                    onCheckedChange = { state.setType(type) },
                    weight = 1f
                )
            }
        }

        CommonContent(
            state = state,
            modifier = Modifier.weight(1f)
        )

        Button(
            onClick = { if (state.resultAvailable) onUpdate(state.getResult()) },
            enabled = state.resultAvailable
        ) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(Res.string.save),
                modifier = Modifier.size(ButtonDefaults.IconSize)
            )
            Spacer(Modifier.width(ButtonDefaults.IconSpacing))
            Text(text = stringResource(Res.string.save))
        }
    }
}

@Composable
private fun CommonContent(
    state: EditCharacterCustomModifierState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier.verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedTextField(
            value = state.name,
            onValueChange = { state.name = it },
            label = { Text(stringResource(Res.string.name)) },
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = state.description,
            onValueChange = { state.description = it },
            label = { Text(stringResource(Res.string.description)) },
            modifier = Modifier.fillMaxWidth()
        )

        HorizontalDivider()

        when (val detail = state.activeDetailState) {
            is ValueModifierState -> EditCustomValueModifierDialogContent(detail)
            is RollModifierState -> EditCustomRollModifierDialogContent(detail)
            is DamageModifierState -> EditCustomDamageModifierDialogContent(detail)
        }
    }
}

@Composable
private fun EditCustomValueModifierDialogContent(
    state: ValueModifierState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Column {
            OutlinedIntTextField(
                value = state.priority,
                onValueChange = { if (it != 0) state.priority = it },
                label = { Text("Priority") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(Modifier.height(8.dp))
            Text(
                text = stringResource(Res.string.value_modifiers_priority_hint_text),
                style = MaterialTheme.typography.bodySmall
            )
        }

        HorizontalDivider()

        // Target Configuration
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UiEntriesDropdown(
                label = { Text("Scope") },
                current = state.targetScope,
                options = ModifierValueTarget.entries,
                onSelected = { state.targetScope = it },
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            state.targetScope.targetKeys()?.let { targetKeys ->
                val options = targetKeys.associate {
                    it.name to if (it is UiEntry) stringResource(it.stringRes) else it.name
                }
                EntriesDropdown(
                    current = state.targetKey,
                    onSelected = { state.targetKey = it },
                    options = options.keys,
                    label = { Text("Target Key") },
                    toString = { options[it].orEmpty() },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        // Operation and Priority
        UiEntriesDropdown(
            label = { Text("Operation") },
            current = state.operation,
            options = ValueOperation.entries,
            onSelected = { state.operation = it },
            modifier = Modifier.weight(1f),
            leadingIcon = {
                Icon(
                    painter = painterResource(state.operation.iconRes),
                    contentDescription = null
                )
            },
            singleLine = true
        )

        HorizontalDivider()

        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            // Source Configuration
            UiEntriesDropdown(
                label = { Text("Value source") },
                current = state.sourceType,
                options = ValueSourceType.entries,
                onSelected = { state.sourceType = it },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            state.sourceType.targetKeys()?.let { targetKeys ->
                val options = targetKeys.associate {
                    it.name to if (it is UiEntry) stringResource(it.stringRes) else it.name
                }
                EntriesDropdown(
                    current = state.targetKey,
                    onSelected = { state.targetKey = it },
                    options = options.keys,
                    label = { Text("Target Key") },
                    toString = { options[it].orEmpty() },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (state.sourceType != ValueSourceType.FLAT) {
                OutlinedDoubleTextField(
                    value = state.multiplier,
                    onValueChange = { state.multiplier = it },
                    label = { Text("Multiplier") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                    modifier = Modifier.weight(1f)
                )
            }

            OutlinedIntTextField(
                value = state.flatValue,
                onValueChange = { state.flatValue = it },
                label = { Text("Flat Bonus/Base") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f)
            )
        }
    }
}
@Composable
private fun EditCustomRollModifierDialogContent(
    state: RollModifierState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            UiEntriesDropdown(
                label = { Text("Target Scope") },
                current = state.targetScope,
                options = ModifierRollTarget.entries,
                onSelected = { state.targetScope = it },
                modifier = Modifier.weight(1f),
                singleLine = true
            )

            state.targetScope.targetKeys()?.let { targetKeys ->
                val options = targetKeys.associate {
                    it.name to if (it is UiEntry) stringResource(it.stringRes) else it.name
                }
                EntriesDropdown(
                    current = state.targetKey,
                    onSelected = { state.targetKey = it },
                    options = options.keys,
                    label = { Text("Target Key") },
                    toString = { options[it].orEmpty() },
                    modifier = Modifier.weight(1f),
                    singleLine = true
                )
            }
        }

        UiEntriesDropdown(
            label = { Text("Roll operation") },
            current = state.operation,
            options = RollOperation.entries,
            onSelected = { state.operation = it },
            singleLine = true
        )
    }
}
@Composable
private fun EditCustomDamageModifierDialogContent(
    state: DamageModifierState,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        UiEntriesDropdown(
            label = { Text("Damage type") },
            current = state.damageType,
            options = DamageTypes.entries,
            onSelected = { state.damageType = it },
            singleLine = true
        )

        UiEntriesDropdown(
            label = { Text("Interaction") },
            current = state.interaction,
            options = DamageInteractionType.entries,
            onSelected = { state.interaction = it },
            singleLine = true
        )
    }
}