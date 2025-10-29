package com.davanok.dvnkdnd.ui.navigation

import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.ui.components.FullScreenCard
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.back
import org.jetbrains.compose.resources.stringResource

@Composable
fun NotImplementedYetScreen(
    onBack: () -> Unit
) {
    FullScreenCard(
        navButtons = {
            TextButton(onClick = onBack) {
                Text(text = stringResource(Res.string.back))
            }
        }
    ) {
        Text("Not yet implemented")
    }
}