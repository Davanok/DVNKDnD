package com.davanok.dvnkdnd.domain.enums.dndEnums

import androidx.compose.ui.graphics.Color
import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.artifact_rarity_item
import dvnkdnd.composeapp.generated.resources.common_rarity_item
import dvnkdnd.composeapp.generated.resources.legendary_rarity_item
import dvnkdnd.composeapp.generated.resources.rare_rarity_item
import dvnkdnd.composeapp.generated.resources.uncommon_rarity_item
import dvnkdnd.composeapp.generated.resources.various_rarity_item
import dvnkdnd.composeapp.generated.resources.very_rare_rarity_item
import org.jetbrains.compose.resources.StringResource

enum class ItemsRarity(val stringRes: StringResource, val color: Color) {
    COMMON(Res.string.common_rarity_item, Color.Unspecified),
    UNCOMMON(Res.string.uncommon_rarity_item, Color(0xFF1FC219)),
    RARE(Res.string.rare_rarity_item, Color(0xFF4990E2)),
    VERY_RARE(Res.string.very_rare_rarity_item, Color(0xFF9810E0)),
    LEGENDARY(Res.string.legendary_rarity_item, Color(0xFFFEA227)),
    ARTIFACT(Res.string.artifact_rarity_item, Color(0xFFD10154)),
    VARIOUS(Res.string.various_rarity_item, Color.Unspecified)
}