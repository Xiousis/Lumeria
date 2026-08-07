package com.example.lumeria.database

import com.example.lumeria.data.Bounty

object BountyDatabase {
    val bounties = listOf(
        Bounty(
            id = 1,
            targetName = "Silas the Strangler",
            level = 65,
            description = "A ruthless murderer known for ambushing travelers in the Shadow Marsh.",
            location = "Shadow Marsh",
            rewardGold = 25000,
            rewardXp = 15000
        ),
        Bounty(
            id = 2,
            targetName = "Kaelen Bloodscar",
            level = 68,
            description = "Lead of the Red Fang bandits. Wanted for multiple counts of arson in the Dragon Peaks.",
            location = "Dragon Peaks",
            rewardGold = 35000,
            rewardXp = 22000
        ),
        Bounty(
            id = 3,
            targetName = "The Silent Blade",
            level = 70,
            description = "An assassin-for-hire who has taken up residence in the Dark Citadel. High profile target.",
            location = "Dark Citadel",
            rewardGold = 50000,
            rewardXp = 30000
        ),
        Bounty(
            id = 4,
            targetName = "Iron-Jaw Morg",
            level = 72,
            description = "An escaped convict and former arena champion turned brigand leader.",
            location = "Stonehold Pass",
            rewardGold = 65000,
            rewardXp = 40000
        ),
        Bounty(
            id = 5,
            targetName = "Veyra the Forsaken",
            level = 75,
            description = "A rogue mage using forbidden dark arts. Extremely dangerous. Do not engage alone.",
            location = "Dark Citadel",
            rewardGold = 100000,
            rewardXp = 65000
        )
    )

    fun getBounty(id: Int): Bounty? = bounties.find { it.id == id }
}
