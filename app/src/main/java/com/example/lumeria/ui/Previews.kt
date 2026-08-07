package com.example.lumeria.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.lumeria.battle.BattleScreen
import com.example.lumeria.battle.BattleViewModel
import com.example.lumeria.components.HpBar
import com.example.lumeria.components.ManaBar
import com.example.lumeria.data.PlayerData
import com.example.lumeria.data.Quest
import com.example.lumeria.database.StoryDatabase
import com.example.lumeria.screens.ArcCompletionScreen
import com.example.lumeria.screens.GamblingHouseScreen
import com.example.lumeria.screens.GameMenuScreen
import com.example.lumeria.screens.ElderRitualScreen
import com.example.lumeria.screens.InnScreen
import com.example.lumeria.screens.InventoryScreen
import com.example.lumeria.screens.MainMenu
import com.example.lumeria.screens.QuestScreen
import com.example.lumeria.screens.ShopScreen
import com.example.lumeria.screens.SkillScreen
import com.example.lumeria.screens.StatsScreen
import com.example.lumeria.screens.StoryDialogueScreen
import com.example.lumeria.screens.StoryJournalScreen
import com.example.lumeria.screens.StoryScreen
import com.example.lumeria.screens.StorySelectionScreen
import com.example.lumeria.screens.TitleScreen
import com.example.lumeria.screens.WorldMapScreen
import com.example.lumeria.screens.DefeatedScreen
import com.example.lumeria.theme.TEXTBASEDRPGMAGICTheme

// ---------------------------------------------------------
// STORY PREVIEWS
// ---------------------------------------------------------

@Preview(showBackground = true, name = "Story Selection")
@Composable
fun StorySelectionPreview() {
    TEXTBASEDRPGMAGICTheme {
        StorySelectionScreen(
            playerData = PlayerData(level = 10, completedStoryArcs = listOf(1)),
            onSelectArc = { _ -> },
            onReturn = {}
        )
    }
}

@Preview(showBackground = true, name = "Story Dialogue")
@Composable
fun StoryDialoguePreview() {
    val arc = StoryDatabase.arcs[0]
    TEXTBASEDRPGMAGICTheme {
        StoryDialogueScreen(
            arc = arc,
            eventIndex = 0,
            onNext = {},
            onChoiceSelected = { _ -> },
            onBattleStart = { _, _ -> },
            onReturn = {}
        )
    }
}

@Preview(showBackground = true, name = "Arc Completion")
@Composable
fun ArcCompletionPreview() {
    val arc = StoryDatabase.arcs[0]
    TEXTBASEDRPGMAGICTheme {
        ArcCompletionScreen(
            arc = arc,
            nextArc = StoryDatabase.arcs[1],
            onContinue = {}
        )
    }
}

@Preview(showBackground = true, name = "Story Journal")
@Composable
fun StoryJournalPreview() {
    TEXTBASEDRPGMAGICTheme {
        StoryJournalScreen(
            playerData = PlayerData(),
            onReturn = {}
        )
    }
}

// ---------------------------------------------------------
// COMPONENT PREVIEWS
// ---------------------------------------------------------

@Preview(showBackground = true, name = "HP & Mana Bars")
@Composable
fun BarsPreview() {
    TEXTBASEDRPGMAGICTheme {
        Column(modifier = Modifier.padding(16.dp)) {
            HpBar(15, 30, isHero = true)
            Spacer(Modifier.height(8.dp))
            HpBar(15, 30, isHero = false)
            Spacer(Modifier.height(8.dp))
            ManaBar(10, 20, isHero = true)
            Spacer(Modifier.height(8.dp))
            ManaBar(10, 20, isHero = false)
        }
    }
}

// ---------------------------------------------------------
// TITLE & INTRO PREVIEWS
// ---------------------------------------------------------

@Preview(showBackground = true, name = "Title Screen")
@Composable
fun TitlePreview() {
    TEXTBASEDRPGMAGICTheme {
        TitleScreen(onStart = {}, onSettings = {})
    }
}

@Preview(showBackground = true, name = "Main Menu")
@Composable
fun MainMenuPreview() {
    TEXTBASEDRPGMAGICTheme {
        MainMenu(
            activePlayerData = PlayerData(),
            slot1Data = PlayerData(level = 5, currentLocation = "Goblin Forest"),
            slot2Data = null,
            slot3Data = null,
            activeSlot = 1,
            onSelectSlot = {},
            onStartGame = {},
            onNewGame = {},
            onSettings = {}
        )
    }
}

@Preview(showBackground = true, name = "Story Intro")
@Composable
fun StoryPreview() {
    TEXTBASEDRPGMAGICTheme {
        StoryScreen {}
    }
}

// ---------------------------------------------------------
// GAME MENU & DEATH PREVIEWS
// ---------------------------------------------------------

@Preview(showBackground = true, name = "Game Menu")
@Composable
fun GameMenuPreview() {
    TEXTBASEDRPGMAGICTheme {
        GameMenuScreen(
            onBattle = {},
            onStory = {},
            onSkills = {},
            onShop = {},
            onInventory = {},
            onQuests = {},
            onStats = {},
            onElder = {},
            onInn = {},
            onGambling = {},
            onJournal = {},
            onBestiary = {},
            onArena = {},
            onBlacksmith = {},
            onBank = {},
            onBounty = {},
            onKingdom = {},
            onGuild = {},
            onTower = {},
            onTrophyRoom = {},
            onCodex = {},
            onFishing = {},
            onFamiliarStore = {},
            onSettings = {},
            selectedTabIndex = 0,
            onTabSelected = {},
            onReturnToMain = {},
            playerData = PlayerData(),
            towerUnlockMessage = null,
            onDismissTowerUnlock = {}
        )
    }
}

