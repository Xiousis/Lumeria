package com.solerforge.lumeria.database

import com.solerforge.lumeria.models.*

object WorldEventDatabase {
    val events = listOf(
        WorldEvent(
            id = 1,
            title = "The Wounded Traveler",
            description = "You find a traveler slumped against a tree, clutching a bleeding leg. He looks up weakly as you approach.",
            options = listOf(
                EventOption(
                    "Offer a Health Potion",
                    EventOutcome.Reward(xp = 2500L, message = "The traveler drinks the potion and regains his strength. \"Thank you, kind soul! Take this gold as a token of my gratitude.\" (Gained 1000G & XP)")
                ),
                EventOption(
                    "Bandage his wounds",
                    EventOutcome.Buff(
                        PlayerBuff(BuffType.Defense, 1.1, 2, "Traveler's Blessing"),
                        "You skillfully patch him up. He blesses your journey. (Defense increased for 2 battles)"
                    )
                ),
                EventOption(
                    "Walk away",
                    EventOutcome.Nothing("You decide not to get involved. The world is a dangerous place.")
                )
            )
        ),
        WorldEvent(
            id = 2,
            title = "A Mysterious Beggar",
            description = "An old man in tattered rags stops you. \"Spare some coin for a hungry soul? I have stories to tell... and perhaps a bit of luck to share.\"",
            options = listOf(
                EventOption(
                    "Give 500 Gold",
                    EventOutcome.Buff(
                        PlayerBuff(BuffType.Experience, 1.25, 2, "Beggar's Wisdom"),
                        "The man smiles, revealing missing teeth. \"Wisdom comes to those who give.\" (XP Gain +25% for 2 battles)"
                    )
                ),
                EventOption(
                    "Give 100 Gold",
                    EventOutcome.Reward(xp = 500L, message = "He nods gratefully. \"Every bit helps. Safe travels!\"")
                ),
                EventOption(
                    "Refuse",
                    EventOutcome.Penalty(hpLost = 10, message = "The beggar scowls. \"May your luck be as dry as my stomach!\" You feel a sudden chill. (Lost 10 HP)")
                )
            )
        ),
        WorldEvent(
            id = 3,
            title = "Hidden Bandit Camp",
            description = "You stumble upon a small, hidden camp. Three men are sharpening their blades. They spot you immediately.",
            options = listOf(
                EventOption(
                    "Attack them first",
                    EventOutcome.Ambush("Elite Orc", "You charge in! But these aren't ordinary bandits... (FORCED BATTLE)")
                ),
                EventOption(
                    "Try to sneak away",
                    EventOutcome.Penalty(goldLost = 200L, message = "You trip on a branch! They hear you and demand a 'toll' to let you pass. (Lost 200 Gold)")
                ),
                EventOption(
                    "Attempt to parley",
                    EventOutcome.Nothing("They laugh at your words, but are too lazy to give chase. You escape... this time.")
                )
            )
        ),
        WorldEvent(
            id = 4,
            title = "Ancient Shrine",
            description = "A moss-covered shrine stands in a clearing. It hums with a faint, otherworldly energy.",
            options = listOf(
                EventOption(
                    "Pray at the shrine",
                    EventOutcome.Buff(
                        PlayerBuff(BuffType.Damage, 1.15, 3, "Shrine's Fervor"),
                        "A warm light envelops you. You feel a surge of power! (Damage +15% for 3 battles)"
                    )
                ),
                EventOption(
                    "Offer some Gold (1000G)",
                    EventOutcome.Buff(
                        PlayerBuff(BuffType.HealthRegen, 1.0, 5, "Celestial Protection"),
                        "The shrine glows brightly. Your wounds seem to close faster. (HP fully restored!)"
                    )
                ),
                EventOption(
                    "Investigate closely",
                    EventOutcome.Reward(gold = 2000L, message = "You find a hidden compartment at the base! (Found 2000 Gold)")
                )
            )
        ),
        WorldEvent(
            id = 5,
            title = "A Shady Merchant",
            description = "A merchant with a wide, suspicious grin pulls you aside. \"Interested in a blind box? Only 2500 gold for a chance at glory!\"",
            options = listOf(
                EventOption(
                    "Buy the box (2500G)",
                    EventOutcome.Reward(xp = 10000L, message = "You open the box... it's filled with ancient scrolls! (Gained 10,000 XP)")
                ),
                EventOption(
                    "Ignore him",
                    EventOutcome.Nothing("You've seen enough 'blind boxes' to know better.")
                )
            )
        )
    )

    fun getRandomEvent(): WorldEvent = events.random()
}
