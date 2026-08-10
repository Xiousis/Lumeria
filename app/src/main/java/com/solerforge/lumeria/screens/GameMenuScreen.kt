package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.utils.MusicManager
import com.solerforge.lumeria.components.RpgButton

@Composable
fun GameMenuScreen(
    onBattle: () -> Unit,
    onStory: () -> Unit,
    onSkills: () -> Unit,
    onShop: () -> Unit,
    onInventory: () -> Unit,
    onQuests: () -> Unit,
    onStats: () -> Unit,
    onElder: () -> Unit,
    onInn: () -> Unit,
    onGambling: () -> Unit,
    onJournal: () -> Unit,
    onBestiary: () -> Unit,
    onArena: () -> Unit,
    onBlacksmith: () -> Unit,
    onBank: () -> Unit,
    onBounty: () -> Unit,
    onKingdom: () -> Unit,
    onGuild: () -> Unit,
    onTower: () -> Unit,
    onTrophyRoom: () -> Unit,
    onCodex: () -> Unit,
    onFishing: () -> Unit,
    onFamiliarStore: () -> Unit,
    onWorldChat: () -> Unit,
    onSettings: () -> Unit,
    onRebirth: () -> Unit,
    onRaid: () -> Unit,
    onLeaderboard: () -> Unit,
    onReturnToMain: () -> Unit,
    playerData: PlayerData,
    towerUnlockMessage: String? = null,
    onDismissTowerUnlock: () -> Unit = {}
) {
    val context = LocalContext.current
    val race = com.solerforge.lumeria.database.RaceDatabase.getRace(playerData.playerRace)
    val isMonster = playerData.isReborn && race.isMonster
    
    val primaryColor = if (isMonster) Color.Red else Color(0xFFFFD700)
    val accentColor = if (isMonster) Color(0xFFA855F7) else Color.Cyan

    LaunchedEffect(playerData.isReborn) {
        val theme = if (playerData.isReborn) R.raw.echoes_of_lumeria_title else R.raw.lumeria_main_menu_theme
        MusicManager.playMusic(context, theme)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = if (playerData.isReborn) R.drawable.hall_of_heroes_bg else R.drawable.menu_background),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Dark overlay
        Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)))

        if (playerData.isReborn) {
            MmoLayout(
                onBattle = onBattle,
                onStory = onStory,
                onSkills = onSkills,
                onShop = onShop,
                onInventory = onInventory,
                onQuests = onQuests,
                onStats = onStats,
                onElder = onElder,
                onInn = onInn,
                onGambling = onGambling,
                onJournal = onJournal,
                onBestiary = onBestiary,
                onArena = onArena,
                onBlacksmith = onBlacksmith,
                onBank = onBank,
                onBounty = onBounty,
                onKingdom = onKingdom,
                onGuild = onGuild,
                onTower = onTower,
                onTrophyRoom = onTrophyRoom,
                onCodex = onCodex,
                onFishing = onFishing,
                onFamiliarStore = onFamiliarStore,
                onWorldChat = onWorldChat,
                onSettings = onSettings,
                onRebirth = onRebirth,
                onRaid = onRaid,
                onLeaderboard = onLeaderboard,
                onReturnToMain = onReturnToMain,
                playerData = playerData,
                race = race,
                isMonster = isMonster,
                primaryColor = primaryColor,
                accentColor = accentColor
            )
        } else {
            ClassicLayout(
                onBattle = onBattle,
                onStory = onStory,
                onSkills = onSkills,
                onShop = onShop,
                onInventory = onInventory,
                onQuests = onQuests,
                onStats = onStats,
                onElder = onElder,
                onInn = onInn,
                onGambling = onGambling,
                onJournal = onJournal,
                onBestiary = onBestiary,
                onArena = onArena,
                onBlacksmith = onBlacksmith,
                onBank = onBank,
                onBounty = onBounty,
                onKingdom = onKingdom,
                onGuild = onGuild,
                onTower = onTower,
                onTrophyRoom = onTrophyRoom,
                onCodex = onCodex,
                onFishing = onFishing,
                onFamiliarStore = onFamiliarStore,
                onWorldChat = onWorldChat,
                onSettings = onSettings,
                onRebirth = onRebirth,
                onRaid = onRaid,
                onLeaderboard = onLeaderboard,
                onReturnToMain = onReturnToMain,
                playerData = playerData
            )
        }

        // TOWER UNLOCK DIALOG
        if (towerUnlockMessage != null) {
            androidx.compose.material3.AlertDialog(
                onDismissRequest = onDismissTowerUnlock,
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = onDismissTowerUnlock) {
                        Text("EXCELLENT", color = Color.Cyan)
                    }
                },
                title = { Text("TOWER UNLOCKED", fontWeight = FontWeight.Black, color = Color.Yellow) },
                text = { Text(towerUnlockMessage, color = Color.White) },
                containerColor = Color(0xFF1E293B),
                textContentColor = Color.White
            )
        }
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ClassicLayout(
    onBattle: () -> Unit,
    onStory: () -> Unit,
    onSkills: () -> Unit,
    onShop: () -> Unit,
    onInventory: () -> Unit,
    onQuests: () -> Unit,
    onStats: () -> Unit,
    onElder: () -> Unit,
    onInn: () -> Unit,
    onGambling: () -> Unit,
    onJournal: () -> Unit,
    onBestiary: () -> Unit,
    onArena: () -> Unit,
    onBlacksmith: () -> Unit,
    onBank: () -> Unit,
    onBounty: () -> Unit,
    onKingdom: () -> Unit,
    onGuild: () -> Unit,
    onTower: () -> Unit,
    onTrophyRoom: () -> Unit,
    onCodex: () -> Unit,
    onFishing: () -> Unit,
    onFamiliarStore: () -> Unit,
    onWorldChat: () -> Unit,
    onSettings: () -> Unit,
    onRebirth: () -> Unit,
    onRaid: () -> Unit,
    onLeaderboard: () -> Unit,
    onReturnToMain: () -> Unit,
    playerData: PlayerData
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.lumeria_main_menu_font),
            contentDescription = "Lumeria",
            modifier = Modifier.height(80.dp).padding(vertical = 16.dp),
            contentScale = ContentScale.Fit
        )

        Spacer(modifier = Modifier.height(8.dp))

        // ADVENTURE CATEGORY
        CategorySection(R.drawable.adventure_font) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                maxItemsInEachRow = 2
            ) {
                ClassicMenuButton("World Map", onBattle, Modifier.weight(1f))
                ClassicMenuButton("Story Mode", onStory, Modifier.weight(1f))
                ClassicMenuButton("Quests", onQuests, Modifier.weight(1f))
                ClassicMenuButton("Bounties", onBounty, Modifier.weight(1f))
                ClassicMenuButton("Grand Arena", onArena, Modifier.weight(1f))
                if (com.solerforge.lumeria.database.TowerDatabase.isTowerUnlocked(playerData.defeatedBosses)) {
                    ClassicMenuButton("Battle Tower", onTower, Modifier.weight(1f))
                }
                ClassicMenuButton("Raid", onRaid, Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // TOWN SQUARE CATEGORY
        CategorySection(R.drawable.town_square_font) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                maxItemsInEachRow = 2
            ) {
                ClassicMenuButton("Shop", onShop, Modifier.weight(1f))
                ClassicMenuButton("Blacksmith", onBlacksmith, Modifier.weight(1f))
                ClassicMenuButton("Bank", onBank, Modifier.weight(1f))
                ClassicMenuButton("Inn", onInn, Modifier.weight(1f))
                ClassicMenuButton("The Elder", onElder, Modifier.weight(1f))
                ClassicMenuButton("Gambling", onGambling, Modifier.weight(1f))
                ClassicMenuButton("Fishing", onFishing, Modifier.weight(1f))
                ClassicMenuButton("Familiar Store", onFamiliarStore, Modifier.weight(1f))
                ClassicMenuButton("Kingdom", onKingdom, Modifier.weight(1f))
                ClassicMenuButton("Guild", onGuild, Modifier.weight(1f))
                ClassicMenuButton("World Chat", onWorldChat, Modifier.weight(1f))
                ClassicMenuButton("Leaderboard", onLeaderboard, Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // PLAYER CATEGORY
        CategorySection(R.drawable.player_font) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                maxItemsInEachRow = 2
            ) {
                ClassicMenuButton("Stats", onStats, Modifier.weight(1f))
                ClassicMenuButton("Inventory", onInventory, Modifier.weight(1f))
                ClassicMenuButton("Skill Book", onSkills, Modifier.weight(1f))
                ClassicMenuButton("Trophy Room", onTrophyRoom, Modifier.weight(1f))
                ClassicMenuButton("Journal", onJournal, Modifier.weight(1f))
                ClassicMenuButton("Bestiary", onBestiary, Modifier.weight(1f))
                ClassicMenuButton("Codex", onCodex, Modifier.weight(1f))
                if (playerData.level >= 100) {
                    ClassicMenuButton("Rebirth Altar", onRebirth, Modifier.weight(1f), Color.Yellow)
                }
                ClassicMenuButton("Options", onSettings, Modifier.weight(1f))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        
        Text(
            text = "Return to Title",
            color = Color.Cyan,
            modifier = Modifier.clickable { onReturnToMain() }.padding(16.dp),
            fontWeight = FontWeight.Bold
        )
        
        Spacer(modifier = Modifier.height(32.dp))
    }
}

@Composable
fun CategorySection(imageRes: Int, content: @Composable () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
        Image(
            painter = painterResource(id = imageRes),
            contentDescription = null,
            modifier = Modifier.height(50.dp).padding(vertical = 8.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.height(8.dp))
        content()
    }
}

@Composable
fun ClassicMenuButton(label: String, onClick: () -> Unit, modifier: Modifier = Modifier, color: Color = Color.White) {
    RpgButton(
        text = label,
        onClick = onClick,
        modifier = modifier.padding(4.dp).height(50.dp),
        contentColor = color
    )
}

@Composable
fun MmoLayout(
    onBattle: () -> Unit,
    onStory: () -> Unit,
    onSkills: () -> Unit,
    onShop: () -> Unit,
    onInventory: () -> Unit,
    onQuests: () -> Unit,
    onStats: () -> Unit,
    onElder: () -> Unit,
    onInn: () -> Unit,
    onGambling: () -> Unit,
    onJournal: () -> Unit,
    onBestiary: () -> Unit,
    onArena: () -> Unit,
    onBlacksmith: () -> Unit,
    onBank: () -> Unit,
    onBounty: () -> Unit,
    onKingdom: () -> Unit,
    onGuild: () -> Unit,
    onTower: () -> Unit,
    onTrophyRoom: () -> Unit,
    onCodex: () -> Unit,
    onFishing: () -> Unit,
    onFamiliarStore: () -> Unit,
    onWorldChat: () -> Unit,
    onSettings: () -> Unit,
    onRebirth: () -> Unit,
    onRaid: () -> Unit,
    onLeaderboard: () -> Unit,
    onReturnToMain: () -> Unit,
    playerData: PlayerData,
    race: com.solerforge.lumeria.models.Race,
    isMonster: Boolean,
    primaryColor: Color,
    accentColor: Color
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .windowInsetsPadding(WindowInsets.statusBars)
    ) {
        // TOP STATUS BAR
        TopDashboard(playerData, primaryColor, accentColor, onSettings)

        Row(modifier = Modifier.fillMaxSize()) {
            // LEFT SIDEBAR (NAVIGATION)
            Column(
                modifier = Modifier
                    .width(140.dp)
                    .fillMaxHeight()
                    .background(Color.Black.copy(alpha = 0.4f))
                    .verticalScroll(rememberScrollState())
                    .padding(vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                NavHeader("COMBAT", accentColor)
                NavLink("World Map", onBattle, primaryColor)
                NavLink("Story Mode", onStory, primaryColor)
                NavLink("Quests", onQuests, primaryColor)
                NavLink("Bounties", onBounty, primaryColor)
                NavLink("Grand Arena", onArena, primaryColor)
                if (com.solerforge.lumeria.database.TowerDatabase.isTowerUnlocked(playerData.defeatedBosses)) {
                    NavLink("Battle Tower", onTower, primaryColor)
                }
                NavLink("Raid", onRaid, primaryColor)

                Spacer(modifier = Modifier.height(16.dp))
                NavHeader("CHARACTER", accentColor)
                NavLink("Stats", onStats, primaryColor)
                NavLink("Inventory", onInventory, primaryColor)
                NavLink("Skill Book", onSkills, primaryColor)
                NavLink("Trophy Room", onTrophyRoom, primaryColor)
                NavLink("Journal", onJournal, primaryColor)
                NavLink("Bestiary", onBestiary, primaryColor)
                NavLink("Codex", onCodex, primaryColor)
                if (!playerData.isReborn && playerData.level >= 100) {
                    NavLink("Rebirth Altar", onRebirth, primaryColor)
                }

                Spacer(modifier = Modifier.height(16.dp))
                NavHeader("TOWN", accentColor)
                NavLink("Shop", onShop, primaryColor)
                NavLink("Blacksmith", onBlacksmith, primaryColor)
                NavLink("Bank", onBank, primaryColor)
                NavLink("Inn", onInn, primaryColor)
                NavLink("The Elder", onElder, primaryColor)
                NavLink("Gambling", onGambling, primaryColor)
                NavLink("Fishing", onFishing, primaryColor)
                NavLink("Familiar Store", onFamiliarStore, primaryColor)

                Spacer(modifier = Modifier.height(16.dp))
                NavHeader("SOCIAL", accentColor)
                NavLink("Kingdom", onKingdom, primaryColor)
                NavLink("Guild", onGuild, primaryColor)
                NavLink("World Chat", onWorldChat, primaryColor)
                NavLink("Leaderboard", onLeaderboard, primaryColor)

                Spacer(modifier = Modifier.height(24.dp))
                NavLink("Title Screen", onReturnToMain, Color.Gray)
                Spacer(modifier = Modifier.height(32.dp))
            }

            // MAIN CONTENT AREA (DASHBOARD)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxHeight()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                DashboardCentral(playerData, race, isMonster, primaryColor, accentColor)
            }
        }
    }
}

