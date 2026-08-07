package com.example.lumeria.database

import com.example.lumeria.data.LootDrop

object BossLootDatabase {

    val allGodTierItems = listOf(
        "Mantle of Perseverance", "Coin of the Tyrant", "Starshard Edge",
        "Aegis of Mountains", "Bogwalker Greaves", "Fang of Eternity",
        "Void Singularity", "Bulwark of Heroes", "Endbringer", "Eye of Creation"
    )

    private val bossLoot = mapOf(
        "Training Captain" to listOf(
            LootDrop("Captain's Badge", 100),
            LootDrop("Training Sword", 40),
            LootDrop("Veteran Armor", 5), // Legendary
            LootDrop("Helm of the First Hero", 1), // Mythic
        ),
        "Goblin King" to listOf(
            LootDrop("Goblin Crown", 100),
            LootDrop("King's Dagger", 25),
            LootDrop("Goblin King's Blade", 5), // Legendary
            LootDrop("Royal Goblin Crown", 1), // Mythic
        ),
        "Crystal Guardian" to listOf(
            LootDrop("Crystal Core", 100),
            LootDrop("Crystal Shield", 20),
            LootDrop("Crystal Boots", 30),
            LootDrop("Crystal Longsword", 5), // Legendary
            LootDrop("Prismatic Crystal Heart", 1), // Mythic
        ),
        "Stone Titan" to listOf(
            LootDrop("Titan Heart", 100),
            LootDrop("Titan Hammer", 20),
            LootDrop("Wolfstride Boots", 25),
            LootDrop("Knight's Crest", 5), // Story mini-boss or titan
            LootDrop("Titanbreaker Maul", 5), // Legendary
            LootDrop("Titan Core Relic", 1), // Mythic
        ),
        "Marsh Horror" to listOf(
            LootDrop("Rotten Essence", 100),
            LootDrop("Poison Fang", 20),
            LootDrop("Tower Shield", 15),
            LootDrop("Swamp Lord Armor", 5), // Legendary
            LootDrop("Heart of the Bog", 1), // Mythic
        ),
        "Ancient Dragon" to listOf(
            LootDrop("Dragon Scale", 100),
            LootDrop("Dragon Fang", 25),
            LootDrop("Dragon Shield", 10),
            LootDrop("Dragonrunner Boots", 15),
            LootDrop("Dragon Slayer Blade", 5), // Legendary
            LootDrop("Ancient Dragon Heart", 1), // Mythic
        ),
        "Lord Xarthos" to listOf(
            LootDrop("Xarthos Soul", 100),
            LootDrop("Void Armor", 15),
            LootDrop("Voidwalker Boots", 10),
            LootDrop("Void Bulwark", 5),
            LootDrop("Void Crown", 2),
            LootDrop("Blade of Xarthos", 5), // Legendary
            LootDrop("Crown of the Void King", 1), // Mythic
        ),
        "The Forgotten Creator" to listOf(
            LootDrop("Godwalker", 100)
        )
    )

    private val godTierLoot = mapOf(
        "Training Captain" to "Mantle of Perseverance",
        "Goblin King" to "Coin of the Tyrant",
        "Crystal Guardian" to "Starshard Edge",
        "Stone Titan" to "Aegis of Mountains",
        "Marsh Horror" to "Bogwalker Greaves",
        "Ancient Dragon" to "Fang of Eternity",
        "Lord Xarthos" to "Void Singularity",
        "Aurelius" to "Bulwark of Heroes",
        "Ascended Xarthos" to "Endbringer",
        "The World Eater" to "Eye of Creation",
        "World Eater Final Form" to "Eye of Creation"
    )

    fun rollLoot(bossName: String, bonusChance: Int = 0): List<String> {
        val drops = mutableListOf<String>()
        
        // Regular Loot
        bossLoot[bossName]?.forEach { loot ->
            if ((1..100).random() <= (loot.chance + bonusChance)) {
                drops.add(loot.itemName)
            }
        }
        
        // God Tier Loot (0.001% chance + bonus)
        godTierLoot[bossName]?.let { godItem ->
            val finalGodChance = 0.00001 + (bonusChance.toDouble() / 100000.0)
            if (Math.random() < finalGodChance) {
                drops.add(godItem)
            }
        }
        
        return drops
    }

    fun rollRiftLoot(): List<String> {
        // Rifts always give some crafting materials or consumables
        val pool = listOf("Hi-Potion", "Hi-Mana Potion", "Elixir", "Void Shard", "Crystal Core", "Dragon Scale")
        return listOf(pool.random())
    }
}
