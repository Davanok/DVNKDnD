package com.davanok.dvnkdnd.domain.entities.dndModifiers

import com.davanok.dvnkdnd.domain.enums.dndEnums.Skills

data class SkillsGroup(
    val athletics: Int,
    val acrobatics: Int,
    val sleightOfHands: Int,
    val stealth: Int,
    val arcana: Int,
    val history: Int,
    val investigation: Int,
    val nature: Int,
    val religion: Int,
    val animalHandling: Int,
    val insight: Int,
    val medicine: Int,
    val perception: Int,
    val survival: Int,
    val deception: Int,
    val intimidation: Int,
    val performance: Int,
    val persuasion: Int
) {
    fun toMap() = mapOf(
        Skills.ATHLETICS to athletics,
        Skills.ACROBATICS to acrobatics,
        Skills.SLEIGHT_OF_HAND to sleightOfHands,
        Skills.STEALTH to stealth,
        Skills.ARCANA to arcana,
        Skills.HISTORY to history,
        Skills.INVESTIGATION to investigation,
        Skills.NATURE to nature,
        Skills.RELIGION to religion,
        Skills.ANIMAL_HANDLING to animalHandling,
        Skills.INSIGHT to insight,
        Skills.MEDICINE to medicine,
        Skills.PERCEPTION to perception,
        Skills.SURVIVAL to survival,
        Skills.DECEPTION to deception,
        Skills.INTIMIDATION to intimidation,
        Skills.PERFORMANCE to performance,
        Skills.PERSUASION to persuasion
    )
    fun skills() = listOf(
        athletics,
        acrobatics,
        sleightOfHands,
        stealth,
        arcana,
        history,
        investigation,
        nature,
        religion,
        animalHandling,
        insight,
        medicine,
        perception,
        survival,
        deception,
        intimidation,
        performance,
        persuasion
    )
    operator fun get(key: Skills) = toMap()[key]!!
}

fun AttributesGroup.toSkillsGroup() = SkillsGroup(
    athletics = strength,
    acrobatics = dexterity,
    sleightOfHands = dexterity,
    stealth = dexterity,
    arcana = intelligence,
    history = intelligence,
    investigation = intelligence,
    nature = intelligence,
    religion = intelligence,
    animalHandling = wisdom,
    insight = wisdom,
    medicine = wisdom,
    perception = wisdom,
    survival = wisdom,
    deception = charisma,
    intimidation = charisma,
    performance = charisma,
    persuasion = charisma
)

fun Map<Skills, Int>.toSkillsGroup() = SkillsGroup(
    athletics = get(Skills.ATHLETICS) ?: 0,
    acrobatics = get(Skills.ACROBATICS) ?: 0,
    sleightOfHands = get(Skills.SLEIGHT_OF_HAND) ?: 0,
    stealth = get(Skills.STEALTH) ?: 0,
    arcana = get(Skills.ARCANA) ?: 0,
    history = get(Skills.HISTORY) ?: 0,
    investigation = get(Skills.INVESTIGATION) ?: 0,
    nature = get(Skills.NATURE) ?: 0,
    religion = get(Skills.RELIGION) ?: 0,
    animalHandling = get(Skills.ANIMAL_HANDLING) ?: 0,
    insight = get(Skills.INSIGHT) ?: 0,
    medicine = get(Skills.MEDICINE) ?: 0,
    perception = get(Skills.PERCEPTION) ?: 0,
    survival = get(Skills.SURVIVAL) ?: 0,
    deception = get(Skills.DECEPTION) ?: 0,
    intimidation = get(Skills.INTIMIDATION) ?: 0,
    performance = get(Skills.PERFORMANCE) ?: 0,
    persuasion = get(Skills.PERSUASION) ?: 0
)
