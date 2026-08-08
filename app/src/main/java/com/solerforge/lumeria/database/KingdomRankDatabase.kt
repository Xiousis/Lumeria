package com.solerforge.lumeria.database

import androidx.compose.ui.graphics.Color

data class KingdomRank(
    val id: Int,
    val name: String,
    val requiredRenown: Int,
    val rewardGold: Int,
    val rewardXp: Int,
    val rewardTitle: String?,
    val color: Color
)

object KingdomRankDatabase {
    val ranks = listOf(
        KingdomRank(0, "Nobody", 0, 0, 0, null, Color.Gray),
        KingdomRank(1, "Known Wanderer", 1000, 25000, 10000, "Wanderer", Color.White),
        KingdomRank(2, "Respected Adventurer", 5000, 100000, 50000, "Respected", Color.Cyan),
        KingdomRank(3, "Local Hero", 15000, 500000, 250000, "Hero of the Wilds", Color.Green),
        KingdomRank(4, "Kingdom Protector", 50000, 2000000, 1000000, "Royal Protector", Color.Yellow),
        KingdomRank(5, "National Champion", 125000, 7500000, 5000000, "Kingdom Champion", Color(0xFFFF9800)),
        KingdomRank(6, "Legendary Hero", 250000, 25000000, 20000000, "Living Legend", Color.Magenta),
        KingdomRank(7, "Hero of Lumeria", 500000, 100000000, 50000000, "Guardian of the Realm", Color.Cyan)
    )

    fun getCurrentRank(renown: Int): KingdomRank {
        return ranks.filter { renown >= it.requiredRenown }.maxByOrNull { it.id } ?: ranks[0]
    }

    fun getNextRank(renown: Int): KingdomRank? {
        return ranks.find { it.requiredRenown > renown }
    }

    fun getNationUpgradeCost(level: Int): Int {
        return when (level) {
            0 -> 10000
            1 -> 25000
            2 -> 50000
            3 -> 100000
            4 -> 200000
            5 -> 500000
            6 -> 1000000
            7 -> 2500000
            8 -> 5000000
            9 -> 10000000
            else -> 0
        }
    }
}
