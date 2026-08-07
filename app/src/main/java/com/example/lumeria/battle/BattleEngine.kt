package com.example.lumeria.battle

import androidx.compose.ui.graphics.Color
import com.example.lumeria.database.*
import com.example.lumeria.models.*

object BattleEngine {

    /**
     * Processes a single player turn.
     * Returns the updated state and a list of new logs.
     */
    fun processPlayerTurn(
        state: BattleUiState,
        skillName: String,
        storyEnemyName: String?,
        isBossBattle: Boolean,
    ): Pair<BattleUiState, List<LogEntry>> {
        val skill = SkillDatabase.getSkill(skillName)
        val newLogs = mutableListOf<LogEntry>()
        
        // 1. START OF TURN HOUSEKEEPING (Decrement all active cooldowns)
        val workingCooldowns = state.skillCooldowns.toMutableMap()
        workingCooldowns.keys.forEach { name ->
            val cd = workingCooldowns[name] ?: 0
            if (cd > 0) workingCooldowns[name] = cd - 1
        }

        var currentState = state.copy(
            parryActive = false, // Reset parry stance at start of turn
            skillCooldowns = workingCooldowns,
        )

        // Mana Regeneration (Wisdom based)
        val offHand1 = GameDatabase.getOffHand(state.currentPlayerSnapshot.equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(state.currentPlayerSnapshot.equippedOffHand2)
        val totalWisdom = state.currentPlayerSnapshot.wisdom + offHand1.wisdom + offHand2.wisdom
        var wisdomRegen = (totalWisdom / 5)
        
        if (state.currentPlayerSnapshot.activeKingdomLawId == 3) {
            wisdomRegen = (wisdomRegen * 0.85).toInt() // Void Resistance Act: -15% Regen
        }
        
        // Apply Mana Regen Buffs
        state.currentPlayerSnapshot.activeBuffs.find { it.type == BuffType.ManaRegen }?.let {
            wisdomRegen = (wisdomRegen * it.value).toInt()
        }
        
        if (state.currentPlayerSnapshot.unlockedTraits.contains("Will of the Ancients")) {
            wisdomRegen += 50
        }

        val nextManaWithRegen = minOf(state.playerMana + wisdomRegen, state.currentPlayerSnapshot.maxMana)
        if (nextManaWithRegen > state.playerMana) {
            newLogs.add(LogEntry("Regenerated ${nextManaWithRegen - state.playerMana} Mana.", Color.Cyan))
        }

        // --- HP REGENERATION ---
        var hpRegen = 0
        if (state.currentPlayerSnapshot.unlockedTraits.contains("Blood of the Gods")) hpRegen += 10
        if (state.currentPlayerSnapshot.unlockedTraits.contains("Eyes of the Creator")) hpRegen += 20
        if (state.currentPlayerSnapshot.unlockedTraits.contains("Will of the Ancients")) hpRegen += 50
        
        // Passive from items/buffs
        if (state.currentPlayerSnapshot.activeBuffs.any { it.type == BuffType.HealthRegen }) {
            hpRegen += (state.currentPlayerSnapshot.maxHp * 0.05).toInt()
        }

        val nextHpWithRegen = minOf(state.playerHp + hpRegen, state.currentPlayerSnapshot.maxHp)
        if (nextHpWithRegen > state.playerHp) {
            newLogs.add(LogEntry("Regenerated ${nextHpWithRegen - state.playerHp} HP.", Color.Green))
        }

        currentState = currentState.copy(
            playerMana = nextManaWithRegen,
            playerHp = nextHpWithRegen,
        )

        // 2. CHECK STUN
        if (state.playerStunnedTurns > 0) {
            newLogs.add(LogEntry("Xious is stunned and cannot move!", Color.Red))
            return Pair(currentState.copy(playerStunnedTurns = state.playerStunnedTurns - 1), newLogs)
        }

        // 3. DEFENSE CALCULATION
        var bossDef = if (isBossBattle) {
            if (storyEnemyName != null) StoryBossDatabase.getBoss(state.enemy.name).defense
            else BossDatabase.getBoss(state.enemy.name).defense
        } else 0
        
        if (state.bossShieldTurns > 0) {
            bossDef = (bossDef * 1.5).toInt() + 10
        }

        // 4. APPLY BUFFS / STANCES
        var damageBuffMultiplier = state.damageBuffMultiplier
        
        // Apply Passive Damage Buffs from Food/Events
        state.currentPlayerSnapshot.activeBuffs.find { it.type == BuffType.Damage }?.let {
            damageBuffMultiplier *= it.value
        }

        var buffTurns = state.buffTurns
        var critBuffTurns = state.critBuffTurns
        var superBuffTurns = state.superBuffTurns
        var parryActive = false // Already reset above, but local variable for this turn's action

        when (skill.effectType) {
            "Buff" -> {
                damageBuffMultiplier = 1.25
                buffTurns = 3
                newLogs.add(LogEntry("Xious uses ${skill.name}! Attack increased!", Color.Green))
            }
            "CritBuff" -> {
                critBuffTurns = 3
                newLogs.add(LogEntry("Xious uses ${skill.name}! Critical chance increased!", Color.Green))
            }
            "SuperBuff" -> {
                damageBuffMultiplier = 1.5
                superBuffTurns = 5
                newLogs.add(LogEntry("Xious uses ${skill.name}! Entering a state of high power!", Color.Green))
            }
            "Parry" -> {
                parryActive = true
                newLogs.add(LogEntry("Xious assumes a parry stance!", Color.Green))
            }
        }

        val bonusCrit = if (critBuffTurns > 0) 0.35 else 0.0

        // 5. EXECUTE LOGIC (Purely Player action)
        val skillLevel = state.currentPlayerSnapshot.skillLevels[skill.name] ?: 1
        val leveledSkill = SkillDatabase.getSkillWithLevel(skill.name, skillLevel)

        val result = BattleLogic.processTurn(
            skill = leveledSkill,
            player = state.currentPlayerSnapshot,
            currentHp = state.playerHp,
            currentMana = state.playerMana,
            currentEnemyHp = if (state.enemyHp > 0) state.enemyHp else state.enemy2Hp,
            enemy = if (state.enemyHp > 0) state.enemy else state.enemy2 ?: state.enemy,
            targetDefense = bossDef,
            playerDamageBuff = damageBuffMultiplier,
            bonusCritChance = bonusCrit,
            previousSkillName = state.lastSkillName,
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
                playerDamageBuff = damageBuffMultiplier,
                bonusCritChance = bonusCrit,
                previousSkillName = state.lastSkillName,
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

        if (skill.name != "None") {
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
        var finalEnemy1Hp = if (state.enemyHp > 0) result.enemyHp else state.enemyHp
        var finalEnemy2Hp = if (state.enemyHp <= 0) result.enemyHp else enemy2Hp
        
        var playerHp = maxOf(state.playerHp, result.playerHp) 
        var playerMana = result.playerMana
        
        var bleedTurns = state.bleedTurns
        var enemyStunnedTurns = state.enemyStunnedTurns
        var burnTurns = state.burnTurns
        var poisonTurns = state.poisonTurns
        
        var enemy2BleedTurns = state.enemy2BleedTurns
        var enemy2StunnedTurns = state.enemy2StunnedTurns
        var enemy2BurnTurns = state.enemy2BurnTurns
        var enemy2PoisonTurns = state.enemy2PoisonTurns

        if (skill.name == "None") {
            val restRegen = (state.currentPlayerSnapshot.wisdom / 2).coerceAtLeast(5)
            playerMana = minOf(playerMana + restRegen, state.currentPlayerSnapshot.maxMana)
            newLogs.add(LogEntry("Xious rests and recovers $restRegen Mana.", Color.Cyan))
        } else if ((skill.skillType != "Support") && (skill.skillType != "Buff")) {
            newLogs.add(LogEntry("Xious used ${skill.name} for ${result.playerDamage} damage.", Color.Cyan))
            if (result.isCrit) {
                newLogs.add(LogEntry("CRITICAL HIT!", Color.Cyan))
            }
            
            // Reflected damage back to enemy
            if (result.reflectedDamage > 0) {
                newLogs.add(LogEntry("Reflected ${result.reflectedDamage} damage back to ${state.enemy.name}!", Color.Cyan))
            }

            // --- Status Effects for Enemy 1 ---
            when (result.appliedStatus) {
                StatusEffect.Bleed -> {
                    bleedTurns = 3
                    newLogs.add(LogEntry("${state.enemy.name} is bleeding!", Color.Yellow))
                }
                StatusEffect.Stun -> {
                    newLogs.add(LogEntry("${state.enemy.name} was stunned!", Color.Yellow))
                    enemyStunnedTurns = 1
                }
                StatusEffect.Burn -> {
                    burnTurns = 3
                    newLogs.add(LogEntry("${state.enemy.name} caught on fire!", Color(0xFFFF5722)))
                }
                StatusEffect.Poison -> {
                    poisonTurns = 4
                    newLogs.add(LogEntry("${state.enemy.name} was poisoned!", Color(0xFF4CAF50)))
                }
                else -> {}
            }

            // --- Status Effects for Enemy 2 (if AoE hit) ---
            if (isAoE && state.enemy2 != null && enemy2Hp < state.enemy2Hp) {
                // AoE has 25% chance to apply status to secondary target if it has one
                if ((skill.statusEffect != StatusEffect.None) && (Math.random() < 0.25)) {
                    when (skill.statusEffect) {
                        StatusEffect.Bleed -> {
                            enemy2BleedTurns = 3
                            newLogs.add(LogEntry("${state.enemy2.name} is bleeding!", Color.Yellow))
                        }
                        StatusEffect.Stun -> {
                            enemy2StunnedTurns = 1
                            newLogs.add(LogEntry("${state.enemy2.name} was stunned!", Color.Yellow))
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
            newLogs.add(LogEntry("Xious healed for ${result.actualHeal} HP.", Color.Green))
        }

        if (skill.cooldown > 0) {
            workingCooldowns[skill.name] = skill.cooldown
        }

        // --- 5.6 FAMILIAR ACTION ---
        var familiarActionCounter = state.familiarActionCounter + 1
        val familiar = FamiliarDatabase.getFamiliar(state.currentPlayerSnapshot.equippedFamiliar)
        var familiarActionTaken = false
        
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
                    damageBuffMultiplier *= 1.25
                    newLogs.add(LogEntry("${familiar.name} boosted your attack!", Color.Yellow))
                }
                FamiliarActionType.Debuff -> {
                    newLogs.add(LogEntry("${familiar.name} distracted the enemy!", Color.Magenta))
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
            damageBuffMultiplier = if (familiarActionTaken && familiar.actionType == FamiliarActionType.Buff) damageBuffMultiplier else (if ((buffTurns > 0) || (superBuffTurns > 0)) damageBuffMultiplier else 1.0),
            buffTurns = maxOf(0, buffTurns - 1),
            critBuffTurns = maxOf(0, critBuffTurns - 1),
            superBuffTurns = maxOf(0, superBuffTurns - 1),
            parryActive = parryActive,
            bleedTurns = bleedTurns,
            enemy2BleedTurns = enemy2BleedTurns,
            enemyStunnedTurns = enemyStunnedTurns,
            enemy2StunnedTurns = enemy2StunnedTurns,
            burnTurns = burnTurns,
            enemy2BurnTurns = enemy2BurnTurns,
            poisonTurns = poisonTurns,
            enemy2PoisonTurns = enemy2PoisonTurns,
            lastSkillName = skill.name,
            skillCooldowns = workingCooldowns,
            battleMessage = if (result.isCrit) "CRITICAL HIT!" else "",
        )

        // Check for enemy sounds/speech after damage
        val (chatState, chatLogs) = checkEnemyChat(currentState, storyEnemyName)
        currentState = chatState
        newLogs.addAll(chatLogs)
        newLogs.addAll(enemy2Logs)

        return Pair(currentState, newLogs)
    }

    fun processEnemyTurn(
        state: BattleUiState,
        isBossBattle: Boolean,
    ): Pair<BattleUiState, List<LogEntry>> {
        val newLogs = mutableListOf<LogEntry>()
        
        // 1. Start of Turn Housekeeping (Decrement enemy cooldowns)
        val workingCooldowns = state.enemySkillCooldowns.toMutableMap()
        workingCooldowns.keys.forEach { name ->
            val cd = workingCooldowns[name] ?: 0
            if (cd > 0) workingCooldowns[name] = cd - 1
        }
        
        var currentState = state.copy(enemySkillCooldowns = workingCooldowns)

        // --- PASSIVE STATUS DAMAGE (Burn, Poison, Bleed) ---
        val (passiveState, passiveLogs) = applyPassiveDamage(currentState)
        currentState = passiveState
        newLogs.addAll(passiveLogs)

        // 2. Check if all enemies are dead after passive damage
        if (currentState.enemyHp <= 0 && (currentState.enemy2Hp <= 0)) return Pair(currentState, newLogs)

        // --- ENEMY 1 ACTION ---
        if (currentState.enemyHp > 0) {
            // Check if stunned
            if (currentState.enemyStunnedTurns > 0) {
                newLogs.add(LogEntry("${currentState.enemy.name} is stunned!", Color.Yellow))
                currentState = currentState.copy(enemyStunnedTurns = currentState.enemyStunnedTurns - 1)
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
            // Check if stunned
            if (state.enemy2StunnedTurns > 0) {
                newLogs.add(LogEntry("${state.enemy2.name} is stunned!", Color.Yellow))
                currentState = currentState.copy(enemy2StunnedTurns = state.enemy2StunnedTurns - 1)
            } else {
                // Improved dodge calculation
                val player = state.currentPlayerSnapshot
                var dodgeChance = (player.agility * 0.005) + (player.luck * 0.002)
                if (player.unlockedTraits.contains("Eyes of the Creator")) dodgeChance += 0.50
                if (player.joinedGuild == "House of Wind") dodgeChance += 0.02
                
                val isDodge = Math.random() < dodgeChance
                
                if (isDodge) {
                    newLogs.add(LogEntry("Xious dodged the ${state.enemy2.name}'s attack!", Color.Cyan))
                } else {
                    val armorDef = GameDatabase.getArmor(state.currentPlayerSnapshot.equippedArmor).defense
                    val totalDef = state.currentPlayerSnapshot.defense + armorDef
                    var enemyDmgDealt = BattleLogic.calculateEnemyDamage(state.enemy2.name, state.enemy2.level, totalDef)
                    if (state.parryActive) enemyDmgDealt /= 2
                    enemyDmgDealt = maxOf(1, enemyDmgDealt)
                    
                    val playerHp = maxOf(0, currentState.playerHp - enemyDmgDealt)
                    currentState = currentState.copy(playerHp = playerHp)
                    newLogs.add(LogEntry("${state.enemy2.name} attacks for $enemyDmgDealt damage.", Color.Red))
                }
            }
        }
        
        return Pair(currentState, newLogs)
    }

    private fun checkEnemyChat(state: BattleUiState, storyEnemyName: String?): Pair<BattleUiState, List<LogEntry>> {
        // Skip story mobs as requested (they have their own logic or we want standard ones here)
        if (storyEnemyName != null) return Pair(state, emptyList())

        val hpPercent = state.enemyHp.toFloat() / state.enemy.maxHp
        val thresholds = listOf(0.75f, 0.50f, 0.25f)
        
        // Find the highest threshold that hasn't been hit yet but is now reached
        val hitThreshold = thresholds.find { (hpPercent <= it) && (!state.chatThresholdsHit.contains(it)) }

        if (hitThreshold != null) {
            val message = if (state.enemy.isHumanoid) {
                EnemyChatDatabase.getHumanoidQuote(state.enemy.name, hpPercent)
            } else {
                EnemyChatDatabase.getMonsterSound(state.enemy.name, hpPercent)
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
        var enemyHp = state.enemyHp
        var enemy2Hp = state.enemy2Hp
        
        if (state.burnTurns > 0) {
            val burnDmg = (state.enemy.maxHp * 0.05).toInt().coerceAtLeast(1)
            enemyHp = maxOf(0, enemyHp - burnDmg)
            newLogs.add(LogEntry("Burn damage: $burnDmg to ${state.enemy.name}", Color(0xFFFF5722)))
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
                enemyHp = enemyHp,
                enemy2Hp = enemy2Hp,
                burnTurns = maxOf(0, state.burnTurns - 1),
                enemy2BurnTurns = maxOf(0, state.enemy2BurnTurns - 1),
                poisonTurns = maxOf(0, state.poisonTurns - 1),
                enemy2PoisonTurns = maxOf(0, state.enemy2PoisonTurns - 1),
                bleedTurns = maxOf(0, state.bleedTurns - 1),
                enemy2BleedTurns = maxOf(0, state.enemy2BleedTurns - 1),
            ),
            newLogs
        )
    }

    private fun processNormalEnemyAction(state: BattleUiState): Pair<BattleUiState, List<LogEntry>> {
        val newLogs = mutableListOf<LogEntry>()
        
        // Improved dodge calculation
        val player = state.currentPlayerSnapshot
        var dodgeChance = (player.agility * 0.005) + (player.luck * 0.002)
        if (player.unlockedTraits.contains("Eyes of the Creator")) dodgeChance += 0.50
        if (player.joinedGuild == "House of Wind") dodgeChance += 0.02
        
        val isDodge = Math.random() < dodgeChance
        
        if (isDodge) {
            newLogs.add(LogEntry("Xious dodged the ${state.enemy.name}'s attack!", Color.Cyan))
            return Pair(
                state.copy(battleMessage = "DODGED!"),
                newLogs
            )
        }

        val armorDef = GameDatabase.getArmor(state.currentPlayerSnapshot.equippedArmor).defense
        val headDef = GameDatabase.getHeadGear(state.currentPlayerSnapshot.equippedHead).defense
        val shieldDef = GameDatabase.getShield(state.currentPlayerSnapshot.equippedShield).defense
        val offHand1 = GameDatabase.getOffHand(state.currentPlayerSnapshot.equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(state.currentPlayerSnapshot.equippedOffHand2)
        val itemDef = offHand1.defense + offHand2.defense
        
        val traitBonusDef = state.currentPlayerSnapshot.unlockedTraits.sumOf { TraitDatabase.getTrait(it)?.vitBonus ?: 0 }
        var totalDef = state.currentPlayerSnapshot.defense + armorDef + headDef + shieldDef + itemDef + traitBonusDef
        
        // Apply Passive Defense Buffs from Food/Events
        state.currentPlayerSnapshot.activeBuffs.find { it.type == com.example.lumeria.models.BuffType.Defense }?.let {
            totalDef = (totalDef * it.value).toInt()
        }

        val effectiveDef = if (state.playerDefenseDebuffTurns > 0) (totalDef * 0.8).toInt() else totalDef
        
        var enemyDmgDealt = BattleLogic.calculateEnemyDamage(state.enemy.name, state.enemy.level, effectiveDef)
        
        // --- KINGDOM LAWS ---
        when (state.currentPlayerSnapshot.activeKingdomLawId) {
            2 -> enemyDmgDealt = (enemyDmgDealt * 1.15).toInt() // Warrior's Draft: +15% Enemy Dmg
        }

        if (state.parryActive) enemyDmgDealt /= 2
        
        enemyDmgDealt = maxOf(1, enemyDmgDealt)
        var playerHp = maxOf(0, state.playerHp - enemyDmgDealt)
        var enemyHp = state.enemyHp

        if (state.parryActive && state.lastSkillName == "Absolute Counter") {
            val reflected = (enemyDmgDealt * 0.75).toInt()
            enemyHp = maxOf(0, enemyHp - reflected)
            newLogs.add(LogEntry("ABSOLUTE COUNTER! Reflected $reflected damage!", Color.Cyan))
        }
        
        var secondWindUsed = state.secondWindUsed
        if (playerHp <= 0 && !secondWindUsed && state.currentPlayerSnapshot.equippedArmor == "Mantle of Perseverance") {
            playerHp = 1
            secondWindUsed = true
            newLogs.add(LogEntry("SECOND WIND! Xious survived a lethal blow!", Color.Cyan))
        }
        
        newLogs.add(LogEntry("${state.enemy.name} attacks for $enemyDmgDealt damage.", Color.Red))
        
        return Pair(
            state.copy(playerHp = playerHp, enemyHp = enemyHp, secondWindUsed = secondWindUsed),
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
        val bossData = BossDatabase.getBoss(state.enemy.name)
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
        // Improved dodge calculation
        val player = state.currentPlayerSnapshot
        var dodgeChance = (player.agility * 0.005) + (player.luck * 0.002)
        if (player.unlockedTraits.contains("Eyes of the Creator")) dodgeChance += 0.50
        if (player.joinedGuild == "House of Wind") dodgeChance += 0.02
        
        val isDodge = Math.random() < dodgeChance
        
        if (isDodge) {
            newLogs.add(LogEntry("Xious dodged the ${state.enemy.name}'s attack!", Color.Cyan))
            return Pair(
                state.copy(battleMessage = "DODGED!"),
                newLogs
            )
        }

        var playerHp = state.playerHp
        var enemyHp = state.enemyHp
        var playerStunnedTurns = state.playerStunnedTurns
        var playerDefenseDebuffTurns = state.playerDefenseDebuffTurns

        // 3. DAMAGE CALCULATION
        val armorDef = GameDatabase.getArmor(state.currentPlayerSnapshot.equippedArmor).defense
        val headDef = GameDatabase.getHeadGear(state.currentPlayerSnapshot.equippedHead).defense
        val shieldDef = GameDatabase.getShield(state.currentPlayerSnapshot.equippedShield).defense
        val offHand1 = GameDatabase.getOffHand(state.currentPlayerSnapshot.equippedOffHand)
        val offHand2 = GameDatabase.getOffHand(state.currentPlayerSnapshot.equippedOffHand2)
        val itemDef = offHand1.defense + offHand2.defense
        
        val traitBonusDef = state.currentPlayerSnapshot.unlockedTraits.sumOf { TraitDatabase.getTrait(it)?.vitBonus ?: 0 }
        var totalDef = state.currentPlayerSnapshot.defense + armorDef + headDef + shieldDef + itemDef + traitBonusDef

        // Apply Passive Defense Buffs from Food/Events
        state.currentPlayerSnapshot.activeBuffs.find { it.type == com.example.lumeria.models.BuffType.Defense }?.let {
            totalDef = (totalDef * it.value).toInt()
        }

        val effectiveDef = if (playerDefenseDebuffTurns > 0) (totalDef * 0.8).toInt() else totalDef
        
        var multiplier = attack.multiplier
        if (enrageTurns > 0) {
            multiplier *= 1.25 
        }
        
        val ignoreArmor = attack.effectType == "IgnoreArmor"
        var baseDmg = BattleLogic.calculateBossDamage(state.enemy.level, effectiveDef, multiplier, ignoreArmor)

        // --- KINGDOM LAWS ---
        when (state.currentPlayerSnapshot.activeKingdomLawId) {
            2 -> baseDmg = (baseDmg * 1.15).toInt() // Warrior's Draft: +15% Enemy Dmg
            3 -> {
                // Void Resistance: Reduce damage from Void/Dark bosses
                if (state.enemy.name.contains("Void") || state.enemy.name.contains("Xarthos") || state.enemy.name.contains("Umbra")) {
                    baseDmg = (baseDmg * 0.8).toInt()
                }
            }
        }

        if (state.parryActive) baseDmg /= 2
        baseDmg = maxOf(1, baseDmg)

        if (state.parryActive && state.lastSkillName == "Absolute Counter") {
            val reflected = (baseDmg * 0.75).toInt()
            enemyHp = maxOf(0, enemyHp - reflected)
            newLogs.add(LogEntry("ABSOLUTE COUNTER! Reflected $reflected damage!", Color.Cyan))
        }

        newLogs.add(LogEntry("${state.enemy.name} deals $baseDmg damage.", Color.Red))

        // 4. APPLY EFFECTS
        val hasAbsoluteResistance = player.unlockedTraits.contains("Will of the Ancients")

        when (attack.effectType) {
            "Stun" -> {
                if (Math.random() < 0.25 && !hasAbsoluteResistance) {
                    playerStunnedTurns = 1
                    newLogs.add(LogEntry("Xious was stunned by the impact!", Color.Yellow))
                } else if (hasAbsoluteResistance) {
                    newLogs.add(LogEntry("Will of the Ancients resists the stun!", Color.Cyan))
                }
                playerHp = maxOf(0, playerHp - baseDmg)
            }
            "Heal" -> {
                val heal = (state.enemy.maxHp * 0.05).toInt()
                enemyHp = minOf(enemyHp + heal, state.enemy.maxHp)
                newLogs.add(LogEntry("${state.enemy.name} regains composure and heals for $heal HP!", Color.Yellow))
                playerHp = maxOf(0, playerHp - baseDmg)
            }
            "DefenseDown" -> {
                if (!hasAbsoluteResistance) {
                    playerDefenseDebuffTurns = 3
                    newLogs.add(LogEntry("Xious's defense was lowered!", Color.Yellow))
                } else {
                    newLogs.add(LogEntry("Will of the Ancients resists the debuff!", Color.Cyan))
                }
                playerHp = maxOf(0, playerHp - baseDmg)
            }
            else -> {
                playerHp = maxOf(0, playerHp - baseDmg)
            }
        }

        var secondWindUsed = state.secondWindUsed
        if (playerHp <= 0 && !secondWindUsed && state.currentPlayerSnapshot.equippedArmor == "Mantle of Perseverance") {
            playerHp = 1
            secondWindUsed = true
            newLogs.add(LogEntry("SECOND WIND! Xious survived a lethal blow!", Color.Cyan))
        }

        return Pair(
            state.copy(
                playerHp = playerHp,
                enemyHp = enemyHp,
                phase2Triggered = phase2,
                phase3Triggered = phase3,
                bossEnrageTurns = enrageTurns,
                bossShieldTurns = shieldTurns,
                playerStunnedTurns = playerStunnedTurns,
                playerDefenseDebuffTurns = playerDefenseDebuffTurns,
                secondWindUsed = secondWindUsed
            ),
            newLogs
        )
    }
}
