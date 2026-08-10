package com.solerforge.lumeria.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.MainViewModel
import com.solerforge.lumeria.R
import com.solerforge.lumeria.battle.BattleScreen
import com.solerforge.lumeria.battle.BattleViewModel
import com.solerforge.lumeria.models.Screen
import com.solerforge.lumeria.screens.*
import com.solerforge.lumeria.viewmodels.*
import androidx.compose.ui.platform.LocalContext
import com.solerforge.lumeria.components.DonationDialog
import android.annotation.SuppressLint

@Composable
fun AppNavigation(
    mainViewModel: MainViewModel,
    kingdomViewModel: KingdomViewModel,
    economyViewModel: EconomyViewModel,
    adventureViewModel: AdventureViewModel,
    billingViewModel: BillingViewModel,
    guildViewModel: GuildViewModel,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(0.dp)
) {
    val playerData by mainViewModel.playerData.collectAsState()
    val slot1Data by mainViewModel.slot1Data.collectAsState()
    val slot2Data by mainViewModel.slot2Data.collectAsState()
    val slot3Data by mainViewModel.slot3Data.collectAsState()
    var showDonationDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    @SuppressLint("HardwareIds")
    LaunchedEffect(Unit) {
        val deviceId = android.provider.Settings.Secure.getString(context.contentResolver, android.provider.Settings.Secure.ANDROID_ID)
        if (deviceId != null) {
            mainViewModel.setDeviceId(deviceId)
        }
    }

    Box(modifier = modifier.padding(contentPadding)) {
        AnimatedContent(
            targetState = mainViewModel.currentScreen,
            transitionSpec = {
                fadeIn(animationSpec = tween(400)) togetherWith
                        fadeOut(animationSpec = tween(400))
            },
            label = "ScreenTransition"
        ) { targetScreen ->
            Box(modifier = Modifier.fillMaxSize()) {
                when (targetScreen) {
                    Screen.Title -> TitleScreen(
                        isReborn = playerData.isReborn,
                        onStart = { mainViewModel.navigateTo(Screen.MainMenu) },
                        onSettings = { mainViewModel.navigateTo(Screen.Settings) }
                    )

                    Screen.MainMenu -> MainMenu(
                        activePlayerData = playerData,
                        slot1Data = slot1Data,
                        slot2Data = slot2Data,
                        slot3Data = slot3Data,
                        activeSlot = mainViewModel.activeSlot,
                        onSelectSlot = { mainViewModel.selectSlot(it) },
                        onStartGame = { mainViewModel.onStartGame() },
                        onNewGame = { mainViewModel.onNewGame() },
                        onSettings = { mainViewModel.navigateTo(Screen.Settings) }
                    )

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
                        onElder = { mainViewModel.navigateFactionAware(Screen.ElderRitual, Screen.TheAncientOne) },
                        onInn = { mainViewModel.onEnterInn() },
                        onGambling = { mainViewModel.navigateFactionAware(Screen.GamblingHouse, Screen.DenOfDeceit) },
                        onJournal = { mainViewModel.navigateTo(Screen.StoryJournal) },
                        onBestiary = { mainViewModel.navigateTo(Screen.Bestiary) },
                        onArena = { mainViewModel.navigateTo(Screen.ArenaSelection) },
                        onBlacksmith = { mainViewModel.onEnterForge() },
                        onBank = { mainViewModel.navigateFactionAware(Screen.Bank, Screen.TheHoard) },
                        onBounty = { mainViewModel.navigateTo(Screen.BountyBoard) },
                        onKingdom = { mainViewModel.navigateFactionAware(Screen.Kingdom, Screen.OverlordsThrone) },
                        onGuild = { mainViewModel.navigateFactionAware(Screen.Guild, Screen.TheSyndicate) },
                        onTower = { mainViewModel.navigateTo(Screen.Tower) },
                        onTrophyRoom = { mainViewModel.navigateTo(Screen.TrophyRoom) },
                        onCodex = { mainViewModel.navigateTo(Screen.Codex) },
                        onFishing = { mainViewModel.navigateFactionAware(Screen.Fishing, Screen.AbyssalLake) },
                        onFamiliarStore = { mainViewModel.navigateFactionAware(Screen.FamiliarStore, Screen.BeastPen) },
                        onWorldChat = { mainViewModel.navigateTo(Screen.WorldChat) },
                        onLeaderboard = { mainViewModel.navigateTo(Screen.Leaderboard) },
                        onSettings = { mainViewModel.navigateTo(Screen.Settings) },
                        onRebirth = { mainViewModel.navigateTo(Screen.RebirthRequirements) },
                        onRaid = { mainViewModel.navigateTo(Screen.Raid) },
                        selectedTabIndex = mainViewModel.gameMenuTabIndex,
                        onTabSelected = { mainViewModel.updateGameMenuTab(it) },
                        onReturnToMain = { mainViewModel.replaceWith(Screen.MainMenu) },
                        playerData = playerData,
                        towerUnlockMessage = mainViewModel.towerUnlockMessage,
                        onDismissTowerUnlock = { mainViewModel.dismissTowerUnlock() }
                    )

                    Screen.Battle -> {
                        val battleViewModel: BattleViewModel = androidx.lifecycle.viewmodel.compose.viewModel(
                            key = "battle_${mainViewModel.battleSeed}",
                            factory = BattleViewModel.provideFactory(
                                isBoss = mainViewModel.isBossBattle,
                                isPvP = mainViewModel.battleOrchestrator.isPvPBattle,
                                isTower = mainViewModel.isTowerBattle,
                                isRift = mainViewModel.isRiftBattle,
                                isStoryReplay = mainViewModel.battleOrchestrator.isStoryReplay,
                                storyEnemyName = mainViewModel.storyEnemyName,
                                arenaOpponent = mainViewModel.arenaOpponent,
                                shadowOpponent = mainViewModel.battleOrchestrator.shadowOpponent,
                                playerSnapshot = mainViewModel.grindingPlayerData ?: playerData,
                                activeBounty = mainViewModel.battleOrchestrator.grindingPlayerData
                                    ?.activeBountyId
                                    ?.let { com.solerforge.lumeria.database.BountyDatabase.getBounty(it) },
                                guildExam = mainViewModel.currentGuildExam,
                                currentEventIndex = mainViewModel.currentEventIndex,
                                locationName = mainViewModel.locationOverride
                            )
                        )

                        BattleScreen(
                            viewModel = battleViewModel,
                            onLeave = { finalData -> 
                                mainViewModel.onBattleLeave(finalData, true) 
                            },
                            onFlee = { finalData ->
                                mainViewModel.onBattleFlee(finalData)
                            },
                            onDeath = { snapshot ->
                                mainViewModel.onBattleDeath(snapshot)
                            },
                            onPlayerUpdate = { newData ->
                                mainViewModel.onPlayerBattleUpdate(newData)
                            },
                            onBattleAgain = { newData ->
                                mainViewModel.onBattleAgain(newData)
                            }
                        )
                    }

                    Screen.Defeated -> DefeatedScreen(
                        goldLost = mainViewModel.goldLostOnDeath,
                        onReturnToMenu = { mainViewModel.onDeathReturn() }
                    )

                    Screen.Inventory -> InventoryScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Stats -> StatsScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Shop -> ShopScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.BlackMarket -> ShopScreen(
                        playerData = playerData,
                        isMonsterMode = true,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Inn -> InnScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.TheLair -> InnScreen(
                        playerData = playerData,
                        isMonsterMode = true,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.ElderRitual -> ElderRitualScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.TheAncientOne -> ElderRitualScreen(
                        playerData = playerData,
                        isMonsterMode = true,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.GamblingHouse -> GamblingHouseScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.DenOfDeceit -> GamblingHouseScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Bank, Screen.TheHoard -> BankScreen(
                        playerData = playerData,
                        onDeposit = { amount -> economyViewModel.deposit(amount, playerData) { newData -> mainViewModel.updatePlayer(newData) } },
                        onWithdraw = { amount -> economyViewModel.withdraw(amount, playerData) { newData -> mainViewModel.updatePlayer(newData) } },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Kingdom, Screen.OverlordsThrone -> KingdomScreen(
                        playerData = playerData,
                        onUpgradeNation = { kingdomViewModel.upgradeNation(playerData) { newData -> mainViewModel.updatePlayer(newData) } },
                        onClaimReward = { rankId -> kingdomViewModel.claimRankReward(rankId, playerData) { newData -> mainViewModel.updatePlayer(newData) } },
                        onProclamations = { mainViewModel.navigateTo(Screen.KingdomLaws) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Guild, Screen.TheSyndicate -> GuildScreen(
                        playerData = playerData,
                        guildViewModel = guildViewModel,
                        deviceIdFlow = mainViewModel.deviceId,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onNavigateToIntro = { mainViewModel.navigateTo(Screen.GuildIntro) },
                        onStartExam = { exam -> mainViewModel.onStartGuildExam(exam) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Fishing, Screen.AbyssalLake -> FishingScreen(
                        playerData = playerData,
                        onFishCaught = { mainViewModel.onFishCaught(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.FamiliarStore, Screen.BeastPen -> FamiliarStoreScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.BlackMarketIntro -> LocationIntroScreen(
                        title = "The Black Market",
                        history = "In the dark corners of the new world, the Black Market thrives. Forbidden artifacts and soul-bound gear are traded here for gold and blood. Billy's reach does not extend to these shadows. Here, only the strong or the cunning survive the transaction.",
                        backgroundId = R.drawable.dark_citadel_bg,
                        onContinue = { mainViewModel.onCompleteBillyIntro() }
                    )

                    Screen.LairIntro -> LocationIntroScreen(
                        title = "The Lair",
                        history = "A sanctuary for those whom the light has forsaken. Deep within the earth, ancient ley lines converge to provide a place of rest and regeneration for monster-kind. It is silent, cold, and perfect for restoring your dark essence.",
                        backgroundId = R.drawable.lord_umbra,
                        onContinue = { mainViewModel.onCompleteInnIntro() }
                    )

                    Screen.ArmoryIntro -> LocationIntroScreen(
                        title = "The Armory",
                        history = "Where monsters go to sharpen their claws. The Armory uses primitive but effective techniques to reinforce natural armor and weaponize raw mana. Kazufi's forge is too refined for the requirements of a true beast.",
                        backgroundId = R.drawable.tower_final_floor_bg,
                        onContinue = { mainViewModel.onCompleteForgeIntro() }
                    )

                    Screen.Blacksmith, Screen.TheArmory -> BlacksmithScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Skills -> SkillScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.WorldMap -> WorldMapScreen(
                        playerData = playerData,
                        onTravel = { locationName -> 
                            adventureViewModel.travel(
                                locationName = locationName,
                                playerData = playerData,
                                onUpdatePlayer = { mainViewModel.updatePlayer(it) },
                                onNavigateToBattle = { isRift, data ->
                                    mainViewModel.navigateToBattle(isRift = isRift, enemyName = null, isBoss = false, snapshot = data, location = locationName)
                                },
                                onNavigateToEvent = { mainViewModel.navigateTo(Screen.WorldEvent) }
                            )
                        },
                        onBossBattle = { locationName ->
                            mainViewModel.onBossBattle(locationName)
                        },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.StorySelection -> StorySelectionScreen(
                        playerData = playerData,
                        onSelectArc = { arc -> mainViewModel.updateStoryArc(arc) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.ArcCompletion -> {
                        mainViewModel.selectedStoryArc?.let { arc ->
                            ArcCompletionScreen(
                                arc = arc,
                                nextArc = null,
                                levelsGained = mainViewModel.levelsGainedOnCompletion,
                                newLevel = playerData.level,
                                onContinue = { mainViewModel.onArcCompletionContinue() }
                            )
                        }
                    }

                    Screen.StoryJournal -> StoryJournalScreen(
                        playerData = playerData,
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.StoryDialogue -> {
                        mainViewModel.selectedStoryArc?.let { arc ->
                            StoryDialogueScreen(
                                arc = arc,
                                eventIndex = mainViewModel.currentEventIndex,
                                onNext = { mainViewModel.advanceStoryEvent() },
                                onChoiceSelected = { option -> 
                                    mainViewModel.onStoryChoiceSelected(option)
                                },
                                onBattleStart = { enemyName, isBoss -> 
                                    mainViewModel.updateStoryEnemyName(enemyName)
                                    mainViewModel.navigateToBattle(isRift = false, enemyName = enemyName, isBoss = isBoss, snapshot = playerData, location = arc.locationName)
                                },
                                onReturn = { 
                                    mainViewModel.resetStorySelection() 
                                }
                            )
                        }
                    }

                    Screen.Quests -> QuestScreen(
                        playerData = playerData,
                        onPlayerUpdate = { mainViewModel.updatePlayer(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Bestiary -> BestiaryScreen(
                        playerData = playerData,
                        onBack = { mainViewModel.popBackstack() }
                    )

                    Screen.ArenaSelection -> {
                        val shadows by mainViewModel.shadows.collectAsState()
                        LaunchedEffect(Unit) { mainViewModel.fetchShadows() }
                        
                        ArenaSelectionScreen(
                            playerData = playerData,
                            shadows = shadows,
                            onStartBattle = { opponent -> mainViewModel.onArenaBattleStart(opponent) },
                            onStartShadowBattle = { shadow -> 
                                mainViewModel.onShadowBattleStart(shadow)
                            },
                            onReturn = { mainViewModel.popBackstack() }
                        )
                    }

                    Screen.BountyBoard -> BountyBoardScreen(
                        playerData = playerData,
                        onAcceptBounty = { mainViewModel.onAcceptBounty(it) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.KingdomLaws -> KingdomLawsScreen(
                        playerData = playerData,
                        onSelectLaw = { lawId -> kingdomViewModel.selectKingdomLaw(lawId, playerData) { newData -> mainViewModel.updatePlayer(newData) } },
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
                                playerData = playerData,
                                outcomeMessage = adventureViewModel.eventOutcomeMessage,
                                onOptionSelected = { option -> 
                                    adventureViewModel.processEventOutcome(
                                        option = option, 
                                        playerData = playerData, 
                                        onUpdatePlayer = { mainViewModel.updatePlayer(it) }, 
                                        onNavigateToBattle = { enemyName, updated ->
                                            mainViewModel.navigateToBattle(isRift = false, enemyName = enemyName, isBoss = false, snapshot = updated)
                                        }
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

                    Screen.Codex -> LumeriaCodexScreen(
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.GuildIntro -> playerData.joinedGuild?.let { guildName ->
                        GuildIntroScreen(
                            guildName = guildName,
                            onContinue = { mainViewModel.popBackstack() }
                        )
                    }

                    Screen.BillyIntro -> LocationIntroScreen(
                        title = "Billy's General Store",
                        history = "Billy's General Store has been the cornerstone of Lumeria's commerce for over three decades. Billy himself is a veteran of the Great Void War, and though he no longer carries a blade, he ensures that every adventurer is equipped with the finest gear his gold can buy. 'A sharp blade is a long life,' he often says.",
                        backgroundId = R.drawable.billys_store_bg,
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

                    Screen.RebirthRequirements -> RebirthRequirementsScreen(
                        playerData = playerData,
                        onStartRebirth = { mainViewModel.navigateTo(Screen.RebirthSelection) },
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.RebirthSelection -> {
                        val nameAvailability by mainViewModel.nameAvailability.collectAsState()
                        val deviceId by mainViewModel.deviceId.collectAsState()
                        CharacterCreationScreen(
                            title = "FORGE A NEW LEGACY",
                            buttonText = "CONFIRM REBIRTH",
                            nameAvailability = nameAvailability,
                            isIdentityReady = deviceId != null,
                            rebirthError = mainViewModel.rebirthError,
                            onClearError = { mainViewModel.clearRebirthError() },
                            onCheckName = { mainViewModel.checkNameAvailability(it) },
                            onConfirm = { name, gender, className, raceName -> 
                                mainViewModel.onPerformRebirth(name, gender, className, raceName)
                            },
                            onCancel = { mainViewModel.popBackstack() }
                        )
                    }

                    Screen.RebirthIntro -> RebirthIntroScreen(
                        playerName = playerData.playerName,
                        onContinue = { mainViewModel.replaceWith(Screen.GameMenu) }
                    )

                    Screen.Raid -> RaidScreen(
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.WorldChat -> WorldChatScreen(
                        playerData = playerData,
                        onReturn = { mainViewModel.popBackstack() }
                    )

                    Screen.Leaderboard -> {
                        val globalRankings by mainViewModel.leaderboardGlobal.collectAsState()
                        val pvpRankings by mainViewModel.leaderboardPvP.collectAsState()
                        val isLeaderboardLoading by mainViewModel.isLeaderboardLoading.collectAsState()
                        LaunchedEffect(Unit) { mainViewModel.fetchLeaderboards() }

                        LeaderboardScreen(
                            globalRankings = globalRankings,
                            pvpRankings = pvpRankings,
                            isLoading = isLeaderboardLoading,
                            onReturn = { mainViewModel.popBackstack() }
                        )
                    }
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
