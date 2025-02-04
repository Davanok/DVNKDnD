package com.davanok.dvnkdnd.data.platform

import androidx.compose.ui.graphics.ImageBitmap

expect fun ImageBitmap.Companion.fromByteArray(bytes: ByteArray): ImageBitmap
expect fun ImageBitmap.toByteArray(): ByteArray