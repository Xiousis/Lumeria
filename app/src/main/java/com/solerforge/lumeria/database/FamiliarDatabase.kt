package com.solerforge.lumeria.database

import com.solerforge.lumeria.models.ElementType
import com.solerforge.lumeria.models.Familiar
import com.solerforge.lumeria.models.FamiliarActionType

object FamiliarDatabase {
    val familiars = listOf(
        Familiar(
            name = "None",
            description = "No familiar equipped.",
            rarity = "Common",
            actionType = FamiliarActionType.Damage,
            basePower = 0,
            price = 0
        ),
        Familiar(
            name = "Stray Pup",
            description = "A loyal but scruffy pup found in the fields.",
            rarity = "Common",
            actionType = FamiliarActionType.Damage,
            basePower = 5,
            price = 5000,
            requiredClasses = listOf("Warrior", "Berserker", "Paladin", "Samurai")
        ),
        Familiar(
            name = "Forest Butterfly",
            description = "Its wings release soothing pollen.",
            rarity = "Common",
            actionType = FamiliarActionType.Heal,
            basePower = 5,
            price = 6500,
            element = ElementType.Holy,
            requiredClasses = listOf("Mage", "Bard", "Monk", "Archer")
        ),
        Familiar(
            name = "Baby Dragon",
            description = "A tiny red dragon that breathes sparks.",
            rarity = "Uncommon",
            actionType = FamiliarActionType.Damage,
            basePower = 8,
            price = 35000,
            element = ElementType.Fire,
            requiredClasses = listOf("Mage", "Berserker", "Samurai", "Warrior")
        ),
        Familiar(
            name = "Healing Wisp",
            description = "A gentle orb of light that mends wounds.",
            rarity = "Uncommon",
            actionType = FamiliarActionType.Heal,
            basePower = 10,
            price = 45000,
            element = ElementType.Holy,
            requiredClasses = listOf("Paladin", "Monk", "Mage", "Bard")
        ),
        Familiar(
            name = "Glow Worm",
            description = "Radiates a soft, energizing light.",
            rarity = "Uncommon",
            actionType = FamiliarActionType.Mana,
            basePower = 8,
            price = 25000,
            element = ElementType.Holy,
            requiredClasses = listOf("Mage", "Necromancer", "Bard")
        ),
        Familiar(
            name = "Mana Sprite",
            description = "A playful sprite that feeds on ambient energy.",
            rarity = "Rare",
            actionType = FamiliarActionType.Mana,
            basePower = 12,
            price = 120000,
            element = ElementType.Holy,
            requiredClasses = listOf("Mage", "Necromancer")
        ),
        Familiar(
            name = "Shadow Bat",
            description = "A creature of the night that weakens foes.",
            rarity = "Rare",
            actionType = FamiliarActionType.Debuff,
            basePower = 5,
            price = 150000,
            element = ElementType.Dark,
            requiredClasses = listOf("Assassin", "Necromancer", "Archer")
        ),
        Familiar(
            name = "Phoenix Chick",
            description = "A legendary bird of rebirth and intense flames.",
            rarity = "Legendary",
            actionType = FamiliarActionType.Damage,
            basePower = 25,
            price = 0,
            element = ElementType.Fire
        ),
        Familiar(
            name = "Celestial Owl",
            description = "A wise companion from the stars.",
            rarity = "Legendary",
            actionType = FamiliarActionType.Buff,
            basePower = 20,
            price = 0,
            element = ElementType.Holy
        ),
        Familiar(
            name = "Mire Hydra",
            description = "A multi-headed beast from the deep marsh.",
            rarity = "Mythic",
            actionType = FamiliarActionType.Debuff,
            basePower = 15,
            price = 0,
            element = ElementType.Dark
        ),
        Familiar(
            name = "Magma Drake",
            description = "A juvenile dragon born of pure volcanic fire.",
            rarity = "Mythic",
            actionType = FamiliarActionType.Damage,
            basePower = 45,
            price = 0,
            element = ElementType.Fire
        ),
        Familiar(
            name = "Void Eye",
            description = "An ocular construct from the deepest void.",
            rarity = "Mythic",
            actionType = FamiliarActionType.Mana,
            basePower = 25,
            price = 0,
            element = ElementType.Dark
        )
    )

    fun getFamiliar(name: String): Familiar {
        return familiars.find { it.name == name } ?: familiars[0]
    }

    fun getPowerForLevel(basePower: Int, level: Int): Int {
        // Increases power by 20% per level
        return (basePower * (1.0 + (level - 1) * 0.20)).toInt()
    }

    fun getExpForNextLevel(currentLevel: Int): Int {
        return currentLevel * 100 // 100, 200, 300...
    }
}
