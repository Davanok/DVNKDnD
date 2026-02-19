package character.modifiers_calculation

import com.davanok.dvnkdnd.domain.entities.character.characterUtils.calculateModifiers.calculateValueModifiers
import com.davanok.dvnkdnd.domain.enums.dndEnums.Attributes
import com.davanok.dvnkdnd.domain.enums.dndEnums.ModifierValueTarget
import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueOperation
import com.davanok.dvnkdnd.domain.enums.dndEnums.ValueSourceType
import kotlin.test.Test
import kotlin.test.assertEquals

class SkillAndSavingThrowModifiersTest {

    @Test
    fun `Attribute change updates saving throws and skills`() {
        val character = testCharacter(
            attributes = mapOf(Attributes.STRENGTH to 19)
        )

        val applied = character.calculateValueModifiers().values

        assertEquals(4, applied.savingThrowModifiers.strength)
        assertEquals(4, applied.skillModifiers.athletics)
    }

    @Test
    fun `Skill expertise stacks on base skill`() {
        val character = testCharacter(
            attributes = mapOf(Attributes.STRENGTH to 10),
            modifiers = listOf(
                testModifier(
                    priority = 1,
                    targetScope = ModifierValueTarget.ATTRIBUTE,
                    targetKey = Attributes.STRENGTH.name,
                    operation = ValueOperation.SET,
                    sourceType = ValueSourceType.FLAT,
                    flatValue = 20
                ),
                testModifier(
                    priority = 10,
                    targetScope = ModifierValueTarget.SKILL,
                    targetKey = Skills.ATHLETICS.name,
                    operation = ValueOperation.ADD,
                    sourceType = ValueSourceType.FLAT,
                    flatValue = 4
                )
            )
        )

        val applied = character.calculateValueModifiers().values

        assertEquals(9, applied.skillModifiers.athletics)
    }
}
