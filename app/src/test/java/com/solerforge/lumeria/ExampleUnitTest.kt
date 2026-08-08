package com.solerforge.lumeria

import com.solerforge.lumeria.data.PlayerData
import org.junit.Test
import org.junit.Assert.*

/**
 * Basic game rules unit tests.
 */
class GameRulesUnitTest {
    @Test
    fun testLevelCap() {
        val normalPlayer = PlayerData(level = 10, isReborn = false)
        assertEquals(100, normalPlayer.getLevelCap())
        
        val rebornPlayer = PlayerData(level = 10, isReborn = true)
        assertEquals(200, rebornPlayer.getLevelCap())
    }

    @Test
    fun testInitialStats() {
        val player = PlayerData()
        assertEquals(1, player.level)
        assertEquals(0, player.xp)
        assertEquals(0, player.gold)
        assertEquals("Novice", player.currentTitle)
    }

    @Test
    fun testMaxHpCalculation() {
        val player = PlayerData(level = 1, vitality = 5)
        // Base 30 + (1-1)*10 + 5*10 = 80
        assertEquals(80, player.calculateMaxHp())
        
        val leveledPlayer = player.copy(level = 10, vitality = 10)
        // Base 30 + (10-1)*10 + 10*10 = 30 + 90 + 100 = 220
        assertEquals(220, leveledPlayer.calculateMaxHp())
    }
}
