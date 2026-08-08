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
        var newData = playerData.copy(currentLocation = locationName)
        
        // 5% Chance for World Event
        if (Math.random() < 0.05) {
            currentWorldEvent = WorldEventDatabase.getRandomEvent()
            eventOutcomeMessage = null
            newData = newData.copy(worldEventsEncountered = newData.worldEventsEncountered + 1)
            onUpdatePlayer(newData)
            onNavigateToEvent()
        } else {
            onUpdatePlayer(newData)
            val isRift = (newData.voidIncursionLocation == locationName)
            onNavigateToBattle(isRift, newData)
        }
    }

    fun processEventOutcome(
        option: com.solerforge.lumeria.models.EventOption, 
        playerData: PlayerData, 
        onUpdatePlayer: (PlayerData) -> Unit,
        onNavigateToBattle: (String) -> Unit
    ) {
        val outcome = option.outcome
        
        // 1. Validate requirements
        if (playerData.gold < option.goldCost) return
        if (option.requiredItem != null && !playerData.inventory.contains(option.requiredItem)) return

        // 2. Consume costs
        var updated = playerData.copy(gold = playerData.gold - option.goldCost)
        if (option.requiredItem != null) {
            val newList = updated.inventory.toMutableList()
            newList.remove(option.requiredItem)
            updated = updated.copy(inventory = newList)
        }

        var shouldBattle = false
        var enemyName = ""

        when (outcome) {
            is EventOutcome.Reward -> {
                updated = updated.gainExperience(outcome.xp).copy(
                    gold = updated.gold + outcome.gold
                )
            }
            is EventOutcome.Penalty -> {
                updated = updated.copy(
                    gold = maxOf(0L, updated.gold - outcome.goldLost),
                    hp = maxOf(1, updated.hp - outcome.hpLost)
                )
            }
            is EventOutcome.Buff -> {
                updated = updated.copy(
                    activeBuffs = (updated.activeBuffs + outcome.buff)
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
