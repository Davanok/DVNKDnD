package com.davanok.dvnkdnd.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.rememberTransformableState
import androidx.compose.foundation.gestures.transformable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.withTransform
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import com.davanok.dvnkdnd.data.platform.toByteArray
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.cancel
import dvnkdnd.composeapp.generated.resources.discard_changes
import dvnkdnd.composeapp.generated.resources.finish
import dvnkdnd.composeapp.generated.resources.restart_alt
import dvnkdnd.composeapp.generated.resources.rotate_90_degrees_cw
import dvnkdnd.composeapp.generated.resources.rotate_left
import dvnkdnd.composeapp.generated.resources.rotate_right
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.decodeToImageBitmap
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.min
import kotlin.math.sin

private fun Float.toRadians(): Float = (this * PI / 180).toFloat()

@Stable
private fun Modifier.mirrorHorizontal() = scale(scaleX = -1f, scaleY = 1f)

@Stable
class CropState(
    val imageBitmap: ImageBitmap,
    val boxSizePx: Float,
    initialScale: Float
) {
    var scale by mutableFloatStateOf(initialScale)
    var rotation by mutableFloatStateOf(0f)
    var offset by mutableStateOf(Offset.Zero)

    fun updateZoom(zoomChange: Float) {
        val newScale = (scale * zoomChange).coerceAtLeast(
            boxSizePx / min(
                imageBitmap.width,
                imageBitmap.height
            )
        )
        if (newScale != scale) {
            // Adjust offset to zoom relative to center
            val actualZoom = newScale / scale
            offset = Offset(offset.x * actualZoom, offset.y * actualZoom)
            scale = newScale
            validateOffset()
        }
    }

    fun updateOffset(delta: Offset) {
        // Rotate the delta based on current rotation so drag follows finger
        val angleRad = rotation.toRadians()
        val rotatedDelta = Offset(
            x = delta.x * cos(angleRad) + delta.y * sin(angleRad),
            y = -delta.x * sin(angleRad) + delta.y * cos(angleRad)
        )
        offset += rotatedDelta
        validateOffset()
    }

    private fun validateOffset() {
        val imgWidth = imageBitmap.width * scale
        val imgHeight = imageBitmap.height * scale
        // Bound the offset so the box never sees "empty" space
        val maxX = (imgWidth - boxSizePx) / 2f
        val maxY = (imgHeight - boxSizePx) / 2f
        offset = Offset(
            offset.x.coerceIn(-maxX, maxX),
            offset.y.coerceIn(-maxY, maxY)
        )
    }

    fun reset() {
        scale = boxSizePx / min(imageBitmap.width, imageBitmap.height)
        rotation = 0f
        offset = Offset.Zero
    }
}

@Composable
fun ImageCropDialog(
    bytes: ByteArray,
    boxSize: Dp = 300.dp,
    onResult: (result: ByteArray?) -> Unit
) {
    val scope = rememberCoroutineScope { Dispatchers.IO }
    val imageBitmap by produceState<ImageBitmap?>(initialValue = null, key1 = bytes) {
        scope.launch {
            value = bytes.decodeToImageBitmap()
        }
    }

    Dialog(
        onDismissRequest = { onResult(null) },
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        if (imageBitmap == null)
            CircularProgressIndicator()
        else
            ImageCropDialogContent(
                imageBitmap = imageBitmap!!,
                boxSize = boxSize,
                onResult = { scope.launch { onResult(it?.toByteArray()) } },
                modifier = Modifier.fillMaxSize()
            )
    }
}

