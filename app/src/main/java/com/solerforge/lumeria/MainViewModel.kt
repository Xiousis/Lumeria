package com.solerforge.lumeria

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.data.PlayerDataRepository
import com.solerforge.lumeria.data.SaveStatus
import com.solerforge.lumeria.database.*
import com.solerforge.lumeria.managers.BattleOrchestrator
import com.solerforge.lumeria.managers.EconomyManager
import com.solerforge.lumeria.managers.LeaderboardManager
import com.solerforge.lumeria.models.LeaderboardEntry
import com.solerforge.lumeria.models.Screen
import com.solerforge.lumeria.models.StoryArc
import com.solerforge.lumeria.managers.CloudSaveManager
import com.solerforge.lumeria.managers.ShadowManager
import com.solerforge.lumeria.utils.AchievementManager
import com.google.android.gms.games.PlayGames
import com.google.firebase.Firebase
import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.analytics
import com.google.firebase.analytics.logEvent
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class MainViewModel(private val repository: PlayerDataRepository) : ViewModel() {

    private val _backstack = mutableStateListOf(Screen.Title)
    val backstack: List<Screen> get() = _backstack

    private val analytics = Firebase.analytics

    val currentScreen: Screen get() = _backstack.last()

    var activeSlot by mutableIntStateOf(1)
        private set

    val battleOrchestrator = BattleOrchestrator(
        onUpdatePlayer = { updatePlayer(it) },
        onNavigate = { navigateTo(it) },
        onPopBackstack = { popBackstack() },
        onNavigateToBattleScreen = { navigateTo(Screen.Battle) }
    )

    val economyManager = EconomyManager(
        onUpdatePlayer = { updatePlayer(it) },
        onNavigate = { navigateTo(it) },
        onReplace = { replaceTo(it) }
    )

    val battleSeed: Int get() = battleOrchestrator.battleSeed
    val isBossBattle: Boolean get() = battleOrchestrator.isBossBattle
    val storyEnemyName: String? get() = battleOrchestrator.storyEnemyName
    val arenaOpponent: ArenaOpponent? get() = battleOrchestrator.arenaOpponent
    val isTowerBattle: Boolean get() = battleOrchestrator.isTowerBattle
    val isRiftBattle: Boolean get() = battleOrchestrator.isRiftBattle
    val locationOverride: String? get() = battleOrchestrator.locationOverride
    val currentGuildExam: GuildDatabase.GuildExam? get() = battleOrchestrator.currentGuildExam
    val grindingPlayerData: PlayerData? get() = battleOrchestrator.grindingPlayerData

    var selectedStoryArc by mutableStateOf<StoryArc?>(null)
        private set

    var currentEventIndex by mutableIntStateOf(0)
        private set

    var previousLevelForCompletion by mutableIntStateOf(1)
        private set

    var isResetting by mutableStateOf(value = false)
        private set

    var gameMenuTabIndex by mutableIntStateOf(0)
        private set

    private val _deviceId = MutableStateFlow<String?>(null)
    val deviceId = _deviceId.asStateFlow()

    fun setDeviceId(id: String) {
        _deviceId.value = id
    }

    private val _nameQuery = MutableStateFlow("")
    
    @OptIn(FlowPreview::class, ExperimentalCoroutinesApi::class)
    val nameAvailability: StateFlow<Boolean?> = _nameQuery
        .debounce(500.milliseconds)
        .flatMapLatest { query ->
            if (query.length < 3) flowOf(null)
            else flow<Boolean?> {
                try {
                    emit(com.solerforge.lumeria.managers.NameManager.isNameAvailable(query))
                } catch (e: Exception) {
                    emit(null) // Error or offline: cannot verify
                }
            }
        }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    fun checkNameAvailability(name: String) {
        _nameQuery.value = name
    }

    var towerUnlockMessage by mutableStateOf<String?>(null)
        private set

    var showCorruptionRecovery by mutableStateOf(false)
        private set

    private val _shadows = MutableStateFlow<List<PlayerData>>(emptyList())
    val shadows = _shadows.asStateFlow()

    private val _leaderboardGlobal = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboardGlobal = _leaderboardGlobal.asStateFlow()

    private val _leaderboardPvP = MutableStateFlow<List<LeaderboardEntry>>(emptyList())
    val leaderboardPvP = _leaderboardPvP.asStateFlow()

    private val _isLeaderboardLoading = MutableStateFlow(false)
    val isLeaderboardLoading = _isLeaderboardLoading.asStateFlow()

    @OptIn(ExperimentalCoroutinesApi::class)
    private val saveResult = snapshotFlow { activeSlot }
        .flatMapLatest { slot ->
            repository.getPlayerDataFlow(slot)
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerDataRepository.SaveResult(PlayerData(), SaveStatus.Empty),
        )

    val playerData = saveResult.map { it.data }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PlayerData(),
    )

    val saveStatus = saveResult.map { it.status }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SaveStatus.Empty,
    )

    val slot1Data = repository.getPlayerDataFlow(1).stateIn(viewModelScope, SharingStarted.Eagerly, PlayerDataRepository.SaveResult(PlayerData(), SaveStatus.Empty))
    val slot2Data = repository.getPlayerDataFlow(2).stateIn(viewModelScope, SharingStarted.Eagerly, PlayerDataRepository.SaveResult(PlayerData(), SaveStatus.Empty))
    val slot3Data = repository.getPlayerDataFlow(3).stateIn(viewModelScope, SharingStarted.Eagerly, PlayerDataRepository.SaveResult(PlayerData(), SaveStatus.Empty))

    val settings = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = com.solerforge.lumeria.data.GameSettings()
    )

    var goldLostOnDeath by mutableStateOf(0L)
        private set
    
    val levelsGainedOnCompletion: Int get() = maxOf(0, playerData.value.level - previousLevelForCompletion)

    init {
        viewModelScope.launch {
            repository.lastUsedSlotFlow.take(1).collect { slot ->
                activeSlot = slot
            }
        }
    }

    fun navigateTo(screen: Screen) {
        _backstack.add(screen)
    }

    fun popBackstack() {
        if (_backstack.size > 1) {
            _backstack.removeAt(_backstack.size - 1)
        }
    }

    fun replaceTo(screen: Screen) {
        if (_backstack.isNotEmpty()) {
            _backstack.removeAt(_backstack.size - 1)
        }
        _backstack.add(screen)
    }

    fun selectSlot(slot: Int) {
        activeSlot = slot
    }

    fun triggerCorruptionRecovery() {
        showCorruptionRecovery = true
    }

    fun dismissCorruptionRecovery() {
        showCorruptionRecovery = false
    }

    fun restoreBackup() {
        val targetSlot = activeSlot
        viewModelScope.launch {
            try {
                // repository already handles backup logic internally in updatePlayer,
                // but we can force a restore by clearing primary if corrupted.
                // For simplicity, we just trigger a fresh read which the flow will emit.
                showCorruptionRecovery = false
            } catch (e: Exception) {
                showCorruptionRecovery = false
            }
        }
    }

    fun startFresh() {
        onNewGame()
        showCorruptionRecovery = false
    }

    fun restoreFromCloud() {
        syncCloudSave { result ->
            showCorruptionRecovery = false
        }
    }

    fun fetchShadows() {
        viewModelScope.launch {
            _shadows.value = ShadowManager.fetchRandomShadows()
        }
    }

    fun updateGameMenuTab(index: Int) {
        gameMenuTabIndex = index
    }

    fun fetchLeaderboards() {
        viewModelScope.launch {
            _isLeaderboardLoading.value = true
            _leaderboardGlobal.value = LeaderboardManager.fetchGlobalRankings()
            _leaderboardPvP.value = LeaderboardManager.fetchPvPRankings()
            _isLeaderboardLoading.value = false
        }
    }

    fun updatePlayer(newData: PlayerData) {
        updatePlayer { newData }
    }

    fun updatePlayer(transform: (PlayerData) -> PlayerData) {
        if (isResetting) return

        val targetSlot = activeSlot
        viewModelScope.launch {
            val finalData = try {
                repository.updatePlayer(targetSlot) { current ->
                    val newData = transform(current)
                    val newlyUnlocked = AchievementManager.checkAchievements(newData)
                    
                    if (newlyUnlocked.isNotEmpty()) {
                        newData.copy(unlockedAchievements = (newData.unlockedAchievements + newlyUnlocked).distinct())
                    } else newData
                }
            } catch (e: Exception) {
                analytics.logEvent("save_corruption_prevented") {
                    param("slot", activeSlot.toLong())
                    param("error", e.message ?: "Unknown")
                }
                triggerCorruptionRecovery()
                return@launch
            }

            // Sync with leaderboard if applicable
            _deviceId.value?.let { syncLeaderboard(it) }

            // Analytics using the result of the commit
            val previousData = playerData.value
            if (finalData.level > previousData.level) {
                analytics.logEvent(FirebaseAnalytics.Event.LEVEL_UP) {
                    param(FirebaseAnalytics.Param.LEVEL, finalData.level.toLong())
                    param("character_class", finalData.playerClass)
                }
            }

            if (finalData.gold >= 1000000 && previousData.gold < 1000000) {
                analytics.logEvent("wealth_milestone") {
                    param(FirebaseAnalytics.Param.VALUE, 1000000L)
                }
            }
        }
    }

    fun updateSettings(newSettings: com.solerforge.lumeria.data.GameSettings) {
        viewModelScope.launch {
            repository.saveSettings(newSettings)
        }
    }

    fun onStartGame() {
        _backstack.clear()
        if (playerData.value.introSeen) {
            _backstack.add(Screen.GameMenu)
        } else {
            _backstack.add(Screen.Story)
        }
    }

    fun onNewGame() {
        val targetSlot = activeSlot
        viewModelScope.launch {
            isResetting = true
            repository.clearPlayerData(targetSlot)
            
            selectedStoryArc = null
            currentEventIndex = 0
            battleOrchestrator.storyEnemyName = null
            battleOrchestrator.battleSeed++
            previousLevelForCompletion = 1
            battleOrchestrator.grindingPlayerData = null
            
            delay(100.milliseconds) 
            repository.savePlayerData(PlayerData(), targetSlot)
            
            _backstack.clear()
            _backstack.add(Screen.Story)
            isResetting = false
        }
    }



    private fun applyClassBonuses(data: PlayerData, className: String): PlayerData {
        val statsAppliedData = data
        return when (className) {
            "Mage" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Magic Bolt", "Mana Shield", "Heal"),
                equippedSkills = listOf("Magic Bolt", "Mana Shield", "Heal"),
                inventory = listOf("Apprentice Staff", "Mage Robes", "Canvas Shoes", "Mana Potion"),
                equippedWeapon = "Apprentice Staff",
                equippedArmor = "Mage Robes",
                equippedBoots = "Canvas Shoes"
            )
            "Samurai" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Slash", "Quick Draw", "Parry"),
                equippedSkills = listOf("Slash", "Quick Draw", "Parry"),
                inventory = listOf("Training Katana", "Shinobi Garb", "Sandals"),
                equippedWeapon = "Training Katana",
                equippedArmor = "Shinobi Garb",
                equippedBoots = "Sandals"
            )
            "Paladin" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Guard", "Shield Bash", "Heal"),
                equippedSkills = listOf("Guard", "Shield Bash", "Heal"),
                inventory = listOf("Knight Blade", "Knight Armor", "Knight Shield", "Leather Boots"),
                equippedWeapon = "Knight Blade",
                equippedArmor = "Knight Armor",
                equippedShield = "Knight Shield",
                equippedBoots = "Leather Boots"
            )
            "Assassin" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Slash", "Quick Draw", "Smoke Bomb"),
                equippedSkills = listOf("Slash", "Quick Draw", "Smoke Bomb"),
                inventory = listOf("Squire Dagger", "Scout Armor", "Scout Boots"),
                equippedWeapon = "Squire Dagger",
                equippedArmor = "Scout Armor",
                equippedBoots = "Scout Boots"
            )
            "Monk" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Heavy Strike", "Guard", "Heal"),
                equippedSkills = listOf("Heavy Strike", "Guard", "Heal"),
                inventory = listOf("None", "Padded Tunic", "Canvas Shoes"),
                equippedWeapon = "None",
                equippedArmor = "Padded Tunic",
                equippedBoots = "Canvas Shoes"
            )
            "Archer" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Slash", "Quick Draw", "Heavy Strike"),
                equippedSkills = listOf("Slash", "Quick Draw", "Heavy Strike"),
                inventory = listOf("Nomad Bow", "Rugged Vest", "Leather Boots"),
                equippedWeapon = "Nomad Bow",
                equippedArmor = "Rugged Vest",
                equippedBoots = "Leather Boots"
            )
            "Necromancer" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Magic Bolt", "Bleeding Slash"),
                equippedSkills = listOf("Magic Bolt", "Bleeding Slash"),
                inventory = listOf("Traveler's Staff", "Mage Robes", "Canvas Shoes"),
                equippedWeapon = "Traveler's Staff",
                equippedArmor = "Mage Robes",
                equippedBoots = "Canvas Shoes"
            )
            "Bard" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Heal", "Lion's Roar"),
                equippedSkills = listOf("Heal", "Lion's Roar"),
                inventory = listOf("Wanderer's Gladius", "Explorer Jacket", "Cloud Sprints"),
                equippedWeapon = "Wanderer's Gladius",
                equippedArmor = "Explorer Jacket",
                equippedBoots = "Cloud Sprints"
            )
            "Berserker" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Heavy Strike", "Whirlwind Slash"),
                equippedSkills = listOf("Heavy Strike", "Whirlwind Slash"),
                inventory = listOf("Orc Cleaver", "Rugged Vest", "Leather Boots"),
                equippedWeapon = "Orc Cleaver",
                equippedArmor = "Rugged Vest",
                equippedBoots = "Leather Boots"
            )
            "Dragon Knight" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Heavy Strike", "Ignite", "Guard"),
                equippedSkills = listOf("Heavy Strike", "Ignite", "Guard"),
                inventory = listOf("Drake Blade", "Dragon Scales", "Plate Boots"),
                equippedWeapon = "Drake Blade",
                equippedArmor = "Dragon Scales",
                equippedBoots = "Plate Boots"
            )
            "Void Reaver" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Bleeding Slash", "Magic Bolt", "Smoke Bomb"),
                equippedSkills = listOf("Bleeding Slash", "Magic Bolt", "Smoke Bomb"),
                inventory = listOf("Void Edge", "Abyssal Garb", "Shadow Steps"),
                equippedWeapon = "Void Edge",
                equippedArmor = "Abyssal Garb",
                equippedBoots = "Shadow Steps"
            )
            "Seraph" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Magic Bolt", "Heal", "Guard"),
                equippedSkills = listOf("Magic Bolt", "Heal", "Guard"),
                inventory = listOf("Starlight Staff", "Divine Robes", "Cloud Sprints"),
                equippedWeapon = "Starlight Staff",
                equippedArmor = "Divine Robes",
                equippedBoots = "Cloud Sprints"
            )
            "Blood Mage" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Magic Bolt", "Bleeding Slash", "Heal"),
                equippedSkills = listOf("Magic Bolt", "Bleeding Slash", "Heal"),
                inventory = listOf("Sanguine Staff", "Crimson Robes", "Canvas Shoes"),
                equippedWeapon = "Sanguine Staff",
                equippedArmor = "Crimson Robes",
                equippedBoots = "Canvas Shoes"
            )
            "Shadow Stalker" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Quick Draw", "Smoke Bomb", "Slash"),
                equippedSkills = listOf("Quick Draw", "Smoke Bomb", "Slash"),
                inventory = listOf("Shadow Dagger", "Night Armor", "Silent Boots"),
                equippedWeapon = "Shadow Dagger",
                equippedArmor = "Night Armor",
                equippedBoots = "Silent Boots"
            )
            "World Breaker" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Heavy Strike", "Guard", "Slash"),
                equippedSkills = listOf("Heavy Strike", "Guard", "Slash"),
                inventory = listOf("Titan Maul", "Plate Armor", "Plate Boots"),
                equippedWeapon = "Titan Maul",
                equippedArmor = "Plate Armor",
                equippedBoots = "Plate Boots"
            )
            "Technomancer" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Magic Bolt", "Mana Shield", "Quick Draw"),
                equippedSkills = listOf("Magic Bolt", "Mana Shield", "Quick Draw"),
                inventory = listOf("Logic Staff", "Plated Robes", "Heavy Boots"),
                equippedWeapon = "Logic Staff",
                equippedArmor = "Plated Robes",
                equippedBoots = "Heavy Boots"
            )
            "Enchanter" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Magic Bolt", "Heal", "Mana Shield"),
                equippedSkills = listOf("Magic Bolt", "Heal", "Mana Shield"),
                inventory = listOf("Living Branch", "Nature Robes", "Sandals"),
                equippedWeapon = "Living Branch",
                equippedArmor = "Nature Robes",
                equippedBoots = "Sandals"
            )
            "Slime Sage" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Magic Bolt", "Heal", "Guard"),
                equippedSkills = listOf("Magic Bolt", "Heal", "Guard"),
                inventory = listOf("Fluid Staff", "Gelatinous Tunic", "Canvas Shoes"),
                equippedWeapon = "Fluid Staff",
                equippedArmor = "Gelatinous Tunic",
                equippedBoots = "Canvas Shoes"
            )
            "Void Walker" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Magic Bolt", "Smoke Bomb", "Quick Draw"),
                equippedSkills = listOf("Magic Bolt", "Smoke Bomb", "Quick Draw"),
                inventory = listOf("Void Rod", "Sovereign Robes", "Shadow Steps"),
                equippedWeapon = "Void Rod",
                equippedArmor = "Sovereign Robes",
                equippedBoots = "Shadow Steps"
            )
            "Dragon Lord" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Heavy Strike", "Ignite", "Guard"),
                equippedSkills = listOf("Heavy Strike", "Ignite", "Guard"),
                inventory = listOf("Calamity Blade", "Elder Scales", "Plate Boots"),
                equippedWeapon = "Calamity Blade",
                equippedArmor = "Elder Scales",
                equippedBoots = "Plate Boots"
            )
            "Arbiter" -> statsAppliedData.copy(
                unlockedSkills = listOf("None", "Magic Bolt", "Slash", "Heal", "Guard"),
                equippedSkills = listOf("Magic Bolt", "Slash", "Heal"),
                inventory = listOf("Balanced Blade", "Nephilim Garb", "Cloud Sprints"),
                equippedWeapon = "Balanced Blade",
                equippedArmor = "Nephilim Garb",
                equippedBoots = "Cloud Sprints"
            )
            else -> statsAppliedData.copy( // Warrior
                unlockedSkills = listOf("None", "Slash", "Heavy Strike", "Guard"),
                equippedSkills = listOf("Slash", "Heavy Strike", "Guard"),
                inventory = listOf("Iron Sword", "Leather Armor", "Leather Boots"),
                equippedWeapon = "Iron Sword",
                equippedArmor = "Leather Armor",
                equippedBoots = "Leather Boots"
            )
        }.let { data ->
            data.recalculateVitals()
        }
    }

    fun onIntroSeen() {
        if (!isResetting) {
            val targetSlot = activeSlot
            viewModelScope.launch {
                repository.savePlayerData(playerData.value.copy(introSeen = true), targetSlot)
            }
        }
        replaceWith(Screen.GameMenu)
    }

    fun updateStoryArc(arc: StoryArc?) {
        selectedStoryArc = arc
        if (arc != null) {
            val isReplay = playerData.value.completedStoryArcs.contains(arc.id) || 
                           playerData.value.completedSideStoryArcs.contains(arc.id)
            
            battleOrchestrator.isStoryReplay = isReplay
            
            val savedArcId = playerData.value.activeStoryArcId
            val savedIndex = playerData.value.currentStoryEventIndex

            if (isReplay) {
                currentEventIndex = 0
            } else if (savedArcId == arc.id) {
                currentEventIndex = savedIndex
                if (currentEventIndex >= arc.events.size) {
                    processArcCompletion(arc)
                    return
                }
            } else {
                currentEventIndex = 0
                updatePlayer { it.copy(activeStoryArcId = arc.id, currentStoryEventIndex = 0) }
            }
            navigateTo(Screen.StoryDialogue)
        }
    }

    fun advanceStoryEvent() {
        val arc = selectedStoryArc ?: return
        currentEventIndex++
        
        val isReplay = battleOrchestrator.isStoryReplay
        if (!isReplay) {
            updatePlayer { it.copy(currentStoryEventIndex = currentEventIndex) }
        }
        
        if (currentEventIndex >= arc.events.size) {
            processArcCompletion(arc)
        }
    }

    fun onStoryChoiceSelected(option: com.solerforge.lumeria.models.StoryChoiceOption) {
        val arc = selectedStoryArc ?: return
        val nextIndex = option.nextEventIndex ?: (currentEventIndex + 1)
        val isFinalEvent = nextIndex >= arc.events.size
        
        val isReplay = playerData.value.completedStoryArcs.contains(arc.id) || 
                       playerData.value.completedSideStoryArcs.contains(arc.id)

        if (isFinalEvent) {
            // Handle reward and completion atomically
            processArcCompletion(arc, if (isReplay) 0L else option.rewardXp, if (isReplay) 0L else option.rewardGold)
        } else {
            updatePlayer { current ->
                var updated = current
                if (!isReplay && (option.rewardGold > 0 || option.rewardXp > 0)) {
                    updated = current.gainExperience(option.rewardXp).copy(
                        gold = current.gold + option.rewardGold
                    )
                }
                
                if (isReplay) updated else updated.copy(currentStoryEventIndex = nextIndex)
            }
            currentEventIndex = nextIndex
        }
    }

    fun updateStoryEventIndex(index: Int) {
        currentEventIndex = index
    }

    fun updateStoryEnemyName(name: String?) {
        battleOrchestrator.storyEnemyName = name
    }

    fun updatePreviousLevel(level: Int) {
        previousLevelForCompletion = level
    }

    fun navigateToBattle(isRift: Boolean, enemyName: String? = null, isBoss: Boolean = false, snapshot: PlayerData? = null, location: String? = null) {
        val currentArc = selectedStoryArc
        val isReplay = if (currentArc != null) {
            playerData.value.completedStoryArcs.contains(currentArc.id) || 
            playerData.value.completedSideStoryArcs.contains(currentArc.id)
        } else false

        battleOrchestrator.startBattle(isRift, enemyName, isBoss, snapshot ?: playerData.value, location, isReplay)
    }

    fun onEnterShop() {
        economyManager.enterShop(playerData.value)
    }

    fun onCompleteBillyIntro() {
        economyManager.completeBillyIntro(playerData.value)
    }

    fun onEnterInn() {
        economyManager.enterInn(playerData.value)
    }

    fun onCompleteInnIntro() {
        economyManager.completeInnIntro(playerData.value)
    }

    fun onEnterForge() {
        economyManager.enterForge(playerData.value)
    }

    fun onCompleteForgeIntro() {
        economyManager.completeForgeIntro(playerData.value)
    }

    fun onBattleDeath(snapshot: PlayerData) {
        goldLostOnDeath = snapshot.gold * 15L / 100L
        val updated = battleOrchestrator.handleDeath(snapshot, goldLostOnDeath)
        updatePlayer(updated)
    }

    fun onPlayerBattleUpdate(newData: PlayerData) {
        battleOrchestrator.grindingPlayerData = newData
        updatePlayer(newData)
    }

    fun onBattleAgain(newData: PlayerData) {
        val finalData = battleOrchestrator.handleBattleAgain(newData)
        updatePlayer(finalData)
    }

    fun onBattleFlee(newData: PlayerData) {
        val finalData = battleOrchestrator.handleLeave(newData)
        updatePlayer(finalData)
        popBackstack()
    }

    fun onBattleLeave(newData: PlayerData, victoryProcessed: Boolean) {
        val currentArc = selectedStoryArc
        val currentArena = battleOrchestrator.arenaOpponent
        val currentExam = battleOrchestrator.currentGuildExam
        val towerMode = battleOrchestrator.isTowerBattle

        val finalData = battleOrchestrator.handleLeave(newData)
        
        if (victoryProcessed) {
            val bossDefeated = finalData.defeatedBosses.size > playerData.value.defeatedBosses.size
            if (bossDefeated) {
                val newBoss = (finalData.defeatedBosses - playerData.value.defeatedBosses.toSet()).firstOrNull()
                analytics.logEvent("boss_defeat") {
                    param("boss_name", newBoss ?: "Unknown")
                    param("player_level", finalData.level.toLong())
                }
            }
        }

        selectedStoryArc = null
        
        var stateToSave = finalData

        if ((currentArc != null) && victoryProcessed) {
            selectedStoryArc = currentArc
            currentEventIndex = stateToSave.currentStoryEventIndex
            
            if (currentEventIndex >= currentArc.events.size) {
                processArcCompletion(currentArc)
                return // processArcCompletion handles its own navigation and update
            } else {
                battleOrchestrator.storyEnemyName = null
                popBackstack() 
            }
        } else if (currentArc != null) {
            popBackstack() // Pop Battle
            popBackstack() // Pop Dialogue
        } else if ((currentArena != null) && victoryProcessed) {
            popBackstack() // Return to ArenaSelection
        } else if (towerMode && victoryProcessed) {
            popBackstack() // Return to Tower
        } else if (currentExam != null && victoryProcessed) {
            popBackstack() // Return to Guild
        } else {
            if (victoryProcessed && !stateToSave.towerUnlockedMessageSeen && TowerDatabase.isTowerUnlocked(stateToSave.defeatedBosses)) {
                towerUnlockMessage = "The seal on the BATTLE TOWER has been broken! You can now access it in the Adventure tab."
                stateToSave = stateToSave.copy(towerUnlockedMessageSeen = true)
            }
            popBackstack() 
        }

        updatePlayer(stateToSave)
    }

    private fun processArcCompletion(arc: StoryArc, extraXp: Long = 0L, extraGold: Long = 0L) {
        analytics.logEvent("arc_completion") {
            param("arc_id", arc.id.toLong())
            param("arc_title", arc.title)
        }
        
        // Define ranges for Main vs Side arcs
        val isSideArc = when {
            arc.id in 100..999 -> true    // Original Side Arcs
            arc.id >= 2000 -> true         // Legacy Side Arcs
            else -> false
        }

        updatePlayer { current ->
            val alreadyCompleted = if (isSideArc) {
                current.completedSideStoryArcs.contains(arc.id)
            } else {
                current.completedStoryArcs.contains(arc.id)
            }

            val updatedArcs = if (isSideArc) current.completedStoryArcs else (current.completedStoryArcs + arc.id).distinct()
            val updatedSideArcs = if (isSideArc) (current.completedSideStoryArcs + arc.id).distinct() else current.completedSideStoryArcs
            
            val updatedTitles = if (arc.rewardTitle != null) {
                (current.unlockedTitles + arc.rewardTitle).distinct()
            } else current.unlockedTitles
            
            val currentQuests = current.quests.toMutableList()
            val questsToFilter = if (current.isReborn) GameDatabase.allQuests else GameDatabase.allQuests.filter { !it.title.contains("(Legacy)") }
            val newQuests = questsToFilter.filter {
                current.unlockedLocations.contains(it.requiredLocation) &&
                currentQuests.none { existing -> existing.title == it.title }
            }
            currentQuests.addAll(newQuests)

            val rewardXp = if (alreadyCompleted) 0L else arc.rewardXp
            val rewardGold = if (alreadyCompleted) 0L else arc.rewardGold

            val stats = current.gainExperience(rewardXp + extraXp)

            val finalized = stats.copy(
                gold = stats.gold + rewardGold + extraGold,
                completedStoryArcs = updatedArcs,
                completedSideStoryArcs = updatedSideArcs,
                unlockedTitles = updatedTitles,
                currentTitle = arc.rewardTitle ?: stats.currentTitle,
                quests = currentQuests,
                activeStoryArcId = if (alreadyCompleted) current.activeStoryArcId else null,
                currentStoryEventIndex = if (alreadyCompleted) current.currentStoryEventIndex else 0 
            )
            
            if (!finalized.towerUnlockedMessageSeen && TowerDatabase.isTowerUnlocked(finalized.defeatedBosses)) {
                towerUnlockMessage = "The seal on the BATTLE TOWER has been broken! You can now access it in the Adventure tab."
                finalized.copy(towerUnlockedMessageSeen = true)
            } else finalized
        }

        previousLevelForCompletion = playerData.value.level
        navigateTo(Screen.ArcCompletion)
    }

    fun onArcCompletionContinue() {
        previousLevelForCompletion = 1
        selectedStoryArc = null
        currentEventIndex = 0
        battleOrchestrator.isStoryReplay = false
        battleOrchestrator.storyEnemyName = null
        _backstack.clear()
        _backstack.add(Screen.GameMenu)
        _backstack.add(Screen.StorySelection)
    }

    fun onPerformRebirth(name: String, gender: String, className: String, raceName: String) {
        val targetSlot = activeSlot
        val id = _deviceId.value ?: return // Guard: ID must be ready for online action
        val oldData = playerData.value

        if (oldData.isReborn) return
        
        viewModelScope.launch {
            val claimed = com.solerforge.lumeria.managers.NameManager.claimName(name, id)
            if (!claimed) {
                return@launch
            }

            val race = com.solerforge.lumeria.database.RaceDatabase.getRace(raceName)
            val newData = PlayerData(
                playerName = name,
                gender = gender,
                playerRace = raceName,
                playerClass = className,
                isReborn = true,
                legacyHeroStats = oldData,
                introSeen = true,
                currentLocation = "Celestial Plains",
                unlockedLocations = listOf("Celestial Plains"),
                joinedGuild = oldData.joinedGuild,
                joinedGuildId = oldData.joinedGuildId,
                saveVersion = 5
            )
            
            val baseStats = GameDatabase.getClassBaseStats(className)
            val statsAppliedData = newData.copy(
                strength = baseStats[0] + race.strBonus,
                vitality = baseStats[1] + race.vitBonus,
                defense = baseStats[2] + race.defBonus,
                intelligence = baseStats[3] + race.intBonus,
                agility = baseStats[4] + race.agiBonus,
                luck = baseStats[5] + race.luckBonus,
                wisdom = baseStats[6] + race.wisBonus
            )

            val finalizedData = applyClassBonuses(statsAppliedData, className)

            repository.savePlayerData(finalizedData, targetSlot)
            updatePlayer { finalizedData }
            _backstack.clear()
            _backstack.add(Screen.RebirthIntro)
        }
    }

    fun onDeathReturn() {
        updatePlayer { current ->
            current.copy(
                hp = current.maxHp,
                mana = current.maxMana,
            )
        }
        
        // Clear story state on death return to prevent bugs
        selectedStoryArc = null
        currentEventIndex = 0
        battleOrchestrator.isStoryReplay = false
        battleOrchestrator.storyEnemyName = null
        
        _backstack.clear()
        _backstack.add(Screen.GameMenu)
    }

    fun onAcceptBounty(bountyId: Int) {
        val current = playerData.value
        if (current.activeBountyId == null) {
            updatePlayer(current.copy(activeBountyId = bountyId))
            navigateTo(Screen.GameMenu)
        }
    }

    fun onArenaBattleStart(opponent: com.solerforge.lumeria.database.ArenaOpponent) {
        battleOrchestrator.startArenaBattle(opponent, playerData.value)
    }

    fun onShadowBattleStart(shadow: PlayerData) {
        battleOrchestrator.startShadowBattle(shadow, playerData.value)
    }

    fun onTowerBattleStart() {
        battleOrchestrator.startTowerBattle(playerData.value)
    }

    fun onStartGuildExam(exam: com.solerforge.lumeria.database.GuildDatabase.GuildExam) {
        battleOrchestrator.startGuildExam(exam, playerData.value)
    }

    fun onFishCaught(fish: com.solerforge.lumeria.database.Fish) {
        updatePlayer { current ->
            var updatedXp = current.fishingXp + 25 
            var updatedLevel = current.fishingLevel
            
            val needed = FishDatabase.getXpForNextLevel(updatedLevel)
            if (updatedXp >= needed) {
                updatedXp -= needed
                updatedLevel++
            }
            
            current.copy(
                inventory = current.inventory + fish.name,
                fishingXp = updatedXp,
                fishingLevel = updatedLevel,
                totalFishCaught = current.totalFishCaught + 1,
            )
        }
    }

    fun onBossBattle(locationName: String) {
        battleOrchestrator.locationOverride = locationName
        battleOrchestrator.startBossBattle(locationName, grindingPlayerData ?: playerData.value)
    }

    fun onWorldEventComplete() {
        popBackstack()
    }

    fun dismissTowerUnlock() {
        towerUnlockMessage = null
    }

    fun resetStorySelection() {
        selectedStoryArc = null
        currentEventIndex = 0
        battleOrchestrator.isStoryReplay = false
        battleOrchestrator.storyEnemyName = null
        popBackstack()
    }

    fun replaceWith(screen: Screen) {
        _backstack.clear()
        _backstack.add(screen)
    }

    fun onEnterCode(code: String, deviceId: String): String {
        if (!BuildConfig.DEBUG) return "Invalid Code (Release Restricted)"

        return when (code.trim().lowercase()) {
            "materger" -> {
                if (deviceId != "66f1b16f3832fb84" && 
                    deviceId != "193856e18771485c" && 
                    deviceId != "099246b8b868c49e") {
                    return "Unauthorized Device: Your ID ($deviceId) is not on the whitelist."
                }
                val current = playerData.value
                val allArenaNames = com.solerforge.lumeria.database.ArenaEnemyDatabase.opponents.map { it.enemy.name }
                val worldBosses = listOf(
                    "Training Captain", "Goblin King", "Crystal Guardian", 
                    "Stone Titan", "Marsh Horror", "Ancient Dragon", "Lord Xarthos"
                )
                val leveledData = current.copy(
                    level = 100,
                    playerName = "Xious",
                    isReborn = false,
                    gold = current.gold + 5000000,
                    statPoints = current.statPoints + 495,
                    totalStatPointsEarned = current.totalStatPointsEarned + 495,
                    renown = 500000,
                    nationUpgradeLevel = 10,
                    claimedRankIds = (0..7).toList(),
                    completedArenaOpponents = allArenaNames,
                    defeatedBosses = (current.defeatedBosses + worldBosses).distinct(),
                    completedStoryArcs = (1..20).toList(),
                    towerHighestFloor = 100,
                    unlockedTraits = (current.unlockedTraits + "Eyes of the Creator").distinct(),
                    visitedBilly = true,
                    visitedInn = true,
                    visitedForge = true
                )
                val updated = leveledData.copy(
                    maxHp = leveledData.calculateMaxHp(),
                    maxMana = leveledData.calculateMaxMana(),
                    hp = leveledData.calculateMaxHp(),
                    mana = leveledData.calculateMaxMana()
                )
                updatePlayer(updated)
                "Code Accepted: Ready for Rebirth! Visit the Rebirth Altar."
            }
            else -> "Invalid Code"
        }
    }

    fun exportPlayerData(): String {
        return repository.exportSave(playerData.value)
    }

    fun importPlayerData(saveString: String): String {
        val importedData = repository.importSave(saveString)
        return if (importedData != null) {
            updatePlayer { importedData }
            "Save imported successfully! Welcome back, hero."
        } else {
            "Invalid save code. Please try again."
        }
    }

    fun syncCloudSave(onResult: (String) -> Unit) {
        val targetSlot = activeSlot
        viewModelScope.launch {
            val success = repository.syncFromCloud(targetSlot)
            if (success) {
                onResult("Cloud save restored successfully!")
            } else {
                onResult("No cloud save found or sync failed.")
            }
        }
    }

    fun syncLeaderboard(deviceId: String) {
        viewModelScope.launch {
            LeaderboardManager.updateLeaderboard(playerData.value, deviceId)
        }
    }
}

class MainViewModelFactory(private val repository: PlayerDataRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
