package com.davanok.dvnkdnd.ui.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.text.AnnotatedString
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

fun Int.toSignedString() = if (this > 0) "+$this" else toString()
@Suppress("ComposableNaming")
@Composable
fun AnnotatedString.Builder.append(res: StringResource, vararg args: Any) =
    append(stringResource(res, *args))