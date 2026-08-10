package com.solerforge.lumeria.database

import com.solerforge.lumeria.data.Quest

object GameDatabase {

    data class Weapon(
        val name: String,
        val attack: Int,
        val price: Long,
        val rarity: String = "Common",
        val description: String = "",
        val lore: String = "",
        val requiredLocation: String = "Training Fields",
        val requiredRank: Int = 0,
        val isBossDrop: Boolean = false,
        val requiredClasses: List<String> = emptyList(),
        val requiredRaces: List<String> = emptyList()
    )

    data class Armor(
        val name: String,
        val defense: Int,
        val price: Long,
        val rarity: String = "Common",
        val description: String = "",
        val lore: String = "",
        val requiredLocation: String = "Training Fields",
        val requiredRank: Int = 0,
        val isBossDrop: Boolean = false,
        val requiredClasses: List<String> = emptyList(),
        val requiredRaces: List<String> = emptyList()
    )

    data class HeadGear(
        val name: String,
        val defense: Int,
        val price: Long,
        val rarity: String = "Common",
        val description: String = "",
        val lore: String = "",
        val requiredLocation: String = "Training Fields",
        val requiredRank: Int = 0,
        val isBossDrop: Boolean = false,
        val requiredClasses: List<String> = emptyList(),
        val requiredRaces: List<String> = emptyList()
    )

    data class Boots(
        val name: String,
        val agility: Int,
        val price: Long,
        val rarity: String = "Common",
        val description: String = "",
        val requiredLocation: String = "Training Fields",
        val requiredRank: Int = 0,
        val isBossDrop: Boolean = false,
        val requiredClasses: List<String> = emptyList(),
        val requiredRaces: List<String> = emptyList()
    )

    data class Shield(
        val name: String,
        val defense: Int,
        val price: Long,
        val rarity: String = "Common",
        val description: String = "",
        val requiredLocation: String = "Training Fields",
        val requiredRank: Int = 0,
        val isBossDrop: Boolean = false,
        val requiredClasses: List<String> = emptyList(),
        val requiredRaces: List<String> = emptyList()
    )

    data class OffHand(
        val name: String,
        val strength: Int = 0,
        val defense: Int = 0,
        val vitality: Int = 0,
        val agility: Int = 0,
        val intelligence: Int = 0,
        val luck: Int = 0,
        val wisdom: Int = 0,
        val price: Long,
        val rarity: String = "Common",
        val description: String = "",
        val lore: String = "",
        val requiredLocation: String = "Training Fields",
        val requiredRank: Int = 0,
        val isBossDrop: Boolean = false,
        val requiredClasses: List<String> = emptyList(),
        val requiredRaces: List<String> = emptyList()
    )

    data class Consumable(
        val name: String,
        val healAmount: Int,
        val manaAmount: Int = 0,
        val price: Long,
        val rarity: String = "Common",
        val description: String = "",
        val lore: String = "",
        val requiredClasses: List<String> = emptyList()
    )

    data class FishingRod(
        val name: String,
        val price: Long,
        val rarity: String = "Common",
        val description: String = "",
        val reactionBonus: Int = 0, // Reduces reaction window in ms
        val requiredLevel: Int = 1,
        val requiredClasses: List<String> = emptyList()
    )

    data class CraftingMaterial(
        val name: String,
        val zone: String,
        val description: String,
        val rarity: String = "Common"
    )

    val craftingMaterials = listOf(
        CraftingMaterial("Rusty Scrap", "Training Fields", "Old metal bits from the fields."),
        CraftingMaterial("Goblin Leather", "Goblin Forest", "Tough leather from forest creatures."),
        CraftingMaterial("Crystal Shard", "Crystal Caverns", "A glowing fragment from the deep caves."),
        CraftingMaterial("Mountain Ore", "Stonehold Pass", "Heavy ore found in the high passes."),
        CraftingMaterial("Swamp Ooze", "Shadow Marsh", "Gooey residue from marsh monsters."),
        CraftingMaterial("Drake Talon", "Dragon Peaks", "A sharp talon from a peak drake."),
        CraftingMaterial("Shadow Dust", "Dark Citadel", "Essence of shadows from the citadel."),
        CraftingMaterial("Arena Medal", "Grand Arena", "A medal earned from the fighting pits."),
        CraftingMaterial("Tower Essence", "Tower of Trials", "Pure energy from the celestial tower.")
    )

    fun getRequiredMaterial(zone: String): String? {
        return craftingMaterials.find { it.zone == zone }?.name
    }

