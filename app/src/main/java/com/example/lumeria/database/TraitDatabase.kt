package com.example.lumeria.database

import com.example.lumeria.models.Trait

object TraitDatabase {

    val commonTraits = listOf(
        Trait("Hardy", "+10 Max HP", "Common", hpBonus = 10),
        Trait("Focused", "+5 Max Mana", "Common", manaBonus = 5),
        Trait("Strong", "+2 Strength", "Common", strBonus = 2),
        Trait("Resilient", "+2 Vitality", "Common", vitBonus = 2),
        Trait("Sharp", "+2 Intelligence", "Common", intBonus = 2),
        Trait("Quick", "+2 Agility", "Common", agiBonus = 2),
        Trait("Healthy", "+15 Max HP", "Common", hpBonus = 15),
        Trait("Mindful", "+8 Max Mana", "Common", manaBonus = 8),
        Trait("Brute", "+3 Strength", "Common", strBonus = 3),
        Trait("Durable", "+3 Vitality", "Common", vitBonus = 3)
    )

    val uncommonTraits = listOf(
        Trait("Athlete", "+10 Agility", "Uncommon", agiBonus = 10),
        Trait("Scholar", "+10 Intelligence", "Uncommon", intBonus = 10),
        Trait("Warrior", "+5 Strength", "Uncommon", strBonus = 5),
        Trait("Guardian", "+5 Vitality", "Uncommon", vitBonus = 5),
        Trait("Sprinter", "+15 Agility", "Uncommon", agiBonus = 15),
        Trait("Sage", "+15 Intelligence", "Uncommon", intBonus = 15),
        Trait("Knight", "+8 Strength", "Uncommon", strBonus = 8),
        Trait("Protector", "+8 Vitality", "Uncommon", vitBonus = 8),
        Trait("Vigorous", "+30 Max HP", "Uncommon", hpBonus = 30),
        Trait("Soulful", "+20 Max Mana", "Uncommon", manaBonus = 20)
    )

    val rareTraits = listOf(
        Trait("Master", "+10 to all stats", "Rare", strBonus = 10, vitBonus = 10, intBonus = 10, agiBonus = 10),
        Trait("Titan", "+50 Max HP", "Rare", hpBonus = 50),
        Trait("Mage", "+30 Max Mana", "Rare", manaBonus = 30),
        Trait("Assassin", "+25 Agility", "Rare", agiBonus = 25),
        Trait("Berserker", "+15 Strength", "Rare", strBonus = 15),
        Trait("Iron Body", "+15 Vitality", "Rare", vitBonus = 15),
        Trait("Genius", "+20 Intelligence", "Rare", intBonus = 20),
        Trait("Wind Runner", "+30 Agility", "Rare", agiBonus = 30),
        Trait("War God", "+20 Strength", "Rare", strBonus = 20),
        Trait("Unstoppable", "+25 Vitality", "Rare", vitBonus = 25)
    )

    val epicTraits = listOf(
        Trait("Dragon Blood", "+100 Max HP", "Epic", hpBonus = 100),
        Trait("Archmage", "+60 Max Mana", "Epic", manaBonus = 60),
        Trait("Sword Saint", "+30 Strength", "Epic", strBonus = 30),
        Trait("Immortal", "+30 Vitality", "Epic", vitBonus = 30),
        Trait("Oracle", "+40 Intelligence", "Epic", intBonus = 40),
        Trait("Shadow Master", "+50 Agility", "Epic", agiBonus = 50),
        Trait("Heroic", "+15 to all stats", "Epic", strBonus = 15, vitBonus = 15, intBonus = 15, agiBonus = 15),
        Trait("Colossus", "+40 Vitality", "Epic", vitBonus = 40),
        Trait("Void Touch", "+20 to all stats", "Epic", strBonus = 20, vitBonus = 20, intBonus = 20, agiBonus = 20),
        Trait("Overlord", "+40 Strength", "Epic", strBonus = 40)
    )

    val legendaryTraits = listOf(
        Trait("Hero's Spirit", "+30 Strength, +30 Vitality", "Legendary", strBonus = 30, vitBonus = 30),
        Trait("Ancient Wisdom", "+40 Intelligence, +20 to all", "Legendary", intBonus = 40, strBonus = 20, vitBonus = 20, agiBonus = 20),
        Trait("Celestial Grace", "+50 Agility, +20 to all", "Legendary", agiBonus = 50, strBonus = 20, vitBonus = 20, intBonus = 20)
    )

    val mythicTraits = listOf(
        Trait("Void Essence", "+50 to all stats", "Mythic", strBonus = 50, vitBonus = 50, intBonus = 50, agiBonus = 50),
        Trait("Creator's Spark", "+75 to all stats", "Mythic", strBonus = 75, vitBonus = 75, intBonus = 75, agiBonus = 75),
        Trait("Lumeria's Blessing", "+100 to all stats", "Mythic", strBonus = 100, vitBonus = 100, intBonus = 100, agiBonus = 100)
    )

    val godTierTraits = listOf(
        Trait("Blood of the Gods", "+200 Max HP, +10 HP regen/turn", "God Tier", hpBonus = 200, specialEffect = "Regen"),
        Trait("Eyes of the Creator", "+50% Crit Chance, +50% Dodge Chance, +20 HP Regen, 5% Stun Chance, -25% STR, +30% Magic DMG", "God Tier", specialEffect = "GodEyes"),
        Trait("Will of the Ancients", "+150 to all stats, +50 HP/Mana Regen, Absolute Resistance", "God Tier", strBonus = 150, vitBonus = 150, intBonus = 150, agiBonus = 150, manaBonus = 150, hpBonus = 150, specialEffect = "Ancient")
    )

    val allTraits = commonTraits + uncommonTraits + rareTraits + epicTraits + legendaryTraits + mythicTraits + godTierTraits

    fun getTrait(name: String): Trait? {
        return allTraits.find { it.name == name }
    }

    fun rollTrait(): Trait {
        val roll = Math.random()
        return when {
            roll < 0.0001 -> godTierTraits.random()
            roll < 0.01 -> mythicTraits.random()
            roll < 0.05 -> legendaryTraits.random()
            roll < 0.15 -> epicTraits.random()
            roll < 0.25 -> rareTraits.random()
            roll < 0.40 -> uncommonTraits.random()
            else -> commonTraits.random()
        }
    }
}
