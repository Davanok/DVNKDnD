package com.davanok.dvnkdnd.ui.pages.newEntity.newItem

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.util.fastRoundToInt

@Composable
fun NewItemScreen(

) {
    var scale by remember { mutableStateOf(1f) }
    var offset by remember { mutableStateOf(Offset.Zero) }
    var rotate by remember { mutableStateOf(0f) }
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        Box(
            modifier = Modifier
                .size(200.dp)
                .offset { IntOffset(offset.x.fastRoundToInt(), offset.y.fastRoundToInt()) }
                .pointerInput(Unit){
                    detectTransformGestures { _, pan, zoom, rotation ->
                        offset += pan
                        scale *= zoom
                        rotate *= rotation
                    }
                }.graphicsLayer {
                    scaleX = scale
                    scaleY = scale
                    rotationZ = rotate
                }.background(MaterialTheme.colorScheme.primary),
        )
    }
}