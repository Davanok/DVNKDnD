package com.davanok.dvnkdnd.database.model

import androidx.compose.ui.util.fastMap
import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation
import com.davanok.dvnkdnd.data.model.entities.character.CharacterFull
import com.davanok.dvnkdnd.data.model.entities.character.CharacterMin
import com.davanok.dvnkdnd.data.model.entities.character.CharacterWithAllModifiers
import com.davanok.dvnkdnd.data.model.entities.character.CharacterWithAllSkills
import com.davanok.dvnkdnd.data.model.entities.DatabaseImage
import com.davanok.dvnkdnd.data.model.entities.character.DnDCharacterHealth
import com.davanok.dvnkdnd.data.model.entities.character.toDnDCoinsGroup
import com.davanok.dvnkdnd.data.model.entities.dndModifiers.toModifiersGroup
import com.davanok.dvnkdnd.database.entities.character.*
import com.davanok.dvnkdnd.database.entities.dndEntities.DnDBaseEntity
import kotlin.uuid.Uuid

fun Character.toCharacterMin() = CharacterMin(
    id = id,
    name = name,
    level = level,
    image = image
)

data class DbCharacterWithAllModifiers(
    @Embedded val character: Character,

    @Relation(parentColumn = "id", entityColumn = "id")
    val characterStats: CharacterStats?,

    @Relation(
        entity = CharacterSelectedModifierBonus::class,
        parentColumn = "id",
        entityColumn = "character_id",
        projection = ["bonus_id"]
    )
    val selectedModifiers: List<Uuid>,

    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CharacterClass::class,
            parentColumn = "character_id",
            entityColumn = "class_id"
        )
    )
    val classesWithModifiers: List<DbEntityWithModifiers>,

    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "race",
        entityColumn = "id"
    )
    val raceModifiers: DbEntityWithModifiers?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "sub_race",
        entityColumn = "id"
    )
    val subRaceModifiers: DbEntityWithModifiers?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "background",
        entityColumn = "id"
    )
    val backgroundModifiers: DbEntityWithModifiers?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "sub_background",
        entityColumn = "id"
    )
    val subBackgroundModifiers: DbEntityWithModifiers?,
) {
    fun toCharacterWithAllModifiers() = CharacterWithAllModifiers(
        character = character.toCharacterMin(),
        characterStats = characterStats?.toModifiersGroup(),
        selectedModifierBonuses = selectedModifiers,
        classes = classesWithModifiers.fastMap { it.toDnDEntityWithModifiers() },
        race = raceModifiers?.toDnDEntityWithModifiers(),
        subRace = subRaceModifiers?.toDnDEntityWithModifiers(),
        background = backgroundModifiers?.toDnDEntityWithModifiers(),
        subBackground = subBackgroundModifiers?.toDnDEntityWithModifiers()
    )
}

data class DbCharacterWithAllSkills(
    @Embedded val character: Character,

    @Relation(
        entity = CharacterSelectedSkill::class,
        parentColumn = "id",
        entityColumn = "character_id",
        projection = ["skill_id"]
    )
    val selectedSkills: List<Uuid>,

    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            value = CharacterClass::class,
            parentColumn = "character_id",
            entityColumn = "class_id"
        )
    )
    val classesWithSkills: List<DbEntityWithSkills>,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "race",
        entityColumn = "id"
    )
    val raceWithSkills: DbEntityWithSkills?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "sub_race",
        entityColumn = "id"
    )
    val subRaceWithSkills: DbEntityWithSkills?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "background",
        entityColumn = "id"
    )
    val backgroundWithSkills: DbEntityWithSkills?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "sub_background",
        entityColumn = "id"
    )
    val subBackgroundWithSkills: DbEntityWithSkills?
) {
    fun toCharacterWithAllSkills() = CharacterWithAllSkills(
        character = character.toCharacterMin(),
        selectedSkills = selectedSkills,
        classes = classesWithSkills.fastMap { it.toDndEntityWithSkills() },
        race = raceWithSkills?.toDndEntityWithSkills(),
        subRace = subRaceWithSkills?.toDndEntityWithSkills(),
        background = backgroundWithSkills?.toDndEntityWithSkills(),
        subBackground = subBackgroundWithSkills?.toDndEntityWithSkills()
    )
}

data class DbFullCharacter(
    @Embedded val character: Character,

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val images: List<CharacterImage>,

    @Relation(parentColumn = "id", entityColumn = "character_id")
    val coins: CharacterCoins?,

    @Relation(parentColumn = "id", entityColumn = "id")
    val stats: CharacterStats?,
    @Relation(parentColumn = "id", entityColumn = "id")
    val health: CharacterHealth?,
    @Relation(parentColumn = "id", entityColumn = "id")
    val usedSpells: CharacterSpellSlots?,

    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            CharacterClass::class,
            parentColumn = "character_id",
            entityColumn = "class_id"
        )
    )
    val classes: List<DbFullEntity>,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            CharacterClass::class,
            parentColumn = "character_id",
            entityColumn = "sub_class_id"
        )
    )
    val subClasses: List<DbFullEntity>,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "race",
        entityColumn = "id"
    )
    val race: DbFullEntity?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "sub_race",
        entityColumn = "id"
    )
    val subRace: DbFullEntity?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "background",
        entityColumn = "id"
    )
    val background: DbFullEntity?,
    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "sub_background",
        entityColumn = "id"
    )
    val subBackground: DbFullEntity?,

    @Relation(
        entity = DnDBaseEntity::class,
        parentColumn = "id",
        entityColumn = "id",
        associateBy = Junction(
            CharacterFeat::class,
            parentColumn = "character_id",
            entityColumn = "feat_id"
        )
    )
    val feats: List<DbFullEntity>,

    @Relation(
        parentColumn = "id",
        entityColumn = "character_id",
    )
    val selectedModifierBonuses: List<CharacterSelectedModifierBonus>,
    @Relation(
        parentColumn = "id",
        entityColumn = "character_id",
    )
    val selectedSkills: List<CharacterSelectedSkill>,
    @Relation(
        parentColumn = "id",
        entityColumn = "character_id",
    )
    val selectedProficiencies: List<CharacterProficiency>
) {
    fun toCharacterFull(): CharacterFull = CharacterFull(
        character = character.toCharacterMin(),
        images = images.fastMap { DatabaseImage(it.id, it.path) },
        coins = coins?.toDnDCoinsGroup(),
        stats = stats?.toModifiersGroup(),
        health = health?.let { DnDCharacterHealth(it.max, it.current, it.temp) },
        usedSpells = usedSpells?.usedSpells.orEmpty(),
        classes = (classes + subClasses).fastMap { it.toDnDFullEntity() },
        race = race?.toDnDFullEntity(),
        subRace = subRace?.toDnDFullEntity(),
        background = background?.toDnDFullEntity(),
        subBackground = subBackground?.toDnDFullEntity(),
        feats = feats.fastMap { it.toDnDFullEntity() },
        selectedModifierBonuses = selectedModifierBonuses.fastMap { it.bonusId },
        selectedSkills = selectedSkills.fastMap { it.skillId },
        selectedProficiencies = selectedProficiencies.fastMap { it.proficiencyId }
    )
}
