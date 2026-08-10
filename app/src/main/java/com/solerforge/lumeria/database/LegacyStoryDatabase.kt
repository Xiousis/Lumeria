package com.solerforge.lumeria.database

import com.solerforge.lumeria.R
import com.solerforge.lumeria.models.StoryArc
import com.solerforge.lumeria.models.StoryChoiceOption
import com.solerforge.lumeria.models.StoryEvent

object LegacyStoryDatabase {

    val mainArcs = listOf(
        StoryArc(
            id = 1001,
            title = "A New Star Rises",
            requiredLevel = 1,
            description = "You awake in the Celestial Plains, chosen by the stars.",
            locationName = "Celestial Plains",
            events = listOf(
                StoryEvent.Dialogue("Oracle", "The stars have aligned. A new successor has been chosen."),
                StoryEvent.Battle("Astral Slime"),
                StoryEvent.Dialogue("Oracle", "Prove your worth in the starlight."),
                StoryEvent.Battle("The Star Sovereign", isBoss = true)
            ),
            rewardXp = 5000, rewardGold = 10000, rewardTitle = "Star-Born"
        ),
        StoryArc(
            id = 1002,
            title = "The Ancient Whisper",
            requiredLevel = 21,
            description = "The trees of the Arboretum speak of a forgotten era.",
            locationName = "Ancient Arboretum",
            events = listOf(
                StoryEvent.Dialogue("Warden", "The forest remembers the founder. Do you?"),
                StoryEvent.Battle("Elder Treant"),
                StoryEvent.Battle("The Ancient Oak", isBoss = true)
            ),
            rewardXp = 15000, rewardGold = 25000
        ),
        StoryArc(
            id = 1003,
            title = "Refracted Truths",
            requiredLevel = 41,
            description = "Journey into the Prismatic Depths to see through the void.",
            locationName = "Prismatic Depths",
            events = listOf(
                StoryEvent.Dialogue("Crystal Spirit", "Light is only half of the story."),
                StoryEvent.Battle("Emerald Crawler"),
                StoryEvent.Battle("Prismatic Queen", isBoss = true)
            ),
            rewardXp = 40000, rewardGold = 50000
        ),
        StoryArc(
            id = 1004,
            title = "The Unbreakable Peak",
            requiredLevel = 61,
            description = "Challenge the Ironbound Peaks, where Xious once stood.",
            locationName = "Ironbound Peaks",
            events = listOf(
                StoryEvent.Dialogue("Mountaineer", "The air here is thinner than your resolve."),
                StoryEvent.Battle("Peak Sentinel"),
                StoryEvent.Battle("The Iron Titan", isBoss = true)
            ),
            rewardXp = 100000, rewardGold = 100000
        ),
        StoryArc(
            id = 1005,
            title = "The Eternal Mist",
            requiredLevel = 81,
            description = "Face the horrors that never sleep in the Fen.",
            locationName = "Eternal Fen",
            events = listOf(
                StoryEvent.Dialogue("Mist Walker", "Some things in this swamp are older than time."),
                StoryEvent.Battle("Bog-Behemoth"),
                StoryEvent.Battle("Fen Horror", isBoss = true)
            ),
            rewardXp = 250000, rewardGold = 250000
        ),
        StoryArc(
            id = 1006,
            title = "The Captured Sun",
            requiredLevel = 101,
            description = "Reclaim the power of the sun from Solaris.",
            locationName = "Solaris Volcano",
            events = listOf(
                StoryEvent.Dialogue("Ember Priest", "The flame of the successor must burn brighter than the sun."),
                StoryEvent.Battle("Ember-Drake"),
                StoryEvent.Battle("Solaris Drake", isBoss = true)
            ),
            rewardXp = 750000, rewardGold = 500000
        ),
        StoryArc(
            id = 1007,
            title = "The Spire of Souls",
            requiredLevel = 121,
            description = "Climb the Abyssal Spire to face the Magister.",
            locationName = "Abyssal Spire",
            events = listOf(
                StoryEvent.Dialogue("Soul Harvester", "Your soul is but a flicker in the void."),
                StoryEvent.Battle("Spire-Guard"),
                StoryEvent.Battle("Abyssal Magister", isBoss = true)
            ),
            rewardXp = 2000000, rewardGold = 1000000
        ),
        StoryArc(
            id = 1008,
            title = "The Storm's Eye",
            requiredLevel = 141,
            description = "Conquer the Floating Isles and tame the Zephyr.",
            locationName = "The Floating Isles",
            events = listOf(
                StoryEvent.Dialogue("Sky Dancer", "Only those who fly can truly lead."),
                StoryEvent.Battle("Thunder-Bird"),
                StoryEvent.Battle("Grand Zephyr", isBoss = true)
            ),
            rewardXp = 5000000, rewardGold = 2500000
        ),
        StoryArc(
            id = 1009,
            title = "The Drowned Legacy",
            requiredLevel = 156,
            description = "Dive into the Sunken City to find the founder's secrets.",
            locationName = "Sunken City of Lumeria",
            events = listOf(
                StoryEvent.Dialogue("Sunken Guard", "The capital fell, but the legacy remains."),
                StoryEvent.Battle("Deep-Sea Horror"),
                StoryEvent.Battle("The Deep One", isBoss = true)
            ),
            rewardXp = 12000000, rewardGold = 5000000
        ),
        StoryArc(
            id = 1010,
            title = "The River of Time",
            requiredLevel = 171,
            description = "Navigate the Valley of Echoes where time fractures.",
            locationName = "Valley of Echoes",
            events = listOf(
                StoryEvent.Dialogue("Echo of Time", "Tick... tick... the successor faces the end."),
                StoryEvent.Battle("Memory-Ghost"),
                StoryEvent.Battle("Chronos", isBoss = true)
            ),
            rewardXp = 30000000, rewardGold = 10000000
        ),
        StoryArc(
            id = 1011,
            title = "The Binding of Fate",
            requiredLevel = 186,
            description = "Face the Tundra and decide your own destiny.",
            locationName = "Frozen Tundra of Fate",
            events = listOf(
                StoryEvent.Dialogue("Fate Weaver", "Your destiny is a thread I hold in my hand."),
                StoryEvent.Battle("Ice-Drake"),
                StoryEvent.Battle("The Fate Binder", isBoss = true)
            ),
            rewardXp = 80000000, rewardGold = 25000000
        ),
        StoryArc(
            id = 1012,
            title = "The Origin and the End",
            requiredLevel = 196,
            description = "The final confrontation at the Genesis Nexus.",
            locationName = "The Genesis Nexus",
            events = listOf(
                StoryEvent.Dialogue("The Creator", "You have reached the beginning. But the end awaits."),
                StoryEvent.Battle("The Genesis Creator", isBoss = true),
                StoryEvent.Dialogue("The Creator", "You have surpassed all creation. But there is one final test."),
                StoryEvent.Dialogue("The Creator", "Face the legend you once were."),
                StoryEvent.Battle("The Echo of Xious", isBoss = true),
                StoryEvent.Dialogue("The Creator", "The cycle is complete. You are the True Legend.")
            ),
            rewardXp = 0, rewardGold = 0, rewardTitle = "Eternal Successor"
        )
    )

    val sideArcs = listOf(
        StoryArc(
            id = 2001,
            title = "Starry Night",
            requiredLevel = 10,
            description = "Collect fallen star shards for the Oracle.",
            locationName = "Celestial Plains",
            events = listOf(
                StoryEvent.Dialogue("Oracle", "The sky is losing its luster."),
                StoryEvent.Battle("Nova Sprite"),
                StoryEvent.Dialogue("Oracle", "The shards glow with potential.")
            ),
            rewardXp = 8000, rewardGold = 15000
        ),
        StoryArc(
            id = 2002,
            title = "The Root of Evil",
            requiredLevel = 30,
            description = "Purge the corruption from the Arboretum.",
            locationName = "Ancient Arboretum",
            events = listOf(
                StoryEvent.Dialogue("Dryad", "The roots are turning black."),
                StoryEvent.Battle("Vine-Wrapper"),
                StoryEvent.Battle("Corrupted Warden", isBoss = true)
            ),
            rewardXp = 25000, rewardGold = 40000
        )
    )

    fun getArc(id: Int): StoryArc? {
        return (mainArcs + sideArcs).find { it.id == id }
    }
}
