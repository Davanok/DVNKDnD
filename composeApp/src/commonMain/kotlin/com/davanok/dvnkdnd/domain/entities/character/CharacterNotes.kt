package com.davanok.dvnkdnd.domain.entities.character

import androidx.compose.runtime.Immutable
import kotlinx.serialization.Serializable
import kotlin.uuid.Uuid


private const val MAX_TITLE_LENGTH = 20

@Serializable
@Immutable
data class CharacterNote(
    val id: Uuid,
    val pinned: Boolean = false,
    val tags: Set<String> = emptySet(),
    val text: String = ""
) {
    val title = text
        .lineSequence()
        .takeIf { it.count() > 1 }
        ?.firstOrNull { it.length in 1..MAX_TITLE_LENGTH && it.isNotBlank() }

    val body =
        if (title == null) text
        else text.substringAfter('\n')
}
