package character.modifiers_calculation

import com.davanok.dvnkdnd.domain.entities.character.CharacterBase
import com.davanok.dvnkdnd.domain.entities.character.CharacterCustomValueModifier
import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.character.CharacterItem
import com.davanok.dvnkdnd.domain.entities.character.CharacterMainEntityInfo
import com.davanok.dvnkdnd.domain.entities.character.CharacterSelectedModifiers
import com.davanok.dvnkdnd.domain.entities.dndEntities.ArmorInfo
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.domain.entities.dndEntities.EntityBase
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullItem
import com.davanok.dvnkdnd.domain.entities.dndEntities.Item
import com.davanok.dvnkdnd.domain.entities.dndEntities.FullRace
import com.davanok.dvnkdnd.domain.entities.dndModifiers.DnDValueModifier
import com.davanok.dvnkdnd.domain.entities.dndModifiers.ModifiersGroup
import com.davanok.dvnkdnd.domain.entities.dndModifiers.toAttributesGroup
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ItemsRarity
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import kotlin.uuid.Uuid

internal fun testModifier(
    priority: Int,
    targetScope: ModifierValueTarget,
    targetKey: String,
    operation: ValueOperation,
    sourceType: ValueSourceType,
    sourceKey: String? = null,
    multiplier: Double = 1.0,
    flatValue: Int = 0
) = DnDValueModifier(
    id = Uuid.random(),
    priority = priority,
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation,
    sourceType = sourceType,
    sourceKey = sourceKey,
    multiplier = multiplier,
    flatValue = flatValue,
    condition = null
)

internal fun testCharacter(
    attributes: Map<Attributes, Int> = emptyMap(),
    modifiers: List<DnDValueModifier> = emptyList(),
    entities: List<DnDFullEntity> = emptyList(),
    items: List<CharacterItem> = emptyList(),
    selectedModifiers: List<Uuid> =
        entities.flatMap { e ->
            e.modifiersGroups.flatMap { g -> g.modifiers.map { it.id } }
        }
): CharacterFull =
    CharacterFull(
        character = CharacterBase(
            id = Uuid.NIL,
            userId = null,
            name = "Test",
            description = "",
            level = 1
        ),
        attributes = attributes.toAttributesGroup(),
        mainEntities = entities.map {
            CharacterMainEntityInfo(
                level = 1,
                entity = it,
                subEntity = null
            )
        },
        items = items,
        selectedModifiers = CharacterSelectedModifiers(
            valueModifiers = selectedModifiers.toSet()
        ),
        customModifiers = modifiers.map { it.toCharacterCustomValueModifier() }
    )

internal fun testEntity(
    type: DnDEntityTypes,
    modifiersGroups: List<ModifiersGroup>,
    item: FullItem? = null,
    race: FullRace? = null
) = DnDFullEntity(
    entity = EntityBase(
        id = Uuid.NIL,
        type = type,
        name = "Test entity",
        description = "",
        source = ""
    ),
    modifiersGroups = modifiersGroups,
    proficiencies = emptyList(),
    features = emptyList(),
    item = item,
    race = race
)

internal fun testModifiersGroup(
    modifiers: List<DnDValueModifier>,
    selectionLimit: Int = 0
) = ModifiersGroup(
    id = Uuid.NIL,
    name = "Test modifiers group",
    description = "",
    selectionLimit = selectionLimit,
    modifiers = modifiers
)
internal fun testCharacterItem(
    equipped: Boolean,
    entity: DnDFullEntity
) = CharacterItem(
    equipped = equipped,
    attuned = true,
    count = null,
    item = entity
)
internal fun testArmorItem(
    armorClass: Int,
    dexMaxModifier: Int,
    requiredStrength: Int
) = FullItem(
    item = Item(
        cost = null,
        weight = null,
        equippable = true,
        rarity = ItemsRarity.COMMON
    ),
    effects = emptyList(),
    activations = emptyList(),
    properties = emptyList(),
    armor = ArmorInfo(
        armorClass = armorClass,
        dexMaxModifier = dexMaxModifier,
        requiredStrength = requiredStrength,
        stealthDisadvantage = false
    ),
    weapon = null
)
private fun DnDValueModifier.toCharacterCustomValueModifier() = CharacterCustomValueModifier(
    id = id,
    name = "Test Modifier",
    description = "",
    priority = priority,
    targetScope = targetScope,
    targetKey = targetKey,
    operation = operation,
    sourceType = sourceType,
    sourceKey = sourceKey,
    multiplier = multiplier,
    flatValue = flatValue,
    condition = condition
)