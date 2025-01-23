package com.davanok.dvnkdnd.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

@Composable
fun EmptyImage(
    modifier: Modifier = Modifier,
    text: String
) {
    Box(
        modifier = modifier
            .background(MaterialTheme.colorScheme.primaryContainer),
        contentAlignment = Alignment.Center
    ) {
        Text(
            color = MaterialTheme.colorScheme.onPrimaryContainer,
            text = text.first().uppercase()
        )
    }
}