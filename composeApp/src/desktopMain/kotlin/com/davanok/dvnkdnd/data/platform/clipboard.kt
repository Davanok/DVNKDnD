package com.davanok.dvnkdnd.data.platform

import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.platform.ClipEntry
import java.awt.datatransfer.StringSelection

@OptIn(ExperimentalComposeUiApi::class)
actual fun clipEntryOf(value: String): ClipEntry =
    ClipEntry(StringSelection(value))