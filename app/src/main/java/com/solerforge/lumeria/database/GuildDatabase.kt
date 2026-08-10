package com.solerforge.lumeria.database

import com.solerforge.lumeria.models.ElementType
import com.solerforge.lumeria.models.Skill
import com.solerforge.lumeria.models.StatusEffect

data class GuildSkillRequirement(
    val skill: Skill,
    val requiredGuildLevel: Int,
    val fee: Long
)

object GuildDatabase {
    
    val fireSkills = listOf(
        GuildSkillRequirement(Skill("Ignite", "A basic fire spell.", 15, 0, 10, "Attack", 1.2, elementType = ElementType.Fire, requiredClasses = listOf("Mage", "Necromancer", "Paladin")), 1, 1000L),
        GuildSkillRequirement(Skill("Fireball", "A classic projectile.", 25, 1, 15, "Attack", 1.5, elementType = ElementType.Fire, requiredClasses = listOf("Mage", "Necromancer")), 2, 3000L),
        GuildSkillRequirement(Skill("Flame Pillar", "Erupting fire from beneath.", 40, 2, 25, "Attack", 2.0, elementType = ElementType.Fire, requiredClasses = listOf("Mage", "Necromancer")), 3, 7500L),
        GuildSkillRequirement(Skill("Heat Wave", "A wide area burn.", 50, 3, 35, "Attack", 1.8, effectType = "Burn", elementType = ElementType.Fire, statusEffect = StatusEffect.Burn, requiredClasses = listOf("Mage", "Necromancer")), 4, 15000L),
        GuildSkillRequirement(Skill("Inferno", "A devastating fire storm.", 75, 5, 50, "Attack", 3.0, elementType = ElementType.Fire, requiredClasses = listOf("Mage", "Necromancer")), 5, 30000L),
        GuildSkillRequirement(Skill("Blazing Soul", "Boosts fire damage significantly.", 60, 4, 45, "Buff", 1.5, effectType = "DamageBuff", elementType = ElementType.Fire, requiredClasses = listOf("Mage", "Berserker", "Warrior")), 6, 50000L),
        GuildSkillRequirement(Skill("Dragon Breath", "Mimics the dragon's fire.", 100, 6, 60, "Attack", 4.0, elementType = ElementType.Fire, requiredClasses = listOf("Mage", "Berserker", "Warrior")), 7, 75000L),
        GuildSkillRequirement(Skill("Solar Flare", "Blinds and burns the enemy.", 120, 8, 70, "Attack", 3.5, effectType = "Stun", elementType = ElementType.Fire, statusEffect = StatusEffect.Stun, requiredClasses = listOf("Mage", "Paladin")), 8, 100000L),
        GuildSkillRequirement(Skill("Phoenix Rebirth", "Heals and buffs fire power.", 150, 10, 80, "Support", 2.0, effectType = "Heal", elementType = ElementType.Fire, requiredClasses = listOf("Mage", "Paladin", "Bard")), 9, 250000L),
        GuildSkillRequirement(Skill("Supernova", "The ultimate fire magic.", 250, 12, 100, "Attack", 6.0, effectType = "LimitBreak", elementType = ElementType.Fire, requiredClasses = listOf("Mage")), 10, 500000L)
    )

