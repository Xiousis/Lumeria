package com.example.lumeria

import com.example.lumeria.battle.BattleLogic
import com.example.lumeria.data.Enemy
import com.example.lumeria.data.PlayerData
import com.example.lumeria.models.Skill
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class BattleLogicTest {

    @Test
    fun testMultiHitLogic() {
        val player = PlayerData(level = 10, agility = 20)
        val enemy = Enemy("Training Dummy", 1000, level = 1)
        val whirlwind = Skill("Whirlwind Slash", "...", 0, 0, 1, "Attack", 1.5, "MultiHit")
        
        val result = BattleLogic.processTurn(
            skill = whirlwind,
            player = player,
            currentHp = 100,
            currentMana = 100,
            currentEnemyHp = 1000,
            enemy = enemy
        )
        
        assertEquals("Whirlwind Slash should hit exactly twice", 2, result.multiHits)
    }

    @Test
    fun testLevelUpRestore() {
        val player = PlayerData(level = 1, xp = 0, hp = 5, mana = 5)
        val enemy = Enemy("Slime", 10, level = 1, rewardXp = 500) // Guaranteed level up
        
        val updatedPlayer = BattleLogic.calculateVictory(
            player = player,
            enemy = enemy,
            isBossBattle = false,
            isStoryMode = true,
            currentHp = 5,
            currentMana = 5
        )
        
        assertTrue("Should have leveled up", updatedPlayer.level > 1)
        assertEquals("HP should be fully restored on level up", updatedPlayer.maxHp, updatedPlayer.hp)
        assertEquals("Mana should be fully restored on level up", updatedPlayer.maxMana, updatedPlayer.mana)
    }

    @Test
    fun testZeroMultiplierSkill() {
        val player = PlayerData(level = 10)
        val enemy = Enemy("Dummy", 100, level = 1)
        val noneSkill = Skill("None", "...", 0, 0, 1, "Support", 0.0)
        
        val result = BattleLogic.processTurn(
            skill = noneSkill,
            player = player,
            currentHp = 100,
            currentMana = 100,
            currentEnemyHp = 100,
            enemy = enemy
        )
        
        assertEquals("Zero multiplier skill should deal zero damage", 0, result.playerDamage)
    }

    @Test
    fun testStatCalculationConsistency() {
        val player = PlayerData(level = 5, vitality = 10, intelligence = 10)
        val calculatedHp = player.calculateMaxHp()
        val calculatedMana = player.calculateMaxMana()
        
        // Base 30 + (5-1)*10 + 10*10 = 30 + 40 + 100 = 170
        assertEquals(170, calculatedHp)
        // Base 20 + (5-1)*10 + 10*5 = 20 + 40 + 50 = 110
        assertEquals(110, calculatedMana)
    }

    @Test
    fun testXpPenalty() {
        val playerHighLevel = PlayerData(level = 30)
        val enemyLowLevel = Enemy("Slime", 10, level = 1)
        
        val (xpGain, _) = BattleLogic.calculateRewards(playerHighLevel, enemyLowLevel)
        assertEquals("XP should be reduced to 1 for > 20 level difference", 1, xpGain)
        
        val playerMidLevel = PlayerData(level = 15)
        val (xpGainMid, _) = BattleLogic.calculateRewards(playerMidLevel, enemyLowLevel)
        // Base XP for level 1 = (1*1*2) + (1*50) + 100 = 152. 
        // 15 - 1 = 14 (> 10). Penalty should be half.
        assertEquals("XP should be halved for > 10 level difference", 76, xpGainMid)
    }
}
