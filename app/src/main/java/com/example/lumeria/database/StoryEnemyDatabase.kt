package com.example.lumeria.database

import com.example.lumeria.data.Enemy

object StoryEnemyDatabase {

    private val enemies = mapOf(
        "Training Bandit" to Enemy("Training Bandit", 150, 2, rewardXp = 80, rewardGold = 100),
        "Village Troublemaker" to Enemy("Village Troublemaker", 250, 3, rewardXp = 120, rewardGold = 150),
        "Goblin Scout" to Enemy("Goblin Scout", 500, 7, rewardXp = 250, rewardGold = 250),
        "Goblin Marauder" to Enemy("Goblin Marauder", 1200, 12, rewardXp = 500, rewardGold = 400),
        "Cave Stalker" to Enemy("Cave Stalker", 2500, 18, rewardXp = 1000, rewardGold = 800),
        "Undead Squire" to Enemy("Undead Squire", 4500, 24, rewardXp = 2000, rewardGold = 1200),
        "Mountain Giant" to Enemy("Mountain Giant", 8000, 30, rewardXp = 4000, rewardGold = 2000),
        "Marsh Dweller" to Enemy("Marsh Dweller", 15000, 40, rewardXp = 8000, rewardGold = 4000),
        "Fire Elemental" to Enemy("Fire Elemental", 35000, 55, rewardXp = 15000, rewardGold = 8000),
        "Steam Sentry" to Enemy("Steam Sentry", 50000, 62, rewardXp = 25000, rewardGold = 12000),
        "Gear Golem" to Enemy("Gear Golem", 80000, 70, rewardXp = 40000, rewardGold = 20000),
        "Elite Orc" to Enemy("Elite Orc", 120000, 78, rewardXp = 60000, rewardGold = 30000),
        "Spectral Knight" to Enemy("Spectral Knight", 180000, 85, rewardXp = 100000, rewardGold = 50000),
        "Wyvern Scout" to Enemy("Wyvern Scout", 250000, 90, rewardXp = 150000, rewardGold = 75000),
        "Void Cultist" to Enemy("Void Cultist", 350000, 95, rewardXp = 250000, rewardGold = 100000),
        "Chrono Stalker" to Enemy("Chrono Stalker", 500000, 100, rewardXp = 400000, rewardGold = 150000),
        "Shadow Assassin" to Enemy("Shadow Assassin", 700000, 105, rewardXp = 600000, rewardGold = 200000),
        "Golden Construct" to Enemy("Golden Construct", 1000000, 110, rewardXp = 800000, rewardGold = 300000),
        "Inquisition Guard" to Enemy("Inquisition Guard", 1300000, 115, rewardXp = 1000000, rewardGold = 400000),
        "Rift Spawn" to Enemy("Rift Spawn", 1800000, 120, rewardXp = 1500000, rewardGold = 500000),
        "Void Horror" to Enemy("Void Horror", 2500000, 125, rewardXp = 2000000, rewardGold = 750000),
        // SIDE STORY ENEMIES
        "Tribal Scout" to Enemy("Tribal Scout", 1000, 10, rewardXp = 500, rewardGold = 500),
        "Tribal Warrior" to Enemy("Tribal Warrior", 3500, 20, rewardXp = 1500, rewardGold = 1000),
        "Ashen Spirit" to Enemy("Ashen Spirit", 20000, 45, rewardXp = 10000, rewardGold = 5000)
    )

    fun getEnemy(name: String): Enemy {
        return enemies[name] ?: Enemy(name, 100, 1, rewardXp = 25, rewardGold = 15)
    }
}
