package character.modifiers_calculation

import com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers.calculateValueModifiers
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import kotlin.test.Test
import kotlin.test.assertEquals

class DerivedStatsTest {

    @Test
    fun `Dexterity affects AC and initiative`() {
        val character = testCharacter(
            attributes = mapOf(Attributes.DEXTERITY to 14)
        )

        val applied = character.calculateValueModifiers().values

        assertEquals(12, applied.derivedValues.armorClass)
        assertEquals(2, applied.derivedValues.initiative)
    }

    @Test
    fun `Passive perception reacts to wisdom`() {
        val character = testCharacter(
            attributes = mapOf(Attributes.WISDOM to 14)
        )

        val applied = character.calculateValueModifiers().values

        assertEquals(12, applied.derivedValues.passivePerception)
    }
}
