package com.solerforge.lumeria.database

import com.solerforge.lumeria.models.Race
import com.solerforge.lumeria.models.RaceRarity
import kotlin.random.Random

object RaceDatabase {
    val races = listOf(
        // COMMON (Total: 50.0%)
        Race(
            name = "Human",
            description = "A versatile and adaptive race with balanced potential.",
            rarity = RaceRarity.Common,
            rollChance = 30.0,
            isMonster = false,
            strBonus = 5, vitBonus = 5, defBonus = 5, intBonus = 5, agiBonus = 5, luckBonus = 5, wisBonus = 5
        ),
        Race(
            name = "Halfling",
            description = "Small, nimble, and exceptionally lucky explorers.",
            rarity = RaceRarity.Common,
            rollChance = 20.0,
            isMonster = false,
            luckBonus = 15,
            agiBonus = 8,
            strBonus = -5
        ),

        // UNCOMMON (Total: 25.0%)
        Race(
            name = "Half-Orc",
            description = "Possesses raw power and resilience, but lacks intellect.",
            rarity = RaceRarity.Uncommon,
            rollChance = 5.0,
            isMonster = false,
            strBonus = 20,
            vitBonus = 20,
            intBonus = -10
        ),
        Race(
            name = "Half-Elf",
            description = "Combines human versatility with elven grace.",
            rarity = RaceRarity.Uncommon,
            rollChance = 5.0,
            isMonster = false,
            agiBonus = 12,
            intBonus = 12,
            wisBonus = 5
        ),
        Race(
            name = "Half-Goblin",
            description = "Scrappy and lucky, but physically weak.",
            rarity = RaceRarity.Uncommon,
            rollChance = 5.0,
            isMonster = false,
            luckBonus = 20,
            agiBonus = 10,
            strBonus = -10
        ),
        Race(
            name = "Dwarf",
            description = "Stout warriors with skin like stone, but slow.",
            rarity = RaceRarity.Uncommon,
            rollChance = 5.0,
            isMonster = false,
            vitBonus = 20,
            defBonus = 20,
            agiBonus = -10
        ),
        Race(
            name = "Gnome",
            description = "Intelligent and curious inventors, but very small.",
            rarity = RaceRarity.Uncommon,
            rollChance = 5.0,
            isMonster = false,
            intBonus = 15,
            wisBonus = 15,
            vitBonus = -10
        ),

        // RARE (Total: 15.0%)
        Race(
            name = "Elf",
            description = "Ancient beings attuned to speed, but physically fragile.",
            rarity = RaceRarity.Rare,
            rollChance = 4.0,
            isMonster = false,
            agiBonus = 20,
            luckBonus = 20,
            strBonus = -15
        ),
        Race(
            name = "Half-Giant",
            description = "Towering individuals with unmatched might, but clumsy.",
            rarity = RaceRarity.Rare,
            rollChance = 4.0,
            isMonster = false,
            strBonus = 40,
            vitBonus = 30,
            agiBonus = -20
        ),
        Race(
            name = "Dragonkin",
            description = "Sturdy descendants of dragons, but lack fortune.",
            rarity = RaceRarity.Rare,
            rollChance = 4.0,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            strBonus = 25,
            vitBonus = 25,
            luckBonus = -10
        ),
        Race(
            name = "Dark Elf",
            description = "Masters of shadow and precision, but shunned by the sun.",
            rarity = RaceRarity.Rare,
            rollChance = 3.0,
            isMonster = true,
            canUsePotions = false,
            canHavePets = false,
            intBonus = 20,
            agiBonus = 20,
            vitBonus = -10
        ),

        // EPIC (Total: 8.5%)
        Race(
            name = "Vampire",
            description = "Undead nobility. Lethal speed, but cursed life force.",
            rarity = RaceRarity.Epic,
            rollChance = 2.0,
            isMonster = true,
            canUsePotions = false,
            canHavePets = false,
            agiBonus = 30,
            intBonus = 20,
            vitBonus = -20,
            luckBonus = 10
        ),
        Race(
            name = "Werewolf",
            description = "Cursed ferocity. Massive strength, but reckless defense.",
            rarity = RaceRarity.Epic,
            rollChance = 2.0,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            strBonus = 40,
            agiBonus = 20,
            defBonus = -20
        ),
        Race(
            name = "Beholder",
            description = "A multi-eyed aberration of pure paranoia and magic.",
            rarity = RaceRarity.Epic,
            rollChance = 0.5,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            intBonus = 40,
            wisBonus = 40,
            vitBonus = -20,
            agiBonus = -10
        ),
        Race(
            name = "Slime",
            description = "Amorphous beings with incredible resilience. Cannot wear armor.",
            rarity = RaceRarity.Epic,
            rollChance = 1.0,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            vitBonus = 60,
            strBonus = -20,
            defBonus = 20
        ),
        Race(
            name = "Fairy",
            description = "Tiny beings of pure magic. Too small for normal gear.",
            rarity = RaceRarity.Epic,
            rollChance = 1.0,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            luckBonus = 50,
            wisBonus = 30,
            vitBonus = -40,
            intBonus = 20
        ),
        Race(
            name = "Ghost",
            description = "Ethereal remnants. Hard to hit, but cannot touch solid gear.",
            rarity = RaceRarity.Epic,
            rollChance = 1.0,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            agiBonus = 40,
            wisBonus = 30,
            vitBonus = -50,
            defBonus = 20
        ),
        Race(
            name = "Cyborg",
            description = "Enhanced with ancient tech. Great defense, but lacks soul.",
            rarity = RaceRarity.Epic,
            rollChance = 1.0,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            defBonus = 30,
            intBonus = 25,
            wisBonus = -20,
            strBonus = 15
        ),

        // LEGENDARY (Total: 1.4994%)
        Race(
            name = "Demon",
            description = "An apex predator from the void. Massive destructive power.",
            rarity = RaceRarity.Legendary,
            rollChance = 0.4,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            strBonus = 80,
            intBonus = 80,
            agiBonus = 40,
            wisBonus = -20
        ),
        Race(
            name = "Dracolich",
            description = "An ancient dragon that refused to die, binding its soul to undeath.",
            rarity = RaceRarity.Legendary,
            rollChance = 1.0993,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            strBonus = 60,
            intBonus = 60,
            defBonus = 60,
            agiBonus = -20
        ),

        // GOD TIER (Total: 0.0007%)
        Race(
            name = "Angel",
            description = "A divine messenger. Supreme wisdom and holy power.",
            rarity = RaceRarity.GodTier,
            rollChance = 0.0001,
            isMonster = false,
            intBonus = 230,
            wisBonus = 230,
            defBonus = 140,
            vitBonus = 80,
            luckBonus = -50
        ),
        Race(
            name = "Royal Demon",
            description = "The absolute pinnacle of demonic evolution. Commands the legions of hell.",
            rarity = RaceRarity.GodTier,
            rollChance = 0.0001,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            strBonus = 180,
            intBonus = 180,
            agiBonus = 100,
            vitBonus = 80,
            wisBonus = -40
        ),
        Race(
            name = "Shadow Monarch",
            description = "One who rules over death and shadows. The dark king.",
            rarity = RaceRarity.GodTier,
            rollChance = 0.0001,
            isMonster = true,
            canUsePotions = false,
            canHavePets = false,
            agiBonus = 200,
            intBonus = 150,
            wisBonus = 150,
            luckBonus = 50,
            vitBonus = -20
        ),
        Race(
            name = "Titan",
            description = "The absolute bedrock of existence. Unbreakable.",
            rarity = RaceRarity.GodTier,
            rollChance = 0.0001,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            vitBonus = 150,
            defBonus = 120,
            strBonus = 100,
            agiBonus = -30
        ),
        Race(
            name = "Void Sovereign",
            description = "Ruler of the between-spaces. Reality bends to them.",
            rarity = RaceRarity.GodTier,
            rollChance = 0.0001,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            intBonus = 150,
            wisBonus = 150,
            luckBonus = 100,
            vitBonus = -30
        ),
        Race(
            name = "Elder Dragon",
            description = "An ancient wing of destruction. Peak physical form.",
            rarity = RaceRarity.GodTier,
            rollChance = 0.0001,
            isMonster = true,
            canWearGear = false,
            canUsePotions = false,
            canHavePets = false,
            strBonus = 150,
            agiBonus = 100,
            vitBonus = 100,
            defBonus = 50,
            wisBonus = -20
        ),
        Race(
            name = "Nephilim",
            description = "Forbidden union of light and dark. Perfect balance.",
            rarity = RaceRarity.GodTier,
            rollChance = 0.0001,
            isMonster = true,
            canUsePotions = false,
            canHavePets = false,
            strBonus = 100,
            intBonus = 100,
            agiBonus = 100,
            luckBonus = 100,
            vitBonus = 50
        )
    )

    fun rollRace(): Race {
        val roll = Random.nextDouble(0.0, 100.0)
        var cumulative = 0.0
        for (race in races) {
            cumulative += race.rollChance
            if (roll <= cumulative) return race
        }
        return races.first()
    }

    fun getRace(name: String): Race {
        return races.find { it.name == name } ?: races.first()
    }
}
