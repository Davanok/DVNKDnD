package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.util.fastForEach
import com.davanok.dvnkdnd.domain.enums.dndEnums.Dices
import org.jetbrains.compose.resources.stringResource
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sqrt

@Composable
fun ManyDicesDialogContent(
    animationState: AnimationState,
    animRequest: AnimRequest,
    rotAnimsMap: Map<Dices, RotAnims>,
    diceCompanionContent: @Composable ((state: AnimationState, value: Int) -> Unit)?,
    groupCompanionContent: @Composable ((state: AnimationState, values: List<Int>) -> Unit)?,
    minItemWidth: Dp = DiceRollerDefaults.DEFAULT_MIN_ITEM_WIDTH
) {
    val textMeasurer = rememberTextMeasurer()
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minItemWidth),
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        animRequest.dices.fastForEach { (dice, values) ->
            stickyHeader {
                DicesGroupHeader(
                    animationState = animationState,
                    dice = dice,
                    values = values,
                    groupCompanionContent = groupCompanionContent
                )
            }
            val diceSpan = GridItemSpan(if (dice == Dices.D100) 2 else 1)
            items(items = values, span = { diceSpan }) { value ->
                SingleDiceContent(
                    textMeasurer = textMeasurer,
                    animationState = animationState,
                    dice = dice,
                    value = value,
                    rotation = rotAnimsMap.getValue(dice).toRotation(),
                    diceCompanionContent = diceCompanionContent
                )
            }
        }
    }
}

@Composable
fun SomeDicesDialogContent(
    animationState: AnimationState,
    dice: Dices,
    values: List<Int>,
    rotation: Rotation,
    diceCompanionContent: @Composable ((state: AnimationState, value: Int) -> Unit)?,
    groupCompanionContent: @Composable ((state: AnimationState, values: List<Int>) -> Unit)?,
    minItemWidth: Dp = DiceRollerDefaults.DEFAULT_MIN_ITEM_WIDTH
) {
    BoxWithConstraints {
        val columns by remember(values.size, maxWidth) {
            derivedStateOf {
                val byCount = max(1, sqrt(values.size.toDouble()).toInt())
                val byWidth = max(1, floor((maxWidth / minItemWidth).toDouble()).toInt())
                minOf(byCount, byWidth)
            }
        }

        val gridWidth by remember(columns, maxWidth) {
            derivedStateOf { min(maxWidth, minItemWidth * columns) }
        }

        val textMeasurer = rememberTextMeasurer()
        LazyVerticalGrid(
            modifier = Modifier.widthIn(max = gridWidth).align(Alignment.Center),
            columns = GridCells.Fixed(columns)
        ) {
            stickyHeader {
                DicesGroupHeader(
                    animationState = animationState,
                    dice = dice,
                    values = values,
                    groupCompanionContent = groupCompanionContent
                )
            }
            val diceSpan = GridItemSpan(if (dice == Dices.D100) 2 else 1)
            items(items = values, span = { diceSpan }) { value ->
                SingleDiceContent(
                    textMeasurer = textMeasurer,
                    animationState = animationState,
                    dice = dice,
                    value = value,
                    rotation = rotation,
                    diceCompanionContent = diceCompanionContent
                )
            }
        }
    }
}

@Composable
fun DicesGroupHeader(
    modifier: Modifier = Modifier,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceContainer,
    animationState: AnimationState,
    dice: Dices,
    values: List<Int>,
    groupCompanionContent: @Composable ((state: AnimationState, values: List<Int>) -> Unit)?
) {
    Surface(modifier = modifier, color = backgroundColor) {
        Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
            Row {
                Text(text = stringResource(dice.stringRes))
                Spacer(Modifier.width(16.dp))
                AnimatedVisibility(visible = animationState == AnimationState.Finished) {
                    Text(values.sum().toString())
                }
            }
            groupCompanionContent?.invoke(animationState, values)
        }
    }
}

@Composable
fun SingleDiceContent(
    textMeasurer: TextMeasurer,
    animationState: AnimationState,
    dice: Dices,
    value: Int,
    rotation: Rotation,
    diceCompanionContent: @Composable ((state: AnimationState, value: Int) -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val valueToShow = when (animationState) {
        AnimationState.Idle -> dice.faces
        AnimationState.Running -> null
        AnimationState.Finished -> value
    }

    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        val textStyle = MaterialTheme.typography.titleMedium

        val canvasSize = DiceRollerDefaults.DEFAULT_CANVAS_SIZE
        Row(
            modifier = if (dice == Dices.D100) Modifier
                .padding(8.dp)
                .border(1.dp, MaterialTheme.colorScheme.primary, MaterialTheme.shapes.medium)
            else Modifier
        ) {
            Canvas(Modifier.size(canvasSize)) {
                drawDice(
                    dice = dice,
                    rotation = rotation,
                    closestFaceContent = valueToShow?.let { v ->
                        {
                            val text =
                                if (dice != Dices.D100) v.toString() else v.toString().let { s ->
                                    s.getOrElse(s.length - 2) { '0' }.let { "${it}0" }
                                }
                            val layout = textMeasurer.measure(text, style = textStyle)
                            drawText(
                                layout,
                                color = Color.Black,
                                topLeft = Offset(-layout.size.width / 2f, -layout.size.height / 2f)
                            )
                        }
                    }
                )
            }

            if (dice == Dices.D100) {
                Canvas(Modifier.size(canvasSize)) {
                    drawD100(rotation, closestFaceContent = valueToShow?.let { v ->
                        {
                            val text = (v % 10).toString()
                            val layout = textMeasurer.measure(text, style = textStyle)
                            drawText(
                                layout,
                                color = Color.Black,
                                topLeft = Offset(-layout.size.width / 2f, -layout.size.height / 2f)
                            )
                        }
                    })
                }
            }
        }

        diceCompanionContent?.invoke(animationState, value)
    }
}
