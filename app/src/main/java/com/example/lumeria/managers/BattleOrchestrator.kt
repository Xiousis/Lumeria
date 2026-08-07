package com.example.lumeria.managers

import androidx.compose.runtime.*
import com.example.lumeria.data.PlayerData
import com.example.lumeria.database.*
import com.example.lumeria.models.Screen
import com.example.lumeria.models.StoryArc

class BattleOrchestrator(
    private val onUpdatePlayer: (PlayerData) -> Unit,
    private val onNavigate: (Screen) -> Unit,
    private val onPopBackstack: () -> Unit,
    private val onNavigateToBattleScreen: () -> Unit
) {
    var isRiftBattle by mutableStateOf(false)
    var isBossBattle by mutableStateOf(false)
    var storyEnemyName by mutableStateOf<String?>(null)
    var arenaOpponent by mutableStateOf<ArenaOpponent?>(null)
    var isTowerBattle by mutableStateOf(false)
    var locationOverride by mutableStateOf<String?>(null)
    var currentGuildExam by mutableStateOf<GuildDatabase.GuildExam?>(null)
    var battleSeed by mutableIntStateOf(0)
    var grindingPlayerData by mutableStateOf<PlayerData?>(null)

    fun startBattle(isRift: Boolean, enemyName: String? = null, isBoss: Boolean = false, snapshot: PlayerData, location: String? = null) {
        isRiftBattle = isRift
        if (isRift) {
            isBossBattle = (snapshot.riftStep >= 3)
        } else {
            isBossBattle = isBoss
        }
        storyEnemyName = enemyName
        arenaOpponent = null
        isTowerBattle = false
        grindingPlayerData = snapshot
        locationOverride = location
        battleSeed++
        onNavigateToBattleScreen()
        checkSpawnVoidIncursion(snapshot)
    }

    fun startBossBattle(locationName: String, player: PlayerData) {
        val newData = player.copy(currentLocation = locationName)
        grindingPlayerData = newData 
        onUpdatePlayer(newData)
        isBossBattle = true
        isTowerBattle = false
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
        storyEnemyName = null
        grindingPlayerData = player
        battleSeed++
        onNavigateToBattleScreen()
    }

    fun startTowerBattle(player: PlayerData) {
        isTowerBattle = true
        isBossBattle = (player.towerFloor % 10 == 0)
        arenaOpponent = null
        storyEnemyName = null
        grindingPlayerData = player
        battleSeed++
        onNavigateToBattleScreen()
    }

    fun startGuildExam(exam: GuildDatabase.GuildExam, player: PlayerData) {
        currentGuildExam = exam
        isBossBattle = true
        storyEnemyName = exam.enemyName
        arenaOpponent = null
        isTowerBattle = false
        grindingPlayerData = player
        battleSeed++
        onNavigateToBattleScreen()
    }

    fun handleBattleAgain(newData: PlayerData) {
        grindingPlayerData = newData
        onUpdatePlayer(newData)
        if (isRiftBattle) {
            isBossBattle = (newData.riftStep >= 3)
        }
        battleSeed++
        checkSpawnVoidIncursion(newData)
    }

    private fun checkSpawnVoidIncursion(current: PlayerData) {
        if ((current.voidIncursionLocation == null) && (Math.random() < 0.15)) {
            val potentialLocations = WorldDatabase.locations.filter { 
                it.name != current.currentLocation && 
                (current.level >= it.requiredLevel || current.unlockedLocations.contains(it.name)) 
            }
            if (potentialLocations.isNotEmpty()) {
                val newLoc = potentialLocations.random().name
                onUpdatePlayer(current.copy(voidIncursionLocation = newLoc))
            }
        }
    }

    fun handleDeath(player: PlayerData, goldLost: Int): PlayerData {
        val isInBossFight = isRiftBattle && (player.riftStep >= 3)
        
        val result = if (isInBossFight) {
            player.copy(
                deathCount = player.deathCount + 1,
                voidIncursionLocation = null,
                riftStep = 0,
                riftLoot = emptyList(),
                riftGold = 0,
                riftXp = 0
            )
        } else if (isRiftBattle) {
            val stats = com.example.lumeria.models.XiousStats(player.level, player.xp).gainXp(player.riftXp)
            val leveledPlayer = player.copy(
                level = stats.level,
                xp = stats.xp,
                statPoints = player.statPoints + (maxOf(0, stats.level - player.level) * 5),
                inventory = (player.inventory + player.riftLoot),
                gold = player.gold + player.riftGold,
                deathCount = player.deathCount + 1,
                voidIncursionLocation = null,
                riftStep = 0,
                riftLoot = emptyList(),
                riftGold = 0,
                riftXp = 0
            )
            val newMaxHp = leveledPlayer.calculateMaxHp()
            val newMaxMana = leveledPlayer.calculateMaxMana()
            leveledPlayer.copy(
                maxHp = newMaxHp,
                maxMana = newMaxMana,
                hp = if (stats.level > player.level) newMaxHp else player.hp,
                mana = if (stats.level > player.level) newMaxMana else player.mana
            )
        } else {
            player.copy(
                deathCount = player.deathCount + 1,
                voidIncursionLocation = null,
            )
        }
        
        grindingPlayerData = null
        isRiftBattle = false
        isBossBattle = false
        isTowerBattle = false
        arenaOpponent = null
        currentGuildExam = null
        
        onNavigate(Screen.Defeated)
        return result
    }

    fun handleLeave(newData: PlayerData): PlayerData {
        grindingPlayerData = null
        val wasRift = isRiftBattle
        
        var finalPlayerData = newData
        if (wasRift) {
            val stats = com.example.lumeria.models.XiousStats(newData.level, newData.xp).gainXp(newData.riftXp)
            val leveledPlayer = newData.copy(
                level = stats.level,
                xp = stats.xp,
                statPoints = newData.statPoints + (maxOf(0, stats.level - newData.level) * 5),
                inventory = (newData.inventory + newData.riftLoot),
                gold = newData.gold + newData.riftGold,
                riftLoot = emptyList(),
                riftGold = 0,
                riftXp = 0,
                riftStep = 0,
                voidIncursionLocation = null
            )
            val newMaxHp = leveledPlayer.calculateMaxHp()
            val newMaxMana = leveledPlayer.calculateMaxMana()
            finalPlayerData = leveledPlayer.copy(
                maxHp = newMaxHp,
                maxMana = newMaxMana,
                hp = if (stats.level > newData.level) newMaxHp else leveledPlayer.hp,
                mana = if (stats.level > newData.level) newMaxMana else leveledPlayer.mana
            )
        }

        isRiftBattle = false
        isBossBattle = false
        isTowerBattle = false
        arenaOpponent = null
        currentGuildExam = null
        storyEnemyName = null
        locationOverride = null
        
        return finalPlayerData
    }
}
