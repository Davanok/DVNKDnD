package com.davanok.dvnkdnd.domain.enums.dndEnums

import dvnkdnd.composeapp.generated.resources.Res
import dvnkdnd.composeapp.generated.resources.character_movement_type_climb
import dvnkdnd.composeapp.generated.resources.character_movement_type_fly
import dvnkdnd.composeapp.generated.resources.character_movement_type_swim
import dvnkdnd.composeapp.generated.resources.character_movement_type_walk
import org.jetbrains.compose.resources.StringResource


enum class CharacterMovementType(val stringRes: StringResource) {
    WALK(Res.string.character_movement_type_walk),
    FLY(Res.string.character_movement_type_fly),
    SWIM(Res.string.character_movement_type_swim),
    CLIMB(Res.string.character_movement_type_climb)
}