    val waterSkills = listOf(
        GuildSkillRequirement(Skill("Water Bolt", "A concentrated stream of water.", 12, 0, 10, "Attack", 1.1, elementType = ElementType.Ice, requiredClasses = listOf("Mage", "Necromancer")), 1, 1000L),
        GuildSkillRequirement(Skill("Aqua Shield", "Reduces incoming damage.", 30, 2, 15, "Buff", 0.0, effectType = "DefenseBuff", elementType = ElementType.Ice, requiredClasses = listOf("Mage", "Paladin", "Monk")), 2, 3000L),
        GuildSkillRequirement(Skill("Ice Spike", "Piercing ice from the ground.", 35, 1, 25, "Attack", 1.7, elementType = ElementType.Ice, requiredClasses = listOf("Mage", "Necromancer")), 3, 7500L),
        GuildSkillRequirement(Skill("Healing Rain", "Gentle water that restores HP.", 45, 3, 30, "Support", 1.5, effectType = "Heal", elementType = ElementType.Ice, requiredClasses = listOf("Mage", "Paladin", "Monk", "Bard")), 4, 15000L),
        GuildSkillRequirement(Skill("Blizzard", "Freezing winds and ice.", 80, 5, 50, "Attack", 2.5, effectType = "Freeze", elementType = ElementType.Ice, requiredClasses = listOf("Mage", "Necromancer")), 5, 30000L),
        GuildSkillRequirement(Skill("Tidal Wave", "A massive wave of force.", 100, 4, 60, "Attack", 3.5, elementType = ElementType.Ice, requiredClasses = listOf("Mage", "Necromancer")), 6, 50000L),
        GuildSkillRequirement(Skill("Absolute Zero", "Stops the enemy in their tracks.", 150, 8, 75, "Attack", 4.0, effectType = "Stun", elementType = ElementType.Ice, statusEffect = StatusEffect.Stun, requiredClasses = listOf("Mage", "Necromancer")), 7, 100000L),
        GuildSkillRequirement(Skill("Mist Cover", "Increases dodge chance.", 60, 4, 40, "Buff", 0.0, effectType = "EvasionBuff", elementType = ElementType.Ice, requiredClasses = listOf("Mage", "Assassin", "Bard")), 8, 150000L),
        GuildSkillRequirement(Skill("Whirlpool", "Drains enemy mana.", 120, 6, 70, "Attack", 2.0, elementType = ElementType.Ice, requiredClasses = listOf("Mage", "Necromancer")), 9, 300000L),
        GuildSkillRequirement(Skill("Oceanic Wrath", "The power of the deep sea.", 300, 15, 100, "Attack", 7.0, effectType = "LimitBreak", elementType = ElementType.Ice, requiredClasses = listOf("Mage")), 10, 750000L)
    )

    val windSkills = listOf(
        GuildSkillRequirement(Skill("Wind Slash", "A sharp gust of air.", 10, 0, 10, "Attack", 1.0, elementType = ElementType.Physical, requiredClasses = listOf("Samurai", "Assassin", "Archer", "Bard")), 1, 1000L),
        GuildSkillRequirement(Skill("Swift Step", "Increases agility.", 20, 2, 15, "Buff", 0.0, effectType = "AgilityBuff", elementType = ElementType.Physical, requiredClasses = listOf("Samurai", "Assassin", "Archer", "Monk", "Bard")), 2, 3000L),
        GuildSkillRequirement(Skill("Tornado", "A spinning vortex of wind.", 50, 4, 30, "Attack", 2.2, effectType = "Stun", elementType = ElementType.Physical, statusEffect = StatusEffect.Stun, requiredClasses = listOf("Samurai", "Assassin", "Archer", "Bard")), 3, 8000L),
        GuildSkillRequirement(Skill("Air Blade", "Multiple strikes with air.", 40, 2, 25, "Attack", 1.8, effectType = "MultiHit", elementType = ElementType.Physical, requiredClasses = listOf("Samurai", "Assassin", "Archer")), 4, 16000L),
        GuildSkillRequirement(Skill("Vacuum Burst", "Sucks in the enemy and explodes.", 70, 5, 45, "Attack", 2.8, elementType = ElementType.Physical, requiredClasses = listOf("Samurai", "Assassin", "Archer")), 5, 35000L),
        GuildSkillRequirement(Skill("Eye of the Storm", "Ultimate focus and speed.", 100, 6, 60, "Buff", 2.0, effectType = "CritBuff", elementType = ElementType.Physical, requiredClasses = listOf("Samurai", "Assassin", "Archer")), 6, 60000L),
        GuildSkillRequirement(Skill("Cyclone Strike", "Heavy wind damage.", 130, 8, 75, "Attack", 4.5, elementType = ElementType.Physical, requiredClasses = listOf("Samurai", "Assassin", "Archer")), 7, 120000L),
        GuildSkillRequirement(Skill("Feather Weight", "Ignore weight, move first.", 50, 4, 40, "Buff", 0.0, effectType = "EvasionBuff", elementType = ElementType.Physical, requiredClasses = listOf("Samurai", "Assassin", "Archer", "Monk")), 8, 200000L),
        GuildSkillRequirement(Skill("Thunderstorm", "Wind and lightning combined.", 180, 10, 85, "Attack", 5.0, elementType = ElementType.Lightning, requiredClasses = listOf("Samurai", "Assassin", "Archer")), 9, 400000L),
        GuildSkillRequirement(Skill("God Wind", "The breath of the wind god.", 400, 20, 100, "Attack", 10.0, effectType = "LimitBreak", elementType = ElementType.Physical, requiredClasses = listOf("Samurai", "Assassin")), 10, 1000000L)
    )

