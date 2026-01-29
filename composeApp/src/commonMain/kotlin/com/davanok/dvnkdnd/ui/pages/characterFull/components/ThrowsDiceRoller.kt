package com.davanok.dvnkdnd.ui.pages.characterFull.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.ChipColors
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.PlainTooltip
import androidx.compose.material3.RichTooltip
import androidx.compose.material3.Text
import androidx.compose.material3.TooltipAnchorPosition
import androidx.compose.material3.TooltipBox
import androidx.compose.material3.TooltipDefaults
import androidx.compose.material3.rememberTooltipState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.AttributesGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroupInfo
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ValueModifierInfo
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import com.davanok.dvnkdnd.ui.components.diceRoller.AnimationState
import com.davanok.dvnkdnd.ui.components.diceRoller.DiceRollerDialog
import com.davanok.dvnkdnd.ui.components.diceRoller.DiceRollerState
import com.davanok.dvnkdnd.ui.components.text.buildPreview
import com.davanok.dvnkdnd.ui.model.UiSelectableState
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.uuid.Uuid

sealed interface ThrowsDiceRollerModifier {
    data class AttributesModifier(val attribute: Attributes) : ThrowsDiceRollerModifier
    data class SavingThrowsModifier(val attribute: Attributes) : ThrowsDiceRollerModifier
    data class SkillsModifier(val skill: Skills) : ThrowsDiceRollerModifier
}

private fun ThrowsDiceRollerModifier.toModifierTarget(): Triple<ModifierValueTarget, String, Attributes> {
    return when (this) {
        is ThrowsDiceRollerModifier.AttributesModifier ->
            Triple(ModifierValueTarget.ATTRIBUTE, attribute.name, attribute)

        is ThrowsDiceRollerModifier.SavingThrowsModifier ->
            Triple(ModifierValueTarget.SAVING_THROW, attribute.name, attribute)

        is ThrowsDiceRollerModifier.SkillsModifier ->
            Triple(ModifierValueTarget.SKILL, skill.name, skill.attribute)
    }
}

private fun resolveModifiers(
    throwType: ThrowsDiceRollerModifier,
    characterModifiedAttributes: AttributesGroup,
    characterModifiers: Map<ModifierValueTarget, List<ValueModifierInfo>>
): List<ValueModifierInfo> {
    val (targetType, targetName, attribute) = throwType.toModifierTarget()

    // Create Base Ability Modifier
    val baseModValue = calculateModifier(characterModifiedAttributes[attribute])

    val baseModifier = ValueModifierInfo(
        modifier = DnDValueModifier(
            id = Uuid.NIL,
            priority = 0, // Ensure base is always first
            targetScope = targetType,
            targetKey = targetName,
            operation = ValueOperation.ADD,
            sourceType = ValueSourceType.FLAT,
            sourceKey = null,
            multiplier = 1.0,
            flatValue = baseModValue,
            condition = null
        ),
        group = ModifiersGroupInfo(
            id = Uuid.NIL,
            name = "",
            description = null,
            selectionLimit = 0
        ),
        resolvedValue = baseModValue,
        state = UiSelectableState(selectable = false, selected = true)
    )

    // Collect external modifiers
    val appliedModifiers = if (targetType == ModifierValueTarget.ATTRIBUTE) {
        emptyList()
    } else {
        characterModifiers[targetType]
            ?.filter { it.modifier.targetKey == targetName }
            ?.sortedBy { it.modifier.priority }
            .orEmpty()
    }

    return (listOf(baseModifier) + appliedModifiers).filter { it.haveEffect() }
}


@Composable
fun CharacterThrowsDiceRoller(
    characterModifiedAttributes: AttributesGroup,
    characterModifiers: Map<ModifierValueTarget, List<ValueModifierInfo>>,
    state: DiceRollerState
) {
    DiceRollerDialog(
        state = state,
        diceCompanionContent = { animState, resultDiceValue, throwSpec ->

            // Validate input
            val throwType =
                throwSpec.modifier as? ThrowsDiceRollerModifier ?: return@DiceRollerDialog

            // Resolve Data
            val modifiers = remember(throwType, characterModifiedAttributes, characterModifiers) {
                resolveModifiers(throwType, characterModifiedAttributes, characterModifiers)
            }

            Column(
                modifier = Modifier.padding(8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                var stepState by remember { mutableIntStateOf(-1) }

                ModifierFlowRow(modifiers = modifiers, activeIndex = stepState)

                AnimatedVisibility(
                    visible = animState == AnimationState.Finished
                ) {
                    val currentValue by produceState(initialValue = resultDiceValue, key1 = resultDiceValue) {
                        modifiers.forEachIndexed { index, mod ->
                            delay(700) // Consider extracting this constant
                            stepState = index
                            value = mod.applyForValue(value)
                        }
                        delay(700)
                        stepState = -1 // Reset highlight after calculation
                    }
                    val animatedValue by animateIntAsState(targetValue = currentValue, label = "RollResult")

                    ResultText(value = animatedValue, modifier = Modifier.padding(top = 8.dp))
                }
            }
        }
    )
}

// -----------------------------------------------------------------------------
// 3. Sub-Composables (UI Components)
// -----------------------------------------------------------------------------


@Composable
private fun ModifierFlowRow(
    modifiers: List<ValueModifierInfo>,
    activeIndex: Int
) {
    FlowRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        modifiers.forEachIndexed { index, modifier ->
            val isActive = index == activeIndex
            ModifierChip(
                modInfo = modifier,
                colors = if (isActive) {
                    AssistChipDefaults.assistChipColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer
                    )
                } else {
                    AssistChipDefaults.assistChipColors()
                }
            )
        }
    }
}

@Composable
private fun ResultText(value: Int, modifier: Modifier = Modifier) {
    AnimatedContent(
        targetState = value,
        transitionSpec = {
            (slideInVertically { height -> height } + fadeIn()) togetherWith
                    (slideOutVertically { height -> -height } + fadeOut())
        },
        label = "ResultTextAnimation"
    ) { targetValue ->
        Text(
            modifier = modifier,
            text = targetValue.toString(),
            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ModifierChip(
    modInfo: ValueModifierInfo,
    colors: ChipColors = AssistChipDefaults.assistChipColors(),
    modifier: Modifier = Modifier
) {
    val tooltipState = rememberTooltipState(isPersistent = true)
    val scope = rememberCoroutineScope()

    val hasTooltipInfo by remember(modInfo) {
        derivedStateOf { modInfo.group.name.isNotBlank() || !modInfo.group.description.isNullOrBlank() }
    }

    TooltipBox(
        positionProvider = TooltipDefaults.rememberTooltipPositionProvider(TooltipAnchorPosition.Above),
        tooltip = {
            if (modInfo.group.description.isNullOrBlank()) {
                PlainTooltip { Text(modInfo.group.name) }
            } else {
                RichTooltip(title = { Text(modInfo.group.name) }) {
                    Text(modInfo.group.description)
                }
            }
        },
        state = tooltipState,
        enableUserInput = false
    ) {
        AssistChip(
            modifier = modifier,
            colors = colors,
            onClick = {
                if (hasTooltipInfo) {
                    scope.launch {
                        if (tooltipState.isVisible)
                            tooltipState.dismiss()
                        else
                            tooltipState.show()
                    }
                }
            },
            label = { Text(text = modInfo.buildPreview()) }
        )
    }
}