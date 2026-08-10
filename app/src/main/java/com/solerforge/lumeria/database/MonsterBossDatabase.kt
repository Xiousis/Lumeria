package com.solerforge.lumeria.database

object MonsterBossDatabase {

    val bosses = listOf(
        Boss(
            name = "Sergeant Miller",
            hp = 1200,
            level = 8,
            rewardXp = 500,
            rewardGold = 300,
            defense = 30,
            moveset = listOf(
                BossAttack("Heavy Shield", 0.0, "Boosts defense.", "DefenseBuff"),
                BossAttack("Guard Strike", 1.8, "Slow but heavy blow.", "Normal")
            ),
            description = "A stern guard who enforces order in the slums. He has no patience for troublemakers."
        ),
        Boss(
            name = "Captain Harlen",
            hp = 5000,
            level = 25,
            rewardXp = 3000,
            rewardGold = 2500,
            defense = 120,
            moveset = listOf(
                BossAttack("Market Justice", 2.2, "A swift rapier thrust.", "Normal"),
                BossAttack("Call for Backup", 0.8, "Guards join the fray.", "MultiHit", cooldown = 3)
            ),
            description = "Commander of the Market District. He takes his duty to protect commerce very seriously."
        ),
        Boss(
            name = "Lady Elara",
            hp = 15000,
            level = 45,
            rewardXp = 10000,
            rewardGold = 8000,
            defense = 250,
            moveset = listOf(
                BossAttack("Rose Thorn", 1.8, "A piercing attack that bleeds.", "Bleed"),
                BossAttack("Garden's Grace", 0.0, "Heals using the flowers.", "Heal", cooldown = 4)
            ),
            description = "A noblewoman who protects the Royal Gardens with both blade and petal-magic."
        ),
        Boss(
            name = "Grand Inquisitor",
            hp = 45000,
            level = 65,
            rewardXp = 35000,
            rewardGold = 20000,
            defense = 500,
            moveset = listOf(
                BossAttack("Holy Fire", 2.5, "Burn the heretic!", "Normal"),
                BossAttack("Light's Judgment", 3.5, "Absolute punishment.", "IgnoreArmor", cooldown = 4)
            ),
            description = "The highest authority in the Temple District. He sees monsters where others see shadows."
        ),
        Boss(
            name = "General Ironheart",
            hp = 120000,
            level = 85,
            rewardXp = 100000,
            rewardGold = 50000,
            defense = 1500,
            moveset = listOf(
                BossAttack("Iron Fortress", 0.0, "Becomes nearly invulnerable.", "DefenseBuff", cooldown = 5),
                BossAttack("Heart-Piercer", 4.0, "A legendary spear thrust.", "Normal")
            ),
            description = "The supreme commander of the King's Guard. His heart is as cold and hard as the steel he wears."
        ),
        Boss(
            name = "King Lumeria",
            hp = 500000,
            level = 105,
            rewardXp = 500000,
            rewardGold = 250000,
            defense = 3000,
            moveset = listOf(
                BossAttack("King's Wrath", 4.0, "The power of a kingdom.", "MultiHit"),
                BossAttack("Divine Sovereignty", 0.0, "Full HP restore and power up.", "Heal", cooldown = 10)
            ),
            description = "The ruler of Lumeria. He has led his people through countless crises, but this might be the last."
        ),
        Boss(
            name = "Master of Riches",
            hp = 75000000,
            level = 135,
            rewardXp = 50000000,
            rewardGold = 100000000,
            defense = 12000,
            moveset = listOf(
                BossAttack("Gilded Rain", 4.0, "A shower of molten gold.", "MultiHit", hitCount = 3),
                BossAttack("Bribe", 0.0, "Attempts to buy your loyalty, stunning you.", "Stun", cooldown = 4),
                BossAttack("Treasury Blast", 8.0, "Releases the energy of a thousand artifacts.", "IgnoreArmor", cooldown = 6)
            ),
            description = "The keeper of Lumeria's forbidden wealth. He believes that everything, including life itself, has a price."
        ),
        Boss(
            name = "The First Hero",
            hp = 120000000,
            level = 150,
            rewardXp = 100000000,
            rewardGold = 0,
            defense = 18000,
            moveset = listOf(
                BossAttack("Original Strike", 6.0, "The first technique ever recorded.", "Normal"),
                BossAttack("Valor's Echo", 0.0, "Heals using the memories of war.", "Heal", cooldown = 5),
                BossAttack("Legacy Blade", 10.0, "A strike that carries the weight of history.", "IgnoreArmor", cooldown = 8)
            ),
            description = "The legendary founder of the kingdom. Even in death, his spirit remains the ultimate guardian of Lumeria's legacy."
        ),
        Boss(
            name = "The Grand Master",
            hp = 250000000,
            level = 175,
            rewardXp = 250000000,
            rewardGold = 0,
            defense = 25000,
            moveset = listOf(
                BossAttack("Perfect Technique", 8.0, "A strike with no wasted movement.", "Normal"),
                BossAttack("Meditative Stance", 0.0, "Becomes untouchable through focus.", "EvasionBuff", cooldown = 4),
                BossAttack("Shatter Point", 15.0, "Hits the exact weakness of the target.", "IgnoreArmor", cooldown = 6)
            ),
            description = "The teacher of kings and heroes. He has achieved a level of martial perfection that transcends the physical realm."
        ),
        Boss(
            name = "The Hero King",
            hp = 500000000,
            level = 195,
            rewardXp = 500000000,
            rewardGold = 0,
            defense = 40000,
            moveset = listOf(
                BossAttack("Sovereign Command", 10.0, "Reality bends to his will.", "MultiHit", hitCount = 5),
                BossAttack("Divine Aegis", 0.0, "A shield of pure cosmic power.", "Shield", cooldown = 10),
                BossAttack("End of Days", 50.0, "A technique designed to end all existence.", "IgnoreArmor", cooldown = 15)
            ),
            description = "The absolute pinnacle of the Hero Kingdom. He is no longer just a king, but a god of order defending his dying world."
        )
    )

    fun getBoss(name: String): Boss {
        return bosses.find { it.name == name } ?: error("Unknown monster boss: $name")
    }
}
