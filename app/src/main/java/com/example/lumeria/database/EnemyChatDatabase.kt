package com.example.lumeria.database

object EnemyChatDatabase {

    fun getMonsterSound(name: String, hpPercent: Float): String? {
        val sounds = when {
            name.contains("Slime", ignoreCase = true) -> listOf("*Gloop*", "*Squish*", "*Bloop*")
            name.contains("Rat", ignoreCase = true) -> listOf("*Squeak!*", "*Chitter*", "*Scratches*")
            name.contains("Wolf", ignoreCase = true) -> listOf("*Growl...*", "*Snarl*", "*Howl!*")
            name.contains("Drake", ignoreCase = true) || name.contains("Dragon", ignoreCase = true) -> 
                listOf("*RAAAAARGH!*", "*Heavy Breathing*", "*Smoking Nostrils*")
            name.contains("Spirit", ignoreCase = true) -> listOf("*Wailing...*", "*Eerie Whisper*", "*Fading Hiss*")
            name.contains("Golem", ignoreCase = true) -> listOf("*Grinding Stone*", "*Heavy Thud*", "*Stone Cracking*")
            else -> listOf("*Growl*", "*Roar*", "*Aggressive Noise*")
        }
        
        return when {
            hpPercent < 0.25f -> sounds.last()
            hpPercent < 0.60f -> sounds[1]
            else -> sounds.first()
        }
    }

    fun getHumanoidQuote(name: String, hpPercent: Float): String? {
        val quotes = when {
            name.contains("Goblin", ignoreCase = true) -> listOf(
                "\"Heh, easy prey!\"",
                "\"Ouch! You pay for that!\"",
                "\"No... not the shiny gold...\""
            )
            name.contains("Orc", ignoreCase = true) -> listOf(
                "\"For the horde!\"",
                "\"Stronger than you look!\"",
                "\"I go to the ancestors...\""
            )
            name.contains("Knight", ignoreCase = true) || name.contains("Guard", ignoreCase = true) -> listOf(
                "\"Halt, criminal!\"",
                "\"A worthy opponent!\"",
                "\"My duty... is done...\""
            )
            name.contains("Rogue", ignoreCase = true) || name.contains("Assassin", ignoreCase = true) -> listOf(
                "\"Didn't see me, did you?\"",
                "\"Too fast for you!\"",
                "\"Even shadows... fall...\""
            )
            name.contains("Samurai", ignoreCase = true) || name.contains("Shogun", ignoreCase = true) -> listOf(
                "\"Focus... strike...\"",
                "\"A clean hit. My turn.\"",
                "\"Honor... in defeat...\""
            )
            name.contains("Monk", ignoreCase = true) -> listOf(
                "\"The mind is the sharpest blade.\"",
                "\"You have yet to find balance.\"",
                "\"I return... to the stream...\""
            )
            name.contains("Wizard", ignoreCase = true) || name.contains("Spellsword", ignoreCase = true) -> listOf(
                "\"Behold the power of the arcane!\"",
                "\"My mana... is fading...\"",
                "\"The magic... flickers out...\""
            )
            name.contains("Mountain Man", ignoreCase = true) || name.contains("Nomad", ignoreCase = true) -> listOf(
                "\"The wilds don't take kindly to strangers!\"",
                "\"I've survived worse storms!\"",
                "\"The earth... calls...\""
            )
            else -> listOf(
                "\"You won't leave here alive!\"",
                "\"Is that all you've got?\"",
                "\"Curse... you...\""
            )
        }

        return when {
            hpPercent < 0.25f -> quotes.last()
            hpPercent < 0.60f -> quotes[1]
            else -> quotes.first()
        }
    }
}
