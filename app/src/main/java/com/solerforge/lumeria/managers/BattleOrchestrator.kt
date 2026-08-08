package com.solerforge.lumeria.managers

import androidx.compose.runtime.*
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.*
import com.solerforge.lumeria.models.Screen
import com.solerforge.lumeria.models.StoryArc

class BattleOrchestrator(
    private val onUpdatePlayer: (PlayerData) -> Unit,
    private val onNavigate: (Screen) -> Unit,
    private val onPopBackstack: () -> Unit,
    private val onNavigateToBattleScreen: () -> Unit,
) {
    var isRiftBattle by mutableStateOf(false)
    var isBossBattle by mutableStateOf(false)
    var isPvPBattle by mutableStateOf(false)
    var storyEnemyName by mutableStateOf<String?>(null)
    var arenaOpponent by mutableStateOf<ArenaOpponent?>(null)
    var shadowOpponent by mutableStateOf<PlayerData?>(null)
    var isTowerBattle by mutableStateOf(false)
    var isStoryReplay by mutableStateOf(false)
    var locationOverride by mutableStateOf<String?>(null)
    var currentGuildExam by mutableStateOf<GuildDatabase.GuildExam?>(null)
    var battleSeed by mutableIntStateOf(0)
    var grindingPlayerData by mutableStateOf<PlayerData?>(null)

    fun startBattle(isRift: Boolean, enemyName: String? = null, isBoss: Boolean = false, snapshot: PlayerData, location: String? = null, isReplay: Boolean = false) {
        isRiftBattle = isRift
        isPvPBattle = false
        shadowOpponent = null
        isStoryReplay = isReplay
        if (isRift) {
            isBossBattle = (snapshot.riftStep >= 3)
        } else {
            isBossBattle = isBoss
        }
        storyEnemyName = enemyName
        arenaOpponent = null
        isTowerBattle = false
        grindingPlayerData = snapshot.copy(battleBuffsConsumed = false)
        locationOverride = location
        battleSeed++
        onNavigateToBattleScreen()
        // Rift spawning moved to post-battle handlers
    }

    fun startBossBattle(locationName: String, player: PlayerData) {
        val newData = player.copy(currentLocation = locationName, battleBuffsConsumed = false)
        grindingPlayerData = newData 
        locationOverride = locationName
        onUpdatePlayer(newData)
        isBossBattle = true
        isTowerBattle = false
        isPvPBattle = false
        isStoryReplay = false
        shadowOpponent = null
        isRiftBattle = (newData.voidIncursionLocation == locationName)
        arenaOpponent = null
        storyEnemyName = null
        battleSeed++
        onNavigateToBattleScreen()
    }

    fun startArenaBattle(opponent: ArenaOpponent, player: PlayerData) {
        arenaOpponent = opponent
        isBossBattle = false
        isTowerBattle = false
        isPvPBattle = false
        isStoryReplay = false
        shadowOpponent = null
        storyEnemyName = null
        grindingPlayerData = player.copy(battleBuffsConsumed = false)
        battleSeed++
        onNavigateToBattleScreen()
    }

    fun startTowerBattle(player: PlayerData) {
        isTowerBattle = true
        isBossBattle = (player.towerFloor % 10 == 0)
        arenaOpponent = null
        isPvPBattle = false
        isStoryReplay = false
        shadowOpponent = null
        storyEnemyName = null
        grindingPlayerData = player.copy(battleBuffsConsumed = false)
        battleSeed++
        onNavigateToBattleScreen()
    }

    fun startGuildExam(exam: GuildDatabase.GuildExam, player: PlayerData) {
        currentGuildExam = exam
        isBossBattle = true
        isPvPBattle = false
        isStoryReplay = false
        shadowOpponent = null
        storyEnemyName = exam.enemyName
        arenaOpponent = null
        isTowerBattle = false
        grindingPlayerData = player.copy(battleBuffsConsumed = false)
        battleSeed++
        onNavigateToBattleScreen()
    }

    fun handleBattleAgain(newData: PlayerData): PlayerData {
        grindingPlayerData = newData.copy(battleBuffsConsumed = false)
        if (isRiftBattle) {
            isBossBattle = (newData.riftStep >= 3)
        }
        battleSeed++
        
        val buffedData = newData.consumeBattleBuffs()
        return checkSpawnVoidIncursion(buffedData)
    }

    private fun checkSpawnVoidIncursion(current: PlayerData): PlayerData {
        if ((current.voidIncursionLocation == null) && (Math.random() < 0.15)) {
            val potentialLocations = (if (current.isReborn) WorldDatabase.legacyLocations else WorldDatabase.locations).filter { 
                it.name != current.currentLocation && 
                (current.level >= it.requiredLevel || current.unlockedLocations.contains(it.name)) 
            }
            if (potentialLocations.isNotEmpty()) {
                val newLoc = potentialLocations.random().name
                return current.copy(voidIncursionLocation = newLoc)
            }
        }
        return current
    }

    fun startShadowBattle(shadow: PlayerData, player: PlayerData) {
        shadowOpponent = shadow
        isPvPBattle = true
        isBossBattle = false
        isTowerBattle = false
        isStoryReplay = false
        arenaOpponent = null
        storyEnemyName = null
        grindingPlayerData = player.copy(battleBuffsConsumed = false)
        battleSeed++
        onNavigateToBattleScreen()
    }

    fun handleDeath(player: PlayerData, goldLost: Long): PlayerData {
        val isInBossFight = isRiftBattle && (player.riftStep >= 3)
        val isPvP = isPvPBattle

        val result = if (isInBossFight) {
            player.copy(
                deathCount = player.deathCount + 1,
                voidIncursionLocation = null,
                riftStep = 0,
                riftLoot = emptyList(),
                riftGold = 0,
                riftXp = 0,
                gold = maxOf(0L, player.gold - goldLost)
            )
        } else if (isRiftBattle) {
            val leveledPlayer = player.gainExperience(player.riftXp).copy(
                inventory = (player.inventory + player.riftLoot),
                gold = maxOf(0L, player.gold + player.riftGold - goldLost),
                deathCount = player.deathCount + 1,
                voidIncursionLocation = null,
                riftStep = 0,
                riftLoot = emptyList(),
                riftGold = 0,
                riftXp = 0
            )
            leveledPlayer
        } else {
            player.copy(
                deathCount = player.deathCount + 1,
                voidIncursionLocation = null,
                gold = maxOf(0L, player.gold - goldLost)
            )
        }.let { data ->
            if (isPvP) data.copy(pvpLosses = data.pvpLosses + 1) else data
        }
        
        grindingPlayerData = null
        isRiftBattle = false
        isBossBattle = false
        isTowerBattle = false
        isStoryReplay = false
        arenaOpponent = null
        currentGuildExam = null
        
        onNavigate(Screen.Defeated)
        val buffedResult = result.consumeBattleBuffs()
        return checkSpawnVoidIncursion(buffedResult)
    }

    fun handleLeave(newData: PlayerData): PlayerData {
        grindingPlayerData = null
        val wasRift = isRiftBattle
        val isPvP = isPvPBattle

        var finalPlayerData = newData
        if (wasRift) {
            val leveledPlayer = newData.gainExperience(newData.riftXp).copy(
                inventory = (newData.inventory + newData.riftLoot),
                gold = newData.gold + newData.riftGold,
                riftLoot = emptyList(),
                riftGold = 0,
                riftXp = 0,
                riftStep = 0,
                voidIncursionLocation = null
            )
            finalPlayerData = leveledPlayer
        }

        if (isPvP) {
            finalPlayerData = finalPlayerData.copy(pvpWins = finalPlayerData.pvpWins + 1)
        }

        isRiftBattle = false
        isBossBattle = false
        isTowerBattle = false
        isPvPBattle = false
        isStoryReplay = false
        arenaOpponent = null
        shadowOpponent = null
        currentGuildExam = null
        storyEnemyName = null
        locationOverride = null
        
        val buffedFinalData = finalPlayerData.consumeBattleBuffs()
        return checkSpawnVoidIncursion(buffedFinalData)
    }
}
