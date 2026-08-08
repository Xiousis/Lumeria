package com.solerforge.lumeria

import com.solerforge.lumeria.battle.BattleLogic
import com.solerforge.lumeria.data.Enemy
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.*
import com.solerforge.lumeria.managers.BattleOrchestrator
import com.solerforge.lumeria.models.BuffType
import com.solerforge.lumeria.models.PlayerBuff
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LumeriaComprehensiveTests {

    @Test
    fun testLevelUpGrantsExactlyFiveStatPoints() {
        val player = PlayerData(level = 1, xp = 0, statPoints = 0)
        // Gain enough XP for exactly 1 level (Level 1 req: 150)
        val updated = player.gainExperience(150)
        
        assertEquals("Level should be 2", 2, updated.level)
        assertEquals("Stat points should be exactly 5", 5, updated.statPoints)
        
        // Multi-level test
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
        val bounty = BountyDatabase.getBounty(1)!!
        val player = PlayerData(activeBountyId = 1)
        
        val correctTarget = Enemy(name = "Silas the Strangler", baseName = "Silas the Strangler", maxHp = 100, level = 65)
        val wrongTarget = Enemy(name = "Shadow Marsh Bandit", baseName = "Bandit", maxHp = 100, level = 65)

        // Correct target
        val rewardsCorrect = BattleLogic.calculateRewards(player, correctTarget, bounty = bounty)
        assertEquals(bounty.rewardXp, rewardsCorrect.first)
        // Gold reward includes luck bonus
        val expectedGold = (bounty.rewardGold * (1.0 + player.luck * 0.01)).toLong()
        assertEquals(expectedGold, rewardsCorrect.second)

        // Wrong target
        val rewardsWrong = BattleLogic.calculateRewards(player, wrongTarget, bounty = bounty)
        assertTrue("Wrong target should not give bounty XP", rewardsWrong.first < bounty.rewardXp)
    }

    @Test
    fun testDeathPenaltyAndGoldLoss() {
        val orchestrator = BattleOrchestrator(
            onUpdatePlayer = {},
            onNavigate = {},
            onPopBackstack = {},
            onNavigateToBattleScreen = {}
        )
        
        val player = PlayerData(gold = 1000L, deathCount = 5)
        val goldLost = 150L // 15% of 1000
        
        val result = orchestrator.handleDeath(player, goldLost)
        
        assertEquals("Death count should increment", 6, result.deathCount)
        assertEquals("Gold should be lost correctly", 850L, result.gold)
    }

    @Test
    fun testRiftBankingOnBossVictory() {
        val player = PlayerData(
            level = 1,
            xp = 0,
            gold = 0,
            riftStep = 3,
            riftXp = 5000L,
            riftGold = 2000L,
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
        
        // Calculate expected final XP manually: gainExperience(5000 + bossReward)
        val (bossXp, bossGold) = BattleLogic.calculateRewards(player, boss, isBossBattle = true, isRift = true)
        val expectedData = player.gainExperience(5000L + bossXp)
        
        assertEquals("Rift XP should be banked and level up should occur", expectedData.level, updated.level)
        assertEquals("XP should match expected after levels", expectedData.xp, updated.xp)
        assertEquals("Gold should include rift gold and boss gold", 2000L + bossGold, updated.gold)
        assertTrue("Rift Loot should be in inventory", updated.inventory.contains("Rift Shard"))
        assertTrue("Boss Loot should be in inventory", updated.inventory.contains("Boss Crystal"))
        assertEquals("Rift step should reset", 0, updated.riftStep)
    }

    @Test
    fun testKingdomNationUpgradeCost() {
        val player = PlayerData(gold = 50000L, nationUpgradeLevel = 2)
        val cost = KingdomRankDatabase.getNationUpgradeCost(2) // 50000L
        
        assertEquals(50000L, cost)
        
        val updated = player.copy(
            gold = player.gold - cost,
            nationUpgradeLevel = player.nationUpgradeLevel + 1
        )
        
        assertEquals(0L, updated.gold)
        assertEquals(3, updated.nationUpgradeLevel)
    }

    @Test
    fun testGuildXpProgression() {
        val player = PlayerData(joinedGuild = "House of Fire", guildLevel = 1, guildXp = 0)
        val enemy = Enemy("Training Dummy", "Slime", 10, level = 1)
        
        val updated = BattleLogic.calculateVictory(
            player = player,
            enemy = enemy,
            currentHp = 30,
            currentMana = 20
        )
        
        // (enemy.level * 10) + 50 = (1 * 10) + 50 = 60
        assertEquals(60L, updated.guildXp)
    }

    @Test
    fun testStoryReplayZeroRewards() {
        val arc = StoryDatabase.arcs[0]
        val player = PlayerData(completedStoryArcs = listOf(arc.id))
        val enemy = Enemy("Training Bandit", "Training Bandit", 20, level = 1)

        val rewards = BattleLogic.calculateRewards(
            player = player,
            enemy = enemy,
            isStoryMode = true,
            isReplay = true
        )
        
        assertEquals("Replay rewards should be 0 XP", 0L, rewards.first)
        assertEquals("Replay rewards should be 0 Gold", 0L, rewards.second)
    }

    @Test
    fun testWorldEventBuffApplication() {
        val player = PlayerData(activeBuffs = emptyList())
        val buff = PlayerBuff(BuffType.Damage, 1.5, 3, "Super Buff")
        
        val updated = player.copy(activeBuffs = player.activeBuffs + buff)
        
        assertEquals(1, updated.activeBuffs.size)
        assertEquals("Super Buff", updated.activeBuffs[0].description)
        assertEquals(1.5, updated.activeBuffs[0].value, 0.001)
    }

    @Test
    fun testLootGodTierDuplicatePrevention() {
        val player = PlayerData(inventory = listOf("Endbringer")) // God Tier Weapon
        val drops = listOf("Endbringer", "Iron Sword")
        
        // Calculate victory with drops
        val updated = BattleLogic.calculateVictory(
            player = player,
            enemy = Enemy("Boss", "Lord Xarthos", 1000, 80),
            lootDrops = drops,
            isBossBattle = true,
            currentHp = 30,
            currentMana = 20
        )
        
        val endbringerCount = updated.inventory.count { it == "Endbringer" }
        assertEquals("Should not have duplicate God Tier items", 1, endbringerCount)
        assertTrue("Should still gain normal items", updated.inventory.contains("Iron Sword"))
    }
}
