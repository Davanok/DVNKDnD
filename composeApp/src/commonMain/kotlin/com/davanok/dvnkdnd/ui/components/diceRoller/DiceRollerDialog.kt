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
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastForEach
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.Job
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.launch
import kotlin.random.Random

@Composable
fun DiceRollerDialog(
    state: DiceRollerState,
    diceCompanionContent: @Composable ((AnimationState, Int) -> Unit)? =
        { animationState, value ->
            // default simple companion content
            AnimatedVisibility(
                visible = animationState == AnimationState.Finished,
                enter = fadeIn() + expandVertically(),
                exit = fadeOut() + shrinkVertically(),
            ) {
                Text(value.toString(), modifier = Modifier.padding(bottom = 8.dp))
            }
        },
    groupCompanionContent: @Composable ((AnimationState, List<Int>) -> Unit)? = null,
) {
    state.animRequest?.let { req ->
        DiceRollerDialogImpl(
            animRequest = req,
            onDismiss = state::dismiss,
            animationState = state.animationState,
            onAnimationStateChange = state::setAnimationState,
            diceCompanionContent = diceCompanionContent,
            groupCompanionContent = groupCompanionContent
        )
    }
}

@Composable
private fun DiceRollerDialogImpl(
    animRequest: AnimRequest,
    onDismiss: () -> Unit,
    animationState: AnimationState,
    onAnimationStateChange: (AnimationState) -> Unit,
    diceCompanionContent: @Composable ((AnimationState, Int) -> Unit)?,
    groupCompanionContent: @Composable ((AnimationState, List<Int>) -> Unit)?
) {
    val scope = rememberCoroutineScope()
    var animationJob by remember(animRequest) { mutableStateOf<Job?>(null) }

    val rotAnimsMap = remember(animRequest) {
        animRequest.dices
            .map { it.first }
            .distinct()
            .associateWith { it.previewRotation().toRotAnims() }
    }

    fun startRotationAnimation() {
        if (animationJob?.isActive == true) return

        onAnimationStateChange(AnimationState.Running)

        animationJob = scope.launch {
            val perDiceJobs = mutableListOf<Job>()

            animRequest.dices.fastForEach { (dice, _) ->
                val targetRot = dice.previewRotation()
                val anims = rotAnimsMap.getValue(dice)

                val extraSpinsX = Random.nextInt(2, 6) * 360f + Random.nextFloat() * 90f
                val extraSpinsY = Random.nextInt(1, 5) * 360f + Random.nextFloat() * 90f
                val extraSpinsZ = Random.nextInt(1, 4) * 360f + Random.nextFloat() * 90f

                perDiceJobs += launch {
                    // --- phase 1: spin ---
                    awaitAll(
                        async {
                            anims.x.animateTo(
                                targetRot.x + extraSpinsX,
                                tween(
                                    DiceRollerDefaults.MAIN_SPIN_DURATION_MS,
                                    easing = LinearEasing
                                )
                            )
                        },
                        async {
                            anims.y.animateTo(
                                targetRot.y + extraSpinsY,
                                tween(
                                    DiceRollerDefaults.MAIN_SPIN_DURATION_MS,
                                    easing = LinearEasing
                                )
                            )
                        },
                        async {
                            anims.z.animateTo(
                                targetRot.z + extraSpinsZ,
                                tween(
                                    DiceRollerDefaults.MAIN_SPIN_DURATION_MS,
                                    easing = LinearEasing
                                )
                            )
                        }
                    )

                    // --- phase 2: settle ---
                    val springSpec = spring<Float>(
                        dampingRatio = DiceRollerDefaults.SETTLE_SPRING_DAMPING,
                        stiffness = DiceRollerDefaults.SETTLE_SPRING_STIFFNESS
                    )

                    suspend fun animateToShortest(
                        anim: Animatable<Float, AnimationVector1D>,
                        desired: Float
                    ) {
                        val current = anim.value
                        val curMod = current % 360f
                        val wantMod = desired % 360f
                        val delta = ((wantMod - curMod + 540f) % 360f) - 180f
                        anim.animateTo(current + delta, springSpec)
                        anim.snapTo(desired)
                    }

                    awaitAll(
                        async { animateToShortest(anims.x, targetRot.x) },
                        async { animateToShortest(anims.y, targetRot.y) },
                        async { animateToShortest(anims.z, targetRot.z) }
                    )
                }
            }

            perDiceJobs.joinAll()
        }.also { job ->
            job.invokeOnCompletion { cause ->
                if (cause == null) {
                    onAnimationStateChange(AnimationState.Finished)
                }
            }
        }
    }

    fun skipAnimationToEnd() {
        scope.launch {
            animationJob?.cancelAndJoin()
            animationJob = null

            animRequest.dices.fastForEach { (dice, _) ->
                val target = dice.previewRotation()
                val anims = rotAnimsMap.getValue(dice)
                anims.x.snapTo(target.x)
                anims.y.snapTo(target.y)
                anims.z.snapTo(target.z)
            }

            onAnimationStateChange(AnimationState.Finished)
        }
    }

    val onClick = {
        when (animationState) {
            AnimationState.Idle -> startRotationAnimation()
            AnimationState.Running -> skipAnimationToEnd()
            AnimationState.Finished -> onDismiss()
        }
    }

    Dialog(
        onDismissRequest = onClick,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        val interactionSource = remember { MutableInteractionSource() }
        Card(
            modifier = Modifier.clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
        ) {
            when {
                animRequest.dices.size > 1 ->
                    ManyDicesDialogContent(
                        animationState,
                        animRequest,
                        rotAnimsMap,
                        diceCompanionContent,
                        groupCompanionContent
                    )

                animRequest.dices.first().second.size > 1 -> {
                    val (dice, values) = animRequest.dices.first()
                    SomeDicesDialogContent(
                        animationState,
                        dice,
                        values,
                        rotAnimsMap.getValue(dice).toRotation(),
                        diceCompanionContent,
                        groupCompanionContent
                    )
                }

                else -> {
                    val dice = animRequest.dices.first().first
                    val value = animRequest.dices.first().second.first()
                    val textMeasurer = rememberTextMeasurer()
                    SingleDiceContent(
                        textMeasurer,
                        animationState,
                        dice,
                        value,
                        rotAnimsMap.getValue(dice).toRotation(),
                        diceCompanionContent
                    )
                }
            }
        }
    }
}
