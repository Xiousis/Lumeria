package com.solerforge.lumeria.database

import androidx.compose.ui.graphics.Color

data class PlayerClass(
    val name: String,
    val description: String,
    val color: Color,
    val requiredRaces: List<String> = emptyList() // Empty means all races
)

object ClassDatabase {
    val classes = listOf(
        PlayerClass("Warrior", "High Strength & Vitality. A master of the blade.", Color(0xFFD32F2F)),
        PlayerClass("Mage", "High Intelligence & Mana. Wielder of arcane arts.", Color(0xFF1976D2)),
        PlayerClass("Samurai", "High Agility & Luck. A swift and precise fighter.", Color(0xFF388E3C)),
        PlayerClass("Paladin", "Ultimate Defense & Holy Power. An unbreakable wall.", Color(0xFFFBC02D)),
        PlayerClass("Assassin", "Extreme Luck & Agility. Strikes from the shadows.", Color(0xFF7B1FA2)),
        PlayerClass("Monk", "High Vitality & Wisdom. Uses spirit and fists.", Color(0xFFE64A19)),
        PlayerClass("Archer", "Balanced Agility & Strength. Master of long range.", Color(0xFF689F38)),
        PlayerClass("Necromancer", "High Mana & Dark Power. Commands the void.", Color(0xFF424242)),
        PlayerClass("Bard", "High Luck & Wisdom. Inspires through music.", Color(0xFFF06292)),
        PlayerClass("Berserker", "Pure Strength & Speed. Reckless destruction.", Color(0xFFB71C1C)),
        
        // UNIQUE RACE CLASSES
        PlayerClass("Dragon Knight", "Exclusive to Dragonkin. Draconic might and fire.", Color(0xFFFF5722), listOf("Dragonkin")),
        PlayerClass("Void Reaver", "Exclusive to Demon. Devours the essence of the void.", Color(0xFFA855F7), listOf("Demon")),
        PlayerClass("Seraph", "Exclusive to Angel. Celestial judge of the high heavens.", Color(0xFFFFD600), listOf("Angel")),
        PlayerClass("Blood Mage", "Exclusive to Vampire. Sacrifices vitality for absolute power.", Color(0xFF8B0000), listOf("Vampire")),
        PlayerClass("Shadow Stalker", "Exclusive to Dark Elf & Ghost. Strikes from beyond the veil.", Color(0xFF2D3436), listOf("Dark Elf", "Ghost")),
        PlayerClass("World Breaker", "Exclusive to Titan & Half-Giant. Crushes planets with a single blow.", Color(0xFF636E72), listOf("Titan", "Half-Giant")),
        PlayerClass("Technomancer", "Exclusive to Cyborg. Uses ancient logic to manipulate mana.", Color(0xFF00CEC9), listOf("Cyborg")),
        PlayerClass("Enchanter", "Exclusive to Fairy & Elf. Commands the beauty of the natural world.", Color(0xFFFAB1A0), listOf("Fairy", "Elf")),
        PlayerClass("Slime Sage", "Exclusive to Slime. Uses its fluid form to adapt to any threat.", Color(0xFF55EFC4), listOf("Slime")),
        PlayerClass("Void Walker", "Exclusive to Void Sovereign. Master of the space between stars.", Color(0xFF6C5CE7), listOf("Void Sovereign")),
        PlayerClass("Dragon Lord", "Exclusive to Elder Dragon. The ultimate peak of draconic power.", Color(0xFFD63031), listOf("Elder Dragon")),
        PlayerClass("Arbiter", "Exclusive to Nephilim. Enforcer of the cosmic balance.", Color(0xFFFDCB6E), listOf("Nephilim"))
    )

    fun getAvailableClasses(raceName: String): List<PlayerClass> {
        return classes.filter { it.requiredRaces.isEmpty() || it.requiredRaces.contains(raceName) }
    }
}
