package com.davanok.dvnkdnd.domain.entities.character.characterUtils

import com.davanok.dvnkdnd.domain.entities.character.CharacterFull
import com.davanok.dvnkdnd.domain.entities.dndEntities.DnDFullEntity
import kotlin.uuid.Uuid

fun CharacterFull.getEntitiesWithLevel(): List<Pair<DnDFullEntity, Int>> {
    // 1. Define a recursive visitor function
    // We use a Set to prevent infinite loops (e.g., A -> B -> A)
    val visited = mutableSetOf<Uuid>()

    fun collectRecursive(
        entity: DnDFullEntity,
        effectiveLevel: Int
    ): Sequence<Pair<DnDFullEntity, Int>> = sequence {
        // Guard against cycles
        if (!visited.add(entity.entity.id)) return@sequence

        // Yield the current entity
        yield(entity to effectiveLevel)

        // 2. Process children (Abilities)
        // We map abilities to the actual entity data located in 'companionEntities'
        val features = entity.features
            .asSequence()
            .filter { it.level <= effectiveLevel } // Only include unlocked abilities
            .mapNotNull { featureLink ->
                entity.companionEntities.firstOrNull { it.entity.id == featureLink.featureId }
            }

        features.forEach { childEntity ->
            // 3. Recurse with the parent's effective level
            yieldAll(collectRecursive(childEntity, effectiveLevel))
        }
    }

    // 4. Collect Roots and flattening structure
    val result = sequence {
        // -- Root: Main Entities (Classes/Subclasses, Races/Subraces, Backgrounds/Sub backgrounds) --
        // Uses specific level
        mainEntities.forEach { main ->
            yieldAll(collectRecursive(main.entity, main.level))
            main.subEntity?.let { sub ->
                yieldAll(collectRecursive(sub, main.level))
            }
        }

        // -- Root: Feats --
        // Uses total character level
        feats.forEach { feat ->
            yieldAll(collectRecursive(feat, character.level))
        }

        // -- Root: States --
        // Uses total character level
        states.forEach { state ->
            yieldAll(collectRecursive(state.state, character.level))
        }
    }

    return result.toList()
}