    val allSkills: List<Skill> = fireSkills.map { it.skill } + waterSkills.map { it.skill } + windSkills.map { it.skill }
 fun getGuildSkills(guildName: String, playerClass: String): List<GuildSkillRequirement> {
        val allSkills = when (guildName) {
            "House of Fire" -> fireSkills
            "House of Water" -> waterSkills
            "House of Wind" -> windSkills
            else -> emptyList()
        }
        return allSkills.filter { it.skill.requiredClasses.isEmpty() || it.skill.requiredClasses.contains(playerClass) }
    }
    
    fun getGuildMaster(guildName: String): String {
        return when (guildName) {
            "House of Fire" -> "Ignis the Scorched"
            "House of Water" -> "Marina the Fluid"
            "House of Wind" -> "Zephyr the Unseen"
            else -> "Unknown Master"
        }
    }
    
    fun getGuildDescription(guildName: String): String {
        return when (guildName) {
            "House of Fire" -> "Passionate warriors who harness the destructive power of flames. +2% STR boost."
            "House of Water" -> "Calm and steady mages who manipulate life and ice. +2% HP/Mana boost."
            "House of Wind" -> "Swift assassins who move with the grace of a gale. +2% Dodge/Crit boost."
            else -> ""
        }
    }

    fun getGuildMasterDialogue(guildName: String, level: Int): String {
        return when (guildName) {
            "House of Fire" -> when {
                level >= 10 -> "\"Your spirit burns brighter than any sun, Champion. The world will tremble at your touch.\""
                level >= 7 -> "\"The flames within you have matured. You are becoming a true force of destruction.\""
                level >= 4 -> "\"I see the spark in your eyes, cub. Let's stoke that fire into a blaze!\""
                else -> "\"Show me your dedication, and I shall share our secrets. The path of fire requires a burning heart.\""
            }
            "House of Water" -> when {
                level >= 10 -> "\"You have reached perfect tranquility. The tides themselves answer your call, Archmage.\""
                level >= 7 -> "\"Your mind is as deep as the ocean. You are ready for our most sacred techniques.\""
                level >= 4 -> "\"Flow like the stream, young one. Persistence always wears down the stone.\""
                else -> "\"Show me your dedication, and I shall share our secrets. The path of water requires a calm and steady mind.\""
            }
            "House of Wind" -> when {
                level >= 10 -> "\"You have become the storm itself. Invisible, untouchable, and inevitable.\""
                level >= 7 -> "\"The winds whisper your name across the realm. Your speed is unmatched.\""
                level >= 4 -> "\"Move with the breeze, apprentice. Do not fight the gale; become it.\""
                else -> "\"Show me your dedication, and I shall share our secrets. The path of wind requires grace and swiftness.\""
            }
            else -> "\"Show me your dedication, and I shall share our secrets.\""
        }
    }

    fun getGuildXpForNextLevel(currentLevel: Int): Long {
        return currentLevel * 1000L + (currentLevel.toLong() * currentLevel * 500L)
    }

    data class GuildQuest(
        val id: Int,
        val title: String,
        val targetEnemy: String,
        val targetCount: Int,
        val rewardGuildXp: Long,
        val rewardGold: Long,
        val minLevel: Int
    )

    val fireQuests = listOf(
        GuildQuest(1, "Sparking Interest", "Slime", 10, 500L, 1000L, 1),
        GuildQuest(2, "Forest Fire", "Goblin", 15, 1200L, 2500L, 5),
        GuildQuest(3, "Magma Hunt", "Lava Serpent", 5, 5000L, 15000L, 50)
    )

    val waterQuests = listOf(
        GuildQuest(11, "Hydration Station", "Slime", 10, 500L, 1000L, 1),
        GuildQuest(12, "Tidal Cleanse", "Cave Bat", 15, 1200L, 2500L, 5),
        GuildQuest(13, "Frozen Depths", "Crystal Slime", 12, 3000L, 8000L, 25)
    )

