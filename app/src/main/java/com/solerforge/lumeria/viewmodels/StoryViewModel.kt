package com.solerforge.lumeria.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.data.PlayerDataRepository
import com.solerforge.lumeria.database.GameDatabase
import com.solerforge.lumeria.models.StoryArc
import com.solerforge.lumeria.models.StoryChoiceOption
import com.solerforge.lumeria.models.XiousStats
import kotlinx.coroutines.launch

class StoryViewModel(
    private val repository: PlayerDataRepository,
    private val activeSlot: Int
) : ViewModel() {

    var selectedStoryArc by mutableStateOf<StoryArc?>(null)
        private set

    var currentEventIndex by mutableIntStateOf(0)
        private set

    var storyEnemyName by mutableStateOf<String?>(null)
        private set

    var previousLevelForCompletion by mutableIntStateOf(1)
        private set

    fun selectArc(arc: StoryArc) {
        selectedStoryArc = arc
        currentEventIndex = 0
    }

    fun nextEvent(playerData: PlayerData, onNavigateToCompletion: () -> Unit, onBattleStart: (String, Boolean) -> Unit) {
        selectedStoryArc?.let { arc ->
            currentEventIndex++
            if (currentEventIndex >= arc.events.size) {
                // Progression logic is still handled in MainViewModel for now due to complexity of rewards
                onNavigateToCompletion()
            }
        }
    }

    fun onChoiceSelected(
        option: StoryChoiceOption,
        playerData: PlayerData,
        onUpdatePlayer: (PlayerData) -> Unit,
        onNavigateToCompletion: () -> Unit
    ) {
        var current = playerData
        
        // Apply Rewards
        if (option.rewardGold > 0 || option.rewardXp > 0) {
            current = current.gainExperience(option.rewardXp).copy(
                gold = current.gold + option.rewardGold
            )
            onUpdatePlayer(current)
        }

        selectedStoryArc?.let { arc ->
            if (option.nextEventIndex != null) {
                currentEventIndex = option.nextEventIndex
            } else {
                currentEventIndex++
            }

            if (currentEventIndex >= arc.events.size) {
                onNavigateToCompletion()
            }
        }
    }

    fun setStoryBattle(enemyName: String, isBoss: Boolean) {
        storyEnemyName = enemyName
    }

    fun resetStory() {
        selectedStoryArc = null
        currentEventIndex = 0
        storyEnemyName = null
    }
    
    fun incrementEventIndex() {
        currentEventIndex++
    }

    fun setEventIndex(index: Int) {
        currentEventIndex = index
    }
}
