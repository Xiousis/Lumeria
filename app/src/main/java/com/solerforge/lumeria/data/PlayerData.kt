package com.solerforge.lumeria.data

import com.solerforge.lumeria.database.GameDatabase
import com.solerforge.lumeria.database.TraitDatabase
import kotlinx.serialization.Serializable

@Serializable
enum class SaveStatus {
    Empty, Loaded, RecoveredFromBackup, Corrupt
}

@Serializable
data class PlayerData(
    val level: Int = 1,
    val xp: Long = 0,
    val gold: Long = 0,
    val hp: Int = 80,
    val maxHp: Int = 80,
    val mana: Int = 45,
    val maxMana: Int = 45,
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
    val activeStoryArcId: Int? = null,
    val currentStoryEventIndex: Int = 0,
    val unlockedTitles: List<String> = listOf("Novice"),
    val currentTitle: String = "Novice",
    val introSeen: Boolean = false,
    val unlockedSkills: List<String> = listOf("None", "Slash", "Heavy Strike", "Magic Bolt", "Heal"),
    val equippedSkills: List<String> = listOf("Slash", "Heavy Strike", "Magic Bolt", "Heal"),
    val unlockedTraits: List<String> = emptyList(),
    val unlockedAchievements: List<String> = emptyList(),
    val freeWishesUsed: Int = 0,
    val bankGold: Long = 0,
    val activeBountyId: Int? = null,
    val completedBountyIds: List<Int> = emptyList(),
    val renown: Long = 0,
    val nationUpgradeLevel: Int = 0,
    val claimedRankIds: List<Int> = emptyList(),
    val joinedGuild: String? = null,
    val joinedGuildId: String? = null,
    val guildLevel: Int = 1,
    val guildXp: Long = 0,
    val activeBuffs: List<com.solerforge.lumeria.models.PlayerBuff> = emptyList(),
    val towerFloor: Int = 1,
    val towerHighestFloor: Int = 0,
    val towerCompleted: Boolean = false,
    val fishingLevel: Int = 1,
    val fishingXp: Long = 0,
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
    val pendingWager: Int = 0,
    val visitedBilly: Boolean = false,
    val visitedInn: Boolean = false,
    val visitedForge: Boolean = false,
    val voidIncursionLocation: String? = null,
    val riftStep: Int = 0,
    val riftGold: Long = 0,
    val riftXp: Long = 0,
    val riftLoot: List<String> = emptyList(),
    val towerUnlockedMessageSeen: Boolean = false,
    val skillExp: Map<String, Int> = emptyMap(),
    val skillLevels: Map<String, Int> = emptyMap(),
    val equippedFamiliar: String = "None",
    val familiarExp: Map<String, Int> = emptyMap(),
    val familiarLevels: Map<String, Int> = emptyMap(),
    val activeKingdomLawId: Int? = null,
    val completedTutorialSteps: List<String> = emptyList(),
    val unlockedFeatures: Set<String> = setOf("WORLD MAP", "STORY MODE", "INVENTORY", "CHARACTER STATS", "SKILL BOOK", "STORY JOURNAL", "BILLY'S STORE", "THE INN"),
    val totalStatPointsEarned: Int = 0,
    val villagesPlundered: Int = 0,
    val soulsHarvested: Long = 0,
    val heroPartiesDefeated: Int = 0,
    val saveVersion: Int = 6,
    
    // Legacy / Rebirth System
    val playerName: String = "Xious",
    val gender: String = "Male",
    val playerRace: String = "Human",
    val playerClass: String = "Warrior",
    val isReborn: Boolean = false,
    val legacyHeroStats: PlayerData? = null,

    // Leaderboard & PvP
    val pvpWins: Int = 0,
    val pvpLosses: Int = 0,
    val battleBuffsConsumed: Boolean = false,
    val lastSavedAt: Long = 0L
) {
    fun hasOffhand(name: String): Boolean = equippedOffHand == name || equippedOffHand2 == name

    fun getLevelCap(): Int = if (isReborn) 200 else 100

    fun gainExperience(amount: Long): PlayerData {
        val oldLevel = this.level
        val stats = com.solerforge.lumeria.models.XiousStats(level, xp, getLevelCap()).gainXp(amount)
        val levelsGained = stats.level - oldLevel
        
        var currentData = this.copy(
            level = stats.level,
            xp = stats.xp,
            statPoints = statPoints + (levelsGained * 5),
            totalStatPointsEarned = totalStatPointsEarned + (levelsGained * 5)
        )
        
        val newMaxHp = currentData.calculateMaxHp()
        val newMaxMana = currentData.calculateMaxMana()
        
        currentData = currentData.copy(
            maxHp = newMaxHp,
            maxMana = newMaxMana
        )
        
        if (levelsGained > 0) {
            currentData = currentData.copy(
                hp = newMaxHp,
                mana = newMaxMana
            )
        }
        
        // Automatic Skill Unlocks
        val newlyUnlockedSkills = com.solerforge.lumeria.database.SkillDatabase.skills
            .asSequence()
            .filter { skill ->
                val classMatches = skill.requiredClasses.isEmpty() || skill.requiredClasses.contains(playerClass)
                val isEvolvedVersionOwned = skill.evolvesTo != null && unlockedSkills.contains(skill.evolvesTo)
                
                (skill.unlockLevel <= stats.level) && 
                (!unlockedSkills.contains(skill.name)) &&
                (!skill.isEvolutionOnly) &&
                (!isEvolvedVersionOwned) &&
                classMatches
            }
            .map { it.name }
            .toList()
            
        if (newlyUnlockedSkills.isNotEmpty()) {
            currentData = currentData.copy(
                unlockedSkills = (unlockedSkills + newlyUnlockedSkills).distinct()
            )
        }
        
        return currentData.checkFeatureUnlocks()
    }

    fun checkFeatureUnlocks(): PlayerData {
        val newFeatures = unlockedFeatures.toMutableSet()
        
        if (level >= 5) {
            newFeatures.add("THE FORGE")
            newFeatures.add("IRONCLAD BANK")
            newFeatures.add("THE ROYAL COURT")
        }
        if (level >= 10) {
            newFeatures.add("THE ELDER")
            newFeatures.add("QUEST LOG")
            newFeatures.add("GRAND ARENA")
        }
        if (level >= 15) {
            newFeatures.add("FISHING POND")
            newFeatures.add("FAMILIAR NURSERY")
            newFeatures.add("LUMERIA CODEX")
        }
        if (defeatedBosses.isNotEmpty() || level >= 8) {
             newFeatures.add("BOUNTY BOARD")
        }
        if (renown >= 2000) {
            newFeatures.add("THE GUILD")
            newFeatures.add("HALL OF HEROES")
        }
        if (level >= 30) {
            newFeatures.add("THE GAMBLING HOUSE")
        }
        if (killCounts.isNotEmpty() || level >= 5) {
            newFeatures.add("BESTIARY")
        }

        return if (newFeatures.size > unlockedFeatures.size) copy(unlockedFeatures = newFeatures) else this
    }

    fun recalculateVitals(): PlayerData {
        val hpMax = calculateMaxHp().coerceAtLeast(1)
        val manaMax = calculateMaxMana().coerceAtLeast(1)

        return copy(
            strength = strength.coerceAtLeast(1),
            vitality = vitality.coerceAtLeast(1),
            defense = defense.coerceAtLeast(1),
            intelligence = intelligence.coerceAtLeast(1),
            agility = agility.coerceAtLeast(1),
            luck = luck.coerceAtLeast(1),
            wisdom = wisdom.coerceAtLeast(1),
            maxHp = hpMax,
            maxMana = manaMax,
            hp = hp.coerceIn(0, hpMax),
            mana = mana.coerceIn(0, manaMax)
        )
    }

    fun respec(): PlayerData {
        val totalInvested = if (totalStatPointsEarned > 0) totalStatPointsEarned else (level - 1) * 5
        
        // Base stats from class definitions
        val baseStats = if (!isReborn) {
            listOf(5, 5, 5, 5, 5, 5, 5)
        } else {
            GameDatabase.getClassBaseStats(playerClass)
        }

        val race = com.solerforge.lumeria.database.RaceDatabase.getRace(playerRace)

        return copy(
            strength = maxOf(1, baseStats[0] + race.strBonus),
            vitality = maxOf(1, baseStats[1] + race.vitBonus),
            defense = maxOf(1, baseStats[2] + race.defBonus),
            intelligence = maxOf(1, baseStats[3] + race.intBonus),
            agility = maxOf(1, baseStats[4] + race.agiBonus),
            luck = maxOf(1, baseStats[5] + race.luckBonus),
            wisdom = maxOf(1, baseStats[6] + race.wisBonus),
            statPoints = totalInvested,
            respecCount = respecCount + 1
        ).recalculateVitals()
    }

    fun consumeBattleBuffs(): PlayerData {
        if (battleBuffsConsumed || activeBuffs.isEmpty()) return this
        
        val updatedBuffs = activeBuffs.asSequence()
            .map { it.copy(battlesRemaining = it.battlesRemaining - 1) }
            .filter { it.battlesRemaining > 0 }
            .toList()
        
        return copy(activeBuffs = updatedBuffs, battleBuffsConsumed = true)
    }

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
        
        return maxOf(1, (base * multiplier).toInt())
    }

    fun calculateMaxMana(): Int {
        val traitBonusMana = unlockedTraits.sumOf { TraitDatabase.getTrait(it)?.manaBonus ?: 0 }
        val traitBonusInt = unlockedTraits.sumOf { TraitDatabase.getTrait(it)?.intBonus ?: 0 }
        
        val offHand1 = GameDatabase.getOffHand(equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(equippedOffHand2)
        val itemInt = offHand1.intelligence + offHand2.intelligence
        
        val base = 20 + ((level - 1) * 10) + ((intelligence + traitBonusInt + itemInt) * 5) + traitBonusMana
        val finalMana = if (joinedGuild == "House of Water") (base * 1.02).toInt() else base
        return maxOf(1, finalMana)
    }
}
