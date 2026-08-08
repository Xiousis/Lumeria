package com.solerforge.lumeria.database

data class Fish(
    val name: String,
    val rarity: String,
    val basePrice: Int,
    val description: String,
    val requiredLevel: Int = 1
)

object FishDatabase {
    val fishTable = listOf(
        Fish("Minnow", "Common", 10, "A small, common fish found everywhere."),
        Fish("Bass", "Common", 25, "A standard prize for any fisher."),
        Fish("Bluegill", "Common", 15, "A colorful little sunfish."),
        Fish("Rockfish", "Common", 20, "Hides among the stones in shallow water."),
        Fish("Guppy", "Common", 5, "Tiny and almost worthless, but cute."),
        Fish("Neon Tetra", "Common", 12, "Glows with a faint blue light."),
        
        Fish("Catfish", "Uncommon", 75, "Found in murky waters. Watch the whiskers!"),
        Fish("Trout", "Uncommon", 120, "A fast swimmer that puts up a fight.", 5),
        Fish("Mackerel", "Uncommon", 85, "A staple for sailors and travelers."),
        Fish("Barracuda", "Uncommon", 150, "Aggressive and toothy. Hard to reel in.", 8),
        Fish("Flathead", "Uncommon", 100, "Lies flat on the bottom, blending in."),
        
        Fish("Salmon", "Rare", 500, "Known for their incredible journey upstream.", 10),
        Fish("Eel", "Rare", 800, "Slithery and hard to catch.", 15),
        Fish("Tuna", "Rare", 600, "A massive, powerful ocean swimmer.", 12),
        Fish("Pufferfish", "Rare", 700, "Careful! One prick and you're in trouble.", 18),
        Fish("Kingfish", "Rare", 900, "A prize catch for any sporting fisher.", 20),
        
        Fish("Golden Carp", "Legendary", 5000, "A mythical fish said to bring good fortune.", 25),
        Fish("Anglerfish", "Legendary", 6000, "Brings its own light to the deep dark.", 30),
        Fish("Great White", "Legendary", 8000, "The king of the upper ocean.", 35),
        
        Fish("Lava Snapper", "Mythic", 15000, "Somehow survives in the hottest volcanic vents.", 40),
        Fish("Kraken Hatchling", "Mythic", 20000, "A small piece of a much larger nightmare.", 50),
        
        Fish("Void Ray", "God Tier", 50000, "An ethereal being from the depths of the abyss.", 60),
        Fish("Leviathan Fin", "God Tier", 0, "A legendary fin from the deep. Eating it grants permanent +2% HP.", 70),
        Fish("Stellar Koi", "God Tier", 0, "A fish that swam among the stars. Eating it grants permanent +2% HP.", 80),
        Fish("Abyssal Serpent", "God Tier", 0, "A nightmare from the deepest trench. Eating it grants permanent +2% HP.", 90)
    )

    fun rollFish(fishingLevel: Int): Fish {
        val available = fishTable.filter { fishingLevel >= it.requiredLevel }
        val roll = Math.random()
        return when {
            roll < 0.01 -> available.find { it.rarity == "God Tier" } ?: available.last()
            roll < 0.05 -> available.find { it.rarity == "Mythic" } ?: available.last()
            roll < 0.15 -> available.find { it.rarity == "Legendary" } ?: available.last()
            roll < 0.30 -> available.find { it.rarity == "Rare" } ?: available.last()
            roll < 0.60 -> available.find { it.rarity == "Uncommon" } ?: available.last()
            else -> available.find { it.rarity == "Common" } ?: available.first()
        }
    }

    fun getXpForNextLevel(level: Int): Int {
        return level * 100 + (level * level * 20)
    }
}
