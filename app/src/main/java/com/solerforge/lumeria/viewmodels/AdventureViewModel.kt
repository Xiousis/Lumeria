package com.solerforge.lumeria.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.WorldDatabase
import com.solerforge.lumeria.database.WorldEventDatabase
import com.solerforge.lumeria.models.EventOutcome
import com.solerforge.lumeria.models.WorldEvent
import com.solerforge.lumeria.models.XiousStats

class AdventureViewModel : ViewModel() {

    var currentWorldEvent by mutableStateOf<WorldEvent?>(null)
        private set
    
    var eventOutcomeMessage by mutableStateOf<String?>(null)
        private set

    fun travel(
        locationName: String, 
        playerData: PlayerData, 
        onUpdatePlayer: (PlayerData) -> Unit,
        onNavigateToBattle: (Boolean, PlayerData) -> Unit,
        onNavigateToEvent: () -> Unit
    ) {
        val newData = playerData.copy(currentLocation = locationName)
        onUpdatePlayer(newData)
        
        // 5% Chance for World Event
        if (Math.random() < 0.05) {
            currentWorldEvent = WorldEventDatabase.getRandomEvent()
            eventOutcomeMessage = null
            onUpdatePlayer(newData.copy(worldEventsEncountered = newData.worldEventsEncountered + 1))
            onNavigateToEvent()
        } else {
            val isRift = (newData.voidIncursionLocation == locationName)
            onNavigateToBattle(isRift, newData)
        }
    }

    fun processEventOutcome(
        outcome: EventOutcome, 
        playerData: PlayerData, 
        onUpdatePlayer: (PlayerData) -> Unit,
        onNavigateToBattle: (String) -> Unit
    ) {
        var updated = playerData
        var shouldBattle = false
        var enemyName = ""

        when (outcome) {
            is EventOutcome.Reward -> {
                updated = playerData.copy(
                    gold = playerData.gold + outcome.gold,
                    xp = playerData.xp + outcome.xp
                )
                if (outcome.xp > 0) {
                     val stats = XiousStats(playerData.level, playerData.xp, playerData.getLevelCap()).gainXp(outcome.xp)
                     updated = updated.copy(level = stats.level, xp = stats.xp)
                }
            }
            is EventOutcome.Penalty -> {
                updated = playerData.copy(
                    gold = maxOf(0, playerData.gold - outcome.goldLost),
                    hp = maxOf(1, playerData.hp - outcome.hpLost)
                )
            }
            is EventOutcome.Buff -> {
                updated = playerData.copy(
                    activeBuffs = (playerData.activeBuffs + outcome.buff)
                )
                if (outcome.buff.type == com.solerforge.lumeria.models.BuffType.HealthRegen) {
                    updated = updated.copy(hp = updated.maxHp, mana = updated.maxMana)
                }
            }
            is EventOutcome.Ambush -> {
                shouldBattle = true
                enemyName = outcome.enemyName
            }
            is EventOutcome.Nothing -> {}
        }

        onUpdatePlayer(updated)
        eventOutcomeMessage = when(outcome) {
            is EventOutcome.Reward -> outcome.message
            is EventOutcome.Penalty -> outcome.message
            is EventOutcome.Buff -> outcome.message
            is EventOutcome.Ambush -> outcome.message
            is EventOutcome.Nothing -> outcome.message
        }
        
        if (shouldBattle) {
            onNavigateToBattle(enemyName)
        }
    }

    fun completeEvent() {
        currentWorldEvent = null
        eventOutcomeMessage = null
    }
}
