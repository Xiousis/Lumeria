package com.solerforge.lumeria.database

import androidx.compose.ui.graphics.Color

data class PlayerClass(
    val name: String,
    val description: String,
    val color: Color,
    val isMonsterClass: Boolean = false,
    val requiredRaces: List<String> = emptyList() // Empty means all races in that faction
)

object ClassDatabase {
    val classes = listOf(
        PlayerClass("Warrior", "High Strength & Vitality. A master of the blade.", Color(0xFFD32F2F)),
        PlayerClass("Mage", "High Intelligence & Mana. Wielder of arcane arts.", Color(0xFF1976D2)),
        PlayerClass("Samurai", "High Agility & Luck. A swift and precise fighter.", Color(0xFF388E3C)),
        PlayerClass("Paladin", "Ultimate Defense & Holy Power. An unbreakable wall.", Color(0xFFFBC02D)),
        PlayerClass("Assassin", "Extreme Luck & Agility. Strikes from the shadows.", Color(0xFF7B1FA2)),
        PlayerClass("Monk", "High Vitality & Wisdom. Uses spirit and fists.", Color(0xFFE64A19)),
        PlayerClass("Archer", "Balanced Agility & Strength. Master of long range.", Color(0xFF689F38)),
        PlayerClass("Necromancer", "High Mana & Dark Power. Commands the void.", Color(0xFF424242)),
        PlayerClass("Bard", "High Luck & Wisdom. Inspires through music.", Color(0xFFF06292)),
        PlayerClass("Berserker", "Pure Strength & Speed. Reckless destruction.", Color(0xFFB71C1C)),

        // UNIQUE RACE CLASSES (Heroes)
        // Human
        PlayerClass("Hero", "A legendary champion of humanity.", Color(0xFFFFD700), false, listOf("Human")),
        PlayerClass("King's Guard", "The elite protector of the realm.", Color(0xFFC0C0C0), false, listOf("Human")),
        PlayerClass("High Sage", "A master of all known human magics.", Color(0xFF4169E1), false, listOf("Human")),
        PlayerClass("Grand Commander", "A strategic genius who leads humanity to victory.", Color(0xFFDAA520), false, listOf("Human")),

        // Elf
        PlayerClass("High Elf", "An ancient noble with supreme magical affinity.", Color(0xFF00FA9A), false, listOf("Elf")),
        PlayerClass("Spellblade", "Combines elven grace with arcane strikes.", Color(0xFF8A2BE2), false, listOf("Elf")),
        PlayerClass("Forest Warden", "The ultimate protector of the deep woods.", Color(0xFF228B22), false, listOf("Elf")),

        // Dwarf
        PlayerClass("Mountain Thane", "A legendary dwarven warrior of the peaks.", Color(0xFF8B4513), false, listOf("Dwarf")),
        PlayerClass("Runekeeper", "Inscribes ancient power into the very earth.", Color(0xFF4682B4), false, listOf("Dwarf")),
        PlayerClass("Iron Guard", "An immovable wall of dwarven steel.", Color(0xFF708090), false, listOf("Dwarf")),

        // Gnome
        PlayerClass("Clockwork Tinkerer", "Uses mechanical marvels to dominate the battlefield.", Color(0xFF6B4226), false, listOf("Gnome")),
        PlayerClass("Arcane Inventor", "Pushes the boundaries of magic and science.", Color(0xFF00CED1), false, listOf("Gnome")),
        PlayerClass("Burrow Sentinel", "A stout defender of the hidden gnome warrens.", Color(0xFF556B2F), false, listOf("Gnome")),

        // Half-Elf
        PlayerClass("Wanderer Spellblade", "A traveling master of both blade and sorcery.", Color(0xFF9370DB), false, listOf("Half-Elf")),
        PlayerClass("Urban Stalker", "A master of city streets and dark alleys.", Color(0xFF2F4F4F), false, listOf("Half-Elf")),
        PlayerClass("Diplomat", "Navigates the complexities of politics with wisdom.", Color(0xFFBDB76B), false, listOf("Half-Elf")),

        // Half-Goblin
        PlayerClass("Scrappy Scavenger", "Finds opportunity and weapons in the trash of others.", Color(0xFF8B8B83), false, listOf("Half-Goblin")),
        PlayerClass("Tunnel Runner", "Incredibly swift in tight spaces and dark caves.", Color(0xFF556B2F), false, listOf("Half-Goblin")),
        PlayerClass("Boom-Maker", "Obsessed with things that go 'bang' in the night.", Color(0xFFFF4500), false, listOf("Half-Goblin")),

        // Halfling
        PlayerClass("Master Thief", "The pinnacle of stealth and nimble fingers.", Color(0xFF2F4F4F), false, listOf("Halfling")),
        PlayerClass("Lucky Wanderer", "Fortune favors this light-footed traveler.", Color(0xFFDAA520), false, listOf("Halfling")),
        PlayerClass("Shire Protector", "A surprisingly resilient guardian of the small folk.", Color(0xFF8FBC8F), false, listOf("Halfling")),

        // Half-Orc
        PlayerClass("Tribal Warlord", "Unites the clans through raw strength.", Color(0xFF800000), false, listOf("Half-Orc")),
        PlayerClass("Pit Fighter", "A brutal gladiator who thrives in combat.", Color(0xFFB22222), false, listOf("Half-Orc")),

        // Half-Giant
        PlayerClass("Colossus", "A towering force of nature.", Color(0xFF696969), false, listOf("Half-Giant")),
        PlayerClass("World Shaper", "Uses immense strength to alter the battlefield.", Color(0xFFD2B48C), false, listOf("Half-Giant")),
        
        // UNIQUE RACE CLASSES (Monsters)
        // Dragonkin
        PlayerClass("Dragon Knight", "Draconic might and fire. A master of draconic combat.", Color(0xFFFF5722), true, listOf("Dragonkin")),
        PlayerClass("Flame Breather", "Commands the searing heat of dragon fire.", Color(0xFFFF7043), true, listOf("Dragonkin")),
        PlayerClass("Scaled Vanguard", "An unbreakable shield of dragon scales.", Color(0xFFBF360C), true, listOf("Dragonkin")),
        PlayerClass("Wyrm Stalker", "A swift predator that hunts like a flying drake.", Color(0xFFD84315), true, listOf("Dragonkin")),

        // Dark Elf
        PlayerClass("Shadow Stalker", "Strikes from the deepest shadows with precision.", Color(0xFF2D3436), true, listOf("Dark Elf")),
        PlayerClass("Nightblade", "A master of silent lethality and dark steel.", Color(0xFF37474F), true, listOf("Dark Elf")),
        PlayerClass("Void Weaver", "Manipulates the essence of the void into deadly spells.", Color(0xFF4527A0), true, listOf("Dark Elf")),
        PlayerClass("Obsidian Archer", "Fires arrows tipped with soul-piercing obsidian.", Color(0xFF212121), true, listOf("Dark Elf")),

        // Vampire
        PlayerClass("Blood Mage", "Sacrifices vitality for absolute hemomantic power.", Color(0xFF8B0000), true, listOf("Vampire")),
        PlayerClass("Night Stalker", "A relentless predator of the eternal night.", Color(0xFF4A148C), true, listOf("Vampire")),
        PlayerClass("Thrall Master", "Commands the weak-willed through dark charisma.", Color(0xFF311B92), true, listOf("Vampire")),
        PlayerClass("Dread Knight", "An undead juggernaut clad in blood-soaked plate.", Color(0xFF1A237E), true, listOf("Vampire")),

        // Werewolf
        PlayerClass("Pack Leader", "Inspires feral allies with primal roars.", Color(0xFF5D4037), true, listOf("Werewolf")),
        PlayerClass("Lunar Ravager", "Gains immense power under the pale moonlight.", Color(0xFF4E342E), true, listOf("Werewolf")),
        PlayerClass("Feral Berserker", "Surrenders all reason for beastly destruction.", Color(0xFF3E2723), true, listOf("Werewolf")),
        PlayerClass("Moonlit Hunter", "Tracks prey with unmatched lupine senses.", Color(0xFF212121), true, listOf("Werewolf")),

        // Beholder
        PlayerClass("Eye Tyrant", "Commands reality through its many eyes.", Color(0xFF6D4C41), true, listOf("Beholder")),
        PlayerClass("Gaze Weaver", "Spins webs of arcane energy with a single look.", Color(0xFF5D4037), true, listOf("Beholder")),
        PlayerClass("Arcane Sentinel", "An ever-watchful guardian of ancient secrets.", Color(0xFF4E342E), true, listOf("Beholder")),
        PlayerClass("Reality Warper", "Distorts the fabric of space with sheer will.", Color(0xFF3E2723), true, listOf("Beholder")),

        // Slime
        PlayerClass("Slime Sage", "Uses its fluid form to adapt to any threat.", Color(0xFF55EFC4), true, listOf("Slime")),
        PlayerClass("Amorphous Tank", "Absorbs all damage into its gelatinous mass.", Color(0xFF00B894), true, listOf("Slime")),
        PlayerClass("Corrosive Striker", "Dissolves armor and bone with acidic strikes.", Color(0xFF00D2D3), true, listOf("Slime")),
        PlayerClass("Mimic Master", "Shapes its body to replicate any foe.", Color(0xFF01CBCB), true, listOf("Slime")),

        // Fairy
        PlayerClass("Enchanter", "Commands the beauty of the natural world.", Color(0xFFFAB1A0), true, listOf("Fairy")),
        PlayerClass("Pixie Trickster", "Confuses enemies with illusions and dust.", Color(0xFFFF8A65), true, listOf("Fairy")),
        PlayerClass("Nature Guardian", "Defends the woods with thorns and vines.", Color(0xFF81C784), true, listOf("Fairy")),
        PlayerClass("Luminous Sprite", "A beacon of pure, blinding magical light.", Color(0xFFFFF176), true, listOf("Fairy")),

        // Ghost
        PlayerClass("Ethereal Assassin", "Passes through walls to deliver a killing blow.", Color(0xFFB0BEC5), true, listOf("Ghost")),
        PlayerClass("Soul Reaper", "Collects the essence of the fallen to fuel its power.", Color(0xFF78909C), true, listOf("Ghost")),
        PlayerClass("Haunting Spectre", "Terrifies foes with wails from the beyond.", Color(0xFF546E7A), true, listOf("Ghost")),
        PlayerClass("Phantasm Lord", "Rules over the spirits of the long deceased.", Color(0xFF37474F), true, listOf("Ghost")),

        // Cyborg
        PlayerClass("Technomancer", "Uses ancient logic to manipulate mana.", Color(0xFF00CEC9), true, listOf("Cyborg")),
        PlayerClass("Cyber Commando", "Integrated weaponry for maximum efficiency.", Color(0xFF00ACC1), true, listOf("Cyborg")),
        PlayerClass("Logic Core", "Processes combat scenarios with mechanical precision.", Color(0xFF00838F), true, listOf("Cyborg")),
        PlayerClass("Mecha Pilot", "Commands a massive suit of ancient technology.", Color(0xFF006064), true, listOf("Cyborg")),

        // Demon
        PlayerClass("Void Reaver", "Devours the essence of the void.", Color(0xFFA855F7), true, listOf("Demon")),
        PlayerClass("Abyssal Juggernaut", "An unstoppable force of demonic destruction.", Color(0xFF7E57C2), true, listOf("Demon")),
        PlayerClass("Hellfire Sorcerer", "Wields flames that burn even the soul.", Color(0xFFD32F2F), true, listOf("Demon")),
        PlayerClass("Chaos Bringer", "A herald of the end times, thriving in disorder.", Color(0xFFB71C1C), true, listOf("Demon")),

        // Angel
        PlayerClass("Seraph", "Celestial judge of the high heavens.", Color(0xFFFFD600), false, listOf("Angel")),
        PlayerClass("Archangel", "A supreme commander of the divine host.", Color(0xFFFFEA00), false, listOf("Angel")),
        PlayerClass("Divine Shield", "An unbreakable barrier of holy light.", Color(0xFFFDD835), false, listOf("Angel")),
        PlayerClass("Celestial Archer", "Fires beams of light with divine accuracy.", Color(0xFFFFEE58), false, listOf("Angel")),

        // Dracolich
        PlayerClass("Death Knight", "A skeletal champion of the frozen void.", Color(0xFF455A64), true, listOf("Dracolich")),
        PlayerClass("Bone Dragon", "A terrifying construct of ancient draconic remains.", Color(0xFF607D8B), true, listOf("Dracolich")),
        PlayerClass("Necrotic Wyrm", "Spreads plague and decay with every breath.", Color(0xFF78909C), true, listOf("Dracolich")),
        PlayerClass("Eternal Lich", "A master of death who can never truly die.", Color(0xFF90A4AE), true, listOf("Dracolich")),

        // Royal Demon
        PlayerClass("Infernal Overlord", "The ultimate ruler of the demonic host.", Color(0xFFB71C1C), true, listOf("Royal Demon")),
        PlayerClass("Hell King", "Sits upon the throne of the deepest abyss.", Color(0xFFC62828), true, listOf("Royal Demon")),
        PlayerClass("Demon Prince", "The favored heir of the dark legions.", Color(0xFFD32F2F), true, listOf("Royal Demon")),
        PlayerClass("Archduke of Ruin", "Administers the destruction of entire worlds.", Color(0xFFE53935), true, listOf("Royal Demon")),

        // Shadow Monarch
        PlayerClass("Monarch of Shadows", "Commands the very shadows of existence.", Color(0xFF212121), true, listOf("Shadow Monarch")),
        PlayerClass("Shadow Sovereign", "The supreme ruler of the umbral realm.", Color(0xFF000000), true, listOf("Shadow Monarch")),
        PlayerClass("Lord of Darkness", "Extinguishes all light with its presence.", Color(0xFF424242), true, listOf("Shadow Monarch")),
        PlayerClass("Umbral Conqueror", "Expands the domain of shadows across all lands.", Color(0xFF616161), true, listOf("Shadow Monarch")),

        // Titan
        PlayerClass("World Breaker", "Crushes planets with a single blow.", Color(0xFF636E72), true, listOf("Titan")),
        PlayerClass("Earth Shaper", "Molds the world to its monumental will.", Color(0xFF4E342E), true, listOf("Titan")),
        PlayerClass("Colossal Guardian", "A living mountain that protects the earth.", Color(0xFF3E2723), true, listOf("Titan")),
        PlayerClass("Mountain King", "The ancient ruler of the deepest peaks.", Color(0xFF2D3436), true, listOf("Titan")),

        // Void Sovereign
        PlayerClass("Void Walker", "Master of the space between stars.", Color(0xFF6C5CE7), true, listOf("Void Sovereign")),
        PlayerClass("Rift Master", "Tears holes in reality to summon void energy.", Color(0xFF5E35B1), true, listOf("Void Sovereign")),
        PlayerClass("Singularity Lord", "Contains the power of a collapsing star.", Color(0xFF4527A0), true, listOf("Void Sovereign")),
        PlayerClass("Cosmic Devourer", "Consumes entire galaxies to fuel its hunger.", Color(0xFF311B92), true, listOf("Void Sovereign")),

        // Elder Dragon
        PlayerClass("Dragon Lord", "The ultimate peak of draconic power.", Color(0xFFD63031), true, listOf("Elder Dragon")),
        PlayerClass("Ancient Wyrm", "A creature of legend that has seen aeons pass.", Color(0xFFC62828), true, listOf("Elder Dragon")),
        PlayerClass("Sky Emperor", "Rules the heavens with wings that blot out the sun.", Color(0xFFB71C1C), true, listOf("Elder Dragon")),
        PlayerClass("Primal Guardian", "The first dragon, protector of all life and death.", Color(0xFF8E0000), true, listOf("Elder Dragon")),

        // Nephilim
        PlayerClass("Arbiter", "Enforcer of the cosmic balance.", Color(0xFFFDCB6E), true, listOf("Nephilim")),
        PlayerClass("Divine Hybrid", "Possesses the strengths of both light and dark.", Color(0xFFFFB300), true, listOf("Nephilim")),
        PlayerClass("Chaos Balancer", "Maintains order by weaponizing absolute chaos.", Color(0xFFFFA000), true, listOf("Nephilim")),
        PlayerClass("Nephilim Vanguard", "The elite soldier of the celestial middle-ground.", Color(0xFFFF8F00), true, listOf("Nephilim"))
    )

    fun getAvailableClasses(raceName: String): List<PlayerClass> {
        val race = RaceDatabase.getRace(raceName)
        return classes.filter { pc ->
            pc.isMonsterClass == race.isMonster && (pc.requiredRaces.isEmpty() || pc.requiredRaces.contains(raceName))
        }
    }
}
