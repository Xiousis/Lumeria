package com.solerforge.lumeria.database

object StoryBossDatabase {

    val bosses = listOf(
        Boss(
            name = "Captain Garrick",
            hp = 1500,
            level = 8,
            rewardXp = 500,
            rewardGold = 300,
            defense = 25,
            moveset = listOf(
                BossAttack("Veteran Slash", 1.75, "A precise strike from a master.", "Normal"),
                BossAttack("Disarm", 1.0, "Knocks your weapon aside to stun.", "Stun", cooldown = 3),
                BossAttack("Tactical Guard", 0.0, "Assumes a defensive stance.", "DefenseBuff", cooldown = 4)
            ),
            description = "A veteran commander who once led the King's Vanguard. His tactical brilliance is matched only by his unwavering loyalty."
        ),
        Boss(
            name = "Skarr the Raider",
            hp = 3000,
            level = 12,
            rewardXp = 1000,
            rewardGold = 800,
            defense = 40,
            moveset = listOf(
                BossAttack("Marauder Strike", 2.0, "A brutal axe swing.", "Normal"),
                BossAttack("Call the Pack", 0.8, "Summons bandits to strike twice.", "MultiHit", cooldown = 3),
                BossAttack("Bloodlust", 0.0, "Heals upon seeing blood.", "Lifesteal", cooldown = 4)
            ),
            description = "A brutal warlord who rose from the ranks of lawless marauders. He leads his pack with a ferocity that leaves entire villages in ash."
        ),
        Boss(
            name = "The Crystal Warden",
            hp = 6500,
            level = 20,
            rewardXp = 2500,
            rewardGold = 2000,
            defense = 85,
            moveset = listOf(
                BossAttack("Refraction Beam", 2.5, "A beam of light that ignores armor.", "IgnoreArmor", cooldown = 3),
                BossAttack("Crystal Shell", 0.0, "Hardens its body for defense.", "Shield", cooldown = 5),
                BossAttack("Shatter", 1.75, "Explodes crystals outward.", "MultiHit")
            ),
            description = "An ancient construct carved from the purest mana-crystals. It guards the subterranean vaults with tireless, prismatic vigilance."
        ),
        Boss(
            name = "Sir Dorian",
            hp = 12000,
            level = 28,
            rewardXp = 5000,
            rewardGold = 5000,
            defense = 150,
            moveset = listOf(
                BossAttack("Corrupted Strike", 1.8, "A heavy blow tainted by dark magic.", "Normal"),
                BossAttack("Dark Shield", 0.0, "Raises a shield of shadow.", "Shield", cooldown = 3),
                BossAttack("Despair", 1.2, "Lowers player spirit and stuns.", "Stun", cooldown = 4)
            ),
            description = "A fallen knight whose once-noble soul has been consumed by shadow. He wanders the ruins of his former kingdom, seeking redemption through combat."
        ),
        Boss(
            name = "Grondar Earthshaker",
            hp = 25000,
            level = 35,
            rewardXp = 10000,
            rewardGold = 10000,
            defense = 280,
            moveset = listOf(
                BossAttack("Mountain Crush", 2.8, "Crushes the target with stone.", "Normal", cooldown = 2),
                BossAttack("QuakeStomp", 1.5, "Causes the earth to rise and stun.", "Stun", cooldown = 4),
                BossAttack("Stone Armor", 0.0, "Coats itself in thick granite.", "DefenseBuff", cooldown = 5)
            ),
            description = "A massive golem formed from the very bedrock of the world. Each of his steps causes the earth to tremble and mountains to bow."
        ),
        Boss(
            name = "Mirefang",
            hp = 25000,
            level = 42,
            rewardXp = 20000,
            rewardGold = 20000,
            defense = 250,
            moveset = listOf(
                BossAttack("Toxic Bite", 1.75, "A bite that lowers defense.", "DefenseDown"),
                BossAttack("Swamp Lunge", 2.5, "Attacks from the murky depths.", "Normal", cooldown = 2),
                BossAttack("Regenerative Muck", 0.0, "Heals using swamp essence.", "Heal", cooldown = 3)
            ),
            description = "A prehistoric beast that lurks within the heart of the Great Swamp. Its toxic breath has claimed the lives of countless unwary travelers."
        ),
        Boss(
            name = "Infernal Warden",
            hp = 45000,
            level = 50,
            rewardXp = 40000,
            rewardGold = 40000,
            defense = 400,
            moveset = listOf(
                BossAttack("Hellfire", 2.5, "Engulfs the battlefield in flames.", "Normal"),
                BossAttack("Cinder Guard", 0.0, "Reduces damage with heat.", "DefenseBuff", cooldown = 3),
                BossAttack("Inferno Burst", 1.8, "A multi-hit fire blast.", "MultiHit", cooldown = 2)
            ),
            description = "A demon forged in the deepest pits of the underworld. He stands as a gatekeeper to the realms of fire and eternal suffering."
        ),
        Boss(
            name = "The Iron Juggernaut",
            hp = 75000,
            level = 58,
            rewardXp = 60000,
            rewardGold = 60000,
            defense = 550,
            moveset = listOf(
                BossAttack("Steam Blast", 2.2, "Releases scalding steam.", "IgnoreArmor", cooldown = 3),
                BossAttack("Piston Punch", 2.5, "A mechanical punch of high power.", "Normal"),
                BossAttack("Overdrive", 0.0, "Increases speed and attack.", "Buff", cooldown = 6)
            ),
            description = "A steam-powered monstrosity created by the mad artificers of the old world. Its mechanical strength is fueled by a core of pure energy."
        ),
        Boss(
            name = "War Chief Krag",
            hp = 120000,
            level = 65,
            rewardXp = 100000,
            rewardGold = 100000,
            defense = 750,
            moveset = listOf(
                BossAttack("Horde Onslaught", 1.5, "Attacks multiple times.", "MultiHit", cooldown = 2),
                BossAttack("Battle Roar", 1.2, "Stuns with sheer volume.", "Stun", cooldown = 4),
                BossAttack("Warchief's Edge", 3.0, "A stroke that defines leaders.", "Normal")
            ),
            description = "The supreme leader of the unified orcish tribes. His roar can inspire an entire army to march into the jaws of certain death."
        ),
        Boss(
            name = "King Maldrake",
            hp = 250000,
            level = 72,
            rewardXp = 200000,
            rewardGold = 25000,
            defense = 1000,
            moveset = listOf(
                BossAttack("Ghostly Blade", 2.2, "A spectral slash.", "IgnoreArmor"),
                BossAttack("Call of the Grave", 1.5, "Drains life from the living.", "Lifesteal", cooldown = 3),
                BossAttack("Royal Judgment", 3.0, "Crushes the unworthy.", "Normal", cooldown = 5)
            ),
            description = "The spectral remains of a monarch who refused to leave his throne. He rules over a kingdom of ghosts and forgotten memories."
        ),
        Boss(
            name = "Grand Magister Veyra",
            hp = 350000,
            level = 78,
            rewardXp = 300000,
            rewardGold = 200000,
            defense = 800,
            moveset = listOf(
                BossAttack("Arcane Nova", 2.5, "An explosion of pure magic.", "IgnoreArmor"),
                BossAttack("Mana Shield", 0.0, "Creates a barrier of Mana.", "Shield", cooldown = 4),
                BossAttack("Spell Drain", 1.8, "Steals life to fuel spells.", "Lifesteal", cooldown = 3)
            ),
            description = "A prodigy of the arcane arts who delved too deep into forbidden knowledge. Her mastery over mana is both terrifying and beautiful."
        ),
        Boss(
            name = "Elder Wyvern Tyrant",
            hp = 500000,
            level = 85,
            rewardXp = 400000,
            rewardGold = 250000,
            defense = 1500,
            moveset = listOf(
                BossAttack("Sky Strike", 2.5, "Attacks from the clouds.", "Normal"),
                BossAttack("Tail Swipe", 1.8, "A wide arc that stuns.", "Stun", cooldown = 3),
                BossAttack("Dragon Roar", 0.0, "Greatly increases attack power.", "Buff", cooldown = 5)
            ),
            description = "The undisputed master of the high peaks. This ancient predator has reigned supreme for centuries, feared by all who fly."
        ),
        Boss(
            name = "Chrono Sentinel",
            hp = 750000,
            level = 90,
            rewardXp = 600000,
            rewardGold = 500000,
            defense = 1200,
            moveset = listOf(
                BossAttack("Time Warp", 1.2, "Distorts time to stun.", "Stun", cooldown = 2),
                BossAttack("Temporal Rift", 2.5, "Rips through the space-time continuum.", "IgnoreArmor", cooldown = 4),
                BossAttack("Accelerate", 0.0, "Increases speed and damage.", "Buff", cooldown = 6)
            ),
            description = "A guardian of the temporal rifts, existing outside of normal time. It ensures that the threads of destiny remain unbroken and untangled."
        ),
        Boss(
            name = "Lord Umbra",
            hp = 1200000,
            level = 95,
            rewardXp = 1000000,
            rewardGold = 1000000,
            defense = 1800,
            moveset = listOf(
                BossAttack("Shadow Scythe", 3.2, "A blade of darkness.", "Normal"),
                BossAttack("Umbral Grasp", 1.5, "Shadows rise to stun you.", "Stun", cooldown = 3),
                BossAttack("Void Portal", 0.0, "Heals by tapping the void.", "Heal", cooldown = 5)
            ),
            description = "The manifestation of the void's hunger for light. He seeks to envelop the world in a never-ending night of despair."
        ),
        Boss(
            name = "Aurelius",
            hp = 1800000,
            level = 100,
            rewardXp = 1500000,
            rewardGold = 1500000,
            defense = 2200,
            moveset = listOf(
                BossAttack("Golden Lunge", 3.0, "A precise strike from the legend.", "Normal"),
                BossAttack("Sun's Radiance", 0.0, "Heals with divine light.", "Heal", cooldown = 4),
                BossAttack("Champion's Pride", 4.0, "An overwhelming assault.", "MultiHit")
            ),
            description = "A champion chosen by the sun itself to fight the encroaching darkness. His presence brings warmth to allies and scorching heat to foes."
        ),
        Boss(
            name = "High Inquisitor Kael",
            hp = 2500000,
            level = 105,
            rewardXp = 2500000,
            rewardGold = 2500000,
            defense = 3000,
            moveset = listOf(
                BossAttack("Holy Radiance", 3.0, "Blinds and burns.", "Normal"),
                BossAttack("Inquisition", 5.0, "Absolute judgment.", "IgnoreArmor", cooldown = 4),
                BossAttack("Purify", 0.0, "Heals and removes debuffs.", "Heal", cooldown = 5)
            ),
            description = "A fanatic seeker of truth who believes that only through fire can the world be purified. His judgment is swift and uncompromising."
        ),
        Boss(
            name = "Rift Sovereign",
            hp = 4000000,
            level = 108,
            rewardXp = 5000000,
            rewardGold = 5000000,
            defense = 4000,
            moveset = listOf(
                BossAttack("Rift Tear", 3.5, "Tears reality.", "MultiHit", cooldown = 3),
                BossAttack("Dimension Shift", 0.0, "Becomes untouchable.", "EvasionBuff", cooldown = 4),
                BossAttack("Singularity", 7.0, "Consumes everything.", "IgnoreArmor", cooldown = 6)
            ),
            description = "A being from between worlds that has clawed its way into our reality. It commands the very fabric of space to do its bidding."
        ),
        Boss(
            name = "Void Prophet",
            hp = 6000000,
            level = 110,
            rewardXp = 10000000,
            rewardGold = 10000000,
            defense = 5000,
            moveset = listOf(
                BossAttack("Eldritch Whispers", 1.2, "Destroys the mind.", "Stun", cooldown = 3),
                BossAttack("Void Collapse", 6.0, "The void focuses.", "IgnoreArmor", cooldown = 5),
                BossAttack("Soul Tap", 3.5, "Drains life directly.", "Lifesteal", cooldown = 4)
            ),
            description = "A herald of the coming end, speaking words that drive the sane to madness. He prepares the way for the arrival of the World Eater."
        ),
        Boss(
            name = "Ascended Xarthos",
            hp = 12000000,
            level = 112,
            rewardXp = 25000000,
            rewardGold = 25000000,
            defense = 8000,
            moveset = listOf(
                BossAttack("God-Slaying Edge", 5.0, "Ends legends.", "Normal"),
                BossAttack("Reality Erasure", 8.0, "Deletes defenses.", "IgnoreArmor", cooldown = 6),
                BossAttack("Void Eternal", 0.0, "Infinite power.", "Buff", cooldown = 5)
            ),
            description = "A mortal who achieved godhood through the consumption of countless souls. He now views the world as nothing more than a plaything."
        ),
        Boss(
            name = "The World Eater",
            hp = 50000000,
            level = 115,
            rewardXp = 100000000,
            rewardGold = 100000000,
            defense = 15000,
            moveset = listOf(
                BossAttack("Civilization Crumble", 6.0, "Cumbles existence.", "MultiHit"),
                BossAttack("Entropic Breath", 12.0, "Total annihilation.", "IgnoreArmor", cooldown = 5),
                BossAttack("Eternal Hunger", 5.0, "Consumes all life.", "Lifesteal", cooldown = 3)
            ),
            description = "An entity of cosmic proportions that feeds on the essence of dying planets. Its hunger is infinite, and its arrival marks the end of all things."
        ),
        Boss(
            name = "Void Reaper Prime",
            hp = 3000000,
            level = 105,
            rewardXp = 3000000,
            rewardGold = 1000000,
            defense = 3500,
            moveset = listOf(
                BossAttack("Prime Harvest", 3.0, "A strike that reaps the soul.", "Lifesteal"),
                BossAttack("Void Storm", 1.8, "A flurry of void energy.", "MultiHit", cooldown = 2),
                BossAttack("Execution", 5.0, "A finishing blow.", "IgnoreArmor", cooldown = 4)
            ),
            description = "The ultimate executioner of the void, sent to harvest the souls of the powerful. None can escape the swing of its soul-reaping scythe."
        ),
        Boss(
            name = "The Last Herald",
            hp = 8000000,
            level = 108,
            rewardXp = 15000000,
            rewardGold = 5000000,
            defense = 6000,
            moveset = listOf(
                BossAttack("Herald's Proclamation", 3.5, "The announcement of the end.", "Normal"),
                BossAttack("Final Warning", 1.2, "Stuns the listener.", "Stun", cooldown = 3),
                BossAttack("Entropy Beam", 3.0, "Reduces all matter to nothing.", "IgnoreArmor", cooldown = 4)
            ),
            description = "The final messenger of a forgotten age, carrying the weight of a billion lost lives. His proclamation is the silence that follows the storm."
        ),
        Boss(
            name = "Ascended Xarthos Remnant",
            hp = 15000000,
            level = 112,
            rewardXp = 30000000,
            rewardGold = 10000000,
            defense = 10000,
            moveset = listOf(
                BossAttack("Echo of the Void", 4.0, "A lingering strike from Xarthos.", "Normal"),
                BossAttack("Will of the Lost", 0.0, "Heals using the memory of power.", "Heal", cooldown = 4),
                BossAttack("Cursed Existence", 2.5, "Lowers defense significantly.", "DefenseDown", cooldown = 5)
            ),
            description = "A flickering shadow of the god-king's former power. Even in this diminished state, its presence is enough to shatter the weak."
        ),
        Boss(
            name = "World Eater Phase 1",
            hp = 20000000,
            level = 110,
            rewardXp = 50000000,
            rewardGold = 25000000,
            defense = 12000,
            moveset = listOf(
                BossAttack("Consumption", 5.0, "Slowly eating reality.", "Normal"),
                BossAttack("Void Tentacles", 1.5, "Strikes multiple times.", "MultiHit", cooldown = 2),
                BossAttack("Mind Devour", 1.2, "Stuns with cosmic horror.", "Stun", cooldown = 4)
            ),
            description = "The initial manifestation of the cosmic horror as it begins its consumption. The sky turns to ash as the entity's influence grows."
        ),
        Boss(
            name = "World Eater Phase 2",
            hp = 35000000,
            level = 112,
            rewardXp = 75000000,
            rewardGold = 40000000,
            defense = 14000,
            moveset = listOf(
                BossAttack("Starlight Erosion", 6.0, "Erodes the very essence of your soul.", "Normal"),
                BossAttack("Void Storm", 2.0, "A flurry of multi-hitting void energy.", "MultiHit", cooldown = 2),
                BossAttack("Gravity Well", 1.5, "Crushes you under infinite weight.", "Stun", cooldown = 3),
                BossAttack("Entropy Shield", 0.0, "Protects itself with absolute chaos.", "Shield", cooldown = 5)
            ),
            description = "The entity sheds its outer layer, revealing a core of pure, entropic energy. The very laws of physics begin to unravel in its wake."
        ),
        Boss(
            name = "World Eater Final Form",
            hp = 60000000,
            level = 115,
            rewardXp = 150000000,
            rewardGold = 75000000,
            defense = 18000,
            moveset = listOf(
                BossAttack("Universe Erasure", 15.0, "The end of all things. Absolute destruction.", "IgnoreArmor", cooldown = 4),
                BossAttack("Absolute Hunger", 8.0, "Consumes the player's life force.", "Lifesteal", cooldown = 3),
                BossAttack("Reality Collapse", 6.0, "Existence itself breaks down.", "MultiHit", cooldown = 2),
                BossAttack("The Void Eternal", 0.0, "Ascends beyond the concept of death.", "Buff", cooldown = 10)
            ),
            description = "The culmination of total annihilation, where entity and void become one. To look upon this form is to witness the heat death of the universe."
        ),
        // SIDE STORY BOSSES
        Boss(
            name = "Kaela Flameheart",
            hp = 2500,
            level = 18,
            rewardXp = 1500,
            rewardGold = 1000,
            defense = 40,
            moveset = listOf(
                BossAttack("Flame Spear", 2.2, "A burning thrust.", "Normal"),
                BossAttack("Wild Hunt", 1.5, "Strikes multiple times.", "MultiHit", cooldown = 3),
                BossAttack("Battle Cry", 0.0, "Increases damage and heals slightly.", "Buff", cooldown = 5)
            ),
            description = "A warrior-priestess who channels the primal power of fire. Her spear burns with the intensity of a thousand suns."
        ),
        Boss(
            name = "Emberclaw Guardian",
            hp = 8000,
            level = 32,
            rewardXp = 5000,
            rewardGold = 2500,
            defense = 120,
            moveset = listOf(
                BossAttack("Molten Slash", 2.5, "Ignores some defense.", "IgnoreArmor"),
                BossAttack("Pounce", 1.5, "Stuns the target.", "Stun", cooldown = 3)
            ),
            description = "A majestic beast that protects the sacred volcanic groves. Its claws are imbued with the heat of molten magma."
        ),
        Boss(
            name = "Void Scorcher",
            hp = 35000,
            level = 55,
            rewardXp = 25000,
            rewardGold = 10000,
            defense = 450,
            moveset = listOf(
                BossAttack("Void Flame", 3.0, "Consumes mana and health.", "Lifesteal"),
                BossAttack("Supernova", 5.0, "A massive explosion.", "Normal", cooldown = 6)
            ),
            description = "A corrupted elemental that burns with a cold, purple flame. It leaves nothing but frozen ash and hollow silence in its path."
        ),
        Boss(
            name = "Ancestral Guardian",
            hp = 100000,
            level = 72,
            rewardXp = 80000,
            rewardGold = 50000,
            defense = 1000,
            moveset = listOf(
                BossAttack("Spirit Strike", 2.8, "A strike from the past.", "Normal"),
                BossAttack("Ancient Seal", 1.2, "Blocks skills for a turn.", "Stun", cooldown = 4)
            ),
            description = "A spirit of the forest that has protected the land for generations. It draws power from the wisdom of those who came before."
        ),
        Boss(
            name = "The Forgotten Creator",
            hp = 150000000,
            level = 115,
            rewardXp = 0,
            rewardGold = 0,
            defense = 25000,
            moveset = listOf(
                BossAttack("Universal Breath", 6.0, "A blast of pure creation energy.", "Normal"),
                BossAttack("Timeline Fracture", 2.5, "Strikes multiple times, destabilizing reality.", "MultiHit", cooldown = 2),
                BossAttack("Cosmic Silence", 3.5, "A crushing vacuum that stuns.", "Stun", cooldown = 3),
                BossAttack("Architect's Will", 0.0, "Heals 500,000 HP and increases power.", "Buff", cooldown = 5),
                BossAttack("Black Hole Singularity", 5.0, "Consumes your very essence.", "Lifesteal", cooldown = 4),
                BossAttack("The Big Bang", 25.0, "The absolute end. Total annihilation.", "IgnoreArmor", cooldown = 8)
            ),
            description = "A primordial being that shaped the stars and galaxies before being cast aside. It seeks to reclaim its lost dominion over all existence."
        ),
        Boss(
            name = "Corrupted Warden",
            hp = 12000,
            level = 38,
            rewardXp = 8000,
            rewardGold = 5000,
            defense = 100,
            moveset = listOf(
                BossAttack("Soul Lash", 2.2, "A whip made of dark energy.", "Normal"),
                BossAttack("Chain Grasp", 1.0, "Pulls you closer to stun.", "Stun", cooldown = 3)
            ),
            description = "A once-noble protector who has been twisted by the influence of the void. He now guards the gates of the dark realm with mindless fury."
        )
    )

    fun getBoss(name: String): Boss {
        return bosses.find { it.name == name } ?: error("Unknown story boss: $name")
    }
}
