package com.solerforge.lumeria.database

data class BossAttack(
    val name: String,
    val multiplier: Double = 1.0,
    val description: String,
    val effectType: String = "Normal", // "Stun", "Heal", "DefenseDown", "MultiHit", "IgnoreArmor", "Buff", "Lifesteal"
    val cooldown: Int = 0,
    val hitCount: Int = 1
)

data class Boss(
    val name: String,
    val hp: Int,
    val level: Int,
    val rewardXp: Long,
    val rewardGold: Long,
    val defense: Int,
    val moveset: List<BossAttack>,
    val description: String = "",
    val isLegacy: Boolean = false
)

object BossDatabase {

    val bosses = listOf(
        // WORLD BOSSES
        Boss(
            name = "Training Captain",
            hp = 1200,
            level = 6,
            rewardXp = 250L,
            rewardGold = 500L,
            defense = 20,
            moveset = listOf(
                BossAttack("Shield Bash", 1.25, "A heavy slam that can stun.", "Stun", cooldown = 2),
                BossAttack("Commanding Strike", 1.75, "A powerful veteran strike.", "Normal", cooldown = 0),
                BossAttack("Rally Command", 0.0, "Regains composure and heals.", "Heal", cooldown = 3)
            )
        ),
        Boss(
            name = "Goblin King",
            hp = 4500,
            level = 16,
            rewardXp = 800L,
            rewardGold = 2000L,
            defense = 45,
            moveset = listOf(
                BossAttack("Crown Smash", 2.0, "A heavy club attack.", "Normal", cooldown = 0),
                BossAttack("Goblin Ambush", 1.0, "Calls reinforcements to attack twice.", "MultiHit", cooldown = 2, hitCount = 2),
                BossAttack("King's Roar", 1.0, "Reduces player defense for 3 turns.", "DefenseDown", cooldown = 3)
            )
        ),
        Boss(
            name = "Crystal Guardian",
            hp = 10000,
            level = 26,
            rewardXp = 2500L,
            rewardGold = 7500L,
            defense = 80,
            moveset = listOf(
                BossAttack("Crystal Shards", 0.6, "Fires razor-sharp crystals 3 times.", "MultiHit", cooldown = 0, hitCount = 3),
                BossAttack("Reflective Barrier", 0.0, "Blocks 50% of incoming damage.", "Shield", cooldown = 4),
                BossAttack("Prismatic Beam", 2.5, "A massive beam that ignores armor.", "IgnoreArmor", cooldown = 3)
            )
        ),
        Boss(
            name = "Stone Titan",
            hp = 12000,
            level = 36,
            rewardXp = 6000L,
            rewardGold = 20000L,
            defense = 80,
            moveset = listOf(
                BossAttack("Boulder Toss", 2.0, "Throws a giant rock.", "Normal", cooldown = 0),
                BossAttack("Titanic Stomp", 3.5, "Crushes the ground and stuns.", "Stun", cooldown = 4),
                BossAttack("Earthquake", 1.25, "Shakes the battlefield, reducing accuracy.", "Normal", cooldown = 2)
            )
        ),
        Boss(
            name = "Marsh Horror",
            hp = 25000,
            level = 51,
            rewardXp = 12000L,
            rewardGold = 60000L,
            defense = 120,
            moveset = listOf(
                BossAttack("Toxic Spit", 1.25, "A poisonous attack.", "Normal", cooldown = 0),
                BossAttack("Devour", 3.5, "Deals heavy damage and heals.", "Lifesteal", cooldown = 3),
                BossAttack("Bog Mist", 0.0, "Summons thick mist to dodge.", "EvasionBuff", cooldown = 5)
            )
        ),
        Boss(
            name = "Ancient Dragon",
            hp = 50000,
            level = 66,
            rewardXp = 25000L,
            rewardGold = 150000L,
            defense = 180,
            moveset = listOf(
                BossAttack("Dragon Breath", 4.5, "A massive fire attack.", "Normal", cooldown = 4),
                BossAttack("Wing Buffet", 1.0, "A wind attack that hits twice.", "MultiHit", cooldown = 0, hitCount = 2),
                BossAttack("Ancient Fury", 0.0, "Dragon becomes enraged!", "Buff", cooldown = 6)
            )
        ),
        Boss(
            name = "Lord Xarthos",
            hp = 100000,
            level = 80,
            rewardXp = 50000L,
            rewardGold = 400000L,
            defense = 300,
            moveset = listOf(
                BossAttack("Void Slash", 2.5, "A blade of void energy.", "Normal", cooldown = 0),
                BossAttack("Soul Drain", 2.0, "Steals life from the target.", "Lifesteal", cooldown = 3),
                BossAttack("Collapse Reality", 6.0, "Ultimate void technique.", "IgnoreArmor", cooldown = 5)
            )
        ),
        // LEGACY BOSSES
        Boss(
            name = "The Star Sovereign",
            hp = 150000, level = 20, rewardXp = 100000L, rewardGold = 0L, defense = 400,
            moveset = listOf(
                BossAttack("Stellar Flare", 3.0, "Burns with the power of stars."),
                BossAttack("Supernova", 8.0, "A massive explosion.", "IgnoreArmor", cooldown = 6)
            ),
            isLegacy = true
        ),
        Boss(
            name = "The Ancient Oak",
            hp = 300000, level = 40, rewardXp = 250000L, rewardGold = 0L, defense = 600,
            moveset = listOf(
                BossAttack("Root Crush", 2.5, "Crushes from beneath."),
                BossAttack("Nature's Blessing", 0.0, "Heals using the earth.", "Heal", cooldown = 4)
            ),
            isLegacy = true
        ),
        Boss(
            name = "Prismatic Queen",
            hp = 500000, level = 60, rewardXp = 500000L, rewardGold = 0L, defense = 800,
            moveset = listOf(
                BossAttack("Refracted Beam", 4.0, "Light bounces and hits harder."),
                BossAttack("Crystal Cage", 1.0, "Stuns the target in crystal.", "Stun", cooldown = 5)
            ),
            isLegacy = true
        ),
        Boss(
            name = "The Iron Titan",
            hp = 800000, level = 80, rewardXp = 1000000L, rewardGold = 0L, defense = 1200,
            moveset = listOf(
                BossAttack("Heavy Anvil", 5.0, "A massive weight falls."),
                BossAttack("Iron Will", 0.0, "Hardens for massive defense.", "Shield", cooldown = 5)
            ),
            isLegacy = true
        ),
        Boss(
            name = "Fen Horror",
            hp = 1200000, level = 100, rewardXp = 2000000L, rewardGold = 0L, defense = 1500,
            moveset = listOf(
                BossAttack("Swamp Gulp", 3.0, "Bites and heals.", "Lifesteal"),
                BossAttack("Toxic Mist", 1.0, "Reduces player defense.", "DefenseDown", cooldown = 3)
            ),
            isLegacy = true
        ),
        Boss(
            name = "Solaris Drake",
            hp = 2000000, level = 120, rewardXp = 4000000L, rewardGold = 0L, defense = 2000,
            moveset = listOf(
                BossAttack("Solar Breath", 6.0, "Melts everything."),
                BossAttack("Incinerate", 10.0, "Ultimate fire attack.", "IgnoreArmor", cooldown = 7)
            ),
            isLegacy = true
        ),
        Boss(
            name = "Abyssal Magister",
            hp = 3500000, level = 140, rewardXp = 8000000L, rewardGold = 0L, defense = 2500,
            moveset = listOf(
                BossAttack("Void Sphere", 5.0, "A ball of pure nothingness."),
                BossAttack("Reality Tear", 12.0, "Tears space apart.", "IgnoreArmor", cooldown = 6)
            ),
            isLegacy = true
        ),
        Boss(
            name = "Grand Zephyr",
            hp = 5000000, level = 155, rewardXp = 15000000L, rewardGold = 0L, defense = 3000,
            moveset = listOf(
                BossAttack("Gale Blade", 4.0, "Sharp wind cuts deep."),
                BossAttack("Hurricane", 1.5, "Hits 5 times in a storm.", "MultiHit", cooldown = 4, hitCount = 5)
            ),
            isLegacy = true
        ),
        Boss(
            name = "The Deep One",
            hp = 8000000, level = 170, rewardXp = 30000000L, rewardGold = 0L, defense = 4000,
            moveset = listOf(
                BossAttack("Abyssal Pressure", 7.0, "The weight of the ocean."),
                BossAttack("Drown", 0.0, "Stuns the hero.", "Stun", cooldown = 5)
            ),
            isLegacy = true
        ),
        Boss(
            name = "Chronos",
            hp = 12000000, level = 185, rewardXp = 60000000L, rewardGold = 0L, defense = 5000,
            moveset = listOf(
                BossAttack("Time Warp", 5.0, "Strikes before you can move."),
                BossAttack("Rewind", 0.0, "Heals back to previous state.", "Heal", cooldown = 8)
            ),
            isLegacy = true
        ),
        Boss(
            name = "The Fate Binder",
            hp = 20000000, level = 195, rewardXp = 120000000L, rewardGold = 0L, defense = 7000,
            moveset = listOf(
                BossAttack("Destiny's Call", 10.0, "Unavoidable damage."),
                BossAttack("Thread Cut", 20.0, "Attempts to end the life.", "IgnoreArmor", cooldown = 10)
            ),
            isLegacy = true
        ),
        Boss(
            name = "The Genesis Creator",
            hp = 50000000, level = 200, rewardXp = 0L, rewardGold = 0L, defense = 10000,
            moveset = listOf(
                BossAttack("The First Thought", 15.0, "Damage from the beginning."),
                BossAttack("Final Erasure", 100.0, "Erasure from existence.", "IgnoreArmor", cooldown = 15)
            ),
            isLegacy = true
        )
    )

    fun getBoss(name: String): Boss {
        return bosses.find { it.name == name } ?: error("Unknown boss: $name")
    }

    fun resolveBoss(name: String): Boss? {
        return StoryBossDatabase.bosses.find { it.name == name }
            ?: bosses.find { it.name == name }
    }
}
