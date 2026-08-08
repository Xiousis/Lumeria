package com.solerforge.lumeria.database

import com.solerforge.lumeria.data.Enemy
import com.solerforge.lumeria.models.ElementType

data class ArenaOpponent(
    val enemy: Enemy,
    val rank: String,
    val description: String,
    val rewardGold: Int,
    val rewardXp: Int,
    val moveset: List<BossAttack> = emptyList()
)

object ArenaEnemyDatabase {
    val opponents = listOf(
        // --- BRONZE RANK ---
        ArenaOpponent(
            enemy = Enemy("Shield Squire", 150, 3, isHumanoid = true),
            rank = "Bronze",
            description = "Hides behind a heavy wooden board.",
            rewardGold = 100,
            rewardXp = 150,
            moveset = listOf(
                BossAttack("Warrior Art: Board Slam", 1.2, "Slams his board forward.", "Normal"),
                BossAttack("Warrior Art: Tunker", 0.8, "A quick jab from safety.", "Normal"),
                BossAttack("Warrior Art: Brace", 0.0, "Hunkers down to heal.", "Heal", cooldown = 3)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Initiate Knight", 250, 5, isHumanoid = true),
            rank = "Bronze",
            description = "A young recruit testing their steel.",
            rewardGold = 250,
            rewardXp = 200,
            moveset = listOf(
                BossAttack("Warrior Art: Squire Strike", 1.3, "A basic overhead swing.", "Normal"),
                BossAttack("Warrior Art: Focus", 0.0, "Gathers strength for next turn.", "Buff", cooldown = 2)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Wild Brawler", 600, 8, isHumanoid = true),
            rank = "Bronze",
            description = "Fights with nothing but his bare fists.",
            rewardGold = 450,
            rewardXp = 400,
            moveset = listOf(
                BossAttack("Warrior Art: Haymaker", 1.8, "A slow but heavy punch.", "Normal", cooldown = 1),
                BossAttack("Warrior Art: Jab Combo", 0.7, "Two quick hits.", "MultiHit"),
                BossAttack("Warrior Art: Roar", 1.0, "Intimidates the opponent.", "DefenseDown", cooldown = 3)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Apprentice Mage", 400, 10, isHumanoid = true),
            rank = "Bronze",
            description = "Still learning how to control his sparks.",
            rewardGold = 600,
            rewardXp = 500,
            moveset = listOf(
                BossAttack("Warrior Art: Spark", 1.5, "A small burst of electricity.", "Normal"),
                BossAttack("Warrior Art: Mana Shield", 0.0, "Protects himself with energy.", "Buff", cooldown = 4)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Drunken Monk", 1200, 12, isHumanoid = true),
            rank = "Bronze",
            description = "Their movements are unpredictable and fluid.",
            rewardGold = 800,
            rewardXp = 600,
            moveset = listOf(
                BossAttack("Warrior Art: Stumble Kick", 1.4, "An awkward but powerful kick.", "Normal"),
                BossAttack("Warrior Art: Swaying Guard", 0.0, "Becomes hard to hit.", "Buff", cooldown = 2),
                BossAttack("Warrior Art: Gourd Gulp", 0.0, "Heals while drinking.", "Heal", cooldown = 4)
            )
        ),

        // --- SILVER RANK ---
        ArenaOpponent(
            enemy = Enemy("Wandering Samurai", 3500, 20, isHumanoid = true, 
                elementalResistances = mapOf(ElementType.Physical to 0.8)),
            rank = "Silver",
            description = "A master of the quick-draw technique.",
            rewardGold = 2500,
            rewardXp = 2000,
            moveset = listOf(
                BossAttack("Warrior Art: Iaido Strike", 2.2, "An incredibly fast draw.", "Normal", cooldown = 1),
                BossAttack("Warrior Art: Hilt Bash", 1.0, "Stuns the opponent with the handle.", "Stun", cooldown = 3),
                BossAttack("Warrior Art: Focus Breathing", 0.0, "Prepares for a counter.", "Buff", cooldown = 4)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Veteran Guard", 5000, 25, isHumanoid = true),
            rank = "Silver",
            description = "Has survived a hundred skirmishes.",
            rewardGold = 4500,
            rewardXp = 3500,
            moveset = listOf(
                BossAttack("Warrior Art: Tactical Strike", 1.6, "A professional sword lunge.", "Normal"),
                BossAttack("Warrior Art: Shield Wall", 0.0, "Greatly increases defense.", "Buff", cooldown = 3),
                BossAttack("Warrior Art: Second Wind", 0.0, "Refuses to fall.", "Heal", cooldown = 5)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Arena Wizard", 8000, 30, isHumanoid = true,
                elementalResistances = mapOf(ElementType.Fire to 0.5, ElementType.Ice to 0.5)),
            rank = "Silver",
            description = "Specializes in elemental crowd control.",
            rewardGold = 8000,
            rewardXp = 6000,
            moveset = listOf(
                BossAttack("Warrior Art: Fire Pillar", 2.0, "Erupts flames under the feet.", "Normal"),
                BossAttack("Warrior Art: Frost Nova", 1.2, "Freezes the area, potentially stunning.", "Stun", cooldown = 3),
                BossAttack("Warrior Art: Arcane Surge", 0.0, "Increases magic power.", "Buff", cooldown = 4)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Shadow Assassin", 10000, 35, isHumanoid = true),
            rank = "Silver",
            description = "Moves faster than the eye can follow.",
            rewardGold = 12000,
            rewardXp = 9000,
            moveset = listOf(
                BossAttack("Warrior Art: Backstab", 3.0, "Strikes from the shadows.", "Normal", cooldown = 2),
                BossAttack("Warrior Art: Twin Daggers", 1.0, "Hits twice in rapid succession.", "MultiHit"),
                BossAttack("Warrior Art: Smoke Bomb", 0.0, "Vanish to dodge next turn.", "Buff", cooldown = 5)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Desert Nomad", 15000, 40, isHumanoid = true,
                elementalResistances = mapOf(ElementType.Fire to 0.7)),
            rank = "Silver",
            description = "Quick and deadly with a curved scimitar.",
            rewardGold = 15000,
            rewardXp = 12000,
            moveset = listOf(
                BossAttack("Warrior Art: Sandstorm Slash", 1.8, "Blinds with sand while cutting.", "DefenseDown"),
                BossAttack("Warrior Art: Scimitar Dance", 0.9, "Three flowing strikes.", "MultiHit"),
                BossAttack("Warrior Art: Canteen", 0.0, "Restores stamina.", "Heal", cooldown = 4)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Elemental Archer", 22000, 45, isHumanoid = true),
            rank = "Silver",
            description = "Fires arrows infused with raw magic.",
            rewardGold = 25000,
            rewardXp = 18000,
            moveset = listOf(
                BossAttack("Warrior Art: Lightning Arrow", 2.5, "A bolt of pure energy.", "Normal"),
                BossAttack("Warrior Art: Rain of Arrows", 0.6, "Fires a volley.", "MultiHit"),
                BossAttack("Warrior Art: Poison Tip", 1.0, "Arrow coated in venom.", "Normal")
            )
        ),

        // --- GOLD RANK ---
        ArenaOpponent(
            enemy = Enemy("Mountain Man", 35000, 50, isHumanoid = true),
            rank = "Gold",
            description = "Tougher than the rocks he calls home.",
            rewardGold = 35000,
            rewardXp = 25000,
            moveset = listOf(
                BossAttack("Warrior Art: Avalanche Slam", 2.8, "A crushing hammer blow that can stun.", "Stun", cooldown = 3),
                BossAttack("Warrior Art: Stone Skin", 0.0, "Body becomes hard as rock, boosting defense.", "Buff", cooldown = 4)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Paladin of Light", 48000, 55, isHumanoid = true,
                elementalResistances = mapOf(ElementType.Holy to 0.2)),
            rank = "Gold",
            description = "A holy warrior with iron-clad faith.",
            rewardGold = 50000,
            rewardXp = 38000,
            moveset = listOf(
                BossAttack("Warrior Art: Holy Smite", 3.5, "Divine energy strikes from above.", "Normal", cooldown = 2),
                BossAttack("Warrior Art: Consecration", 0.0, "Holy light heals and cleanses.", "Heal", cooldown = 5),
                BossAttack("Warrior Art: Aegis Aura", 0.0, "A shield of light that reduces damage.", "Buff", cooldown = 6)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Berserker", 65000, 60, isHumanoid = true,
                elementalResistances = mapOf(ElementType.Physical to 0.9)),
            rank = "Gold",
            description = "Fights with reckless abandon and pure rage.",
            rewardGold = 75000,
            rewardXp = 50000,
            moveset = listOf(
                BossAttack("Warrior Art: Wild Rend", 4.0, "Inaccurate but devastating strike.", "Normal", cooldown = 1),
                BossAttack("Warrior Art: Blood Frenzy", 0.0, "Trades defense for raw power.", "Buff", cooldown = 3),
                BossAttack("Warrior Art: Rampage", 1.5, "Repeatedly bashes the target with force.", "MultiHit")
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Dark Sorcerer", 85000, 65, isHumanoid = true,
                elementalResistances = mapOf(ElementType.Dark to 0.5)),
            rank = "Gold",
            description = "Wields forbidden magic from the abyss.",
            rewardGold = 100000,
            rewardXp = 75000,
            moveset = listOf(
                BossAttack("Warrior Art: Shadow Bolt", 2.5, "A concentrated bolt of dark energy.", "Normal"),
                BossAttack("Warrior Art: Soul Drain", 2.0, "Steals health to sustain himself.", "Lifesteal", cooldown = 3),
                BossAttack("Warrior Art: Abyss Curse", 1.0, "Weakens the hero's spirit and defense.", "DefenseDown", cooldown = 4)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Elite Spellsword", 110000, 70, isHumanoid = true,
                elementalResistances = mapOf(ElementType.Holy to 0.5, ElementType.Dark to 0.5)),
            rank = "Gold",
            description = "A perfect blend of magic and martial prowess.",
            rewardGold = 135000,
            rewardXp = 110000,
            moveset = listOf(
                BossAttack("Warrior Art: Arcane Edge", 2.0, "A magical blade that ignores all armor.", "IgnoreArmor"),
                BossAttack("Warrior Art: Blink Strike", 1.5, "Teleports and strikes instantly.", "Normal", cooldown = 2),
                BossAttack("Warrior Art: Mana Barrier", 0.0, "A shield of raw magical energy.", "Buff", cooldown = 4)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Crag Golem", 150000, 75, isHumanoid = false,
                elementalResistances = mapOf(ElementType.Physical to 0.5)),
            rank = "Gold",
            description = "A living pile of enchanted stone.",
            rewardGold = 180000,
            rewardXp = 150000,
            moveset = listOf(
                BossAttack("Warrior Art: Seismic Slam", 2.5, "Shakes the arena to stun the hero.", "Stun", cooldown = 3),
                BossAttack("Warrior Art: Meteor Throw", 3.5, "Launches a massive boulder.", "Normal", cooldown = 2),
                BossAttack("Warrior Art: Core Repair", 0.0, "Grows new stone skin through magic.", "Heal", cooldown = 5)
            )
        ),

        // --- PLATINUM RANK ---
        ArenaOpponent(
            enemy = Enemy("Grandmaster Monk", 250000, 80, isHumanoid = true),
            rank = "Platinum",
            description = "Has achieved perfect harmony with the void.",
            rewardGold = 300000,
            rewardXp = 250000,
            moveset = listOf(
                BossAttack("Warrior Art: Void Palm", 6.0, "The legendary killing strike that bypasses all defense.", "IgnoreArmor", cooldown = 4),
                BossAttack("Warrior Art: Gale Kick", 2.5, "A swift airborne attack that strikes like a storm.", "Normal"),
                BossAttack("Warrior Art: Transcendence", 0.0, "Enters a state of perfect focus, increasing damage.", "Buff", cooldown = 5)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Imperial General", 350000, 85, isHumanoid = true),
            rank = "Platinum",
            description = "A brilliant strategist and master swordsman.",
            rewardGold = 500000,
            rewardXp = 400000,
            moveset = listOf(
                BossAttack("Warrior Art: Conqueror's Slash", 3.5, "A calculated strike designed to break armor.", "IgnoreArmor"),
                BossAttack("Warrior Art: Legion Assault", 1.2, "Calls upon the spirits of his legion to strike!", "MultiHit", cooldown = 3),
                BossAttack("Warrior Art: Iron Discipline", 0.0, "Focuses through pain to regain HP.", "Heal", cooldown = 4)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Royal Duelist", 550000, 90, isHumanoid = true,
                elementalResistances = mapOf(ElementType.Lightning to 0.5)),
            rank = "Platinum",
            description = "A swift fencer from the capital's high court.",
            rewardGold = 800000,
            rewardXp = 650000,
            moveset = listOf(
                BossAttack("Warrior Art: Lightning Lunge", 4.5, "A strike as fast as a lightning bolt.", "Normal", cooldown = 1),
                BossAttack("Warrior Art: Grand Parry", 1.2, "Deflects and stuns the opponent with finesse.", "Stun", cooldown = 3),
                BossAttack("Warrior Art: Noble Dance", 0.0, "Masterful footwork that increases speed and damage.", "Buff", cooldown = 3)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Void Cultist", 750000, 94, isHumanoid = true),
            rank = "Platinum",
            description = "Draws power from the collapsing void.",
            rewardGold = 1200000,
            rewardXp = 900000,
            moveset = listOf(
                BossAttack("Warrior Art: Obsidian Shards", 1.2, "Fires razor-sharp dark crystals in a flurry.", "MultiHit"),
                BossAttack("Warrior Art: Entropy Siphon", 2.5, "Drains the life energy of the opponent.", "Lifesteal", cooldown = 3),
                BossAttack("Warrior Art: Void Fracture", 5.0, "Tears the fabric of space around the hero.", "IgnoreArmor", cooldown = 4)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Dragon Knight", 950000, 98, isHumanoid = true,
                elementalResistances = mapOf(ElementType.Fire to 0.3)),
            rank = "Platinum",
            description = "Wields a lance of pure dragon-fire.",
            rewardGold = 1600000,
            rewardXp = 1250000,
            moveset = listOf(
                BossAttack("Warrior Art: Inferno Lance", 6.0, "A flaming pierce that burns through anything.", "Normal", cooldown = 2),
                BossAttack("Warrior Art: Wyvern Roar", 1.2, "A deafening roar that stuns the hero.", "Stun", cooldown = 4),
                BossAttack("Warrior Art: Dragon Heart", 0.0, "Surrounds himself in an aura of pure dragon-fire.", "Buff", cooldown = 5)
            )
        ),

        // --- MASTER RANK ---
        ArenaOpponent(
            enemy = Enemy("Shogun of Shadows", 1200000, 100, isHumanoid = true,
                elementalResistances = mapOf(ElementType.Physical to 0.5, ElementType.Dark to 0.2)),
            rank = "Master",
            description = "The ultimate test of a warrior's resolve.",
            rewardGold = 2500000,
            rewardXp = 1800000,
            moveset = listOf(
                BossAttack("Warrior Art: Shadow Siphon", 3.0, "Strikes from the dark to steal life.", "Lifesteal", cooldown = 3),
                BossAttack("Warrior Art: Nightfall", 7.0, "The world turns black as he strikes with absolute power.", "IgnoreArmor", cooldown = 5),
                BossAttack("Warrior Art: Shadow Clone", 1.5, "Multiple strikes from all directions.", "MultiHit", cooldown = 3)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Grand Arbiter", 1800000, 100, isHumanoid = true,
                elementalResistances = mapOf(ElementType.Holy to 0.1, ElementType.Dark to 0.1)),
            rank = "Master",
            description = "Judges all who seek true power.",
            rewardGold = 4000000,
            rewardXp = 3000000,
            moveset = listOf(
                BossAttack("Warrior Art: Divine Verdict", 8.0, "The final judgment that bypasses all defense.", "IgnoreArmor", cooldown = 5),
                BossAttack("Warrior Art: Law of Restoration", 0.0, "Invokes the law of life to regain massive HP.", "Heal", cooldown = 6),
                BossAttack("Warrior Art: Holy Silence", 0.0, "Strips the opponent of their will to fight.", "Stun", cooldown = 4)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Lord of the Void", 2500000, 100, isHumanoid = false),
            rank = "Master",
            description = "A being of pure, destructive energy.",
            rewardGold = 7500000,
            rewardXp = 6000000,
            moveset = listOf(
                BossAttack("Warrior Art: Event Horizon", 10.0, "Everything is consumed by the void.", "IgnoreArmor", cooldown = 5),
                BossAttack("Warrior Art: Rift Barrage", 2.0, "Endless pulses of destructive void energy.", "MultiHit"),
                BossAttack("Warrior Art: Cosmic Devour", 4.0, "Absorbs the essence of reality itself.", "Lifesteal", cooldown = 4)
            )
        ),
        ArenaOpponent(
            enemy = Enemy("Eternal Champion", 5000000, 100, isHumanoid = true),
            rank = "Master",
            description = "The legend who never knew defeat.",
            rewardGold = 15000000,
            rewardXp = 12500000,
            moveset = listOf(
                BossAttack("Warrior Art: Zenith Slash", 15.0, "The strike that ended an era. Absolute destruction.", "IgnoreArmor", cooldown = 5),
                BossAttack("Warrior Art: Thousand Cuts", 3.0, "A flurry of legendary hits that cannot be escaped.", "MultiHit", cooldown = 3),
                BossAttack("Warrior Art: Undying Will", 0.0, "Refuses to fall, healing through sheer determination.", "Heal", cooldown = 6)
            )
        )
    )

    fun getOpponent(name: String): ArenaOpponent? {
        return opponents.find { it.enemy.name == name }
    }
}
