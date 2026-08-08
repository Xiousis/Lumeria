package com.solerforge.lumeria.database

import com.solerforge.lumeria.models.ElementType
import com.solerforge.lumeria.models.Skill
import com.solerforge.lumeria.models.StatusEffect

object SkillDatabase {
    val skills = listOf(
        Skill("None", "No skill equipped.", 0, 0, 0, "Support"),
        
        // --- BASE SKILLS (Evolution paths) ---
        Skill("Slash", "Basic sword attack.", 0, 0, 1, "Attack", evolvesTo = "Omnislash", requiredClasses = listOf("Warrior", "Samurai", "Assassin", "Paladin")),
        Skill("Heavy Strike", "A slow but powerful overhead swing.", 0, 2, 1, "Attack", 2.5, evolvesTo = "Grand Slam", requiredClasses = listOf("Warrior", "Berserker", "Monk")),
        Skill("Magic Bolt", "A simple magical blast.", 3, 2, 1, "Attack", 2.5, elementType = ElementType.Ice, evolvesTo = "Arcane Comet", requiredClasses = listOf("Mage", "Necromancer")),
        Skill("Heal", "Small burst of recovery.", 5, 3, 1, "Support", multiplier = 1.0, effectType = "Heal", evolvesTo = "Holy Grace", requiredClasses = listOf("Mage", "Paladin", "Monk", "Bard")),
        
        // --- CLASS STARTING SKILLS ---
        Skill("Quick Draw", "A lightning-fast strike before the enemy reacts.", 4, 0, 1, "Attack", 1.8, requiredClasses = listOf("Samurai", "Assassin", "Archer")),
        Skill("Parry", "Deflect an incoming attack and reduce damage.", 0, 2, 1, "Support", effectType = "Parry", requiredClasses = listOf("Warrior", "Samurai", "Paladin")),
        Skill("Mana Shield", "Uses mana to absorb incoming damage.", 0, 4, 1, "Buff", effectType = "Buff", requiredClasses = listOf("Mage")),
        Skill("Smoke Bomb", "Increases your chance to dodge.", 8, 5, 1, "Buff", effectType = "Buff", requiredClasses = listOf("Assassin")),
        Skill("Guard", "Hunker down to increase your defense.", 0, 3, 1, "Buff", effectType = "Buff", requiredClasses = listOf("Warrior", "Paladin", "Monk")),
        
        // --- EVOLVED SKILLS (Only acquired via evolution at Mastery Level 3) ---
        Skill("Omnislash", "Master level flurry. Faster and sharper.", 2, 0, 99, "Attack", 1.5, "MultiHit", requiredClasses = listOf("Warrior", "Samurai", "Assassin", "Paladin")),
        Skill("Grand Slam", "A massive earth-shaking strike.", 5, 2, 99, "Attack", 4.5, "Stun", statusEffect = StatusEffect.Stun, requiredClasses = listOf("Warrior", "Berserker", "Monk")),
        Skill("Arcane Comet", "A rain of magical fire and ice.", 8, 3, 99, "Attack", 5.0, elementType = ElementType.Ice, statusEffect = StatusEffect.Burn, requiredClasses = listOf("Mage", "Necromancer")),
        Skill("Holy Grace", "Divine light that heals and boosts.", 12, 4, 99, "Support", 2.5, "Heal", requiredClasses = listOf("Mage", "Paladin", "Monk", "Bard")),

        // --- MID TIER SKILLS ---
        Skill("Shield Bash", "Slam your guard into the enemy. Chance to stun.", 5, 3, 5, "Attack", 1.8, effectType = "Stun", statusEffect = StatusEffect.Stun, evolvesTo = "Bulwark Slam", requiredClasses = listOf("Paladin")),
        Skill("Whirlwind Slash", "Spin with your blade, hitting the enemy twice.", 10, 3, 10, "Attack", 1.5, "MultiHit", evolvesTo = "Cyclone Edge", requiredClasses = listOf("Warrior", "Berserker", "Samurai")),
        Skill("Parry Stance", "Focus on defense to reduce the next hit by 50%.", 8, 4, 15, "Support", effectType = "Parry", evolvesTo = "Absolute Counter", requiredClasses = listOf("Warrior", "Samurai", "Paladin")),
        Skill("Bleeding Slash", "A jagged cut that deals damage over time.", 12, 4, 20, "Attack", 2.5, effectType = "Bleed", statusEffect = StatusEffect.Bleed, evolvesTo = "Vampiric Touch", requiredClasses = listOf("Assassin", "Necromancer", "Berserker")),
        
        // --- EVOLVED MID TIER (Only acquired via evolution at Mastery Level 3) ---
        Skill("Bulwark Slam", "Devastating shield rush that ignores armor.", 10, 3, 99, "Attack", 3.0, "IgnoreArmor", requiredClasses = listOf("Paladin")),
        Skill("Cyclone Edge", "A violent tornado of steel. 3 hits.", 15, 3, 99, "Attack", 1.8, "MultiHit", requiredClasses = listOf("Warrior", "Berserker", "Samurai")),
        Skill("Absolute Counter", "Reflects a portion of the next hit.", 12, 4, 99, "Support", effectType = "Parry", requiredClasses = listOf("Warrior", "Samurai", "Paladin")),
        Skill("Vampiric Touch", "Drains life while causing heavy bleeding.", 18, 4, 99, "Attack", 3.5, "Lifesteal", statusEffect = StatusEffect.Bleed, requiredClasses = listOf("Assassin", "Necromancer", "Berserker")),

        // --- HIGH TIER SKILLS ---
        Skill("Executioner Strike", "Deals double damage if enemy HP is low (<30%).", 15, 5, 25, "Attack", 4.0, "Execute", requiredClasses = listOf("Warrior", "Assassin", "Berserker")),
        Skill("Lion's Roar", "A battle cry that increases your damage.", 10, 5, 30, "Buff", effectType = "Buff", requiredClasses = listOf("Warrior", "Berserker", "Bard")),
        Skill("Blade Dance", "A flurry of 4 swift strikes.", 20, 6, 40, "Attack", 1.2, "MultiHit", requiredClasses = listOf("Samurai", "Assassin", "Bard")),
        Skill("Earthsplitter", "A massive slam that ignores 50% defense.", 25, 6, 50, "Attack", 6.0, "IgnoreArmor", requiredClasses = listOf("Warrior", "Berserker", "Paladin")),
        Skill("Dragon Fang", "Increases your critical chance for a few turns.", 15, 7, 60, "Buff", effectType = "CritBuff", requiredClasses = listOf("Warrior", "Samurai", "Assassin", "Archer")),
        Skill("Hero's Valor", "Enter a state of heightened power and damage.", 30, 8, 75, "Buff", effectType = "SuperBuff", requiredClasses = listOf("Warrior", "Berserker", "Samurai", "Paladin")),
        
        // --- ULTIMATE SKILLS ---
        Skill("King's Judgment", "The ultimate sword technique. Massive damage.", 50, 10, 90, "Attack", 12.0, requiredClasses = listOf("Warrior", "Samurai", "Paladin")),
        Skill("Worldbreaker", "Final limit break. Guaranteed crit, ignores armor.", 100, 1, 100, "Attack", 50.0, "LimitBreak", requiredClasses = listOf("Warrior", "Berserker", "Samurai")),

        // --- GOD TIER UNIQUE SKILLS (Unlocked via Traits) ---
        Skill("Ichor Surge", "Draw upon the blood of gods to deal massive damage and recover HP.", 40, 5, 999, "Attack", 15.0, "HealAttack"),
        Skill("Starlight Judgment", "The heavens rain down punishment. 5 hits, ignores defense.", 60, 6, 999, "Attack", 3.0, "MultiHitIgnore"),
        Skill("Ancient Dominion", "Manifest the will of the ancients. Massive damage and stun.", 80, 8, 999, "Attack", 25.0, "Stun")
    )

    fun getSkill(name: String): Skill {
        return skills.find { it.name == name } ?: skills[0]
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
