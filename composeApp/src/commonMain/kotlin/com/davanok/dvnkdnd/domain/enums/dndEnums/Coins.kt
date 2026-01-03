package com.davanok.dvnkdnd.domain.enums.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.copper_pieces_full
import dvnkdnd.composeapp.generated.resources.electrum_pieces_full
import dvnkdnd.composeapp.generated.resources.gold_pieces_full
import dvnkdnd.composeapp.generated.resources.other_pieces_full
import dvnkdnd.composeapp.generated.resources.platinum_pieces_full
import dvnkdnd.composeapp.generated.resources.silver_pieces_full
import org.jetbrains.compose.resources.StringResource

enum class Coins(val stringRes: StringResource) {
    COPPER      (Res.string.copper_pieces_full),
    SILVER      (Res.string.silver_pieces_full),
    ELECTRUM    (Res.string.electrum_pieces_full),
    GOLD        (Res.string.gold_pieces_full),
    PLATINUM    (Res.string.platinum_pieces_full),
    OTHER       (Res.string.other_pieces_full)
}