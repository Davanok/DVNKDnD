package com.davanok.dvnkdnd.ui.components.diceRoller

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Immutable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.min
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.util.fastMap
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.floor
import kotlin.math.max
import kotlin.math.sqrt
import kotlin.random.Random

interface DiceRoller {
    suspend fun roll(dices: List<Pair<Dices, Int>>) {
        require(dices.isNotEmpty())
        require(dices.all { it.second > 0 })
    }
    suspend fun roll(dice: Dices, count: Int) {
        require(count > 0)
        roll(listOf(dice to count))
    }
    suspend fun roll(dice: Dices) {
        roll(listOf(dice to 1))
    }
}

@Immutable
private data class AnimRequest(
    val dices: List<Pair<Dices, List<Int>>>
) {
    init {
        require(dices.isNotEmpty())
        require(dices.all { it.second.isNotEmpty() })
    }
}

enum class AnimationState { Idle, Running, Finished }

private data class RotAnims(
    val x: Animatable<Float, AnimationVector1D>,
    val y: Animatable<Float, AnimationVector1D>,
    val z: Animatable<Float, AnimationVector1D>
)

private fun Rotation.toRotAnims(): RotAnims = RotAnims(
    x = Animatable(x),
    y = Animatable(y),
    z = Animatable(z)
)

private fun RotAnims.toRotation(): Rotation = Rotation(x = x.value, y = y.value, z = z.value)

private const val MAIN_SPIN_DURATION_MS = 700
private const val SETTLE_SPRING_STIFFNESS = 900f
private const val SETTLE_SPRING_DAMPING = 1.0f
private val DEFAULT_MIN_ITEM_WIDTH = 150.dp
private val DEFAULT_CANVAS_SIZE = 150.dp


@Composable
fun rememberDiceRoller(
    diceCompanionContent: @Composable ((state: AnimationState, value: Int) -> Unit)? = 
        { state, value ->
            AnimatedVisibility(
                visible = state == AnimationState.Finished,
                enter = fadeIn() + expandVertically(),
                exit = shrinkVertically() + fadeOut(),
            ) {
                Text(value.toString(), modifier = Modifier.padding(bottom = 8.dp))
            }
        },
    groupCompanionContent: @Composable ((state: AnimationState, values: List<Int>) -> Unit)? = null,
    onRolled: (List<Pair<Dices, List<Int>>>) -> Unit,
): DiceRoller {
    var animRequest by remember { mutableStateOf<AnimRequest?>(null) }

    // show dialog when there is an animation request
    animRequest?.let { req ->
        DiceDialog(
            animRequest = req,
            onDismiss = {
                onRolled(req.dices)
                animRequest = null
            },
            diceCompanionContent = diceCompanionContent,
            groupCompanionContent = groupCompanionContent
        )
    }

    return remember {
        object : DiceRoller {
            override suspend fun roll(dices: List<Pair<Dices, Int>>) {
                val diceValues = dices.fastMap { (dice, count) ->
                    val maxValue = dice.faces + 1
                    dice to List(count) { Random.nextInt(1, maxValue) }
                }
                animRequest = AnimRequest(diceValues)
            }
        }
    }
}

