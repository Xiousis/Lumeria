package com.solerforge.lumeria.models

import kotlinx.serialization.Serializable

@Serializable
data class Guild(
    val id: String = "",
    val name: String = "",
    val leaderId: String = "",
    val leaderName: String = "",
    val level: Int = 1,
    val xp: Long = 0,
    val membersCount: Int = 1,
    val maxMembers: Int = 20,
    val guildVault: Long = 0,
    val description: String = "A new guild in Lumeria.",
    val createdAt: Long = System.currentTimeMillis(),
    val isPublic: Boolean = true,
    val trophies: List<String> = emptyList(),
    val warRating: Int = 1000,
    val warWins: Int = 0,
    val warLosses: Int = 0,
    val announcement: String = "Welcome to the guild!"
)

@Serializable
data class GuildMember(
    val playerId: String = "",
    val playerName: String = "",
    val playerLevel: Int = 1,
    val rank: String = "Member",
    val contributionGold: Long = 0,
    val contributionXp: Long = 0,
    val joinedAt: Long = System.currentTimeMillis()
)
