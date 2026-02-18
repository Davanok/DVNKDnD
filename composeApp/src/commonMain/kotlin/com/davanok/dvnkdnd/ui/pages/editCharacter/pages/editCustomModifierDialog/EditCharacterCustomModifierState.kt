package com.davanok.dvnkdnd.ui.pages.editCharacter.pages.editCustomModifierDialog

import androidx.compose.runtime.Stable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.Saver
import androidx.compose.runtime.saveable.listSaver
import androidx.compose.runtime.setValue
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomDamageModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomRollModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomValueModifier
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageInteractionType
import com.davanok.dvnkdnd.domain.enums.dndEnums.DamageTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierType
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierRollTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.RollOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import com.davanok.dvnkdnd.domain.enums.dndEnums.targetKeys
import kotlin.collections.set
import kotlin.uuid.Uuid

@Stable
class EditCharacterCustomModifierState(initialModifier: CharacterCustomModifier? = null) {
    var name by mutableStateOf(initialModifier?.name ?: "")
    var description by mutableStateOf(initialModifier?.description ?: "")
    var condition by mutableStateOf(initialModifier?.condition ?: "")

    var activeType by mutableStateOf(
        when (initialModifier) {
            is CharacterCustomValueModifier -> DnDModifierType.VALUE
            is CharacterCustomRollModifier -> DnDModifierType.ROLL
            is CharacterCustomDamageModifier -> DnDModifierType.DAMAGE
            null -> DnDModifierType.entries.first()
        }
    )

    // Cache to preserve user input when switching back and forth between types
    private val stateCache = mutableMapOf<DnDModifierType, ModifierDetailState>().apply {
        initialModifier?.let {
            val detail = when (it) {
                is CharacterCustomValueModifier -> ValueModifierState(it)
                is CharacterCustomRollModifier -> RollModifierState(it)
                is CharacterCustomDamageModifier -> DamageModifierState(it)
            }
            put(activeType, detail)
        }
    }

    val activeDetailState: ModifierDetailState
        get() = stateCache.getOrPut(activeType) {
            when (activeType) {
                DnDModifierType.VALUE -> ValueModifierState()
                DnDModifierType.ROLL -> RollModifierState()
                DnDModifierType.DAMAGE -> DamageModifierState()
            }
        }

    fun setType(type: DnDModifierType) {
        activeType = type
    }

    val resultAvailable: Boolean by derivedStateOf {
        name.isNotBlank() && activeDetailState.isValid
    }

    fun getResult(): CharacterCustomModifier = activeDetailState.buildModifier(
        name = name,
        description = description,
        condition = condition.ifBlank { null }
    )

    companion object {
        val Saver: Saver<EditCharacterCustomModifierState, *> = listSaver(
            save = { state ->
                listOf(
                    state.name,
                    state.description,
                    state.condition,
                    state.activeType.name
                ) + state.activeDetailState.saveState()
            },
            restore = { list ->
                val type = DnDModifierType.valueOf(list[3] as String)
                val detailData = list.subList(4, list.size)

                EditCharacterCustomModifierState().apply {
                    name = list[0] as String
                    description = list[1] as String
                    condition = list[2] as String
                    activeType = type

                    stateCache[type] = when (type) {
                        DnDModifierType.VALUE -> ValueModifierState.restore(detailData)
                        DnDModifierType.ROLL -> RollModifierState.restore(detailData)
                        DnDModifierType.DAMAGE -> DamageModifierState.restore(detailData)
                    }
                }
            }
        )
    }
}

@Stable
sealed interface ModifierDetailState {
    val isValid: Boolean
    fun buildModifier(
        name: String,
        description: String,
        condition: String?
    ): CharacterCustomModifier

    fun saveState(): List<Any?>
}

