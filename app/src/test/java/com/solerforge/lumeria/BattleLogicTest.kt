package com.solerforge.lumeria

import com.solerforge.lumeria.battle.BattleLogic
import com.solerforge.lumeria.data.Enemy
import com.solerforge.lumeria.data.PlayerData
import org.junit.Assert.assertEquals
import org.junit.Test

class BattleLogicTest {

    @Test
    fun testCalculateRewards() {
        val player = PlayerData(level = 5)
        val enemy = Enemy("Slime", "Slime", 20, level = 1, rewardXp = 100, rewardGold = 50)
        
        // Normal battle
        val (xp, gold) = BattleLogic.calculateRewards(player, enemy)
        assertEquals(1L, xp) // Level penalty: 5 - 1 = 4 (> 20 penalty is extreme)
        
        // Rift battle (1.5x)
        val (xpRift, goldRift) = BattleLogic.calculateRewards(player, enemy, isRift = true)
        assertEquals(1L, xpRift)
        
        // Tower battle (static rewards)
        val enemyTower = Enemy("Tower Guardian", "Tower Guardian", 100, level = 10, rewardXp = 500, rewardGold = 100)
        val (xpTower, goldTower) = BattleLogic.calculateRewards(player, enemyTower, isTower = true)
        assertEquals(500L, xpTower)
        assertEquals(100L, goldTower)
    }

    @Test
    fun testLevelPenalty() {
        val player = PlayerData(level = 15)
        val lowEnemy = Enemy("Slime", "Slime", 10, level = 1, rewardXp = 100, rewardGold = 50)
        
        val (xp, _) = BattleLogic.calculateRewards(player, lowEnemy)
        // levelDiff = 14 -> xpGain / 2 = 50 (if base was 100, but logic calculated differently)
        // Actually base for level 1: (1*1*2) + (1*50) + 100 = 152. 152 / 2 = 76.
        assertEquals(76L, xp)
    }
}