    val weapons = listOf(
        Weapon("Wooden Sword", 0, 0, description = "A simple practice sword made of oak.", requiredLocation = "Training Fields"),
        Weapon("Iron Sword", 3, 250, description = "A reliable blade used by city guards.", requiredLocation = "Training Fields", requiredClasses = listOf("Warrior", "Samurai", "Paladin", "Berserker")),
        Weapon("Steel Sword", 6, 500, description = "A finely honed blade made of reinforced steel.", requiredLocation = "Training Fields", requiredClasses = listOf("Warrior", "Samurai", "Paladin", "Berserker")),
        Weapon("Bronze Shortsword", 8, 750, description = "A sturdy sword favored by local adventurers.", requiredLocation = "Goblin Forest", requiredClasses = listOf("Warrior", "Samurai", "Paladin", "Berserker")),
        Weapon("Knight Blade", 10, 1500, description = "A heavy sword issued to the elite knight corp.", requiredLocation = "Training Fields", requiredClasses = listOf("Warrior", "Paladin")),
        Weapon("Hunter Spear", 10, 2000, description = "Excellent against fast forest creatures.", requiredLocation = "Goblin Forest", requiredClasses = listOf("Warrior", "Archer", "Monk")),
        Weapon("Crystal Saber", 14, 4500, description = "Forged using refined crystal fragments.", requiredLocation = "Crystal Caverns"),
        Weapon("Cavern Pike", 16, 6000, description = "A long weapon popular among cave explorers.", requiredLocation = "Crystal Caverns"),
        Weapon("Orc Cleaver", 20, 15000, description = "A brutal weapon captured from an Orc warband.", requiredLocation = "Stonehold Pass"),
        Weapon("Mountain Blade", 24, 25000, description = "Tempered to withstand harsh mountain weather.", requiredLocation = "Stonehold Pass"),
        Weapon("Venom Fang", 35, 60000, description = "A blade coated in toxic residue.", requiredLocation = "Shadow Marsh"),
        Weapon("Spirit Reaver", 40, 100000, description = "A weapon etched with ancient runes.", requiredLocation = "Shadow Marsh"),
        Weapon("Drakefang Sword", 55, 250000, description = "Forged from the fang of a Fire Drake.", requiredLocation = "Dragon Peaks"),
        Weapon("Volcanic Greatblade", 65, 450000, description = "Still warm from the forge.", requiredLocation = "Dragon Peaks"),
        Weapon("Shadow Edge", 80, 650000, description = "A blade forged in perpetual darkness.", requiredLocation = "Dark Citadel"),
        Weapon("Voidsteel Blade", 90, 850000, description = "One of the strongest weapons ever sold.", requiredLocation = "Dark Citadel"),
        
        // Distribution Expansion
        Weapon("Training Rapier", 4, 350, requiredLocation = "Training Fields"),
        Weapon("Squire Dagger", 5, 600, requiredLocation = "Training Fields"),
        Weapon("Forest Hatchet", 7, 1200, requiredLocation = "Goblin Forest"),
        Weapon("Goblin Cleaver", 9, 2200, requiredLocation = "Goblin Forest"),
        Weapon("Glinting Dagger", 13, 5000, requiredLocation = "Crystal Caverns"),
        Weapon("Gem-Encrusted Blade", 15, 7500, requiredLocation = "Crystal Caverns"),
        Weapon("Stone Hammer", 22, 28000, requiredLocation = "Stonehold Pass"),
        Weapon("Crag Axe", 26, 45000, requiredLocation = "Stonehold Pass"),
        Weapon("Swamp Trident", 38, 120000, requiredLocation = "Shadow Marsh"),
        Weapon("Bog Sickle", 42, 180000, requiredLocation = "Shadow Marsh"),
        Weapon("Lava Cutlass", 60, 500000, requiredLocation = "Dragon Peaks"),
        Weapon("Dragon Bone Knife", 70, 750000, requiredLocation = "Dragon Peaks"),
        Weapon("Abyssal Scythe", 85, 1200000, requiredLocation = "Dark Citadel"),
        Weapon("Soul Eater", 95, 2000000, requiredLocation = "Dark Citadel"),

        // Starting Class Weapons
        Weapon("Apprentice Staff", 4, 300, requiredLocation = "Training Fields", requiredClasses = listOf("Mage", "Necromancer")),
        Weapon("Training Katana", 6, 500, requiredLocation = "Training Fields", requiredClasses = listOf("Samurai")),
        
        // Rank Rewards (3 per rank)
        Weapon("Wanderer's Gladius", 15, 6000, "Rare", requiredRank = 1, requiredClasses = listOf("Warrior", "Samurai", "Bard")),
        Weapon("Traveler's Staff", 12, 4500, "Rare", requiredRank = 1, requiredClasses = listOf("Mage", "Necromancer")),
        Weapon("Nomad Bow", 14, 5500, "Rare", requiredRank = 1, requiredClasses = listOf("Archer")),
        
        Weapon("Royal Guard Lance", 30, 45000, "Rare", requiredRank = 2),
        Weapon("Silver Guard Sword", 28, 38000, "Rare", requiredRank = 2),
        Weapon("Elite Crossbow", 32, 55000, "Rare", requiredRank = 2),
        
        Weapon("Champion's Mace", 50, 250000, "Legendary", requiredRank = 3),
        Weapon("Hero's Longsword", 55, 300000, "Legendary", requiredRank = 3),
        Weapon("Valor Hammer", 60, 450000, "Legendary", requiredRank = 3),
        
        Weapon("Protector's Greatsword", 80, 800000, "Legendary", requiredRank = 4),
        Weapon("Guardian Axe", 75, 700000, "Legendary", requiredRank = 4),
        Weapon("Sentinel Halberd", 85, 1000000, "Legendary", requiredRank = 4),
        
        Weapon("National Saber", 110, 2500000, "Mythic", requiredRank = 5),
        Weapon("Imperial Rapier", 105, 2200000, "Mythic", requiredRank = 5),
        Weapon("Sovereign Blade", 120, 3500000, "Mythic", requiredRank = 5),
        
        Weapon("Legendary Excalibur", 200, 10000000, "God Tier", requiredRank = 6),
        Weapon("God-Slayer Edge", 190, 9000000, "God Tier", requiredRank = 6),
        Weapon("Cosmic Scythe", 210, 12000000, "God Tier", requiredRank = 6),

        // Boss Drops
        Weapon("Training Sword", 5, 1000, "Rare", "A worn but well-balanced sword.", isBossDrop = true),
        Weapon("King's Dagger", 15, 3000, "Rare", "A jagged blade used by the Goblin King.", isBossDrop = true),
        Weapon("Champion's Greatsword", 20, 8000, "Legendary", "A massive sword once held by a champion.", isBossDrop = true),
        Weapon("Goblin King's Blade", 30, 12000, "Legendary", "The royal blade of the goblin high-king.", isBossDrop = true),
        Weapon("Crystal Longsword", 45, 25000, "Legendary", "A blade made of sharpened prismatic crystal.", isBossDrop = true),
        Weapon("Titanbreaker Maul", 60, 50000, "Legendary", "A heavy hammer capable of shattering stone.", isBossDrop = true),
        Weapon("Dragon Slayer Blade", 90, 150000, "Legendary", "A legendary weapon forged to pierce dragon scales.", isBossDrop = true),
        Weapon("Void Staff", 100, 200000, "Legendary", "A dark staff that hums with forbidden power.", isBossDrop = true),
        Weapon("Blade of Xarthos", 125, 300000, "Legendary", "The cursed blade of the Void Lord himself.", isBossDrop = true),
        
        // God Tier Drops
        Weapon("Starshard Edge", 80, 0, "God Tier", "The crystals reward determination. 20% chance for double attack.", isBossDrop = true),
        Weapon("Fang of Eternity", 110, 0, "God Tier", "A blade that drinks life. 15% Lifesteal.", isBossDrop = true),
        Weapon("Endbringer", 150, 0, "God Tier", "The end of all things. Critical Hits deal 300% damage.", isBossDrop = true),

        // --- Race-Specific Monster Weapons ---
        Weapon("Dragon Claws", 25, 3500, "Rare", "Natural weapons of a dragonkin.", requiredRaces = listOf("Dragonkin"), requiredLocation = "Training Fields"),
        Weapon("Dragon God's Claws", 145, 5500000, "Mythic", "Claws that have tasted the blood of gods.", requiredRaces = listOf("Dragonkin"), requiredLocation = "Dark Citadel"),
        Weapon("Primal Fangs", 28, 4000, "Rare", "Fierce fangs of a young werewolf.", requiredRaces = listOf("Werewolf"), requiredLocation = "Training Fields"),
        Weapon("Lunar Ravager Fangs", 155, 6800000, "Mythic", "Fangs empowered by the eternal moon.", requiredRaces = listOf("Werewolf"), requiredLocation = "Dark Citadel"),
        Weapon("Arcane Eyestalk", 32, 5500, "Rare", "A focus for a beholder's mental power.", requiredRaces = listOf("Beholder"), requiredLocation = "Training Fields"),
        Weapon("Eye Tyrant's Focus", 165, 8500000, "Mythic", "A focus that channels pure destruction.", requiredRaces = listOf("Beholder"), requiredLocation = "Dark Citadel"),
        Weapon("Corrosive Nucleus", 22, 2800, "Rare", "The pulsating acidic heart of a slime.", requiredRaces = listOf("Slime"), requiredLocation = "Training Fields"),
        Weapon("Acidic Overlord Nucleus", 135, 4800000, "Mythic", "A nucleus dripping with world-dissolving acid.", requiredRaces = listOf("Slime"), requiredLocation = "Dark Citadel"),
        Weapon("Stardust Wand", 20, 3200, "Rare", "A wand that glitters with fae magic.", requiredRaces = listOf("Fairy"), requiredLocation = "Training Fields"),
        Weapon("Fae Queen's Scepter", 130, 4200000, "Mythic", "A scepter woven from pure celestial magic.", requiredRaces = listOf("Fairy"), requiredLocation = "Dark Citadel"),
        Weapon("Ethereal Scythe", 38, 6500, "Rare", "A blade that cuts through the physical and spiritual.", requiredRaces = listOf("Ghost"), requiredLocation = "Training Fields"),
        Weapon("Soul Reaper's Blade", 175, 12000000, "Mythic", "A weapon that harvests the very essence of life.", requiredRaces = listOf("Ghost"), requiredLocation = "Dark Citadel"),
        Weapon("Integrated Laser", 42, 8000, "Rare", "A low-power tactical combat laser.", requiredRaces = listOf("Cyborg"), requiredLocation = "Training Fields"),
        Weapon("Photon Annihilator", 185, 15000000, "Mythic", "A devastating energy weapon from a lost age.", requiredRaces = listOf("Cyborg"), requiredLocation = "Dark Citadel"),
        Weapon("Void Blade", 85, 1800000, "Legendary", "A blade forged in the deepest abyss.", requiredRaces = listOf("Demon"), requiredLocation = "Shadow Marsh"),
        Weapon("Abyssal Doomblade", 230, 22000000, "God Tier", "The personal blade of a Demon Overlord.", requiredRaces = listOf("Demon"), requiredRank = 5),
        Weapon("Undead Heart", 95, 2000000, "Legendary", "A pulsing necrotic focus of pure undeath.", requiredRaces = listOf("Dracolich"), requiredLocation = "Shadow Marsh"),
        Weapon("Phylactery Focus", 220, 20000000, "God Tier", "A focus containing the soul of a lich.", requiredRaces = listOf("Dracolich"), requiredRank = 5),
        Weapon("Scepter of Hell", 155, 5500000, "Mythic", "The symbol of demonic sovereignty.", requiredRaces = listOf("Royal Demon"), requiredLocation = "Dark Citadel"),
        Weapon("Diabolic Overlord Scepter", 260, 38000000, "God Tier", "A weapon that commands the legions of hell.", requiredRaces = listOf("Royal Demon"), requiredRank = 6),
        Weapon("Mountain Smasher", 140, 4500000, "Mythic", "A hammer made of petrified ironwood.", requiredRaces = listOf("Titan"), requiredLocation = "Dragon Peaks"),
        Weapon("Earth-Breaker Hammer", 250, 32000000, "God Tier", "A massive weapon that can level cities.", requiredRaces = listOf("Titan"), requiredRank = 6),
        Weapon("Singularity Core", 165, 7000000, "Mythic", "The condensed heart of a dying star.", requiredRaces = listOf("Void Sovereign"), requiredLocation = "Dark Citadel"),
        Weapon("Event Horizon Piercer", 270, 45000000, "God Tier", "A weapon that tears the fabric of space.", requiredRaces = listOf("Void Sovereign"), requiredRank = 6),
        Weapon("Primal Breath", 145, 5000000, "Mythic", "The focused kinetic energy of an elder dragon.", requiredRaces = listOf("Elder Dragon"), requiredLocation = "Dragon Peaks"),
        Weapon("Cataclysmic Dragon Breath", 240, 28000000, "God Tier", "The pure primal flame of the first dragon.", requiredRaces = listOf("Elder Dragon"), requiredRank = 6)
    )

