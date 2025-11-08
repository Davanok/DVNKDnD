package com.davanok.dvnkdnd.ui.pages.newEntity.newItem

import androidx.compose.runtime.Composable
import com.davanok.dvnkdnd.data.model.entities.character.CharacterSpell
import com.davanok.dvnkdnd.data.model.entities.dndEntities.DnDFullEntity
import com.davanok.dvnkdnd.ui.pages.characterFull.contents.CharacterSpellsScreen
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray

@Composable
fun NewItemScreen() {
    val spellsJson = """
        [
            {
                "id": "18f83828-8ab1-498c-a39e-a32bcd673bd0",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Sleep",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "18f83828-8ab1-498c-a39e-a32bcd673bd0",
                    "area": {
                        "id": "18f83828-8ab1-498c-a39e-a32bcd673bd0",
                        "type": "TARGET",
                        "range": 90,
                        "width": 0,
                        "height": 0
                    },
                    "level": 1,
                    "ritual": false,
                    "school": "ENCHANTMENT",
                    "attacks": [],
                    "duration": "1 minute",
                    "components": [
                        "VERBAL",
                        "SOMATIC",
                        "MATERIAL"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": "a pinch of fine sand, rose petals, or a cricket"
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "This spell sends creatures into a magical slumber. Roll 5d8; the total is how many hit points of creatures this spell can affect.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "6783fabf-097b-4093-8567-dd16694605e2",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Scorching Ray",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "6783fabf-097b-4093-8567-dd16694605e2",
                    "area": null,
                    "level": 2,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC",
                        "MATERIAL"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": "a piece of tallow, a pinch of brimstone, and a dusting of powdered iron"
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "You create three rays of fire and hurl them at targets within range. Make a ranged spell attack for each ray. On a hit, the target takes fire damage.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "aac1511d-4bc5-4f0a-8147-97670443369f",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Hold Person",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "aac1511d-4bc5-4f0a-8147-97670443369f",
                    "area": {
                        "id": "aac1511d-4bc5-4f0a-8147-97670443369f",
                        "type": "TARGET",
                        "range": 60,
                        "width": 0,
                        "height": 0
                    },
                    "level": 2,
                    "ritual": false,
                    "school": "ENCHANTMENT",
                    "attacks": [],
                    "duration": "Up to 1 minute",
                    "components": [
                        "VERBAL",
                        "SOMATIC"
                    ],
                    "casting_time": "ACTION",
                    "concentration": true,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "Choose a humanoid that you can see within range. The target must succeed on a Wisdom saving throw or be paralyzed for the duration.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "6dc2564a-08f3-437c-a0b2-1dac5c898d36",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Misty Step",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "6dc2564a-08f3-437c-a0b2-1dac5c898d36",
                    "area": null,
                    "level": 2,
                    "ritual": false,
                    "school": "CONJURATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL"
                    ],
                    "casting_time": "BONUS_ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "Briefly surrounded by silvery mist, you teleport up to 30 feet to an unoccupied space that you can see.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "9830edf4-0f38-4bf3-a566-acb596b8533f",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Fireball",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "9830edf4-0f38-4bf3-a566-acb596b8533f",
                    "area": {
                        "id": "9830edf4-0f38-4bf3-a566-acb596b8533f",
                        "type": "SPHERE",
                        "range": 150,
                        "width": 20,
                        "height": 20
                    },
                    "level": 3,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC",
                        "MATERIAL"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": "a tiny ball of bat guano and sulfur"
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "A bright streak flashes to a point you choose and blossoms into an explosion of flame. Each creature in a 20-foot-radius sphere must make a Dexterity saving throw.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "627d1f8f-d7bc-4f54-80e3-818692e3ecf4",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Lightning Bolt",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "627d1f8f-d7bc-4f54-80e3-818692e3ecf4",
                    "area": {
                        "id": "627d1f8f-d7bc-4f54-80e3-818692e3ecf4",
                        "type": "LINE",
                        "range": 100,
                        "width": 5,
                        "height": 0
                    },
                    "level": 3,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC",
                        "MATERIAL"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": "a bit of fur and a rod of amber, crystal, or glass"
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "A stroke of lightning forming a line of 100 feet blasts out from you in a direction you choose. Each creature in the line must make a Dexterity saving throw.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "bafeb306-5493-4121-a599-74dace84429d",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Counterspell",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "bafeb306-5493-4121-a599-74dace84429d",
                    "area": null,
                    "level": 3,
                    "ritual": false,
                    "school": "ABJURATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "SOMATIC"
                    ],
                    "casting_time": "REACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "You attempt to interrupt a creature in the process of casting a spell. If the creature is casting a spell of 3rd level or lower, its spell fails and has no effect.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "a6fd334d-e679-4a76-930a-6ea78fa913c3",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Polymorph",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "a6fd334d-e679-4a76-930a-6ea78fa913c3",
                    "area": {
                        "id": "a6fd334d-e679-4a76-930a-6ea78fa913c3",
                        "type": "TARGET",
                        "range": 60,
                        "width": 0,
                        "height": 0
                    },
                    "level": 4,
                    "ritual": false,
                    "school": "TRANSMUTATION",
                    "attacks": [],
                    "duration": "Up to 1 hour",
                    "components": [
                        "VERBAL",
                        "SOMATIC",
                        "MATERIAL"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": "a caterpillar cocoon"
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "This spell transforms a creature that you can see within range into a new form. The transformation lasts for the duration or until the target drops to 0 hit points or dies.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "8b5f913d-b322-4c59-a5fb-1fc4dc6188dd",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Cone of Cold",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "8b5f913d-b322-4c59-a5fb-1fc4dc6188dd",
                    "area": {
                        "id": "8b5f913d-b322-4c59-a5fb-1fc4dc6188dd",
                        "type": "CONE",
                        "range": 0,
                        "width": 60,
                        "height": 0
                    },
                    "level": 5,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC",
                        "MATERIAL"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": "a small crystal or glass cone"
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "A blast of cold air erupts from your hands. Each creature in a 60-foot cone must make a Constitution saving throw.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "565a21be-dc49-4e0b-8eec-69f756eceafd",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Fireball",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "565a21be-dc49-4e0b-8eec-69f756eceafd",
                    "area": {
                        "id": "565a21be-dc49-4e0b-8eec-69f756eceafd",
                        "type": "SPHERE",
                        "range": 150,
                        "width": 20,
                        "height": 20
                    },
                    "level": 3,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC",
                        "MATERIAL"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": "a tiny ball of bat guano and sulfur"
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "A bright streak flashes from your pointing finger to a point you choose within range and then blossoms with a low roar into an explosion of flame.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "6b2fa333-f09b-4473-88c2-5cdb0302ff35",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Magic Missile",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "6b2fa333-f09b-4473-88c2-5cdb0302ff35",
                    "area": null,
                    "level": 1,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "You create three glowing darts of magical force. Each dart hits a creature of your choice that you can see within range.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "ae4295bf-8bb8-4051-b26b-808506dccae2",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Healing Word",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "ae4295bf-8bb8-4051-b26b-808506dccae2",
                    "area": null,
                    "level": 1,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL"
                    ],
                    "casting_time": "BONUS_ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "A creature of your choice that you can see within range regains hit points equal to 1d4 + your spellcasting ability modifier.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "ec2e89b9-814c-4e36-857f-c2afa78201a7",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Eldritch Blast",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "ec2e89b9-814c-4e36-857f-c2afa78201a7",
                    "area": {
                        "id": "ec2e89b9-814c-4e36-857f-c2afa78201a7",
                        "type": "POINT",
                        "range": 120,
                        "width": 0,
                        "height": 0
                    },
                    "level": null,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Warlock expanded",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "A beam of crackling energy streaks toward a creature you can see within range. Make a ranged spell attack. On a hit, the target takes 1d10 force damage.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "f5c72645-4c14-41a3-a293-79cf6144b9ed",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Prestidigitation",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "f5c72645-4c14-41a3-a293-79cf6144b9ed",
                    "area": null,
                    "level": 0,
                    "ritual": false,
                    "school": "ILLUSION",
                    "attacks": [],
                    "duration": "Up to 1 hour (varies by effect)",
                    "components": [
                        "VERBAL",
                        "SOMATIC"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "A minor magical trick that novice spellcasters use for practice. It can perform simple sensory effects, clean/soil objects, cook small items, chill/warm a nonliving material, or create a small nonmagical trinket.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "cc32519e-b451-4491-bb1c-3566a05ce334",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Mage Hand",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "cc32519e-b451-4491-bb1c-3566a05ce334",
                    "area": {
                        "id": "cc32519e-b451-4491-bb1c-3566a05ce334",
                        "type": "POINT",
                        "range": 30,
                        "width": 0,
                        "height": 0
                    },
                    "level": 0,
                    "ritual": false,
                    "school": "CONJURATION",
                    "attacks": [],
                    "duration": "1 minute",
                    "components": [
                        "SOMATIC"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "A spectral, floating hand appears at a point you choose within range. The hand lasts for the duration and can manipulate an object, open an unlocked door or container, stow or retrieve an item, or pour the contents out of a vial.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "8a39daf5-8115-4f80-8b5b-0e626c1b2ce9",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Minor Illusion",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "8a39daf5-8115-4f80-8b5b-0e626c1b2ce9",
                    "area": null,
                    "level": 0,
                    "ritual": false,
                    "school": "ILLUSION",
                    "attacks": [],
                    "duration": "1 minute",
                    "components": [
                        "SOMATIC",
                        "MATERIAL"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": "a bit of fleece"
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "You create a sound or an image of an object within range that lasts for the duration. The illusion also ends if you dismiss it as an action or cast this spell again.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "c24d3a77-31c7-49ab-b2d7-603860b2feef",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Fire Bolt",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "c24d3a77-31c7-49ab-b2d7-603860b2feef",
                    "area": {
                        "id": "c24d3a77-31c7-49ab-b2d7-603860b2feef",
                        "type": "POINT",
                        "range": 120,
                        "width": 0,
                        "height": 0
                    },
                    "level": 0,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "You hurl a mote of fire at a creature or object within range. Make a ranged spell attack. On a hit, the target takes fire damage.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "01575d82-d1c1-4def-81c2-9aa0a09f312f",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Ray of Frost",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "01575d82-d1c1-4def-81c2-9aa0a09f312f",
                    "area": {
                        "id": "01575d82-d1c1-4def-81c2-9aa0a09f312f",
                        "type": "POINT",
                        "range": 60,
                        "width": 0,
                        "height": 0
                    },
                    "level": 0,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "A frigid beam of blue-white light streaks toward a creature within range. Make a ranged spell attack. On a hit, it takes cold damage and its speed is reduced.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "d4110820-ab79-48b6-962c-82bd2b1e23ff",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Eldritch Blast",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "d4110820-ab79-48b6-962c-82bd2b1e23ff",
                    "area": {
                        "id": "d4110820-ab79-48b6-962c-82bd2b1e23ff",
                        "type": "OTHER",
                        "range": 120,
                        "width": 0,
                        "height": 0
                    },
                    "level": 0,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "A beam of crackling energy streaks toward a creature you can see within range. Make a ranged spell attack. On a hit, the target takes force damage.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "5d7809e2-6ea5-4f27-9f78-d0dde804a902",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Magic Missile",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "5d7809e2-6ea5-4f27-9f78-d0dde804a902",
                    "area": null,
                    "level": 1,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "You create three glowing darts of magical force. Each dart hits a creature of your choice that you can see within range.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "d81a5de3-5105-4f89-b1d3-30d5e02b950b",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Shield",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "d81a5de3-5105-4f89-b1d3-30d5e02b950b",
                    "area": null,
                    "level": 1,
                    "ritual": false,
                    "school": "ABJURATION",
                    "attacks": [],
                    "duration": "1 round",
                    "components": [
                        "VERBAL",
                        "SOMATIC"
                    ],
                    "casting_time": "REACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "An invisible barrier of magical force appears and protects you. Until the start of your next turn, you have a +5 bonus to AC, including against the triggering attack.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            },
            {
                "id": "b9c5a123-a157-4bc7-8015-98fc39a1fa52",
                "cls": null,
                "feat": null,
                "item": null,
                "name": "Cure Wounds",
                "race": null,
                "type": "SPELL",
                "spell": {
                    "id": "b9c5a123-a157-4bc7-8015-98fc39a1fa52",
                    "area": {
                        "id": "b9c5a123-a157-4bc7-8015-98fc39a1fa52",
                        "type": "TOUCH",
                        "range": 0,
                        "width": 0,
                        "height": 0
                    },
                    "level": 1,
                    "ritual": false,
                    "school": "EVOCATION",
                    "attacks": [],
                    "duration": "Instantaneous",
                    "components": [
                        "VERBAL",
                        "SOMATIC"
                    ],
                    "casting_time": "ACTION",
                    "concentration": false,
                    "casting_time_other": null,
                    "material_component": null
                },
                "source": "Player's handbook",
                "ability": null,
                "user_id": "882f9afb-9ee6-48cd-8022-02fb6b615e3e",
                "abilities": [],
                "parent_id": null,
                "background": null,
                "description": "A creature you touch regains a number of hit points equal to 1d8 + your spellcasting ability modifier.",
                "proficiencies": [],
                "modifier_groups": [],
                "companion_entities": []
            }
        ]
    """.trimIndent()

    val json = Json {
        ignoreUnknownKeys = true
    }

    val spellsListJson = json.parseToJsonElement(spellsJson).jsonArray

    val spells = spellsListJson.map {
        json.decodeFromJsonElement(DnDFullEntity.serializer(), it)
    }.map {
        CharacterSpell(false, it)
    }

    CharacterSpellsScreen(
        spells = spells,
        onClick = {  }
    )
}