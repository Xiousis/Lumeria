package com.example.lumeria.database

object StoryCodexDatabase {

    data class CodexEntry(
        val name: String,
        val description: String,
        val category: String // "Character", "Boss", "World"
    )

    val entries = listOf(
        // --- CHARACTERS ---
        CodexEntry(
            "Xious",
            "The protagonist of our story. A young man from a small village who rose from a perceived 'slacker' to the Savior of the Realm through sheer determination and magic.",
            "Character"
        ),
        CodexEntry(
            "Village Elder",
            "The elderly mentor who first pushed Xious onto the path of adventure. Though he acts grumpy and dismisses training as 'sleeping until noon,' he cares deeply for the village's safety and knows more about the ancient world than he lets on.",
            "Character"
        ),
        CodexEntry(
            "Kaela Flameheart",
            "A fierce and skilled tribal warrior known as the Crimson Huntress. Though initially dismissive of village life, she has formed an unlikely bond with Xious, accepting him as 'pack' after he proved his strength and resolve in the trials of the Emberclaw.",
            "Character"
        ),
        CodexEntry(
            "Billy",
            "The energetic owner of Billy's General Store. He seems to have a supply chain that reaches even the most dangerous corners of Lumeria. Always ready with a smile and a steep price, Billy is the lifeline for every adventurer.",
            "Character"
        ),
        CodexEntry(
            "Kazufi",
            "The master blacksmith of the Royal Forge. Kazufi is as tough as the enchanted steel she works with. She speaks with the rhythm of a hammer on an anvil and has no patience for those who don't respect their equipment. If you want your blade to sing, Kazufi is the only one to trust.",
            "Character"
        ),
        CodexEntry(
            "Frank",
            "The smooth-talking host of the Gambling House. Frank believes that life is just one big game of Blackjack. While some call him a scoundrel, he's always fair to those who play by the rules of his table.",
            "Character"
        ),
        CodexEntry(
            "Yumi",
            "The kind-hearted proprietor of the local Inn. Her cooking and warm beds have restored the spirits of countless weary travelers. She treats every adventurer like family, though she worries about the dangers they face.",
            "Character"
        ),
        CodexEntry(
            "King Alaric",
            "The steadfast ruler of Lumeria's capital. He carries the weight of a fracturing kingdom on his shoulders. While he rewards renown and loyalty, he is increasingly concerned about the void's influence spreading through his lands.",
            "Character"
        ),
        CodexEntry(
            "Master Ignis",
            "The strict and serious leader of the House of Fire. Born from the core of Mt. Ignis, he demands absolute perfection and discipline from his disciples. To him, strength is the only currency that matters in a world on fire.",
            "Character"
        ),
        CodexEntry(
            "Master Marina",
            "The majestic and kind leader of the House of Water. She guides her followers with the patience of the tides and the clarity of a mountain stream. She teaches that true power lies in fluidity and adaptation.",
            "Character"
        ),
        CodexEntry(
            "Master Zephyr",
            "The mysterious, ninja-like master of the House of Wind. A ghost amongst men, he values the secret over the shout. His past is shrouded in whispers, and his strikes are as sudden and invisible as a gale.",
            "Character"
        ),

        // --- BOSSES ---
        CodexEntry(
            "Captain Garrick",
            "A veteran swordsman who served as Xious's first real test of skill. His discipline and tactical guard have humbled many aspiring warriors.",
            "Boss"
        ),
        CodexEntry(
            "Skarr the Raider",
            "A ruthless goblin leader who organized the scattered tribes into a dangerous raiding force. He was the first sign that the monsters of the woods were growing more coordinated.",
            "Boss"
        ),
        CodexEntry(
            "The Crystal Warden",
            "An ancient construct of living mineral awakened by the humming energy of the caverns. It exists only to protect the subterranean core from those who would exploit its power.",
            "Boss"
        ),
        CodexEntry(
            "Sir Dorian",
            "Once a noble hero and defender of the pass, he was corrupted by dark magic and turned into a hollow shell. Defeating him was an act of mercy to release his soul from eternal servitude.",
            "Boss"
        ),
        CodexEntry(
            "Grondar Earthshaker",
            "A mountain giant warlord who believed that flesh and bone should crumble before the weight of the stone. His rage was felt in every tremor across Stonehold Pass.",
            "Boss"
        ),
        CodexEntry(
            "Mirefang",
            "A mutated beast that rose from the black sludge of the Shadow Marsh. It was the apex predator of the swamp, feeding on both the unwary and the spirits that haunted the mists.",
            "Boss"
        ),
        CodexEntry(
            "Infernal Warden",
            "The guardian of the Eternal Flame. He believed that fire should only be wielded by those with the resolve to endure its bite. He tested Xious's spirit in the heart of the Dragon Peaks.",
            "Boss"
        ),
        CodexEntry(
            "The Iron Juggernaut",
            "A forgotten mining construct that went haywire after centuries of dormancy. A massive engine of destruction fueled by steam and ancient gears.",
            "Boss"
        ),
        CodexEntry(
            "War Chief Krag",
            "The orc leader who united the northern tribes under a single banner of conquest. He viewed the civilized world as weak and sought to claim all of Lumeria through blood and iron.",
            "Boss"
        ),
        CodexEntry(
            "King Maldrake",
            "A ghost king of ash who returned from the grave to claim his ancient throne. He sought to turn Lumeria into a kingdom of spectral servants.",
            "Boss"
        ),
        CodexEntry(
            "Elder Wyvern Tyrant",
            "The apex dragon of the highest peaks. Its wings cast shadows over entire regions, and its breath could melt even the strongest enchanted steel.",
            "Boss"
        ),
        CodexEntry(
            "Grand Magister Veyra",
            "A mage whose ambition led her to perform forbidden rituals to tear the sky open. She believed the void was a tool for infinite knowledge, failing to see it as a force of total erasure.",
            "Boss"
        ),
        CodexEntry(
            "Lord Umbra",
            "The shadow ruler who plunged the Dark Citadel into eternal night. He served as the final gatekeeper before Lord Xarthos's seat of power.",
            "Boss"
        ),
        CodexEntry(
            "Aurelius",
            "The first hero of Lumeria who returned from the Hall of Heroes to judge the current generation. He sought to ensure that Xious was truly ready for the cosmic threats ahead.",
            "Boss"
        ),
        CodexEntry(
            "High Inquisitor Kael",
            "A zealot who purged the weak in a misguided attempt to purify the world before the void's arrival. His 'mercy' was nothing more than slaughter.",
            "Boss"
        ),
        CodexEntry(
            "Lord Xarthos",
            "The architect of the void's first incursion. A being of immense power who believes mortality is a flaw to be erased. He has ascended and returned multiple times, each more dangerous than the last.",
            "Boss"
        ),
        CodexEntry(
            "Rift Sovereign",
            "An entity of pure spatial instability that sought to tear reality apart from the inside out. Its presence was a cancer on the fabric of Lumeria.",
            "Boss"
        ),
        CodexEntry(
            "Void Prophet",
            "The harbinger of the end who viewed the destruction of all things as a necessary state of being. He spoke for the thousand dead worlds that came before.",
            "Boss"
        ),
        CodexEntry(
            "The World Eater",
            "A cosmic entity that existed before the first sunrise. It consumes civilizations and leaves only silence in its wake. The final boss of the journey.",
            "Boss"
        ),
        CodexEntry(
            "The Eternal Champion",
            "The undisputed master of the Grand Arena. A warrior who has never known defeat and whose technique is whispered to be a gift from the gods of old. Facing him is the ultimate test of mortal skill.",
            "Boss"
        ),
        CodexEntry(
            "Grand Arbiter",
            "The supreme judge of the arena who decides which warriors are worthy of ascending to the Master Rank. He wields both law and magic with terrifying precision.",
            "Boss"
        ),
        CodexEntry(
            "Lord of the Void",
            "A being of pure, destructive energy that somehow manifested within the arena's most dangerous trials. It is a living rift that seeks to unmake its challengers.",
            "Boss"
        ),

        // --- WORLD ---
        CodexEntry(
            "Lumeria",
            "A world once peaceful, now caught in the crosshairs of ancient entities and void-borne terrors. Its diverse regions hold the keys to its survival and the secrets of the ancient creators.",
            "World"
        ),
        CodexEntry(
            "Training Fields",
            "The outskirts of Xious's home village. A serene landscape of rolling hills where many young adventurers take their first steps into the wider world.",
            "World"
        ),
        CodexEntry(
            "Goblin Forest",
            "A dense, overgrown woodland infested with goblin tribes. The air is thick with the scent of damp moss and woodsmoke from countless hidden camps.",
            "World"
        ),
        CodexEntry(
            "Crystal Caverns",
            "A subterranean wonder filled with glowing flora and jagged mineral formations. Beautiful but deadly, home to creatures made of living stone.",
            "World"
        ),
        CodexEntry(
            "Stonehold Pass",
            "A high-altitude mountain path. Harsh winds and frequent rockslides make the journey difficult, and the pass is guarded by ancient stone titans.",
            "World"
        ),
        CodexEntry(
            "Shadow Marsh",
            "A gloomy, fog-drenched swamp where the sun rarely penetrates the canopy. Murky waters home to venomous toads and vengeful spirits.",
            "World"
        ),
        CodexEntry(
            "Dragon Peaks",
            "The highest mountains in Lumeria, scorched by volcanic activity. This is the domain of drakes and dragons who do not tolerate intruders.",
            "World"
        ),
        CodexEntry(
            "Dark Citadel",
            "A monolithic fortress of obsidian and despair. It is the seat of Lord Xarthos and the heart of the darkness threatening to consume Lumeria.",
            "World"
        ),
        CodexEntry(
            "Grand Arena",
            "The ultimate stage for Lumeria's warriors. Fighters from all ranks gather here to test their might and climb the ranks from Bronze to Master.",
            "World"
        ),
        CodexEntry(
            "Tower of Trials",
            "A 100-floor gauntlet of increasingly powerful enemies. It is said that only a true champion can reach the peak and face the final trial within.",
            "World"
        ),
        CodexEntry(
            "The Void",
            "A primordial force of non-existence that seeks to consume all of creation. It is not merely a place, but a terminal hunger that erases memory, time, and matter. Lord Xarthos is its primary conduit in Lumeria.",
            "World"
        ),
        CodexEntry(
            "The Cosmic Loom",
            "The metaphysical engine that weaves the threads of reality. It was created by the prime architects to maintain the balance of Lumeria. When the Loom is damaged, 'Rifts' appear, allowing the void to leak through.",
            "World"
        ),
        CodexEntry(
            "God Tier Relics",
            "Ten legendary artifacts forged from the heart of dying stars. Items like 'Endbringer' and the 'Mantle of Perseverance' are said to grant their wielders power that rivals the ancient creators themselves.",
            "World"
        )
    )
}