@Preview(showBackground = true, name = "Gambling House")
@Composable
fun GamblingHousePreview() {
    TEXTBASEDRPGMAGICTheme {
        GamblingHouseScreen(
            playerData = PlayerData(gold = 1000),
            onPlayerUpdate = {},
            onReturn = {}
        )
    }
}

@Preview(showBackground = true, name = "Defeated Screen")
@Composable
fun DefeatedPreview() {
    TEXTBASEDRPGMAGICTheme {
        DefeatedScreen(goldLost = 150) {}
    }
}

// ---------------------------------------------------------
// COMBAT PREVIEWS
// ---------------------------------------------------------

@Preview(showBackground = true, name = "Battle Screen")
@Composable
fun BattlePreview() {
    val mockViewModel = BattleViewModel(playerData = PlayerData())
    TEXTBASEDRPGMAGICTheme {
        BattleScreen(
            viewModel = mockViewModel,
            onDeath = {},
            onPlayerUpdate = { _ -> },
            onBattleAgain = { _ -> },
            onLeave = { _ -> }
        )
    }
}

@Preview(showBackground = true, name = "Yumi's Inn")
@Composable
fun InnPreview() {
    TEXTBASEDRPGMAGICTheme {
        InnScreen(
            playerData = PlayerData(gold = 500),
            onPlayerUpdate = {},
            onReturn = {}
        )
    }
}

@Preview(showBackground = true, name = "Elder's Hut - New Player")
@Composable
fun ElderRitualNewPreview() {
    TEXTBASEDRPGMAGICTheme {
        ElderRitualScreen(
            playerData = PlayerData(gold = 100000, freeWishesUsed = 0),
            onPlayerUpdate = {},
            onReturn = {}
        )
    }
}

@Preview(showBackground = true, name = "Elder's Hut - Selection")
@Composable
fun ElderRitualSelectionPreview() {
    TEXTBASEDRPGMAGICTheme {
        // We can't easily force internal state of ElderRitualScreen from outside 
        // because it uses 'remember'. But we can check if the code allows it.
        ElderRitualScreen(
            playerData = PlayerData(
                gold = 100000, 
                freeWishesUsed = 3,
                unlockedTraits = listOf("Brawny", "Nimble")
            ),
            onPlayerUpdate = {},
            onReturn = {}
        )
    }
}

// ---------------------------------------------------------
// ECONOMY PREVIEWS
// ---------------------------------------------------------

@Preview(showBackground = true, name = "Billy's Shop")
@Composable
fun ShopPreview() {
    TEXTBASEDRPGMAGICTheme {
        ShopScreen(
            modifier = Modifier.padding(16.dp),
            playerData = PlayerData(gold = 100),
            onPlayerUpdate = {},
            onReturn = {}
        )
    }
}

@Preview(showBackground = true, name = "Inventory")
@Composable
fun InventoryPreview() {
    TEXTBASEDRPGMAGICTheme {
        InventoryScreen(
            playerData = PlayerData(inventory = listOf("Iron Sword", "Chain Mail", "Potion", "Potion")),
            onPlayerUpdate = {},
            onReturn = {}
        )
    }
}

// ---------------------------------------------------------
// PROGRESSION PREVIEWS
// ---------------------------------------------------------

@Preview(showBackground = true, name = "Quest Log")
@Composable
fun QuestPreview() {
    TEXTBASEDRPGMAGICTheme {
        QuestScreen(
            modifier = Modifier.padding(16.dp),
            playerData = PlayerData(
                quests = listOf(
                    Quest("Slime Squash", "Slime", 3, 1, 50, 20),
                    Quest("Goblin Guard", "Training Goblin", 5, 5, 40, 15, isCompleted = true)
                )
            ),
            onPlayerUpdate = {},
            onReturn = {}
        )
    }
}

@Preview(showBackground = true, name = "Character Stats")
@Composable
fun StatsPreview() {
    TEXTBASEDRPGMAGICTheme {
        StatsScreen(
            modifier = Modifier.padding(16.dp),
            playerData = PlayerData(statPoints = 5),
            onPlayerUpdate = {},
            onReturn = {}
        )
    }
}

@Preview(showBackground = true, name = "Skill Management")
@Composable
fun SkillPreview() {
    TEXTBASEDRPGMAGICTheme {
        SkillScreen(
            modifier = Modifier.padding(16.dp),
            playerData = PlayerData(),
            onPlayerUpdate = {},
            onReturn = {}
        )
    }
}

@Preview(showBackground = true, name = "World Map")
@Composable
fun WorldMapPreview() {
    TEXTBASEDRPGMAGICTheme {
        WorldMapScreen(
            modifier = Modifier.padding(16.dp),
            playerData = PlayerData(),
            onTravel = {},
            onBossBattle = {},
            onReturn = {}
        )
    }
}