    val armors = listOf(
        Armor("Cloth Armor", 0, 0, description = "Basic travel clothes.", requiredLocation = "Training Fields"),
        Armor("Leather Armor", 2, 200, description = "Light armor made of boiled leather.", requiredLocation = "Training Fields", requiredClasses = listOf("Warrior", "Archer", "Assassin", "Samurai", "Monk")),
        Armor("Scout Armor", 6, 750, description = "Light armor with improved mobility.", requiredLocation = "Goblin Forest", requiredClasses = listOf("Assassin", "Archer", "Samurai")),
        Armor("Chain Mail", 5, 1200, description = "Interlocking steel rings for solid protection.", requiredLocation = "Training Fields", requiredClasses = listOf("Warrior", "Paladin", "Berserker")),
        Armor("Forest Chainmail", 8, 2500, description = "Chainmail reinforced with hardwood plates.", requiredLocation = "Goblin Forest", requiredClasses = listOf("Warrior", "Paladin", "Berserker")),
        Armor("Knight Armor", 8, 3000, description = "Plate armor reserved for the kingdom's defenders.", requiredLocation = "Training Fields", requiredClasses = listOf("Paladin", "Warrior")),
        Armor("Crystal Guard", 12, 4500, description = "Armor lined with crystal shards.", requiredLocation = "Crystal Caverns"),
        Armor("Echo Mail", 14, 6000, description = "Protective armor designed for underground travel.", requiredLocation = "Crystal Caverns"),
        Armor("Stoneguard Plate", 18, 15000, description = "Heavy armor used by mountain defenders.", requiredLocation = "Stonehold Pass"),
        Armor("Highland Armor", 22, 25000, description = "A durable suit crafted for difficult terrain.", requiredLocation = "Stonehold Pass"),
        Armor("Swamp Plate", 30, 60000, description = "Armor resistant to corrosion and decay.", requiredLocation = "Shadow Marsh"),
        Armor("Spirit Cloak", 35, 100000, description = "A mysterious cloak that hums with magic.", requiredLocation = "Shadow Marsh"),
        Armor("Drakescale Armor", 50, 250000, description = "Armor crafted from hardened drake scales.", requiredLocation = "Dragon Peaks"),
        Armor("Magma Plate", 60, 450000, description = "Heavy armor infused with volcanic energy.", requiredLocation = "Dragon Peaks"),
        Armor("Shadowguard Armor", 75, 650000, description = "Armor worn by elite citadel warriors.", requiredLocation = "Dark Citadel"),
        Armor("Voidsteel Plate", 85, 850000, description = "Nearly indestructible armor.", requiredLocation = "Dark Citadel"),
        
        // Distribution Expansion
        Armor("Padded Tunic", 1, 150, requiredLocation = "Training Fields"),
        Armor("Rugged Vest", 4, 500, requiredLocation = "Goblin Forest"),
        Armor("Glimmering Suit", 10, 3800, requiredLocation = "Crystal Caverns"),
        Armor("Granite Cuirass", 20, 20000, requiredLocation = "Stonehold Pass"),
        Armor("Ooze Leather", 28, 55000, requiredLocation = "Shadow Marsh"),
        Armor("Obsidian Plate", 55, 300000, requiredLocation = "Dragon Peaks"),
        Armor("Void Shroud", 80, 800000, requiredLocation = "Dark Citadel"),

        // Starting Class Armors
        Armor("Mage Robes", 2, 300, requiredLocation = "Training Fields", requiredClasses = listOf("Mage", "Necromancer")),
        Armor("Shinobi Garb", 4, 500, requiredLocation = "Training Fields", requiredClasses = listOf("Samurai", "Assassin")),
        
        // Rank Rewards
        Armor("Wanderer's Cape", 5, 2500, "Rare", requiredRank = 1, requiredClasses = listOf("Bard", "Archer", "Assassin")),
        Armor("Explorer Jacket", 4, 2000, "Rare", requiredRank = 1, requiredClasses = listOf("Bard", "Archer", "Assassin")),
        Armor("Nomad Robes", 3, 1500, "Rare", requiredRank = 1, requiredClasses = listOf("Mage", "Necromancer")),
        
        Armor("Royal Knight Plate", 18, 40000, "Rare", requiredRank = 2),
        Armor("Palace Guard Armor", 16, 32000, "Rare", requiredRank = 2),
        Armor("Citadel Cuirass", 20, 55000, "Rare", requiredRank = 2),
        
        Armor("Hero's Breastplate", 45, 250000, "Legendary", requiredRank = 3),
        Armor("Valor Mail", 42, 200000, "Legendary", requiredRank = 3),
        Armor("Champion Suit", 48, 350000, "Legendary", requiredRank = 3),
        
        Armor("Protector's Aegis Mail", 70, 900000, "Legendary", requiredRank = 4),
        Armor("Guardian Bulwark", 65, 750000, "Legendary", requiredRank = 4),
        Armor("Sentinel Plate", 75, 1200000, "Legendary", requiredRank = 4),
        
        Armor("Champion's Regalia", 100, 3000000, "Mythic", requiredRank = 5),
        Armor("Grandmaster Robes", 90, 2500000, "Mythic", requiredRank = 5),
        Armor("Warlord Armor", 110, 4500000, "Mythic", requiredRank = 5),
        
        Armor("Legendary Godplate", 180, 15000000, "God Tier", requiredRank = 6),
        Armor("Ethereal Weave", 160, 12000000, "God Tier", requiredRank = 6),
        Armor("Celestial Mail", 200, 20000000, "God Tier", requiredRank = 6),

        // Boss Drops & Trophies
        Armor("Veteran Armor", 15, 5000, "Rare", "Battle-scarred armor from an old warrior.", isBossDrop = true),
        Armor("Prismatic Crystal Heart", 60, 20000, "Mythic", "A core of pure energy.", isBossDrop = true),
        Armor("Titan Core Relic", 85, 40000, "Mythic", "A fragment of an earth giant.", isBossDrop = true),
        Armor("Heart of the Bog", 115, 75000, "Mythic", "A pulsing black gem.", isBossDrop = true),
        Armor("Ancient Dragon Heart", 150, 125000, "Mythic", "A warm, glowing stone.", isBossDrop = true),
        Armor("Swamp Lord Armor", 100, 100000, "Legendary", "Armor dripping with dark energy.", isBossDrop = true),
        Armor("Void Armor", 200, 250000, "Legendary", "Armor that seems to blur the edges of reality.", isBossDrop = true),
        
        // God Tier Drops
        Armor("Mantle of Perseverance", 35, 0, "God Tier", "Survive lethal damage once per battle.", isBossDrop = true),
        
        // Trophies
        Armor("Captain's Badge", 0, 0, "Rare", isBossDrop = true),
        Armor("Goblin Crown", 0, 0, "Rare", isBossDrop = true),
        Armor("Crystal Core", 0, 0, "Rare", isBossDrop = true),
        Armor("Titan Heart", 0, 0, "Rare", isBossDrop = true),
        Armor("Rotten Essence", 0, 0, "Rare", isBossDrop = true),
        Armor("Dragon Scale", 0, 0, "Rare", isBossDrop = true),
        Armor("Xarthos Soul", 0, 0, "Rare", isBossDrop = true),
        Armor("Godwalker", 0, 0, "God Tier", isBossDrop = true),

        // --- Race-Specific Monster Armor ---
        Armor("Obsidian Scales", 65, 480000, "Legendary", "Hardened volcanic scales of a dragonkin.", requiredRaces = listOf("Dragonkin"), requiredLocation = "Dragon Peaks"),
        Armor("Dragon Lord's Plate", 210, 15000000, "God Tier", "Indestructible plate fused with dragon blood.", requiredRaces = listOf("Dragonkin"), requiredRank = 6),
        Armor("Lunar Fur", 58, 420000, "Legendary", "Protective fur that shimmers with moonlight.", requiredRaces = listOf("Werewolf"), requiredLocation = "Dragon Peaks"),
        Armor("Fenrir's Eternal Pelt", 195, 12000000, "God Tier", "The mantle of the legendary wolf-god.", requiredRaces = listOf("Werewolf"), requiredRank = 6),
        Armor("Abyssal Shell", 62, 550000, "Legendary", "A protective shell hardened by the abyss.", requiredRaces = listOf("Beholder"), requiredLocation = "Dragon Peaks"),
        Armor("Gaze-Reflecting Carapace", 200, 18000000, "God Tier", "Armor that reflects the gaze of the gazer.", requiredRaces = listOf("Beholder"), requiredRank = 6),
        Armor("Gelatinous Skin", 70, 650000, "Legendary", "Tough, non-Newtonian slime skin.", requiredRaces = listOf("Slime"), requiredLocation = "Dragon Peaks"),
        Armor("Unstoppable Ooze Membrane", 220, 25000000, "God Tier", "A membrane that absorbs and redirects force.", requiredRaces = listOf("Slime"), requiredRank = 6),
        Armor("Gossamer Wings", 50, 300000, "Legendary", "Magically reinforced wings of a fairy.", requiredRaces = listOf("Fairy"), requiredLocation = "Dragon Peaks"),
        Armor("Celestial Starlight Shroud", 180, 9500000, "God Tier", "A shroud woven from captured starlight.", requiredRaces = listOf("Fairy"), requiredRank = 6),
        Armor("Spectral Shroud", 55, 450000, "Legendary", "A ghostly veil that blurs the wearer's form.", requiredRaces = listOf("Ghost"), requiredLocation = "Dragon Peaks"),
        Armor("Haunting Void Raiment", 205, 14000000, "God Tier", "Armor made of solid shadow and deathly mist.", requiredRaces = listOf("Ghost"), requiredRank = 6),
        Armor("Titanium Plating", 75, 850000, "Legendary", "High-grade cybernetic reinforcement.", requiredRaces = listOf("Cyborg"), requiredLocation = "Dragon Peaks"),
        Armor("Neutronium Alloy Chassis", 230, 28000000, "God Tier", "A chassis built to withstand a supernova.", requiredRaces = listOf("Cyborg"), requiredRank = 6),
        Armor("Hellfire Carapace", 100, 2800000, "Mythic", "Demonic armor that burns with eternal fire.", requiredRaces = listOf("Demon"), requiredLocation = "Dark Citadel"),
        Armor("Diabolic Plate of Ruin", 250, 40000000, "God Tier", "The impenetrable armor of a demon lord.", requiredRaces = listOf("Demon"), requiredRank = 6),
        Armor("Bone Plating", 90, 2200000, "Mythic", "Reinforced skeletal plating of a dracolich.", requiredRaces = listOf("Dracolich"), requiredLocation = "Dark Citadel"),
        Armor("Lich-King's Skeletal Mail", 240, 35000000, "God Tier", "Mail steeped in millennia of death magic.", requiredRaces = listOf("Dracolich"), requiredRank = 6),
        Armor("Crown of Thorns", 150, 6500000, "Mythic", "A crown that grants profane protection.", requiredRaces = listOf("Royal Demon"), requiredLocation = "Dark Citadel"),
        Armor("Archduke's Sovereign Mantle", 290, 55000000, "God Tier", "The pinnacle of demonic protective regalia.", requiredRaces = listOf("Royal Demon"), requiredRank = 6),
        Armor("Bedrock Armor", 160, 7500000, "Mythic", "Armor hewn from the roots of the world.", requiredRaces = listOf("Titan"), requiredLocation = "Dark Citadel"),
        Armor("Continent-Shifting Plate", 310, 65000000, "God Tier", "Armor that holds the weight of tectonic plates.", requiredRaces = listOf("Titan"), requiredRank = 6),
        Armor("Event Horizon", 180, 9500000, "Mythic", "Armor that absorbs space and light.", requiredRaces = listOf("Void Sovereign"), requiredLocation = "Dark Citadel"),
        Armor("Void-Emperor's Regalia", 330, 85000000, "God Tier", "The absolute protection of the void ruler.", requiredRaces = listOf("Void Sovereign"), requiredRank = 6),
        Armor("Ancient Scales", 140, 5500000, "Mythic", "Scales that have seen the birth of worlds.", requiredRaces = listOf("Elder Dragon"), requiredLocation = "Dark Citadel"),
        Armor("Celestial Dragon Shell", 270, 50000000, "God Tier", "The indestructible hide of an Elder Dragon.", requiredRaces = listOf("Elder Dragon"), requiredRank = 6)
    )