@Composable
fun TopDashboard(playerData: PlayerData, primaryColor: Color, accentColor: Color, onSettings: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(60.dp)
            .background(Brush.verticalGradient(listOf(Color.Black, Color.Transparent)))
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(
                text = playerData.playerName.uppercase(),
                color = Color.White,
                fontWeight = FontWeight.Black,
                fontSize = 18.sp
            )
            Text(
                text = "Level ${playerData.level} ${playerData.playerClass}",
                color = primaryColor,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            StatIndicator("💰", playerData.gold.toString(), Color.Yellow)
            Spacer(modifier = Modifier.width(16.dp))
            StatIndicator("✨", playerData.renown.toString(), accentColor)
            
            IconButton(onClick = onSettings) {
                Image(
                    painter = painterResource(id = R.drawable.settings_button_icon),
                    contentDescription = "Settings",
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun StatIndicator(icon: String, value: String, color: Color) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Text(icon, fontSize = 16.sp)
        Spacer(modifier = Modifier.width(4.dp))
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 14.sp)
    }
}

@Composable
fun NavHeader(title: String, color: Color) {
    Text(
        text = title,
        color = color,
        fontWeight = FontWeight.Black,
        fontSize = 10.sp,
        letterSpacing = 1.sp,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
fun NavLink(label: String, onClick: () -> Unit, color: Color) {
    Text(
        text = label,
        color = color,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        textAlign = TextAlign.Center
    )
}

@Composable
fun DashboardCentral(
    playerData: PlayerData,
    race: com.solerforge.lumeria.models.Race,
    isMonster: Boolean,
    primaryColor: Color,
    accentColor: Color
) {
    // Vitals Section
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.6f))
            .border(1.dp, Color.DarkGray, RoundedCornerShape(8.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            val portraitId = if (isMonster) {
                R.drawable. hall_of_heroes_bg // Placeholder for monster portrait
            } else {
                when {
                    playerData.hp.toFloat() / playerData.maxHp <= 0.20f -> R.drawable.xious_hp_20
                    playerData.hp.toFloat() / playerData.maxHp <= 0.50f -> R.drawable.xious_hp_50
                    playerData.hp.toFloat() / playerData.maxHp <= 0.75f -> R.drawable.xious_hp_75
                    else -> R.drawable.xious_hp_100
                }
            }
            
            Image(
                painter = painterResource(id = portraitId),
                contentDescription = "Portrait",
                modifier = Modifier.size(80.dp).clip(RoundedCornerShape(8.dp)),
                contentScale = ContentScale.Crop
            )
            
            Spacer(modifier = Modifier.width(16.dp))
            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = if (isMonster) "★ DARK OVERLORD ★" else "★ LEGENDARY HERO ★",
                    color = if (isMonster) Color.Red else Color(0xFFFFD600),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = race.name.uppercase(),
                    color = race.rarity.color,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                com.solerforge.lumeria.components.HpBar(playerData.hp, playerData.maxHp)
                Spacer(modifier = Modifier.height(4.dp))
                com.solerforge.lumeria.components.ManaBar(playerData.mana, playerData.maxMana)
            }
        }
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // XP Progress
        val nextLevelXp = com.solerforge.lumeria.models.XiousStats.getRequiredXp(playerData.level)
        val xpProgress = (playerData.xp.toFloat() / nextLevelXp).coerceIn(0f, 1f)
        Column {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Experience", color = Color.Gray, fontSize = 10.sp)
                Text("${(xpProgress * 100).toInt()}%", color = Color.Gray, fontSize = 10.sp)
            }
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .background(Color.DarkGray, RoundedCornerShape(2.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(xpProgress)
                        .fillMaxHeight()
                        .background(primaryColor, RoundedCornerShape(2.dp))
                )
            }
        }
    }
    
    Spacer(modifier = Modifier.height(16.dp))

    // World Status
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(Color.Black.copy(alpha = 0.4f))
            .padding(12.dp)
    ) {
        Text("CURRENT LOCATION", color = accentColor, fontWeight = FontWeight.Black, fontSize = 10.sp)
        Text(playerData.currentLocation.uppercase(), color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(12.dp))
        
        if (isMonster) {
            DashboardMetric("Villages Plundered", playerData.villagesPlundered.toString(), Color.Red)
            DashboardMetric("Souls Harvested", playerData.soulsHarvested.toString(), Color.Magenta)
            DashboardMetric("Heroes Slain", playerData.heroPartiesDefeated.toString(), Color.Red)
        } else {
            DashboardMetric("Quests Active", playerData.quests.size.toString(), Color.Cyan)
            DashboardMetric("Bounties Claimed", playerData.completedBountyIds.size.toString(), Color.Yellow)
            DashboardMetric("Bosses Defeated", playerData.defeatedBosses.size.toString(), Color.Green)
        }
    }
}

@Composable
fun DashboardMetric(label: String, value: String, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = Color.LightGray, fontSize = 12.sp)
        Text(value, color = color, fontWeight = FontWeight.Bold, fontSize = 12.sp)
    }
}
