package com.example.lumeria.battle

import androidx.compose.ui.graphics.Color
import com.example.lumeria.data.Enemy
import com.example.lumeria.data.PlayerData

data class FloatingNumber(
    val id: Long = java.util.UUID.randomUUID().mostSignificantBits,
    val text: String,
    val color: Color,
    val isCrit: Boolean = false,
    val initialX: Float,
    val initialY: Float,
    val createdAt: Long = System.currentTimeMillis()
)

data class BattleUiState(
    val enemy: Enemy,
    val enemyLogoId: Int,
    val enemyHp: Int,
    val enemy2: Enemy? = null,
    val enemy2LogoId: Int = 0,
    val enemy2Hp: Int = 0,
    val playerHp: Int,
    val playerMana: Int,
    val isDying: Boolean = false,
    val victoryProcessed: Boolean = false,
    val victoryLoot: List<String> = emptyList(),
    val xpGained: Int = 0,
    val goldGained: Int = 0,
    val currentPlayerSnapshot: PlayerData,
    val battleLog: List<LogEntry> = emptyList(),
    val battleMessage: String = "",
    
    // Animation States
    val playerOffsetX: Float = 0f,
    val enemyOffsetX: Float = 0f,
    val enemy2OffsetX: Float = 0f,
    val floatingNumbers: List<FloatingNumber> = emptyList(),
    val playerFloatingText: String = "",
    val playerFloatingColor: Color = Color.Red,
    val playerFloatingY: Float = 0f,
    val enemyFloatingText: String = "",
    val enemyFloatingColor: Color = Color.Red,
    val enemyFloatingY: Float = 0f,
    val enemy2FloatingText: String = "",
    val enemy2FloatingColor: Color = Color.Red,
    val enemy2FloatingY: Float = 0f,
    val playerFlashAlpha: Float = 0f,
    val enemyFlashAlpha: Float = 0f,
    val enemy2FlashAlpha: Float = 0f,
    
    // Status Effects
    val playerStunnedTurns: Int = 0,
    val playerDefenseDebuffTurns: Int = 0,
    val bossShieldTurns: Int = 0,
    val bossEnrageTurns: Int = 0,
    val enemyStunnedTurns: Int = 0,
    val enemy2StunnedTurns: Int = 0,
    val burnTurns: Int = 0,
    val enemy2BurnTurns: Int = 0,
    val poisonTurns: Int = 0,
    val enemy2PoisonTurns: Int = 0,
    val bleedTurns: Int = 0,
    val enemy2BleedTurns: Int = 0,
    
    // Familiar State
    val familiarActionCounter: Int = 0,
    
    // Player Buffs/State
    val skillCooldowns: Map<String, Int> = emptyMap(),
    val lastSkillName: String? = null,
    val damageBuffMultiplier: Double = 1.0,
    val buffTurns: Int = 0,
    val critBuffTurns: Int = 0,
    val superBuffTurns: Int = 0,
    val parryActive: Boolean = false,
    val secondWindUsed: Boolean = false,
    
    // Boss AI State
    val phase2Triggered: Boolean = false,
    val phase3Triggered: Boolean = false,
    
    // Enemy Chat Tracking
    val chatThresholdsHit: Set<Float> = emptySet(),
    
    // UI Feedback
    val isProcessing: Boolean = false,
    val isFleeable: Boolean = true,
    val isStoryMode: Boolean = false,
    val isBossBattle: Boolean = false,
    val isRiftBattle: Boolean = false,
    val isArenaBattle: Boolean = false,
    val locationName: String = "Training Fields",
    val towerFloor: Int? = null,
    val arenaMoveset: List<com.example.lumeria.database.BossAttack> = emptyList(),
    val screenShake: Float = 0f,
    val playerShake: Float = 0f,
    val enemyShake: Float = 0f,
    val enemy2Shake: Float = 0f,
    val enemySkillCooldowns: Map<String, Int> = emptyMap(),
)
