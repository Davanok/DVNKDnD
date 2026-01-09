package com.davanok.dvnkdnd.ui.components.text

import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.davanok.dvnkdnd.domain.entities.character.CoinsGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.Coins
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.character_has_no_money
import dvnkdnd.composeapp.generated.resources.copper_pieces_short
import dvnkdnd.composeapp.generated.resources.electrum_pieces_short
import dvnkdnd.composeapp.generated.resources.gold_pieces_short
import dvnkdnd.composeapp.generated.resources.other_pieces_short
import dvnkdnd.composeapp.generated.resources.platinum_pieces_short
import dvnkdnd.composeapp.generated.resources.silver_pieces_short
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.stringResource

private val Coins.color: Color
    get() = when(this) {
        Coins.COPPER -> Color(0xFFB87333)
        Coins.SILVER -> Color(0xFFC0C0C0)
        Coins.ELECTRUM -> Color(0xFF1ECECA)
        Coins.GOLD -> Color(0xFFFFD700)
        Coins.PLATINUM -> Color(0xFFE5E4E2)
        Coins.OTHER -> Color(0xFFFFFFFF)
    }

private val Coins.shortStringRes: StringResource
    get() = when(this) {
        Coins.COPPER -> Res.string.copper_pieces_short
        Coins.SILVER -> Res.string.silver_pieces_short
        Coins.ELECTRUM -> Res.string.electrum_pieces_short
        Coins.GOLD -> Res.string.gold_pieces_short
        Coins.PLATINUM -> Res.string.platinum_pieces_short
        Coins.OTHER -> Res.string.other_pieces_short
    }

@Composable
fun CoinsGroup.buildString(): String {
    val parts = mapOf(
        Coins.PLATINUM to platinum,
        Coins.GOLD to gold,
        Coins.ELECTRUM to electrum,
        Coins.SILVER to silver,
        Coins.COPPER to copper
    )

    val notZero = parts
        .filterValues { it > 0 }

    return if (notZero.isEmpty())
        stringResource(Res.string.character_has_no_money)
    else {
        notZero
            .map { (coin, count) -> "$count${stringResource(coin.shortStringRes)}" }
            .joinToString(" ")
    }
}
@Composable
fun CoinsGroup.buildAnnotatedString(): AnnotatedString {
    val parts = mapOf(
        Coins.PLATINUM to platinum,
        Coins.GOLD to gold,
        Coins.ELECTRUM to electrum,
        Coins.SILVER to silver,
        Coins.COPPER to copper
    )

    val notZero = parts
        .filterValues { it > 0 }

    return if (notZero.isEmpty())
        AnnotatedString(stringResource(Res.string.character_has_no_money))
    else
        buildAnnotatedString {
            notZero.forEach { (coin, count) ->
                append(count.toString())
                withStyle(style = LocalTextStyle.current.toSpanStyle().copy(color = coin.color)) {
                    append(stringResource(coin.shortStringRes))
                }
                append(' ')
            }
        }
}
