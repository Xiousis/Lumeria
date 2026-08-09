package com.solerforge.lumeria.models

import androidx.compose.ui.graphics.Color

enum class RaceRarity(val label: String, val color: Color) {
    Common("Common", Color(0xFFBDBDBD)),
    Uncommon("Uncommon", Color(0xFF4CAF50)),
    Rare("Rare", Color(0xFF2196F3)),
    Epic("Epic", Color(0xFFE67E22)),
    Legendary("Legendary", Color(0xFFA855F7)),
    GodTier("God Tier", Color(0xFFFFD700))
}

data class Race(
    val name: String,
    val description: String,
    val rarity: RaceRarity,
    val rollChance: Double, // Percentage (0.0 to 100.0)
    val strBonus: Int = 0,
    val vitBonus: Int = 0,
    val defBonus: Int = 0,
    val intBonus: Int = 0,
    val agiBonus: Int = 0,
    val luckBonus: Int = 0,
    val wisBonus: Int = 0
)