    val headGears = listOf(
        HeadGear("None", 0, 0, description = "No head gear equipped."),
        HeadGear("Leather Cap", 1, 100, requiredLocation = "Training Fields"),
        HeadGear("Iron Helm", 3, 500, requiredLocation = "Training Fields"),
        HeadGear("Knight Helm", 6, 2000, requiredLocation = "Stonehold Pass"),
        HeadGear("Crystal Circlet", 10, 5000, requiredLocation = "Crystal Caverns"),
        HeadGear("Dragon Scale Helm", 20, 25000, rarity = "Legendary", requiredLocation = "Dragon Peaks"),
        HeadGear("Void Crown", 40, 50000, rarity = "Mythic", description = "Ultimate head defense."),
        
        // Distribution Expansion
        HeadGear("Straw Hat", 1, 60, requiredLocation = "Training Fields"),
        HeadGear("Bandana", 1, 80, requiredLocation = "Training Fields"),
        HeadGear("Squire Coif", 2, 250, requiredLocation = "Training Fields"),
        HeadGear("Feathered Cap", 3, 500, requiredLocation = "Goblin Forest"),
        HeadGear("Hunter Hood", 4, 800, requiredLocation = "Goblin Forest"),
        HeadGear("Forest Band", 2, 400, requiredLocation = "Goblin Forest"),
        HeadGear("Glow Mask", 7, 2500, requiredLocation = "Crystal Caverns"),
        HeadGear("Crystal Horns", 12, 7000, requiredLocation = "Crystal Caverns"),
        HeadGear("Cave Lantern Hat", 5, 2000, requiredLocation = "Crystal Caverns"),
        HeadGear("Stone Mask", 15, 15000, requiredLocation = "Stonehold Pass"),
        HeadGear("Mountain Turban", 18, 22000, requiredLocation = "Stonehold Pass"),
        HeadGear("Crag Helmet", 22, 35000, requiredLocation = "Stonehold Pass"),
        HeadGear("Muddy Hood", 25, 60000, requiredLocation = "Shadow Marsh"),
        HeadGear("Plague Mask", 35, 110000, requiredLocation = "Shadow Marsh"),
        HeadGear("Vile Crown", 45, 180000, requiredLocation = "Shadow Marsh"),
        HeadGear("Lava Goggles", 55, 450000, requiredLocation = "Dragon Peaks"),
        HeadGear("Dragon Horns", 65, 650000, requiredLocation = "Dragon Peaks"),
        HeadGear("Drakescale Coif", 50, 350000, requiredLocation = "Dragon Peaks"),
        HeadGear("Void Veil", 80, 1200000, requiredLocation = "Dark Citadel"),
        HeadGear("Shadow Hood", 90, 1800000, requiredLocation = "Dark Citadel"),
        HeadGear("Citadel Crown", 100, 2500000, requiredLocation = "Dark Citadel"),

        // Rank Rewards
        HeadGear("Wanderer's Hat", 5, 3000, "Rare", requiredRank = 1),
        HeadGear("Traveler's Hood", 4, 2500, "Rare", requiredRank = 1),
        HeadGear("Wayfarer Circlet", 6, 4000, "Rare", requiredRank = 1),
        
        HeadGear("Guard Helm", 15, 35000, "Rare", requiredRank = 2),
        HeadGear("Royal Ribbon", 10, 25000, "Rare", requiredRank = 2),
        HeadGear("Knight Visor", 18, 50000, "Rare", requiredRank = 2),
        
        HeadGear("Hero's Diadem", 35, 200000, "Legendary", requiredRank = 3),
        HeadGear("Valor Crown", 40, 300000, "Legendary", requiredRank = 3),
        HeadGear("Victory Laurel", 30, 150000, "Legendary", requiredRank = 3),
        
        HeadGear("Protector's Faceguard", 60, 800000, "Legendary", requiredRank = 4),
        HeadGear("Guardian Helm", 55, 650000, "Legendary", requiredRank = 4),
        HeadGear("Sentinel Mask", 65, 1000000, "Legendary", requiredRank = 4),
        
        HeadGear("Champion's Laurel", 90, 2500000, "Mythic", requiredRank = 5),
        HeadGear("National Crown", 100, 3500000, "Mythic", requiredRank = 5),
        HeadGear("Imperial Tiara", 85, 2000000, "Mythic", requiredRank = 5),
        
        HeadGear("Divine Halo", 160, 12000000, "God Tier", requiredRank = 6),
        HeadGear("Crown of Eternity", 180, 18000000, "God Tier", requiredRank = 6),
        HeadGear("Godly Aura", 200, 25000000, "God Tier", requiredRank = 6),
        
        HeadGear("Helm of the First Hero", 25, 75000, "Mythic", isBossDrop = true),
        HeadGear("Royal Goblin Crown", 40, 25000, "Mythic", isBossDrop = true),
        HeadGear("Crown of the Void King", 250, 500000, "Mythic", isBossDrop = true)
    )

