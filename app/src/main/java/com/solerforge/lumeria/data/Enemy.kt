package com.solerforge.lumeria.data

import com.solerforge.lumeria.models.ElementType

data class Enemy(
    val name: String,
    val baseName: String,
    val maxHp: Int, 
    val level: Int,
    val rewardXp: Int = 0,
    val rewardGold: Int = 0,
    val isHumanoid: Boolean = false,
    val elementalResistances: Map<ElementType, Double> = emptyMap(),
    val materialDrop: String? = null,
)
