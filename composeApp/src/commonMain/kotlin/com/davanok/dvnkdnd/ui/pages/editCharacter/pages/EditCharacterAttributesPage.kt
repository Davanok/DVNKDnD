package com.davanok.dvnkdnd.ui.pages.editCharacter.pages

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.domain.entities.SpeedValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterModifiedValues
import com.davanok.dvnkdnd.domain.entities.character.CharacterOptionalValues
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.ui.components.NullableOutlinedIntTextField
import com.davanok.dvnkdnd.ui.components.OutlinedIntTextField
import com.davanok.dvnkdnd.ui.components.text.modifiersText.buildPreview
import com.davanok.dvnkdnd.ui.pages.editCharacter.EditCharacterScreenEvent
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.character_armor_class
import dvnkdnd.composeapp.generated.resources.character_initiative
import dvnkdnd.composeapp.generated.resources.character_movement_type_climb
import dvnkdnd.composeapp.generated.resources.character_movement_type_fly
import dvnkdnd.composeapp.generated.resources.character_movement_type_swim
import dvnkdnd.composeapp.generated.resources.character_movement_type_walk
import dvnkdnd.composeapp.generated.resources.decrease_value
import dvnkdnd.composeapp.generated.resources.increase_value
import dvnkdnd.composeapp.generated.resources.overrides
import dvnkdnd.composeapp.generated.resources.proficiency_bonus
import dvnkdnd.composeapp.generated.resources.total_value
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditCharacterAttributesPage(
    character: CharacterFull,
    eventSink: (EditCharacterScreenEvent) -> Unit,
    modifier: Modifier = Modifier
) {
    Content(
        optionalValues = character.optionalValues,
        attributes = character.attributes,
        onAttributeChange = { attribute, value ->
            // Update the specific attribute and send the event
            val updatedAttributes = character.attributes.set(attribute, value)
            eventSink(EditCharacterScreenEvent.UpdateAttributes(updatedAttributes))
        },
        modifiedValues = character.appliedValues.attributes,
        modifiers = character.calculatedValueModifiers[ModifierValueTarget.ATTRIBUTE].orEmpty(),
        modifier = modifier,
        proficiencyBonus = character.proficiencyBonus,
        resolvedValues = character.appliedValues,
        onOptionalValuesChange = { eventSink(EditCharacterScreenEvent.UpdateOptionalValues(it)) }
    )
}

@Composable
private fun Content(
    proficiencyBonus: Int,
    optionalValues: CharacterOptionalValues,
    resolvedValues: CharacterModifiedValues,
    attributes: AttributesGroup,
    onAttributeChange: (Attributes, Int) -> Unit,
    modifiedValues: AttributesGroup,
    modifiers: List<ValueModifierInfo>,
    onOptionalValuesChange: (CharacterOptionalValues) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Attributes.entries.forEach { attribute ->
            val targetModifiers = remember(modifiers) {
                modifiers.filter { it.modifier.targetKey == attribute.name }
            }
            AttributeStepperRow(
                attributeName = stringResource(attribute.stringRes),
                baseValue = attributes[attribute],
                modifiedValue = modifiedValues[attribute],
                onValueChange = { newValue -> onAttributeChange(attribute, newValue) },
                modifiers = targetModifiers
            )
        }

        OptionalValues(
            optionalValues = optionalValues,
            onChange = onOptionalValuesChange,
            resolvedValues = resolvedValues,
            proficiencyBonus = proficiencyBonus
        )
    }
}