    val boots = listOf(
        Boots("Traveler Boots", 0, 0, description = "Simple travel boots.", requiredLocation = "Training Fields"),
        Boots("Leather Boots", 3, 100, description = "Lightweight boots.", requiredLocation = "Training Fields"),
        Boots("Scout Boots", 7, 500, description = "Designed for speed.", requiredLocation = "Goblin Forest"),
        Boots("Crystal Boots", 12, 1500, description = "Infused with crystal energy.", requiredLocation = "Crystal Caverns"),
        Boots("Wolfstride Boots", 20, 5000, description = "Move like the wind.", requiredLocation = "Stonehold Pass"),
        Boots("Dragonrunner Boots", 35, 15000, description = "Forged from drake leather.", requiredLocation = "Dragon Peaks"),
        Boots("Voidwalker Boots", 50, 35000, rarity = "Legendary", description = "Ignore the limits of space.", requiredLocation = "Dark Citadel"),
        
        // Distribution Expansion
        Boots("Canvas Shoes", 1, 50, requiredLocation = "Training Fields"),
        Boots("Sandals", 1, 30, requiredLocation = "Training Fields"),
        Boots("Squire Boots", 2, 200, requiredLocation = "Training Fields"),
        Boots("Mud Boots", 4, 450, requiredLocation = "Goblin Forest"),
        Boots("Hunter Treads", 6, 900, requiredLocation = "Goblin Forest"),
        Boots("Mossy Steps", 5, 600, requiredLocation = "Goblin Forest"),
        Boots("Sparkle Sprints", 10, 3000, requiredLocation = "Crystal Caverns"),
        Boots("Prismatic Boots", 14, 6500, requiredLocation = "Crystal Caverns"),
        Boots("Echo Sabatons", 9, 2200, requiredLocation = "Crystal Caverns"),
        Boots("Heavy Greaves", 18, 18000, requiredLocation = "Stonehold Pass"),
        Boots("Climbing Boots", 22, 28000, requiredLocation = "Stonehold Pass"),
        Boots("Mountain Striders", 25, 45000, requiredLocation = "Stonehold Pass"),
        Boots("Swamp Waders", 30, 85000, requiredLocation = "Shadow Marsh"),
        Boots("Toxic Treads", 38, 140000, requiredLocation = "Shadow Marsh"),
        Boots("Ooze Steps", 28, 65000, requiredLocation = "Shadow Marsh"),
        Boots("Lava Stompers", 45, 400000, requiredLocation = "Dragon Peaks"),
        Boots("Fire-Walking Boots", 60, 750000, requiredLocation = "Dragon Peaks"),
        Boots("Drake Talons", 55, 550000, requiredLocation = "Dragon Peaks"),
        Boots("Ghost Steps", 75, 1500000, requiredLocation = "Dark Citadel"),
        Boots("Ethereal Boots", 90, 2200000, requiredLocation = "Dark Citadel"),
        Boots("Void Greaves", 100, 3500000, requiredLocation = "Dark Citadel"),

        // Rank Rewards
        Boots("Swift Wanderer Boots", 8, 4000, "Rare", requiredRank = 1),
        Boots("Cloud Sprints", 7, 3500, "Rare", requiredRank = 1),
        Boots("Dashers", 6, 2800, "Rare", requiredRank = 1),
        
        Boots("Knight's Greaves", 16, 40000, "Rare", requiredRank = 2),
        Boots("Royal Sabatons", 18, 55000, "Rare", requiredRank = 2),
        Boots("Guard Boots", 14, 32000, "Rare", requiredRank = 2),
        
        Boots("Hero's Sabatons", 35, 250000, "Legendary", requiredRank = 3),
        Boots("Valor Steps", 32, 200000, "Legendary", requiredRank = 3),
        Boots("Champion Treads", 40, 400000, "Legendary", requiredRank = 3),
        
        Boots("Protector's Fleetfeet", 60, 900000, "Legendary", requiredRank = 4),
        Boots("Guardian Greaves", 55, 750000, "Legendary", requiredRank = 4),
        Boots("Sentinel Boots", 65, 1300000, "Legendary", requiredRank = 4),
        
        Boots("Champion's Greaves", 90, 3000000, "Mythic", requiredRank = 5),
        Boots("National Sabatons", 100, 4500000, "Mythic", requiredRank = 5),
        Boots("Imperial Sprints", 85, 2200000, "Mythic", requiredRank = 5),
        
        Boots("Godspeed Boots", 160, 15000000, "God Tier", requiredRank = 6),
        Boots("Flash Steps", 150, 12000000, "God Tier", requiredRank = 6),
        Boots("Time Warp Boots", 180, 20000000, "God Tier", requiredRank = 6),
        
        // God Tier
        Boots("Bogwalker Greaves", 60, 0, "God Tier", "Enemies lose 2% HP every turn.", isBossDrop = true)
    )

    val shields = listOf(
        Shield("None", 0, 0, description = "No shield equipped."),
        Shield("Wooden Shield", 2, 50, requiredLocation = "Training Fields"),
        Shield("Iron Shield", 5, 250, requiredLocation = "Training Fields"),
        Shield("Knight Shield", 10, 1000, requiredLocation = "Stonehold Pass"),
        Shield("Tower Shield", 18, 5000, requiredLocation = "Shadow Marsh"),
        Shield("Dragon Shield", 30, 15000, rarity = "Legendary", requiredLocation = "Dragon Peaks"),
        Shield("Void Bulwark", 50, 0, rarity = "Mythic", description = "Ultimate defense."),
        
        // Distribution Expansion
        Shield("Buckler", 1, 80, requiredLocation = "Training Fields"),
        Shield("Plank", 1, 40, requiredLocation = "Training Fields"),
        Shield("Squire Shield", 3, 200, requiredLocation = "Training Fields"),
        Shield("Leaf Shield", 4, 600, requiredLocation = "Goblin Forest"),
        Shield("Bark Shield", 3, 400, requiredLocation = "Goblin Forest"),
        Shield("Branch Barrier", 5, 1000, requiredLocation = "Goblin Forest"),
        Shield("Crystal Buckler", 12, 4500, requiredLocation = "Crystal Caverns"),
        Shield("Reflecting Shield", 14, 8500, requiredLocation = "Crystal Caverns"),
        Shield("Prismatic Wall", 16, 12000, requiredLocation = "Crystal Caverns"),
        Shield("Stone Barrier", 22, 25000, requiredLocation = "Stonehold Pass"),
        Shield("Granite Wall", 25, 45000, requiredLocation = "Stonehold Pass"),
        Shield("Crag Defender", 28, 70000, requiredLocation = "Stonehold Pass"),
        Shield("Mossy Shield", 35, 110000, requiredLocation = "Shadow Marsh"),
        Shield("Vile Plate", 42, 180000, requiredLocation = "Shadow Marsh"),
        Shield("Toxic Aegis", 38, 140000, requiredLocation = "Shadow Marsh"),
        Shield("Obsidian Shield", 55, 450000, requiredLocation = "Dragon Peaks"),
        Shield("Inferno Shield", 65, 750000, requiredLocation = "Dragon Peaks"),
        Shield("Magma Shell", 60, 600000, requiredLocation = "Dragon Peaks"),
        Shield("Shadow Guard", 85, 1500000, requiredLocation = "Dark Citadel"),
        Shield("Void Gate", 100, 2200000, requiredLocation = "Dark Citadel"),
        Shield("Citadel Bulwark", 95, 1800000, requiredLocation = "Dark Citadel"),

        // Rank Rewards
        Shield("Wanderer's Buckler", 8, 5000, "Rare", requiredRank = 1),
        Shield("Explorer Ward", 7, 4000, "Rare", requiredRank = 1),
        Shield("Nomad Plank", 6, 3000, "Rare", requiredRank = 1),
        
        Shield("Royal Shield", 18, 50000, "Rare", requiredRank = 2),
        Shield("Palace Guard", 16, 42000, "Rare", requiredRank = 2),
        Shield("Elite Aegis", 20, 75000, "Rare", requiredRank = 2),
        
        Shield("Hero's Aegis", 45, 300000, "Legendary", requiredRank = 3),
        Shield("Valor Wall", 40, 250000, "Legendary", requiredRank = 3),
        Shield("Victory Guard", 50, 450000, "Legendary", requiredRank = 3),
        
        Shield("Protector's Bulwark", 75, 1000000, "Legendary", requiredRank = 4),
        Shield("Guardian Gate", 70, 850000, "Legendary", requiredRank = 4),
        Shield("Sentinel Wall", 80, 1500000, "Legendary", requiredRank = 4),
        
        Shield("Champion's Guard", 110, 3500000, "Mythic", requiredRank = 5),
        Shield("National Aegis", 100, 2800000, "Mythic", requiredRank = 5),
        Shield("Imperial Bulwark", 120, 5000000, "Mythic", requiredRank = 5),
        
        Shield("Divine Barrier", 200, 20000000, "God Tier", requiredRank = 6),
        Shield("Heavenly Gate", 180, 15000000, "God Tier", requiredRank = 6),
        Shield("Aura Shield", 220, 30000000, "God Tier", requiredRank = 6),
        
        // God Tier
        Shield("Aegis of Mountains", 100, 0, "God Tier", "Reduce all incoming damage by 15%.", isBossDrop = true),
        Shield("Bulwark of Heroes", 125, 0, "God Tier", "Gain 25% damage when below 50% HP.", isBossDrop = true)
    )

