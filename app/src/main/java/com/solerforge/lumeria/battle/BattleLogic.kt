package com.solerforge.lumeria.battle

import com.solerforge.lumeria.data.Enemy
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.BossDatabase
import com.solerforge.lumeria.database.GameDatabase
import com.solerforge.lumeria.database.ArenaOpponent
import com.solerforge.lumeria.models.*

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
            "Slime", "Wild Rat", "Crystal Slime" -> 6
            "Training Goblin", "Goblin", "Goblin Archer" -> 10
            "Goblin Rogue", "Cave Bat", "Crystal Spider", "Mountain Wolf" -> 14
            "Orc", "Poison Toad", "Fire Drake" -> 18
            "Dark Spirit", "Stone Golem", "Lava Serpent" -> 22
            "Swamp Horror", "Shadow Knight" -> 28
            "Void Reaper" -> 40
            else -> 12
        } + (enemyLevel * 2.2).toInt()
        
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
        val baseDamage = (bossLevel * 5) + 30
        val effectiveArmor = if (ignoreArmor) 0 else armorDefense
        val finalDmg = (baseDamage * multiplier).toInt()
        
        // If multiplier is 0 (Heal/Buff), damage is 0. Otherwise at least 1.
        if (multiplier <= 0.0) return 0
        return maxOf(1, finalDmg - (effectiveArmor / 2))
    }

    fun calculateDodgeChance(player: PlayerData, evasionBonus: Double): Double {
        var dodgeChance = (player.agility * 0.005) + (player.luck * 0.002) + evasionBonus
        if (player.unlockedTraits.contains("Eyes of the Creator")) dodgeChance += 0.50
        if (player.joinedGuild == "House of Wind") dodgeChance += 0.02
        return dodgeChance.coerceIn(0.0, 0.90)
    }

    fun getSkillDamage(player: PlayerData, skill: Skill): Int {
        val weaponBonus = GameDatabase.getWeapon(player.equippedWeapon).attack
        
        val offHand1 = GameDatabase.getOffHand(player.equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(player.equippedOffHand2)
        
        val itemStr = offHand1.strength + offHand2.strength
        val itemInt = offHand1.intelligence + offHand2.intelligence
        val itemWis = offHand1.wisdom + offHand2.wisdom

        // Trait Bonuses
        val traitBonusStr = player.unlockedTraits.sumOf { com.solerforge.lumeria.database.TraitDatabase.getTrait(it)?.strBonus ?: 0 }
        val traitBonusInt = player.unlockedTraits.sumOf { com.solerforge.lumeria.database.TraitDatabase.getTrait(it)?.intBonus ?: 0 }
        
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
        if (player.joinedGuild == "House of Fire" && (skill.elementType == ElementType.Physical || skill.elementType == ElementType.Fire)) {
            multiplier *= 1.02
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
        enemyEvasionBonus: Double = 0.0,
    ): TurnResult {
        var nextEnemyHp = currentEnemyHp
        var nextPlayerHp = currentHp
        var playerDmgDealt = 0
        var actualHeal = 0
        var isCrit = false
        var isDodge = false
        var enemyDmgDealt = 0
        var stunApplied = false
        var bleedApplied = false
        var multiHits = 1
        val secondWindTriggered = false
        val reflectedDamage = 0
        var comboTriggered = false
        var appliedStatus = StatusEffect.None

        // 0. CHECK ENEMY DODGE
        val canEnemyDodge = skill.skillType == "Attack"
        if (canEnemyDodge && Math.random() < enemyEvasionBonus) {
            isDodge = true
            return TurnResult(
                playerHp = currentHp,
                playerMana = maxOf(0, currentMana - skill.manaCost),
                enemyHp = currentEnemyHp,
                isCrit = false,
                isDodge = true,
                playerDamage = 0,
                enemyDamage = 0,
                actualHeal = 0,
                stunApplied = false,
                bleedApplied = false,
                multiHits = 1,
                secondWindTriggered = false,
                reflectedDamage = 0,
                appliedStatus = StatusEffect.None,
                elementalMultiplier = 1.0,
                comboTriggered = false
            )
        }

        val bootsAgi = GameDatabase.getBoots(player.equippedBoots).agility
        val offHand1 = GameDatabase.getOffHand(player.equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(player.equippedOffHand2)
        
        val traitBonusAgi = player.unlockedTraits.sumOf { com.solerforge.lumeria.database.TraitDatabase.getTrait(it)?.agiBonus ?: 0 }
        
        val totalAgi = player.agility + bootsAgi + offHand1.agility + offHand2.agility + traitBonusAgi
        val totalLuck = player.luck + offHand1.luck + offHand2.luck

        // World Event Buffs are handled in BattleEngine to avoid double application
        var eventDamageMult = 1.0

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
                    val traitBonusInt = player.unlockedTraits.sumOf { com.solerforge.lumeria.database.TraitDatabase.getTrait(it)?.intBonus ?: 0 }
                    val healAmount = (10 + ((player.intelligence + intellectBonus + traitBonusInt) * 2)) * skill.multiplier
                    actualHeal = minOf(healAmount.toInt(), player.maxHp - nextPlayerHp)
                    nextPlayerHp = minOf(nextPlayerHp + healAmount.toInt(), player.maxHp)
                }
                "Parry" -> { /* Handled in Engine */ }
                "Buff", "DefenseBuff", "EvasionBuff", "CritBuff", "SuperBuff", "AgilityBuff" -> { /* Handled in Engine */ }
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
                        if (nextEnemyHp <= 0) return@repeat
                        
                        // Crit Logic: Agility + Luck
                        var currentCritChance = (totalAgi * 0.01) + (totalLuck * 0.005)
                        if (player.joinedGuild == "House of Wind") currentCritChance += 0.02
                        currentCritChance += bonusCritChance
                        if (hasEyeOfCreation) currentCritChance += 0.1
                        if (hasEyesOfCreatorTrait) currentCritChance += 0.5
                        
                        currentCritChance = currentCritChance.coerceIn(0.0, 0.90)
                        
                        // --- KINGDOM LAWS ---
                        if (player.activeKingdomLawId == 4) currentCritChance -= 0.10

                        var currentCrit = Math.random() < currentCritChance
                        if (skill.effectType == "LimitBreak") currentCrit = true
                        if (currentCrit) isCrit = true
                        
                        var baseDmg = getSkillDamage(player, skill).toDouble()
                        
                        // Enemy Passives: Magic Resist & Crystal Armor
                        if (enemy.passive == com.solerforge.lumeria.data.EnemyPassive.MAGIC_RESIST && skill.elementType != ElementType.Physical) {
                            baseDmg *= 0.6
                        }
                        if (enemy.passive == com.solerforge.lumeria.data.EnemyPassive.CRYSTAL_ARMOR) {
                            baseDmg = (baseDmg * 0.8) - 10
                        }

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
                        val kills = player.killCounts[enemy.baseName] ?: 0
                        if (kills >= 1000) baseDmg *= 1.10
                        
                        // Execute logic
                        if ((skill.effectType == "Execute") && ((nextEnemyHp.toDouble() / enemy.maxHp) < 0.3)) {
                            baseDmg *= 2.0
                        }
                        
                        var effectiveTargetDef = targetDefense
                        if (skill.effectType == "IgnoreArmor") effectiveTargetDef /= 2
                        if ((skill.effectType == "LimitBreak") || (skill.effectType == "MultiHitIgnore")) effectiveTargetDef = 0
                        
                        val hitDmg = if (skill.multiplier > 0) maxOf(1, (baseDmg - effectiveTargetDef).toInt()) else 0
                        
                        // Only deal damage up to remaining HP
                        val actualDmg = if (hitDmg > nextEnemyHp) nextEnemyHp else hitDmg
                        
                        totalDmg += actualDmg
                        nextEnemyHp = maxOf(nextEnemyHp - hitDmg, 0)
                        
                        // Lifesteal based on actual damage dealt
                        if (player.equippedWeapon == "Fang of Eternity") {
                            val drain = (actualDmg * 0.15).toInt()
                            if (drain > 0) nextPlayerHp = minOf(nextPlayerHp + drain, player.maxHp)
                        }
                        if (hasEyeOfCreation) {
                            val drain = (actualDmg * 0.1).toInt()
                            if (drain > 0) nextPlayerHp = minOf(nextPlayerHp + drain, player.maxHp)
                        }
                        if (skill.effectType == "Lifesteal") {
                            val drain = (actualDmg * 0.20).toInt() // 20% Life drain for skills
                            if (drain > 0) nextPlayerHp = minOf(nextPlayerHp + drain, player.maxHp)
                        }
                        if (skill.effectType == "HealAttack") {
                            val drain = (actualDmg * 0.50).toInt() // 50% Life drain for God Tier skill
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
                    } else if ((skill.effectType == "Freeze") && (Math.random() < 0.3)) {
                         appliedStatus = StatusEffect.Freeze
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
        bounty: com.solerforge.lumeria.data.Bounty? = null,
        isTower: Boolean = false,
        isRift: Boolean = false,
        isReplay: Boolean = false,
    ): Pair<Long, Long> {
        if (isStoryMode && isReplay) return Pair(0L, 0L)

        var xpGain: Long
        var goldGain: Long

        val isBountyDefeated = bounty != null && enemy.baseName == bounty.targetName

        if (isBountyDefeated) {
            xpGain = bounty.rewardXp
            goldGain = bounty.rewardGold
        } else if (isTower) {
            xpGain = enemy.rewardXp
            goldGain = enemy.rewardGold
        } else if (arenaOpponent != null) {
            xpGain = arenaOpponent.rewardXp
            goldGain = arenaOpponent.rewardGold
        } else if (isBossBattle) {
            val bossData = BossDatabase.resolveBoss(enemy.baseName)
            xpGain = maxOf(1L, bossData?.rewardXp ?: (enemy.level * 100L))
            goldGain = bossData?.rewardGold ?: (enemy.maxHp / 2L)
        } else {
            if (isStoryMode) {
                xpGain = maxOf(1L, enemy.rewardXp)
                goldGain = maxOf(1L, enemy.rewardGold)
            } else {
                xpGain = (enemy.level.toLong() * enemy.level * 2) + (enemy.level * 50) + 100
                goldGain = (enemy.maxHp * 0.8).toLong()
            }
        }

        if (isRift) {
            xpGain = (xpGain * 1.5).toLong()
            goldGain = (goldGain * 1.5).toLong()
        }

        // --- KINGDOM LAWS ---
        when (player.activeKingdomLawId) {
            1 -> goldGain = (goldGain * 0.9).toLong() // Merchant's Subsidy: -10% Battle Gold
            2 -> xpGain = (xpGain * 1.25).toLong()   // Warrior's Draft: +25% XP
            5 -> goldGain = (goldGain * 0.8).toLong() // Imperial Tithe: -20% Gold
        }

        // Apply Level Penalty for XP
        if ((!isBossBattle) && (!isBountyDefeated)) {
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
        goldGain = (goldGain * (1.0 + (player.luck * 0.01))).toLong()

        // Apply World Event Buffs
        player.activeBuffs.forEach { buff ->
            when (buff.type) {
                BuffType.Experience -> xpGain = (xpGain * buff.value).toLong()
                BuffType.Gold -> goldGain = (goldGain * buff.value).toLong()
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
        activeBounty: com.solerforge.lumeria.data.Bounty? = null,
        isTower: Boolean = false,
        isRift: Boolean = false,
        isReplay: Boolean = false,
        currentHp: Int,
        currentMana: Int,
        currentEventIndex: Int = 0,
        currentGuildExam: com.solerforge.lumeria.database.GuildDatabase.GuildExam? = null,
    ): PlayerData {
        val (baseXpGain, baseGoldGain) = calculateRewards(player, enemy, isBossBattle, isStoryMode, arenaOpponent, activeBounty, isTower, isRift, isReplay)

        var updatedActiveBountyId = player.activeBountyId
        var updatedCompletedBountyIds = player.completedBountyIds
        
        if ((activeBounty != null) && (enemy.baseName == activeBounty.targetName)) {
            updatedActiveBountyId = null
            updatedCompletedBountyIds = (player.completedBountyIds + activeBounty.id).distinct()
        }
        
        val newDefeatedBosses = if (isBossBattle && !isStoryMode) {
            (player.defeatedBosses + enemy.name).distinct()
        } else {
            player.defeatedBosses
        }
        
        // Battle XP Boost (Guild Max Level)
        var finalXpGain = baseXpGain
        if ((player.joinedGuild != null) && (player.guildLevel >= 10)) {
            finalXpGain = (finalXpGain * 1.05).toLong()
        }

        // --- RIFT LOGIC: Store rewards in rift session if not boss fight ---
        if (isRift && !isBossBattle) {
            val filteredDrops = lootDrops.filter { drop ->
                val isFamiliar = com.solerforge.lumeria.database.FamiliarDatabase.familiars.any { it.name == drop }
                if (isFamiliar) return@filter false
                val isGodTier = com.solerforge.lumeria.database.BossLootDatabase.allGodTierItems.contains(drop)
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

        var updatedPlayer = player.copy(hp = currentHp, mana = currentMana).gainExperience(totalXpGain)
        
        var updatedEventIndex = currentEventIndex
        if (isStoryMode) {
            updatedEventIndex++
        }

        if (isReplay) {
            return updatedPlayer.copy(
                gold = updatedPlayer.gold + totalGoldGain,
                currentStoryEventIndex = updatedEventIndex
            )
        }

        // Quest Update Logic (Common for all)
        val currentQuests = updatedPlayer.quests.toMutableList()
        val updatedQuests = currentQuests.asSequence().map { quest ->
            if ((!quest.isCompleted) && (quest.targetEnemy == enemy.name)) {
                val newCount = minOf(quest.currentCount + 1, quest.targetCount)
                quest.copy(currentCount = newCount, isCompleted = newCount >= quest.targetCount)
            } else {
                quest
            }
        }.toMutableList()

        // Bestiary Update Logic
        val newKillCounts = updatedPlayer.killCounts.toMutableMap()
        newKillCounts[enemy.baseName] = (newKillCounts[enemy.baseName] ?: 0) + 1

        // Renown Logic
        var renownGain = when {
            isRift -> 750L
            (activeBounty != null) && (enemy.baseName == activeBounty.targetName) -> 250L
            isTower -> 40L
            arenaOpponent != null -> 60L
            isBossBattle -> 150L
            isStoryMode -> 50L
            else -> 15L
        }

        if (updatedPlayer.activeKingdomLawId == 5) {
            renownGain = (renownGain * 1.5).toLong() // Imperial Tithe: +50% Renown
        }

        // Arena Completion Tracking
        val newCompletedArena = if (arenaOpponent != null) {
            (updatedPlayer.completedArenaOpponents + enemy.name).distinct()
        } else {
            updatedPlayer.completedArenaOpponents
        }

        // Guild XP Logic
        var updatedGuildXp = updatedPlayer.guildXp
        var updatedGuildLevel = updatedPlayer.guildLevel
        if (updatedPlayer.joinedGuild != null) {
            val needed = com.solerforge.lumeria.database.GuildDatabase.getGuildXpForNextLevel(updatedGuildLevel)
            if (updatedGuildLevel < 10) {
                updatedGuildXp += (enemy.level * 10L) + 50L
                if (updatedGuildXp > needed) updatedGuildXp = needed.toLong()
            }
        }
        
        // Handle Guild Exam Victory
        if (currentGuildExam != null) {
            updatedGuildLevel++
            updatedGuildXp = 0
        }

        // Familiar XP Logic
        val workingFamiliarExp = updatedPlayer.familiarExp.toMutableMap()
        val workingFamiliarLevels = updatedPlayer.familiarLevels.toMutableMap()
        if (updatedPlayer.equippedFamiliar != "None") {
            val fName = updatedPlayer.equippedFamiliar
            val currentExp = workingFamiliarExp[fName] ?: 0
            val currentLvl = workingFamiliarLevels[fName] ?: 1
            val famXpGain = (enemy.level * 5L) + 20L
            val newExp = currentExp.toLong() + famXpGain
            val needed = com.solerforge.lumeria.database.FamiliarDatabase.getExpForNextLevel(currentLvl).toLong()
            if (newExp >= needed) {
                workingFamiliarLevels[fName] = currentLvl + 1
                workingFamiliarExp[fName] = (newExp - needed).toInt()
            } else {
                workingFamiliarExp[fName] = newExp.toInt()
            }
        }
        
        // Unlock Familiars from Drops
        combinedLoot.forEach { drop ->
            if (com.solerforge.lumeria.database.FamiliarDatabase.familiars.any { it.name == drop } && !workingFamiliarLevels.containsKey(drop)) {
                workingFamiliarLevels[drop] = 1
                workingFamiliarExp[drop] = 0
            }
        }

        var towerFloor = updatedPlayer.towerFloor
        var towerHighestFloor = updatedPlayer.towerHighestFloor
        var towerCompleted = updatedPlayer.towerCompleted
        
        if (isTower) {
            if (towerFloor >= 100) {
                towerCompleted = true
                towerHighestFloor = maxOf(towerHighestFloor, towerFloor)
            } else {
                towerFloor++
                towerHighestFloor = maxOf(towerHighestFloor, updatedPlayer.towerFloor)
            }
        }

        return updatedPlayer.copy(
            gold = updatedPlayer.gold + totalGoldGain,
            activeBountyId = updatedActiveBountyId,
            completedBountyIds = updatedCompletedBountyIds,
            renown = updatedPlayer.renown + renownGain,
            guildXp = updatedGuildXp,
            guildLevel = updatedGuildLevel,
            familiarExp = workingFamiliarExp,
            familiarLevels = workingFamiliarLevels,
            completedArenaOpponents = newCompletedArena,
            voidIncursionLocation = if (isRift) null else updatedPlayer.voidIncursionLocation,
            riftGold = if (isRift && isBossBattle) 0 else updatedPlayer.riftGold,
            riftXp = if (isRift && isBossBattle) 0 else updatedPlayer.riftXp,
            riftLoot = if (isRift && isBossBattle) emptyList() else updatedPlayer.riftLoot,
            riftStep = if (isRift && isBossBattle) 0 else updatedPlayer.riftStep,
            quests = updatedQuests,
            defeatedBosses = newDefeatedBosses,
            killCounts = newKillCounts,
            towerFloor = towerFloor,
            towerHighestFloor = towerHighestFloor,
            towerCompleted = towerCompleted,
            currentStoryEventIndex = updatedEventIndex,
        ).let { playerWithStats ->
            // Inventory Updates (Loot)
            val filteredDrops = combinedLoot.filter { drop ->
                val isFamiliar = com.solerforge.lumeria.database.FamiliarDatabase.familiars.any { it.name == drop }
                if (isFamiliar) return@filter false
                val isGodTier = com.solerforge.lumeria.database.BossLootDatabase.allGodTierItems.contains(drop)
                if (isGodTier && playerWithStats.inventory.contains(drop)) return@filter false
                true
            }

            val updatedInventory = (playerWithStats.inventory + filteredDrops)

            // Titles & Achievements
            val updatedUnlockedTitles = playerWithStats.unlockedTitles.toMutableList()
            var updatedCurrentTitle = playerWithStats.currentTitle
            
            val godTierItems = com.solerforge.lumeria.database.BossLootDatabase.allGodTierItems
            if ((godTierItems.all { it in updatedInventory }) && ("Godwalker" !in updatedUnlockedTitles)) {
                updatedUnlockedTitles.add("Godwalker")
                updatedCurrentTitle = "Godwalker"
            }

            if ((playerWithStats.level >= 100) && ("Immortal Legend" !in updatedUnlockedTitles)) {
                updatedUnlockedTitles.add("Immortal Legend")
                updatedCurrentTitle = "Immortal Legend"
            }

            playerWithStats.copy(
                inventory = updatedInventory,
                unlockedTitles = updatedUnlockedTitles.distinct(),
                currentTitle = updatedCurrentTitle,
            ).checkFeatureUnlocks()
        }
    }
}