    val windQuests = listOf(
        GuildQuest(21, "Breeze Through", "Wild Rat", 12, 500L, 1000L, 1),
        GuildQuest(22, "Gale Force", "Goblin Archer", 8, 1500L, 3000L, 10),
        GuildQuest(23, "High Altitude", "Stone Golem", 6, 6000L, 20000L, 60)
    )

    fun getGuildQuests(guildName: String): List<GuildQuest> {
        return when (guildName) {
            "House of Fire" -> fireQuests
            "House of Water" -> waterQuests
            "House of Wind" -> windQuests
            else -> emptyList()
        }
    }

    data class GuildExam(
        val rank: Int,
        val enemyName: String,
        val level: Int,
        val hp: Int,
        val introDialogue: String,
        val victoryDialogue: String
    )

    val fireExams = listOf(
        GuildExam(2, "Fire Disciple", 10, 800, "\"To advance in the House of Fire, you must first prove your inner flame can withstand a spark.\"", "\"Impressive. Your flame burns steady. You are now a Rank 2 member.\""),
        GuildExam(3, "Fire Adept", 15, 1500, "\"The heat rises. Can you control a larger blaze?\"", "\"You have tamed the flickering flame. Rank 3 is yours.\""),
        GuildExam(4, "Cinder Warrior", 22, 3000, "\"Strength is more than just heat; it is force. Strike me with all you have!\"", "\"Your strikes carry the weight of a volcanic eruption. Rank 4 achieved.\""),
        GuildExam(5, "Blaze Knight", 30, 5000, "\"Only those with the strength of an inferno may claim the title of Elite.\"", "\"You have the heart of a dragon! Welcome to the Elite tier.\""),
        GuildExam(6, "Solar Monk", 40, 8000, "\"The sun's light is pure and blinding. Can you find your target in the glare?\"", "\"Clarity achieved. Rank 6 attained.\""),
        GuildExam(7, "Magma Sentinel", 50, 12000, "\"Solid as rock, fluid as lava. Prove you can break the unbreakable.\"", "\"Even the mountains bow to your heat. Rank 7 achieved.\""),
        GuildExam(8, "Inferno Master", 60, 20000, "\"You seek mastery over the storm of fire. Survive the vortex!\"", "\"The inferno obeys your will. Rank 8 is yours.\""),
        GuildExam(9, "Phoenix Aspirant", 75, 35000, "\"To be reborn, you must first endure the fire. Show me your resilience.\"", "\"Like the phoenix, you rise stronger. Rank 9 achieved.\""),
        GuildExam(10, "Ignis the Scorched", 100, 100000, "\"The final test is against me. Show me the absolute peak of your destruction!\"", "\"I am proud to call you my equal. Rise, Guild Champion!\"")
    )

    val waterExams = listOf(
        GuildExam(2, "Water Acolyte", 10, 800, "\"Water is patient, but it can also be relentless. Show me your flow.\"", "\"Well done. You move with the grace of a stream. Rank 2 attained.\""),
        GuildExam(3, "Mist Walker", 15, 1500, "\"The fog conceals many things. Can you see the path ahead?\"", "\"Your vision is clear. Rank 3 achieved.\""),
        GuildExam(4, "Ice Warder", 22, 3000, "\"Hardness has its place. Can you break through my frozen defense?\"", "\"A sharp strike can shatter any ice. Rank 4 attained.\""),
        GuildExam(5, "Tidal Sage", 30, 5000, "\"A true master of water understands the weight of the ocean.\"", "\"Your mind is as deep as the abyss. You are now an Elite member.\""),
        GuildExam(6, "Frost Reaver", 40, 8000, "\"Cold is the absence of life. Can you endure the bite of the north?\"", "\"You have a heart of permafrost. Rank 6 achieved.\""),
        GuildExam(7, "Oceanic Guard", 50, 12000, "\"The tide comes in, the tide goes out. Can you stand against the pull?\"", "\"Unshakeable as the reef. Rank 7 attained.\""),
        GuildExam(8, "Abyssal Crawler", 60, 20000, "\"In the deepest depths, there is only pressure. Can you breathe where others drown?\"", "\"The abyss is your home now. Rank 8 achieved.\""),
        GuildExam(9, "Glacier Titan", 75, 35000, "\"A moving mountain of ice. Stop me if you can!\"", "\"Nothing can stop your momentum. Rank 9 attained.\""),
        GuildExam(10, "Marina the Fluid", 100, 100000, "\"Be like water, my friend. Let us see if you can wash away my techniques!\"", "\"You have achieved perfect tranquility. All hail the new Guild Champion!\"")
    )

