package com.davanok.dvnkdnd.ui.pages.editCharacter.pages

import androidx.compose.foundation.hoverable
import androidx.compose.foundation.indication
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Restore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.core.utils.groupByNotNull
import com.davanok.dvnkdnd.domain.dnd.proficiencyBonusByLevel
import com.davanok.dvnkdnd.domain.entities.SpeedValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterModifiedValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.entities.toMap
import com.davanok.dvnkdnd.domain.entities.update
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.CharacterMovementType
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.ui.components.text.modifiersText.buildPreview
import com.davanok.dvnkdnd.ui.components.textFields.OutlinedNullableIntTextField
import com.davanok.dvnkdnd.ui.pages.editCharacter.EditCharacterScreenEvent
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.armor_class
import dvnkdnd.composeapp.generated.resources.attributes_header
import dvnkdnd.composeapp.generated.resources.decrease_value
import dvnkdnd.composeapp.generated.resources.increase_value
import dvnkdnd.composeapp.generated.resources.movement_speed
import dvnkdnd.composeapp.generated.resources.overrides
import dvnkdnd.composeapp.generated.resources.proficiency_bonus
import dvnkdnd.composeapp.generated.resources.remove_speed_overrides
import dvnkdnd.composeapp.generated.resources.reset_value
import dvnkdnd.composeapp.generated.resources.value_by_default
import org.jetbrains.compose.resources.stringResource

@Immutable
data class UiState(
    val baseAttributes: AttributesGroup,
    val attributeModifiers: Map<Attributes, List<ValueModifierInfo>>,
    val optionalValues: CharacterOptionalValues,
    val resolvedValues: CharacterModifiedValues,
    val characterBaseProficiencyBonus: Int
)

private fun CharacterFull.toUiState() = UiState(
    baseAttributes = attributes,
    attributeModifiers = calculatedValueModifiers[ModifierValueTarget.ATTRIBUTE]
        ?.groupByNotNull { it.targetAs<Attributes>() }
        .orEmpty(),
    optionalValues = optionalValues,
    resolvedValues = appliedValues,
    characterBaseProficiencyBonus = proficiencyBonusByLevel(character.level)
)

@Composable
fun EditCharacterAttributesPage(
    character: CharacterFull,
    eventSink: (EditCharacterScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState = remember(character) { character.toUiState() }
    Content(
        uiState = uiState, // Pass character or specific properties for better stability
        onAttributeChange = { attribute, value ->
            val updatedAttributes = character.attributes.set(attribute, value)
            eventSink(EditCharacterScreenEvent.UpdateAttributes(updatedAttributes))
        },
        onOptionalValuesChange = { eventSink(EditCharacterScreenEvent.UpdateOptionalValues(it)) },
        modifier = modifier
    )
}

@Composable
private fun Content(
    uiState: UiState,
    onAttributeChange: (Attributes, Int) -> Unit,
    onOptionalValuesChange: (CharacterOptionalValues) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    var itemsMaxHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // --- Attributes Section ---
        Text(
            text = stringResource(Res.string.attributes_header), // Use a header
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        FlowRow(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            maxItemsInEachRow = 3,
            maxLines = 3
        ) {
            Attributes.entries.forEach { attribute ->
                AttributeCard(
                    attribute = attribute,
                    baseValue = uiState.baseAttributes[attribute],
                    modifiedValue = uiState.resolvedValues.attributes[attribute],
                    modifiers = uiState.attributeModifiers[attribute].orEmpty(),
                    onValueChange = { onAttributeChange(attribute, it) },
                    modifier = Modifier.weight(1f)
                        .then(
                            if (itemsMaxHeight > 0) Modifier.height(density.run { itemsMaxHeight.toDp() })
                            else Modifier
                        )
                        .onGloballyPositioned {
                            val h = it.size.height
                            if (h > itemsMaxHeight) itemsMaxHeight = h
                        }
                )
            }
        }

        Text(
            text = stringResource(Res.string.overrides),
            style = MaterialTheme.typography.headlineSmall,
            fontWeight = FontWeight.Bold
        )

        // --- Overrides / Optional Values Section ---
        OptionalValuesSection(
            optionalValues = uiState.optionalValues,
            resolvedValues = uiState.resolvedValues,
            proficiencyBonus = uiState.characterBaseProficiencyBonus,
            onChange = onOptionalValuesChange
        )
    }
}

@Composable
private fun AttributeCard(
    attribute: Attributes,
    baseValue: Int,
    modifiedValue: Int,
    modifiers: List<ValueModifierInfo>,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val currentValue by rememberUpdatedState(baseValue)
    val attributeName = stringResource(attribute.stringRes)
    val interactionSource = remember { MutableInteractionSource() }

    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp).align(Alignment.CenterHorizontally),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = attributeName,
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.secondary
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = modifiedValue.toString(),
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )

            // Stepper Control
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(CircleShape)
                    .indication(interactionSource, ripple())
                    .hoverable(interactionSource)
                    .pointerHoverIcon(PointerIcon.Hand)
                    .pointerInput(Unit) {
                        awaitPointerEventScope {
                            var accumulator = 0f

                            while (true) {
                                val event = awaitPointerEvent()

                                if (event.type == PointerEventType.Scroll) {
                                    event.changes.forEach { change ->
                                        accumulator += change.scrollDelta.y

                                        if (accumulator <= -1f) {
                                            onValueChange(currentValue + 1)
                                            accumulator = 0f
                                        } else if (accumulator >= 1f) {
                                            onValueChange(currentValue - 1)
                                            accumulator = 0f
                                        }

                                        change.consume()
                                    }
                                }
                            }
                        }
                    },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                IconButton(onClick = { onValueChange(baseValue - 1) }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = stringResource(
                            Res.string.decrease_value,
                            attributeName
                        )
                    )
                }
                Text(
                    text = baseValue.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                )
                IconButton(onClick = { onValueChange(baseValue + 1) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(
                            Res.string.increase_value,
                            attributeName
                        )
                    )
                }
            }

            // Show active modifiers as small badges
            if (modifiers.isNotEmpty()) {
                HorizontalDivider()
                modifiers.forEach { info ->
                    Text(
                        text = info.buildPreview(),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionalValuesSection(
    optionalValues: CharacterOptionalValues,
    resolvedValues: CharacterModifiedValues,
    proficiencyBonus: Int,
    onChange: (CharacterOptionalValues) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Core Overrides in a Row
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedOverridableIntTextField(
                    baseValue = proficiencyBonus,
                    overriddenValue = optionalValues.proficiencyBonus,
                    onValueChange = { onChange(optionalValues.copy(proficiencyBonus = it)) },
                    label = { Text(stringResource(Res.string.proficiency_bonus), maxLines = 1) },
                    modifier = Modifier.weight(1f)
                )
                OutlinedOverridableIntTextField(
                    baseValue = resolvedValues.derivedValues.armorClass,
                    overriddenValue = optionalValues.armorClass,
                    onValueChange = { onChange(optionalValues.copy(armorClass = it)) },
                    label = { Text(stringResource(Res.string.armor_class), maxLines = 1) },
                    modifier = Modifier.weight(1f)
                )
            }
            Spacer(Modifier.height(8.dp))

            MovementSpeedSection(
                resolvedSpeed = resolvedValues.speed,
                overriddenSpeed = optionalValues.speedValues,
                onOverriddenSpeedChange = {
                    onChange(optionalValues.copy(speedValues = it))
                }
            )
        }
    }
}

