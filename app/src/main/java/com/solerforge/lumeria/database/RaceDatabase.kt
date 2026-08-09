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
            strBonus = 5, vitBonus = 5, defBonus = 5, intBonus = 5, agiBonus = 5, luckBonus = 5, wisBonus = 5
        ),
        Race(
            name = "Halfling",
            description = "Small, nimble, and exceptionally lucky explorers.",
            rarity = RaceRarity.Common,
            rollChance = 20.0,
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
            strBonus = 20,
            vitBonus = 20,
            intBonus = -10
        ),
        Race(
            name = "Half-Elf",
            description = "Combines human versatility with elven grace.",
            rarity = RaceRarity.Uncommon,
            rollChance = 5.0,
            agiBonus = 12,
            intBonus = 12,
            wisBonus = 5
        ),
        Race(
            name = "Half-Goblin",
            description = "Scrappy and lucky, but physically weak.",
            rarity = RaceRarity.Uncommon,
            rollChance = 5.0,
            luckBonus = 20,
            agiBonus = 10,
            strBonus = -10
        ),
        Race(
            name = "Dwarf",
            description = "Stout warriors with skin like stone, but slow.",
            rarity = RaceRarity.Uncommon,
            rollChance = 5.0,
            vitBonus = 20,
            defBonus = 20,
            agiBonus = -10
        ),
        Race(
            name = "Gnome",
            description = "Intelligent and curious inventors, but very small.",
            rarity = RaceRarity.Uncommon,
            rollChance = 5.0,
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
            agiBonus = 20,
            luckBonus = 20,
            strBonus = -15
        ),
        Race(
            name = "Half-Giant",
            description = "Towering individuals with unmatched might, but clumsy.",
            rarity = RaceRarity.Rare,
            rollChance = 4.0,
            strBonus = 40,
            vitBonus = 30,
            agiBonus = -20
        ),
        Race(
            name = "Dragonkin",
            description = "Sturdy descendants of dragons, but lack fortune.",
            rarity = RaceRarity.Rare,
            rollChance = 4.0,
            strBonus = 25,
            vitBonus = 25,
            luckBonus = -10
        ),
        Race(
            name = "Dark Elf",
            description = "Masters of shadow and precision, but shunned by the sun.",
            rarity = RaceRarity.Rare,
            rollChance = 3.0,
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
            strBonus = 40,
            agiBonus = 20,
            defBonus = -20
        ),
        Race(
            name = "Fairy",
            description = "Tiny beings of pure magic. Lucky, but extremely fragile.",
            rarity = RaceRarity.Epic,
            rollChance = 1.5,
            luckBonus = 50,
            wisBonus = 30,
            vitBonus = -40,
            intBonus = 20
        ),
        Race(
            name = "Ghost",
            description = "Ethereal remnants. Hard to hit, but barely present.",
            rarity = RaceRarity.Epic,
            rollChance = 1.5,
            agiBonus = 40,
            wisBonus = 30,
            vitBonus = -50,
            defBonus = 20
        ),
        Race(
            name = "Cyborg",
            description = "Enhanced with ancient tech. Great defense, but lacks soul.",
            rarity = RaceRarity.Epic,
            rollChance = 1.5,
            defBonus = 30,
            intBonus = 25,
            wisBonus = -20,
            strBonus = 15
        ),

        // LEGENDARY (Total: 1.4996%)
        Race(
            name = "Demon",
            description = "An apex predator from the void. Massive destructive power.",
            rarity = RaceRarity.Legendary,
            rollChance = 0.75,
            strBonus = 80,
            intBonus = 80,
            agiBonus = 40,
            wisBonus = -20
        ),
        Race(
            name = "Angel",
            description = "A divine messenger. High wisdom and magic, but lacks luck.",
            rarity = RaceRarity.Legendary,
            rollChance = 0.7496,
            intBonus = 80,
            wisBonus = 80,
            defBonus = 40,
            luckBonus = -20
        ),

        // GOD TIER (Total: 0.0004%)
        Race(
            name = "Titan",
            description = "The absolute bedrock of existence. Unbreakable.",
            rarity = RaceRarity.GodTier,
            rollChance = 0.0001,
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
