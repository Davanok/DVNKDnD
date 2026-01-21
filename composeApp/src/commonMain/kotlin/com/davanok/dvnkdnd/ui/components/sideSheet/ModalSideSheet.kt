package com.davanok.dvnkdnd.ui.components.sideSheet

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.AnimationVector1D
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.DraggableAnchors
import androidx.compose.foundation.gestures.Orientation
import androidx.compose.foundation.gestures.anchoredDraggable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.paneTitle
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.lerp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.launch
import kotlin.math.min

@Composable
fun ModalSideSheet(
    onDismissRequest: () -> Unit,
    modifier: Modifier = Modifier,
    sheetState: SheetState = rememberModalSideSheetState(),
    sheetMaxWidth: Dp = SideSheetDefaults.SheetMaxWidth,
    shape: Shape = SideSheetDefaults.ExpandedShape,
    containerColor: Color = SideSheetDefaults.ContainerColor,
    contentColor: Color = contentColorFor(containerColor),
    tonalElevation: Dp = 0.dp,
    scrimColor: Color = SideSheetDefaults.ScrimColor,
    contentWindowInsets: @Composable () -> WindowInsets = { SideSheetDefaults.windowInsets },
    properties: ModalSideSheetProperties = ModalSideSheetDefaults.properties,
    content: @Composable ColumnScope.() -> Unit,
) {
    val scope = rememberCoroutineScope()
    val predictiveBackProgress = remember { Animatable(initialValue = 0f) }

    val animateToDismiss: () -> Unit = {
        scope.launch { sheetState.hide() }.invokeOnCompletion {
            if (!sheetState.isVisible) onDismissRequest()
        }
    }

    // Handle external dismissal when state reaches Hidden (e.g., via swipe)
    LaunchedEffect(sheetState) {
        snapshotFlow { sheetState.isHidden }
            .distinctUntilChanged()
            .drop(1)
            .collect { value ->
                if (value) onDismissRequest()
            }
    }

    // Initial trigger to show the sheet
    LaunchedEffect(Unit) {
        sheetState.show()
    }

    ModalSideSheetDialog(
        properties = properties,
        onDismissRequest = animateToDismiss,
        predictiveBackProgress = predictiveBackProgress,
    ) {
        Box(modifier = Modifier.fillMaxSize().semantics { isTraversalGroup = true }) {
            Scrim(
                color = scrimColor,
                onDismissRequest = animateToDismiss,
                visible = sheetState.targetValue != SideSheetValue.Hidden,
            )
            ModalSideSheetContent(
                predictiveBackProgress = predictiveBackProgress,
                modifier = modifier,
                sheetState = sheetState,
                sheetMaxWidth = sheetMaxWidth,
                shape = shape,
                containerColor = containerColor,
                contentColor = contentColor,
                tonalElevation = tonalElevation,
                contentWindowInsets = contentWindowInsets,
                content = content
            )
        }
    }
}

@Composable
fun ModalSideSheetDialog(
    onDismissRequest: () -> Unit,
    properties: ModalSideSheetProperties,
    predictiveBackProgress: Animatable<Float, AnimationVector1D>,
    content: @Composable () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissRequest,
        properties = DialogProperties(
            dismissOnBackPress = properties.shouldDismissOnBackPress,
            dismissOnClickOutside = properties.shouldDismissOnClickOutside,
            usePlatformDefaultWidth = false
        ),
        content = content
    )
}

@Composable
private fun BoxScope.ModalSideSheetContent(
    predictiveBackProgress: Animatable<Float, AnimationVector1D>,
    sheetState: SheetState,
    sheetMaxWidth: Dp,
    shape: Shape,
    containerColor: Color,
    contentColor: Color,
    tonalElevation: Dp,
    contentWindowInsets: @Composable () -> WindowInsets,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    BoxWithConstraints(modifier = modifier.align(Alignment.CenterEnd)) {
        val fullWidth = constraints.maxWidth.toFloat()

        LaunchedEffect(fullWidth) {
            sheetState.anchoredDraggableState.updateAnchors(
                DraggableAnchors {
                    SideSheetValue.Hidden at fullWidth
                    SideSheetValue.Expanded at 0f
                }
            )
        }

        Surface(
            modifier = Modifier
                .widthIn(max = sheetMaxWidth)
                .fillMaxHeight()
                .anchoredDraggable(
                    state = sheetState.anchoredDraggableState,
                    orientation = Orientation.Horizontal
                )
                .graphicsLayer {
                    val sheetOffset = sheetState.anchoredDraggableState.offset
                    if (!sheetOffset.isNaN()) {
                        translationX = sheetOffset
                    }

                    // Predictive Back Transformations
                    val progress = predictiveBackProgress.value
                    if (progress > 0f) {
                        scaleX =
                            calculatePredictiveBackScale(size.width, progress, isVertical = false)
                        scaleY =
                            calculatePredictiveBackScale(size.height, progress, isVertical = true)
                        transformOrigin = TransformOrigin(0.5f, 0.5f)
                    }
                }
                .semantics {
                    paneTitle = "Side Sheet"
                    traversalIndex = 0f
                },
            shape = shape,
            color = containerColor,
            contentColor = contentColor,
            tonalElevation = tonalElevation
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(contentWindowInsets())
                    .padding(horizontal = 24.dp)
            ) {
                content()
            }
        }
    }
}


private fun calculatePredictiveBackScale(
    availableSize: Float,
    progress: Float,
    isVertical: Boolean
): Float {
    if (availableSize <= 0f) return 1f
    val maxDist = if (isVertical) 24.dp else 48.dp // Using standard Material 3 values
    val pxDist = maxDist.value * 3 // Simplified density conversion or use LocalDensity
    return 1f - (lerp(0f, min(pxDist, availableSize), progress) / availableSize)
}

@Composable
private fun Scrim(
    color: Color,
    onDismissRequest: () -> Unit,
    visible: Boolean
) {
    if (color == Color.Unspecified) return

    val alpha by animateFloatAsState(
        targetValue = if (visible) 1f else 0f,
        animationSpec = tween(),
        label = "ScrimAlpha"
    )

    Canvas(
        Modifier
            .fillMaxSize()
            .pointerInput(onDismissRequest) {
                detectTapGestures { onDismissRequest() }
            }
            .semantics {
                contentDescription = "Dismiss"
            }
    ) {
        drawRect(color = color, alpha = alpha)
    }
}