@Composable
private fun MovementSpeedSection(
    resolvedSpeed: SpeedValues,
    overriddenSpeed: SpeedValues?,
    onOverriddenSpeedChange: (SpeedValues?) -> Unit,
    modifier: Modifier = Modifier
) {
    val resolvedSpeedMap = resolvedSpeed.toMap()
    val overriddenSpeedMap = overriddenSpeed?.toMap()

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(Res.string.movement_speed),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.weight(1f)
            )

            IconButton(
                onClick = { onOverriddenSpeedChange(null) }
            ) {
                Icon(
                    imageVector = Icons.Default.Restore,
                    contentDescription = stringResource(Res.string.remove_speed_overrides)
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        // Using a FlowRow (from Foundation) allows these to wrap on small screens
        FlowRow(
            maxItemsInEachRow = 2,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            CharacterMovementType.entries.forEach { type ->
                OutlinedOverridableIntTextField(
                    baseValue = resolvedSpeedMap[type] ?: 0,
                    overriddenValue = overriddenSpeedMap?.get(type),
                    onValueChange = {
                        val base = overriddenSpeed ?: SpeedValues.Default
                        onOverriddenSpeedChange(base.update(type, it ?: 0))
                    },
                    label = { Text(stringResource(type.stringRes), maxLines = 1) },
                    modifier = Modifier.weight(1f)
                )
            }
        }
    }
}

@Composable
private fun OutlinedOverridableIntTextField(
    baseValue: Int,
    overriddenValue: Int?,
    onValueChange: (Int?) -> Unit,
    label: @Composable () -> Unit,
    modifier: Modifier = Modifier
) {
    val isOverridden = overriddenValue != null

    OutlinedNullableIntTextField(
        value = overriddenValue,
        onValueChange = onValueChange,
        label = label,
        placeholder = if (isOverridden) null else {
            { Text(baseValue.toString()) }
        },
        supportingText = {
            Text(stringResource(Res.string.value_by_default, baseValue))
        },
        trailingIcon = if (!isOverridden) null else {
            {
                IconButton(
                    onClick = { onValueChange(null) }
                ) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = stringResource(Res.string.reset_value)
                    )
                }
            }
        },
        modifier = modifier
    )
}