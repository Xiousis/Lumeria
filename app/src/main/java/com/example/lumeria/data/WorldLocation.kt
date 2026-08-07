package com.example.lumeria.data

data class WorldLocation(
    val name: String,
    val description: String,
    val levelRange: String,
    val minLevel: Int,
    val maxLevel: Int,
    val enemies: List<String>,
    val boss: String,
    val bossUnlocked: Boolean = false,
    val x: Float, // 0.0 to 1.0 (percent of map width)
    val y: Float,  // 0.0 to 1.0 (percent of map height)
    val requiredLevel: Int = 1
)
