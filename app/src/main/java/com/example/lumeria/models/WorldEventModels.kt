package com.example.lumeria.models

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
    data class Reward(val gold: Int = 0, val xp: Int = 0, val message: String) : EventOutcome()
    @Serializable
    data class Penalty(val goldLost: Int = 0, val hpLost: Int = 0, val message: String) : EventOutcome()
    @Serializable
    data class Buff(val buff: PlayerBuff, val message: String) : EventOutcome()
    @Serializable
    data class Ambush(val enemyName: String, val message: String) : EventOutcome()
    @Serializable
    data class Nothing(val message: String) : EventOutcome()
}

data class EventOption(
    val text: String,
    val outcome: EventOutcome
)

data class WorldEvent(
    val id: Int,
    val title: String,
    val description: String,
    val options: List<EventOption>
)