    val offHands = listOf(
        OffHand("None", price = 0, description = "No off-hand equipped."),
        OffHand("Warrior Charm", strength = 5, price = 500, requiredLocation = "Goblin Forest"),
        OffHand("Hunter Totem", agility = 10, price = 2500, requiredLocation = "Crystal Caverns"),
        OffHand("Wizard Tome", intelligence = 15, price = 5000, requiredLocation = "Stonehold Pass"),
        
        // Distribution Expansion
        OffHand("Copper Ring", strength = 2, price = 200, requiredLocation = "Training Fields"),
        OffHand("Iron Bracer", strength = 3, price = 400, requiredLocation = "Training Fields"),
        OffHand("Focus Stone", intelligence = 4, price = 600, requiredLocation = "Training Fields"),
        OffHand("Leaf Amulet", agility = 4, price = 800, requiredLocation = "Goblin Forest"),
        OffHand("Feather Charm", agility = 6, price = 1500, requiredLocation = "Goblin Forest"),
        OffHand("Forest Core", strength = 8, price = 3000, requiredLocation = "Goblin Forest"),
        OffHand("Quartz Orb", intelligence = 10, price = 6000, requiredLocation = "Crystal Caverns"),
        OffHand("Echo Shell", intelligence = 12, price = 10000, requiredLocation = "Crystal Caverns"),
        OffHand("Glow Gem", agility = 15, price = 18000, requiredLocation = "Crystal Caverns"),
        OffHand("Crag Stone", strength = 18, price = 35000, requiredLocation = "Stonehold Pass"),
        OffHand("Heavy Belt", strength = 22, price = 60000, requiredLocation = "Stonehold Pass"),
        OffHand("Mountain Soul", intelligence = 25, price = 110000, requiredLocation = "Stonehold Pass"),
        OffHand("Swamp Lamp", intelligence = 35, price = 250000, requiredLocation = "Shadow Marsh"),
        OffHand("Toxic Vial", agility = 40, price = 450000, requiredLocation = "Shadow Marsh"),
        OffHand("Mire Essence", strength = 45, price = 700000, requiredLocation = "Shadow Marsh"),
        OffHand("Fire Ember", strength = 60, price = 1200000, requiredLocation = "Dragon Peaks"),
        OffHand("Magma Core", strength = 75, price = 2500000, requiredLocation = "Dragon Peaks"),
        OffHand("Dragon Eye", intelligence = 80, price = 4000000, requiredLocation = "Dragon Peaks"),
        OffHand("Dark Sigil", intelligence = 100, price = 8000000, requiredLocation = "Dark Citadel"),
        OffHand("Shadow Essence", intelligence = 120, price = 12000000, requiredLocation = "Dark Citadel"),
        OffHand("Void Shard", agility = 140, price = 18000000, requiredLocation = "Dark Citadel"),

        // Rank Rewards
        OffHand("Wanderer's Gem", 5, 0, 0, 5, 5, luck = 5, wisdom = 5, price = 8000, rarity = "Rare", requiredRank = 1),
        OffHand("Traveler's Trinket", 4, 0, 0, 4, 4, luck = 4, wisdom = 4, price = 6000, rarity = "Rare", requiredRank = 1),
        OffHand("Nomad's Coin", 6, 0, 0, 6, 6, luck = 6, wisdom = 6, price = 12000, rarity = "Rare", requiredRank = 1),
        
        OffHand("Royal Seal", 15, 0, 0, 15, 15, luck = 15, wisdom = 15, price = 100000, rarity = "Rare", requiredRank = 2),
        OffHand("Guard Badge", 12, 0, 0, 12, 12, luck = 12, wisdom = 12, price = 75000, rarity = "Rare", requiredRank = 2),
        OffHand("Elite Insignia", 18, 0, 0, 18, 18, luck = 18, wisdom = 18, price = 150000, rarity = "Rare", requiredRank = 2),
        
        OffHand("Hero's Medal", 30, 0, 0, 30, 30, luck = 30, wisdom = 30, price = 500000, rarity = "Legendary", requiredRank = 3),
        OffHand("Valor Stone", 28, 0, 0, 28, 28, luck = 28, wisdom = 28, price = 400000, rarity = "Legendary", requiredRank = 3),
        OffHand("Victory Ribbon", 35, 0, 0, 35, 35, luck = 35, wisdom = 35, price = 800000, rarity = "Legendary", requiredRank = 3),
        
        OffHand("Protector's Aura", 55, 0, 0, 55, 55, luck = 55, wisdom = 55, price = 2000000, rarity = "Legendary", requiredRank = 4),
        OffHand("Guardian Spirit", 50, 0, 0, 50, 50, luck = 50, wisdom = 50, price = 1500000, rarity = "Legendary", requiredRank = 4),
        OffHand("Sentinel Link", 60, 0, 0, 60, 60, luck = 60, wisdom = 60, price = 3000000, rarity = "Legendary", requiredRank = 4),
        
        OffHand("Champion's Relic", 85, 0, 0, 85, 85, luck = 85, wisdom = 85, price = 6000000, rarity = "Mythic", requiredRank = 5),
        OffHand("National Emblem", 80, 0, 0, 80, 80, luck = 80, wisdom = 80, price = 5000000, rarity = "Mythic", requiredRank = 5),
        OffHand("Imperial Catalyst", 95, 0, 0, 95, 95, luck = 95, wisdom = 95, price = 8500000, rarity = "Mythic", requiredRank = 5),
        
        OffHand("God's Favor", 150, 0, 0, 150, 150, luck = 150, wisdom = 150, price = 25000000, rarity = "God Tier", requiredRank = 6),
        OffHand("Creator's Breath", 140, 0, 0, 140, 140, luck = 140, wisdom = 140, price = 20000000, rarity = "God Tier", requiredRank = 6),
        OffHand("Eternal Light", 160, 0, 0, 160, 160, luck = 160, wisdom = 160, price = 35000000, rarity = "God Tier", requiredRank = 6),
        
        // Rewards
        OffHand("Knight's Crest", strength = 15, price = 15000, rarity = "Rare", isBossDrop = true),
        OffHand("Emberclaw Totem", strength = 20, agility = 20, price = 35000, rarity = "Legendary", isBossDrop = true),
        OffHand("Void Crown", strength = 50, agility = 50, intelligence = 50, luck = 50, wisdom = 50, price = 100000, rarity = "Mythic", isBossDrop = true),
        
        // God Tier
        OffHand("Coin of the Tyrant", strength = 15, agility = 15, luck = 50, price = 0, rarity = "God Tier", isBossDrop = true),
        OffHand("Void Singularity", strength = 50, agility = 50, intelligence = 50, luck = 50, wisdom = 50, price = 0, rarity = "God Tier", isBossDrop = true),
        OffHand("Eye of Creation", strength = 75, agility = 75, intelligence = 75, luck = 75, wisdom = 75, price = 0, rarity = "God Tier", isBossDrop = true)
    )

    val consumables = listOf(
        Consumable("Potion", 10, 0, 250, description = "Heals a small amount of HP."),
        Consumable("Hi-Potion", 30, 0, 1500, description = "Heals a significant amount of HP."),
        Consumable("Mana Potion", 0, 20, 300, description = "Restores a small amount of Mana."),
        Consumable("Hi-Mana Potion", 0, 60, 1800, description = "Restores a significant amount of Mana.")
    )

    val fishingRods = listOf(
        FishingRod("Bamboo Rod", 1000, description = "A simple rod made of sturdy bamboo.", reactionBonus = 50),
        FishingRod("Fiberglass Rod", 5000, description = "Lighter and more flexible.", reactionBonus = 100, requiredLevel = 5),
        FishingRod("Carbon Fiber Rod", 15000, description = "A high-tech rod for serious fishers.", reactionBonus = 200, requiredLevel = 15),
        FishingRod("Mythril Rod", 40000, rarity = "Legendary", description = "Forged from mythril. Unbreakable.", reactionBonus = 350, requiredLevel = 30),
        FishingRod("Void Weaver Rod", 100000, rarity = "Mythic", description = "Reels in fish from other dimensions.", reactionBonus = 500, requiredLevel = 50)
    )

