package com.solerforge.lumeria.models

import kotlinx.serialization.Serializable

@Serializable
data class LeaderboardEntry(
    val playerName: String,
    val level: Int,
    val playerClass: String,
    val pvpWins: Int,
    val pvpLosses: Int,
    val lastUpdated: Long = System.currentTimeMillis()
)