@Composable
private fun DiceDialog(
    animRequest: AnimRequest,
    onDismiss: () -> Unit,
    diceCompanionContent: @Composable ((state: AnimationState, value: Int) -> Unit)?,
    groupCompanionContent: @Composable ((state: AnimationState, values: List<Int>) -> Unit)?
) {
    var animationState by remember { mutableStateOf(AnimationState.Idle) }
    val scope = rememberCoroutineScope()
    var animationJob by remember { mutableStateOf<Job?>(null) }

    // create animatables per dice type (distinct Dices)
    val rotAnimsMap = remember(animRequest) {
        animRequest.dices
            .fastMap { it.first }
            .distinct()
            .associateWith { it.previewRotation().toRotAnims() }
    }

    fun startRotationAnimation() {
        if (animationJob?.isActive == true) return

        animationJob = scope.launch {
            animationState = AnimationState.Running

            val perDiceJobs = mutableListOf<Job>()

            animRequest.dices.fastForEach { (dice, _) ->
                val targetRot = dice.previewRotation()
                val anims = rotAnimsMap.getValue(dice)

                val extraSpinsX = Random.nextInt(2, 6) * 360f + Random.nextFloat() * 90f
                val extraSpinsY = Random.nextInt(1, 5) * 360f + Random.nextFloat() * 90f
                val extraSpinsZ = Random.nextInt(1, 4) * 360f + Random.nextFloat() * 90f

                val job = launch {
                    // Phase 1: main spin (parallel per axis)
                    val spins = listOf(
                        async {
                            anims.x.animateTo(
                                targetValue = targetRot.x + extraSpinsX,
                                animationSpec = tween(MAIN_SPIN_DURATION_MS, easing = LinearEasing)
                            )
                        },
                        async {
                            anims.y.animateTo(
                                targetValue = targetRot.y + extraSpinsY,
                                animationSpec = tween(MAIN_SPIN_DURATION_MS, easing = LinearEasing)
                            )
                        },
                        async {
                            anims.z.animateTo(
                                targetValue = targetRot.z + extraSpinsZ,
                                animationSpec = tween(MAIN_SPIN_DURATION_MS, easing = LinearEasing)
                            )
                        }
                    )
                    spins.awaitAll()

                    // Phase 2: spring-settle to the final orientation using shortest path
                    val springSpec = spring<Float>(
                        dampingRatio = SETTLE_SPRING_DAMPING,
                        stiffness = SETTLE_SPRING_STIFFNESS
                    )

                    suspend fun animateToShortest(
                        anim: Animatable<Float, AnimationVector1D>,
                        current: Float,
                        desired: Float
                    ) {
                        val curMod = current % 360f
                        val wantMod = desired % 360f
                        val rawDelta = ((wantMod - curMod + 540f) % 360f) - 180f
                        val correctedTarget = current + rawDelta
                        anim.animateTo(correctedTarget, animationSpec = springSpec)
                        anim.snapTo(desired) // ensure exact final angle
                    }

                    awaitAll(
                        async { animateToShortest(anims.x, anims.x.value, targetRot.x) },
                        async { animateToShortest(anims.y, anims.y.value, targetRot.y) },
                        async { animateToShortest(anims.z, anims.z.value, targetRot.z) }
                    )
                }

                perDiceJobs += job
            }

            // wait for all dice animations to finish
            perDiceJobs.joinAll()
            animationState = AnimationState.Finished
            animationJob = null
        }
    }

    fun skipAnimationToEnd() {
        scope.launch {
            // cancel any running animation and snap each animatable to final preview rotation
            runCatching { animationJob?.cancelAndJoin() }
            animRequest.dices.fastForEach { (dice, _) ->
                val target = dice.previewRotation()
                val anims = rotAnimsMap.getValue(dice)
                anims.x.snapTo(target.x)
                anims.y.snapTo(target.y)
                anims.z.snapTo(target.z)
            }
            animationState = AnimationState.Finished
        }
    }

    val onClick = {
        when (animationState) {
            AnimationState.Idle -> startRotationAnimation()
            AnimationState.Running -> skipAnimationToEnd()
            AnimationState.Finished -> onDismiss()
        }
    }

    Dialog(onDismissRequest = onClick, properties = DialogProperties(usePlatformDefaultWidth = false)) {
        val interactionSource = remember { MutableInteractionSource() }
        Card(modifier = Modifier.clickable(interactionSource = interactionSource, indication = null, onClick = onClick)) {
            when {
                animRequest.dices.size > 1 -> 
                    ManyDicesDialogContent(
                        animationState = animationState, 
                        animRequest = animRequest, 
                        rotAnimsMap = rotAnimsMap,
                        diceCompanionContent = diceCompanionContent,
                        groupCompanionContent = groupCompanionContent
                    )
                animRequest.dices.first().second.size > 1 -> {
                    val (dice, values) = animRequest.dices.first()
                    SomeDicesDialogContent(
                        animationState = animationState, 
                        dice = dice,
                        values = values, 
                        rotation = rotAnimsMap.getValue(dice).toRotation(),
                        diceCompanionContent = diceCompanionContent,
                        groupCompanionContent = groupCompanionContent
                    )
                }
                else -> {
                    val dice = animRequest.dices.first().first
                    val value = animRequest.dices.first().second.first()
                    val textMeasurer = rememberTextMeasurer()
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
}

// ---------------------- content composables ----------------------

@Composable
private fun ManyDicesDialogContent(
    animationState: AnimationState,
    animRequest: AnimRequest,
    rotAnimsMap: Map<Dices, RotAnims>,
    diceCompanionContent: @Composable ((state: AnimationState, value: Int) -> Unit)?,
    groupCompanionContent: @Composable ((state: AnimationState, values: List<Int>) -> Unit)?,
    minItemWidth: Dp = DEFAULT_MIN_ITEM_WIDTH
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
            items(
                items = values,
                span = { diceSpan }
            ) { value ->
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
private fun SomeDicesDialogContent(
    animationState: AnimationState,
    dice: Dices,
    values: List<Int>,
    rotation: Rotation,
    diceCompanionContent: @Composable ((state: AnimationState, value: Int) -> Unit)?,
    groupCompanionContent: @Composable ((state: AnimationState, values: List<Int>) -> Unit)?,
    minItemWidth: Dp = DEFAULT_MIN_ITEM_WIDTH
) {
    BoxWithConstraints {
        // columns heuristics: prefer square-ish grid (sqrt of count) but limited by available width
        val columns by remember(values.size, maxWidth) {
            derivedStateOf {
                val byCount = max(1, sqrt(values.size.toDouble()).toInt())
                val byWidth = max(1, floor((maxWidth / minItemWidth).toDouble()).toInt())
                minOf(byCount, byWidth)
            }
        }

        val gridWidth by remember(columns, maxWidth) {
            derivedStateOf {
                min(maxWidth, minItemWidth * columns)
            }
        }

        val textMeasurer = rememberTextMeasurer()
        LazyVerticalGrid(
            modifier = Modifier
                .widthIn(max = gridWidth)
                .align(Alignment.Center),
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
            items(
                items = values,
                span = { diceSpan }
            ) { value ->
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
private fun DicesGroupHeader(
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
private fun SingleDiceContent(
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

        val canvasSize = DEFAULT_CANVAS_SIZE
        Row (
            modifier = Modifier.then(
                if (dice == Dices.D100) Modifier
                    .padding(8.dp)
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(12.dp)
                    )
                else Modifier
            )
        ) {
            Canvas(Modifier.size(canvasSize)) {
                drawDice(
                    dice = dice,
                    rotation = rotation,
                    closestFaceContent = valueToShow?.let { v -> {
                        val text = v.toString().let { vStr ->
                            if (dice != Dices.D100) vStr
                            else vStr.getOrElse(vStr.length - 2) { '0' }.let { "${it}0" }
                        }

                        val layout = textMeasurer.measure(text, style = textStyle)
                        drawText(
                            layout,
                            color = Color.Black,
                            topLeft = Offset(-layout.size.width / 2f, -layout.size.height / 2f)
                        )
                    } }
                )
            }

            if (dice == Dices.D100) {
                Canvas(Modifier.size(canvasSize)) {
                    drawD100(
                        rotation,
                        closestFaceContent = valueToShow?.let { v -> {
                            val text = (v % 10).toString()

                            val layout = textMeasurer.measure(text, style = textStyle)
                            drawText(
                                layout,
                                color = Color.Black,
                                topLeft = Offset(-layout.size.width / 2f, -layout.size.height / 2f)
                            )
                        } }
                    )
                }
            }
        }
        diceCompanionContent?.invoke(animationState, value)
    }
}
