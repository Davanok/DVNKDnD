package com.davanok.dvnkdnd.ui.pages.newEntity.newCharacter.newCharacterMain

import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityMin
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDEntityWithSubEntities
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import okio.Path

sealed interface NewCharacterMainEvent {

    class AddImage(val bytes: ByteArray) : NewCharacterMainEvent
    data class RemoveImage(val path: Path) : NewCharacterMainEvent
    data class SetMainImage(val path: Path?) : NewCharacterMainEvent

    data class SetName(val name: String) : NewCharacterMainEvent
    data class SetDescription(val description: String) : NewCharacterMainEvent

    data class SetClass(
        val cls: DnDEntityWithSubEntities?,
        val subCls: DnDEntityMin? = null
    ) : NewCharacterMainEvent

    data class SetSubClass(val subCls: DnDEntityMin?) : NewCharacterMainEvent

    data class SetRace(
        val race: DnDEntityWithSubEntities?,
        val subRace: DnDEntityMin? = null
    ) : NewCharacterMainEvent

    data class SetSubRace(val subRace: DnDEntityMin?) : NewCharacterMainEvent

    data class SetBackground(
        val background: DnDEntityWithSubEntities?,
        val subBackground: DnDEntityMin? = null
    ) : NewCharacterMainEvent

    data class SetSubBackground(val subBackground: DnDEntityMin?) : NewCharacterMainEvent

    data class OpenSearchSheet(val entityType: DnDEntityTypes, val query: String) : NewCharacterMainEvent
}
