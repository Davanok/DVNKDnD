package character.modifiers_calculation

import com.davanok.dvnkdnd.domain.dnd.calculateModifier
import com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers.calculateValueModifiers
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import kotlin.test.Test
import kotlin.test.assertEquals

class AttributeModifiersTest {

    @Test
    fun `SET overrides base attribute`() {
        val character = testCharacter(
            attributes = mapOf(Attributes.STRENGTH to 15),
            modifiers = listOf(
                testModifier(
                    priority = 100,
                    targetScope = ModifierValueTarget.ATTRIBUTE,
                    targetKey = Attributes.STRENGTH.name,
                    operation = ValueOperation.SET,
                    sourceType = ValueSourceType.FLAT,
                    flatValue = 19
                )
            )
        )

        val applied = character.calculateValueModifiers().values

        assertEquals(19, applied.attributes.strength)
        assertEquals(4, calculateModifier(applied.attributes.strength))
    }

    @Test
    fun `SET then ADD applies in priority order`() {
        val character = testCharacter(
            attributes = mapOf(Attributes.STRENGTH to 15),
            modifiers = listOf(
                testModifier(
                    priority = -1,
                    targetScope = ModifierValueTarget.ATTRIBUTE,
                    targetKey = Attributes.STRENGTH.name,
                    operation = ValueOperation.SET,
                    sourceType = ValueSourceType.FLAT,
                    flatValue = 10
                ),
                testModifier(
                    priority = 1,
                    targetScope = ModifierValueTarget.ATTRIBUTE,
                    targetKey = Attributes.STRENGTH.name,
                    operation = ValueOperation.ADD,
                    sourceType = ValueSourceType.FLAT,
                    flatValue = 2
                )
            )
        )

        val applied = character.calculateValueModifiers().values

        assertEquals(12, applied.attributes.strength)
    }
}
