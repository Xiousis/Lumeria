package com.solerforge.lumeria.database

import androidx.compose.ui.graphics.Color

data class KingdomRank(
    val id: Int,
    val name: String,
    val requiredRenown: Long,
    val rewardGold: Long,
    val rewardXp: Long,
    val rewardTitle: String?,
    val color: Color
)

object KingdomRankDatabase {
    val ranks = listOf(
        KingdomRank(0, "Nobody", 0L, 0L, 0L, null, Color.Gray),
        KingdomRank(1, "Known Wanderer", 1000L, 25000L, 10000L, "Wanderer", Color.White),
        KingdomRank(2, "Respected Adventurer", 5000L, 100000L, 50000L, "Respected", Color.Cyan),
        KingdomRank(3, "Local Hero", 15000L, 500000L, 250000L, "Hero of the Wilds", Color.Green),
        KingdomRank(4, "Kingdom Protector", 50000L, 2000000L, 1000000L, "Royal Protector", Color.Yellow),
        KingdomRank(5, "National Champion", 125000L, 7500000L, 5000000L, "Kingdom Champion", Color(0xFFFF9800)),
        KingdomRank(6, "Legendary Hero", 250000L, 25000000L, 20000000L, "Living Legend", Color.Magenta),
        KingdomRank(7, "Hero of Lumeria", 500000L, 100000000L, 50000000L, "Guardian of the Realm", Color.Cyan)
    )

    fun getCurrentRank(renown: Long): KingdomRank {
        return ranks.filter { renown >= it.requiredRenown }.maxByOrNull { it.id } ?: ranks[0]
    }

    fun getNextRank(renown: Long): KingdomRank? {
        return ranks.find { it.requiredRenown > renown }
    }

    fun getNationUpgradeCost(level: Int): Long {
        return when (level) {
            0 -> 10000L
            1 -> 25000L
            2 -> 50000L
            3 -> 100000L
            4 -> 200000L
            5 -> 500000L
            6 -> 1000000L
            7 -> 2500000L
            8 -> 5000000L
            9 -> 10000000L
            else -> 0L
        }
    }
}
