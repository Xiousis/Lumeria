package com.solerforge.lumeria.data

import kotlinx.serialization.Serializable

@Serializable
data class Quest(
    val title: String,
    val targetEnemy: String,
    val targetCount: Int,
    val currentCount: Int = 0,
    val rewardGold: Long,
    val rewardXp: Long,
    val isCompleted: Boolean = false,
    val isClaimed: Boolean = false,
    val requiredLocation: String = "Training Fields"
)
