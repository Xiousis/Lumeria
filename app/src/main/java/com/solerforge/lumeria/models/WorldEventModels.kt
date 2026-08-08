package com.solerforge.lumeria.models

import kotlinx.serialization.Serializable

@Serializable
data class PlayerBuff(
    val type: BuffType,
    val value: Double,
    val battlesRemaining: Int,
    val description: String,
    val isFood: Boolean = false
)

enum class BuffType {
    Damage, Defense, Experience, Gold, HealthRegen, ManaRegen, Agility
}

@Serializable
sealed class EventOutcome {
    @Serializable
    data class Reward(val gold: Long = 0L, val xp: Long = 0L, val message: String) : EventOutcome()
    @Serializable
    data class Penalty(val goldLost: Long = 0L, val hpLost: Int = 0, val message: String) : EventOutcome()
    @Serializable
    data class Buff(val buff: PlayerBuff, val message: String) : EventOutcome()
    @Serializable
    data class Ambush(val enemyName: String, val message: String) : EventOutcome()
    @Serializable
    data class Nothing(val message: String) : EventOutcome()
}

data class EventOption(
    val text: String,
    val outcome: EventOutcome,
    val goldCost: Long = 0L,
    val requiredItem: String? = null
)

data class WorldEvent(
    val id: Int,
    val title: String,
    val description: String,
    val options: List<EventOption>
)
