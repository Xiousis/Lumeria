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
        onNavigate = { navigateTo(it) }
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

    var deviceId: String? = null

    var towerUnlockMessage by mutableStateOf<String?>(null)
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

    fun selectSlot(slot: Int) {
        activeSlot = slot
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
            try {
                repository.updatePlayer(targetSlot) { current ->
                    val newData = transform(current)
                    val newlyUnlocked = AchievementManager.checkAchievements(newData)
                    
                    if (newlyUnlocked.isNotEmpty()) {
                        newData.copy(unlockedAchievements = (newData.unlockedAchievements + newlyUnlocked).distinct())
                    } else newData
                }
            } catch (e: Exception) {
                // TODO: Show recovery UI. For now, we log and prevent overwrite.
                analytics.logEvent("save_corruption_prevented") {
                    param("slot", activeSlot.toLong())
                    param("error", e.message ?: "Unknown")
                }
                return@launch
            }

            // Perform comparisons for logging after the atomic update
            val newData = transform(playerData.value)
            val previousData = playerData.value

            if (newData.level > previousData.level) {
                analytics.logEvent(FirebaseAnalytics.Event.LEVEL_UP) {
                    param(FirebaseAnalytics.Param.LEVEL, newData.level.toLong())
                    param("character_class", newData.joinedGuild ?: "None")
                }
            }

            if (newData.gold >= 1000000 && previousData.gold < 1000000) {
                analytics.logEvent("wealth_milestone") {
                    param("amount", 1000000L)
                }
            }

            // Sync Leaderboard if stats changed (Reborn players only)
            if (newData.isReborn && (newData.level != previousData.level || 
                newData.pvpWins != previousData.pvpWins || 
                newData.pvpLosses != previousData.pvpLosses)) {
                deviceId?.let { syncLeaderboard(it) }
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

    fun onIntroSeen() {
        if (!isResetting) {
            val targetSlot = activeSlot
            viewModelScope.launch {
                repository.savePlayerData(playerData.value.copy(introSeen = true), targetSlot)
            }
        }
        navigateTo(Screen.GameMenu)
    }

    fun updateStoryArc(arc: StoryArc?) {
        selectedStoryArc = arc
        if (arc != null) {
            val isReplay = playerData.value.completedStoryArcs.contains(arc.id) || 
                           playerData.value.completedSideStoryArcs.contains(arc.id)
            
            val savedArcId = playerData.value.activeStoryArcId
            val savedIndex = playerData.value.currentStoryEventIndex

            if (!isReplay && savedArcId == arc.id) {
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
        
        updatePlayer { it.copy(currentStoryEventIndex = currentEventIndex) }
        
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
                updated.copy(currentStoryEventIndex = nextIndex)
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
                activeStoryArcId = null,
                currentStoryEventIndex = 0 
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
        navigateTo(Screen.StorySelection)
    }

    fun onPerformRebirth(name: String, gender: String, className: String) {
        val oldData = playerData.value
        val newData = PlayerData(
            playerName = name,
            gender = gender,
            playerClass = className,
            isReborn = true,
            legacyHeroStats = oldData,
            introSeen = true,
            currentLocation = "Training Fields",
            unlockedLocations = listOf("Training Fields"),
            saveVersion = 3
        )
        
        val baseStats = GameDatabase.getClassBaseStats(className)
        val statsAppliedData = newData.copy(
            strength = baseStats[0],
            vitality = baseStats[1],
            defense = baseStats[2],
            intelligence = baseStats[3],
            agility = baseStats[4],
            luck = baseStats[5],
            wisdom = baseStats[6]
        )

        // Initial class bonuses
        val finalizedData = when (className) {
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

        val targetSlot = activeSlot
        viewModelScope.launch {
            repository.savePlayerData(finalizedData, targetSlot)
            _backstack.clear()
            _backstack.add(Screen.GameMenu)
        }
    }

    fun onDeathReturn() {
        updatePlayer { current ->
            current.copy(
                hp = current.maxHp,
                mana = current.maxMana,
            )
        }
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
        updatePlayer { it.copy(battleBuffsConsumed = false) }
        battleOrchestrator.startArenaBattle(opponent, playerData.value)
    }

    fun onShadowBattleStart(shadow: PlayerData) {
        updatePlayer { it.copy(battleBuffsConsumed = false) }
        battleOrchestrator.startShadowBattle(shadow, playerData.value)
    }

    fun onTowerBattleStart() {
        updatePlayer { it.copy(battleBuffsConsumed = false) }
        battleOrchestrator.startTowerBattle(playerData.value)
    }

    fun onStartGuildExam(exam: com.solerforge.lumeria.database.GuildDatabase.GuildExam) {
        updatePlayer { it.copy(battleBuffsConsumed = false) }
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
        updatePlayer { it.copy(battleBuffsConsumed = false) }
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
        popBackstack()
    }

    fun onEnterCode(code: String, deviceId: String): String {
        if (!BuildConfig.DEBUG) return "Invalid Code"

        return when (code.trim()) {
            "Materger" -> {
                if (deviceId != "66f1b16f3832fb84") {
                    return "Unauthorized Device: This code is restricted to the developer."
                }
                val current = playerData.value
                val allArenaNames = com.solerforge.lumeria.database.ArenaEnemyDatabase.opponents.map { it.enemy.name }
                val worldBosses = listOf(
                    "Training Captain", "Goblin King", "Crystal Guardian", 
                    "Stone Titan", "Marsh Horror", "Ancient Dragon", "Lord Xarthos"
                )
                val leveledData = current.copy(
                    level = 100,
                    gold = current.gold + 5000000,
                    statPoints = current.statPoints + 495,
                    renown = 500000,
                    nationUpgradeLevel = 10,
                    claimedRankIds = (0..7).toList(),
                    completedArenaOpponents = allArenaNames,
                    defeatedBosses = (current.defeatedBosses + worldBosses).distinct(),
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
                "Code Accepted: Max Level, Max Rank, Max Upgrades & 5M Gold granted!"
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
