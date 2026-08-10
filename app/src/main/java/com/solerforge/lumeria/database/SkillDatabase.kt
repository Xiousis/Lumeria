package com.solerforge.lumeria.database

import com.solerforge.lumeria.models.ElementType
import com.solerforge.lumeria.models.Skill
import com.solerforge.lumeria.models.StatusEffect

object SkillDatabase {
    val skills = listOf(
        Skill("None", "No skill equipped.", 0, 0, 0, "Support"),
        
        // --- BASE & SHARED SKILLS ---
        Skill("Slash", "Basic sword attack.", 0, 0, 1, "Attack", 1.0, evolvesTo = "Omnislash", requiredClasses = listOf("Warrior", "Samurai", "Assassin", "Paladin")),
        Skill("Heavy Strike", "A slow but powerful overhead swing.", 0, 2, 1, "Attack", 2.5, evolvesTo = "Grand Slam", requiredClasses = listOf("Warrior", "Berserker", "Monk")),
        Skill("Magic Bolt", "A simple magical blast.", 3, 2, 1, "Attack", 2.5, elementType = ElementType.Ice, evolvesTo = "Arcane Comet", requiredClasses = listOf("Mage", "Necromancer")),
        Skill("Heal", "Small burst of recovery.", 5, 3, 1, "Support", multiplier = 1.0, effectType = "Heal", evolvesTo = "Holy Grace", requiredClasses = listOf("Mage", "Paladin", "Monk", "Bard")),
        Skill("Quick Draw", "A lightning-fast strike before the enemy reacts.", 4, 0, 1, "Attack", 1.8, requiredClasses = listOf("Samurai", "Assassin", "Archer")),
        Skill("Parry", "Deflect an incoming attack and reduce damage.", 0, 2, 1, "Support", effectType = "Parry", requiredClasses = listOf("Warrior", "Samurai", "Paladin")),
        Skill("Mana Shield", "Uses mana to absorb incoming damage.", 0, 4, 1, "Buff", effectType = "DefenseBuff", requiredClasses = listOf("Mage")),
        Skill("Smoke Bomb", "Increases your chance to dodge.", 8, 5, 1, "Buff", effectType = "EvasionBuff", requiredClasses = listOf("Assassin")),
        Skill("Guard", "Hunker down to increase your defense.", 0, 3, 1, "Buff", effectType = "DefenseBuff", requiredClasses = listOf("Warrior", "Paladin", "Monk")),

        // --- EVOLVED SKILLS ---
        Skill("Omnislash", "Master level flurry. Faster and sharper.", 2, 0, 99, "Attack", 1.5, "MultiHit", requiredClasses = listOf("Warrior", "Samurai", "Assassin", "Paladin"), isEvolutionOnly = true),
        Skill("Grand Slam", "A massive earth-shaking strike.", 5, 2, 99, "Attack", 4.5, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Warrior", "Berserker", "Monk"), isEvolutionOnly = true),
        Skill("Arcane Comet", "A rain of magical fire and ice.", 8, 3, 99, "Attack", 5.0, elementType = ElementType.Ice, statusEffect = StatusEffect.Burn, requiredClasses = listOf("Mage", "Necromancer"), isEvolutionOnly = true),
        Skill("Holy Grace", "Divine light that heals and boosts.", 12, 4, 99, "Support", 2.5, "Heal", requiredClasses = listOf("Mage", "Paladin", "Monk", "Bard"), isEvolutionOnly = true),

        // --- HUMAN HEROES ---
        Skill("Heroic Strike", "Determination-infused blow.", 3, 0, 1, "Attack", 2.0, requiredClasses = listOf("Hero")),
        Skill("Radiant Blade", "Shining blade of light.", 45, 4, 110, "Attack", 10.0, elementType = ElementType.Holy, requiredClasses = listOf("Hero")),
        Skill("Final Judgment", "Ultimate strike of justice.", 120, 10, 190, "Attack", 40.0, elementType = ElementType.Holy, requiredClasses = listOf("Hero")),
        Skill("Shield Wall", "Magical barrier protection.", 4, 3, 1, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("King's Guard")),
        Skill("Aegis of the King", "Ultimate crown protection.", 140, 12, 195, "Buff", 45.0, "DefenseBuff", requiredClasses = listOf("King's Guard")),
        Skill("Arcane Flash", "Quick burst of energy.", 3, 1, 1, "Attack", 1.8, elementType = ElementType.Fire, requiredClasses = listOf("High Sage")),
        Skill("Sage's Wisdom", "Pinnacle of magic.", 150, 15, 200, "Buff", 50.0, "SuperBuff", requiredClasses = listOf("High Sage")),
        Skill("Tactical Strike", "Strategically placed blow.", 3, 0, 1, "Attack", 2.0, requiredClasses = listOf("Grand Commander")),
        Skill("Commander's Wrath", "Unleashed tactical fury.", 130, 12, 195, "Attack", 42.0, requiredClasses = listOf("Grand Commander")),

        // --- ELVEN HEROES ---
        Skill("Mana Spark", "Tiny potent mana spark.", 2, 0, 1, "Attack", 1.7, requiredClasses = listOf("High Elf")),
        Skill("Celestial Singularity", "Astral plane rift.", 130, 12, 190, "Attack", 42.0, requiredClasses = listOf("High Elf")),
        Skill("Mystic Slash", "Magically enhanced strike.", 4, 1, 1, "Attack", 2.1, requiredClasses = listOf("Spellblade")),
        Skill("Blade of Eternity", "Transcendent time strike.", 125, 10, 190, "Attack", 38.0, requiredClasses = listOf("Spellblade")),
        Skill("Leaf Blade", "Nature-infused cut.", 3, 0, 1, "Attack", 1.9, requiredClasses = listOf("Forest Warden")),
        Skill("Wrath of the Woods", "Deep forest protection.", 120, 10, 190, "Attack", 40.0, requiredClasses = listOf("Forest Warden")),

        // --- DWARF HEROES ---
        Skill("Stone Strike", "Heavy mountain-like blow.", 5, 2, 1, "Attack", 2.4, requiredClasses = listOf("Mountain Thane")),
        Skill("Earthbreaker", "Foundation-shattering strike.", 145, 15, 190, "Attack", 48.0, requiredClasses = listOf("Mountain Thane")),
        Skill("Rune of Power", "Ancient inscribed energy.", 4, 2, 1, "Buff", 2.0, "DamageBuff", requiredClasses = listOf("Runekeeper")),
        Skill("Master Rune", "Ultimate runic manifestation.", 135, 12, 190, "Attack", 44.0, requiredClasses = listOf("Runekeeper")),
        Skill("Steel Bastion", "Immovable metal wall.", 6, 3, 1, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Iron Guard")),
        Skill("Iron Fortress", "Ultimate dwarven steel defense.", 150, 20, 195, "Buff", 48.0, "DefenseBuff", requiredClasses = listOf("Iron Guard")),

        // --- GNOME HEROES ---
        Skill("Wrench Toss", "Mechanical tool projectile.", 3, 1, 1, "Attack", 1.6, requiredClasses = listOf("Clockwork Tinkerer")),
        Skill("Masterwork Golem", "Massive mechanical guardian.", 145, 15, 195, "Attack", 46.0, requiredClasses = listOf("Clockwork Tinkerer")),
        Skill("Arcane Spark", "Inventor's magical ignition.", 3, 1, 1, "Attack", 1.8, requiredClasses = listOf("Arcane Inventor")),
        Skill("Grand Invention", "Peak of gnome technology.", 140, 15, 195, "Attack", 45.0, requiredClasses = listOf("Arcane Inventor")),

        // --- HALFLING HEROES ---
        Skill("Backstab", "Lethal strike from behind.", 22, 3, 50, "Attack", 5.0, requiredClasses = listOf("Master Thief")),
        Skill("Lethal Precision", "Perfectly hit vital organs.", 120, 8, 190, "Attack", 42.0, requiredClasses = listOf("Master Thief")),
        Skill("Lucky Hit", "Fortune-favored strike.", 2, 0, 1, "Attack", 2.2, requiredClasses = listOf("Lucky Wanderer")),
        Skill("Fortune's End", "Ultimate luck-based destruction.", 110, 10, 190, "Attack", 40.0, requiredClasses = listOf("Lucky Wanderer")),

        // --- HALF-ORC HEROES ---
        Skill("Brutal Bash", "Crude effective blow.", 4, 2, 1, "Attack", 2.3, requiredClasses = listOf("Tribal Warlord")),
        Skill("Horde's Wrath", "Unleashed tribal fury.", 150, 15, 190, "Attack", 48.0, requiredClasses = listOf("Tribal Warlord")),
        Skill("Pit Strike", "Gladiatorial combat move.", 4, 1, 1, "Attack", 2.2, requiredClasses = listOf("Pit Fighter")),
        Skill("Arena Champion", "Ultimate combat mastery.", 140, 12, 190, "Attack", 45.0, requiredClasses = listOf("Pit Fighter")),

        // --- HALF-GIANT HEROES ---
        Skill("Giant's Grip", "Immense crushing hand strength.", 5, 2, 1, "Attack", 2.5, requiredClasses = listOf("Colossus")),
        Skill("Titan's Awakening", "True titan power gain.", 160, 20, 195, "Buff", 45.0, "SuperBuff", requiredClasses = listOf("Colossus")),

        // --- ANGEL HEROES ---
        Skill("Holy Bolt", "Pure celestial light beam.", 3, 1, 1, "Attack", 1.9, elementType = ElementType.Holy, requiredClasses = listOf("Seraph")),
        Skill("Judgment of the Heavens", "Celestial fire rain.", 140, 10, 195, "Attack", 42.0, elementType = ElementType.Holy, requiredClasses = listOf("Seraph")),
        Skill("Divine Shield", "Unbreakable holy barrier.", 8, 4, 1, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Divine Shield")),
        Skill("Absolute Sanctity", "Supreme holy protection.", 155, 20, 195, "Buff", 50.0, "DefenseBuff", requiredClasses = listOf("Divine Shield")),
        Skill("Light Arrow", "Divine accuracy light beam.", 4, 1, 1, "Attack", 2.0, elementType = ElementType.Holy, requiredClasses = listOf("Celestial Archer")),
        Skill("Starfall", "Raining beams of divinity.", 130, 12, 190, "Attack", 44.0, "MultiHit", elementType = ElementType.Holy, requiredClasses = listOf("Celestial Archer")),

        // --- DRAGONKIN MONSTERS ---
        Skill("Dragon Slash", "Fire-infused claw strike.", 4, 1, 1, "Attack", 2.2, elementType = ElementType.Fire, requiredClasses = listOf("Dragon Knight")),
        Skill("Ascended Dragon Soul", "Draconic power manifestation.", 130, 15, 195, "Attack", 40.0, elementType = ElementType.Fire, requiredClasses = listOf("Dragon Knight")),
        Skill("Spark", "Small igniting flame.", 3, 0, 1, "Attack", 1.8, elementType = ElementType.Fire, statusEffect = StatusEffect.Burn, requiredClasses = listOf("Flame Breather")),
        Skill("Supernova", "Dying star rivals heat.", 140, 10, 180, "Attack", 45.0, elementType = ElementType.Fire, requiredClasses = listOf("Flame Breather")),
        Skill("Scale Harden", "Hardening dragon scales.", 5, 2, 1, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Scaled Vanguard")),
        Skill("Eternal Draconic Aegis", "Ultimate scale defense.", 150, 18, 195, "Buff", 48.0, "DefenseBuff", requiredClasses = listOf("Scaled Vanguard")),

        // --- DARK ELF MONSTERS ---
        Skill("Shadow Strike", "Darkness cover blow.", 3, 1, 1, "Attack", 1.9, elementType = ElementType.Dark, requiredClasses = listOf("Shadow Stalker")),
        Skill("Eclipse", "Blinding total darkness.", 110, 8, 190, "Attack", 36.0, elementType = ElementType.Dark, requiredClasses = listOf("Shadow Stalker")),
        Skill("Dark Edge", "Mastery of silent lethality.", 4, 1, 1, "Attack", 2.0, elementType = ElementType.Dark, requiredClasses = listOf("Nightblade")),
        Skill("Night's End", "Final silence of the blade.", 125, 10, 190, "Attack", 40.0, elementType = ElementType.Dark, requiredClasses = listOf("Nightblade")),

        // --- VAMPIRE MONSTERS ---
        Skill("Blood Needle", "Hardened blood projectile.", 3, 1, 1, "Attack", 1.8, statusEffect = StatusEffect.Bleed, requiredClasses = listOf("Blood Mage")),
        Skill("Blood God's Descent", "Blood deity avatar manifestation.", 145, 12, 195, "Attack", 45.0, statusEffect = StatusEffect.Bleed, requiredClasses = listOf("Blood Mage")),
        Skill("Bloodlust", "Undead juggernaut fury.", 6, 2, 1, "Buff", 3.0, "DamageBuff", requiredClasses = listOf("Dread Knight")),
        Skill("Vampiric Dominance", "Absolute blood control.", 160, 20, 195, "Attack", 50.0, "Lifesteal", requiredClasses = listOf("Dread Knight")),

        // --- WEREWOLF MONSTERS ---
        Skill("Feral Lunge", "Throat-leaping strike.", 4, 1, 1, "Attack", 2.1, requiredClasses = listOf("Pack Leader")),
        Skill("Primal Moonhowl", "Shattering enemy will.", 140, 10, 190, "Attack", 44.0, statusEffect = StatusEffect.Stun, requiredClasses = listOf("Pack Leader")),
        Skill("Claw Fury", "Beastly destruction flurry.", 5, 1, 1, "Attack", 1.5, "MultiHit", requiredClasses = listOf("Feral Berserker")),
        Skill("Lunacy", "Absolute feral madness.", 150, 15, 190, "Attack", 48.0, requiredClasses = listOf("Feral Berserker")),

        // --- BEHOLDER MONSTERS ---
        Skill("Disintegration Ray", "Turning anything to dust.", 6, 2, 1, "Attack", 2.4, requiredClasses = listOf("Eye Tyrant")),
        Skill("Total Reality Collapse", "Every eye fires rays.", 180, 30, 185, "Attack", 50.0, "MultiHit", requiredClasses = listOf("Eye Tyrant")),

        // --- SLIME MONSTERS ---
        Skill("Acid Spray", "Corrosive acid spit.", 3, 1, 1, "Attack", 1.9, statusEffect = StatusEffect.Poison, requiredClasses = listOf("Corrosive Striker")),
        Skill("Universal Solvent", "Armor-dissolving strike.", 140, 10, 190, "Attack", 48.0, "IgnoreArmor", requiredClasses = listOf("Corrosive Striker")),

        // --- FAIRY MONSTERS ---
        Skill("Fairy Dust", "Confusing sparkling dust.", 2, 1, 1, "Support", 1.0, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Pixie Trickster")),
        Skill("Ethereal Waltz", "Deadly untouchable dance.", 130, 12, 190, "Attack", 40.0, "MultiHit", requiredClasses = listOf("Pixie Trickster")),

        // --- GHOST MONSTERS ---
        Skill("Haunting Wail", "Soul-damaging scream.", 4, 2, 1, "Attack", 2.2, elementType = ElementType.Dark, requiredClasses = listOf("Haunting Spectre")),
        Skill("Void Requiem", "Life-draining end song.", 150, 15, 190, "Attack", 48.0, "Lifesteal", elementType = ElementType.Dark, requiredClasses = listOf("Haunting Spectre")),
        Skill("Ghost Strike", "Wall-passing killing blow.", 4, 0, 1, "Attack", 2.1, requiredClasses = listOf("Ethereal Assassin")),
        Skill("Soul Harvest", "Fallen essence collection.", 130, 10, 190, "Attack", 45.0, "Lifesteal", requiredClasses = listOf("Soul Reaper")),

        // --- CYBORG MONSTERS ---
        Skill("Logic Bolt", "Electrical energy burst.", 3, 1, 1, "Attack", 1.8, elementType = ElementType.Lightning, statusEffect = StatusEffect.Shock, requiredClasses = listOf("Technomancer")),
        Skill("Singularity Protocol", "Self-sustaining energy rift.", 150, 12, 190, "Attack", 48.0, requiredClasses = listOf("Technomancer")),

        // --- DEMON MONSTERS ---
        Skill("Void Rend", "Void energy essence tearing.", 5, 2, 1, "Attack", 2.5, elementType = ElementType.Dark, requiredClasses = listOf("Void Reaver")),
        Skill("Total Annihilation", "Nothing but void remains.", 150, 12, 190, "Attack", 50.0, elementType = ElementType.Dark, requiredClasses = listOf("Void Reaver")),
        Skill("Crushing Blow", "Unstoppable demonic force.", 6, 2, 1, "Attack", 2.6, requiredClasses = listOf("Abyssal Juggernaut")),
        Skill("World Crusher", "Total abyssal destruction.", 170, 20, 195, "Attack", 50.0, requiredClasses = listOf("Abyssal Juggernaut")),

        // --- DRACOLICH MONSTERS ---
        Skill("Death Strike", "Skeletal champion blow.", 5, 2, 1, "Attack", 2.4, requiredClasses = listOf("Death Knight")),
        Skill("Necrotic Breath", "Decay-spreading breath.", 145, 15, 195, "Attack", 48.0, statusEffect = StatusEffect.Poison, requiredClasses = listOf("Necrotic Wyrm")),

        // --- ROYAL DEMON MONSTERS ---
        Skill("Infernal Command", "Lesser being obedience.", 5, 1, 1, "Support", 1.0, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Infernal Overlord")),
        Skill("Overlord's Presence", "Dark gravity crushing.", 180, 20, 190, "Attack", 50.0, requiredClasses = listOf("Infernal Overlord")),

        // --- SHADOW MONARCH MONSTERS ---
        Skill("Shadow Extraction", "Fallen foe shadow draw.", 5, 0, 1, "Support", 1.5, requiredClasses = listOf("Monarch of Shadows")),
        Skill("Shadow Army", "Endless shadow summons.", 150, 20, 190, "Attack", 50.0, "MultiHit", requiredClasses = listOf("Monarch of Shadows")),
        Skill("Dark Domain", "Shadow realm rule.", 6, 2, 1, "Buff", 1.0, "DamageBuff", requiredClasses = listOf("Shadow Sovereign")),
        Skill("Absolute Darkness", "Total light extinction.", 180, 25, 195, "Attack", 50.0, elementType = ElementType.Dark, requiredClasses = listOf("Shadow Sovereign")),

        // --- TITAN MONSTERS ---
        Skill("Earthquake", "Ground-slamming stun.", 6, 2, 1, "Attack", 2.5, statusEffect = StatusEffect.Stun, requiredClasses = listOf("World Breaker")),
        Skill("World Sunder", "World-splitting strike.", 170, 25, 190, "Attack", 50.0, requiredClasses = listOf("World Breaker")),

        // --- VOID SOVEREIGN MONSTERS ---
        Skill("Void Step", "Void entry evasion.", 4, 2, 1, "Buff", 1.0, "EvasionBuff", requiredClasses = listOf("Void Walker")),
        Skill("End of All Things", "Space-time total collapse.", 160, 20, 190, "Attack", 50.0, elementType = ElementType.Dark, requiredClasses = listOf("Void Walker")),

        // --- ELDER DRAGON MONSTERS ---
        Skill("Ancient Breath", "Millennia-old breath weapon.", 5, 2, 1, "Attack", 2.5, elementType = ElementType.Fire, statusEffect = StatusEffect.Burn, requiredClasses = listOf("Dragon Lord")),
        Skill("Origin Flame", "World-creating first fire.", 180, 25, 190, "Attack", 50.0, elementType = ElementType.Fire, requiredClasses = listOf("Dragon Lord")),

        // --- NEPHILIM MONSTERS ---
        Skill("Equilibrium Bolt", "Balanced energy blast.", 4, 1, 1, "Attack", 2.1, requiredClasses = listOf("Arbiter")),
        Skill("Universal Balance", "Primal battlefield reset.", 160, 20, 190, "Attack", 48.0, requiredClasses = listOf("Arbiter")),

        // --- ADDITIONAL MONSTER SKILLS ---
        // Beholder
        Skill("Arcane Gaze", "Simple magical gaze.", 3, 1, 1, "Attack", 1.8, requiredClasses = listOf("Gaze Weaver")),
        Skill("Mind Shackles", "Stunning mental bind.", 25, 4, 60, "Support", 1.0, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Gaze Weaver")),
        Skill("Reality Anchor", "Ultimate static field.", 150, 15, 200, "Attack", 45.0, requiredClasses = listOf("Gaze Weaver")),
        Skill("Sentry Beam", "Defensive tracking ray.", 4, 1, 1, "Attack", 1.6, requiredClasses = listOf("Arcane Sentinel")),
        Skill("Aura of Vigilance", "Unbreakable defense.", 35, 6, 65, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Arcane Sentinel")),
        Skill("Eye of the Storm", "Supreme field control.", 160, 20, 195, "Attack", 48.0, requiredClasses = listOf("Arcane Sentinel")),
        Skill("Warp Strike", "Reality bending blow.", 5, 2, 1, "Attack", 2.2, requiredClasses = listOf("Reality Warper")),
        Skill("Spatial Rift", "Tearing space open.", 45, 8, 110, "Attack", 10.0, "IgnoreArmor", requiredClasses = listOf("Reality Warper")),
        Skill("Dimensions End", "Final reality erasure.", 180, 25, 200, "Attack", 55.0, "LimitBreak", requiredClasses = listOf("Reality Warper")),

        // Slime
        Skill("Gel Strike", "Basic sticky blow.", 2, 0, 1, "Attack", 1.6, requiredClasses = listOf("Slime Sage")),
        Skill("Absorb", "Absorbing enemy force.", 30, 5, 55, "Support", 1.0, "Heal", requiredClasses = listOf("Slime Sage")),
        Skill("Infinite Adaptation", "Pinnacle of fluidity.", 145, 12, 190, "Buff", 1.0, "SuperBuff", requiredClasses = listOf("Slime Sage")),
        Skill("Bounce", "Reflective defense.", 3, 1, 1, "Support", 1.0, "Parry", requiredClasses = listOf("Amorphous Tank")),
        Skill("Gelatinous Wall", "Immovable slime barrier.", 40, 6, 115, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Amorphous Tank")),
        Skill("World Eater Slime", "Consuming all in path.", 160, 20, 200, "Attack", 50.0, "Lifesteal", requiredClasses = listOf("Amorphous Tank")),
        Skill("Mimic Strike", "Replicating enemy move.", 4, 1, 1, "Attack", 2.1, requiredClasses = listOf("Mimic Master")),
        Skill("Perfect Copy", "Assuming true form.", 50, 10, 105, "Buff", 5.0, "DamageBuff", requiredClasses = listOf("Mimic Master")),
        Skill("Mirror of Souls", "Final mimicry.", 175, 25, 195, "Attack", 52.0, requiredClasses = listOf("Mimic Master")),

        // Fairy
        Skill("Nature's Touch", "Basic forest healing.", 4, 2, 1, "Support", 1.5, "Heal", requiredClasses = listOf("Enchanter")),
        Skill("Forest Blessing", "Supreme nature buff.", 35, 6, 58, "Buff", 1.0, "SuperBuff", requiredClasses = listOf("Enchanter")),
        Skill("Gaia's Embrace", "Ultimate earth protect.", 155, 18, 190, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Enchanter")),
        Skill("Vine Whip", "Basic thorn strike.", 3, 1, 1, "Attack", 1.7, statusEffect = StatusEffect.Bleed, requiredClasses = listOf("Nature Guardian")),
        Skill("Thorn Armor", "Retaliating spikes.", 42, 7, 112, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Nature Guardian")),
        Skill("World Tree Wrath", "Ancient forest fury.", 165, 20, 195, "Attack", 48.0, requiredClasses = listOf("Nature Guardian")),
        Skill("Lume Beam", "Basic light blast.", 4, 1, 1, "Attack", 2.0, elementType = ElementType.Holy, requiredClasses = listOf("Luminous Sprite")),
        Skill("Prismatic Flare", "Blinding color rain.", 48, 8, 108, "Attack", 12.0, statusEffect = StatusEffect.Stun, requiredClasses = listOf("Luminous Sprite")),
        Skill("Nova of Creation", "Final celestial light.", 185, 30, 200, "Attack", 58.0, "LimitBreak", requiredClasses = listOf("Luminous Sprite")),

        // Ghost
        Skill("Spook", "Terrifying presence.", 2, 1, 1, "Support", 1.0, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Phantasm Lord")),
        Skill("Ethereal Chain", "Soul-binding link.", 40, 8, 110, "Attack", 9.0, requiredClasses = listOf("Phantasm Lord")),
        Skill("Army of the Dead", "Infinite ghost swarm.", 170, 25, 190, "Attack", 50.0, "MultiHit", requiredClasses = listOf("Phantasm Lord")),

        // Cyborg
        Skill("Plasma Cannon", "Basic energy blast.", 5, 2, 1, "Attack", 2.3, elementType = ElementType.Lightning, requiredClasses = listOf("Cyber Commando")),
        Skill("Barrage", "Multi-missile strike.", 45, 8, 105, "Attack", 1.5, "MultiHit", requiredClasses = listOf("Cyber Commando")),
        Skill("Tactical Nuke", "Final orbital strike.", 180, 35, 195, "Attack", 65.0, "IgnoreArmor", requiredClasses = listOf("Cyber Commando")),
        Skill("Analyze", "Scanning enemy weak.", 2, 1, 1, "Buff", 1.0, "CritBuff", requiredClasses = listOf("Logic Core")),
        Skill("Efficiency Mode", "Optimized combat state.", 35, 6, 62, "Buff", 3.0, "DamageBuff", requiredClasses = listOf("Logic Core")),
        Skill("Absolute Logic", "Calculated destruction.", 150, 20, 185, "Attack", 45.0, requiredClasses = listOf("Logic Core")),
        Skill("Mech Smash", "Heavy armor blow.", 6, 3, 1, "Attack", 2.6, requiredClasses = listOf("Mecha Pilot")),
        Skill("Shield Overload", "Massive energy wall.", 55, 10, 118, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Mecha Pilot")),
        Skill("Final Mech Fury", "Ultimate armor crash.", 190, 40, 200, "Attack", 60.0, requiredClasses = listOf("Mecha Pilot")),

        // Demon
        Skill("Chaos Bolt", "Basic hellfire blast.", 4, 1, 1, "Attack", 2.0, elementType = ElementType.Fire, requiredClasses = listOf("Hellfire Sorcerer")),
        Skill("Infernal Nova", "Exploding soul fire.", 50, 8, 102, "Attack", 12.0, elementType = ElementType.Fire, requiredClasses = listOf("Hellfire Sorcerer")),
        Skill("Hell's Final Breath", "Pinnacle of fire.", 180, 25, 192, "Attack", 55.0, elementType = ElementType.Fire, requiredClasses = listOf("Hellfire Sorcerer")),
        Skill("Discord Strike", "Basic chaos blow.", 4, 1, 1, "Attack", 1.9, requiredClasses = listOf("Chaos Bringer")),
        Skill("Entropy Spike", "Destabilizing energy.", 42, 6, 114, "Attack", 11.0, requiredClasses = listOf("Chaos Bringer")),
        Skill("Infinite Chaos", "Total order collapse.", 175, 22, 198, "Attack", 52.0, requiredClasses = listOf("Chaos Bringer")),

        // Dracolich
        Skill("Bone Spike", "Sharp bone projectile.", 3, 1, 1, "Attack", 1.8, requiredClasses = listOf("Bone Dragon")),
        Skill("Skeletal Wings", "Undead wing strike.", 44, 7, 106, "Attack", 1.6, "MultiHit", requiredClasses = listOf("Bone Dragon")),
        Skill("Ancient Bone Storm", "Eternal dragon cage.", 170, 24, 194, "Attack", 50.0, requiredClasses = listOf("Bone Dragon")),
        Skill("Lich Bolt", "Basic death magic.", 5, 2, 1, "Attack", 2.2, elementType = ElementType.Dark, requiredClasses = listOf("Eternal Lich")),
        Skill("Soul Cage", "Ultimate death seal.", 52, 9, 116, "Support", 1.0, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Eternal Lich")),
        Skill("Eternal Undeath", "Beyond the grave fury.", 190, 30, 200, "Attack", 62.0, requiredClasses = listOf("Eternal Lich")),

        // Royal Demon
        Skill("Royal Guard", "Summoning protectors.", 5, 2, 1, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Hell King")),
        Skill("Throne Crush", "Heavier gravity blow.", 55, 10, 110, "Attack", 14.0, requiredClasses = listOf("Hell King")),
        Skill("King of the Abyss", "Supreme demon rule.", 195, 45, 200, "Attack", 70.0, "LimitBreak", requiredClasses = listOf("Hell King")),
        Skill("Dark Pact", "Sacrifice for power.", 6, 3, 1, "Buff", 4.0, "DamageBuff", requiredClasses = listOf("Demon Prince")),
        Skill("Princely Strike", "Noble demonic blow.", 48, 8, 108, "Attack", 12.0, requiredClasses = listOf("Demon Prince")),
        Skill("Cursed Heritage", "Unleashed royal fury.", 185, 25, 195, "Attack", 58.0, requiredClasses = listOf("Demon Prince")),
        Skill("Ruin Strike", "Basic destructive blow.", 5, 2, 1, "Attack", 2.4, requiredClasses = listOf("Archduke of Ruin")),
        Skill("Cataclysm", "World-shaking event.", 65, 12, 120, "Attack", 18.0, requiredClasses = listOf("Archduke of Ruin")),
        Skill("End of Empires", "Ultimate devastation.", 210, 50, 200, "Attack", 80.0, "IgnoreArmor", requiredClasses = listOf("Archduke of Ruin")),

        // Shadow Monarch
        Skill("Umbral Slash", "Basic shadow cut.", 4, 1, 1, "Attack", 2.1, elementType = ElementType.Dark, requiredClasses = listOf("Lord of Darkness")),
        Skill("Black Sun", "Consuming all hope.", 60, 10, 115, "Attack", 16.0, elementType = ElementType.Dark, requiredClasses = listOf("Lord of Darkness")),
        Skill("Eternal Night", "Final light death.", 200, 40, 200, "Attack", 75.0, elementType = ElementType.Dark, requiredClasses = listOf("Lord of Darkness")),
        Skill("Conqueror's Edge", "Basic ruling strike.", 5, 2, 1, "Attack", 2.3, requiredClasses = listOf("Umbral Conqueror")),
        Skill("Shadow Domain", "Expanding the empire.", 58, 9, 112, "Buff", 5.0, "DamageBuff", requiredClasses = listOf("Umbral Conqueror")),
        Skill("World of Shadows", "Total umbral rule.", 190, 35, 195, "Attack", 65.0, requiredClasses = listOf("Umbral Conqueror")),

        // Titan
        Skill("Stone Molder", "Shaping the earth.", 3, 1, 1, "Buff", 2.0, "DamageBuff", requiredClasses = listOf("Earth Shaper")),
        Skill("Terraform", "Massive landscape alt.", 55, 10, 110, "Attack", 14.0, requiredClasses = listOf("Earth Shaper")),
        Skill("Planet Shaper", "Ultimate titan will.", 185, 30, 190, "Attack", 60.0, requiredClasses = listOf("Earth Shaper")),
        Skill("Mountain Guard", "Unbreakable defense.", 6, 3, 1, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Colossal Guardian")),
        Skill("Living Fortress", "Mountain-tier shield.", 62, 12, 118, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Colossal Guardian")),
        Skill("Guardian's Core", "Heart of the world.", 200, 45, 200, "Buff", 1.0, "SuperBuff", requiredClasses = listOf("Colossal Guardian")),
        Skill("High Peak Strike", "Basic summit blow.", 5, 2, 1, "Attack", 2.4, requiredClasses = listOf("Mountain King")),
        Skill("Avalanche", "Crushing snow storm.", 58, 9, 108, "Attack", 2.5, "MultiHit", requiredClasses = listOf("Mountain King")),
        Skill("Peak of Existence", "True mountain rule.", 195, 35, 195, "Attack", 68.0, requiredClasses = listOf("Mountain King")),

        // Void Sovereign
        Skill("Tear Reality", "Basic void rip.", 5, 2, 1, "Attack", 2.5, requiredClasses = listOf("Rift Master")),
        Skill("Void Storm", "Swirling void energy.", 65, 12, 112, "Attack", 3.0, "MultiHit", requiredClasses = listOf("Rift Master")),
        Skill("Rift of No Return", "Final void intake.", 205, 45, 200, "Attack", 75.0, "IgnoreArmor", requiredClasses = listOf("Rift Master")),
        Skill("Collapse", "Basic gravity pull.", 6, 3, 1, "Attack", 2.6, requiredClasses = listOf("Singularity Lord")),
        Skill("Event Horizon", "Point of no escape.", 70, 15, 120, "Support", 1.0, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Singularity Lord")),
        Skill("Infinite Singularity", "Final space death.", 210, 50, 200, "Attack", 80.0, "LimitBreak", requiredClasses = listOf("Singularity Lord")),
        Skill("Consume Star", "Basic cosmic eating.", 7, 4, 1, "Attack", 2.8, requiredClasses = listOf("Cosmic Devourer")),
        Skill("Galaxy Eater", "Large scale hunger.", 75, 18, 125, "Attack", 20.0, "Lifesteal", requiredClasses = listOf("Cosmic Devourer")),
        Skill("Hunger of the Void", "Eternal cosmic feast.", 220, 60, 200, "Attack", 85.0, "LimitBreak", requiredClasses = listOf("Cosmic Devourer")),

        // Elder Dragon
        Skill("Wyrm Strike", "Ancient dragon blow.", 5, 2, 1, "Attack", 2.4, requiredClasses = listOf("Ancient Wyrm")),
        Skill("Aeon Breath", "Millennia-old flame.", 68, 12, 115, "Attack", 18.0, elementType = ElementType.Fire, requiredClasses = listOf("Ancient Wyrm")),
        Skill("Dragon of Ages", "Ultimate wyrm fury.", 200, 40, 200, "Attack", 72.0, requiredClasses = listOf("Ancient Wyrm")),
        Skill("Sky Slash", "Heavenly wing cut.", 4, 1, 1, "Attack", 2.1, requiredClasses = listOf("Sky Emperor")),
        Skill("Cloud Ripper", "Tearing the heavens.", 62, 10, 112, "Attack", 15.0, requiredClasses = listOf("Sky Emperor")),
        Skill("Lord of the Skies", "Final aerial rule.", 195, 35, 195, "Attack", 68.0, requiredClasses = listOf("Sky Emperor")),
        Skill("Primal Guard", "First dragon shield.", 6, 3, 1, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Primal Guardian")),
        Skill("Origin Barrier", "World-start defense.", 70, 15, 120, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Primal Guardian")),
        Skill("Eternal Protector", "Final draconic guard.", 210, 50, 200, "Buff", 1.0, "SuperBuff", requiredClasses = listOf("Primal Guardian")),

        // Nephilim
        Skill("Hybrid Bolt", "Light and dark mix.", 4, 1, 1, "Attack", 2.2, requiredClasses = listOf("Divine Hybrid")),
        Skill("Eclipse Nova", "Total balance blast.", 65, 12, 118, "Attack", 18.0, requiredClasses = listOf("Divine Hybrid")),
        Skill("Divine Monstrosity", "Pinnacle of hybrid.", 200, 45, 200, "Attack", 75.0, requiredClasses = listOf("Divine Hybrid")),
        Skill("Anarchy Spike", "Weaponized chaos.", 4, 1, 1, "Attack", 2.1, requiredClasses = listOf("Chaos Balancer")),
        Skill("Entropy Wall", "Protective disorder.", 60, 10, 110, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Chaos Balancer")),
        Skill("Order in Anarchy", "Final chaotic law.", 195, 40, 195, "Attack", 70.0, requiredClasses = listOf("Chaos Balancer")),
        Skill("Vanguard Edge", "Elite hybrid blow.", 5, 2, 1, "Attack", 2.5, requiredClasses = listOf("Nephilim Vanguard")),
        Skill("Sentinel Rush", "Unstoppable charge.", 62, 12, 115, "Attack", 16.0, "MultiHit", requiredClasses = listOf("Nephilim Vanguard")),
        Skill("Shield of the Void-Light", "Final hybrid guard.", 205, 50, 200, "Buff", 1.0, "SuperBuff", requiredClasses = listOf("Nephilim Vanguard")),

        // --- MYTHIC SKILLS ---
        // Cosmic Envoy
        Skill("Stellar Flare", "Cosmic energy burst.", 15, 2, 1, "Attack", 4.5, "MultiHit", elementType = ElementType.Fire, requiredClasses = listOf("Cosmic Envoy")),
        Skill("Nebula Shield", "Barrier of star dust.", 45, 6, 80, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Cosmic Envoy")),
        Skill("Cosmic Ray", "Pure beam of starlight.", 180, 20, 195, "Attack", 55.0, "IgnoreArmor", elementType = ElementType.Holy, requiredClasses = listOf("Cosmic Envoy")),

        // Sun Lord
        Skill("Solar Supernova", "Heat of a dying star.", 18, 3, 1, "Attack", 5.0, elementType = ElementType.Fire, statusEffect = StatusEffect.Burn, requiredClasses = listOf("Sun Lord")),
        Skill("True Solar Flare", "Blinding solar radiation.", 50, 8, 90, "Support", 1.0, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Sun Lord")),
        Skill("Radiant Aegis", "Supreme sun protection.", 190, 25, 198, "Buff", 1.0, "DefenseBuff", requiredClasses = listOf("Sun Lord")),

        // Moon Goddess
        Skill("Lunar Eclipse", "Blotting out the light.", 16, 2, 1, "Attack", 4.2, elementType = ElementType.Dark, statusEffect = StatusEffect.Stun, requiredClasses = listOf("Moon Goddess")),
        Skill("Moonlight Prayer", "Gentle lunar healing.", 48, 5, 85, "Support", 3.5, "Heal", requiredClasses = listOf("Moon Goddess")),
        Skill("Silver Arrow", "Translucent moon-shot.", 175, 18, 190, "Attack", 52.0, requiredClasses = listOf("Moon Goddess")),

        // World Weaver
        Skill("Matter Deconstruction", "Breaking down atoms.", 20, 4, 1, "Attack", 6.0, "IgnoreArmor", requiredClasses = listOf("World Weaver")),
        Skill("Reality Warp", "Bending space time.", 55, 10, 100, "Support", 1.0, "Parry", requiredClasses = listOf("World Weaver")),
        Skill("Genesis Spark", "The spark of creation.", 200, 30, 200, "Buff", 1.0, "SuperBuff", requiredClasses = listOf("World Weaver")),

        // Eldritch Terror
        Skill("Void Tentacles", "Lashing out from void.", 14, 2, 1, "Attack", 2.0, "MultiHit", requiredClasses = listOf("Eldritch Terror")),
        Skill("Mind Shatter", "Destroying mental sanity.", 42, 6, 75, "Support", 1.0, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Eldritch Terror")),
        Skill("Abyssal Gaze", "Looking into the abyss.", 185, 25, 195, "Attack", 58.0, "Lifesteal", elementType = ElementType.Dark, requiredClasses = listOf("Eldritch Terror")),

        // Entropic God
        Skill("Entropy Burst", "Spreading pure decay.", 17, 3, 1, "Attack", 4.8, "MultiHit", statusEffect = StatusEffect.Poison, requiredClasses = listOf("Entropic God")),
        Skill("Chaos Domain", "Where disorder rules.", 52, 9, 110, "Buff", 1.0, "DamageBuff", requiredClasses = listOf("Entropic God")),
        Skill("Reality Unravel", "Deconstructing existence.", 195, 35, 198, "Attack", 65.0, "IgnoreArmor", requiredClasses = listOf("Entropic God")),

        // Planet Eater
        Skill("World Gulp", "Consuming entire lands.", 22, 5, 1, "Attack", 7.5, requiredClasses = listOf("Planet Eater")),
        Skill("Abyssal Hunger", "Never ending starvation.", 60, 12, 115, "Support", 1.0, "Heal", requiredClasses = listOf("Planet Eater")),
        Skill("Crushing Maw", "The weight of worlds.", 210, 40, 200, "Attack", 72.0, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Planet Eater")),

        // Singularity Core
        Skill("Event Horizon Core", "No light can escape.", 18, 4, 1, "Support", 1.0, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Singularity Core")),
        Skill("Gravitational Pull", "Crushing gravity well.", 58, 10, 105, "Attack", 12.0, requiredClasses = listOf("Singularity Core")),
        Skill("Black Hole Singularity", "Absolute space death.", 215, 50, 200, "Attack", 80.0, "IgnoreArmor", elementType = ElementType.Dark, requiredClasses = listOf("Singularity Core")),

        // --- TRANSCENDENT / BOSS TIER ---
        Skill("Ichor Surge", "Draw upon the blood of gods to deal massive damage and recover HP.", 40, 5, 999, "Attack", 15.0, "HealAttack"),
        Skill("Starlight Judgment", "The heavens rain down punishment. 5 hits, ignores defense.", 60, 6, 999, "Attack", 3.0, "MultiHitIgnore"),
        Skill("Ancient Dominion", "Manifest the will of the ancients. Massive damage and stun.", 80, 8, 999, "Attack", 25.0, "Stun")
    )



    fun getSkill(name: String): Skill {
        return resolveSkill(name) ?: skills[0]
    }

    fun resolveSkill(name: String): Skill? {
        return skills.find { it.name == name } 
            ?: GuildDatabase.allSkills.find { it.name == name }
    }

    fun getSkillWithLevel(name: String, level: Int): Skill {
        val base = getSkill(name)
        if (level <= 1) return base
        
        // Apply level bonuses (10% per level above 1)
        val bonus = 1.0 + (level - 1) * 0.15
        return base.copy(
            multiplier = base.multiplier * bonus,
            manaCost = (base.manaCost * 0.9).toInt().coerceAtLeast(0) // Lower mana cost at higher levels
        )
    }

    fun getExpForNextLevel(currentLevel: Int): Int {
        return when (currentLevel) {
            1 -> 15 // 15 uses to reach Level 2
            2 -> 40 // 40 more uses to reach Level 3
            else -> 0
        }
    }
}
