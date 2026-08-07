package com.example.lumeria.data

import com.example.lumeria.database.GameDatabase
import com.example.lumeria.database.TraitDatabase
import kotlinx.serialization.Serializable

@Serializable
data class PlayerData(
    val level: Int = 1,
    val xp: Int = 0,
    val gold: Int = 0,
    val hp: Int = 30,
    val maxHp: Int = 30,
    val mana: Int = 20,
    val maxMana: Int = 20,
    val strength: Int = 5,
    val vitality: Int = 5,
    val defense: Int = 5,
    val intelligence: Int = 5,
    val agility: Int = 5,
    val luck: Int = 5,
    val wisdom: Int = 5,
    val statPoints: Int = 0,
    val currentLocation: String = "Training Fields",
    val unlockedLocations: List<String> = listOf("Training Fields"),
    val defeatedBosses: List<String> = emptyList(),
    val inventory: List<String> = listOf("Wooden Sword", "Cloth Armor", "Traveler Boots"),
    val equippedWeapon: String = "Wooden Sword",
    val equippedArmor: String = "Cloth Armor",
    val equippedHead: String = "None",
    val equippedBoots: String = "Traveler Boots",
    val equippedShield: String = "None",
    val equippedOffHand: String = "None",
    val equippedOffHand2: String = "None",
    val quests: List<Quest> = GameDatabase.starterQuests,
    val completedStoryArcs: List<Int> = emptyList(),
    val completedSideStoryArcs: List<Int> = emptyList(),
    val unlockedTitles: List<String> = listOf("Novice"),
    val currentTitle: String = "Novice",
    val introSeen: Boolean = false,
    val unlockedSkills: List<String> = listOf("None", "Slash", "Heavy Strike", "Magic Bolt", "Heal"),
    val equippedSkills: List<String> = listOf("Slash", "Heavy Strike", "Magic Bolt", "Heal"),
    val unlockedTraits: List<String> = emptyList(),
    val unlockedAchievements: List<String> = emptyList(),
    val freeWishesUsed: Int = 0,
    val bankGold: Int = 0,
    val activeBountyId: Int? = null,
    val completedBountyIds: List<Int> = emptyList(),
    val renown: Int = 0,
    val nationUpgradeLevel: Int = 0,
    val claimedRankIds: List<Int> = emptyList(),
    val joinedGuild: String? = null,
    val guildLevel: Int = 1,
    val guildXp: Int = 0,
    val activeBuffs: List<com.example.lumeria.models.PlayerBuff> = emptyList(),
    val towerFloor: Int = 1,
    val towerHighestFloor: Int = 0,
    val fishingLevel: Int = 1,
    val fishingXp: Int = 0,
    val equippedRod: String = "None",
    val consumedGodFishCount: Int = 0,
    val killCounts: Map<String, Int> = emptyMap(),
    val completedArenaOpponents: List<String> = emptyList(),
    val deathCount: Int = 0,
    val gamblingWins: Int = 0,
    val totalFishCaught: Int = 0,
    val blacksmithUpgrades: Int = 0,
    val worldEventsEncountered: Int = 0,
    val respecCount: Int = 0,
    val visitedBilly: Boolean = false,
    val visitedInn: Boolean = false,
    val visitedForge: Boolean = false,
    val voidIncursionLocation: String? = null,
    val riftStep: Int = 0,
    val riftGold: Int = 0,
    val riftXp: Int = 0,
    val riftLoot: List<String> = emptyList(),
    val towerUnlockedMessageSeen: Boolean = false,
    val skillExp: Map<String, Int> = emptyMap(),
    val skillLevels: Map<String, Int> = emptyMap(),
    val equippedFamiliar: String = "None",
    val familiarExp: Map<String, Int> = emptyMap(),
    val familiarLevels: Map<String, Int> = emptyMap(),
    val activeKingdomLawId: Int? = null,
    val completedTutorialSteps: List<String> = emptyList(),
    val saveVersion: Int = 1,
) {
    fun calculateMaxHp(): Int {
        val traitBonusHp = unlockedTraits.sumOf { TraitDatabase.getTrait(it)?.hpBonus ?: 0 }
        val traitBonusVit = unlockedTraits.sumOf { TraitDatabase.getTrait(it)?.vitBonus ?: 0 }
        
        val offHand1 = GameDatabase.getOffHand(equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(equippedOffHand2)
        val itemVit = offHand1.vitality + offHand2.vitality

        val base = 30 + ((level - 1) * 10) + ((vitality + traitBonusVit + itemVit) * 10) + traitBonusHp
        
        var multiplier = 1.0
        if (joinedGuild == "House of Water") multiplier += 0.02
        multiplier += (consumedGodFishCount * 0.02)
        
        return (base * multiplier).toInt()
    }

    fun calculateMaxMana(): Int {
        val traitBonusMana = unlockedTraits.sumOf { TraitDatabase.getTrait(it)?.manaBonus ?: 0 }
        val traitBonusInt = unlockedTraits.sumOf { TraitDatabase.getTrait(it)?.intBonus ?: 0 }
        
        val offHand1 = GameDatabase.getOffHand(equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(equippedOffHand2)
        val itemInt = offHand1.intelligence + offHand2.intelligence
        
        val base = 20 + ((level - 1) * 10) + ((intelligence + traitBonusInt + itemInt) * 5) + traitBonusMana
        return if (joinedGuild == "House of Water") (base * 1.02).toInt() else base
    }
}
