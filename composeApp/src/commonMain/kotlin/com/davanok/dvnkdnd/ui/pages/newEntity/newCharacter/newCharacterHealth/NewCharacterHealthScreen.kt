package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterHealth

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.animateIntAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastCoerceAtLeast
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.data.model.ui.isCritical
import com.davanok.dvnkdnd.data.model.ui.toUiMessage
import com.davanok.dvnkdnd.data.model.util.calculateModifier
import com.davanok.dvnkdnd.ui.components.ErrorCard
import com.davanok.dvnkdnd.ui.components.LoadingCard
import com.davanok.dvnkdnd.ui.components.UiToaster
import com.davanok.dvnkdnd.ui.components.diceRoller.rememberDiceRoller
import com.davanok.dvnkdnd.ui.components.newEntity.NewEntityStepScaffold
import com.davanok.dvnkdnd.ui.components.toSignedString
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.constitution
import dvnkdnd.composeapp.generated.resources.enter_health
import dvnkdnd.composeapp.generated.resources.health_dice_value
import dvnkdnd.composeapp.generated.resources.health_points_short
import dvnkdnd.composeapp.generated.resources.new_character_health_screen_title
import dvnkdnd.composeapp.generated.resources.rolling_in_progress
import dvnkdnd.composeapp.generated.resources.total_health_points_short
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun NewCharacterHealthScreen(
    onBack: () -> Unit,
    onContinue: () -> Unit,
    viewModel: NewCharacterHealthViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    UiToaster(
        message = uiState.error?.toUiMessage(),
        onRemoveMessage = viewModel::removeError
    )

    when {
        uiState.isLoading -> LoadingCard()
        uiState.error.isCritical() -> uiState.error?.let {
            ErrorCard(
                text = it.message,
                exception = it.exception,
                onBack = onBack
            )
        }

        else -> NewEntityStepScaffold(
            modifier = Modifier.fillMaxSize(),
            title = stringResource(Res.string.new_character_health_screen_title),
            additionalContent = {
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        stringResource(
                            Res.string.constitution,
                            uiState.characterConstitution,
                            calculateModifier(uiState.characterConstitution).toSignedString()
                        )
                    )
                    uiState.healthDice?.let {
                        Spacer(Modifier.width(8.dp))
                        Text(
                            stringResource(
                                Res.string.health_dice_value,
                                stringResource(it.stringRes)
                            )
                        )
                    }
                }
            },
            onNextClick = { viewModel.commit(onContinue) },
            onBackClick = onBack,
        ) {
            Content(
                health = uiState.baseHealth,
                onHealthChange = viewModel::setHealth,
                constitutionModifier = calculateModifier(uiState.characterConstitution),
                healthDice = uiState.healthDice
            )
        }
    }
}

@Preview
@Composable
private fun Preview() {
    var health by remember { mutableStateOf(10) }
    Content(
        health = health,
        onHealthChange = { health = it },
        constitutionModifier = 2,
        healthDice = Dices.D10
    )
}

@Composable
private fun Content(
    health: Int,
    onHealthChange: (Int) -> Unit,
    constitutionModifier: Int,
    healthDice: Dices?
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        var isRolling by remember { mutableStateOf(false) }
        val displayedTotal by animateIntAsState(targetValue = health + constitutionModifier)

        val focusManager = LocalFocusManager.current
        val haptic = LocalHapticFeedback.current
        val diceRoller = rememberDiceRoller {
            onHealthChange(it.first().second.first())
            isRolling = false
        }

        OutlinedCard(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth(),
                    value = health.toString().let {
                        TextFieldValue(
                            it,
                            TextRange(it.length)
                        )
                    },
                    onValueChange = {
                        val new = it.text.toIntOrNull() ?: 1
                        if (new > 0) onHealthChange(new)
                    },
                    label = { Text(stringResource(Res.string.health_points_short)) },
                    placeholder = { Text(stringResource(Res.string.enter_health)) },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                )

                Spacer(modifier = Modifier.height(20.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = stringResource(Res.string.total_health_points_short),
                            style = MaterialTheme.typography.labelLarge
                        )
                        Spacer(Modifier.height(4.dp))
                        AnimatedContent(
                            targetState = displayedTotal,
                            transitionSpec = {
                                slideInVertically() + fadeIn() togetherWith
                                        slideOutVertically() + fadeOut()
                            }
                        ) { value ->
                            Text(
                                text = value.fastCoerceAtLeast(1).toString(),
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    fontWeight = FontWeight.Bold
                                ),
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                    if (healthDice != null) {
                        Spacer(modifier = Modifier.width(12.dp))

                        Button(
                            onClick = {
                                isRolling = true
                                diceRoller.roll(healthDice)
                                haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            },
                            enabled = !isRolling
                        ) {
                            if (isRolling) {
                                CircularProgressIndicator(
                                    strokeWidth = 2.dp,
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(Res.string.rolling_in_progress))
                            } else {
                                Icon(
                                    painter = painterResource(healthDice.drawableRes),
                                    contentDescription = stringResource(healthDice.stringRes),
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(stringResource(healthDice.stringRes))
                            }
                        }
                    }
                }
            }
        }
    }
}
