package com.solerforge.lumeria.battle

import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.solerforge.lumeria.R
import com.solerforge.lumeria.data.Enemy
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.*
import com.solerforge.lumeria.utils.SoundManager
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.time.Duration.Companion.milliseconds

class BattleViewModel(
    val playerData: PlayerData,
    val isBossBattle: Boolean = false,
    val storyEnemyName: String? = null,
    val arenaOpponent: ArenaOpponent? = null,
    val activeBounty: com.solerforge.lumeria.data.Bounty? = null,
    val isTowerBattle: Boolean = false,
    val isRiftBattle: Boolean = false,
    val isPvPBattle: Boolean = false,
    val isStoryReplay: Boolean = false,
    val shadowOpponent: PlayerData? = null,
    val guildExam: GuildDatabase.GuildExam? = null,
    val currentEventIndex: Int = 0,
    val locationOverride: String? = null,
    val onHaptic: ((HapticType) -> Unit)? = null,
) : ViewModel() {

    private val _state: MutableStateFlow<BattleUiState> = MutableStateFlow(createInitialState())
    val state: StateFlow<BattleUiState> get() = _state.asStateFlow()

    private fun createInitialState(): BattleUiState {
        val locationName = when {
            locationOverride != null -> locationOverride
            arenaOpponent != null -> "Grand Arena"
            isTowerBattle -> "Tower of Trials"
            guildExam != null -> when (playerData.joinedGuild) {
                "House of Fire" -> "Fire Exam"
                "House of Water" -> "Water Exam"
                "House of Wind" -> "Wind Exam"
                else -> "Grand Arena"
            }
            storyEnemyName != null -> {
                // Map specific story enemies to their iconic backgrounds
                when {
                    storyEnemyName.contains("Garrick", ignoreCase = true) -> "Training Fields"
                    storyEnemyName.contains("Bandit", ignoreCase = true) -> "Training Fields"
                    storyEnemyName.contains("Skarr", ignoreCase = true) -> "Goblin Forest"
                    storyEnemyName.contains("Raider", ignoreCase = true) -> "Goblin Forest"
                    storyEnemyName.contains("Goblin", ignoreCase = true) -> "Goblin Forest"
                    storyEnemyName.contains("Crystal", ignoreCase = true) -> "Crystal Caverns"
                    storyEnemyName.contains("Dorian", ignoreCase = true) -> "Training Fields" // Haunted Castle feel?
                    storyEnemyName.contains("Earthshaker", ignoreCase = true) -> "Stonehold Pass"
                    storyEnemyName.contains("Mire", ignoreCase = true) -> "Shadow Marsh"
                    storyEnemyName.contains("Infernal", ignoreCase = true) -> "Dragon Peaks"
                    storyEnemyName.contains("Warden", ignoreCase = true) -> "Dragon Peaks"
                    storyEnemyName.contains("Juggernaut", ignoreCase = true) -> "Dark Citadel"
                    storyEnemyName.contains("Krag", ignoreCase = true) -> "Stonehold Pass"
                    storyEnemyName.contains("Xarthos", ignoreCase = true) -> "Dark Citadel"
                    storyEnemyName.contains("Void", ignoreCase = true) -> "Dark Citadel"
                    storyEnemyName.contains("World Eater", ignoreCase = true) -> "Dark Citadel"
                    else -> "Dark Citadel"
                }
            }
            else -> playerData.currentLocation
        }
        val location = WorldDatabase.getLocation(locationName)

        val enemy = when {
            shadowOpponent != null -> Enemy(
                name = shadowOpponent.playerName,
                baseName = shadowOpponent.playerName,
                maxHp = shadowOpponent.maxHp,
                level = shadowOpponent.level,
                isHumanoid = true,
                rewardXp = shadowOpponent.level * 20L,
                rewardGold = shadowOpponent.level * 5L
            )
            guildExam != null -> Enemy(guildExam.enemyName, guildExam.enemyName, guildExam.hp, guildExam.level, isHumanoid = true, materialDrop = GameDatabase.getRequiredMaterial("Grand Arena"))
            isTowerBattle -> {
                val e = TowerDatabase.getTowerEnemy(playerData.towerFloor)
                e.copy(materialDrop = GameDatabase.getRequiredMaterial("Tower of Trials"))
            }
            arenaOpponent != null -> arenaOpponent.enemy.copy(materialDrop = GameDatabase.getRequiredMaterial("Grand Arena"))
            storyEnemyName == "The Echo of Xious" -> {
                val legacy = playerData.legacyHeroStats ?: playerData
                Enemy(
                    name = "Echo of ${legacy.playerName}",
                    baseName = "The Echo of Xious",
                    maxHp = legacy.maxHp * 2,
                    level = legacy.level,
                    rewardXp = 500000L,
                    rewardGold = 0L,
                    isHumanoid = true,
                    materialDrop = "Spirit of the Founder"
                )
            }
            (storyEnemyName != null) && isBossBattle -> {
                val b = BossDatabase.resolveBoss(storyEnemyName) ?: error("Story Boss $storyEnemyName not found")
                val isHuman = b.name.contains("Captain", ignoreCase = true) || b.name.contains("King", ignoreCase = true) || b.name.contains("Lord", ignoreCase = true)
                Enemy(b.name, b.name, b.hp, b.level, isHumanoid = isHuman, rewardXp = b.rewardXp, rewardGold = b.rewardGold)
            }
            storyEnemyName != null -> {
                val e = StoryEnemyDatabase.resolveEnemy(storyEnemyName, location)
                e.copy(materialDrop = GameDatabase.getRequiredMaterial(location.name))
            }
            isBossBattle -> {
                val bossName = location.boss
                if (bossName == "The Echo of Xious") {
                    val legacy = playerData.legacyHeroStats ?: playerData
                    Enemy(
                        name = "Echo of ${legacy.playerName}",
                        baseName = "The Echo of Xious",
                        maxHp = legacy.maxHp * 2,
                        level = legacy.level,
                        rewardXp = 500000,
                        rewardGold = 0,
                        isHumanoid = true,
                        materialDrop = "Spirit of the Founder"
                    )
                } else {
                    val b = BossDatabase.resolveBoss(bossName) ?: error("Boss $bossName not found")
                    val isHuman = b.name.contains("Captain", ignoreCase = true) || b.name.contains("King", ignoreCase = true) || b.name.contains("Lord", ignoreCase = true)
                    var finalHp = b.hp
                    var finalLevel = b.level
                    var finalName = b.name
                    if (isRiftBattle) {
                        finalName = "Elite $finalName"
                        finalLevel += 10
                        finalHp = (finalHp * 2.0).toInt()
                    }
                    Enemy(finalName, b.name, finalHp, finalLevel, isHumanoid = isHuman, materialDrop = GameDatabase.getRequiredMaterial(location.name))
                }
            }
            else -> {
                val name: String
                var level: Int
                var hp: Int
                val isHuman: Boolean
                
                val activeBounty = playerData.activeBountyId?.let { BountyDatabase.getBounty(it) }
                if ((activeBounty != null) && (activeBounty.location == location.name) && (Math.random() < 0.15)) {
                    name = activeBounty.targetName
                    level = activeBounty.level
                    hp = (level * 40) + 500 // Bounties are tougher
                    isHuman = true
                } else {
                    name = location.enemies.random()
                    level = (location.minLevel..location.maxLevel).random()
                    hp = (level * 25) + 30
                    isHuman = name.contains("Goblin", ignoreCase = true) || name.contains("Orc", ignoreCase = true) || name.contains("Knight", ignoreCase = true) || 
                              name.contains("Rogue", ignoreCase = true) || name.contains("Assassin", ignoreCase = true) || name.contains("Guard", ignoreCase = true) || 
                              name.contains("Squire", ignoreCase = true)
                }

                var finalName = name
                if (isRiftBattle) {
                    finalName = "Elite $name"
                    level += 5
                    hp = (hp * 1.5).toInt()
                }
                Enemy(finalName, name, hp, level, isHumanoid = isHuman, materialDrop = GameDatabase.getRequiredMaterial(location.name))
            }
        }

        val enemyLogoId = getEnemyLogoId(enemy)

        var enemy2: Enemy? = null
        var enemy2LogoId = 0
        
        // --- RANDOM DOUBLE ENCOUNTER (20% chance) ---
        val canDouble = (!isBossBattle) && (storyEnemyName == null) && (arenaOpponent == null) && (!isTowerBattle) && (!isRiftBattle) && (guildExam == null) && (!isPvPBattle) && (shadowOpponent == null)
        if (canDouble && (Math.random() < 0.20)) {
            val name2 = location.enemies.random()
            val level2 = (location.minLevel..location.maxLevel).random()
            val hp2 = (level2 * 25) + 30
            val isHuman2 = name2.contains("Goblin", ignoreCase = true) || name2.contains("Orc", ignoreCase = true) || name2.contains("Knight", ignoreCase = true) || 
                          name2.contains("Rogue", ignoreCase = true) || name2.contains("Assassin", ignoreCase = true) || name2.contains("Guard", ignoreCase = true) || 
                          name2.contains("Squire", ignoreCase = true)
            
            enemy2 = Enemy(name2, name2, hp2, level2, isHumanoid = isHuman2, materialDrop = GameDatabase.getRequiredMaterial(location.name))
            enemy2LogoId = getEnemyLogoId(enemy2)
        }

        return BattleUiState(
            enemy = enemy,
            enemyLogoId = enemyLogoId,
            enemyHp = enemy.maxHp,
            enemy2 = enemy2,
            enemy2LogoId = enemy2LogoId,
            enemy2Hp = enemy2?.maxHp ?: 0,
            playerHp = playerData.hp,
            playerMana = playerData.mana,
            currentPlayerSnapshot = playerData,
            battleLog = listOf(
                LogEntry(
                    if (isRiftBattle && isBossBattle) "⚠ RIFT OVERLORD DETECTED: ${enemy.name}"
                    else if (isRiftBattle) "⚠ VOID RIFT STEP ${playerData.riftStep + 1}/4: ${enemy.name}"
                    else if (isBossBattle) "⚠ BOSS ENCOUNTER: ${enemy.name}"
                    else if (isPvPBattle) "⚔ PVP DUEL: ${enemy.name}"
                    else if (enemy2 != null) "AMBUSH! You are facing a ${enemy.name} and a ${enemy2.name}!"
                    else "A wild ${enemy.name} appears!",
                    if (isBossBattle || isRiftBattle || isPvPBattle) Color.Red else Color.Yellow,
                ),
            ),
            isFleeable = (storyEnemyName == null) && (!isBossBattle) && (arenaOpponent == null) && (!isPvPBattle) && (!isTowerBattle) && (!isRiftBattle) && (guildExam == null), // Disable flee for story/boss/arena/ambush/pvp/tower/rift/exams
            isStoryMode = (storyEnemyName != null) || isBossBattle || (arenaOpponent != null) || isTowerBattle || isPvPBattle,
            isStoryBoss = (storyEnemyName != null) && isBossBattle,
            isStoryReplay = isStoryReplay,
            isBossBattle = isBossBattle,
            isRiftBattle = isRiftBattle,
            isArenaBattle = arenaOpponent != null || isPvPBattle,
            locationName = locationName,
            towerFloor = if (isTowerBattle) playerData.towerFloor else null,
            arenaMoveset = arenaOpponent?.moveset ?: emptyList(),
        )
    }

    // State accessors are handled via the state Flow

    fun takeTurn(skillName: String, onUpdate: (PlayerData) -> Unit) {
        if (state.value.victoryProcessed || state.value.isDying || state.value.isProcessing) return

        viewModelScope.launch {
            _state.update { it.copy(isProcessing = true) }
            
            // --- 1. PLAYER TURN ---
            val initialState = state.value
            val turnResult = BattleEngine.processPlayerTurn(
                initialState, skillName, storyEnemyName, isBossBattle,
            )
            
            val playerTurnState = turnResult.state
            val playerLogs = turnResult.logs

            if (!turnResult.turnConsumed) {
                _state.update { 
                    playerTurnState.copy(
                        battleLog = (it.battleLog + playerLogs).takeLast(20),
                        isProcessing = false,
                    )
                }
                return@launch
            }

            // Trigger Floating Numbers based on HP changes
            val dmg1 = initialState.enemyHp - playerTurnState.enemyHp
            if (dmg1 > 0) {
                triggerFloatingNumber("Enemy1", dmg1.toString(), Color.Red, playerTurnState.battleMessage.contains("CRITICAL"))
            }

            val dmg2 = initialState.enemy2Hp - playerTurnState.enemy2Hp
            if (dmg2 > 0) {
                triggerFloatingNumber("Enemy2", dmg2.toString(), Color.Red)
            }

            val heal = playerTurnState.playerHp - initialState.playerHp
            if (heal > 0) {
                triggerFloatingNumber("Hero", "+$heal", Color.Green)
            }
            
            _state.update { 
                playerTurnState.copy(
                    battleLog = (it.battleLog + playerLogs).takeLast(20),
                    isProcessing = true,
                )
            }
            
            if ((skillName != "Heal") && (!playerLogs.any { it.message.contains("stunned") })) {
                triggerPlayerAttackAnimation()
            }

            delay(1500.milliseconds)

            // --- 2. ENEMY TURN ---
            val stateAfterPlayer = state.value 
            
            if ((stateAfterPlayer.enemyHp > 0 || stateAfterPlayer.enemy2Hp > 0) && (!stateAfterPlayer.victoryProcessed)) {
                _state.update { it.copy(battleLog = (it.battleLog + LogEntry("--- Enemy Turn ---", Color.Gray)).takeLast(20)) }
                delay(500.milliseconds)

                val (enemyTurnState, enemyLogs) = BattleEngine.processEnemyTurn(
                    stateAfterPlayer, isBossBattle,
                )

                // Damage to Player
                val playerDmg = stateAfterPlayer.playerHp - enemyTurnState.playerHp
                if (playerDmg > 0) {
                    triggerFloatingNumber("Hero", playerDmg.toString(), Color.Red)
                }

                // Passive damage to enemies (Status effects)
                val e1Passive = stateAfterPlayer.enemyHp - enemyTurnState.enemyHp
                if (e1Passive > 0) {
                    triggerFloatingNumber("Enemy1", e1Passive.toString(), Color.Yellow)
                }

                val e2Passive = stateAfterPlayer.enemy2Hp - enemyTurnState.enemy2Hp
                if (e2Passive > 0) {
                    triggerFloatingNumber("Enemy2", e2Passive.toString(), Color.Yellow)
                }
                
                _state.update {
                    enemyTurnState.copy(
                        battleLog = (it.battleLog + enemyLogs).takeLast(20),
                        isProcessing = true,
                    )
                }
                
                if (enemyTurnState.playerHp < stateAfterPlayer.playerHp) {
                    if (isBossBattle || isRiftBattle || (arenaOpponent != null)) {
                        triggerScreenShake()
                    }
                    triggerHitAnimation()
                }
                delay(800.milliseconds)
            }
            
            checkBattleEnd(onUpdate)
            _state.update { it.copy(isProcessing = false) }
        }
    }

    private fun updateLogs(newLogs: List<LogEntry>) {
        _state.update { it.copy(battleLog = (it.battleLog + newLogs).takeLast(20)) }
    }

    private fun triggerPlayerAttackAnimation() {
        viewModelScope.launch {
            val isAoE = (state.value.enemy2 != null) && (state.value.enemy2Hp > 0)
            
            _state.update { 
                it.copy(
                    playerOffsetX = 80f, 
                    enemyFlashAlpha = 1f, 
                    enemyOffsetX = 20f, 
                    enemyShake = 10f,
                    enemy2FlashAlpha = if (isAoE) 1f else 0f,
                    enemy2OffsetX = if (isAoE) 20f else 0f,
                    enemy2Shake = if (isAoE) 10f else 0f,
                ) 
            }
            onHaptic?.invoke(HapticType.HIT)
            SoundManager.playSound(R.raw.hit_001)
            
            delay(50.milliseconds)
            SoundManager.playSound(R.raw.monster_hurt)
            
            delay(50.milliseconds)
            _state.update { 
                it.copy(
                    enemyOffsetX = -20f, 
                    enemyShake = -10f,
                    enemy2OffsetX = if (isAoE) -20f else 0f,
                    enemy2Shake = if (isAoE) -10f else 0f
                ) 
            }
            delay(50.milliseconds)
            _state.update { 
                it.copy(
                    enemyOffsetX = 0f, 
                    enemy2OffsetX = 0f, 
                    playerOffsetX = 0f, 
                    enemyFlashAlpha = 0f, 
                    enemy2FlashAlpha = 0f, 
                    enemyShake = 0f,
                    enemy2Shake = 0f
                ) 
            }
        }
    }

    private fun triggerFloatingNumber(target: String, text: String, color: Color, isCrit: Boolean = false) {
        viewModelScope.launch {
            // Target-based X offsets
            val xPos = when(target) {
                "Hero" -> -100f
                "Enemy1" -> 100f
                "Enemy2" -> 250f
                else -> 0f
            }
            
            val num = FloatingNumber(
                text = text,
                color = color,
                isCrit = isCrit,
                initialX = xPos,
                initialY = -50f // Start slightly above center
            )
            
            _state.update { it.copy(floatingNumbers = it.floatingNumbers + num) }
            
            delay(1200.milliseconds)
            
            _state.update { it.copy(floatingNumbers = it.floatingNumbers.filter { n -> n.id != num.id }) }
        }
    }

    private fun triggerHitAnimation() {
        viewModelScope.launch {
            _state.update { it.copy(playerFlashAlpha = 1f, playerShake = 10f) }
            onHaptic?.invoke(HapticType.HIT)
            SoundManager.playSound(R.raw.hit_002)
            delay(100.milliseconds)
            _state.update { it.copy(playerFlashAlpha = 0f, playerShake = 0f) }
        }
    }

    private fun triggerScreenShake() {
        viewModelScope.launch {
            repeat(6) {
                _state.update { it.copy(screenShake = 20f) }
                delay(25.milliseconds)
                _state.update { it.copy(screenShake = -20f) }
                delay(25.milliseconds)
            }
            _state.update { it.copy(screenShake = 0f) }
        }
    }

    private fun getEnemyLogoId(enemy: Enemy): Int {
        return when {
            enemy.name.contains("Echo of", ignoreCase = true) -> R.drawable.battle_xious
            enemy.name.contains("Ignis", ignoreCase = true) -> R.drawable.ignis_battle
            enemy.name.contains("Marina", ignoreCase = true) -> R.drawable.marina_battle
            enemy.name.contains("Zephyr", ignoreCase = true) -> R.drawable.zephyr_battle
            enemy.name.contains("Kaela", ignoreCase = true) -> R.drawable.kaela
            enemy.name.contains("Kazufi", ignoreCase = true) -> R.drawable.kazufi_battle
            
            // World Bosses
            enemy.name.contains("Lord Xarthos", ignoreCase = true) -> R.drawable.lord_xarthos
            enemy.name.contains("Ancient Dragon", ignoreCase = true) -> R.drawable.ancient_dragon
            enemy.name.contains("Crystal Guardian", ignoreCase = true) -> R.drawable.crystal_guardian
            enemy.name.contains("Marsh Horror", ignoreCase = true) -> R.drawable.marsh_horror
            enemy.name.contains("Training Captain", ignoreCase = true) -> R.drawable.training_captain
            enemy.name.contains("Stone Titan", ignoreCase = true) -> R.drawable.stone_titan
            
            // Arena Specific & High Tier Mobs
            enemy.name.contains("Shield Squire", ignoreCase = true) -> R.drawable.shield_squire
            enemy.name.contains("Grand Arbiter", ignoreCase = true) -> R.drawable.grand_arbiter
            enemy.name.contains("Dragon Knight", ignoreCase = true) -> R.drawable.dragon_knight
            enemy.name.contains("Eternal Champion", ignoreCase = true) -> R.drawable.eternal_champion
            enemy.name.contains("Lord of the Void", ignoreCase = true) -> R.drawable.lord_of_the_void
            enemy.name.contains("Shogun of Shadows", ignoreCase = true) -> R.drawable.shogun_of_shadows
            enemy.name.contains("Void Cultist", ignoreCase = true) -> R.drawable.void_cultist_2
            
            enemy.name.contains("Berserker", ignoreCase = true) -> R.drawable.berserker
            enemy.name.contains("Crag Golem", ignoreCase = true) -> R.drawable.crag_golem
            enemy.name.contains("Mountain Man", ignoreCase = true) -> R.drawable.mountain_man
            enemy.name.contains("Dark Sorcerer", ignoreCase = true) -> R.drawable.dark_sorcerer
            enemy.name.contains("Royal Duelist", ignoreCase = true) -> R.drawable.royal_duelist
            enemy.name.contains("Elemental Archer", ignoreCase = true) -> R.drawable.elemental_archer
            enemy.name.contains("Elite Spellsword", ignoreCase = true) -> R.drawable.elite_spellsword
            enemy.name.contains("Grandmaster Monk", ignoreCase = true) -> R.drawable.grandmaster_monk
            enemy.name.contains("Imperial General", ignoreCase = true) -> R.drawable.imperial_general
            enemy.name.contains("Paladin of Light", ignoreCase = true) -> R.drawable.paladin_of_light
            
            enemy.name.contains("Apprentice Mage", ignoreCase = true) -> R.drawable.apprentice_mage
            enemy.name.contains("Arena Wizard", ignoreCase = true) -> R.drawable.arena_wizard
            enemy.name.contains("Desert Nomad", ignoreCase = true) -> R.drawable.desert_nomad
            enemy.name.contains("Drunken Monk", ignoreCase = true) -> R.drawable.drunken_monk
            enemy.name.contains("Initiate Knight", ignoreCase = true) -> R.drawable.initiate_knight
            enemy.name.contains("Shadow Assassin", ignoreCase = true) -> R.drawable.shadow_assassin
            enemy.name.contains("Wandering Samurai", ignoreCase = true) -> R.drawable.wandering_samurai
            enemy.name.contains("Wild Brawler", ignoreCase = true) -> R.drawable.wild_brawler
            enemy.name.contains("Veteran Guard", ignoreCase = true) -> R.drawable.veteran_guard
            
            enemy.name.contains("Billy", ignoreCase = true) -> R.drawable.billy_battle_img

            enemy.name.contains("World Eater", ignoreCase = true) -> {
                when {
                    enemy.name.contains("Phase 1", ignoreCase = true) -> R.drawable.world_eater_phase_1
                    enemy.name.contains("Phase 2", ignoreCase = true) -> R.drawable.world_eater_phase_2
                    else -> R.drawable.world_eater_phase_final
                }
            }
            enemy.name.contains("Rooster", ignoreCase = true) -> R.drawable.aggressive_rooster_001
            enemy.name.contains("Wasp", ignoreCase = true) -> R.drawable.angry_wasp_001
            enemy.name.contains("Snake", ignoreCase = true) || enemy.name.contains("Serpent", ignoreCase = true) -> R.drawable.field_snake_001
            enemy.name.contains("Cave Bat", ignoreCase = true) || enemy.name.contains("Cinder-Bat", ignoreCase = true) -> R.drawable.cave_bat
            enemy.name.contains("Echo Bat", ignoreCase = true) -> R.drawable.echo_bat
            enemy.name.contains("Rock Worm", ignoreCase = true) || enemy.name.contains("Infinite-Serpent", ignoreCase = true) -> R.drawable.rock_worm
            enemy.name.contains("Gem Crawler", ignoreCase = true) || enemy.name.contains("Emerald Crawler", ignoreCase = true) -> R.drawable.gem_crawler
            enemy.name.contains("Stalactite Mimic", ignoreCase = true) -> R.drawable.stalactite_mimic
            enemy.name.contains("Small Spider", ignoreCase = true) || enemy.name.contains("Diamond Weaver", ignoreCase = true) -> R.drawable.small_spider_001
            enemy.name.contains("Crystal Spider", ignoreCase = true) -> R.drawable.crystal_spider
            enemy.name.contains("Forest Spider", ignoreCase = true) -> R.drawable.forest_spider_001
            enemy.name.contains("Crystal Slime", ignoreCase = true) || enemy.name.contains("Astral Slime", ignoreCase = true) -> R.drawable.crystal_slime
            enemy.name.contains("Magma Slime", ignoreCase = true) -> R.drawable.magma_slime
            enemy.name.contains("Slime", ignoreCase = true) -> R.drawable.slime_001
            enemy.name.contains("Stray Dog", ignoreCase = true) || enemy.name.contains("Solar Hound", ignoreCase = true) -> R.drawable.stray_dog_001
            enemy.name.contains("Wild Rat", ignoreCase = true) || enemy.name.contains("Star-Gazer Rabbit", ignoreCase = true) -> R.drawable.wild_rat_001
            enemy.name.contains("Harpy", ignoreCase = true) || enemy.name.contains("Sky-Fin Hawk", ignoreCase = true) -> R.drawable.harpy
            enemy.name.contains("Wood Beetle", ignoreCase = true) || enemy.name.contains("Meteor Beetle", ignoreCase = true) -> R.drawable.wood_beetle_001
            enemy.name.contains("Mountain Wolf", ignoreCase = true) || enemy.name.contains("Frost-Wolf", ignoreCase = true) -> R.drawable.mountain_wolf
            enemy.name.contains("Wolf", ignoreCase = true) || enemy.name.contains("Pup", ignoreCase = true) || enemy.name.contains("Cub", ignoreCase = true) -> R.drawable.wolf_pup_001
            enemy.name.contains("Mountain Hawk", ignoreCase = true) || enemy.name.contains("Iron-Claw Eagle", ignoreCase = true) -> R.drawable.mountain_hawk
            enemy.name.contains("Iron Boar", ignoreCase = true) || enemy.name.contains("Thistle-Back Boar", ignoreCase = true) -> R.drawable.iron_boar
            enemy.name.contains("Wild Boar", ignoreCase = true) -> R.drawable.wild_boar_001
            enemy.name.contains("Goblin King", ignoreCase = true) -> R.drawable.goblin_king_001
            enemy.name.contains("Goblin Archer", ignoreCase = true) -> R.drawable.goblin_archer_001
            enemy.name.contains("Goblin Rogue", ignoreCase = true) -> R.drawable.goblin_rogue_001
            enemy.name.contains("Training Goblin", ignoreCase = true) -> R.drawable.goblin_001
            enemy.name.contains("Goblin", ignoreCase = true) -> R.drawable.goblin_common_001
            enemy.name.contains("Arbiter", ignoreCase = true) -> R.drawable.grand_arbiter
            enemy.name.contains("Cultist", ignoreCase = true) || enemy.name.contains("Void-Walker", ignoreCase = true) -> R.drawable.void_cultist_2
            enemy.name.contains("Wizard", ignoreCase = true) || enemy.name.contains("Sorcerer", ignoreCase = true) || enemy.name.contains("Mage", ignoreCase = true) || enemy.name.contains("Magister", ignoreCase = true) -> R.drawable.vil_elder
            
            enemy.name.contains("Knight", ignoreCase = true) || enemy.name.contains("Paladin", ignoreCase = true) || enemy.name.contains("Squire", ignoreCase = true) || enemy.name.contains("Guard", ignoreCase = true) || enemy.name.contains("Sentinel", ignoreCase = true) -> R.drawable.dark_knight
            
            enemy.name.contains("Orc Grunt", ignoreCase = true) -> R.drawable.orc_grunt
            enemy.name.contains("Orc", ignoreCase = true) -> R.drawable.orc_warrior

            enemy.name.contains("Marsh stalker", ignoreCase = true) || enemy.name.contains("Mist-Stalker", ignoreCase = true) -> R.drawable.marsh_stalker
            enemy.name.contains("Poison Toad", ignoreCase = true) || enemy.name.contains("Corrupted Toad", ignoreCase = true) -> R.drawable.poison_toad
            enemy.name.contains("Swamp Horror", ignoreCase = true) || enemy.name.contains("Bog-Behemoth", ignoreCase = true) -> R.drawable.swamp_horror
            enemy.name.contains("Dread Mosquito", ignoreCase = true) || enemy.name.contains("Rot-Gnat", ignoreCase = true) -> R.drawable.dread_mosquito
            enemy.name.contains("Leech", ignoreCase = true) || enemy.name.contains("Prismatic Leech", ignoreCase = true) -> R.drawable.leech
            enemy.name.contains("Will o the wisp", ignoreCase = true) || enemy.name.contains("Gloom-Wisp", ignoreCase = true) -> R.drawable.will_o_the_wisp
            enemy.name.contains("Dark Spirit", ignoreCase = true) -> R.drawable.dark_spirit
            enemy.name.contains("Slime", ignoreCase = true) -> R.drawable.slime_001
            enemy.name.contains("Shade", ignoreCase = true) -> R.drawable.shade
            enemy.name.contains("Spirit", ignoreCase = true) || enemy.name.contains("Wisp", ignoreCase = true) || enemy.name.contains("Nova Sprite", ignoreCase = true) -> R.drawable.fire_spirit
            enemy.name.contains("Assassin", ignoreCase = true) || enemy.name.contains("Rogue", ignoreCase = true) || enemy.name.contains("Brawler", ignoreCase = true) || enemy.name.contains("Monk", ignoreCase = true) || enemy.name.contains("Samurai", ignoreCase = true) || enemy.name.contains("Archer", ignoreCase = true) -> R.drawable.orc_warrior
            
            enemy.isHumanoid -> R.drawable.battle_xious
            enemy.name.contains("Stone Titan", ignoreCase = true) || enemy.name.contains("The Iron Titan", ignoreCase = true) -> R.drawable.stone_titan
            enemy.name.contains("Stone Golem", ignoreCase = true) || enemy.name.contains("Quartz Golem", ignoreCase = true) -> R.drawable.stone_golem
            enemy.name.contains("Crag Golem", ignoreCase = true) -> R.drawable.crag_golem
            enemy.name.contains("Rock Slide Golem", ignoreCase = true) -> R.drawable.rock_slide_golem
            enemy.name.contains("Golem", ignoreCase = true) || enemy.name.contains("Armor", ignoreCase = true) || enemy.name.contains("Gargoyle", ignoreCase = true) || enemy.name.contains("Guardian", ignoreCase = true) || enemy.name.contains("Titan", ignoreCase = true) || enemy.name.contains("Void", ignoreCase = true) || enemy.name.contains("Lord", ignoreCase = true) || enemy.name.contains("Xarthos", ignoreCase = true) -> R.drawable.dark_knight

            enemy.name.contains("Fire Drake", ignoreCase = true) || enemy.name.contains("Ember-Drake", ignoreCase = true) -> R.drawable.fire_drake
            enemy.name.contains("Peak Wyvern", ignoreCase = true) -> R.drawable.peak_wyvern
            enemy.name.contains("Lava Serpent", ignoreCase = true) || enemy.name.contains("Sapphire Serpent", ignoreCase = true) -> R.drawable.lava_serpent
            enemy.name.contains("Volcanic Turtle", ignoreCase = true) -> R.drawable.volcanic_turtle
            enemy.name.contains("Cinder Elemental", ignoreCase = true) || enemy.name.contains("Darkness-Elemental", ignoreCase = true) -> R.drawable.cinder_elemental
            enemy.name.contains("Ash Griffin", ignoreCase = true) || enemy.name.contains("Sky-Render Griffin", ignoreCase = true) -> R.drawable.ash_griffin
            
            enemy.name.contains("Void Hound", ignoreCase = true) -> R.drawable.void_hound
            enemy.name.contains("Void Reaper", ignoreCase = true) -> R.drawable.void_reaper
            enemy.name.contains("Cursed Armor", ignoreCase = true) -> R.drawable.cursed_armor
            enemy.name.contains("Citadel Guard", ignoreCase = true) -> R.drawable.citadel_guard
            enemy.name.contains("Shadow Knight", ignoreCase = true) -> R.drawable.shadow_knight_mob
            enemy.name.contains("Wicked Gargoyle", ignoreCase = true) -> R.drawable.wicked_gargoyle

            enemy.name.contains("Dragon", ignoreCase = true) || enemy.name.contains("Wyvern", ignoreCase = true) || enemy.name.contains("Drake", ignoreCase = true) -> R.drawable.world_eater_phase_final
            else -> R.drawable.lumeria_logo
        }
    }

    fun getFamiliarLogoId(name: String): Int {
        return when (name) {
            "Baby Dragon" -> R.drawable.world_eater_phase_final
            "Healing Wisp" -> R.drawable.fire_spirit
            "Mana Sprite" -> R.drawable.lumeria_icon
            "Shadow Bat" -> R.drawable.small_spider_001
            "Phoenix Chick" -> R.drawable.fire_spirit
            "Celestial Owl" -> R.drawable.vil_elder
            else -> 0
        }
    }

    private fun checkBattleEnd(onUpdate: (PlayerData) -> Unit) {
        val currentState = _state.value
        if (currentState.playerHp <= 0) {
            _state.update { it.copy(isDying = true) }
            onHaptic?.invoke(HapticType.DEATH)
            return
        }

        if ((currentState.enemyHp <= 0) && (currentState.enemy2Hp <= 0) && (!currentState.victoryProcessed)) {
            val lootBonus = if (isRiftBattle) 5 else 0
            val materialChanceBonus = if (playerData.activeKingdomLawId == 4) 30 else 0
            
            val drops = mutableListOf<String>()
            
            // ROLL LOOT FOR ENEMY 1
            if (isBossBattle) {
                drops.addAll(BossLootDatabase.rollLoot(currentState.enemy.baseName, lootBonus))
                
                // --- MYTHIC FAMILIAR DROPS (0.01% Chance) ---
                val familiarDrop = when (currentState.enemy.baseName) {
                    "Marsh Horror" -> "Mire Hydra"
                    "Ancient Dragon" -> "Magma Drake"
                    "Lord Xarthos" -> "Void Eye"
                    else -> null
                }
                if ((familiarDrop != null) && (Math.random() < 0.0001)) {
                    drops.add(familiarDrop)
                }
            } else if (isRiftBattle) {
                if (Math.random() < 0.25) {
                    drops.addAll(BossLootDatabase.rollRiftLoot())
                }
            }

            // --- MATERIAL DROPS ---
            currentState.enemy.materialDrop?.let { material ->
                drops.add(material) // 100% drop
                if (Math.random() * 100 < materialChanceBonus) {
                    drops.add(material)
                }
            }

            // ROLL LOOT FOR ENEMY 2
            currentState.enemy2?.let { e2 ->
                e2.materialDrop?.let { material ->
                    drops.add(material)
                    if (Math.random() * 100 < materialChanceBonus) {
                        drops.add(material)
                    }
                }
            }
            
            val updatedPlayerStep1 = BattleLogic.calculateVictory(
                player = currentState.currentPlayerSnapshot, 
                enemy = currentState.enemy, 
                isBossBattle = isBossBattle,
                lootDrops = if (isStoryReplay) emptyList() else drops,
                isStoryMode = storyEnemyName != null,
                arenaOpponent = arenaOpponent,
                activeBounty = activeBounty,
                isTower = isTowerBattle,
                isRift = isRiftBattle,
                isReplay = isStoryReplay,
                currentHp = currentState.playerHp,
                currentMana = currentState.playerMana,
                currentEventIndex = currentEventIndex,
                currentGuildExam = guildExam,
            )

            // IF ENEMY 2 EXISTED, SYNC IT TOO
            val updatedPlayer = if (currentState.enemy2 != null) {
                BattleLogic.calculateVictory(
                    player = updatedPlayerStep1,
                    enemy = currentState.enemy2,
                    isBossBattle = false,
                    lootDrops = emptyList(), // Loot already handled above for both
                    isStoryMode = false,
                    arenaOpponent = null,
                    activeBounty = null,
                    isTower = false,
                    isRift = false, // We treat 2nd enemy as normal reward
                    isReplay = isStoryReplay,
                    currentHp = updatedPlayerStep1.hp,
                    currentMana = updatedPlayerStep1.mana,
                    currentEventIndex = updatedPlayerStep1.currentStoryEventIndex,
                    currentGuildExam = null, // Only one exam at a time
                )
            } else updatedPlayerStep1
            
            // Consume buffs atomically with victory
            val finalUpdatedPlayer = updatedPlayer.consumeBattleBuffs()

            val (baseXp1, baseGold1) = BattleLogic.calculateRewards(
                player = currentState.currentPlayerSnapshot, 
                enemy = currentState.enemy, 
                isBossBattle = isBossBattle, 
                isStoryMode = storyEnemyName != null,
                arenaOpponent = arenaOpponent,
                bounty = activeBounty,
                isTower = isTowerBattle,
                isRift = isRiftBattle,
                isReplay = isStoryReplay,
            )

            var finalXp = baseXp1
            var finalGold = baseGold1

            // REWARDS FOR ENEMY 2
            currentState.enemy2?.let { e2 ->
                val (baseXp2, baseGold2) = BattleLogic.calculateRewards(
                    player = currentState.currentPlayerSnapshot,
                    enemy = e2,
                    isBossBattle = false,
                    isStoryMode = false,
                    arenaOpponent = null,
                    bounty = null,
                    isTower = false,
                    isRift = false,
                    isReplay = isStoryReplay,
                )
                finalXp += baseXp2
                finalGold += baseGold2
            }

            _state.update { it.copy(
                victoryProcessed = true,
                victoryLoot = drops,
                xpGained = finalXp,
                goldGained = finalGold,
                currentPlayerSnapshot = finalUpdatedPlayer,
                battleLog = (it.battleLog + if (finalUpdatedPlayer.equippedFamiliar != "None") {
                    val oldLvl = currentState.currentPlayerSnapshot.familiarLevels[finalUpdatedPlayer.equippedFamiliar] ?: 1
                    val newLvl = finalUpdatedPlayer.familiarLevels[finalUpdatedPlayer.equippedFamiliar] ?: 1
                    if (newLvl > oldLvl) {
                        listOf(LogEntry("🐾 ${finalUpdatedPlayer.equippedFamiliar} LEVELED UP to Lv $newLvl!", Color.Magenta))
                    } else {
                        emptyList()
                    }
                } else {
                    emptyList()
                }).takeLast(20)
            ) }

            onUpdate(finalUpdatedPlayer)
        }
    }

    fun useItem(itemName: String, onUpdate: (PlayerData) -> Unit) {
        val battleState = _state.value
        val currentData = battleState.currentPlayerSnapshot
        val newItemList = currentData.inventory.toMutableList()
        
        // 1. Verify item ownership
        if (!newItemList.remove(itemName)) {
            updateLogs(listOf(LogEntry("You don't have $itemName!", Color.Red)))
            return
        }

        // 2. Validate against database
        val dbConsumable = GameDatabase.getConsumable(itemName)
        if (dbConsumable == null) {
            updateLogs(listOf(LogEntry("$itemName cannot be used here.", Color.Gray)))
            return
        }

        val healAmount = dbConsumable.healAmount
        val manaAmount = dbConsumable.manaAmount
        
        val newPlayer = currentData.copy(
            inventory = newItemList,
            hp = minOf(currentData.maxHp, battleState.playerHp + healAmount),
            mana = minOf(currentData.maxMana, battleState.playerMana + manaAmount),
        )
        
        _state.update { it.copy(
            playerHp = newPlayer.hp,
            playerMana = newPlayer.mana,
            currentPlayerSnapshot = newPlayer,
        ) }
        
        updateLogs(listOf(LogEntry("Used $itemName!", Color.Green)))
        onUpdate(newPlayer)
    }

    companion object {
        fun provideFactory(
            playerSnapshot: PlayerData,
            isBoss: Boolean = false,
            storyEnemyName: String? = null,
            arenaOpponent: ArenaOpponent? = null,
            activeBounty: com.solerforge.lumeria.data.Bounty? = null,
            isTower: Boolean = false,
            isRift: Boolean = false,
            isPvP: Boolean = false,
            isStoryReplay: Boolean = false,
            shadowOpponent: PlayerData? = null,
            guildExam: GuildDatabase.GuildExam? = null,
            currentEventIndex: Int = 0,
            locationName: String? = null,
            onHaptic: ((HapticType) -> Unit)? = null,
        ): androidx.lifecycle.ViewModelProvider.Factory = object : androidx.lifecycle.ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                return BattleViewModel(
                    playerData = playerSnapshot,
                    isBossBattle = isBoss,
                    storyEnemyName = storyEnemyName,
                    arenaOpponent = arenaOpponent,
                    activeBounty = activeBounty,
                    isTowerBattle = isTower,
                    isRiftBattle = isRift,
                    isPvPBattle = isPvP,
                    isStoryReplay = isStoryReplay,
                    shadowOpponent = shadowOpponent,
                    guildExam = guildExam,
                    currentEventIndex = currentEventIndex,
                    locationOverride = locationName,
                    onHaptic = onHaptic
                ) as T
            }
        }
    }
}
