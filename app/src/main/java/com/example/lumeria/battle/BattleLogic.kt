package com.example.lumeria.battle

import com.example.lumeria.data.Enemy
import com.example.lumeria.data.PlayerData
import com.example.lumeria.database.BossDatabase
import com.example.lumeria.database.GameDatabase
import com.example.lumeria.database.ArenaOpponent
import com.example.lumeria.models.*

// ---------------------------------------------------------
// BATTLE ENGINE
// ---------------------------------------------------------

object BattleLogic {

    data class TurnResult(
        val playerHp: Int,
        val playerMana: Int,
        val enemyHp: Int,
        val isCrit: Boolean,
        val isDodge: Boolean,
        val playerDamage: Int,
        val enemyDamage: Int,
        val actualHeal: Int,
        val extraEnemyHits: Int = 0,
        val stunApplied: Boolean = false,
        val bleedApplied: Boolean = false,
        val multiHits: Int = 1,
        val secondWindTriggered: Boolean = false,
        val reflectedDamage: Int = 0,
        val appliedStatus: StatusEffect = StatusEffect.None,
        val elementalMultiplier: Double = 1.0,
        val comboTriggered: Boolean = false,
    )

    fun calculateEnemyDamage(
        enemyName: String, 
        enemyLevel: Int, 
        armorDefense: Int,
        multiplier: Double = 1.0,
        ignoreArmor: Boolean = false,
    ): Int {
        val baseEnemyDamage = when (enemyName) {
            "Slime", "Wild Rat", "Crystal Slime" -> 4
            "Training Goblin", "Goblin", "Goblin Archer" -> 6
            "Goblin Rogue", "Cave Bat", "Crystal Spider", "Mountain Wolf" -> 8
            "Orc", "Poison Toad", "Fire Drake" -> 10
            "Dark Spirit", "Stone Golem", "Lava Serpent" -> 12
            "Swamp Horror", "Shadow Knight" -> 15
            "Void Reaper" -> 20
            else -> 8
        } + (enemyLevel * 1.5).toInt()
        
        val effectiveArmor = if (ignoreArmor) 0 else armorDefense
        val finalDmg = (baseEnemyDamage * multiplier).toInt()
        return maxOf(1, finalDmg - (effectiveArmor / 2))
    }

    fun calculateBossDamage(
        bossLevel: Int,
        armorDefense: Int,
        multiplier: Double = 1.0,
        ignoreArmor: Boolean = false,
    ): Int {
        val baseDamage = (bossLevel * 3) + 15
        val effectiveArmor = if (ignoreArmor) 0 else armorDefense
        val finalDmg = (baseDamage * multiplier).toInt()
        return maxOf(1, finalDmg - (effectiveArmor / 2))
    }

