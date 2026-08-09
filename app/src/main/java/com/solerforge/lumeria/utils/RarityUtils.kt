package com.solerforge.lumeria.utils

import androidx.compose.ui.graphics.Color

object RarityUtils {
    fun getColor(rarity: String): Color {
        return when (rarity) {
            "Uncommon" -> Color(0xFF4CAF50) // Green
            "Rare" -> Color(0xFF2196F3) // Blue
            "Epic" -> Color(0xFFE67E22) // Orange
            "Legendary" -> Color(0xFFA855F7) // Purple
            "Mythic" -> Color(0xFFFF69B4) // Pink
            "God Tier" -> Color(0xFFFFD700) // Gold
            else -> Color.White
        }
    }

    fun getStarColor(): Color = Color(0xFF9C27B0) // Purple Star
}
