package com.solerforge.lumeria.viewmodels

import androidx.lifecycle.ViewModel
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.KingdomRankDatabase
import com.solerforge.lumeria.models.XiousStats

class KingdomViewModel : ViewModel() {

    fun upgradeNation(playerData: PlayerData, onUpdatePlayer: (PlayerData) -> Unit) {
        val cost = KingdomRankDatabase.getNationUpgradeCost(playerData.nationUpgradeLevel)
        if (playerData.gold >= cost && playerData.nationUpgradeLevel < 10) {
            val updated = playerData.copy(
                gold = playerData.gold - cost,
                nationUpgradeLevel = playerData.nationUpgradeLevel + 1
            )
            onUpdatePlayer(updated)
        }
    }

    fun claimRankReward(rankId: Int, playerData: PlayerData, onUpdatePlayer: (PlayerData) -> Unit) {
        val rank = KingdomRankDatabase.ranks.find { it.id == rankId }
        if (rank != null && playerData.renown >= rank.requiredRenown && !playerData.claimedRankIds.contains(rankId)) {
            val stats = XiousStats(playerData.level, playerData.xp, playerData.getLevelCap()).gainXp(rank.rewardXp)
            val levelsGained = stats.level - playerData.level
            
            val updatedTitles = if (rank.rewardTitle != null) {
                (playerData.unlockedTitles + rank.rewardTitle).distinct()
            } else playerData.unlockedTitles

            val updatedData = playerData.copy(
                gold = playerData.gold + rank.rewardGold,
                level = stats.level,
                xp = stats.xp,
                unlockedTitles = updatedTitles,
                currentTitle = rank.rewardTitle ?: playerData.currentTitle,
                claimedRankIds = (playerData.claimedRankIds + rankId).distinct()
            )

            // Recalculate max stats and heal if level up
            val finalData = updatedData.copy(
                maxHp = updatedData.calculateMaxHp(),
                maxMana = updatedData.calculateMaxMana(),
                hp = if (levelsGained > 0) updatedData.calculateMaxHp() else updatedData.hp,
                mana = if (levelsGained > 0) updatedData.calculateMaxMana() else updatedData.mana
            )
            
            onUpdatePlayer(finalData)
        }
    }

    fun joinGuild(guildName: String, playerData: PlayerData, onUpdatePlayer: (PlayerData) -> Unit, onNavigateToIntro: () -> Unit) {
        if (playerData.joinedGuild == null) {
            onUpdatePlayer(playerData.copy(joinedGuild = guildName, guildLevel = 1, guildXp = 0))
            onNavigateToIntro()
        }
    }

    fun learnGuildSkill(skillName: String, fee: Int, playerData: PlayerData, onUpdatePlayer: (PlayerData) -> Unit) {
        if (playerData.gold >= fee && !playerData.unlockedSkills.contains(skillName)) {
            onUpdatePlayer(playerData.copy(
                gold = playerData.gold - fee,
                unlockedSkills = (playerData.unlockedSkills + skillName).distinct()
            ))
        }
    }

    fun selectKingdomLaw(lawId: Int?, playerData: PlayerData, onUpdatePlayer: (PlayerData) -> Unit) {
        onUpdatePlayer(playerData.copy(activeKingdomLawId = lawId))
    }
}
