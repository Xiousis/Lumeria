package com.solerforge.lumeria.database

import com.solerforge.lumeria.data.Enemy

object StoryEnemyDatabase {

    private val enemies = mapOf(
        "Training Bandit" to Enemy("Training Bandit", "Training Bandit", 150, 2, rewardXp = 80, rewardGold = 100),
        "Village Troublemaker" to Enemy("Village Troublemaker", "Village Troublemaker", 250, 3, rewardXp = 120, rewardGold = 150),
        "Goblin Scout" to Enemy("Goblin Scout", "Goblin Scout", 500, 7, rewardXp = 250, rewardGold = 250),
        "Goblin Marauder" to Enemy("Goblin Marauder", "Goblin Marauder", 1200, 12, rewardXp = 500, rewardGold = 400),
        "Cave Stalker" to Enemy("Cave Stalker", "Cave Stalker", 2500, 18, rewardXp = 1000, rewardGold = 800),
        "Undead Squire" to Enemy("Undead Squire", "Undead Squire", 4500, 24, rewardXp = 2000, rewardGold = 1200),
        "Mountain Giant" to Enemy("Mountain Giant", "Mountain Giant", 8000, 30, rewardXp = 4000, rewardGold = 2000),
        "Marsh Dweller" to Enemy("Marsh Dweller", "Marsh Dweller", 15000, 40, rewardXp = 8000, rewardGold = 4000),
        "Fire Elemental" to Enemy("Fire Elemental", "Fire Elemental", 35000, 55, rewardXp = 15000, rewardGold = 8000),
        "Steam Sentry" to Enemy("Steam Sentry", "Steam Sentry", 50000, 62, rewardXp = 25000, rewardGold = 12000),
        "Gear Golem" to Enemy("Gear Golem", "Gear Golem", 80000, 70, rewardXp = 40000, rewardGold = 20000),
        "Elite Orc" to Enemy("Elite Orc", "Elite Orc", 120000, 78, rewardXp = 60000, rewardGold = 30000),
        "Spectral Knight" to Enemy("Spectral Knight", "Spectral Knight", 180000, 85, rewardXp = 100000, rewardGold = 50000),
        "Wyvern Scout" to Enemy("Wyvern Scout", "Wyvern Scout", 250000, 90, rewardXp = 150000, rewardGold = 75000),
        "Void Cultist" to Enemy("Void Cultist", "Void Cultist", 350000, 95, rewardXp = 250000, rewardGold = 100000),
        "Chrono Stalker" to Enemy("Chrono Stalker", "Chrono Stalker", 500000, 100, rewardXp = 400000, rewardGold = 150000),
        "Shadow Assassin" to Enemy("Shadow Assassin", "Shadow Assassin", 700000, 105, rewardXp = 600000, rewardGold = 200000),
        "Golden Construct" to Enemy("Golden Construct", "Golden Construct", 1000000, 110, rewardXp = 800000, rewardGold = 300000),
        "Inquisition Guard" to Enemy("Inquisition Guard", "Inquisition Guard", 1300000, 115, rewardXp = 1000000, rewardGold = 400000),
        "Rift Spawn" to Enemy("Rift Spawn", "Rift Spawn", 1800000, 120, rewardXp = 1500000, rewardGold = 500000),
        "Void Horror" to Enemy("Void Horror", "Void Horror", 2500000, 125, rewardXp = 2000000, rewardGold = 750000),
        // SIDE STORY ENEMIES
        "Tribal Scout" to Enemy("Tribal Scout", "Tribal Scout", 1000, 10, rewardXp = 500, rewardGold = 500),
        "Tribal Warrior" to Enemy("Tribal Warrior", "Tribal Warrior", 3500, 20, rewardXp = 1500, rewardGold = 1000),
        "Ashen Spirit" to Enemy("Ashen Spirit", "Ashen Spirit", 20000, 45, rewardXp = 10000, rewardGold = 5000)
    )

    fun getEnemy(name: String): Enemy {
        return enemies[name] ?: Enemy(name, name, 100, 1, rewardXp = 25, rewardGold = 15)
    }

    fun resolveEnemy(name: String, location: com.solerforge.lumeria.data.WorldLocation? = null): Enemy {
        enemies[name]?.let { return it }
        
        // Dynamic fallback for common world enemies if seen in story
        val level = location?.let { (it.minLevel..it.maxLevel).random() } ?: 1
        val hp = (level * 25) + 30
        val isHuman = name.contains("Goblin", ignoreCase = true) || name.contains("Orc", ignoreCase = true) || 
                      name.contains("Knight", ignoreCase = true) || name.contains("Guard", ignoreCase = true)
                      
        return Enemy(
            name = name,
            baseName = name,
            maxHp = hp,
            level = level,
            rewardXp = (level.toLong() * level * 2) + 50,
            rewardGold = (hp * 0.5).toLong(),
            isHumanoid = isHuman,
            materialDrop = location?.let { GameDatabase.getRequiredMaterial(it.name) }
        )
    }
}