    fun getSkillDamage(player: PlayerData, skill: Skill): Int {
        val weaponBonus = GameDatabase.getWeapon(player.equippedWeapon).attack
        
        val offHand1 = GameDatabase.getOffHand(player.equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(player.equippedOffHand2)
        
        val itemStr = offHand1.strength + offHand2.strength
        val itemInt = offHand1.intelligence + offHand2.intelligence
        val itemWis = offHand1.wisdom + offHand2.wisdom

        // Trait Bonuses
        val traitBonusStr = player.unlockedTraits.sumOf { com.example.lumeria.database.TraitDatabase.getTrait(it)?.strBonus ?: 0 }
        val traitBonusInt = player.unlockedTraits.sumOf { com.example.lumeria.database.TraitDatabase.getTrait(it)?.intBonus ?: 0 }
        
        var totalStr = player.strength + itemStr + traitBonusStr
        val totalInt = player.intelligence + itemInt + traitBonusInt
        val totalWis = player.wisdom + itemWis

        val hasEyesOfCreator = player.unlockedTraits.contains("Eyes of the Creator")
        if (hasEyesOfCreator) {
            totalStr = (totalStr * 0.75).toInt()
        }

        val baseDmg = if ((skill.elementType != ElementType.Physical) || (skill.name == "Magic Bolt")) {
            // Magic Damage: Scales heavily with Int, slightly with Wis
            var dmg = 8 + (totalInt * 2.5) + (totalWis * 0.5)
            if (hasEyesOfCreator) dmg *= 1.3
            dmg
        } else {
            // Physical Damage: Scales heavily with Str, slightly with Int (enchantment effect)
            5 + weaponBonus + totalStr + (totalInt * 0.2)
        }
        
        var multiplier = skill.multiplier
        if (player.joinedGuild == "House of Fire") {
            multiplier *= 1.02 // 2% STR boost simulated as damage mult for all physical/fire
        }

        return (baseDmg * multiplier).toInt()
    }

    fun processTurn(
        skill: Skill,
        player: PlayerData,
        currentHp: Int,
        currentMana: Int,
        currentEnemyHp: Int,
        enemy: Enemy,
        targetDefense: Int = 0,
        playerDamageBuff: Double = 1.0,
        bonusCritChance: Double = 0.0,
        previousSkillName: String? = null,
    ): TurnResult {
        var nextEnemyHp = currentEnemyHp
        var nextPlayerHp = currentHp
        var playerDmgDealt = 0
        var actualHeal = 0
        var isCrit = false
        val isDodge = false
        val enemyDmgDealt = 0
        var stunApplied = false
        var bleedApplied = false
        var multiHits = 1
        val secondWindTriggered = false
        val reflectedDamage = 0
        var comboTriggered = false
        var appliedStatus = StatusEffect.None

        val bootsAgi = GameDatabase.getBoots(player.equippedBoots).agility
        val offHand1 = GameDatabase.getOffHand(player.equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(player.equippedOffHand2)
        
        val traitBonusAgi = player.unlockedTraits.sumOf { com.example.lumeria.database.TraitDatabase.getTrait(it)?.agiBonus ?: 0 }
        
        val totalAgi = player.agility + bootsAgi + offHand1.agility + offHand2.agility + traitBonusAgi
        val totalLuck = player.luck + offHand1.luck + offHand2.luck

        // World Event Buffs
        var eventDamageMult = 1.0
        player.activeBuffs.forEach { buff ->
            if (buff.type == BuffType.Damage) eventDamageMult *= buff.value
        }

        // Elemental Multiplier
        val elementalMult = enemy.elementalResistances[skill.elementType] ?: 1.0
        
        // Combo Logic
        var comboMultiplier = 1.0
        if ((previousSkillName == "Shield Bash") && (skill.name == "Heavy Strike")) {
            comboMultiplier = 1.5
            comboTriggered = true
        } else if ((previousSkillName == "Magic Bolt") && (skill.name == "Blade Dance")) {
            comboMultiplier = 1.3
            comboTriggered = true
        }

        // God Tier: Eye of Creation (OffHand) stats
        val hasEyeOfCreation = player.equippedOffHand == "Eye of Creation"
        
        // God Tier Trait: Eyes of the Creator
        val hasEyesOfCreatorTrait = player.unlockedTraits.contains("Eyes of the Creator")
        
        // 1. Player Action
        val manaCost = skill.manaCost
        if (currentMana >= manaCost) {
            when (skill.effectType) {
                "Heal" -> {
                    val intellectBonus = offHand1.intelligence + offHand2.intelligence
                    val traitBonusInt = player.unlockedTraits.sumOf { com.example.lumeria.database.TraitDatabase.getTrait(it)?.intBonus ?: 0 }
                    val healAmount = (10 + ((player.intelligence + intellectBonus + traitBonusInt) * 2)) * skill.multiplier
                    actualHeal = minOf(healAmount.toInt(), player.maxHp - nextPlayerHp)
                    nextPlayerHp = minOf(nextPlayerHp + healAmount.toInt(), player.maxHp)
                }
                "Parry" -> { /* Handled in Engine */ }
                "Buff", "CritBuff", "SuperBuff" -> { /* Handled in Engine */ }
                else -> {
                    // Attack logic
                    multiHits = when (skill.effectType) {
                        "MultiHit", "MultiHitIgnore" -> {
                            when (skill.name) {
                                "Starlight Judgment" -> 5
                                "Blade Dance" -> 4
                                "Whirlwind Slash" -> 2
                                "Cyclone Edge" -> 3
                                "Omnislash" -> 3
                                else -> 2
                            }
                        }
                        else -> 1
                    }
                    
                    var totalDmg = 0
                    repeat(multiHits) {
                        // Crit Logic: Agility + Luck
                        var currentCritChance = (totalAgi * 0.01) + (totalLuck * 0.005)
                        if (player.joinedGuild == "House of Wind") currentCritChance += 0.02
                        currentCritChance += bonusCritChance
                        if (hasEyeOfCreation) currentCritChance += 0.1
                        if (hasEyesOfCreatorTrait) currentCritChance += 0.5
                        
                        // --- KINGDOM LAWS ---
                        if (player.activeKingdomLawId == 4) currentCritChance -= 0.10

                        var currentCrit = Math.random() < currentCritChance
                        if (skill.effectType == "LimitBreak") currentCrit = true
                        if (currentCrit) isCrit = true
                        
                        var baseDmg = getSkillDamage(player, skill).toDouble()
                        
                        // Apply Elemental & Combo multipliers
                        baseDmg *= elementalMult
                        baseDmg *= comboMultiplier
                        
                        // God Tier: Bulwark of Heroes (Shield) effect
                        if ((player.equippedShield == "Bulwark of Heroes") && ((currentHp.toDouble() / player.maxHp) < 0.5)) {
                            baseDmg *= 1.25
                        }
                        
                        // God Tier: Eye of Creation (OffHand) effect
                        if (hasEyeOfCreation) baseDmg *= 1.25
                        
                        var critMultiplier = 2.0
                        // God Tier: Endbringer (Weapon) effect
                        if (player.equippedWeapon == "Endbringer") critMultiplier = 3.0
                        
                        if (currentCrit) baseDmg *= critMultiplier
                        
                        // Apply player damage buff
                        baseDmg *= playerDamageBuff
                        
                        // Apply World Event Buff
                        baseDmg *= eventDamageMult
                        
                        // Bestiary Mastery Damage Bonus (Rank 4 = 1000 Kills)
                        val kills = player.killCounts[enemy.name] ?: 0
                        if (kills >= 1000) baseDmg *= 1.10
                        
                        // Execute logic
                        if ((skill.effectType == "Execute") && ((nextEnemyHp.toDouble() / enemy.maxHp) < 0.3)) {
                            baseDmg *= 2.0
                        }
                        
                        var effectiveTargetDef = targetDefense
                        if (skill.effectType == "IgnoreArmor") effectiveTargetDef /= 2
                        if ((skill.effectType == "LimitBreak") || (skill.effectType == "MultiHitIgnore")) effectiveTargetDef = 0
                        
                        val hitDmg = if (skill.multiplier > 0) maxOf(1, (baseDmg - effectiveTargetDef).toInt()) else 0
                        totalDmg += hitDmg
                        nextEnemyHp = maxOf(nextEnemyHp - hitDmg, 0)
                        
                        // Lifesteal
                        if (player.equippedWeapon == "Fang of Eternity") {
                            val drain = (hitDmg * 0.15).toInt()
                            if (drain > 0) nextPlayerHp = minOf(nextPlayerHp + drain, player.maxHp)
                        }
                        if (hasEyeOfCreation) {
                            val drain = (hitDmg * 0.1).toInt()
                            if (drain > 0) nextPlayerHp = minOf(nextPlayerHp + drain, player.maxHp)
                        }
                        if (skill.effectType == "Lifesteal") {
                            val drain = (hitDmg * 0.20).toInt() // 20% Life drain for skills
                            if (drain > 0) nextPlayerHp = minOf(nextPlayerHp + drain, player.maxHp)
                        }
                        if (skill.effectType == "HealAttack") {
                            val drain = (hitDmg * 0.50).toInt() // 50% Life drain for God Tier skill
                            if (drain > 0) nextPlayerHp = minOf(nextPlayerHp + drain, player.maxHp)
                        }
                    }
                    playerDmgDealt = totalDmg
                    
                    if ((skill.statusEffect != StatusEffect.None) && (Math.random() < 0.4)) {
                        appliedStatus = skill.statusEffect
                        if (appliedStatus == StatusEffect.Stun) stunApplied = true
                        if (appliedStatus == StatusEffect.Bleed) bleedApplied = true
                    } else if ((skill.effectType == "Stun") && (Math.random() < 0.3)) {
                         stunApplied = true
                         appliedStatus = StatusEffect.Stun
                    } else if (skill.effectType == "Bleed") {
                         bleedApplied = true
                         appliedStatus = StatusEffect.Bleed
                    }
                    
                    // Eyes of the Creator: 5% Stun Chance
                    if (hasEyesOfCreatorTrait && (appliedStatus == StatusEffect.None) && (Math.random() < 0.05)) {
                        stunApplied = true
                        appliedStatus = StatusEffect.Stun
                    }
                }
            }
        }

        return TurnResult(
            playerHp = nextPlayerHp,
            playerMana = if (currentMana >= manaCost) maxOf(0, currentMana - manaCost) else currentMana,
            enemyHp = nextEnemyHp,
            isCrit = isCrit,
            isDodge = isDodge,
            playerDamage = playerDmgDealt,
            enemyDamage = enemyDmgDealt,
            actualHeal = actualHeal,
            stunApplied = stunApplied,
            bleedApplied = bleedApplied,
            multiHits = multiHits,
            secondWindTriggered = secondWindTriggered,
            reflectedDamage = reflectedDamage,
            appliedStatus = appliedStatus,
            elementalMultiplier = elementalMult,
            comboTriggered = comboTriggered,
        )
    }

    fun calculateRewards(
        player: PlayerData,
        enemy: Enemy,
        isBossBattle: Boolean = false,
        isStoryMode: Boolean = false,
        arenaOpponent: ArenaOpponent? = null,
        bounty: com.example.lumeria.data.Bounty? = null,
        isTower: Boolean = false,
        isRift: Boolean = false,
    ): Pair<Int, Int> {
        var xpGain: Int
        var goldGain: Int

        if (bounty != null) {
            xpGain = bounty.rewardXp
            goldGain = bounty.rewardGold
        } else if (isTower) {
            xpGain = enemy.rewardXp
            goldGain = enemy.rewardGold
        } else if (arenaOpponent != null) {
            xpGain = arenaOpponent.rewardXp
            goldGain = arenaOpponent.rewardGold
        } else if (isBossBattle) {
            val bossData = if (isStoryMode) {
                com.example.lumeria.database.StoryBossDatabase.getBoss(enemy.name)
            } else {
                BossDatabase.getBoss(enemy.name)
            }
            xpGain = maxOf(1, bossData.rewardXp)
            goldGain = bossData.rewardGold
        } else {
            if (isStoryMode) {
                xpGain = maxOf(1, enemy.rewardXp)
                goldGain = maxOf(1, enemy.rewardGold)
            } else {
                xpGain = (enemy.level * enemy.level * 2) + (enemy.level * 50) + 100
                goldGain = (enemy.maxHp * 0.8).toInt()
            }
        }

        if (isRift) {
            xpGain = (xpGain * 1.5).toInt()
            goldGain = (goldGain * 1.5).toInt()
        }

        // --- KINGDOM LAWS ---
        when (player.activeKingdomLawId) {
            1 -> goldGain = (goldGain * 0.9).toInt() // Merchant's Subsidy: -10% Battle Gold
            2 -> xpGain = (xpGain * 1.25).toInt()   // Warrior's Draft: +25% XP
            5 -> goldGain = (goldGain * 0.8).toInt() // Imperial Tithe: -20% Gold
        }

        // Apply Level Penalty for XP
        if ((!isBossBattle) && (bounty == null)) {
            val levelDiff = player.level - enemy.level
            when {
                levelDiff > 20 -> xpGain = 1
                levelDiff > 10 -> xpGain = maxOf(1, xpGain / 2)
            }
        }

        // Apply Gold Bonuses
        if (player.equippedOffHand == "Coin of the Tyrant") {
            goldGain *= 2
        }
        
        // Luck Bonus to Gold (1% per Luck point)
        goldGain = (goldGain * (1.0 + (player.luck * 0.01))).toInt()

        // Apply World Event Buffs
        player.activeBuffs.forEach { buff ->
            when (buff.type) {
                BuffType.Experience -> xpGain = (xpGain * buff.value).toInt()
                BuffType.Gold -> goldGain = (goldGain * buff.value).toInt()
                else -> {}
            }
        }

        return Pair(xpGain, goldGain)
    }

    fun calculateVictory(
        player: PlayerData,
        enemy: Enemy,
        isBossBattle: Boolean = false,
        lootDrops: List<String> = emptyList(),
        isStoryMode: Boolean = false,
        arenaOpponent: ArenaOpponent? = null,
        activeBounty: com.example.lumeria.data.Bounty? = null,
        isTower: Boolean = false,
        isRift: Boolean = false,
        currentHp: Int,
        currentMana: Int,
    ): PlayerData {
        val (baseXpGain, baseGoldGain) = calculateRewards(player, enemy, isBossBattle, isStoryMode, arenaOpponent, activeBounty, isTower, isRift)

        var updatedActiveBountyId = player.activeBountyId
        var updatedCompletedBountyIds = player.completedBountyIds
        
        if ((activeBounty != null) && (enemy.name == activeBounty.targetName)) {
            updatedActiveBountyId = null
            updatedCompletedBountyIds = (player.completedBountyIds + activeBounty.id).distinct()
        }
        
        val newDefeatedBosses = if (isBossBattle && !isStoryMode) {
            (player.defeatedBosses + enemy.name).distinct()
        } else {
            player.defeatedBosses
        }
        
        val oldLevel = player.level
        
        // Battle XP Boost (Guild Max Level)
        var finalXpGain = baseXpGain
        if ((player.joinedGuild != null) && (player.guildLevel >= 10)) {
            finalXpGain = (finalXpGain * 1.05).toInt()
        }

        // --- RIFT LOGIC: Store rewards in rift session if not boss fight ---
        if (isRift && !isBossBattle) {
            val filteredDrops = lootDrops.filter { drop ->
                val isFamiliar = com.example.lumeria.database.FamiliarDatabase.familiars.any { it.name == drop }
                if (isFamiliar) return@filter false
                val isGodTier = com.example.lumeria.database.BossLootDatabase.allGodTierItems.contains(drop)
                if (isGodTier && (player.inventory.contains(drop) || player.riftLoot.contains(drop))) return@filter false
                true
            }

            return player.copy(
                riftGold = player.riftGold + baseGoldGain,
                riftXp = player.riftXp + finalXpGain,
                riftLoot = player.riftLoot + filteredDrops,
                riftStep = player.riftStep + 1,
                hp = currentHp,
                mana = currentMana,
            )
        }

        // If it's a Rift Boss victory, we combine rift rewards with boss rewards
        val totalXpGain = if (isRift && isBossBattle) finalXpGain + player.riftXp else finalXpGain
        val totalGoldGain = if (isRift && isBossBattle) baseGoldGain + player.riftGold else baseGoldGain
        val combinedLoot = if (isRift && isBossBattle) player.riftLoot + lootDrops else lootDrops

        val leveledStats = XiousStats(level = player.level, xp = player.xp).gainXp(totalXpGain)
        
        // Quest Update Logic (Common for all)
        val currentQuests = player.quests.toMutableList()
        val updatedQuests = currentQuests.asSequence().map { quest ->
            if ((!quest.isCompleted) && (quest.targetEnemy == enemy.name)) {
                val newCount = minOf(quest.currentCount + 1, quest.targetCount)
                quest.copy(currentCount = newCount, isCompleted = newCount >= quest.targetCount)
            } else {
                quest
            }
        }.toMutableList()

        // Stat Growth
        val levelsGained = leveledStats.level - oldLevel
        val newStatPoints = player.statPoints + (levelsGained * 5)
        
        // Bestiary Update Logic
        val newKillCounts = player.killCounts.toMutableMap()
        newKillCounts[enemy.name] = (newKillCounts[enemy.name] ?: 0) + 1

        // Renown Logic
        var renownGain = when {
            isRift -> 750
            (activeBounty != null) && (enemy.name == activeBounty.targetName) -> 250
            isTower -> 40
            arenaOpponent != null -> 60
            isBossBattle -> 150
            isStoryMode -> 50
            else -> 15
        }

        if (player.activeKingdomLawId == 5) {
            renownGain = (renownGain * 1.5).toInt() // Imperial Tithe: +50% Renown
        }

        // Arena Completion Tracking
        val newCompletedArena = if (arenaOpponent != null) {
            (player.completedArenaOpponents + enemy.name).distinct()
        } else {
            player.completedArenaOpponents
        }

        // Guild XP Logic
        var updatedGuildXp = player.guildXp
        val updatedGuildLevel = player.guildLevel
        if (player.joinedGuild != null) {
            val needed = com.example.lumeria.database.GuildDatabase.getGuildXpForNextLevel(updatedGuildLevel)
            if (updatedGuildLevel < 10) {
                updatedGuildXp += (enemy.level * 10) + 50
                if (updatedGuildXp > needed) updatedGuildXp = needed
            }
        }

        // Familiar XP Logic
        val workingFamiliarExp = player.familiarExp.toMutableMap()
        val workingFamiliarLevels = player.familiarLevels.toMutableMap()
        if (player.equippedFamiliar != "None") {
            val fName = player.equippedFamiliar
            val currentExp = workingFamiliarExp[fName] ?: 0
            val currentLvl = workingFamiliarLevels[fName] ?: 1
            val famXpGain = (enemy.level * 5) + 20
            val newExp = currentExp + famXpGain
            val needed = com.example.lumeria.database.FamiliarDatabase.getExpForNextLevel(currentLvl)
            if (newExp >= needed) {
                workingFamiliarLevels[fName] = currentLvl + 1
                workingFamiliarExp[fName] = newExp - needed
            } else {
                workingFamiliarExp[fName] = newExp
            }
        }
        
        // Unlock Familiars from Drops
        combinedLoot.forEach { drop ->
            if (com.example.lumeria.database.FamiliarDatabase.familiars.any { it.name == drop } && !workingFamiliarLevels.containsKey(drop)) {
                workingFamiliarLevels[drop] = 1
                workingFamiliarExp[drop] = 0
            }
        }

        val updatedPlayer = player.copy(
            level = leveledStats.level,
            xp = leveledStats.xp,
            statPoints = newStatPoints,
            killCounts = newKillCounts,
            activeBountyId = updatedActiveBountyId,
            completedBountyIds = updatedCompletedBountyIds,
            renown = player.renown + renownGain,
            guildXp = updatedGuildXp,
            guildLevel = updatedGuildLevel,
            familiarExp = workingFamiliarExp,
            familiarLevels = workingFamiliarLevels,
            completedArenaOpponents = newCompletedArena,
            voidIncursionLocation = if (isRift) null else player.voidIncursionLocation,
            riftGold = if (isRift && isBossBattle) 0 else player.riftGold,
            riftXp = if (isRift && isBossBattle) 0 else player.riftXp,
            riftLoot = if (isRift && isBossBattle) emptyList() else player.riftLoot,
            riftStep = if (isRift && isBossBattle) 0 else player.riftStep,
        )
        
        val newMaxHp = updatedPlayer.calculateMaxHp()
        val newMaxMana = updatedPlayer.calculateMaxMana()
        
        var finalHp = currentHp
        var finalMana = currentMana
        if (levelsGained > 0) {
            finalHp = newMaxHp
            finalMana = newMaxMana
        }
        
        // Inventory Updates (Loot)
        val filteredDrops = combinedLoot.filter { drop ->
            val isFamiliar = com.example.lumeria.database.FamiliarDatabase.familiars.any { it.name == drop }
            if (isFamiliar) return@filter false
            val isGodTier = com.example.lumeria.database.BossLootDatabase.allGodTierItems.contains(drop)
            if (isGodTier && player.inventory.contains(drop)) return@filter false
            true
        }

        val updatedInventory = (player.inventory + filteredDrops)

        // Automatic Skill Unlocks
        val newlyUnlockedSkills = com.example.lumeria.database.SkillDatabase.skills
            .asSequence()
            .filter { (it.unlockLevel <= leveledStats.level) && (!player.unlockedSkills.contains(it.name)) }
            .map { it.name }
            .toList()
        val allUnlockedSkills = (player.unlockedSkills + newlyUnlockedSkills).distinct()

        // Titles & Achievements
        val updatedUnlockedTitles = player.unlockedTitles.toMutableList()
        var updatedCurrentTitle = player.currentTitle
        
        val godTierItems = com.example.lumeria.database.BossLootDatabase.allGodTierItems
        if ((godTierItems.all { it in updatedInventory }) && ("Godwalker" !in updatedUnlockedTitles)) {
            updatedUnlockedTitles.add("Godwalker")
            updatedCurrentTitle = "Godwalker"
        }

        if ((leveledStats.level >= 100) && ("Immortal Legend" !in updatedUnlockedTitles)) {
            updatedUnlockedTitles.add("Immortal Legend")
            updatedCurrentTitle = "Immortal Legend"
        }

        return updatedPlayer.copy(
            gold = player.gold + totalGoldGain,
            hp = finalHp,
            maxHp = newMaxHp,
            mana = finalMana,
            maxMana = newMaxMana,
            statPoints = newStatPoints,
            quests = updatedQuests,
            inventory = updatedInventory,
            defeatedBosses = newDefeatedBosses,
            unlockedSkills = allUnlockedSkills,
            unlockedTitles = updatedUnlockedTitles.distinct(),
            currentTitle = updatedCurrentTitle,
            killCounts = newKillCounts,
        )
    }
}
