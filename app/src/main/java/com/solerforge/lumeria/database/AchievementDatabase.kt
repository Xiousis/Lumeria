package com.solerforge.lumeria.database

import com.solerforge.lumeria.data.Achievement

object AchievementDatabase {
    val achievements = listOf(
        // --- CORE PROGRESSION ---
        Achievement("first_win", "First Blood", "Win your very first battle in Lumeria.", "⚔️"),
        Achievement("reach_lvl_5", "Rising Power", "Reach Level 5 and begin your ascent.", "📈"),
        Achievement("reach_lvl_100", "Immortal Legend", "Reach Level 100 and transcend mortality.", "👑"),
        Achievement("defeat_boss", "Regional Hero", "Defeat a regional boss and clear a zone.", "🏆"),
        Achievement("defeat_xarthos", "God-Slayer", "Defeat Lord Xarthos and end the void's threat.", "🌌"),
        
        // --- WEALTH & NATION ---
        Achievement("collect_1k_gold", "Gold Miner", "Accumulate 1,000 gold pieces.", "💰"),
        Achievement("collect_1m_gold", "Tycoon of Lumeria", "Accumulate 1,000,000 gold pieces.", "💎"),
        Achievement("max_prosperity", "Nation Builder", "Reach Maximum Nation Prosperity (Level 10).", "🏰"),

        // --- GUILD TRIALS ---
        Achievement("join_guild", "House Member", "Join one of the three Great Houses.", "🛡️"),
        Achievement("guild_rank_5", "Elite Member", "Reach Rank 5 in your chosen Guild.", "🎖️"),
        Achievement("guild_rank_10", "Guild Champion", "Defeat your Master and reach Rank 10.", "🥇"),

        // --- ARENA GLORY ---
        Achievement("arena_bronze", "Bronze Gladiator", "Defeat all Bronze Rank arena opponents.", "🥉"),
        Achievement("arena_gold", "Gold Gladiator", "Defeat all Gold Rank arena opponents.", "🥇"),
        Achievement("arena_master", "Grand Champion", "Defeat the Eternal Champion in the Master Rank.", "🏆"),

        // --- BATTLE TOWER ---
        Achievement("tower_25", "Tower Initiate", "Reach Floor 25 of the Tower of Trials.", "🗼"),
        Achievement("tower_50", "Tower Veteran", "Reach Floor 50 of the Tower of Trials.", "⚔️"),
        Achievement("tower_100", "Tower Conqueror", "Conquer all 100 floors of the Battle Tower.", "🌋"),

        // --- FISHING & COLLECTION ---
        Achievement("fishing_10", "Expert Angler", "Reach Fishing Level 10.", "🎣"),
        Achievement("catch_god_fish", "Mythical Catch", "Catch a God Tier fish from the pond.", "✨"),
        Achievement("god_tier_collector", "Favored by Gods", "Collect all 10 God Tier items.", "⭐"),
        
        // --- KNOWLEDGE ---
        Achievement("bestiary_10", "Student of Nature", "Record 10 different enemies in your Bestiary.", "📖"),
        Achievement("bestiary_50", "Master of Secrets", "Record 50 different enemies in your Bestiary.", "📜"),

        // --- NEW CUMULATIVE & CHALLENGE ACHIEVEMENTS ---
        Achievement("death_10", "Unlucky", "Face defeat 10 times. It happens to the best of us.", "💀"),
        Achievement("death_100", "True Persistence", "Face defeat 100 times. Never give up, Xious!", "🕯️"),
        Achievement("gold_10k", "Merchant's Friend", "Amass 10,000 gold pieces.", "💰"),
        Achievement("gold_100k", "Wealthy Adventurer", "Amass 100,000 gold pieces.", "💎"),
        Achievement("kills_100", "Monster Hunter", "Defeat 100 monsters in your journey.", "⚔️"),
        Achievement("kills_1000", "Legendary Slayer", "Defeat 1,000 monsters. The realm is safer now.", "🐉"),
        Achievement("gamble_10", "Lucky Streak", "Win 10 games of Blackjack at the Gambling House.", "🃏"),
        Achievement("gamble_50", "High Roller", "Win 50 games of Blackjack. Lady Luck is on your side.", "🎰"),
        Achievement("fish_50", "Fish Enthusiast", "Catch a total of 50 fish.", "🐟"),
        Achievement("fish_200", "Master of the Sea", "Catch a total of 200 fish. You're a pro!", "🌊"),
        Achievement("upgrade_5", "Apprentice Smith", "Successfully upgrade your equipment 5 times.", "🔨"),
        Achievement("upgrade_25", "Master Smith", "Successfully upgrade your equipment 25 times.", "⚒️"),
        Achievement("event_10", "Social Butterfly", "Encounter 10 world events.", "🗣️"),
        Achievement("event_50", "World Historian", "Encounter 50 world events. You've seen it all.", "📜"),
        Achievement("max_stats", "Perfectionist", "Spend all stat points at Level 100.", "✨"),
        Achievement("side_arcs_5", "Side Quest Hero", "Complete 5 different side story arcs.", "🌟"),
        Achievement("titles_10", "Title Collector", "Unlock 10 different legendary titles.", "🏷️"),
        Achievement("bank_100k", "Safe & Sound", "Store 100,000 gold safely in the Ironclad Bank.", "🏦"),
        Achievement("respec_once", "Self-Reflection", "Ritual of Rebirth: Reset your stats for the first time.", "🔮"),
        Achievement("legendary_gear", "Forged in Fire", "Possess an item upgraded to its maximum (+5).", "🔥"),

        // --- MONSTER ACHIEVEMENTS ---
        Achievement("villages_plundered_1", "Village Raider", "Plunder your first village.", "🏘️"),
        Achievement("villages_plundered_10", "Scourge of the Coast", "Plunder 10 villages.", "🔥"),
        Achievement("souls_harvested_100", "Soul Collector", "Harvest 100 souls from fallen heroes.", "👻"),
        Achievement("souls_harvested_1000", "Reaper of Lumeria", "Harvest 1,000 souls.", "💀"),
        Achievement("hero_parties_defeated_5", "Party Crasher", "Defeat 5 parties of aspiring heroes.", "⚔️"),
        Achievement("hero_parties_defeated_25", "Bane of Legends", "Defeat 25 parties of legendary heroes.", "🏆")
    )
}
