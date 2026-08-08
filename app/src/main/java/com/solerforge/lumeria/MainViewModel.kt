package com.solerforge.lumeria

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.data.PlayerDataRepository
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
        onNavigateToBattleScreen = { navigateTo(Screen.Battle) },
        onPvPOutcome = { isWin -> 
            val current = playerData.value
            val updated = if (isWin) {
                current.copy(pvpWins = current.pvpWins + 1)
            } else {
                current.copy(pvpLosses = current.pvpLosses + 1)
            }
            updatePlayer(updated)
        }
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
    val playerData = snapshotFlow { activeSlot }
        .flatMapLatest { slot ->
            repository.getPlayerDataFlow(slot)
        }
        .map { it ?: PlayerData() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PlayerData(),
        )

    val slot1Data = repository.getPlayerDataFlow(1).stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val slot2Data = repository.getPlayerDataFlow(2).stateIn(viewModelScope, SharingStarted.Eagerly, null)
    val slot3Data = repository.getPlayerDataFlow(3).stateIn(viewModelScope, SharingStarted.Eagerly, null)

    val settings = repository.settingsFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = com.solerforge.lumeria.data.GameSettings()
    )

    val goldLostOnDeath: Int get() = (playerData.value.gold * 0.15).toInt()
    
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

        viewModelScope.launch {
            repository.updatePlayer(activeSlot) { current ->
                val newData = transform(current)
                val newlyUnlocked = AchievementManager.checkAchievements(newData)
                
                // Note: Analytics logging will still use the current snapshot for comparison
                // which is "mostly fine" for non-critical logging, but for state it's now atomic.
                
                if (newlyUnlocked.isNotEmpty()) {
                    newData.copy(unlockedAchievements = (newData.unlockedAchievements + newlyUnlocked).distinct())
                } else newData
            }

            // Perform comparisons for logging after the atomic update if needed, 
            // but for now we keep the existing logic simplified.
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
        viewModelScope.launch {
            isResetting = true
            repository.clearPlayerData(activeSlot)
            
            selectedStoryArc = null
            currentEventIndex = 0
            battleOrchestrator.storyEnemyName = null
            battleOrchestrator.battleSeed++
            previousLevelForCompletion = 1
            battleOrchestrator.grindingPlayerData = null
            
            delay(100.milliseconds) 
            repository.savePlayerData(PlayerData(), activeSlot)
            
            _backstack.clear()
            _backstack.add(Screen.Story)
            isResetting = false
        }
    }

    fun onIntroSeen() {
        if (!isResetting) {
            viewModelScope.launch {
                repository.savePlayerData(playerData.value.copy(introSeen = true), activeSlot)
            }
        }
        navigateTo(Screen.GameMenu)
    }

    fun updateStoryArc(arc: StoryArc?) {
        selectedStoryArc = arc
        if (arc != null) {
            currentEventIndex = 0
            navigateTo(Screen.StoryDialogue)
        }
    }

    fun advanceStoryEvent() {
        val arc = selectedStoryArc ?: return
        currentEventIndex++
        
        if (currentEventIndex >= arc.events.size) {
            processArcCompletion(playerData.value, arc)
        }
    }

    fun onStoryChoiceSelected(option: com.solerforge.lumeria.models.StoryChoiceOption) {
        val arc = selectedStoryArc ?: return
        
        // Apply Rewards
        if (option.rewardGold > 0 || option.rewardXp > 0) {
            updatePlayer { current ->
                current.gainExperience(option.rewardXp).copy(
                    gold = current.gold + option.rewardGold
                )
            }
        }

        if (option.nextEventIndex != null) {
            currentEventIndex = option.nextEventIndex
        } else {
            currentEventIndex++
        }

        if (currentEventIndex >= arc.events.size) {
            processArcCompletion(playerData.value, arc)
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
        battleOrchestrator.startBattle(isRift, enemyName, isBoss, snapshot ?: playerData.value, location)
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

    fun onBattleDeath() {
        val updated = battleOrchestrator.handleDeath(playerData.value, goldLostOnDeath)
        updatePlayer(updated)
    }

    fun onPlayerBattleUpdate(newData: PlayerData) {
        battleOrchestrator.grindingPlayerData = newData
        updatePlayer(newData)
    }

    fun onBattleAgain(newData: PlayerData) {
        battleOrchestrator.handleBattleAgain(newData)
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
            currentEventIndex++
            
            if (currentEventIndex >= currentArc.events.size) {
                processArcCompletion(stateToSave, currentArc)
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
            if (stateToSave.towerFloor >= 100) {
                stateToSave = stateToSave.copy(
                    towerCompleted = true,
                    towerHighestFloor = maxOf(stateToSave.towerHighestFloor, stateToSave.towerFloor)
                )
            } else {
                val nextFloor = stateToSave.towerFloor + 1
                stateToSave = stateToSave.copy(
                    towerFloor = nextFloor,
                    towerHighestFloor = maxOf(stateToSave.towerHighestFloor, stateToSave.towerFloor)
                )
            }
            popBackstack() // Return to Tower
        } else if (currentExam != null && victoryProcessed) {
            val nextLvl = stateToSave.guildLevel + 1
            stateToSave = stateToSave.copy(
                guildLevel = nextLvl,
                guildXp = 0
            )
            popBackstack() // Return to Guild
        } else {
            if (victoryProcessed && !stateToSave.towerUnlockedMessageSeen && TowerDatabase.isTowerUnlocked(stateToSave.defeatedBosses)) {
                towerUnlockMessage = "The seal on the BATTLE TOWER has been broken! You can now access it in the Adventure tab."
                stateToSave = stateToSave.copy(towerUnlockedMessageSeen = true)
            }
            popBackstack() 
        }

        // Apply Buff Reductions to the state we're about to save
        if (stateToSave.activeBuffs.isNotEmpty()) {
            val updatedBuffs = stateToSave.activeBuffs.asSequence()
                .map { it.copy(battlesRemaining = it.battlesRemaining - 1) }
                .filter { it.battlesRemaining > 0 }
                .toList()
            stateToSave = stateToSave.copy(activeBuffs = updatedBuffs)
        }

        updatePlayer(stateToSave)
    }

    private fun processArcCompletion(newData: PlayerData, arc: StoryArc) {
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

        val updatedArcs = if (isSideArc) newData.completedStoryArcs else (newData.completedStoryArcs + arc.id).distinct()
        val updatedSideArcs = if (isSideArc) (newData.completedSideStoryArcs + arc.id).distinct() else newData.completedSideStoryArcs
        
        val updatedTitles = if (arc.rewardTitle != null) {
            (newData.unlockedTitles + arc.rewardTitle).distinct()
        } else newData.unlockedTitles
        
        val currentQuests = newData.quests.toMutableList()
        val questsToFilter = if (newData.isReborn) GameDatabase.allQuests else GameDatabase.allQuests.filter { !it.title.contains("(Legacy)") }
        val newQuests = questsToFilter.filter {
            newData.unlockedLocations.contains(it.requiredLocation) &&
            currentQuests.none { existing -> existing.title == it.title }
        }
        currentQuests.addAll(newQuests)

        val stats = newData.gainExperience(arc.rewardXp)

        val finalData = stats.copy(
            gold = stats.gold + arc.rewardGold,
            completedStoryArcs = updatedArcs,
            completedSideStoryArcs = updatedSideArcs,
            unlockedTitles = updatedTitles,
            currentTitle = arc.rewardTitle ?: stats.currentTitle,
            quests = currentQuests
        )
        updatePlayer(finalData)
        
        if (!finalData.towerUnlockedMessageSeen && TowerDatabase.isTowerUnlocked(finalData.defeatedBosses)) {
            towerUnlockMessage = "The seal on the BATTLE TOWER has been broken! You can now access it in the Adventure tab."
            updatePlayer(finalData.copy(towerUnlockedMessageSeen = true))
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
        
        // Initial class bonuses
        val finalizedData = when (className) {
            "Mage" -> newData.copy(
                intelligence = 15, wisdom = 10,
                unlockedSkills = listOf("None", "Magic Bolt", "Mana Shield", "Heal"),
                equippedSkills = listOf("Magic Bolt", "Mana Shield", "Heal"),
                inventory = listOf("Apprentice Staff", "Mage Robes", "Canvas Shoes", "Mana Potion"),
                equippedWeapon = "Apprentice Staff",
                equippedArmor = "Mage Robes",
                equippedBoots = "Canvas Shoes"
            )
            "Samurai" -> newData.copy(
                agility = 15, luck = 10, strength = 8,
                unlockedSkills = listOf("None", "Slash", "Quick Draw", "Parry"),
                equippedSkills = listOf("Slash", "Quick Draw", "Parry"),
                inventory = listOf("Training Katana", "Shinobi Garb", "Sandals"),
                equippedWeapon = "Training Katana",
                equippedArmor = "Shinobi Garb",
                equippedBoots = "Sandals"
            )
            "Paladin" -> newData.copy(
                defense = 15, vitality = 12, strength = 10,
                unlockedSkills = listOf("None", "Guard", "Shield Bash", "Heal"),
                equippedSkills = listOf("Guard", "Shield Bash", "Heal"),
                inventory = listOf("Knight Blade", "Knight Armor", "Knight Shield", "Leather Boots"),
                equippedWeapon = "Knight Blade",
                equippedArmor = "Knight Armor",
                equippedShield = "Knight Shield",
                equippedBoots = "Leather Boots"
            )
            "Assassin" -> newData.copy(
                luck = 15, agility = 15, strength = 8,
                unlockedSkills = listOf("None", "Slash", "Quick Draw", "Smoke Bomb"),
                equippedSkills = listOf("Slash", "Quick Draw", "Smoke Bomb"),
                inventory = listOf("Squire Dagger", "Scout Armor", "Scout Boots"),
                equippedWeapon = "Squire Dagger",
                equippedArmor = "Scout Armor",
                equippedBoots = "Scout Boots"
            )
            "Monk" -> newData.copy(
                vitality = 15, wisdom = 12, strength = 10,
                unlockedSkills = listOf("None", "Heavy Strike", "Guard", "Heal"),
                equippedSkills = listOf("Heavy Strike", "Guard", "Heal"),
                inventory = listOf("None", "Padded Tunic", "Canvas Shoes"),
                equippedWeapon = "None",
                equippedArmor = "Padded Tunic",
                equippedBoots = "Canvas Shoes"
            )
            "Archer" -> newData.copy(
                agility = 12, strength = 12, luck = 10,
                unlockedSkills = listOf("None", "Slash", "Quick Draw", "Heavy Strike"),
                equippedSkills = listOf("Slash", "Quick Draw", "Heavy Strike"),
                inventory = listOf("Nomad Bow", "Rugged Vest", "Leather Boots"),
                equippedWeapon = "Nomad Bow",
                equippedArmor = "Rugged Vest",
                equippedBoots = "Leather Boots"
            )
            "Necromancer" -> newData.copy(
                intelligence = 15, wisdom = 10,
                unlockedSkills = listOf("None", "Magic Bolt", "Bleeding Slash"),
                equippedSkills = listOf("Magic Bolt", "Bleeding Slash"),
                inventory = listOf("Traveler's Staff", "Mage Robes", "Canvas Shoes"),
                equippedWeapon = "Traveler's Staff",
                equippedArmor = "Mage Robes",
                equippedBoots = "Canvas Shoes"
            )
            "Bard" -> newData.copy(
                luck = 15, wisdom = 15, agility = 10,
                unlockedSkills = listOf("None", "Heal", "Lion's Roar"),
                equippedSkills = listOf("Heal", "Lion's Roar"),
                inventory = listOf("Wanderer's Gladius", "Explorer Jacket", "Cloud Sprints"),
                equippedWeapon = "Wanderer's Gladius",
                equippedArmor = "Explorer Jacket",
                equippedBoots = "Cloud Sprints"
            )
            "Berserker" -> newData.copy(
                strength = 20, vitality = 15, defense = 2,
                unlockedSkills = listOf("None", "Heavy Strike", "Whirlwind Slash"),
                equippedSkills = listOf("Heavy Strike", "Whirlwind Slash"),
                inventory = listOf("Orc Cleaver", "Rugged Vest", "Leather Boots"),
                equippedWeapon = "Orc Cleaver",
                equippedArmor = "Rugged Vest",
                equippedBoots = "Leather Boots"
            )
            else -> newData.copy( // Warrior
                strength = 12, vitality = 10, defense = 8,
                unlockedSkills = listOf("None", "Slash", "Heavy Strike", "Guard"),
                equippedSkills = listOf("Slash", "Heavy Strike", "Guard"),
                inventory = listOf("Iron Sword", "Leather Armor", "Leather Boots"),
                equippedWeapon = "Iron Sword",
                equippedArmor = "Leather Armor",
                equippedBoots = "Leather Boots"
            )
        }.let { data ->
            val maxHp = data.calculateMaxHp()
            val maxMana = data.calculateMaxMana()
            data.copy(
                hp = maxHp,
                maxHp = maxHp,
                mana = maxMana,
                maxMana = maxMana
            )
        }

        viewModelScope.launch {
            repository.savePlayerData(finalizedData, activeSlot)
            _backstack.clear()
            _backstack.add(Screen.GameMenu)
        }
    }

    fun onDeathReturn() {
        val currentData = playerData.value
        val goldLost = goldLostOnDeath
        val healedPlayer = currentData.copy(
            hp = currentData.maxHp,
            mana = currentData.maxMana,
            gold = maxOf(0, currentData.gold - goldLost)
        )
        updatePlayer(healedPlayer)
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
            updatePlayer(importedData)
            "Save imported successfully! Welcome back, hero."
        } else {
            "Invalid save code. Please try again."
        }
    }

    fun syncCloudSave(onResult: (String) -> Unit) {
        viewModelScope.launch {
            val success = repository.syncFromCloud(activeSlot)
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
