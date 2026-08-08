package com.solerforge.lumeria.database

import com.solerforge.lumeria.models.StoryArc
import com.solerforge.lumeria.models.StoryEvent

object SecretBossDatabase {

    val secretArcs = listOf(
        StoryArc(
            id = 999,
            title = "The Source of All Things",
            requiredLevel = 100,
            description = "A rift has opened where Lumeria began.",
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("The Voice", "You have collected the shards of divinity..."),
                StoryEvent.Dialogue("The Voice", "You have defied the void and the end."),
                StoryEvent.Dialogue("The Forgotten Creator", "But are you a legend, Xious? Or merely a draft?"),
                StoryEvent.Dialogue("Xious", "Who are you?"),
                StoryEvent.Dialogue("The Forgotten Creator", "I am the hand that drew the stars. I am the silence between words."),
                StoryEvent.Dialogue("The Forgotten Creator", "Prove to me that this world is worth finishing."),
                StoryEvent.Battle("The Forgotten Creator", isBoss = true),
                StoryEvent.Dialogue("The Forgotten Creator", "Magnificent..."),
                StoryEvent.Dialogue("The Forgotten Creator", "The draft is complete. Lumeria is yours, Immortal Hero.")
            ),
            rewardXp = 0,
            rewardGold = 0,
            rewardTitle = "Creator's Equal"
        )
    )

    fun getArc(id: Int): StoryArc? {
        return secretArcs.find { it.id == id }
    }
}