    val windExams = listOf(
        GuildExam(2, "Gale Scout", 10, 800, "\"Can you catch the wind? Or will it slip through your fingers?\"", "\"Swift and precise. You have earned your place as a Rank 2 member.\""),
        GuildExam(3, "Breeze Blade", 15, 1500, "\"A gentle wind can still cut deep. Show me your finesse.\"", "\"Sharp as a razor. Rank 3 achieved.\""),
        GuildExam(4, "Cloud Dancer", 22, 3000, "\"The sky is vast. Can you keep your footing in the air?\"", "\"You move with the grace of the clouds. Rank 4 attained.\""),
        GuildExam(5, "Storm Assassin", 30, 5000, "\"The shadows of the wind are where we find our true power.\"", "\"You strike like a lightning bolt! Welcome to the Elite ranks.\""),
        GuildExam(6, "Whirlwind Monk", 40, 8000, "\"The center of the storm is where you must find peace.\"", "\"Total focus. Rank 6 achieved.\""),
        GuildExam(7, "Sky Sentinel", 50, 12000, "\"From above, everything is small. Do you have the perspective of a hawk?\"", "\"Keen eyes and a steady hand. Rank 7 attained.\""),
        GuildExam(8, "Cyclone Master", 60, 20000, "\"The wind obeys no one. Can you make it follow your command?\"", "\"The gale is your servant. Rank 8 achieved.\""),
        GuildExam(9, "Void Strider", 75, 35000, "\"Between the gusts, there is a space where time stands still. Find it.\"", "\"You walk between the breaths of the world. Rank 9 attained.\""),
        GuildExam(10, "Zephyr the Unseen", 100, 100000, "\"I am everywhere and nowhere. Strike me if you can!\"", "\"Even I could not track your movement. Rise, Guild Champion!\"")
    )

    fun getGuildExam(guildName: String, targetRank: Int): GuildExam? {
        val exams = when (guildName) {
            "House of Fire" -> fireExams
            "House of Water" -> waterExams
            "House of Wind" -> windExams
            else -> emptyList()
        }
        return exams.find { it.rank == targetRank }
    }

    fun getHouseHistory(guildName: String): String {
        return when (guildName) {
            "House of Fire" -> """
                Born from the core of Mt. Ignis, the House of Fire was forged in the heat of a thousand wars. Our creed is discipline; our weapon is absolute destruction. 
                
                Under the iron rule of Master Ignis, we do not merely fight—we incinerate. He is a strict and serious leader who demands nothing short of perfection, for a flickering flame is a dying one. 
                
                Only those with the resolve of a volcano and the stomach for total war may call this house their home. Welcome to the inferno.
            """.trimIndent()
            
            "House of Water" -> """
                Guided by the eternal flow of the Azure Sea, the House of Water represents the perfect balance between the life-giving stream and the cold reach of winter. 
                
                Master Marina leads us with a majestic and kind spirit as deep as the ocean itself. She teaches us that true strength lies in fluidity and adaptation—to be like water is to be unstoppable. 
                
                Here, you will learn to heal the broken, protect the innocent, and freeze the hearts of the wicked. May the tides guide you.
            """.trimIndent()
            
            "House of Wind" -> """
                Whispering through the shadows of the High Peaks, the House of Wind is the unseen hand that shapes the fate of Lumeria from the darkness. 
                
                Master Zephyr, a mysterious and ninja-like ghost amongst men, teaches the art of the sudden strike and the silent footstep. In our house, we value the secret over the shout, and the gale over the wall. 
                
                We do not seek glory, only the truth hidden in the silence between breaths. Become the storm, or be swept away.
            """.trimIndent()
            
            else -> "The history of this house is lost to time."
        }
    }
}
