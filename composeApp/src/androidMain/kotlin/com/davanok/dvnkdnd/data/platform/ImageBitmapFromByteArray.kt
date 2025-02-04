package com.davanok.dvnkdnd.data.platform

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asAndroidBitmap
import androidx.compose.ui.graphics.asImageBitmap
import java.io.ByteArrayOutputStream

actual fun ImageBitmap.Companion.fromByteArray(bytes: ByteArray): ImageBitmap =
    BitmapFactory.decodeByteArray(bytes, 0, bytes.size).asImageBitmap()
actual fun ImageBitmap.toByteArray(): ByteArray =
    ByteArrayOutputStream().use {
        asAndroidBitmap().compress(Bitmap.CompressFormat.JPEG, 100, it)
        it.toByteArray()
    }
