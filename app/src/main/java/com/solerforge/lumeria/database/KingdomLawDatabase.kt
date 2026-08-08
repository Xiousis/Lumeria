package com.solerforge.lumeria.database

data class KingdomLaw(
    val id: Int,
    val name: String,
    val description: String,
    val positiveEffect: String,
    val tradeOff: String,
    val requiredRank: Int = 3 // Minimum Local Hero
)

object KingdomLawDatabase {
    val laws = listOf(
        KingdomLaw(
            id = 1,
            name = "Merchant's Subsidy",
            description = "The King subsidizes local trade to lower costs.",
            positiveEffect = "-15% Shop Prices",
            tradeOff = "-10% Gold from Battles",
            requiredRank = 2
        ),
        KingdomLaw(
            id = 2,
            name = "Warrior's Draft",
            description = "Intensive training protocols for all combatants.",
            positiveEffect = "+25% XP from Battles",
            tradeOff = "+15% Enemy Damage",
            requiredRank = 3
        ),
        KingdomLaw(
            id = 3,
            name = "Void Resistance Act",
            description = "National prayer and magical shielding against the void.",
            positiveEffect = "+20% Void/Dark Resistance",
            tradeOff = "-15% Mana Regeneration",
            requiredRank = 4
        ),
        KingdomLaw(
            id = 4,
            name = "Industrial Focus",
            description = "Prioritize the collection of raw materials for the forge.",
            positiveEffect = "+30% Material Drop Rate",
            tradeOff = "-10% Critical Hit Chance",
            requiredRank = 3
        ),
        KingdomLaw(
            id = 5,
            name = "Imperial Tithe",
            description = "A portion of all earnings goes directly to the crown's PR machine.",
            positiveEffect = "+50% Renown Gain",
            tradeOff = "-20% Gold Gain",
            requiredRank = 5
        )
    )

    fun getLaw(id: Int?): KingdomLaw? {
        return laws.find { it.id == id }
    }
}