@Composable
private fun ImageCropDialogContent(
    imageBitmap: ImageBitmap,
    boxSize: Dp = 300.dp,
    onResult: (result: ImageBitmap?) -> Unit,
    modifier: Modifier = Modifier
) {
    val density = LocalDensity.current
    val boxSizePx = density.run { boxSize.toPx() }

    val cropState = remember(imageBitmap) {
        CropState(
            imageBitmap = imageBitmap,
            boxSizePx = boxSizePx,
            initialScale = boxSizePx / min(imageBitmap.width, imageBitmap.height)
        )
    }
    val state = rememberTransformableState { zoomChange, offsetChange, _ ->
        cropState.updateZoom(zoomChange)
        cropState.updateOffset(offsetChange)
    }
    Box(modifier = modifier) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .transformable(state = state)
        ) {
            // 1. Draw Image with Transformations
            withTransform({
                translate(center.x, center.y)
                rotate(cropState.rotation, pivot = Offset.Zero)
                translate(cropState.offset.x, cropState.offset.y)
                scale(cropState.scale, cropState.scale, pivot = Offset.Zero)
            }) {
                val topLeft = IntOffset(imageBitmap.width, imageBitmap.height) / -2f
                drawImage(
                    imageBitmap,
                    dstOffset = topLeft
                )
            }

            // 2. Draw "Dimmed" Overlay (Hole in the middle)
            val strokeWidthPx = 2.dp.toPx()
            val rectSize = Size(boxSizePx, boxSizePx)
            val rectOffset = Offset(center.x - boxSizePx / 2, center.y - boxSizePx / 2)

            // Dimmed background
            drawPath(
                path = Path().apply {
                    addRect(Rect(Offset.Zero, size))
                    addRect(Rect(rectOffset, rectSize))
                    fillType = PathFillType.EvenOdd
                },
                color = Color.Black.copy(alpha = 0.6f)
            )

            // Border of crop area
            drawRoundRect(
                color = Color.White,
                topLeft = rectOffset,
                cornerRadius = CornerRadius(x = 12.dp.toPx(), y = 12.dp.toPx()),
                size = rectSize,
                style = Stroke(strokeWidthPx)
            )
        }

        Column(
            modifier = Modifier.align(Alignment.BottomCenter),
            verticalArrangement = Arrangement.spacedBy(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RotateButtons(
                onClear = { cropState.reset() },
                onRotate = { cropState.rotation = (cropState.rotation + it) % 360 }
            )
            ControlButtons(
                onCancel = { onResult(null) },
                onResult = {
                    CoroutineScope(Dispatchers.Default).launch {
                        onResult(performCrop(cropState))
                    }
                }
            )
        }
    }
}

private fun performCrop(state: CropState): ImageBitmap {
    val targetSize = state.boxSizePx.toInt()
    val bitmap = ImageBitmap(targetSize, targetSize)
    val paint = Paint()
    Canvas(bitmap).apply {
        save()
        translate(targetSize / 2f, targetSize / 2f)
        rotate(state.rotation)
        scale(state.scale, state.scale)
        translate(state.offset.x / state.scale, state.offset.y / state.scale)
        drawImage(
            state.imageBitmap,
            Offset(-state.imageBitmap.width / 2f, -state.imageBitmap.height / 2f),
            paint
        )
        restore()
    }
    return bitmap
}

@Composable
private fun RotateButtons(
    modifier: Modifier = Modifier,
    onClear: () -> Unit,
    onRotate: (Int) -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = { onRotate(270) }) {
            Icon(
                modifier = Modifier.mirrorHorizontal(),
                painter = painterResource(Res.drawable.rotate_90_degrees_cw),
                contentDescription = stringResource(Res.string.rotate_left)
            )
        }
        IconButton(
            onClick = onClear
        ) {
            Icon(
                painter = painterResource(Res.drawable.restart_alt),
                contentDescription = stringResource(Res.string.discard_changes)
            )
        }
        IconButton(onClick = { onRotate(90) }) {
            Icon(
                painter = painterResource(Res.drawable.rotate_90_degrees_cw),
                contentDescription = stringResource(Res.string.rotate_right)
            )
        }
    }
}

@Composable
private fun ControlButtons(
    modifier: Modifier = Modifier,
    onCancel: () -> Unit,
    onResult: () -> Unit
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        IconButton(onClick = onCancel) {
            Icon(
                imageVector = Icons.Default.Clear,
                contentDescription = stringResource(Res.string.cancel)
            )
        }
        IconButton(onClick = onResult) {
            Icon(
                imageVector = Icons.Default.Check,
                contentDescription = stringResource(Res.string.finish)
            )
        }
    }
}