    val allQuests = listOf(
        // Training Fields
        Quest("Slime Squash", "Slime", 3, 0, 150, 20, requiredLocation = "Training Fields"),
        Quest("Goblin Guard", "Training Goblin", 5, 0, 250, 25, requiredLocation = "Training Fields"),
        Quest("Rat Exterminator", "Wild Rat", 4, 0, 200, 15, requiredLocation = "Training Fields"),
        Quest("Fields Patrol", "Slime", 10, 0, 500, 50, requiredLocation = "Training Fields"),

        // Goblin Forest
        Quest("Forest Clearing", "Goblin", 5, 0, 600, 40, requiredLocation = "Goblin Forest"),
        Quest("Archer's Bane", "Goblin Archer", 3, 0, 700, 50, requiredLocation = "Goblin Forest"),
        Quest("Rogue Hunt", "Goblin Rogue", 2, 0, 800, 60, requiredLocation = "Goblin Forest"),
        Quest("Forest Guardian", "Goblin", 12, 0, 1500, 120, requiredLocation = "Goblin Forest"),

        // Crystal Caverns
        Quest("Crystal Harvest", "Crystal Slime", 5, 0, 2000, 100, requiredLocation = "Crystal Caverns"),
        Quest("Bat Wing", "Cave Bat", 8, 0, 1800, 80, requiredLocation = "Crystal Caverns"),
        Quest("Spider Silk", "Crystal Spider", 4, 0, 2500, 150, requiredLocation = "Crystal Caverns"),
        Quest("Cave Echoes", "Cave Bat", 15, 0, 5000, 250, requiredLocation = "Crystal Caverns"),

        // Stonehold Pass
        Quest("Pass Guard", "Orc", 5, 0, 8000, 200, requiredLocation = "Stonehold Pass"),
        Quest("Stone Smasher", "Stone Golem", 3, 0, 10000, 250, requiredLocation = "Stonehold Pass"),
        Quest("Wolf Howl", "Mountain Wolf", 6, 0, 6000, 180, requiredLocation = "Stonehold Pass"),
        Quest("Mountain Trek", "Orc", 15, 0, 20000, 500, requiredLocation = "Stonehold Pass"),

        // Shadow Marsh
        Quest("Marsh Mist", "Poison Toad", 5, 0, 25000, 350, requiredLocation = "Shadow Marsh"),
        Quest("Toad Tongue", "Poison Toad", 10, 0, 80000, 700, requiredLocation = "Shadow Marsh"),
        Quest("Spirit Bound", "Dark Spirit", 4, 0, 30000, 450, requiredLocation = "Shadow Marsh"),
        Quest("Swamp Survivor", "Swamp Horror", 3, 0, 40000, 550, requiredLocation = "Shadow Marsh"),

        // Dragon Peaks
        Quest("Peak Ascent", "Stone Golem", 10, 0, 75000, 1000, requiredLocation = "Dragon Peaks"),
        Quest("Lava Scale", "Lava Serpent", 5, 0, 90000, 1200, requiredLocation = "Dragon Peaks"),
        Quest("Drake Talon", "Fire Drake", 4, 0, 100000, 1500, requiredLocation = "Dragon Peaks"),
        Quest("Fury Slayer", "Fire Drake", 12, 0, 250000, 4000, requiredLocation = "Dragon Peaks"),

        // Dark Citadel
        Quest("Citadel Siege", "Shadow Knight", 5, 0, 150000, 3000, requiredLocation = "Dark Citadel"),
        Quest("Shadow Hunt", "Dark Spirit", 20, 0, 200000, 4000, requiredLocation = "Dark Citadel"),
        Quest("Void Breaker", "Void Reaper", 5, 0, 300000, 6000, requiredLocation = "Dark Citadel"),
        Quest("Final Stand", "Void Reaper", 15, 0, 1000000, 20000, requiredLocation = "Dark Citadel")
    )

    // Initial starter quests
    val starterQuests = allQuests.filter { it.requiredLocation == "Training Fields" }

    fun getBaseName(name: String): String {
        return name
            .substringBeforeLast(" +")
            .substringAfter("Void-Touched ")
            .substringAfter("Eternal ")
    }

    fun getWeapon(name: String): Weapon {
        val baseName = getBaseName(name)
        val levelStr = name.substringAfterLast(" +", "")
        val level = levelStr.toIntOrNull() ?: 0
        val base = weapons.find { it.name == baseName } ?: weapons[0]
        
        var multiplier = 1.0 + (level * 0.02)
        if (name.contains("Void-Touched")) multiplier += 0.5
        if (name.contains("Eternal")) multiplier += 0.8
        
        return base.copy(name = name, attack = (base.attack * multiplier).toInt())
    }

    fun getArmor(name: String): Armor {
        val baseName = getBaseName(name)
        val level = name.substringAfterLast(" +", "").toIntOrNull() ?: 0
        val base = armors.find { it.name == baseName } ?: armors[0]
        
        var multiplier = 1.0 + (level * 0.02)
        if (name.contains("Void-Touched")) multiplier += 0.5
        if (name.contains("Eternal")) multiplier += 0.8
        
        return base.copy(name = name, defense = (base.defense * multiplier).toInt())
    }

    fun getHeadGear(name: String): HeadGear {
        val baseName = getBaseName(name)
        val level = name.substringAfterLast(" +", "").toIntOrNull() ?: 0
        val base = headGears.find { it.name == baseName } ?: headGears[0]
        
        var multiplier = 1.0 + (level * 0.02)
        if (name.contains("Void-Touched")) multiplier += 0.5
        if (name.contains("Eternal")) multiplier += 0.8
        
        return base.copy(name = name, defense = (base.defense * multiplier).toInt())
    }

    fun getBoots(name: String): Boots {
        val baseName = getBaseName(name)
        val level = name.substringAfterLast(" +", "").toIntOrNull() ?: 0
        val base = boots.find { it.name == baseName } ?: boots[0]
        
        var multiplier = 1.0 + (level * 0.02)
        if (name.contains("Void-Touched")) multiplier += 0.5
        if (name.contains("Eternal")) multiplier += 0.8
        
        return base.copy(name = name, agility = (base.agility * multiplier).toInt())
    }

    fun getShield(name: String): Shield {
        val baseName = getBaseName(name)
        val level = name.substringAfterLast(" +", "").toIntOrNull() ?: 0
        val base = shields.find { it.name == baseName } ?: shields[0]
        
        var multiplier = 1.0 + (level * 0.02)
        if (name.contains("Void-Touched")) multiplier += 0.5
        if (name.contains("Eternal")) multiplier += 0.8
        
        return base.copy(name = name, defense = (base.defense * multiplier).toInt())
    }

    fun getOffHand(name: String): OffHand {
        val baseName = getBaseName(name)
        val levelStr = name.substringAfterLast(" +", "")
        val level = levelStr.toIntOrNull() ?: 0
        val base = offHands.find { it.name == baseName } ?: offHands[0]
        
        var multiplier = 1.0 + (level * 0.02)
        if (name.contains("Void-Touched")) multiplier += 0.5
        if (name.contains("Eternal")) multiplier += 0.8
        
        return base.copy(
            name = name,
            strength = (base.strength * multiplier).toInt(),
            defense = (base.defense * multiplier).toInt(),
            vitality = (base.vitality * multiplier).toInt(),
            agility = (base.agility * multiplier).toInt(),
            intelligence = (base.intelligence * multiplier).toInt(),
            luck = (base.luck * multiplier).toInt(),
            wisdom = (base.wisdom * multiplier).toInt()
        )
    }

    fun getConsumable(name: String): Consumable? {
        return consumables.find { it.name == name }
    }

    fun getFishingRod(name: String): FishingRod {
        val baseName = getBaseName(name)
        return fishingRods.find { it.name == baseName } ?: FishingRod("None", 0, description = "No rod equipped.", reactionBonus = 0)
    }