@Composable
private fun AttributeStepperRow(
    attributeName: String,
    baseValue: Int,
    modifiedValue: Int,
    onValueChange: (Int) -> Unit,
    modifiers: List<ValueModifierInfo>,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.Bottom,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = attributeName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )

                    if (modifiedValue != baseValue) {
                        Text(
                            text = stringResource(Res.string.total_value, modifiedValue),
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                if (modifiers.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Column(
                        modifier = Modifier.padding(start = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        modifiers.forEach { modifierInfo ->
                            Text(
                                text = "• ${modifierInfo.buildPreview()}",
                                style = MaterialTheme.typography.labelMedium,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                IconButton(onClick = { onValueChange(baseValue - 1) }) {
                    Icon(
                        imageVector = Icons.Default.Remove,
                        contentDescription = stringResource(Res.string.decrease_value, attributeName)
                    )
                }

                Text(
                    text = baseValue.toString(),
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.defaultMinSize(minWidth = 32.dp),
                    textAlign = TextAlign.Center
                )

                IconButton(onClick = { onValueChange(baseValue + 1) }) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = stringResource(Res.string.increase_value, attributeName)
                    )
                }
            }
        }
    }
}

@Composable
private fun OptionalValues(
    optionalValues: CharacterOptionalValues,
    onChange: (CharacterOptionalValues) -> Unit,
    resolvedValues: CharacterModifiedValues,
    proficiencyBonus: Int,
    modifier: Modifier = Modifier
) {
    Surface(
        color = MaterialTheme.colorScheme.surfaceContainer,
        shape = MaterialTheme.shapes.extraLarge,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Text(
                text = stringResource(Res.string.overrides),
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.primary
            )

            NullableOutlinedIntTextField(
                value = optionalValues.proficiencyBonus,
                onValueChange = { onChange(optionalValues.copy(proficiencyBonus = it)) },
                label = {
                    Text(text = stringResource(Res.string.proficiency_bonus))
                },
                placeholder = {
                    Text(text = proficiencyBonus.toString())
                },
                modifier = Modifier.fillMaxWidth()
            )

            NullableOutlinedIntTextField(
                value = optionalValues.initiative,
                onValueChange = { onChange(optionalValues.copy(initiative = it)) },
                label = {
                    Text(text = stringResource(Res.string.character_initiative))
                },
                placeholder = {
                    Text(text = resolvedValues.derivedValues.initiative.toString())
                },
                modifier = Modifier.fillMaxWidth()
            )

            NullableOutlinedIntTextField(
                value = optionalValues.armorClass,
                onValueChange = { onChange(optionalValues.copy(armorClass = it)) },
                label = {
                    Text(text = stringResource(Res.string.character_armor_class))
                },
                placeholder = {
                    Text(text = resolvedValues.derivedValues.armorClass.toString())
                },
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider()

            SpeedSegment(
                speedValues = optionalValues.speedValues ?: SpeedValues.Default,
                resolvedSpeedValues = resolvedValues.speed,
                onSpeedChange = {
                    onChange(optionalValues.copy(speedValues = it))
                }
            )
        }
    }
}

@Composable
private fun SpeedSegment(
    speedValues: SpeedValues,
    resolvedSpeedValues: SpeedValues,
    onSpeedChange: (SpeedValues) -> Unit,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedIntTextField(
            value = speedValues.walk,
            onValueChange = { onSpeedChange(speedValues.copy(walk = it)) },
            label = {
                Text(text = stringResource(Res.string.character_movement_type_walk))
            },
            placeholder = {
                Text(text = resolvedSpeedValues.walk.toString())
            },
            modifier = Modifier.weight(1f)
        )
        OutlinedIntTextField(
            value = speedValues.swim,
            onValueChange = { onSpeedChange(speedValues.copy(swim = it)) },
            label = {
                Text(text = stringResource(Res.string.character_movement_type_swim))
            },
            placeholder = {
                Text(text = resolvedSpeedValues.swim.toString())
            },
            modifier = Modifier.weight(1f)
        )
    }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        OutlinedIntTextField(
            value = speedValues.climb,
            onValueChange = { onSpeedChange(speedValues.copy(climb = it)) },
            label = {
                Text(text = stringResource(Res.string.character_movement_type_climb))
            },
            placeholder = {
                Text(text = resolvedSpeedValues.climb.toString())
            },
            modifier = Modifier.weight(1f)
        )
        OutlinedIntTextField(
            value = speedValues.fly,
            onValueChange = { onSpeedChange(speedValues.copy(fly = it)) },
            label = {
                Text(text = stringResource(Res.string.character_movement_type_fly))
            },
            placeholder = {
                Text(text = resolvedSpeedValues.fly.toString())
            },
            modifier = Modifier.weight(1f)
        )
    }
}
