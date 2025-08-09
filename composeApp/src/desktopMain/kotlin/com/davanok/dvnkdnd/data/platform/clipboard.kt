package com.davanok.dvnkdnd.data.platform

import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

actual fun clipEntryOf(value: String): ClipEntry =
    ClipEntry(StringSelection(value))