@Stable
class ValueModifierState(
    initial: CharacterCustomValueModifier? = null,
    val id: Uuid = initial?.id ?: Uuid.random()
) : ModifierDetailState {
    var priority by mutableIntStateOf(initial?.priority ?: 1)
    var targetScope by mutableStateOf(initial?.targetScope ?: ModifierValueTarget.entries.first())
    var targetKey by mutableStateOf(initial?.targetKey ?: "")
    var operation by mutableStateOf(initial?.operation ?: ValueOperation.entries.first())
    var sourceType by mutableStateOf(initial?.sourceType ?: ValueSourceType.entries.first())
    var sourceKey by mutableStateOf(initial?.sourceKey ?: "")
    var multiplier by mutableDoubleStateOf(initial?.multiplier ?: 1.0)
    var flatValue by mutableIntStateOf(initial?.flatValue ?: 0)

    override val isValid: Boolean by derivedStateOf {
        val scopeValid = targetScope.targetKeys()?.any { it.name == targetKey } ?: true
        val sourceValid = sourceType.targetKeys()?.any { it.name == sourceKey } ?: true
        scopeValid && sourceValid
    }

    override fun buildModifier(name: String, description: String, condition: String?) =
        CharacterCustomValueModifier(
            id = id, name = name, description = description, condition = condition,
            priority = priority, targetScope = targetScope, targetKey = targetKey,
            operation = operation, sourceType = sourceType, sourceKey = sourceKey,
            multiplier = multiplier, flatValue = flatValue
        )

    override fun saveState() = listOf(
        id.toString(), priority, targetScope.name, targetKey,
        operation.name, sourceType.name, sourceKey, multiplier, flatValue
    )

    companion object {
        fun restore(list: List<Any?>) =
            ValueModifierState(id = Uuid.parse(list[0] as String)).apply {
                priority = list[1] as Int
                targetScope = ModifierValueTarget.valueOf(list[2] as String)
                targetKey = list[3] as String
                operation = ValueOperation.valueOf(list[4] as String)
                sourceType = ValueSourceType.valueOf(list[5] as String)
                sourceKey = list[6] as String
                multiplier = list[7] as Double
                flatValue = list[8] as Int
            }
    }
}

@Stable
class RollModifierState(
    initial: CharacterCustomRollModifier? = null,
    val id: Uuid = initial?.id ?: Uuid.random()
) : ModifierDetailState {
    var targetScope by mutableStateOf(initial?.targetScope ?: ModifierRollTarget.entries.first())
    var targetKey by mutableStateOf(initial?.targetKey ?: "")
    var operation by mutableStateOf(initial?.operation ?: RollOperation.entries.first())

    override val isValid: Boolean by derivedStateOf {
        targetScope.targetKeys()?.any { it.name == targetKey } ?: true
    }

    override fun buildModifier(name: String, description: String, condition: String?) =
        CharacterCustomRollModifier(
            id = id, name = name, description = description, condition = condition,
            targetScope = targetScope, targetKey = targetKey, operation = operation
        )

    override fun saveState() = listOf(id.toString(), targetScope.name, targetKey, operation.name)

    companion object {
        fun restore(list: List<Any?>) =
            RollModifierState(id = Uuid.parse(list[0] as String)).apply {
                targetScope = ModifierRollTarget.valueOf(list[1] as String)
                targetKey = list[2] as String
                operation = RollOperation.valueOf(list[3] as String)
            }
    }
}

@Stable
class DamageModifierState(
    initial: CharacterCustomDamageModifier? = null,
    val id: Uuid = initial?.id ?: Uuid.random()
) : ModifierDetailState {
    var damageType by mutableStateOf(initial?.damageType ?: DamageTypes.entries.first())
    var interaction by mutableStateOf(initial?.interaction ?: DamageInteractionType.entries.first())

    override val isValid: Boolean = true

    override fun buildModifier(name: String, description: String, condition: String?) =
        CharacterCustomDamageModifier(
            id = id, name = name, description = description, condition = condition,
            damageType = damageType, interaction = interaction
        )

    override fun saveState() = listOf(id.toString(), damageType.name, interaction.name)

    companion object {
        fun restore(list: List<Any?>) =
            DamageModifierState(id = Uuid.parse(list[0] as String)).apply {
                damageType = DamageTypes.valueOf(list[1] as String)
                interaction = DamageInteractionType.valueOf(list[2] as String)
            }
    }
}