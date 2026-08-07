package com.example.lumeria.database

import com.example.lumeria.models.StoryArc
import com.example.lumeria.models.StoryEvent

object SideStoryDatabase {

    val kaelaArcs = listOf(
        StoryArc(
            id = 101,
            title = "The Crimson Huntress",
            requiredLevel = 12,
            description = "A fierce tribal warrior challenges you.",
            locationName = "Goblin Forest",
            events = listOf(
                StoryEvent.Dialogue("Village Elder", "There is a spearwoman causing trouble east of town."),
                StoryEvent.Dialogue("Kaela", "The forests whisper your name. I expected a hunter."),
                StoryEvent.Dialogue("Kaela", "Instead I find a village cub."),
                StoryEvent.Dialogue("Xious", "And yet you're talking to me."),
                StoryEvent.Dialogue("Kaela", "The wolf looks at every creature before deciding if it is prey."),
                StoryEvent.Battle("Tribal Scout"),
                StoryEvent.Dialogue("Kaela", "Good. Your blood has some fire in it. Prove you're worth teaching."),
                StoryEvent.Battle("Kaela Flameheart", isBoss = true)
            ),
            rewardXp = 5000,
            rewardGold = 3000,
            rewardTitle = "Forest Challenger"
        ),
        StoryArc(
            id = 102,
            title = "Trial of the Emberclaw",
            requiredLevel = 20,
            description = "Kaela tests your resolve in the mountain pass.",
            locationName = "Stonehold Pass",
            events = listOf(
                StoryEvent.Dialogue("Kaela", "My clan trusts strength. Not words. Not promises."),
                StoryEvent.Dialogue("Kaela", "Strength."),
                StoryEvent.Dialogue("Xious", "Then I'll prove it."),
                StoryEvent.Dialogue("Kaela", "Good. My people become hungry listening to speeches."),
                StoryEvent.Battle("Tribal Warrior"),
                StoryEvent.Battle("Emberclaw Guardian", isBoss = true)
            ),
            rewardXp = 8000,
            rewardGold = 5000
        ),
        StoryArc(
            id = 103,
            title = "Ashes of Memory",
            requiredLevel = 30,
            description = "Journey into the heart of Kaela's past.",
            locationName = "Shadow Marsh",
            events = listOf(
                StoryEvent.Dialogue("Xious", "You've been quiet."),
                StoryEvent.Dialogue("Kaela", "The ashes remember."),
                StoryEvent.Dialogue("Xious", "What does that mean?"),
                StoryEvent.Dialogue("Kaela", "The beast that burned my village. Its tracks still live inside my head."),
                StoryEvent.Dialogue("Xious", "Then let's finish this."),
                StoryEvent.Battle("Ashen Spirit"),
                StoryEvent.Battle("Void Scorcher", isBoss = true)
            ),
            rewardXp = 15000,
            rewardGold = 10000,
            rewardTitle = "Vengeance Seeker"
        ),
        StoryArc(
            id = 104,
            title = "The Pack Walks Together",
            requiredLevel = 40,
            description = "The final trial of the Crimson Huntress.",
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Kaela", "The final gate. The spirits await."),
                StoryEvent.Battle("Ancestral Guardian", isBoss = true),
                StoryEvent.Dialogue("Kaela", "The trial is finished. The spirits have spoken."),
                StoryEvent.Dialogue("Xious", "What did they say?"),
                StoryEvent.Dialogue("Kaela", "That you are stubborn."),
                StoryEvent.Dialogue("Xious", "Only stubborn?"),
                StoryEvent.Dialogue("Kaela", "Strong. Brave. Annoying."),
                StoryEvent.Dialogue("Xious", "There it is."),
                StoryEvent.Dialogue("Kaela", "Pack walks together. From this day forward. You are pack.")
            ),
            rewardXp = 30000,
            rewardGold = 25000,
            rewardTitle = "Flameheart's Kin"
        )
    )

    val allArcs = kaelaArcs // Expand with other characters later

    fun getArc(id: Int): StoryArc? {
        return allArcs.find { it.id == id }
    }
}
