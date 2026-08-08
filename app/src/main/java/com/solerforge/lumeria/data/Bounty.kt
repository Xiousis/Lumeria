package com.solerforge.lumeria.data

import kotlinx.serialization.Serializable

@Serializable
data class Bounty(
    val id: Int,
    val targetName: String,
    val level: Int,
    val description: String,
    val location: String, // Where they are found
    val rewardGold: Int,
    val rewardXp: Int,
    val portraitId: Int? = null
)
