package com.solerforge.lumeria.database

import com.solerforge.lumeria.data.Enemy

object TowerDatabase {

    fun getTowerEnemy(floor: Int): Enemy {
        val level = 81 + ((floor - 1) * 0.5).toInt().coerceAtMost(119) // Scales from 81 to 200
        
        return if (floor % 10 == 0) {
            // BOSS FLOOR
            val bossNames = listOf(
                "Iron Golem Guard", "Shadow Weaver", "Cursed Paladin", 
                "Inferno Wraith", "Ancient Sentinel", "Void Stalker", 
                "Crystal Archmage", "Doom Herald", "Sky Piercer", "Tower Godling"
            )
            val name = bossNames.getOrElse((floor / 10) - 1) { "Tower Titan" }
            Enemy(
                name = "Elite: $name",
                baseName = name,
                maxHp = (level * 500) + 50000,
                level = level,
                rewardXp = floor * 10000,
                rewardGold = floor * 5000,
                isHumanoid = floor % 20 == 0 // Every 20th is humanoid
            )
        } else {
            // REGULAR FLOOR
            val names = listOf("Tower Phantom", "Gargoyle", "Stone Soldier", "Arcane Wisp", "Dark Knight")
            val name = names.random()
            Enemy(
                name = name,
                baseName = name,
                maxHp = (level * 150) + 10000,
                level = level,
                rewardXp = level * 100,
                rewardGold = level * 50
            )
        }
    }

    fun isTowerUnlocked(defeatedBosses: List<String>): Boolean {
        val worldBosses = listOf(
            "Training Captain", "Goblin King", "Crystal Guardian", 
            "Stone Titan", "Marsh Horror", "Ancient Dragon", "Lord Xarthos"
        )
        return worldBosses.all { it in defeatedBosses }
    }
}
