package com.example.lumeria.database

import com.example.lumeria.R
import com.example.lumeria.models.StoryArc
import com.example.lumeria.models.StoryChoiceOption
import com.example.lumeria.models.StoryEvent

object StoryDatabase {

    val arcs = listOf(
        StoryArc(
            id = 1,
            title = "A Sword Left Idle",
            requiredLevel = 1,
            description = "The Village Elder has had enough of your laziness.",
            backgroundId = R.drawable.elders_hut,
            locationName = "Training Fields",
            events = listOf(
                StoryEvent.Dialogue("Village Elder", "About time you picked up a sword, slacker."),
                StoryEvent.Choice(
                    "How do you respond to the Elder?",
                    listOf(
                        StoryChoiceOption("I've been training!", "The Elder scoffs, but hands you a few coins for your 'effort'.", rewardGold = 100),
                        StoryChoiceOption("Whatever you say, old man.", "The Elder scowls. \"Watch your tongue, cub!\"", rewardXp = 50)
                    )
                ),
                StoryEvent.Dialogue("Xious", "I've been training.", mood = "Happy"),
                StoryEvent.Battle("Training Bandit"),
                StoryEvent.Dialogue("Village Elder", "You call that a victory? Sleeping until noon isn't training."),
                StoryEvent.Battle("Village Troublemaker"),
                StoryEvent.Dialogue("Village Elder", "Fine. If you want to be a warrior, face a real veteran."),
                StoryEvent.Battle("Captain Garrick", isBoss = true)
            ),
            rewardXp = 500,
            rewardGold = 300,
            rewardTitle = "Aspiring Warrior"
        ),
        StoryArc(
            id = 2,
            title = "The Goblin Menace",
            requiredLevel = 5,
            description = "Goblins are raiding the outskirts. Find their leader.",
            backgroundId = R.drawable.goblin_forest_bg,
            locationName = "Goblin Forest",
            events = listOf(
                StoryEvent.Dialogue("Scout", "The forest is crawling with them. They're more organized than usual."),
                StoryEvent.Battle("Goblin Scout"),
                StoryEvent.Dialogue("Xious", "They're coming from the deeper woods.", mood = "Serious"),
                StoryEvent.Choice(
                    "Which path do you take through the woods?",
                    listOf(
                        StoryChoiceOption("The narrow, overgrown trail.", "You find a hidden stash of gold! But the path is dangerous.", rewardGold = 500),
                        StoryChoiceOption("The main road.", "You find a scout's markings. You learn more about their movements.", rewardXp = 200)
                    )
                ),
                StoryEvent.Battle("Goblin Marauder"),
                StoryEvent.Dialogue("Scout", "That's their leader! Skarr the Raider is leading the pack!"),
                StoryEvent.Battle("Skarr the Raider", isBoss = true)
            ),
            rewardXp = 1500,
            rewardGold = 1000,
            rewardTitle = "Goblinbane"
        ),
        StoryArc(
            id = 3,
            title = "Crystals in the Dark",
            requiredLevel = 10,
            description = "An ancient guardian has awakened in the caverns.",
            backgroundId = R.drawable.crystal_caverns_bg,
            locationName = "Crystal Caverns",
            events = listOf(
                StoryEvent.Dialogue("Miner", "Something ancient has awakened. The crystals are humming."),
                StoryEvent.Battle("Crystal Slime"),
                StoryEvent.Dialogue("Xious", "The deeper I go, the colder it gets."),
                StoryEvent.Choice(
                    "You find a vein of rare crystals. What do you do?",
                    listOf(
                        StoryChoiceOption("Mine them quickly.", "You gain some valuable shards to sell later.", rewardGold = 1500),
                        StoryChoiceOption("Leave them be. Respect the cavern.", "A faint warmth fills you. You feel more experienced.", rewardXp = 800)
                    )
                ),
                StoryEvent.Battle("Cave Stalker"),
                StoryEvent.Battle("The Crystal Warden", isBoss = true)
            ),
            rewardXp = 3000,
            rewardGold = 2500
        ),
        StoryArc(
            id = 4,
            title = "The Fallen Knight",
            requiredLevel = 15,
            description = "A tragic encounter with a hero lost to darkness.",
            backgroundId = R.drawable.dark_citadel_bg,
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Villager", "Sir Dorian was our finest defender..."),
                StoryEvent.Battle("Undead Squire"),
                StoryEvent.Choice(
                    "You find Sir Dorian's old journal. Do you read it?",
                    listOf(
                        StoryChoiceOption("Read it. Understand his fall.", "The knowledge of his descent into darkness strengthens your resolve.", rewardXp = 1500),
                        StoryChoiceOption("Close it. Let him rest.", "You find a hidden purse of gold tucked inside the cover.", rewardGold = 2500)
                    )
                ),
                StoryEvent.Dialogue("Xious", "His armor... it's rusted and black."),
            ),
            rewardXp = 5000,
            rewardGold = 4000,
            rewardTitle = "Defender"
        ),
        StoryArc(
            id = 5,
            title = "Earthshaker's Wrath",
            requiredLevel = 20,
            description = "The mountains are trembling. A warlord has arrived.",
            backgroundId = R.drawable.stonehold_pass_bg,
            locationName = "Stonehold Pass",
            events = listOf(
                StoryEvent.Dialogue("Village Elder", "You're still alive?"),
                StoryEvent.Dialogue("Xious", "Good to see you too."),
                StoryEvent.Dialogue("Village Elder", "Don't get sentimental. The refugees say the earth itself is angry."),
                StoryEvent.Battle("Stone Golem"),
                StoryEvent.Dialogue("Xious", "These tremors... they aren't natural."),
                StoryEvent.Battle("Mountain Giant"),
                StoryEvent.Dialogue("Grondar", "Flesh and bone will crumble before the mountain!"),
                StoryEvent.Battle("Grondar Earthshaker", isBoss = true)
            ),
            rewardXp = 8000,
            rewardGold = 6000
        ),
        StoryArc(
            id = 6,
            title = "Swamp Stalker",
            requiredLevel = 28,
            description = "A mutated beast is terrorizing the Shadow Marsh.",
            backgroundId = R.drawable.shadow_marsh_bg,
            locationName = "Shadow Marsh",
            events = listOf(
                StoryEvent.Dialogue("Herbalist", "The water is turning black. Mirefang is feeding again."),
                StoryEvent.Battle("Poison Toad"),
                StoryEvent.Dialogue("Xious", "The mist is so thick I can barely see."),
                StoryEvent.Battle("Marsh Dweller"),
                StoryEvent.Dialogue("Herbalist", "There! The beast rises from the sludge!"),
                StoryEvent.Battle("Mirefang", isBoss = true)
            ),
            rewardXp = 12000,
            rewardGold = 10000
        ),
        StoryArc(
            id = 7,
            title = "Keeper of Embers",
            requiredLevel = 35,
            description = "The volcanic forge must be reclaimed.",
            backgroundId = R.drawable.forge_intro_bg,
            locationName = "Dragon Peaks",
            events = listOf(
                StoryEvent.Dialogue("Blacksmith", "The Warden has taken the Eternal Flame."),
                StoryEvent.Battle("Fire Elemental"),
                StoryEvent.Dialogue("Xious", "The air is burning my lungs."),
                StoryEvent.Battle("Lava Serpent"),
                StoryEvent.Dialogue("Infernal Warden", "None shall wield the fire but the worthy!"),
                StoryEvent.Battle("Infernal Warden", isBoss = true)
            ),
            rewardXp = 18000,
            rewardGold = 15000
        ),
        StoryArc(
            id = 8,
            title = "Mechanical Menace",
            requiredLevel = 42,
            description = "A forgotten mining construct has gone haywire.",
            backgroundId = R.drawable.main_map_v2,
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Engineer", "The Juggernaut has activated! It's destroying everything!"),
                StoryEvent.Battle("Steam Sentry"),
                StoryEvent.Dialogue("Xious", "Steel and steam... I'll need to hit it hard."),
                StoryEvent.Battle("Gear Golem"),
                StoryEvent.Battle("The Iron Juggernaut", isBoss = true)
            ),
            rewardXp = 25000,
            rewardGold = 20000,
            rewardTitle = "Ironbreaker"
        ),
        StoryArc(
            id = 9,
            title = "Warchief's Incursion",
            requiredLevel = 50,
            description = "The clans are united under a new banner.",
            backgroundId = R.drawable.stonehold_pass_bg,
            locationName = "Stonehold Pass",
            events = listOf(
                StoryEvent.Dialogue("Commander", "Krag has united the northern tribes. They're at our gates!"),
                StoryEvent.Battle("Elite Orc"),
                StoryEvent.Dialogue("War Chief Krag", "Your world is soft. We will take what is ours!"),
                StoryEvent.Battle("War Chief Krag", isBoss = true)
            ),
            rewardXp = 35000,
            rewardGold = 30000
        ),
        StoryArc(
            id = 10,
            title = "The Forgotten King",
            requiredLevel = 58,
            description = "Maldrake has returned to claim his ancient throne.",
            backgroundId = R.drawable.dark_citadel_bg,
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Village Elder", "So, you've made it this far. Impressive."),
                StoryEvent.Dialogue("Ghost", "Long live the King... the King of Ash."),
                StoryEvent.Battle("Spectral Knight"),
                StoryEvent.Dialogue("King Maldrake", "You stand in my court, mortal. Kneel."),
                StoryEvent.Battle("King Maldrake", isBoss = true)
            ),
            rewardXp = 50000,
            rewardGold = 45000
        ),
        StoryArc(
            id = 11,
            title = "Tyrant of the Skies",
            requiredLevel = 65,
            description = "An Elder Wyvern has claimed the Peaks as its nest.",
            backgroundId = R.drawable.dragon_peaks_bg,
            locationName = "Dragon Peaks",
            events = listOf(
                StoryEvent.Dialogue("Hunter", "The shadows on the ground... they're too big to be clouds."),
                StoryEvent.Battle("Wyvern Scout"),
                StoryEvent.Dialogue("Elder Wyvern Tyrant", "*A deafening roar shakes the mountain*"),
                StoryEvent.Battle("Elder Wyvern Tyrant", isBoss = true)
            ),
            rewardXp = 75000,
            rewardGold = 60000
        ),
        StoryArc(
            id = 12,
            title = "Magister's Ambition",
            requiredLevel = 72,
            description = "Veyra is performing a ritual to tear the sky open.",
            backgroundId = R.drawable.dark_citadel_bg,
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Acolyte", "The power... she's drawing too much from the void!"),
                StoryEvent.Battle("Void Cultist"),
                StoryEvent.Dialogue("Grand Magister Veyra", "You cannot stop the inevitable progression of knowledge!"),
                StoryEvent.Battle("Grand Magister Veyra", isBoss = true)
            ),
            rewardXp = 100000,
            rewardGold = 85000
        ),
        StoryArc(
            id = 13,
            title = "Watcher of Time",
            requiredLevel = 78,
            description = "Reality is fracturing at the edge of the world.",
            backgroundId = R.drawable.main_map_v2,
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Voice", "Tick... tick... your time is running out."),
                StoryEvent.Battle("Chrono Stalker"),
                StoryEvent.Dialogue("Xious", "The ground is literally disappearing beneath me!"),
                StoryEvent.Battle("Chrono Sentinel", isBoss = true)
            ),
            rewardXp = 130000,
            rewardGold = 110000
        ),
        StoryArc(
            id = 14,
            title = "Shadow Ruler",
            requiredLevel = 84,
            description = "Lord Umbra has plunged the Citadel into eternal night.",
            backgroundId = R.drawable.dark_citadel_bg,
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Shadow", "Welcome to the darkness. You'll never leave."),
                StoryEvent.Battle("Shadow Assassin"),
                StoryEvent.Dialogue("Lord Umbra", "I am the night that consumes your sun."),
                StoryEvent.Battle("Lord Umbra", isBoss = true)
            ),
            rewardXp = 180000,
            rewardGold = 150000
        ),
        StoryArc(
            id = 15,
            title = "The First Hero",
            requiredLevel = 90,
            description = "Aurelius has returned to judge the current era.",
            backgroundId = R.drawable.grand_arena_bg,
            locationName = "Grand Arena",
            events = listOf(
                StoryEvent.Dialogue("Village Elder", "You've become a fine warrior, Xious. But can you face the first?"),
                StoryEvent.Dialogue("Aurelius", "Show me the strength of this generation."),
                StoryEvent.Battle("Golden Construct"),
                StoryEvent.Dialogue("Aurelius", "Not bad... but how do you handle my elites?"),
                StoryEvent.Battle("Golden Construct"), // Representing Elite
                StoryEvent.Dialogue("Aurelius", "Enter the Hall of Heroes. Face your final trial."),
                StoryEvent.Battle("Aurelius", isBoss = true)
            ),
            rewardXp = 250000,
            rewardGold = 200000,
            rewardTitle = "Champion"
        ),
        StoryArc(
            id = 16,
            title = "The Citadel Inquisitor",
            requiredLevel = 94,
            description = "Kael is purging the weak before Xarthos returns.",
            backgroundId = R.drawable.dark_citadel_bg,
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Victim", "He calls it mercy... but it's just slaughter."),
                StoryEvent.Battle("Inquisition Guard"),
                StoryEvent.Dialogue("High Inquisitor Kael", "I will purify this world with holy fire!"),
                StoryEvent.Battle("High Inquisitor Kael", isBoss = true)
            ),
            rewardXp = 350000,
            rewardGold = 300000
        ),
        StoryArc(
            id = 17,
            title = "Rift Breaker",
            requiredLevel = 95,
            description = "The Rift Sovereign is tearing reality apart.",
            backgroundId = R.drawable.main_map_v2,
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Prophet", "The Sovereign comes. Your reality is a fragile dream."),
                StoryEvent.Battle("Rift Spawn"),
                StoryEvent.Battle("Rift Sovereign", isBoss = true)
            ),
            rewardXp = 500000,
            rewardGold = 450000
        ),
        StoryArc(
            id = 18,
            title = "The Void Prophet",
            requiredLevel = 96,
            description = "The harbinger of the end finally reveals themselves.",
            backgroundId = R.drawable.dark_citadel_bg,
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Void Prophet", "The end is not a destination, but a state of being."),
                StoryEvent.Battle("Void Horror"),
                StoryEvent.Battle("Void Prophet", isBoss = true)
            ),
            rewardXp = 750000,
            rewardGold = 700000
        ),
        StoryArc(
            id = 19,
            title = "Lord Xarthos Returns",
            requiredLevel = 98,
            description = "Xarthos has ascended beyond mortality.",
            backgroundId = R.drawable.dark_citadel_bg,
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Ascended Xarthos", "You thought you had won? I am eternal."),
                StoryEvent.Dialogue("Xious", "I'll just have to defeat you again."),
                StoryEvent.Battle("Ascended Xarthos", isBoss = true)
            ),
            rewardXp = 1000000,
            rewardGold = 1000000
        ),
        StoryArc(
            id = 20,
            title = "Beyond the Void",
            requiredLevel = 100,
            description = "The final confrontation with the end of all things.",
            backgroundId = R.drawable.dark_citadel_bg,
            locationName = "Dark Citadel",
            events = listOf(
                StoryEvent.Dialogue("Village Elder", "I warned you once, slacker... some doors are not meant to be opened."),
                StoryEvent.Dialogue("Village Elder", "The void is hungry. It has sent its first reaper to claim what remains of us."),
                StoryEvent.Battle("Void Reaper Prime", isBoss = true),
                StoryEvent.Dialogue("Xious", "It's getting stronger... the air itself is turning into nothing."),
                StoryEvent.Choice(
                    "This is it. How do you face the end?",
                    listOf(
                        StoryChoiceOption("With unwavering hope.", "A blinding light erupts from your soul!", rewardXp = 100000),
                        StoryChoiceOption("With cold, calculated fury.", "Your blade burns with a dark intensity!", rewardGold = 100000)
                    )
                ),
                StoryEvent.Dialogue("Xious", "If this is the end, I'm going out on my feet!"),
                StoryEvent.Battle("The Last Herald", isBoss = true),
                StoryEvent.Dialogue("The Void", "*Whispers from a thousand dead worlds echo in your mind*"),
                StoryEvent.Dialogue("The Void", "Xious... your journey ends in silence."),
                StoryEvent.Battle("Ascended Xarthos Remnant", isBoss = true),
                StoryEvent.Dialogue("Xarthos", "Even my shadows are more than you can handle, little warrior."),
                StoryEvent.Dialogue("Xarthos", "Behold... the one who consumes even the darkness."),
                StoryEvent.Battle("World Eater Phase 1", isBoss = true),
                StoryEvent.Dialogue("World Eater", "I existed before the first sunrise. I existed before memory."),
                StoryEvent.Battle("World Eater Phase 2", isBoss = true),
                StoryEvent.Dialogue("World Eater", "Your reality is but a blink in my eternal hunger."),
                StoryEvent.Battle("World Eater Final Form", isBoss = true),
                StoryEvent.Dialogue("World Eater", "Wait... I am eternal... I existed before memory!"),
                StoryEvent.Dialogue("Xious", "Then you'll be remembered for one thing."),
                StoryEvent.Dialogue("World Eater", "What is that?"),
                StoryEvent.Dialogue("Xious", "Losing.")
            ),
            rewardXp = 1500000,
            rewardGold = 1500000,
            rewardTitle = "Savior of the Realm"
        )
    ).sortedBy { it.id }

    fun getArc(id: Int): StoryArc? {
        return arcs.find { it.id == id }
    }
}
