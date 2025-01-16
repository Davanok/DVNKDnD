package com.davanok.dvnkdnd

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.tooling.preview.Preview
import com.davanok.dvnkdnd.database.entities.character.CharacterMin
import com.davanok.dvnkdnd.ui.pages.charactersList.CharacterCard

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            App()
        }
    }
}

@Preview
@Composable
fun Preview() {
    val character = CharacterMin(
        0,
        "test",
        1,
        null
    )
    CharacterCard(
        character = character,
        onClick = {  }
    )
}

