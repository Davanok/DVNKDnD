package com.davanok.dvnkdnd.ui.pages.characterFull.pages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.VerticalDivider
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers.CalculatedModifiersResult
import com.davanok.dvnkdnd.domain.entities.dndModifiers.SkillsGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import com.davanok.dvnkdnd.ui.components.text.modifiersText.buildPreview
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.attribute
import dvnkdnd.composeapp.generated.resources.saving_throw
import dvnkdnd.composeapp.generated.resources.skills
import dvnkdnd.composeapp.generated.resources.throw_name
import org.jetbrains.compose.resources.stringResource

private const val StatItemMinWidthDp = 200

@Composable
fun CharacterFullAttributesScreen(
    calculationsResult: CalculatedModifiersResult,
    onAttributeClick: (Attributes) -> Unit,
    onSavingThrowClick: (Attributes) -> Unit,
    onSkillClick: (Skills) -> Unit,
    modifier: Modifier = Modifier
) {
    val attributeValues = calculationsResult.values.attributes
    val savingThrowValues = calculationsResult.values.savingThrowModifiers
    val skillsValues = calculationsResult.values.skillModifiers

    var itemsMaxHeight by remember { mutableIntStateOf(0) }
    val density = LocalDensity.current

    LazyVerticalGrid(
        modifier = modifier,
        columns = GridCells.Adaptive(StatItemMinWidthDp.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items = Attributes.entries) { attribute ->
            AttributeGroup(
                modifier = Modifier
                    .fillMaxWidth()
                    .then(
                        if (itemsMaxHeight > 0) Modifier.height(density.run { itemsMaxHeight.toDp() })
                        else Modifier
                    )
                    .onGloballyPositioned {
                        val h = it.size.height
                        if (h > itemsMaxHeight) itemsMaxHeight = h
                    },
                attribute = attribute,
                attributeValue = attributeValues[attribute],
                savingThrowValue = savingThrowValues[attribute],
                skillsValues = skillsValues,
                modifiers = calculationsResult.modifiers,
                onAttributeClick = { onAttributeClick(attribute) },
                onSavingThrowClick = { onSavingThrowClick(attribute) },
                onSkillClick = onSkillClick
            )
        }
    }
}

@Composable
private fun AttributeGroup(
    attribute: Attributes,
    attributeValue: Int,
    savingThrowValue: Int,
    skillsValues: SkillsGroup,
    modifiers: Map<ModifierValueTarget, List<ValueModifierInfo>>,
    onAttributeClick: () -> Unit,
    onSavingThrowClick: () -> Unit,
    onSkillClick: (Skills) -> Unit,
    modifier: Modifier = Modifier
) {
    val attributeModifiers = remember(modifiers, attribute) {
        modifiers[ModifierValueTarget.ATTRIBUTE]
            ?.filter { it.modifier.targetKey == attribute.name }
            .orEmpty()
    }

    val attributeThrowModifiers = remember(modifiers, attribute) {
        modifiers[ModifierValueTarget.ATTRIBUTE_THROW]
            ?.filter { it.modifier.targetKey == attribute.name }
            .orEmpty()
    }

    val savingThrowModifiers = remember(modifiers, attribute) {
        modifiers[ModifierValueTarget.SAVING_THROW]
            ?.filter { it.modifier.targetKey == attribute.name }
            .orEmpty()
    }

    val skillsModifiers = modifiers[ModifierValueTarget.SKILL].orEmpty()

    OutlinedCard(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            // Header
            AttributeItem(
                name = stringResource(attribute.stringRes),
                value = attributeValue.toString(),
                tooltipContent = {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.height(IntrinsicSize.Min)
                    ) {
                        if (attributeModifiers.isNotEmpty()) {
                            ModifiersColumnTooltipContent(
                                modifiers = attributeModifiers,
                                title = stringResource(Res.string.attribute)
                            )
                        }
                        if (attributeModifiers.isNotEmpty() && attributeThrowModifiers.isNotEmpty()) {
                            VerticalDivider()
                        }
                        if (attributeThrowModifiers.isNotEmpty()) {
                            ModifiersColumnTooltipContent(
                                modifiers = attributeThrowModifiers,
                                title = stringResource(Res.string.throw_name)
                            )
                        }
                    }
                },
                tooltipEnabled = attributeModifiers.isNotEmpty() || attributeThrowModifiers.isNotEmpty(),
                onClick = onAttributeClick,
                modifier = Modifier.fillMaxWidth()
            )

            // Saving Throw
            AttributeItem(
                name = stringResource(Res.string.saving_throw),
                value = savingThrowValue.toSignedString(),
                tooltipContent = { ModifiersColumnTooltipContent(savingThrowModifiers) },
                tooltipEnabled = savingThrowModifiers.isNotEmpty(),
                onClick = onSavingThrowClick,
                modifier = Modifier.fillMaxWidth()
            )

            HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

            // Skills
            Text(
                text = stringResource(Res.string.skills),
                style = MaterialTheme.typography.labelMedium
            )

            Spacer(modifier = Modifier.height(8.dp))

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                attribute.skills().fastForEach { skill ->
                    val skillModifiers = remember(skillsModifiers, skill) {
                        skillsModifiers.filter { it.modifier.targetKey == skill.name }
                    }

                    AttributeItem(
                        name = stringResource(skill.stringRes),
                        value = skillsValues[skill].toSignedString(),
                        tooltipContent = { ModifiersColumnTooltipContent(skillModifiers) },
                        tooltipEnabled = skillModifiers.isNotEmpty(),
                        onClick = { onSkillClick(skill) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .defaultMinSize(minHeight = 24.dp)
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AttributeItem(
    name: String,
    value: String,
    tooltipContent: @Composable () -> Unit,
    tooltipEnabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            RichTooltip(text = tooltipContent)
        },
        state = rememberTooltipState(),
        enableUserInput = tooltipEnabled
    ) {
        Row(
            modifier = modifier.clickable(onClick = onClick),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Text(
                modifier = Modifier.weight(1f),
                text = name,
                maxLines = 1
            )
            Text(
                text = value,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun ModifiersColumnTooltipContent(
    modifiers: List<ValueModifierInfo>,
    title: String? = null
) {
    Column {
        if (title != null) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium
            )
            Spacer(Modifier.height(8.dp))
        }
        modifiers.fastForEach { mod ->
            Text(text = mod.buildPreview())
        }
    }
}