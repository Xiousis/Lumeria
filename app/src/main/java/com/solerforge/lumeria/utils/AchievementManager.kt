package com.solerforge.lumeria.utils

import com.solerforge.lumeria.data.PlayerData

object AchievementManager {
    fun checkAchievements(playerData: PlayerData): List<String> {
        val newlyUnlocked = mutableListOf<String>()
        val current = playerData.unlockedAchievements

        // --- CORE PROGRESSION ---
        if ("first_win" !in current && playerData.killCounts.values.sum() > 0) newlyUnlocked.add("first_win")
        if ("reach_lvl_5" !in current && playerData.level >= 5) newlyUnlocked.add("reach_lvl_5")
        if ("reach_lvl_100" !in current && playerData.level >= 100) newlyUnlocked.add("reach_lvl_100")
        if ("defeat_boss" !in current && playerData.defeatedBosses.isNotEmpty()) newlyUnlocked.add("defeat_boss")
        if ("defeat_xarthos" !in current && playerData.defeatedBosses.contains("Lord Xarthos")) newlyUnlocked.add("defeat_xarthos")

        // --- WEALTH & NATION ---
        if ("collect_1k_gold" !in current && playerData.gold >= 1000) newlyUnlocked.add("collect_1k_gold")
        if ("collect_1m_gold" !in current && (playerData.gold + playerData.bankGold) >= 1000000) newlyUnlocked.add("collect_1m_gold")
        if ("max_prosperity" !in current && playerData.nationUpgradeLevel >= 10) newlyUnlocked.add("max_prosperity")

        // --- GUILD TRIALS ---
        if ("join_guild" !in current && playerData.joinedGuild != null) newlyUnlocked.add("join_guild")
        if ("guild_rank_5" !in current && playerData.guildLevel >= 5) newlyUnlocked.add("guild_rank_5")
        if ("guild_rank_10" !in current && playerData.guildLevel >= 10) newlyUnlocked.add("guild_rank_10")

        // --- ARENA GLORY ---
        if ("arena_bronze" !in current) {
            val bronzeDefeated = playerData.completedArenaOpponents.count { name ->
                com.solerforge.lumeria.database.ArenaEnemyDatabase.opponents.find { it.enemy.name == name }?.rank == "Bronze"
            }
            if (bronzeDefeated >= 5) newlyUnlocked.add("arena_bronze")
        }
        if ("arena_gold" !in current) {
            val goldDefeated = playerData.completedArenaOpponents.count { name ->
                com.solerforge.lumeria.database.ArenaEnemyDatabase.opponents.find { it.enemy.name == name }?.rank == "Gold"
            }
            if (goldDefeated >= 6) newlyUnlocked.add("arena_gold")
        }
        if ("arena_master" !in current && playerData.completedArenaOpponents.contains("Eternal Champion")) newlyUnlocked.add("arena_master")

        // --- BATTLE TOWER ---
        if ("tower_25" !in current && playerData.towerHighestFloor >= 25) newlyUnlocked.add("tower_25")
        if ("tower_50" !in current && playerData.towerHighestFloor >= 50) newlyUnlocked.add("tower_50")
        if ("tower_100" !in current && playerData.towerHighestFloor >= 100) newlyUnlocked.add("tower_100")

        // --- FISHING & COLLECTION ---
        if ("fishing_10" !in current && playerData.fishingLevel >= 10) newlyUnlocked.add("fishing_10")
        if ("catch_god_fish" !in current && playerData.consumedGodFishCount > 0) newlyUnlocked.add("catch_god_fish")
        
        if ("god_tier_collector" !in current) {
            val godTierItems = listOf(
                "Mantle of Perseverance", "Coin of the Tyrant", "Starshard Edge",
                "Aegis of Mountains", "Bogwalker Greaves", "Fang of Eternity",
                "Void Singularity", "Bulwark of Heroes", "Endbringer", "Eye of Creation"
            )
            if (godTierItems.all { it in playerData.inventory }) newlyUnlocked.add("god_tier_collector")
        }

        // --- KNOWLEDGE ---
        if ("bestiary_10" !in current && playerData.killCounts.size >= 10) newlyUnlocked.add("bestiary_10")
        if ("bestiary_50" !in current && playerData.killCounts.size >= 50) newlyUnlocked.add("bestiary_50")

        // --- NEW CUMULATIVE & CHALLENGE ACHIEVEMENTS ---
        if ("death_10" !in current && playerData.deathCount >= 10) newlyUnlocked.add("death_10")
        if ("death_100" !in current && playerData.deathCount >= 100) newlyUnlocked.add("death_100")
        if ("gold_10k" !in current && playerData.gold >= 10000) newlyUnlocked.add("gold_10k")
        if ("gold_100k" !in current && playerData.gold >= 100000) newlyUnlocked.add("gold_100k")
        
        val totalKills = playerData.killCounts.values.sum()
        if ("kills_100" !in current && totalKills >= 100) newlyUnlocked.add("kills_100")
        if ("kills_1000" !in current && totalKills >= 1000) newlyUnlocked.add("kills_1000")
        
        if ("gamble_10" !in current && playerData.gamblingWins >= 10) newlyUnlocked.add("gamble_10")
        if ("gamble_50" !in current && playerData.gamblingWins >= 50) newlyUnlocked.add("gamble_50")
        
        if ("fish_50" !in current && playerData.totalFishCaught >= 50) newlyUnlocked.add("fish_50")
        if ("fish_200" !in current && playerData.totalFishCaught >= 200) newlyUnlocked.add("fish_200")
        
        if ("upgrade_5" !in current && playerData.blacksmithUpgrades >= 5) newlyUnlocked.add("upgrade_5")
        if ("upgrade_25" !in current && playerData.blacksmithUpgrades >= 25) newlyUnlocked.add("upgrade_25")
        
        if ("event_10" !in current && playerData.worldEventsEncountered >= 10) newlyUnlocked.add("event_10")
        if ("event_50" !in current && playerData.worldEventsEncountered >= 50) newlyUnlocked.add("event_50")
        
        if ("max_stats" !in current && playerData.level >= 100 && playerData.statPoints == 0) newlyUnlocked.add("max_stats")
        if ("side_arcs_5" !in current && playerData.completedSideStoryArcs.size >= 5) newlyUnlocked.add("side_arcs_5")
        if ("titles_10" !in current && playerData.unlockedTitles.size >= 10) newlyUnlocked.add("titles_10")
        if ("bank_100k" !in current && playerData.bankGold >= 100000) newlyUnlocked.add("bank_100k")
        if ("respec_once" !in current && playerData.respecCount > 0) newlyUnlocked.add("respec_once")
        
        if ("legendary_gear" !in current) {
            val hasPlusFive = listOf(
                playerData.equippedWeapon, playerData.equippedArmor, playerData.equippedHead,
                playerData.equippedBoots, playerData.equippedShield
            ).any { it.contains("+5") }
            if (hasPlusFive) newlyUnlocked.add("legendary_gear")
        }

        return newlyUnlocked
    }
}
