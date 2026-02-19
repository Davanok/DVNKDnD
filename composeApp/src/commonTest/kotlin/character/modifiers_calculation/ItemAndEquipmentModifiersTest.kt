package character.modifiers_calculation

import com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers.calculateValueModifiers
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDEntityTypes
import com.davanok.dvnkdnd.domain.enums.dndEnums.DnDModifierDerivedValuesTargets
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import kotlin.test.Test
import kotlin.test.assertEquals

class ItemAndEquipmentModifiersTest {

    @Test
    fun `Shield adds flat AC bonus`() {
        val shield = testEntity(
            type = DnDEntityTypes.ITEM,
            modifiersGroups = listOf(
                testModifiersGroup(
                    listOf(
                        testModifier(
                            priority = 100,
                            targetScope = ModifierValueTarget.DERIVED_STAT,
                            targetKey = DnDModifierDerivedValuesTargets.ARMOR_CLASS.name,
                            operation = ValueOperation.ADD,
                            sourceType = ValueSourceType.FLAT,
                            flatValue = 2
                        )
                    )
                )
            )
        )

        val character = testCharacter(
            attributes = mapOf(Attributes.DEXTERITY to 10),
            entities = listOf(shield)
        )

        val applied = character.calculateValueModifiers().values

        assertEquals(12, applied.derivedValues.armorClass)
    }

    @Test
    fun `Plate armor SET overrides base AC`() {
        val character = testCharacter(
            attributes = mapOf(Attributes.DEXTERITY to 18),
            modifiers = listOf(
                testModifier(
                    priority = 50,
                    targetScope = ModifierValueTarget.DERIVED_STAT,
                    targetKey = DnDModifierDerivedValuesTargets.ARMOR_CLASS.name,
                    operation = ValueOperation.SET,
                    sourceType = ValueSourceType.FLAT,
                    flatValue = 18
                )
            )
        )

        val applied = character.calculateValueModifiers().values

        assertEquals(18, applied.derivedValues.armorClass)
    }

    @Test
    fun `Light Armor correctly adds Dexterity modifier to base`() {
        // Leather Armor: Base 11
        val leatherArmor = testArmorItem(armorClass = 11, dexMaxModifier = 10, requiredStrength = 0)
        val armorEntity = testEntity(type = DnDEntityTypes.ITEM, modifiersGroups = emptyList(), item = leatherArmor)
        val characterItem = testCharacterItem(equipped = true, entity = armorEntity)

        val character = testCharacter(
            attributes = mapOf(Attributes.DEXTERITY to 16), // Mod +3
            items = listOf(characterItem)
        )

        val applied = character.calculateValueModifiers().values

        // 11 (Armor) + 3 (Dex) = 14
        assertEquals(14, applied.derivedValues.armorClass, "Light armor should be Armor Base + Dex Modifier")
    }

    @Test
    fun `Medium Armor respects Dexterity modifier cap`() {
        // Scale Mail: Base 14, Max Dex 2
        val scaleMail = testArmorItem(armorClass = 14, dexMaxModifier = 2, requiredStrength = 0)
        val armorEntity = testEntity(type = DnDEntityTypes.ITEM, modifiersGroups = emptyList(), item = scaleMail)
        val characterItem = testCharacterItem(equipped = true, entity = armorEntity)

        val character = testCharacter(
            attributes = mapOf(Attributes.DEXTERITY to 20), // Mod +5
            items = listOf(characterItem)
        )

        val applied = character.calculateValueModifiers().values

        // 14 (Armor) + 2 (Capped Dex) = 16
        assertEquals(16, applied.derivedValues.armorClass, "Medium armor should cap the Dex modifier bonus")
    }

    @Test
    fun `Heavy Armor ignores Dexterity modifier`() {
        // Plate Armor: Base 18, Max Dex 0
        val plateArmor = testArmorItem(armorClass = 18, dexMaxModifier = 0, requiredStrength = 15)
        val armorEntity = testEntity(type = DnDEntityTypes.ITEM, modifiersGroups = emptyList(), item = plateArmor)
        val characterItem = testCharacterItem(equipped = true, entity = armorEntity)

        val character = testCharacter(
            attributes = mapOf(Attributes.DEXTERITY to 14), // Mod +2
            items = listOf(characterItem)
        )

        val applied = character.calculateValueModifiers().values

        // Base 18, Dex is ignored
        assertEquals(18, applied.derivedValues.armorClass, "Heavy armor should ignore the Dexterity modifier entirely")
    }

    @Test
    fun `Shields and flat modifiers stack on top of equipped armor`() {
        // 1. Setup Plate Armor (18 AC)
        val plate = testArmorItem(armorClass = 18, dexMaxModifier = 0, requiredStrength = 15)
        val plateEntity = testEntity(type = DnDEntityTypes.ITEM, modifiersGroups = emptyList(), item = plate)

        // 2. Setup Shield (+2 AC via Modifier)
        val shieldMod = testModifier(
            priority = 100,
            targetScope = ModifierValueTarget.DERIVED_STAT,
            targetKey = DnDModifierDerivedValuesTargets.ARMOR_CLASS.name,
            operation = ValueOperation.ADD,
            sourceType = ValueSourceType.FLAT,
            flatValue = 2
        )
        val shieldEntity = testEntity(
            type = DnDEntityTypes.ITEM,
            modifiersGroups = listOf(testModifiersGroup(listOf(shieldMod)))
        )

        val character = testCharacter(
            attributes = mapOf(Attributes.DEXTERITY to 10),
            items = listOf(
                testCharacterItem(equipped = true, entity = plateEntity),
                testCharacterItem(equipped = true, entity = shieldEntity)
            )
        )

        val applied = character.calculateValueModifiers().values

        // 18 (Plate) + 2 (Shield Mod) = 20
        assertEquals(20, applied.derivedValues.armorClass, "Shield modifier should stack on top of base armor class")
    }
}
