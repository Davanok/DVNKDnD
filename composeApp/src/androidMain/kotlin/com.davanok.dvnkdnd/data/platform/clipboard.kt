package com.davanok.dvnkdnd.data.platform

import android.content.ClipData
import androidx.compose.ui.platform.ClipEntry

actual fun clipEntryOf(value: String): ClipEntry =
    ClipEntry(ClipData.newPlainText(value, value))