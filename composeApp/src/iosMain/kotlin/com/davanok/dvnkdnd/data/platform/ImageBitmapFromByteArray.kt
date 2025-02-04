package com.davanok.dvnkdnd.data.platform

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asSkiaBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import coil3.asImage
import kotlinx.cinterop.usePinned
import org.jetbrains.skia.EncodedImageFormat
import org.jetbrains.skia.Image
import platform.UIKit.UIImage
import platform.Foundation.NSData

actual fun ImageBitmap.Companion.fromByteArray(bytes: ByteArray) =
    Image.makeFromEncoded(bytes).toComposeImageBitmap()
actual fun ImageBitmap.toByteArray(): ByteArray =
    Image.makeFromBitmap(this.asSkiaBitmap()).encodeToData(EncodedImageFormat.JPEG)!!.bytes
