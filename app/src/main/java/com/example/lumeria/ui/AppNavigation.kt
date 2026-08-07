package com.example.lumeria.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import com.example.lumeria.MainViewModel
import com.example.lumeria.R
import com.example.lumeria.battle.BattleScreen
import com.example.lumeria.battle.BattleViewModel
import com.example.lumeria.battle.HapticType
import com.example.lumeria.models.Screen
import com.example.lumeria.screens.*
import com.example.lumeria.viewmodels.*
import com.example.lumeria.components.DonationDialog
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.platform.LocalContext
import android.app.Activity

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    storyViewModel: StoryViewModel,
    kingdomViewModel: KingdomViewModel,
    economyViewModel: EconomyViewModel,
    adventureViewModel: AdventureViewModel,
    billingViewModel: BillingViewModel,
    contentPadding: PaddingValues,
    modifier: Modifier = Modifier
) {
    val playerData by mainViewModel.playerData.collectAsState()
    var showDonationDialog by remember { mutableStateOf(false) }

    Box(modifier = modifier) {
        AnimatedContent(
            targetState = mainViewModel.currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) togetherWith
                        fadeOut(animationSpec = tween(400))
            },
            label = "ScreenTransition"
        ) { targetScreen ->
            val isFullFrame = targetScreen != Screen.Settings && targetScreen != Screen.ArcCompletion
            
            Box(
                modifier = if (isFullFrame) Modifier.fillMaxSize() 
                           else Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing)
            ) {
                when (targetScreen) {
                    Screen.Title -> TitleScreen(
                        onStart = { mainViewModel.navigateTo(Screen.MainMenu) },
                        onSettings = { mainViewModel.navigateTo(Screen.Settings) }
                    )

                    Screen.MainMenu -> {
                        val slot1 by mainViewModel.slot1Data.collectAsState()
                        val slot2 by mainViewModel.slot2Data.collectAsState()
                        val slot3 by mainViewModel.slot3Data.collectAsState()

                        MainMenu(
                            activePlayerData = playerData,
                            slot1Data = slot1,
                            slot2Data = slot2,
                            slot3Data = slot3,
                            activeSlot = mainViewModel.activeSlot,
                            onSelectSlot = { mainViewModel.selectSlot(it) },
                            onStartGame = { mainViewModel.onStartGame() },
                            onNewGame = { mainViewModel.onNewGame() },
                            onSettings = { mainViewModel.navigateTo(Screen.Settings) }
                        )
                    }

                    Screen.Story -> StoryScreen {
                        mainViewModel.onIntroSeen()
                    }

                    Screen.GameMenu -> GameMenuScreen(
                        onBattle = { mainViewModel.navigateTo(Screen.WorldMap) },
                        onStory = { mainViewModel.navigateTo(Screen.StorySelection) },
                        onSkills = { mainViewModel.navigateTo(Screen.Skills) },
                        onShop = { mainViewModel.onEnterShop() },
                        onInventory = { mainViewModel.navigateTo(Screen.Inventory) },
                        onQuests = { mainViewModel.navigateTo(Screen.Quests) },
                        onStats = { mainViewModel.navigateTo(Screen.Stats) },
                        onElder = { mainViewModel.navigateTo(Screen.ElderRitual) },
                        onInn = { mainViewModel.onEnterInn() },
                        onGambling = { mainViewModel.navigateTo(Screen.GamblingHouse) },
                        onJournal = { mainViewModel.navigateTo(Screen.StoryJournal) },
                        onBestiary = { mainViewModel.navigateTo(Screen.Bestiary) },
                        onArena = { mainViewModel.navigateTo(Screen.ArenaSelection) },
                        onBlacksmith = { mainViewModel.onEnterForge() },
                        onBank = { mainViewModel.navigateTo(Screen.Bank) },
                        onBounty = { mainViewModel.navigateTo(Screen.BountyBoard) },
                        onKingdom = { mainViewModel.navigateTo(Screen.Kingdom) },
                        onGuild = { mainViewModel.navigateTo(Screen.Guild) },
                        onTower = { mainViewModel.navigateTo(Screen.Tower) },
                        onTrophyRoom = { mainViewModel.navigateTo(Screen.TrophyRoom) },
                        onCodex = { mainViewModel.navigateTo(Screen.Codex) },
                        onFishing = { mainViewModel.navigateTo(Screen.Fishing) },
                        onFamiliarStore = { mainViewModel.navigateTo(Screen.FamiliarStore) },
                        onSettings = { mainViewModel.navigateTo(Screen.Settings) },
                        selectedTabIndex = mainViewModel.gameMenuTabIndex,
                        onTabSelected = { mainViewModel.updateGameMenuTab(it) },
                        onReturnToMain = { mainViewModel.navigateTo(Screen.MainMenu) },
                        playerData = playerData,
                        towerUnlockMessage = mainViewModel.towerUnlockMessage,
                        onDismissTowerUnlock = { mainViewModel.dismissTowerUnlock() }
                    )

                    Screen.Battle -> {
                        val settings by mainViewModel.settings.collectAsState()
                        val haptic = LocalHapticFeedback.current
                        val battleViewModel = remember(mainViewModel.battleSeed) {
                            BattleViewModel(
                                playerData = mainViewModel.grindingPlayerData ?: playerData,
                                isBossBattle = mainViewModel.isBossBattle,
                                storyEnemyName = mainViewModel.storyEnemyName,
                                arenaOpponent = mainViewModel.arenaOpponent,
                                activeBounty = playerData.activeBountyId?.let { com.example.lumeria.database.BountyDatabase.getBounty(it) },
                                isTowerBattle = mainViewModel.isTowerBattle,
                                isRiftBattle = mainViewModel.isRiftBattle,
                                guildExam = mainViewModel.currentGuildExam,
                                locationOverride = mainViewModel.locationOverride,
                                onHaptic = { type ->
                                    if (settings.hapticsEnabled) {
                                        when (type) {
                                            HapticType.HIT -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                            HapticType.CRIT -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            HapticType.DEATH -> haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                                            else -> haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                        }
                                    }
                                }
                            )
                        }
                        BattleScreen(
                            viewModel = battleViewModel,
                            onDeath = { mainViewModel.onBattleDeath() },
                            onPlayerUpdate = { newData -> mainViewModel.onPlayerBattleUpdate(newData) },
                            onBattleAgain = { newData -> mainViewModel.onBattleAgain(newData) },
                            onLeave = { newData -> 
                                if (battleViewModel.state.value.victoryProcessed && storyViewModel.selectedStoryArc != null) {
                                    storyViewModel.incrementEventIndex()
                                }
                                mainViewModel.updateStoryArc(storyViewModel.selectedStoryArc)
                                mainViewModel.updateStoryEventIndex(storyViewModel.currentEventIndex)
                                mainViewModel.updateStoryEnemyName(storyViewModel.storyEnemyName)
                                mainViewModel.updatePreviousLevel(storyViewModel.previousLevelForCompletion)
                                mainViewModel.onBattleLeave(newData, battleViewModel.state.value.victoryProcessed) 
                            }
                        )
                    }

                    Screen.ArcCompletion -> {
                        mainViewModel.selectedStoryArc?.let { arc ->
                            ArcCompletionScreen(
                                arc = arc,
                                nextArc = run {
                                    val database = when {
                                        arc.id >= 900 -> com.example.lumeria.database.SecretBossDatabase.secretArcs
                                        arc.id >= 100 -> com.example.lumeria.database.SideStoryDatabase.allArcs
                                        else -> com.example.lumeria.database.StoryDatabase.arcs
                                    }
                                    database.find { it.id == arc.id + 1 }
                                },
                                levelsGained = mainViewModel.levelsGainedOnCompletion,
                                newLevel = playerData.level,
                                onContinue = { 
                                    storyViewModel.resetStory()
                                    mainViewModel.onArcCompletionContinue() 
                                },
                            )
                        }
                    }

                    Screen.Defeated -> {
                        DefeatedScreen(goldLost = mainViewModel.goldLostOnDeath) {
                            mainViewModel.onDeathReturn()
                        }
                    }

                    Screen.Shop -> ShopScreen(
                        playerData = playerData,
                        onPlayerUpdate = { newData -> mainViewModel.updatePlayer(newData) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.FamiliarStore -> FamiliarStoreScreen(
                        playerData = playerData,
                        onPlayerUpdate = { newData -> mainViewModel.updatePlayer(newData) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Codex -> LumeriaCodexScreen(
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Inventory -> InventoryScreen(
                        playerData = playerData,
                        onPlayerUpdate = { newData -> mainViewModel.updatePlayer(newData) }
                    ) { mainViewModel.popBackstack() }

                    Screen.Quests -> QuestScreen(
                        playerData = playerData,
                        onPlayerUpdate = { newData -> mainViewModel.updatePlayer(newData) }
                    ) { mainViewModel.popBackstack() }

                    Screen.Stats -> StatsScreen(
                        playerData = playerData,
                        onPlayerUpdate = { newData -> mainViewModel.updatePlayer(newData) }
                    ) { mainViewModel.popBackstack() }

                    Screen.Skills -> SkillScreen(
                        playerData = playerData,
                        onPlayerUpdate = { newData -> mainViewModel.updatePlayer(newData) }
                    ) { mainViewModel.popBackstack() }

                    Screen.WorldMap -> WorldMapScreen(
                        playerData = playerData,
                        onTravel = { locationName -> 
                            if (locationName == "Elder's Hut") {
                                mainViewModel.navigateTo(Screen.ElderRitual)
                            } else {
                                adventureViewModel.travel(
                                    locationName = locationName,
                                    playerData = playerData,
                                    onUpdatePlayer = { mainViewModel.updatePlayer(it) },
                                    onNavigateToBattle = { isRift, newData -> mainViewModel.navigateToBattle(isRift, snapshot = newData) },
                                    onNavigateToEvent = { mainViewModel.navigateTo(Screen.WorldEvent) }
                                )
                            }
                        },
                        onBossBattle = { locationName -> mainViewModel.onBossBattle(locationName) }
                    ) { mainViewModel.popBackstack() }

                    Screen.ElderRitual -> ElderRitualScreen(
                        playerData = playerData,
                        onPlayerUpdate = { newData -> mainViewModel.updatePlayer(newData) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Inn -> InnScreen(
                        playerData = playerData,
                        onPlayerUpdate = { newData -> mainViewModel.updatePlayer(newData) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.GamblingHouse -> GamblingHouseScreen(
                        playerData = playerData,
                        onPlayerUpdate = { newData -> mainViewModel.updatePlayer(newData) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.StorySelection -> StorySelectionScreen(
                        playerData = playerData,
                        onSelectArc = { arc -> 
                            storyViewModel.selectArc(arc)
                            mainViewModel.updateStoryArc(arc)
                            mainViewModel.updateStoryEventIndex(0)
                            mainViewModel.navigateTo(Screen.StoryDialogue)
                        },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.StoryJournal -> StoryJournalScreen(
                        playerData = playerData,
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.StoryDialogue -> {
                        storyViewModel.selectedStoryArc?.let { arc ->
                            StoryDialogueScreen(
                                arc = arc,
                                eventIndex = storyViewModel.currentEventIndex,
                                onNext = { 
                                    storyViewModel.nextEvent(
                                        playerData = playerData,
                                        onNavigateToCompletion = { mainViewModel.navigateTo(Screen.ArcCompletion) },
                                        onBattleStart = { enemy, isBoss ->
                                            storyViewModel.setStoryBattle(enemy, isBoss)
                                            mainViewModel.updateStoryEnemyName(enemy)
                                            mainViewModel.navigateToBattle(isRift = false, enemyName = enemy, isBoss = isBoss, snapshot = playerData, location = arc.locationName)
                                        }
                                    )
                                    mainViewModel.updateStoryEventIndex(storyViewModel.currentEventIndex)
                                },
                                onChoiceSelected = { option -> 
                                    storyViewModel.onChoiceSelected(
                                        option = option,
                                        playerData = playerData,
                                        onUpdatePlayer = { mainViewModel.updatePlayer(it) },
                                        onNavigateToCompletion = { mainViewModel.navigateTo(Screen.ArcCompletion) }
                                    )
                                    mainViewModel.updateStoryEventIndex(storyViewModel.currentEventIndex)
                                },
                                onBattleStart = { enemyName, isBoss -> 
                                    storyViewModel.setStoryBattle(enemyName, isBoss)
                                    mainViewModel.updateStoryEnemyName(enemyName)
                                    mainViewModel.navigateToBattle(isRift = false, enemyName = enemyName, isBoss = isBoss, snapshot = playerData, location = arc.locationName)
                                },
                                onReturn = { 
                                    storyViewModel.resetStory()
                                    mainViewModel.resetStorySelection() 
                                }
                            )
                        }
                    }

                    Screen.Bestiary -> BestiaryScreen(
                        playerData = playerData,
                        onBack = { mainViewModel.popBackstack() }
                    )

                    Screen.ArenaSelection -> ArenaSelectionScreen(
                        playerData = playerData,
                        onStartBattle = { opponent -> mainViewModel.onArenaBattleStart(opponent) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Blacksmith -> BlacksmithScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Bank -> BankScreen(
                        playerData = playerData,
                        onDeposit = { amount -> economyViewModel.deposit(amount, playerData, { newData -> mainViewModel.updatePlayer(newData) }) },
                        onWithdraw = { amount -> economyViewModel.withdraw(amount, playerData, { newData -> mainViewModel.updatePlayer(newData) }) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.BountyBoard -> BountyBoardScreen(
                        playerData = playerData,
                        onAcceptBounty = { mainViewModel.onAcceptBounty(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Kingdom -> KingdomScreen(
                        playerData = playerData,
                        onUpgradeNation = { kingdomViewModel.upgradeNation(playerData, { newData -> mainViewModel.updatePlayer(newData) }) },
                        onClaimReward = { rankId -> kingdomViewModel.claimRankReward(rankId, playerData, { newData -> mainViewModel.updatePlayer(newData) }) },
                        onProclamations = { mainViewModel.navigateTo(Screen.KingdomLaws) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.KingdomLaws -> KingdomLawsScreen(
                        playerData = playerData,
                        onSelectLaw = { lawId -> kingdomViewModel.selectKingdomLaw(lawId, playerData, { newData -> mainViewModel.updatePlayer(newData) }) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Guild -> GuildScreen(
                        playerData = playerData,
                        onJoinGuild = { guildName -> kingdomViewModel.joinGuild(guildName, playerData, { newData -> mainViewModel.updatePlayer(newData) }, { mainViewModel.navigateTo(Screen.GuildIntro) }) },
                        onLearnSkill = { skillName, fee -> kingdomViewModel.learnGuildSkill(skillName, fee, playerData, { newData -> mainViewModel.updatePlayer(newData) }) },
                        onStartExam = { mainViewModel.onStartGuildExam(it) },
                        onShowLore = { mainViewModel.navigateTo(Screen.GuildIntro) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Settings -> {
                        val settings by mainViewModel.settings.collectAsState()
                        SettingsScreen(
                            settings = settings,
                            onSettingsChanged = { mainViewModel.updateSettings(it) },
                            onEnterCode = { code, id -> mainViewModel.onEnterCode(code, id) },
                            onExportSave = { mainViewModel.exportPlayerData() },
                            onImportSave = { mainViewModel.importPlayerData(it) },
                            onSyncCloudSave = { mainViewModel.syncCloudSave(it) },
                            onShowDonationDialog = { showDonationDialog = true },
                            onReturn = { mainViewModel.popBackstack() }
                        )
                    }

                    Screen.WorldEvent -> {
                        adventureViewModel.currentWorldEvent?.let { event ->
                            WorldEventScreen(
                                event = event,
                                outcomeMessage = adventureViewModel.eventOutcomeMessage,
                                onOptionSelected = { outcome -> 
                                    adventureViewModel.processEventOutcome(
                                        outcome = outcome,
                                        playerData = playerData,
                                        onUpdatePlayer = { mainViewModel.updatePlayer(it) },
                                        onNavigateToBattle = { enemy -> mainViewModel.navigateToBattle(isRift = false, enemyName = enemy, snapshot = playerData) }
                                    )
                                },
                                onContinue = { 
                                    adventureViewModel.completeEvent()
                                    mainViewModel.onWorldEventComplete() 
                                }
                            )
                        }
                    }

                    Screen.Tower -> TowerScreen(
                        playerData = playerData,
                        onEnterFloor = { mainViewModel.onTowerBattleStart() },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.TrophyRoom -> TrophyRoomScreen(
                        playerData = playerData,
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Fishing -> FishingScreen(
                        playerData = playerData,
                        onFishCaught = { mainViewModel.onFishCaught(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.GuildIntro -> GuildIntroScreen(
                        guildName = playerData.joinedGuild ?: "",
                        onContinue = { mainViewModel.navigateTo(Screen.Guild) }
                    )

                    Screen.BillyIntro -> LocationIntroScreen(
                        title = "Billy's General Store",
                        history = "Established by the legendary merchant Billy during the Great Scarcity, this store has become the heartbeat of Lumeria's economy. Billy claims to have once sold a wooden sword to a god, and looking at his prices, you might just believe him. He is energetic, eccentric, and possesses a supply chain that defies common logic.",
                        backgroundId = R.drawable.billys_store_intro,
                        onContinue = { mainViewModel.onCompleteBillyIntro() }
                    )

                    Screen.InnIntro -> LocationIntroScreen(
                        title = "Yumi's Inn",
                        history = "Yumi's Inn has stood for generations as a sanctuary for weary travelers. Yumi, the current proprietor, treats every guest with a kindness that is rare in a world plagued by the void. It is said that no one has ever left her inn with an empty stomach or a heavy heart. For Xious, this is a place to find the strength for the journey ahead.",
                        backgroundId = R.drawable.inn_scene_first_visit,
                        onContinue = { mainViewModel.onCompleteInnIntro() }
                    )

                    Screen.ForgeIntro -> LocationIntroScreen(
                        title = "The Royal Forge",
                        history = "The Royal Forge is the birthplace of Lumeria's finest weaponry. Kazufi, the master smith, has spent her life perfecting the art of enchanting steel. She is as uncompromising as the metal she works with, believing that a warrior's strength is only as good as the blade they carry. Respect her forge, and she will make your equipment legends of their own.",
                        backgroundId = R.drawable.forge_intro_bg,
                        onContinue = { mainViewModel.onCompleteForgeIntro() }
                    )
                }
            }
        }
        if (showDonationDialog) {
            val products by billingViewModel.products.collectAsState()
            DonationDialog(
                products = products,
                onPurchase = { activity, product -> billingViewModel.makePurchase(activity, product) },
                onDismiss = { showDonationDialog = false }
            )
        }
    }
}
