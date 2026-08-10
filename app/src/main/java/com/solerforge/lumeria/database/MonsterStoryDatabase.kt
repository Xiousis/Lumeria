package com.solerforge.lumeria.database

import com.solerforge.lumeria.R
import com.solerforge.lumeria.models.StoryArc
import com.solerforge.lumeria.models.StoryChoiceOption
import com.solerforge.lumeria.models.StoryEvent

object MonsterStoryDatabase {

    val mainArcs = listOf(
        StoryArc(
            id = 5001,
            title = "A Dark Awakening",
            requiredLevel = 1,
            description = "You emerge from the shadows. The human kingdom is ripe for the taking.",
            locationName = "The Slums",
            events = listOf(
                StoryEvent.Dialogue("Dark Voice", "Awaken, my servant. The age of humans is ending."),
                StoryEvent.Battle("Scared Villager"),
                StoryEvent.Dialogue("{PLAYER_NAME}", "The light... it burns, but their fear is delicious."),
                StoryEvent.Battle("City Guard Trainee"),
                StoryEvent.Battle("Sergeant Miller", isBoss = true)
            ),
            rewardXp = 5000, rewardGold = 10000, rewardTitle = "Harbinger"
        ),
        StoryArc(
            id = 5002,
            title = "Chaos in the Market",
            requiredLevel = 21,
            description = "Disrupt the kingdom's economy and spread terror.",
            locationName = "Market District",
            events = listOf(
                StoryEvent.Dialogue("Merchant", "Please! Take the gold, just spare my life!"),
                StoryEvent.Choice(
                    "What do you do with the merchant?",
                    listOf(
                        StoryChoiceOption("Kill him.", "His soul fuels your power.", rewardXp = 2000),
                        StoryChoiceOption("Rob him.", "His gold is now yours.", rewardGold = 5000)
                    )
                ),
                StoryEvent.Battle("Ironclad Guard"),
                StoryEvent.Battle("Captain Harlen", isBoss = true)
            ),
            rewardXp = 15000, rewardGold = 25000
        ),
        StoryArc(
            id = 5003,
            title = "Pruning the Gardens",
            requiredLevel = 41,
            description = "The Royal Gardens are a symbol of human vanity. Burn them.",
            locationName = "Royal Gardens",
            events = listOf(
                StoryEvent.Dialogue("Noble", "A monster in the gardens? Impossible! Guards!"),
                StoryEvent.Battle("Royal Sword-Master"),
                StoryEvent.Battle("Lady Elara", isBoss = true)
            ),
            rewardXp = 40000, rewardGold = 50000
        ),
        StoryArc(
            id = 5004,
            title = "Desecrating the Temple",
            requiredLevel = 61,
            description = "The gods of men cannot save them from you.",
            locationName = "Temple District",
            events = listOf(
                StoryEvent.Dialogue("High Priest", "The light will purge you, beast!"),
                StoryEvent.Battle("Templar Knight"),
                StoryEvent.Battle("Grand Inquisitor", isBoss = true)
            ),
            rewardXp = 100000, rewardGold = 100000
        ),
        StoryArc(
            id = 5005,
            title = "Storming the Court",
            requiredLevel = 81,
            description = "The King's defenders stand between you and the throne.",
            locationName = "The Royal Court",
            events = listOf(
                StoryEvent.Dialogue("King's Guard", "None shall pass! For the King!"),
                StoryEvent.Battle("Elite Paladin"),
                StoryEvent.Battle("General Ironheart", isBoss = true)
            ),
            rewardXp = 250000, rewardGold = 250000
        ),
        StoryArc(
            id = 5006,
            title = "The King's End",
            requiredLevel = 101,
            description = "Face the King of Lumeria in his final refuge.",
            locationName = "King's Bedroom",
            events = listOf(
                StoryEvent.Dialogue("King Lumeria", "So, the darkness finally reaches my door."),
                StoryEvent.Battle("Royal Guard Captain"),
                StoryEvent.Battle("King Lumeria", isBoss = true)
            ),
            rewardXp = 750000, rewardGold = 500000, rewardTitle = "Kingslayer"
        )
        // More arcs can be added up to level 200
    )

    val sideArcs = listOf(
        StoryArc(
            id = 6001,
            title = "Soul Harvest",
            requiredLevel = 10,
            description = "Collect 10 human souls for the dark ritual.",
            locationName = "The Slums",
            events = listOf(
                StoryEvent.Dialogue("Dark Voice", "The ritual requires essence. Bring it to me."),
                StoryEvent.Battle("Drunkard"),
                StoryEvent.Dialogue("{PLAYER_NAME}", "One more soul for the collection.")
            ),
            rewardXp = 8000, rewardGold = 15000
        )
    )

    fun getArc(id: Int): StoryArc? {
        return (mainArcs + sideArcs).find { it.id == id }
    }
}
