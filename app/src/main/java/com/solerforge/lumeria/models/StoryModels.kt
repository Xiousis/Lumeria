package com.solerforge.lumeria.models

sealed class StoryEvent {
    data class Dialogue(val speaker: String, val text: String, val mood: String = "Neutral") : StoryEvent()
    data class Battle(val enemyName: String, val isBoss: Boolean = false) : StoryEvent()
    data class Choice(
        val prompt: String,
        val options: List<StoryChoiceOption>
    ) : StoryEvent()
}

data class StoryChoiceOption(
    val text: String,
    val outcomeMessage: String,
    val rewardGold: Int = 0,
    val rewardXp: Int = 0,
    val nextEventIndex: Int? = null
)

data class StoryArc(
    val id: Int,
    val title: String,
    val requiredLevel: Int,
    val description: String,
    val events: List<StoryEvent>,
    val backgroundId: Int? = null,
    val locationName: String? = null,
    val rewardXp: Int = 0,
    val rewardGold: Int = 0,
    val rewardTitle: String? = null
)
