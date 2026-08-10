package com.solerforge.lumeria.database

import com.solerforge.lumeria.data.WorldLocation

object MonsterWorldDatabase {
    val locations = listOf(
        WorldLocation(
            name = "The Slums",
            description = "The dark underbelly of the human capital.",
            levelRange = "Lv 1-20",
            minLevel = 1,
            maxLevel = 20,
            enemies = listOf("Scared Villager", "Drunkard", "City Guard Trainee", "Street Urchin", "Rat Catcher", "Shadow Thief", "Corrupt Guard", "Thug"),
            boss = "Sergeant Miller",
            bossUnlocked = true,
            x = 0.5f,
            y = 0.85f,
            requiredLevel = 1,
            isLegacyOnly = true
        ),
        WorldLocation(
            name = "Market District",
            description = "The heart of the kingdom's trade.",
            levelRange = "Lv 21-40",
            minLevel = 21,
            maxLevel = 40,
            enemies = listOf("Ironclad Guard", "Merchant Guard", "Wealthy Noble", "Caravan Escort", "Mercenary", "Hired Blade", "Watchman", "Tax Collector"),
            boss = "Captain Harlen",
            bossUnlocked = true,
            x = 0.2f,
            y = 0.7f,
            requiredLevel = 21,
            isLegacyOnly = true
        ),
        WorldLocation(
            name = "Royal Gardens",
            description = "A peaceful place of beauty, soon to be burned.",
            levelRange = "Lv 41-60",
            minLevel = 41,
            maxLevel = 60,
            enemies = listOf("Royal Sword-Master", "Garden Guard", "Court Duelist", "Noble Cavalier", "Grand Gardener", "Floral Mage", "Petal Dancer", "Swan Knight"),
            boss = "Lady Elara",
            bossUnlocked = true,
            x = 0.8f,
            y = 0.6f,
            requiredLevel = 41,
            isLegacyOnly = true
        ),
        WorldLocation(
            name = "Temple District",
            description = "The center of religious power in Lumeria.",
            levelRange = "Lv 61-80",
            minLevel = 61,
            maxLevel = 80,
            enemies = listOf("Templar Knight", "High Priest", "Holy Inquisitor", "Light Weaver", "Exorcist", "Sanctified Monk", "Zealot", "Righteous Crusader"),
            boss = "Grand Inquisitor",
            bossUnlocked = true,
            x = 0.7f,
            y = 0.4f,
            requiredLevel = 61,
            isLegacyOnly = true
        ),
        WorldLocation(
            name = "The Royal Court",
            description = "Where the elite defenders protect the King.",
            levelRange = "Lv 81-100",
            minLevel = 81,
            maxLevel = 100,
            enemies = listOf("Elite Paladin", "King's Guard", "Royal Archer", "Court Wizard", "Master of Arms", "Iron Captain", "Gilded Knight", "Imperial Sentinel"),
            boss = "General Ironheart",
            bossUnlocked = true,
            x = 0.3f,
            y = 0.3f,
            requiredLevel = 81,
            isLegacyOnly = true
        ),
        WorldLocation(
            name = "King's Bedroom",
            description = "The ultimate refuge of the King of Lumeria.",
            levelRange = "Lv 101-120",
            minLevel = 101,
            maxLevel = 120,
            enemies = listOf("Royal Guard Captain", "Secret Assassin", "King's Champion", "Shadow Shield", "Personal Bodyguard", "Last Defender", "Imperial Guard", "Kingslayer"),
            boss = "King Lumeria",
            bossUnlocked = true,
            x = 0.1f,
            y = 0.2f,
            requiredLevel = 101,
            isLegacyOnly = true
        ),
        WorldLocation(
            name = "Secret Treasury",
            description = "Where the kingdom's greatest riches and artifacts are kept.",
            levelRange = "Lv 121-140",
            minLevel = 121,
            maxLevel = 140,
            enemies = listOf("Vault Guardian", "Gilded Golem", "Treasure Hunter", "Royal Collector", "Artifact Sentinel", "Golden Construct", "Wealthy Rogue", "Imperial Banker"),
            boss = "Master of Riches",
            bossUnlocked = true,
            x = 0.9f,
            y = 0.1f,
            requiredLevel = 121,
            isLegacyOnly = true
        ),
        WorldLocation(
            name = "Hero's Rest",
            description = "A sacred ground where legendary heroes are buried.",
            levelRange = "Lv 141-155",
            minLevel = 141,
            maxLevel = 155,
            enemies = listOf("Legendary Spirit", "Eternal Hero", "Ancestral Warrior", "Spectral Defender", "Heroic Ghost", "Valor Weaver", "Grand Sentinel", "Chosen One"),
            boss = "The First Hero",
            bossUnlocked = true,
            x = 0.5f,
            y = 0.05f,
            requiredLevel = 141,
            isLegacyOnly = true
        ),
        WorldLocation(
            name = "Hall of Valor",
            description = "The place where new legends are born.",
            levelRange = "Lv 156-180",
            minLevel = 156,
            maxLevel = 180,
            enemies = listOf("Aspiring Hero", "Valor Initiate", "Champion Trainee", "Battle Master", "War College Student", "Imperial Knight", "Royal Crusader", "Defender of Light"),
            boss = "The Grand Master",
            bossUnlocked = true,
            x = 0.2f,
            y = 0.5f,
            requiredLevel = 156,
            isLegacyOnly = true
        ),
        WorldLocation(
            name = "Inner Sanctum",
            description = "The core of the Hero Kingdom's power.",
            levelRange = "Lv 181-200",
            minLevel = 181,
            maxLevel = 200,
            enemies = listOf("Sanctum Guard", "Inner Circle Wizard", "Divine Champion", "Holy Arbiter", "Light Sovereign", "Eternal Sentinel", "Reality Guard", "Nexus Defender"),
            boss = "The Hero King",
            bossUnlocked = true,
            x = 0.5f,
            y = 0.0f,
            requiredLevel = 181,
            isLegacyOnly = true
        )
    )

    fun getLocation(name: String): WorldLocation {
        return locations.find { it.name == name }
            ?: error("Unknown monster location: $name")
    }
}
