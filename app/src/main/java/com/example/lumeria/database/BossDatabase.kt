package com.example.lumeria.database

data class BossAttack(
    val name: String,
    val multiplier: Double = 1.0,
    val description: String,
    val effectType: String = "Normal", // "Stun", "Heal", "DefenseDown", "MultiHit", "IgnoreArmor", "Buff", "Lifesteal"
    val cooldown: Int = 0
)

data class Boss(
    val name: String,
    val hp: Int,
    val level: Int,
    val rewardXp: Int,
    val rewardGold: Int,
    val defense: Int,
    val moveset: List<BossAttack>
)

object BossDatabase {

    val bosses = listOf(
        // WORLD BOSSES
        Boss(
            name = "Training Captain",
            hp = 500,
            level = 6,
            rewardXp = 250,
            rewardGold = 500,
            defense = 10,
            moveset = listOf(
                BossAttack("Shield Bash", 1.25, "A heavy slam that can stun.", "Stun", cooldown = 2),
                BossAttack("Commanding Strike", 1.75, "A powerful veteran strike.", "Normal", cooldown = 0),
                BossAttack("Rally Command", 0.0, "Regains composure and heals.", "Heal", cooldown = 3)
            )
        ),
        Boss(
            name = "Goblin King",
            hp = 2500,
            level = 16,
            rewardXp = 800,
            rewardGold = 2000,
            defense = 25,
            moveset = listOf(
                BossAttack("Crown Smash", 2.0, "A heavy club attack.", "Normal", cooldown = 0),
                BossAttack("Goblin Ambush", 1.0, "Calls reinforcements to attack twice.", "MultiHit", cooldown = 2),
                BossAttack("King's Roar", 1.0, "Reduces player defense for 3 turns.", "DefenseDown", cooldown = 3)
            )
        ),
        Boss(
            name = "Crystal Guardian",
            hp = 6000,
            level = 26,
            rewardXp = 2500,
            rewardGold = 7500,
            defense = 50,
            moveset = listOf(
                BossAttack("Crystal Shards", 0.6, "Fires razor-sharp crystals 3 times.", "MultiHit", cooldown = 0),
                BossAttack("Reflective Barrier", 0.0, "Blocks 50% of incoming damage.", "Buff", cooldown = 4),
                BossAttack("Prismatic Beam", 2.5, "A massive beam that ignores armor.", "IgnoreArmor", cooldown = 3)
            )
        ),
        Boss(
            name = "Stone Titan",
            hp = 12000,
            level = 36,
            rewardXp = 6000,
            rewardGold = 20000,
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
            rewardXp = 12000,
            rewardGold = 60000,
            defense = 120,
            moveset = listOf(
                BossAttack("Toxic Spit", 1.25, "A poisonous attack.", "Normal", cooldown = 0),
                BossAttack("Devour", 3.5, "Deals heavy damage and heals.", "Lifesteal", cooldown = 3),
                BossAttack("Bog Mist", 0.0, "Summons thick mist to dodge.", "Buff", cooldown = 5)
            )
        ),
        Boss(
            name = "Ancient Dragon",
            hp = 50000,
            level = 66,
            rewardXp = 25000,
            rewardGold = 150000,
            defense = 180,
            moveset = listOf(
                BossAttack("Dragon Breath", 4.5, "A massive fire attack.", "Normal", cooldown = 4),
                BossAttack("Wing Buffet", 1.0, "A wind attack that hits twice.", "MultiHit", cooldown = 0),
                BossAttack("Ancient Fury", 0.0, "Dragon becomes enraged!", "Buff", cooldown = 6)
            )
        ),
        Boss(
            name = "Lord Xarthos",
            hp = 100000,
            level = 80,
            rewardXp = 50000,
            rewardGold = 400000,
            defense = 300,
            moveset = listOf(
                BossAttack("Void Slash", 2.5, "A blade of void energy.", "Normal", cooldown = 0),
                BossAttack("Soul Drain", 2.0, "Steals life from the target.", "Lifesteal", cooldown = 3),
                BossAttack("Collapse Reality", 6.0, "Ultimate void technique.", "IgnoreArmor", cooldown = 5)
            )
        )
    )

    fun getBoss(name: String): Boss {
        return bosses.find { it.name == name } ?: bosses[0]
    }
}
