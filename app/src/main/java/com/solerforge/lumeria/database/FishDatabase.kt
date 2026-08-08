package com.solerforge.lumeria.database

data class Fish(
    val name: String,
    val rarity: String,
    val basePrice: Long,
    val description: String,
    val requiredLevel: Int = 1
)

object FishDatabase {
    val fishTable = listOf(
        Fish("Minnow", "Common", 10L, "A small, common fish found everywhere."),
        Fish("Bass", "Common", 25L, "A standard prize for any fisher."),
        Fish("Bluegill", "Common", 15L, "A colorful little sunfish."),
        Fish("Rockfish", "Common", 20L, "Hides among the stones in shallow water."),
        Fish("Guppy", "Common", 5L, "Tiny and almost worthless, but cute."),
        Fish("Neon Tetra", "Common", 12L, "Glows with a faint blue light."),
        
        Fish("Catfish", "Uncommon", 75L, "Found in murky waters. Watch the whiskers!"),
        Fish("Trout", "Uncommon", 120L, "A fast swimmer that puts up a fight.", 5),
        Fish("Mackerel", "Uncommon", 85L, "A staple for sailors and travelers."),
        Fish("Barracuda", "Uncommon", 150L, "Aggressive and toothy. Hard to reel in.", 8),
        Fish("Flathead", "Uncommon", 100L, "Lies flat on the bottom, blending in."),
        
        Fish("Salmon", "Rare", 500L, "Known for their incredible journey upstream.", 10),
        Fish("Eel", "Rare", 800L, "Slithery and hard to catch.", 15),
        Fish("Tuna", "Rare", 600L, "A massive, powerful ocean swimmer.", 12),
        Fish("Pufferfish", "Rare", 700L, "Careful! One prick and you're in trouble.", 18),
        Fish("Kingfish", "Rare", 900L, "A prize catch for any sporting fisher.", 20),
        
        Fish("Golden Carp", "Legendary", 5000L, "A mythical fish said to bring good fortune.", 25),
        Fish("Anglerfish", "Legendary", 6000L, "Brings its own light to the deep dark.", 30),
        Fish("Great White", "Legendary", 8000L, "The king of the upper ocean.", 35),
        
        Fish("Lava Snapper", "Mythic", 15000L, "Somehow survives in the hottest volcanic vents.", 40),
        Fish("Kraken Hatchling", "Mythic", 20000L, "A small piece of a much larger nightmare.", 50),
        
        Fish("Void Ray", "God Tier", 50000L, "An ethereal being from the depths of the abyss.", 60),
        Fish("Leviathan Fin", "God Tier", 0L, "A legendary fin from the deep. Eating it grants permanent +2% HP.", 70),
        Fish("Stellar Koi", "God Tier", 0L, "A fish that swam among the stars. Eating it grants permanent +2% HP.", 80),
        Fish("Abyssal Serpent", "God Tier", 0L, "A nightmare from the deepest trench. Eating it grants permanent +2% HP.", 90)
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

    fun getXpForNextLevel(level: Int): Long {
        return level * 100L + (level.toLong() * level * 20L)
    }
}
