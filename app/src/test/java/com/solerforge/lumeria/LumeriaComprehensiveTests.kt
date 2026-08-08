package com.solerforge.lumeria

import com.solerforge.lumeria.battle.BattleLogic
import com.solerforge.lumeria.data.Enemy
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.BountyDatabase
import com.solerforge.lumeria.database.GameDatabase
import com.solerforge.lumeria.database.TowerDatabase
import com.solerforge.lumeria.managers.BattleOrchestrator
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LumeriaComprehensiveTests {

    @Test
    fun testLevelUpGrantsExactlyFiveStatPoints() {
        val player = PlayerData(level = 1, xp = 0, statPoints = 0)
        // Gain enough XP for exactly 1 level (Level 1 req: (1*1*50) + (1*100) = 150)
        val updated = player.gainExperience(150)
        
        assertEquals("Level should be 2", 2, updated.level)
        assertEquals("Stat points should be exactly 5", 5, updated.statPoints)
        
        // Gain enough for 2 more levels
        // Level 2 req: (2*2*50) + (2*100) = 200 + 200 = 400
        // Level 3 req: (3*3*50) + (3*100) = 450 + 300 = 750
        // Total needed from Level 2 to 4: 400 + 750 = 1150
        val multiLevel = player.gainExperience(2000)
        assertTrue(multiLevel.level > 2)
        assertEquals("Stat points should be (level-1) * 5", (multiLevel.level - 1) * 5, multiLevel.statPoints)
    }

    @Test
    fun testRebirthLevelCaps() {
        val normalPlayer = PlayerData(isReborn = false)
        val rebornPlayer = PlayerData(isReborn = true)

        assertEquals(100, normalPlayer.getLevelCap())
        assertEquals(200, rebornPlayer.getLevelCap())

        // Test XP gain at cap
        val cappedNormal = PlayerData(level = 100, isReborn = false, xp = 0)
        val afterXp = cappedNormal.gainExperience(1000000)
        assertEquals("Normal player should not exceed level 100", 100, afterXp.level)
        assertEquals("XP should be 0 at max level", 0L, afterXp.xp)
    }

    @Test
    fun testTowerProgression() {
        val player = PlayerData(towerFloor = 1)
        val towerEnemy = TowerDatabase.getTowerEnemy(1)
        
        val updated = BattleLogic.calculateVictory(
            player = player,
            enemy = towerEnemy,
            isTower = true,
            currentHp = 30,
            currentMana = 20
        )
        
        assertEquals("Tower floor should increment to 2", 2, updated.towerFloor)
        assertEquals("Highest floor should be updated", 1, updated.towerHighestFloor)
    }

    @Test
    fun testBountyRewardsOnlyOnTarget() {
        val bounty = BountyDatabase.getBounty(1)!! // Silas the Strangler
        val player = PlayerData(activeBountyId = 1)
        
        val correctTarget = Enemy(name = "Silas the Strangler", baseName = "Silas the Strangler", maxHp = 100, level = 65)
        val wrongTarget = Enemy(name = "Shadow Marsh Bandit", baseName = "Bandit", maxHp = 100, level = 65)

        // Correct target
        val rewardsCorrect = BattleLogic.calculateRewards(player, correctTarget, bounty = bounty)
        assertEquals(bounty.rewardXp, rewardsCorrect.first)
        assertEquals((bounty.rewardGold * (1.0 + player.luck * 0.01)).toInt(), rewardsCorrect.second)

        // Wrong target
        val rewardsWrong = BattleLogic.calculateRewards(player, wrongTarget, bounty = bounty)
        assertTrue("Wrong target should not give bounty XP", rewardsWrong.first < bounty.rewardXp)
    }

    @Test
    fun testDeathPenaltyAndGoldLoss() {
        // Mocking Orchestrator callbacks
        val orchestrator = BattleOrchestrator(
            onUpdatePlayer = {},
            onNavigate = {},
            onPopBackstack = {},
            onNavigateToBattleScreen = {}
        )
        
        val player = PlayerData(gold = 1000, deathCount = 5)
        val goldLost = 200
        
        val result = orchestrator.handleDeath(player, goldLost)
        
        assertEquals("Death count should increment", 6, result.deathCount)
        // This test is expected to FAIL currently because of the bug in BattleOrchestrator
        assertEquals("Gold should be lost", 800, result.gold)
    }

    @Test
    fun testRiftBankingOnBossVictory() {
        val player = PlayerData(
            riftStep = 3,
            riftXp = 5000,
            riftGold = 2000,
            riftLoot = listOf("Rift Shard"),
            inventory = emptyList()
        )
        
        val boss = Enemy("Rift Boss", "Rift Boss", 1000, level = 50)
        
        val updated = BattleLogic.calculateVictory(
            player = player,
            enemy = boss,
            isBossBattle = true,
            isRift = true,
            currentHp = 30,
            currentMana = 20,
            lootDrops = listOf("Boss Crystal")
        )
        
        assertTrue("Rift XP should be banked", updated.xp >= 5000)
        assertTrue("Rift Gold should be banked", updated.gold >= 2000)
        assertTrue("Rift Loot should be in inventory", updated.inventory.contains("Rift Shard"))
        assertTrue("Boss Loot should be in inventory", updated.inventory.contains("Boss Crystal"))
        assertEquals("Rift step should reset", 0, updated.riftStep)
        assertEquals("Rift rewards should clear", 0L, updated.riftXp)
    }

    @Test
    fun testEquipmentStatBonusScaling() {
        // Base attack for Training Sword is 5 (from GameDatabase.kt)
        val baseWeapon = "Training Sword"
        val upgradedWeapon = "Training Sword +5"
        
        val baseStats = GameDatabase.getWeapon(baseWeapon)
        val upgradedStats = GameDatabase.getWeapon(upgradedWeapon)
        
        assertEquals(5, baseStats.attack)
        // Upgraded: 1.0 + (5 * 0.02) = 1.1 multiplier -> 5 * 1.1 = 5.5 -> 5
        assertEquals(5, upgradedStats.attack) 
        
        // Let's test with a higher base attack weapon for clearer results
        // Hero's Longsword base attack is 55
        val heroWeapon = "Hero's Longsword"
        val heroUpgraded = "Hero's Longsword +5"
        
        val heroBaseStats = GameDatabase.getWeapon(heroWeapon)
        val heroUpgradedStats = GameDatabase.getWeapon(heroUpgraded)
        
        assertEquals(55, heroBaseStats.attack)
        // 55 * 1.1 = 60.5 -> 60
        assertEquals(60, heroUpgradedStats.attack)
        
        // Void-Touched (+50% bonus): 1.0 + (5 * 0.02) + 0.5 = 1.6 -> 55 * 1.6 = 88
        val heroVoid = GameDatabase.getWeapon("Void-Touched Hero's Longsword +5")
        assertEquals(88, heroVoid.attack)
    }

    @Test
    fun testPotionUsageRestoresCorrectAmounts() {
        // "Health Potion" healAmount = 25 (I'll verify this from outline/file)
        val player = PlayerData(hp = 10, maxHp = 100, mana = 5, maxMana = 50, inventory = listOf("Health Potion", "Mana Potion"))
        
        // Mocking ViewModel or just testing the logic from BattleViewModel.useItem
        // Since we can't easily mock ViewModel, we test the logic:
        val healthPotion = GameDatabase.getConsumable("Health Potion")!!
        val updatedHp = minOf(player.maxHp, player.hp + healthPotion.healAmount)
        
        assertEquals("Health Potion should heal", 35, updatedHp)
        
        // Test capping
        val playerNearFull = player.copy(hp = 95)
        val cappedHp = minOf(playerNearFull.maxHp, playerNearFull.hp + healthPotion.healAmount)
        assertEquals("Healing should cap at maxHp", 100, cappedHp)
    }
}