    fun getClassBaseStats(className: String): List<Int> {
        return when (className) {
            "Mage" -> listOf(4, 6, 5, 20, 8, 5, 12) // str, vit, def, int, agi, luck, wis
            "Necromancer" -> listOf(5, 12, 6, 17, 6, 6, 8)
            "Samurai" -> listOf(15, 8, 8, 4, 14, 8, 3)
            "Paladin" -> listOf(10, 14, 18, 2, 4, 2, 10)
            "Assassin" -> listOf(8, 6, 4, 5, 18, 17, 2)
            "Monk" -> listOf(14, 18, 8, 2, 10, 2, 6)
            "Archer" -> listOf(13, 8, 6, 4, 17, 9, 3)
            "Bard" -> listOf(6, 10, 7, 7, 10, 10, 10)
            "Berserker" -> listOf(24, 20, 2, 2, 8, 2, 2)

            // Human
            "Hero" -> listOf(15, 15, 15, 15, 15, 15, 15)
            "King's Guard" -> listOf(15, 20, 25, 5, 10, 5, 10)
            "High Sage" -> listOf(5, 10, 10, 30, 10, 10, 25)
            "Grand Commander" -> listOf(20, 20, 20, 10, 10, 10, 25)

            // Elf
            "High Elf" -> listOf(5, 10, 5, 35, 15, 15, 25)
            "Spellblade" -> listOf(20, 15, 15, 25, 20, 10, 15)
            "Forest Warden" -> listOf(15, 25, 20, 10, 20, 10, 20)

            // Dwarf
            "Mountain Thane" -> listOf(30, 25, 20, 5, 10, 5, 15)
            "Runekeeper" -> listOf(15, 20, 20, 25, 5, 5, 30)
            "Iron Guard" -> listOf(20, 35, 40, 2, 5, 2, 10)

            // Gnome
            "Clockwork Tinkerer" -> listOf(10, 10, 15, 30, 15, 20, 25)
            "Arcane Inventor" -> listOf(5, 10, 10, 40, 10, 20, 30)
            "Burrow Sentinel" -> listOf(15, 25, 30, 10, 10, 15, 15)

            // Half-Elf
            "Wanderer Spellblade" -> listOf(25, 15, 10, 25, 20, 10, 15)
            "Urban Stalker" -> listOf(15, 10, 10, 10, 35, 25, 5)
            "Diplomat" -> listOf(5, 10, 10, 20, 10, 25, 35)

            // Half-Goblin
            "Scrappy Scavenger" -> listOf(15, 15, 10, 10, 25, 45, 10)
            "Tunnel Runner" -> listOf(10, 10, 10, 5, 45, 35, 5)
            "Boom-Maker" -> listOf(10, 10, 5, 30, 15, 40, 10)

            // Halfling
            "Master Thief" -> listOf(10, 10, 5, 10, 35, 40, 5)
            "Lucky Wanderer" -> listOf(10, 15, 10, 10, 25, 60, 10)
            "Shire Protector" -> listOf(15, 20, 25, 5, 15, 20, 10)

            // Half-Orc
            "Tribal Warlord" -> listOf(35, 30, 20, 5, 15, 10, 10)
            "Pit Fighter" -> listOf(40, 35, 15, 2, 20, 15, 2)

            // Half-Giant
            "Colossus" -> listOf(50, 45, 30, 2, 5, 5, 2)
            "World Shaper" -> listOf(40, 40, 40, 20, 5, 5, 20)
            
            // Dragonkin
            "Dragon Knight" -> listOf(25, 25, 20, 10, 10, 5, 5)
            "Flame Breather" -> listOf(15, 15, 15, 30, 5, 5, 15)
            "Scaled Vanguard" -> listOf(20, 35, 30, 5, 5, 5, 10)
            "Wyrm Stalker" -> listOf(25, 15, 10, 5, 25, 15, 5)

            // Dark Elf
            "Shadow Stalker" -> listOf(15, 10, 5, 10, 25, 15, 10)
            "Nightblade" -> listOf(25, 10, 10, 15, 30, 15, 5)
            "Void Weaver" -> listOf(5, 10, 5, 35, 15, 10, 20)
            "Obsidian Archer" -> listOf(20, 15, 10, 5, 25, 15, 10)

            // Vampire
            "Blood Mage" -> listOf(5, 35, 5, 30, 10, 10, 15)
            "Night Stalker" -> listOf(20, 15, 10, 5, 35, 15, 10)
            "Thrall Master" -> listOf(10, 20, 15, 25, 10, 15, 25)
            "Dread Knight" -> listOf(35, 25, 20, 10, 5, 5, 10)

            // Werewolf
            "Pack Leader" -> listOf(25, 20, 15, 5, 15, 15, 20)
            "Lunar Ravager" -> listOf(40, 25, 10, 5, 30, 10, 5)
            "Feral Berserker" -> listOf(50, 30, 5, 2, 20, 5, 2)
            "Moonlit Hunter" -> listOf(25, 15, 10, 5, 35, 20, 5)

            // Beholder
            "Eye Tyrant" -> listOf(5, 25, 25, 50, 10, 10, 45)
            "Gaze Weaver" -> listOf(2, 15, 15, 60, 5, 10, 50)
            "Arcane Sentinel" -> listOf(10, 35, 35, 40, 5, 5, 30)
            "Reality Warper" -> listOf(5, 20, 20, 70, 15, 20, 60)

            // Slime
            "Slime Sage" -> listOf(10, 50, 30, 15, 15, 10, 15)
            "Amorphous Tank" -> listOf(10, 70, 45, 2, 5, 5, 10)
            "Corrosive Striker" -> listOf(35, 40, 20, 10, 20, 10, 5)
            "Mimic Master" -> listOf(20, 35, 25, 20, 25, 30, 20)

            // Fairy
            "Enchanter" -> listOf(2, 10, 5, 40, 20, 25, 40)
            "Pixie Trickster" -> listOf(5, 10, 5, 25, 40, 50, 20)
            "Nature Guardian" -> listOf(15, 30, 25, 35, 15, 15, 35)
            "Luminous Sprite" -> listOf(2, 10, 5, 55, 30, 20, 50)

            // Ghost
            "Ethereal Assassin" -> listOf(25, 10, 5, 10, 50, 30, 10)
            "Soul Reaper" -> listOf(35, 15, 10, 30, 20, 15, 25)
            "Haunting Spectre" -> listOf(5, 15, 10, 45, 35, 20, 40)
            "Phantasm Lord" -> listOf(10, 20, 15, 55, 25, 25, 50)

            // Cyborg
            "Technomancer" -> listOf(15, 20, 30, 40, 10, 5, 20)
            "Cyber Commando" -> listOf(40, 35, 35, 10, 20, 10, 5)
            "Logic Core" -> listOf(5, 25, 40, 50, 10, 5, 50)
            "Mecha Pilot" -> listOf(25, 45, 50, 15, 15, 10, 10)

            // Demon
            "Void Reaver" -> listOf(40, 30, 25, 40, 20, 10, 20)
            "Abyssal Juggernaut" -> listOf(60, 50, 45, 10, 10, 5, 10)
            "Hellfire Sorcerer" -> listOf(10, 25, 20, 70, 20, 15, 35)
            "Chaos Bringer" -> listOf(50, 40, 30, 40, 25, 25, 10)

            // Angel
            "Seraph" -> listOf(25, 30, 30, 60, 20, 10, 50)
            "Archangel" -> listOf(45, 40, 35, 50, 25, 15, 40)
            "Divine Shield" -> listOf(30, 60, 70, 20, 10, 10, 45)
            "Celestial Archer" -> listOf(35, 25, 20, 30, 55, 20, 25)

            // Dracolich
            "Death Knight" -> listOf(65, 55, 50, 40, 15, 10, 25)
            "Bone Dragon" -> listOf(70, 70, 60, 20, 10, 5, 15)
            "Necrotic Wyrm" -> listOf(40, 45, 40, 65, 25, 20, 50)
            "Eternal Lich" -> listOf(15, 50, 40, 85, 20, 30, 70)

            // Royal Demon
            "Infernal Overlord" -> listOf(85, 75, 65, 85, 45, 25, 40)
            "Hell King" -> listOf(100, 80, 70, 60, 40, 30, 30)
            "Demon Prince" -> listOf(75, 70, 60, 95, 55, 40, 55)
            "Archduke of Ruin" -> listOf(90, 85, 80, 75, 30, 20, 60)

            // Shadow Monarch
            "Monarch of Shadows" -> listOf(70, 60, 40, 90, 120, 60, 70)
            "Shadow Sovereign" -> listOf(90, 75, 55, 80, 100, 50, 60)
            "Lord of Darkness" -> listOf(60, 70, 60, 110, 90, 70, 100)
            "Umbral Conqueror" -> listOf(110, 85, 70, 60, 80, 40, 50)

            // Titan
            "World Breaker" -> listOf(120, 100, 90, 10, 10, 10, 20)
            "Earth Shaper" -> listOf(70, 90, 80, 80, 20, 20, 70)
            "Colossal Guardian" -> listOf(80, 130, 120, 20, 10, 10, 50)
            "Mountain King" -> listOf(105, 110, 100, 30, 25, 25, 40)

            // Void Sovereign
            "Void Walker" -> listOf(60, 70, 60, 120, 80, 70, 120)
            "Rift Master" -> listOf(40, 60, 50, 140, 90, 80, 130)
            "Singularity Lord" -> listOf(100, 80, 70, 110, 60, 50, 100)
            "Cosmic Devourer" -> listOf(80, 100, 90, 100, 70, 90, 110)

            // Elder Dragon
            "Dragon Lord" -> listOf(130, 120, 90, 60, 50, 30, 50)
            "Ancient Wyrm" -> listOf(100, 110, 100, 90, 40, 25, 70)
            "Sky Emperor" -> listOf(90, 90, 80, 100, 110, 60, 80)
            "Primal Guardian" -> listOf(110, 140, 110, 40, 35, 30, 60)

            // Nephilim
            "Arbiter" -> listOf(85, 85, 85, 85, 85, 85, 85)
            "Divine Hybrid" -> listOf(95, 80, 75, 95, 85, 80, 90)
            "Chaos Balancer" -> listOf(90, 90, 90, 90, 90, 90, 90)
            "Nephilim Vanguard" -> listOf(110, 100, 100, 70, 70, 60, 70)
            
            else -> listOf(14, 12, 10, 4, 8, 6, 6) // Warrior
        }
    }

}
