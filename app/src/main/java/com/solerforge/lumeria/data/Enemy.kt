package com.solerforge.lumeria.data

import com.solerforge.lumeria.models.ElementType

enum class EnemyPassive {
    POISON_TOUCH,      // Builds poison stacks on hit
    PHYSICAL_COUNTER,  // Damages player when hit by physical attacks
    MAGIC_RESIST,      // Reduces magic damage taken
    DESPERATION,       // Gains damage when below 30% HP
    CRYSTAL_ARMOR      // Flat reduction to all damage
}

data class Enemy(
    val name: String,
    val baseName: String,
    val maxHp: Int, 
    val level: Int,
    val rewardXp: Long = 0,
    val rewardGold: Long = 0,
    val isHumanoid: Boolean = false,
    val elementalResistances: Map<ElementType, Double> = emptyMap(),
    val materialDrop: String? = null,
    val passive: EnemyPassive? = null,
)
