package com.davanok.dvnkdnd.data.local.mappers.character

import com.davanok.dvnkdnd.data.local.db.entities.character.DbCharacterSettings
import com.davanok.dvnkdnd.domain.entities.character.CharacterSettings
import kotlin.uuid.Uuid

fun DbCharacterSettings.toCharacterSettings() = CharacterSettings(
    valueModifiersCompactView = valueModifiersCompactView,
    autoLevelChange = autoLevelChange
)
fun CharacterSettings.toDbCharacterSettings(characterId: Uuid) = DbCharacterSettings(
    id = characterId,
    valueModifiersCompactView = valueModifiersCompactView,
    autoLevelChange = autoLevelChange
)