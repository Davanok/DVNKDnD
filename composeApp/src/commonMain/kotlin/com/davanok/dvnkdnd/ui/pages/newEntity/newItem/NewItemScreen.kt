package com.davanok.dvnkdnd.ui.pages.newEntity.newItem

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.unit.dp
import com.davanok.dvnkdnd.data.model.dndEnums.Dices
import com.davanok.dvnkdnd.ui.components.diceRoller.rememberDiceRoller
import io.github.aakira.napier.Napier
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewItemScreen() {
    val dicesToRoll = remember { mutableStateMapOf<Dices, Int>() }
    val diceRoller = rememberDiceRoller {
        Napier.d { it.toString() }
        dicesToRoll.clear()
    }
    Scaffold (
        topBar = {
            TopAppBar(title = { Text("dice roller") })
        }
    ) { paddingValues ->
        Column (
            modifier = Modifier.padding(paddingValues).verticalScroll(rememberScrollState())
        ) {
            Dices.entries.forEach { dice ->
                Row (
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            dicesToRoll[dice] = dicesToRoll.getOrElse(dice) { 0 } + 1
                        }
                    ) {
                        Image(
                            painter = painterResource(dice.drawableRes),
                            contentDescription = null,
                            colorFilter = ColorFilter.tint(LocalContentColor.current)
                        )
                        Text(
                            text = stringResource(dice.stringRes)
                        )
                    }

                    Text(text = dicesToRoll.getOrElse(dice) { 0 }.toString())
                }
            }
            OutlinedButton(
                onClick = dicesToRoll::clear
            ) {
                Text("clear")
            }
            Spacer(Modifier.height(16.dp))
            Button(
                onClick = { diceRoller.roll(dicesToRoll.toList()) },
                enabled = dicesToRoll.isNotEmpty()
            ) {
                Text(text = "roll")
            }
        }
    }
}