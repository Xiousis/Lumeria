package com.solerforge.lumeria.battle

import androidx.compose.ui.graphics.Color
import com.solerforge.lumeria.database.*
import com.solerforge.lumeria.models.*

object BattleEngine {

    data class PlayerTurnResult(
        val state: BattleUiState,
        val logs: List<LogEntry>,
        val turnConsumed: Boolean
    )

    /**
     * Processes a single player turn.
     * Returns the updated state and a list of new logs.
     */
    fun processPlayerTurn(
        state: BattleUiState,
        skillName: String,
        storyEnemyName: String?,
        isBossBattle: Boolean,
    ): PlayerTurnResult {
        val skill = SkillDatabase.resolveSkill(skillName) ?: SkillDatabase.skills[0]
        val newLogs = mutableListOf<LogEntry>()
        val pName = state.currentPlayerSnapshot.playerName

        // 0. COOLDOWN VALIDATION
        val remainingCooldown = state.skillCooldowns[skill.name] ?: 0
        if (remainingCooldown > 0 && skill.name != "None") {
            newLogs.add(LogEntry("${skill.name} is still cooling down!", Color.Red))
            return PlayerTurnResult(state.copy(isProcessing = false), newLogs, false)
        }
        
        // 1. START OF TURN HOUSEKEEPING (Calculate prospectives)
        val offHand1 = GameDatabase.getOffHand(state.currentPlayerSnapshot.equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(state.currentPlayerSnapshot.equippedOffHand2)
        val totalWisdom = state.currentPlayerSnapshot.wisdom + offHand1.wisdom + offHand2.wisdom
        var wisdomRegen = (totalWisdom / 5)
        
        if (state.currentPlayerSnapshot.activeKingdomLawId == 3) {
            wisdomRegen = (wisdomRegen * 0.85).toInt() 
        }
        
        state.currentPlayerSnapshot.activeBuffs.find { it.type == BuffType.ManaRegen }?.let {
            wisdomRegen = (wisdomRegen * it.value).toInt()
        }
        
        if (state.currentPlayerSnapshot.unlockedTraits.contains("Will of the Ancients")) {
            wisdomRegen += 50
        }

        val nextManaWithRegen = minOf(state.playerMana + wisdomRegen, state.currentPlayerSnapshot.maxMana)

        // Mana Validation (Strict: return original state if fails)
        if (nextManaWithRegen < skill.manaCost && skill.name != "None") {
            newLogs.add(LogEntry("Not enough Mana for ${skill.name}!", Color.Red))
            return PlayerTurnResult(state.copy(isProcessing = false), newLogs, false)
        }

        // --- COMMIT CHANGES ---
        val workingCooldowns = state.skillCooldowns.toMutableMap()
        workingCooldowns.keys.forEach { name ->
            val cd = workingCooldowns[name] ?: 0
            if (cd > 0) workingCooldowns[name] = cd - 1
        }

        if (nextManaWithRegen > state.playerMana) {
            newLogs.add(LogEntry("Regenerated ${nextManaWithRegen - state.playerMana} Mana.", Color.Cyan))
        }

        var hpRegen = 0
        if (state.currentPlayerSnapshot.unlockedTraits.contains("Blood of the Gods")) hpRegen += 10
        if (state.currentPlayerSnapshot.unlockedTraits.contains("Eyes of the Creator")) hpRegen += 20
        if (state.currentPlayerSnapshot.unlockedTraits.contains("Will of the Ancients")) hpRegen += 50
        
        if (state.currentPlayerSnapshot.activeBuffs.any { it.type == BuffType.HealthRegen }) {
            hpRegen += (state.currentPlayerSnapshot.maxHp * 0.05).toInt()
        }

        val nextHpWithRegen = minOf(state.playerHp + hpRegen, state.currentPlayerSnapshot.maxHp)
        if (nextHpWithRegen > state.playerHp) {
            newLogs.add(LogEntry("Regenerated ${nextHpWithRegen - state.playerHp} HP.", Color.Green))
        }

        var currentState = state.copy(
            parryActive = false, 
            skillCooldowns = workingCooldowns,
            playerDefenseDebuffTurns = maxOf(0, state.playerDefenseDebuffTurns - 1),
            playerMana = nextManaWithRegen,
            playerHp = nextHpWithRegen,
        )

        // 2. CHECK STUN / FREEZE
        if (state.playerStunnedTurns > 0) {
            newLogs.add(LogEntry("$pName is stunned and cannot move!", Color.Red))
            return PlayerTurnResult(currentState.copy(playerStunnedTurns = state.playerStunnedTurns - 1), newLogs, true)
        }
        if (state.playerFrozenTurns > 0) {
            newLogs.add(LogEntry("$pName is frozen in ice and cannot move!", Color.Cyan))
            return PlayerTurnResult(currentState.copy(playerFrozenTurns = state.playerFrozenTurns - 1), newLogs, true)
        }

        // 3. DEFENSE CALCULATION
        var bossDef = if (isBossBattle) {
            BossDatabase.resolveBoss(state.enemy.baseName)?.defense ?: (state.enemy.level * 5)
        } else 0
        
        if (state.bossShieldTurns > 0) {
            bossDef = (bossDef * 1.5).toInt() + 10
        }

        // 4. APPLY BUFFS / STANCES
        var activeDamageMultiplier = currentState.damageBuffMultiplier
        var activeDefenseMultiplier = currentState.defenseBuffMultiplier
        var activeEvasionBonus = currentState.evasionBuffBonus
        
        var attackBuffTurns = state.attackBuffTurns
        var defenseBuffTurns = state.defenseBuffTurns
        var evasionBuffTurns = state.evasionBuffTurns
        var critBuffTurns = state.critBuffTurns
        var superBuffTurns = state.superBuffTurns
        var parryActive = false 

        when (skill.effectType) {
            "Buff", "DamageBuff" -> {
                activeDamageMultiplier = 1.25
                attackBuffTurns = 3
                newLogs.add(LogEntry("$pName uses ${skill.name}! Attack increased!", Color.Green))
            }
            "DefenseBuff" -> {
                activeDefenseMultiplier = 1.5
                defenseBuffTurns = 3
                newLogs.add(LogEntry("$pName uses ${skill.name}! Defense increased!", Color.Green))
            }
            "EvasionBuff" -> {
                activeEvasionBonus = 0.25
                evasionBuffTurns = 3
                newLogs.add(LogEntry("$pName uses ${skill.name}! Evasion increased!", Color.Green))
            }
            "CritBuff" -> {
                critBuffTurns = 3
                newLogs.add(LogEntry("$pName uses ${skill.name}! Critical chance increased!", Color.Green))
            }
            "SuperBuff" -> {
                activeDamageMultiplier = 1.5
                superBuffTurns = 5
                newLogs.add(LogEntry("$pName uses ${skill.name}! Entering a state of high power!", Color.Green))
            }
            "AgilityBuff" -> {
                activeEvasionBonus = 0.20
                evasionBuffTurns = 3
                critBuffTurns = 3
                newLogs.add(LogEntry("$pName uses ${skill.name}! Agility increased!", Color.Green))
            }
            "Parry" -> {
                parryActive = true
                newLogs.add(LogEntry("$pName assumes a parry stance!", Color.Green))
            }
        }

        // Combine Active Multiplier with Passive Multipliers for the turn execution
        var totalTurnDamageMultiplier = activeDamageMultiplier
        state.currentPlayerSnapshot.activeBuffs.filter { it.type == BuffType.Damage }.forEach {
            totalTurnDamageMultiplier *= it.value
        }

        val bonusCrit = if (critBuffTurns > 0) 0.35 else 0.0

        // 5. EXECUTE LOGIC (Purely Player action)
        val skillLevel = state.currentPlayerSnapshot.skillLevels[skill.name] ?: 1
        val leveledSkill = SkillDatabase.getSkillWithLevel(skill.name, skillLevel)

        val result = BattleLogic.processTurn(
            skill = leveledSkill,
            player = currentState.currentPlayerSnapshot,
            currentHp = currentState.playerHp,
            currentMana = currentState.playerMana,
            currentEnemyHp = if (currentState.enemyHp > 0) currentState.enemyHp else currentState.enemy2Hp,
            enemy = if (currentState.enemyHp > 0) currentState.enemy else currentState.enemy2 ?: currentState.enemy,
            targetDefense = bossDef,
            playerDamageBuff = totalTurnDamageMultiplier,
            bonusCritChance = bonusCrit,
            previousSkillName = currentState.lastSkillName,
            enemyEvasionBonus = currentState.enemyEvasionBonus,
        )
        
        // --- 5.1 SECONDARY ENEMY LOGIC (AoE) ---
        var enemy2Hp = state.enemy2Hp
        val enemy2Logs = mutableListOf<LogEntry>()
        
        val isAoE = (skill.effectType == "MultiHit") || listOf("Whirlwind", "Cyclone", "Rain", "Comet").any { skill.name.contains(it) }
        val mainTargetWasEnemy1 = state.enemyHp > 0
        
        if (isAoE && (state.enemy2 != null) && (state.enemy2Hp > 0) && mainTargetWasEnemy1) {
            val result2 = BattleLogic.processTurn(
                skill = leveledSkill.copy(multiplier = leveledSkill.multiplier * 0.7), // Reduced dmg for secondary target
                player = state.currentPlayerSnapshot,
                currentHp = state.playerHp,
                currentMana = leveledSkill.manaCost, // Pass current mana so it doesn't fail, but we don't subtract twice
                currentEnemyHp = state.enemy2Hp,
                enemy = state.enemy2,
                targetDefense = 0, // Mobs don't have defense yet in simple logic
                playerDamageBuff = totalTurnDamageMultiplier,
                bonusCritChance = bonusCrit,
                previousSkillName = state.lastSkillName,
                enemyEvasionBonus = currentState.enemyEvasionBonus,
            )
            enemy2Hp = result2.enemyHp
            if (result2.playerDamage > 0) {
                enemy2Logs.add(LogEntry("${state.enemy2.name} was also hit for ${result2.playerDamage} damage!", Color.Cyan))
            }
        }

        // 5.5 SKILL MASTERY & EVOLUTION
        val workingSkillExp = state.currentPlayerSnapshot.skillExp.toMutableMap()
        val workingSkillLevels = state.currentPlayerSnapshot.skillLevels.toMutableMap()
        val workingUnlockedSkills = state.currentPlayerSnapshot.unlockedSkills.toMutableList()
        val workingEquippedSkills = state.currentPlayerSnapshot.equippedSkills.toMutableList()

        var evolvedSkillName: String? = null
        if (skill.name != "None" && !state.isStoryReplay) {
            val currentExp = workingSkillExp[skill.name] ?: 0
            val currentLvl = workingSkillLevels[skill.name] ?: 1
            
            if (currentLvl < 3) {
                val newExp = currentExp + 1
                val needed = SkillDatabase.getExpForNextLevel(currentLvl)
                
                if (newExp >= needed) {
                    val nextLvl = currentLvl + 1
                    workingSkillLevels[skill.name] = nextLvl
                    workingSkillExp[skill.name] = 0
                    newLogs.add(LogEntry("SKILL LEVEL UP! ${skill.name} is now Level $nextLvl!", Color.Yellow))
                    
                    // Handle Evolution at Level 3
                    if (nextLvl == 3 && skill.evolvesTo != null) {
                        val evolvedName = skill.evolvesTo
                        evolvedSkillName = evolvedName
                        newLogs.add(LogEntry("SKILL EVOLVED! ${skill.name} has become $evolvedName!", Color.Cyan))
                        
                        // Replace skill in unlocked and equipped lists
                        workingUnlockedSkills.remove(skill.name)
                        if (!workingUnlockedSkills.contains(evolvedName)) {
                            workingUnlockedSkills.add(evolvedName)
                        }
                        
                        // Replace all instances in equipped skills
                        while (workingEquippedSkills.contains(skill.name)) {
                            val equippedIndex = workingEquippedSkills.indexOf(skill.name)
                            if (equippedIndex != -1) {
                                workingEquippedSkills[equippedIndex] = evolvedName
                            }
                        }
                    }
                } else {
                    workingSkillExp[skill.name] = newExp
                }
            }
        }

        if (result.comboTriggered) {
            newLogs.add(LogEntry("✧ COMBO TRIGGERED ✧", Color.Yellow))
        }

        if (result.elementalMultiplier > 1.1) {
            newLogs.add(LogEntry("It's super effective!", Color.Yellow))
        } else if (result.elementalMultiplier < 0.9) {
            newLogs.add(LogEntry("It's not very effective...", Color.Gray))
        }

        // Handle HP change from Heal/Lifesteal (ensure it never goes down during player turn)
        val activeTarget = if (mainTargetWasEnemy1) state.enemy else state.enemy2 ?: state.enemy
        
        var finalEnemy1Hp = if (mainTargetWasEnemy1) result.enemyHp else state.enemyHp
        var finalEnemy2Hp = if (!mainTargetWasEnemy1) result.enemyHp else enemy2Hp
        
        var playerHp = maxOf(state.playerHp, result.playerHp) 
        var playerMana = result.playerMana
        
        var bleedTurns = state.bleedTurns
        var enemyStunnedTurns = state.enemyStunnedTurns
        var enemyFrozenTurns = state.enemyFrozenTurns
        var burnTurns = state.burnTurns
        var poisonTurns = state.poisonTurns
        
        var enemy2BleedTurns = state.enemy2BleedTurns
        var enemy2StunnedTurns = state.enemy2StunnedTurns
        var enemy2FrozenTurns = state.enemy2FrozenTurns
        var enemy2BurnTurns = state.enemy2BurnTurns
        var enemy2PoisonTurns = state.enemy2PoisonTurns

        if (skill.name == "None") {
            val restRegen = (state.currentPlayerSnapshot.wisdom / 2).coerceAtLeast(5)
            playerMana = minOf(playerMana + restRegen, state.currentPlayerSnapshot.maxMana)
            newLogs.add(LogEntry("$pName rests and recovers $restRegen Mana.", Color.Cyan))
        } else if ((skill.skillType != "Support") && (skill.skillType != "Buff")) {
            newLogs.add(LogEntry("$pName used ${skill.name} for ${result.playerDamage} damage.", Color.Cyan))
            if (result.isCrit) {
                newLogs.add(LogEntry("CRITICAL HIT!", Color.Cyan))
            }
            
            // Reflected damage back to enemy
            if (result.reflectedDamage > 0) {
                newLogs.add(LogEntry("Reflected ${result.reflectedDamage} damage back to ${activeTarget.name}!", Color.Cyan))
            }

            // --- Enemy Passive: Physical Counter (Target Aware) ---
            if (activeTarget.passive == com.solerforge.lumeria.data.EnemyPassive.PHYSICAL_COUNTER && skill.elementType == ElementType.Physical && result.playerDamage > 0) {
                val counterDmg = (result.playerDamage * 0.1).toInt().coerceAtLeast(1)
                playerHp = maxOf(0, playerHp - counterDmg)
                newLogs.add(LogEntry("${activeTarget.name} counters for $counterDmg damage!", Color.Red))
            }

            // --- Status Effects for Primary Target (Target Aware) ---
            when (result.appliedStatus) {
                StatusEffect.Bleed -> {
                    if (mainTargetWasEnemy1) bleedTurns = 3 else enemy2BleedTurns = 3
                    newLogs.add(LogEntry("${activeTarget.name} is bleeding!", Color.Yellow))
                }
                StatusEffect.Stun -> {
                    newLogs.add(LogEntry("${activeTarget.name} was stunned!", Color.Yellow))
                    if (mainTargetWasEnemy1) enemyStunnedTurns = 1 else enemy2StunnedTurns = 1
                }
                StatusEffect.Freeze -> {
                    newLogs.add(LogEntry("${activeTarget.name} was frozen!", Color.Cyan))
                    if (mainTargetWasEnemy1) enemyFrozenTurns = 1 else enemy2FrozenTurns = 1
                }
                StatusEffect.Burn -> {
                    if (mainTargetWasEnemy1) burnTurns = 3 else enemy2BurnTurns = 3
                    newLogs.add(LogEntry("${activeTarget.name} caught on fire!", Color(0xFFFF5722)))
                }
                StatusEffect.Poison -> {
                    if (mainTargetWasEnemy1) poisonTurns = 4 else enemy2PoisonTurns = 4
                    newLogs.add(LogEntry("${activeTarget.name} was poisoned!", Color(0xFF4CAF50)))
                }
                else -> {}
            }

            // --- Status Effects for Enemy 2 (if AoE hit) ---
            if (isAoE && state.enemy2 != null && enemy2Hp < state.enemy2Hp) {
                // AoE has 25% chance to apply status to secondary target if it has one
                if ((skill.statusEffect != StatusEffect.None || skill.effectType == "Freeze") && (Math.random() < 0.25)) {
                    val effectToApply = if (skill.effectType == "Freeze") StatusEffect.Freeze else skill.statusEffect
                    when (effectToApply) {
                        StatusEffect.Bleed -> {
                            enemy2BleedTurns = 3
                            newLogs.add(LogEntry("${state.enemy2.name} is bleeding!", Color.Yellow))
                        }
                        StatusEffect.Stun -> {
                            enemy2StunnedTurns = 1
                            newLogs.add(LogEntry("${state.enemy2.name} was stunned!", Color.Yellow))
                        }
                        StatusEffect.Freeze -> {
                            enemy2FrozenTurns = 1
                            newLogs.add(LogEntry("${state.enemy2.name} was frozen!", Color.Cyan))
                        }
                        StatusEffect.Burn -> {
                            enemy2BurnTurns = 3
                            newLogs.add(LogEntry("${state.enemy2.name} caught on fire!", Color(0xFFFF5722)))
                        }
                        StatusEffect.Poison -> {
                            enemy2PoisonTurns = 4
                            newLogs.add(LogEntry("${state.enemy2.name} was poisoned!", Color(0xFF4CAF50)))
                        }
                        else -> {}
                    }
                }
            }
        } else if (skill.effectType == "Heal") {
            newLogs.add(LogEntry("$pName healed for ${result.actualHeal} HP.", Color.Green))
        }

        if (skill.cooldown > 0) {
            workingCooldowns[skill.name] = skill.cooldown
        }

        // --- 5.6 FAMILIAR ACTION ---
        var familiarActionCounter = state.familiarActionCounter + 1
        val familiar = FamiliarDatabase.getFamiliar(state.currentPlayerSnapshot.equippedFamiliar)
        var familiarActionTaken = false
        
        var enemyDamageMultiplier = state.enemyDamageMultiplier
        var enemyDamageDebuffTurns = state.enemyDamageDebuffTurns
        var enemyEvasionBonus = state.enemyEvasionBonus
        var enemyEvasionBuffTurns = state.enemyEvasionBuffTurns

        if (familiar.name != "None" && familiarActionCounter >= familiar.supportInterval) {
            familiarActionCounter = 0
            familiarActionTaken = true
            val familiarLevel = state.currentPlayerSnapshot.familiarLevels[familiar.name] ?: 1
            val power = FamiliarDatabase.getPowerForLevel(familiar.basePower, familiarLevel)
            
            newLogs.add(LogEntry("🐾 ${familiar.name} used its support action!", Color.Magenta))
            
            when (familiar.actionType) {
                FamiliarActionType.Damage -> {
                    if (finalEnemy1Hp > 0) {
                        finalEnemy1Hp = maxOf(0, finalEnemy1Hp - power)
                    } else if (finalEnemy2Hp > 0) {
                        finalEnemy2Hp = maxOf(0, finalEnemy2Hp - power)
                    }
                    newLogs.add(LogEntry("${familiar.name} dealt $power damage!", Color.Magenta))
                }
                FamiliarActionType.Heal -> {
                    playerHp = minOf(playerHp + power, state.currentPlayerSnapshot.maxHp)
                    newLogs.add(LogEntry("${familiar.name} healed for $power HP!", Color.Green))
                }
                FamiliarActionType.Mana -> {
                    playerMana = minOf(playerMana + power, state.currentPlayerSnapshot.maxMana)
                    newLogs.add(LogEntry("${familiar.name} restored $power Mana!", Color.Cyan))
                }
                FamiliarActionType.Buff -> {
                    activeDamageMultiplier *= 1.25
                    newLogs.add(LogEntry("${familiar.name} boosted your attack!", Color.Yellow))
                }
                FamiliarActionType.Debuff -> {
                    val debuffPercent = power / 100.0
                    enemyDamageMultiplier = (1.0 - debuffPercent).coerceAtLeast(0.1)
                    enemyDamageDebuffTurns = 2
                    newLogs.add(LogEntry("${familiar.name} used its debuff action! Enemy damage reduced by $power%.", Color.Magenta))
                }
            }
        }

        currentState = currentState.copy(
            enemyHp = finalEnemy1Hp,
            enemy2Hp = finalEnemy2Hp,
            playerHp = playerHp,
            playerMana = playerMana,
            familiarActionCounter = familiarActionCounter,
            currentPlayerSnapshot = currentState.currentPlayerSnapshot.copy(
                skillExp = workingSkillExp,
                skillLevels = workingSkillLevels,
                unlockedSkills = workingUnlockedSkills,
                equippedSkills = workingEquippedSkills
            ),
            damageBuffMultiplier = if (familiarActionTaken && familiar.actionType == FamiliarActionType.Buff) activeDamageMultiplier else (if ((attackBuffTurns > 0) || (superBuffTurns > 0)) activeDamageMultiplier else 1.0),
            defenseBuffMultiplier = if (defenseBuffTurns > 0) activeDefenseMultiplier else 1.0,
            evasionBuffBonus = if (evasionBuffTurns > 0) activeEvasionBonus else 0.0,
            attackBuffTurns = maxOf(0, attackBuffTurns - 1),
            defenseBuffTurns = maxOf(0, defenseBuffTurns - 1),
            evasionBuffTurns = maxOf(0, evasionBuffTurns - 1),
            critBuffTurns = maxOf(0, critBuffTurns - 1),
            superBuffTurns = maxOf(0, superBuffTurns - 1),
            parryActive = parryActive,
            bleedTurns = bleedTurns,
            enemy2BleedTurns = enemy2BleedTurns,
            enemyStunnedTurns = enemyStunnedTurns,
            enemy2StunnedTurns = enemy2StunnedTurns,
            enemyFrozenTurns = enemyFrozenTurns,
            enemy2FrozenTurns = enemy2FrozenTurns,
            burnTurns = burnTurns,
            enemy2BurnTurns = enemy2BurnTurns,
            poisonTurns = poisonTurns,
            enemy2PoisonTurns = enemy2PoisonTurns,
            enemyDamageMultiplier = enemyDamageMultiplier,
            enemyDamageDebuffTurns = enemyDamageDebuffTurns,
            enemyEvasionBonus = enemyEvasionBonus,
            enemyEvasionBuffTurns = enemyEvasionBuffTurns,
            lastSkillName = skill.name,
            skillCooldowns = workingCooldowns,
            evolvedSkillName = evolvedSkillName,
            battleMessage = if (result.isCrit) "CRITICAL HIT!" else "",
        )

        // Check for enemy sounds/speech after damage
        val (chatState, chatLogs) = checkEnemyChat(currentState, storyEnemyName, mainTargetWasEnemy1)
        currentState = chatState
        newLogs.addAll(chatLogs)
        newLogs.addAll(enemy2Logs)

        return PlayerTurnResult(currentState, newLogs, true)
    }

    fun processEnemyTurn(
        state: BattleUiState,
        isBossBattle: Boolean,
    ): Pair<BattleUiState, List<LogEntry>> {
        val newLogs = mutableListOf<LogEntry>()
        val pName = state.currentPlayerSnapshot.playerName
        
        // 1. Start of Turn Housekeeping (Decrement enemy cooldowns and buffs)
        val workingCooldowns = state.enemySkillCooldowns.toMutableMap()
        workingCooldowns.keys.forEach { name ->
            val cd = workingCooldowns[name] ?: 0
            if (cd > 0) workingCooldowns[name] = cd - 1
        }
        
        var currentState = state.copy(
            enemySkillCooldowns = workingCooldowns,
            bossEnrageTurns = maxOf(0, state.bossEnrageTurns - 1),
            bossShieldTurns = maxOf(0, state.bossShieldTurns - 1),
            // Timing fixed: decrements happen at end of turn
        )

        // --- PASSIVE STATUS DAMAGE (Burn, Poison, Bleed) ---
        val (passiveState, passiveLogs) = applyPassiveDamage(currentState)
        currentState = passiveState
        newLogs.addAll(passiveLogs)

        // 2. Check if all enemies are dead after passive damage
        if (currentState.enemyHp <= 0 && (currentState.enemy2Hp <= 0)) return Pair(currentState, newLogs)

        // --- ENEMY 1 ACTION ---
        if (currentState.enemyHp > 0) {
            // Check if stunned or frozen
            if (currentState.enemyStunnedTurns > 0) {
                newLogs.add(LogEntry("${currentState.enemy.name} is stunned!", Color.Yellow))
                currentState = currentState.copy(enemyStunnedTurns = currentState.enemyStunnedTurns - 1)
            } else if (currentState.enemyFrozenTurns > 0) {
                newLogs.add(LogEntry("${currentState.enemy.name} is frozen in ice!", Color.Cyan))
                currentState = currentState.copy(enemyFrozenTurns = currentState.enemyFrozenTurns - 1)
            } else {
                val (turnState, turnLogs) = when {
                    currentState.isArenaBattle -> processArenaAction(currentState)
                    isBossBattle -> processBossAction(currentState)
                    else -> processNormalEnemyAction(currentState)
                }
                currentState = turnState
                newLogs.addAll(turnLogs)
            }
        }

        // --- ENEMY 2 ACTION ---
        if (state.enemy2 != null && currentState.enemy2Hp > 0 && currentState.playerHp > 0) {
            // Check if stunned or frozen
            if (state.enemy2StunnedTurns > 0) {
                newLogs.add(LogEntry("${state.enemy2.name} is stunned!", Color.Yellow))
                currentState = currentState.copy(enemy2StunnedTurns = state.enemy2StunnedTurns - 1)
            } else if (state.enemy2FrozenTurns > 0) {
                newLogs.add(LogEntry("${state.enemy2.name} is frozen in ice!", Color.Cyan))
                currentState = currentState.copy(enemy2FrozenTurns = state.enemy2FrozenTurns - 1)
            } else {
                // Centralized dodge calculation
                val dodgeChance = BattleLogic.calculateDodgeChance(currentState.currentPlayerSnapshot, currentState.evasionBuffBonus)
                val isDodge = Math.random() < dodgeChance
                
                if (isDodge) {
                    newLogs.add(LogEntry("$pName dodged the ${currentState.enemy2?.name ?: "enemy"}'s attack!", Color.Cyan))
                } else {
                    val enemyDmgDealt = calculateIncomingEnemyDamage(
                        state = currentState,
                        enemyLevel = currentState.enemy2?.level ?: 1,
                        enemyName = currentState.enemy2?.name ?: "enemy",
                        isBoss = false
                    )
                    
                    val playerHp = maxOf(0, currentState.playerHp - enemyDmgDealt.toInt())
                    currentState = currentState.copy(playerHp = playerHp)
                    newLogs.add(LogEntry("${currentState.enemy2?.name ?: "Enemy"} attacks for $enemyDmgDealt damage.", Color.Red))
                }
            }
        }

        // --- END OF TURN HOUSEKEEPING (Decrement buffs) ---
        currentState = currentState.copy(
            enemyDamageDebuffTurns = maxOf(0, currentState.enemyDamageDebuffTurns - 1),
            enemyEvasionBuffTurns = maxOf(0, currentState.enemyEvasionBuffTurns - 1)
        )
        if (currentState.enemyDamageDebuffTurns == 0) {
            currentState = currentState.copy(enemyDamageMultiplier = 1.0)
        }
        if (currentState.enemyEvasionBuffTurns == 0) {
            currentState = currentState.copy(enemyEvasionBonus = 0.0)
        }
        
        return Pair(currentState, newLogs)
    }

    private fun checkEnemyChat(state: BattleUiState, storyEnemyName: String?, isEnemy1: Boolean): Pair<BattleUiState, List<LogEntry>> {
        // Skip story mobs as requested (they have their own logic or we want standard ones here)
        if (storyEnemyName != null) return Pair(state, emptyList())

        val target = if (isEnemy1) state.enemy else state.enemy2 ?: return Pair(state, emptyList())
        val currentHp = if (isEnemy1) state.enemyHp else state.enemy2Hp
        val hpPercent = currentHp.toFloat() / target.maxHp
        
        // Thresholds tracking is currently shared, which is okay for now but ideally separate.
        // We'll prefix the threshold key with slot to separate them.
        val thresholds = listOf(0.75f, 0.50f, 0.25f)
        
        val hitThreshold = thresholds.find { (hpPercent <= it) && (!state.chatThresholdsHit.contains(it)) }

        if (hitThreshold != null && isEnemy1) { // Only main enemy chats for now to maintain state compatibility
            val message = if (target.isHumanoid) {
                EnemyChatDatabase.getHumanoidQuote(target.name, hpPercent)
            } else {
                EnemyChatDatabase.getMonsterSound(target.name, hpPercent)
            }

            if (message != null) {
                val updatedThresholds = state.chatThresholdsHit + hitThreshold
                val color = if (state.enemy.isHumanoid) Color.Yellow else Color.Gray
                return Pair(
                    state.copy(chatThresholdsHit = updatedThresholds),
                    listOf(LogEntry("${state.enemy.name}: $message", color))
                )
            }
        }
        return Pair(state, emptyList())
    }

    private fun applyPassiveDamage(state: BattleUiState): Pair<BattleUiState, List<LogEntry>> {
        val newLogs = mutableListOf<LogEntry>()
        val pName = state.currentPlayerSnapshot.playerName
        var playerHp = state.playerHp
        var enemyHp = state.enemyHp
        var enemy2Hp = state.enemy2Hp
        
        if (state.burnTurns > 0) {
            val burnDmg = (state.enemy.maxHp * 0.05).toInt().coerceAtLeast(1)
            enemyHp = maxOf(0, enemyHp - burnDmg)
            newLogs.add(LogEntry("Burn damage: $burnDmg to ${state.enemy.name}", Color(0xFFFF5722)))
        }
        if (state.playerPoisonTurns > 0) {
            val poisonDmg = (state.currentPlayerSnapshot.maxHp * 0.04).toInt().coerceAtLeast(1)
            playerHp = maxOf(0, playerHp - poisonDmg)
            newLogs.add(LogEntry("Poison damage: $poisonDmg to $pName", Color(0xFF4CAF50)))
        }
        if (state.poisonTurns > 0) {
            val poisonDmg = (state.enemy.maxHp * 0.04).toInt().coerceAtLeast(1)
            enemyHp = maxOf(0, enemyHp - poisonDmg)
            newLogs.add(LogEntry("Poison damage: $poisonDmg to ${state.enemy.name}", Color(0xFF4CAF50)))
        }
        if (state.bleedTurns > 0) {
            val bleedDmg = (state.enemy.maxHp * 0.06).toInt().coerceAtLeast(1)
            enemyHp = maxOf(0, enemyHp - bleedDmg)
            newLogs.add(LogEntry("Bleed damage: $bleedDmg to ${state.enemy.name}", Color.Yellow))
        }
        
        state.enemy2?.let { e2 ->
            if (state.enemy2BurnTurns > 0) {
                val burnDmg = (e2.maxHp * 0.05).toInt().coerceAtLeast(1)
                enemy2Hp = maxOf(0, enemy2Hp - burnDmg)
                newLogs.add(LogEntry("Burn damage: $burnDmg to ${e2.name}", Color(0xFFFF5722)))
            }
            if (state.enemy2PoisonTurns > 0) {
                val poisonDmg = (e2.maxHp * 0.04).toInt().coerceAtLeast(1)
                enemy2Hp = maxOf(0, enemy2Hp - poisonDmg)
                newLogs.add(LogEntry("Poison damage: $poisonDmg to ${e2.name}", Color(0xFF4CAF50)))
            }
            if (state.enemy2BleedTurns > 0) {
                val bleedDmg = (e2.maxHp * 0.06).toInt().coerceAtLeast(1)
                enemy2Hp = maxOf(0, enemy2Hp - bleedDmg)
                newLogs.add(LogEntry("Bleed damage: $bleedDmg to ${e2.name}", Color.Yellow))
            }
        }
        
        return Pair(
            state.copy(
                playerHp = playerHp,
                enemyHp = enemyHp,
                enemy2Hp = enemy2Hp,
                burnTurns = maxOf(0, state.burnTurns - 1),
                enemy2BurnTurns = maxOf(0, state.enemy2BurnTurns - 1),
                poisonTurns = maxOf(0, state.poisonTurns - 1),
                playerPoisonTurns = maxOf(0, state.playerPoisonTurns - 1),
                enemy2PoisonTurns = maxOf(0, state.enemy2PoisonTurns - 1),
                bleedTurns = maxOf(0, state.bleedTurns - 1),
                enemy2BleedTurns = maxOf(0, state.enemy2BleedTurns - 1),
            ),
            newLogs
        )
    }

    private fun processNormalEnemyAction(state: BattleUiState): Pair<BattleUiState, List<LogEntry>> {
        val newLogs = mutableListOf<LogEntry>()
        val pName = state.currentPlayerSnapshot.playerName
        
        // Centralized dodge calculation
        val dodgeChance = BattleLogic.calculateDodgeChance(state.currentPlayerSnapshot, state.evasionBuffBonus)
        val isDodge = Math.random() < dodgeChance
        
        if (isDodge) {
            newLogs.add(LogEntry("$pName dodged the ${state.enemy.name}'s attack!", Color.Cyan))
            return Pair(
                state.copy(battleMessage = "DODGED!"),
                newLogs
            )
        }

        // Apply Active Skill Defense Buff
        val enemyDmgDealt = calculateIncomingEnemyDamage(
            state = state,
            enemyLevel = state.enemy.level,
            enemyName = state.enemy.name,
            isBoss = false
        )
        
        var playerHp = maxOf(0, state.playerHp - enemyDmgDealt.toInt())
        var enemyHp = state.enemyHp

        if (state.parryActive && state.lastSkillName == "Absolute Counter") {
            val reflected = (enemyDmgDealt * 0.75).toLong()
            enemyHp = maxOf(0, enemyHp - reflected.toInt())
            newLogs.add(LogEntry("ABSOLUTE COUNTER! Reflected $reflected damage!", Color.Cyan))
        }
        
        var secondWindUsed = state.secondWindUsed
        if (playerHp <= 0 && !secondWindUsed && state.currentPlayerSnapshot.equippedArmor == "Mantle of Perseverance") {
            playerHp = 1
            secondWindUsed = true
            newLogs.add(LogEntry("SECOND WIND! $pName survived a lethal blow!", Color.Cyan))
        }
        
        newLogs.add(LogEntry("${state.enemy.name} attacks for $enemyDmgDealt damage.", Color.Red))
        
        // --- Enemy Passive: Poison Touch ---
        var playerPoisonTurns = state.playerPoisonTurns
        if (state.enemy.passive == com.solerforge.lumeria.data.EnemyPassive.POISON_TOUCH && enemyDmgDealt > 0 && !isDodge) {
            playerPoisonTurns = 3
            newLogs.add(LogEntry("$pName was poisoned by ${state.enemy.name}!", Color(0xFF4CAF50)))
        }
        
        return Pair(
            state.copy(playerHp = playerHp, enemyHp = enemyHp, secondWindUsed = secondWindUsed, playerPoisonTurns = playerPoisonTurns),
            newLogs
        )
    }

    private fun processArenaAction(state: BattleUiState): Pair<BattleUiState, List<LogEntry>> {
        val newLogs = mutableListOf<LogEntry>()
        val moveset = state.arenaMoveset
        
        if (moveset.isEmpty()) return processNormalEnemyAction(state)

        // SMART MOVE SELECTION
        val attack = selectSmartMove(state, moveset)
        newLogs.add(LogEntry("${state.enemy.name} uses ${attack.name}!", Color.Red))

        // Update enemy cooldowns
        val updatedCooldowns = state.enemySkillCooldowns.toMutableMap()
        if (attack.cooldown > 0) {
            updatedCooldowns[attack.name] = attack.cooldown
        }

        val (newState, attackLogs) = applyEnemyAttack(state, attack, newLogs)
        return Pair(newState.copy(enemySkillCooldowns = updatedCooldowns), attackLogs)
    }

    private fun processBossAction(state: BattleUiState): Pair<BattleUiState, List<LogEntry>> {
        val newLogs = mutableListOf<LogEntry>()
        val bossData = BossDatabase.resolveBoss(state.enemy.baseName)
        
        if (bossData == null) {
            return processNormalEnemyAction(state)
        }
        
        val hpPercent = (state.enemyHp.toFloat() / state.enemy.maxHp)

        var phase2Triggered = state.phase2Triggered
        val phase3Triggered = state.phase3Triggered
        var bossEnrageTurns = state.bossEnrageTurns
        val bossShieldTurns = state.bossShieldTurns

        // 1. PHASE TRANSITIONS
        if ((state.enemy.name == "Crystal Guardian") && (hpPercent < 0.5f) && (!phase2Triggered)) {
            phase2Triggered = true
            bossEnrageTurns = 99
            newLogs.add(LogEntry("The crystals begin to resonate violently! (+25% DMG)", Color.Yellow))
        }

        // 2. SMART MOVE SELECTION
        val attack = selectSmartMove(state, bossData.moveset)
        newLogs.add(LogEntry("${state.enemy.name} uses ${attack.name}!", Color.Red))

        // Update enemy cooldowns
        val updatedCooldowns = state.enemySkillCooldowns.toMutableMap()
        if (attack.cooldown > 0) {
            updatedCooldowns[attack.name] = attack.cooldown
        }

        val (newState, attackLogs) = applyEnemyAttack(state, attack, newLogs, phase2Triggered, phase3Triggered, bossEnrageTurns, bossShieldTurns)
        return Pair(newState.copy(enemySkillCooldowns = updatedCooldowns), attackLogs)
    }

    private fun selectSmartMove(state: BattleUiState, moveset: List<BossAttack>): BossAttack {
        val availableMoves = moveset.filter { (state.enemySkillCooldowns[it.name] ?: 0) == 0 }
        if (availableMoves.isEmpty()) return moveset.first() // Fallback

        val bossHpPercent = state.enemyHp.toFloat() / state.enemy.maxHp
        val playerHpPercent = state.playerHp.toFloat() / state.currentPlayerSnapshot.maxHp

        // 1. HEAL logic: If Boss < 35% HP, prioritize healing
        val healMove = availableMoves.find { it.effectType == "Heal" }
        if (bossHpPercent < 0.35 && healMove != null) return healMove

        // 2. FINISHER logic: If Player < 25% HP, use the strongest move
        if (playerHpPercent < 0.25) {
            availableMoves.maxByOrNull { it.multiplier }?.let { return it }
        }

        // 3. STUN logic: If Player is NOT stunned, try to stun them
        if (state.playerStunnedTurns == 0) {
            availableMoves.find { it.effectType == "Stun" }?.let { return it }
        }

        // 4. BUFF logic: If Boss is NOT buffed, prioritize it
        val buffMove = availableMoves.find { it.effectType == "Buff" }
        if ((state.bossEnrageTurns == 0) && (buffMove != null)) return buffMove

        // 5. DEFAULT: Random selection from available moves
        return availableMoves.random()
    }

    private fun applyEnemyAttack(
        state: BattleUiState,
        attack: BossAttack,
        newLogs: MutableList<LogEntry>,
        phase2: Boolean = false,
        phase3: Boolean = false,
        enrageTurns: Int = 0,
        shieldTurns: Int = 0
    ): Pair<BattleUiState, List<LogEntry>> {
        val pName = state.currentPlayerSnapshot.playerName
        // Centralized dodge calculation
        val player = state.currentPlayerSnapshot
        val dodgeChance = BattleLogic.calculateDodgeChance(player, state.evasionBuffBonus)
        
        val selfTargetEffects = setOf("Heal", "Buff", "DamageBuff", "DefenseBuff", "EvasionBuff", "Shield")
        val isSelfTarget = attack.effectType in selfTargetEffects
        val isDodge = if (isSelfTarget) false else Math.random() < dodgeChance
        
        if (isDodge) {
            newLogs.add(LogEntry("$pName dodged the ${state.enemy.name}'s attack!", Color.Cyan))
            return Pair(
                state.copy(battleMessage = "DODGED!"),
                newLogs
            )
        }

        var playerHp = state.playerHp
        var enemyHp = state.enemyHp
        var playerStunnedTurns = state.playerStunnedTurns
        var playerDefenseDebuffTurns = state.playerDefenseDebuffTurns
        var currentEnrageTurns = enrageTurns
        var currentShieldTurns = shieldTurns
        var enemyEvasionBonus = state.enemyEvasionBonus
        var enemyEvasionBuffTurns = state.enemyEvasionBuffTurns

        // Apply Active Skill Defense Buff
        val baseDmg = calculateIncomingEnemyDamage(
            state = state,
            enemyLevel = state.enemy.level,
            enemyName = state.enemy.name,
            isBoss = true,
            multiplier = if (currentEnrageTurns > 0) attack.multiplier * 1.25 else attack.multiplier,
            ignoreArmor = attack.effectType == "IgnoreArmor"
        )

        if (state.parryActive && state.lastSkillName == "Absolute Counter" && baseDmg > 0) {
            val reflected = (baseDmg * 0.75).toLong()
            enemyHp = maxOf(0, enemyHp - reflected.toInt())
            newLogs.add(LogEntry("ABSOLUTE COUNTER! Reflected $reflected damage!", Color.Cyan))
        }

        if (baseDmg > 0) {
            newLogs.add(LogEntry("${state.enemy.name} deals $baseDmg damage.", Color.Red))
        }

        // --- Enemy Passive: Poison Touch (Boss/Arena) ---
        var playerPoisonTurns = state.playerPoisonTurns
        if (state.enemy.passive == com.solerforge.lumeria.data.EnemyPassive.POISON_TOUCH && baseDmg > 0 && !isDodge) {
            playerPoisonTurns = 3
            newLogs.add(LogEntry("$pName was poisoned by ${state.enemy.name}!", Color(0xFF4CAF50)))
        }

        // 4. APPLY EFFECTS
        val hasAbsoluteResistance = player.unlockedTraits.contains("Will of the Ancients")

        when (attack.effectType) {
            "Stun" -> {
                if (Math.random() < 0.25 && !hasAbsoluteResistance) {
                    playerStunnedTurns = 1
                    newLogs.add(LogEntry("$pName was stunned by the impact!", Color.Yellow))
                } else if (hasAbsoluteResistance) {
                    newLogs.add(LogEntry("Will of the Ancients resists the stun!", Color.Cyan))
                }
                playerHp = maxOf(0, playerHp - baseDmg.toInt())
            }
            "Heal" -> {
                val heal = (state.enemy.maxHp * 0.10).toInt()
                enemyHp = minOf(enemyHp + heal, state.enemy.maxHp)
                newLogs.add(LogEntry("${state.enemy.name} regains composure and heals for $heal HP!", Color.Yellow))
                playerHp = maxOf(0, playerHp - baseDmg.toInt())
            }
            "DefenseDown" -> {
                if (!hasAbsoluteResistance) {
                    playerDefenseDebuffTurns = 4
                    newLogs.add(LogEntry("$pName's defense was lowered!", Color.Yellow))
                } else {
                    newLogs.add(LogEntry("Will of the Ancients resists the debuff!", Color.Cyan))
                }
                playerHp = maxOf(0, playerHp - baseDmg.toInt())
            }
            "Lifesteal" -> {
                playerHp = maxOf(0, playerHp - baseDmg.toInt())
                val drain = (baseDmg * 0.5).toLong()
                enemyHp = minOf(enemyHp + drain.toInt(), state.enemy.maxHp)
                newLogs.add(LogEntry("${state.enemy.name} drained $drain HP!", Color.Red))
            }
            "MultiHit" -> {
                var totalEnemyDmg = 0L
                repeat(attack.hitCount) {
                    if (playerHp > 0) {
                        val hitDmg: Long = if (it == 0) baseDmg else maxOf(1L, (baseDmg * 0.5).toLong())
                        playerHp = maxOf(0, playerHp - hitDmg.toInt())
                        totalEnemyDmg += hitDmg
                    }
                }
                newLogs.add(LogEntry("${state.enemy.name} strikes ${attack.hitCount} times for $totalEnemyDmg damage!", Color.Red))
            }
            "Buff", "DamageBuff" -> {
                currentEnrageTurns = 3
                newLogs.add(LogEntry("${state.enemy.name} is enraged! (+25% DMG)", Color.Yellow))
                playerHp = maxOf(0, playerHp - baseDmg.toInt())
            }
            "EvasionBuff" -> {
                enemyEvasionBonus = 0.20
                enemyEvasionBuffTurns = 3
                newLogs.add(LogEntry("${state.enemy.name} is shrouded in mist! Evasion increased!", Color.Cyan))
                playerHp = maxOf(0, playerHp - baseDmg.toInt())
            }
            "Shield", "DefenseBuff" -> {
                currentShieldTurns = 3
                newLogs.add(LogEntry("${state.enemy.name} raises a defensive barrier!", Color.Cyan))
                playerHp = maxOf(0, playerHp - baseDmg.toInt())
            }
            else -> {
                playerHp = maxOf(0, playerHp - baseDmg.toInt())
            }
        }

        var secondWindUsed = state.secondWindUsed
        if (playerHp <= 0 && !secondWindUsed && state.currentPlayerSnapshot.equippedArmor == "Mantle of Perseverance") {
            playerHp = 1
            secondWindUsed = true
            newLogs.add(LogEntry("SECOND WIND! $pName survived a lethal blow!", Color.Cyan))
        }

        return Pair(
            state.copy(
                playerHp = playerHp,
                enemyHp = enemyHp,
                phase2Triggered = phase2,
                phase3Triggered = phase3,
                bossEnrageTurns = currentEnrageTurns,
                bossShieldTurns = currentShieldTurns,
                playerStunnedTurns = playerStunnedTurns,
                playerDefenseDebuffTurns = playerDefenseDebuffTurns,
                secondWindUsed = secondWindUsed,
                enemyEvasionBonus = enemyEvasionBonus,
                enemyEvasionBuffTurns = enemyEvasionBuffTurns,
                playerPoisonTurns = playerPoisonTurns
            ),
            newLogs
        )
    }

    private fun calculateIncomingEnemyDamage(
        state: BattleUiState,
        enemyLevel: Int,
        enemyName: String,
        isBoss: Boolean,
        multiplier: Double = 1.0,
        ignoreArmor: Boolean = false
    ): Long {
        val armorDef = GameDatabase.getArmor(state.currentPlayerSnapshot.equippedArmor).defense
        val headDef = GameDatabase.getHeadGear(state.currentPlayerSnapshot.equippedHead).defense
        val shieldDef = GameDatabase.getShield(state.currentPlayerSnapshot.equippedShield).defense
        val offHand1 = GameDatabase.getOffHand(state.currentPlayerSnapshot.equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(state.currentPlayerSnapshot.equippedOffHand2)
        val itemDef = offHand1.defense + offHand2.defense
        
        val traitBonusDef = state.currentPlayerSnapshot.unlockedTraits.sumOf { TraitDatabase.getTrait(it)?.vitBonus ?: 0 }
        var totalDef = state.currentPlayerSnapshot.defense + armorDef + headDef + shieldDef + itemDef + traitBonusDef

        // Apply Passive Defense Buffs from Food/Events
        state.currentPlayerSnapshot.activeBuffs.find { it.type == com.solerforge.lumeria.models.BuffType.Defense }?.let {
            totalDef = (totalDef * it.value).toInt()
        }

        // Apply Active Skill Defense Buff
        totalDef = (totalDef * state.defenseBuffMultiplier).toInt()

        val effectiveDef = if (state.playerDefenseDebuffTurns > 0) (totalDef * 0.8).toInt() else totalDef
        
        var baseDmg = if (isBoss) {
            BattleLogic.calculateBossDamage(enemyLevel, effectiveDef, multiplier, ignoreArmor)
        } else {
            BattleLogic.calculateEnemyDamage(enemyName, enemyLevel, effectiveDef, multiplier, ignoreArmor)
        }
        
        // --- Enemy Passive: Desperation ---
        if (state.enemy.passive == com.solerforge.lumeria.data.EnemyPassive.DESPERATION) {
            val hpPercent = state.enemyHp.toFloat() / state.enemy.maxHp
            if (hpPercent < 0.3) {
                baseDmg = (baseDmg * 1.5).toLong()
            }
        }
        
        baseDmg = (baseDmg * state.enemyDamageMultiplier).toLong()

        // --- KINGDOM LAWS ---
        when (state.currentPlayerSnapshot.activeKingdomLawId) {
            2 -> baseDmg = (baseDmg * 1.15).toLong() // Warrior's Draft: +15% Enemy Dmg
            3 -> {
                if (isBoss && (enemyName.contains("Void") || enemyName.contains("Xarthos") || enemyName.contains("Umbra"))) {
                    baseDmg = (baseDmg * 0.8).toLong()
                }
            }
        }

        if (state.parryActive) baseDmg /= 2
        
        return if (multiplier <= 0.0) 0L else maxOf(1L, baseDmg)
    }
}
