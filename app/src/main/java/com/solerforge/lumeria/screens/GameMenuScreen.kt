package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.utils.MusicManager

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
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onReturnToMain: () -> Unit,
    playerData: PlayerData,
    towerUnlockMessage: String? = null,
    onDismissTowerUnlock: () -> Unit = {}
) {
    val context = LocalContext.current
    val tabs = listOf("Adventure", "Player", "Town Square")

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

        // Vignette / Dark Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color.Black.copy(alpha = 0.4f),
                            Color.Black.copy(alpha = 0.8f),
                        ),
                    )
                ),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(top = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // Header Box to overlap Settings and Logo
            Box(
                modifier = Modifier.fillMaxWidth().height(160.dp), // Increased height for bigger logo
                contentAlignment = Alignment.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.lumeria_main_menu_font),
                    contentDescription = "Lumeria Logo",
                    modifier = Modifier.fillMaxSize().offset(y = (-10).dp).graphicsLayer(scaleX = 1.2f, scaleY = 1.2f),
                    contentScale = ContentScale.Fit
                )
                
                Box(
                    modifier = Modifier.fillMaxSize().padding(end = 24.dp, top = 8.dp),
                    contentAlignment = Alignment.TopEnd
                ) {
                    IconButton(onClick = onSettings, modifier = Modifier.size(80.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.settings_button_icon),
                            contentDescription = "Settings",
                            modifier = Modifier.fillMaxSize()
                        )
                    }
                }
            }
            
            // CUSTOM TAB ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp) // Shorter row for better fit
                    .padding(horizontal = 12.dp)
                    .offset(y = (-50).dp), // Pulled up even closer to logo
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                val tabImages = listOf(
                    R.drawable.adventure_font,
                    R.drawable.player_font,
                    R.drawable.town_square_font
                )
                
                tabs.forEachIndexed { index, title ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight()
                            .clickable(
                                interactionSource = remember { MutableInteractionSource() },
                                indication = null
                            ) { onTabSelected(index) },
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = tabImages[index]),
                            contentDescription = title,
                            modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer(
                                    alpha = if (selectedTabIndex == index) 1f else 0.4f,
                                    scaleX = 1.3f, // Refined scaling
                                    scaleY = 1.3f  
                                ),
                            contentScale = ContentScale.Fit
                        )
                        
                        // Custom Indicator
                        if (selectedTabIndex == index) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(0.5f)
                                    .height(4.dp)
                                    .offset(y = 2.dp)
                                    .background(Color(0xFFA855F7), RoundedCornerShape(2.dp))
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(0.dp))

            // TAB CONTENT
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
            ) {
                when (selectedTabIndex) {
                    0 -> AdventureTab(onBattle, onStory, onArena, onBounty, onTower, onQuests, onRaid, playerData)
                    1 -> PlayerTab(playerData, onInventory, onStats, onSkills, onJournal, onBestiary, onTrophyRoom, onCodex, onWorldChat, onLeaderboard)
                    2 -> TownSquareTab(onShop, onElder, onInn, onGambling, onBlacksmith, onBank, onKingdom, onGuild, onFishing, onFamiliarStore, onRebirth, playerData)
                }
            }

            // FOOTER ACTION
            Image(
                painter = painterResource(id = R.drawable.return_to_title_font),
                contentDescription = "Return to Title",
                modifier = Modifier
                    .padding(bottom = 24.dp)
                    .height(180.dp)
                    .clickable { onReturnToMain() },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun AdventureTab(
    onBattle: () -> Unit, 
    onStory: () -> Unit, 
    onArena: () -> Unit, 
    onBounty: () -> Unit, 
    onTower: () -> Unit,
    onQuests: () -> Unit,
    onRaid: () -> Unit,
    playerData: PlayerData
) {
    val unlocked = playerData.unlockedFeatures
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (playerData.isReborn) {
            MenuCard("DIMENSIONAL RIFTS", "Travel between realms and face otherworldly threats.", onBattle)
            MenuCard("CHRONICLES OF XIOUS", "Relive the legendary journey of the first hero.", onStory)
        } else {
            MenuCard("WORLD MAP", "Travel across Lumeria and clear regions.", onBattle, isLocked = !unlocked.contains("WORLD MAP"))
            MenuCard("STORY MODE", "Progress through the epic Rise of Xious.", onStory, isLocked = !unlocked.contains("STORY MODE"))
        }
        
        MenuCard("BOUNTY BOARD", "Hunt down dangerous outlaws for big rewards.", onBounty, isLocked = !unlocked.contains("BOUNTY BOARD"))
        MenuCard("GRAND ARENA", "Challenge the realm's strongest humans.", onArena, isLocked = !unlocked.contains("GRAND ARENA"))
        
        if (com.solerforge.lumeria.database.TowerDatabase.isTowerUnlocked(playerData.defeatedBosses)) {
            MenuCard("BATTLE TOWER", "Scale the 100 floors of ultimate trials.", onTower)
        }
        
        // RAID - Locked and Hidden until feature is ready
        if (false) {
            MenuCard("RAID", "Coming Soon - Cooperate with others for legendary loot.", onRaid, isLocked = true)
        }
        
        MenuCard("QUEST LOG", "Track your active objectives and rewards.", onQuests, isLocked = !unlocked.contains("QUEST LOG"))
    }
}

@Composable
fun PlayerTab(
    playerData: PlayerData, 
    onInventory: () -> Unit, 
    onStats: () -> Unit, 
    onSkills: () -> Unit, 
    onJournal: () -> Unit, 
    onBestiary: () -> Unit, 
    onTrophyRoom: () -> Unit,
    onCodex: () -> Unit,
    onWorldChat: () -> Unit,
    onLeaderboard: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (playerData.isReborn) {
            // MULTIPLAYER AVATAR CARD
            val race = com.solerforge.lumeria.database.RaceDatabase.getRace(playerData.playerRace)
            Box(
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(32.dp))
                    .background(
                        Brush.radialGradient(
                            colors = listOf(race.rarity.color.copy(alpha = 0.4f), Color.Black)
                        )
                    )
                    .border(4.dp, race.rarity.color, RoundedCornerShape(32.dp)),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = playerData.playerName.take(1).uppercase(),
                        color = Color.White,
                        style = MaterialTheme.typography.displayLarge,
                        fontWeight = FontWeight.Black
                    )
                    Text(
                        text = race.name.uppercase(),
                        color = race.rarity.color,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        } else {
            val portraitId = when {
                playerData.hp.toFloat() / playerData.maxHp <= 0.20f -> R.drawable.xious_hp_20
                playerData.hp.toFloat() / playerData.maxHp <= 0.50f -> R.drawable.xious_hp_50
                playerData.hp.toFloat() / playerData.maxHp <= 0.75f -> R.drawable.xious_hp_75
                else -> R.drawable.xious_hp_100
            }

            Image(
                painter = painterResource(id = portraitId),
                contentDescription = "Hero Portrait",
                modifier = Modifier
                    .size(260.dp)
                    .clip(RoundedCornerShape(32.dp)),
                contentScale = ContentScale.Fit
            )
        }
        
        Spacer(modifier = Modifier.height(8.dp))

        if (playerData.isReborn) {
            Text(
                text = "★ LEGENDARY HERO ★",
                color = Color(0xFFFFD600),
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Black,
                letterSpacing = 2.sp,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            MenuCard("HEROES' CIRCLE", "Communicate with fellow heroes and Legends.", onWorldChat, highlight = true)
            MenuCard("HALL OF LEGENDS", "View global player rankings and PvP records.", onLeaderboard, highlight = true)
        }
        
        MenuCard("LUMERIA CODEX", "Learn about attributes, combat, and world systems.", onCodex, highlight = true, isLocked = !playerData.unlockedFeatures.contains("LUMERIA CODEX"))
        MenuCard("HALL OF HEROES", "View your trophies, medals, and legendary feats.", onTrophyRoom, isLocked = !playerData.unlockedFeatures.contains("HALL OF HEROES"))
        MenuCard("INVENTORY", "Manage your gear and consumables.", onInventory, isLocked = !playerData.unlockedFeatures.contains("INVENTORY"))
        MenuCard("CHARACTER STATS", "Allocate points and review your power.", onStats, isLocked = !playerData.unlockedFeatures.contains("CHARACTER STATS"))
        MenuCard("SKILL BOOK", "Equip and mastery your combat abilities.", onSkills, isLocked = !playerData.unlockedFeatures.contains("SKILL BOOK"))
        MenuCard("BESTIARY", "Review known enemies and their secrets.", onBestiary, isLocked = !playerData.unlockedFeatures.contains("BESTIARY"))
        MenuCard("STORY JOURNAL", "Review your journey and achievements.", onJournal, isLocked = !playerData.unlockedFeatures.contains("STORY JOURNAL"))
        
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun TownSquareTab(
    onShop: () -> Unit,
    onElder: () -> Unit,
    onInn: () -> Unit,
    onGambling: () -> Unit,
    onBlacksmith: () -> Unit,
    onBank: () -> Unit,
    onKingdom: () -> Unit,
    onGuild: () -> Unit,
    onFishing: () -> Unit,
    onFamiliarStore: () -> Unit,
    onRebirth: () -> Unit,
    playerData: PlayerData,
) {
    val unlocked = playerData.unlockedFeatures
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        if (!playerData.isReborn) {
            MenuCard(
                title = "REBIRTH ALTAR",
                subtitle = "Transcend your mortal limits and forge a legacy.",
                onClick = onRebirth,
                highlight = true,
                isLocked = !unlocked.contains("REBIRTH ALTAR")
            )
        }

        MenuCard("THE ROYAL COURT", "Visit King Alaric and review your standing.", onKingdom, isLocked = !unlocked.contains("THE ROYAL COURT"))
        
        val guildUnlocked = playerData.renown >= 7500
        MenuCard(
            title = "THE GUILD",
            subtitle = if (guildUnlocked) "Join a House and learn elemental magic." else "Become Respected Adventurer to unlock.",
            onClick = { if (guildUnlocked) onGuild() },
            isLocked = !guildUnlocked || !unlocked.contains("THE GUILD")
        )

        MenuCard("FISHING POND", "Relax and catch some legendary fish.", onFishing, isLocked = !unlocked.contains("FISHING POND"))
        MenuCard("FAMILIAR NURSERY", "Find a loyal companion to aid you.", onFamiliarStore, isLocked = !unlocked.contains("FAMILIAR NURSERY"))
        MenuCard("IRONCLAD BANK", "Deposit gold to protect it from defeat penalties.", onBank, isLocked = !unlocked.contains("IRONCLAD BANK"))
        MenuCard("THE FORGE", "Upgrade your weapons and armor to +5.", onBlacksmith, isLocked = !unlocked.contains("THE FORGE"))
        MenuCard("BILLY'S STORE", "Buy new gear or sell your old equipment.", onShop, isLocked = !unlocked.contains("BILLY'S STORE"))
        MenuCard("THE ELDER", "Seek wisdom, respec stats, or make a wish.", onElder, isLocked = !unlocked.contains("THE ELDER"))
        
        MenuCard(
            title = "THE INN",
            subtitle = "Rest and recover your strength at Yumi's Inn.",
            onClick = onInn,
            isLocked = !unlocked.contains("THE INN")
        )

        MenuCard(
            title = "THE GAMBLING HOUSE",
            subtitle = "Try your luck at Blackjack and double your gold.",
            onClick = onGambling,
            isLocked = !unlocked.contains("THE GAMBLING HOUSE")
        )
    }
}

@Composable
fun MenuCard(title: String, subtitle: String, onClick: () -> Unit, highlight: Boolean = false, isLocked: Boolean = false) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()
    
    val scale by animateFloatAsState(
        targetValue = if (isPressed) 0.97f else 1f,
        animationSpec = tween(100),
        label = "CardScale"
    )

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(120.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            }
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = { if (!isLocked) onClick() }
            ),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(
                id = when {
                    isLocked -> R.drawable.menu_panel_locked
                    isPressed -> R.drawable.menu_panel_clicked
                    else -> R.drawable.menu_panel_normal
                }
            ),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Column(
            modifier = Modifier
                .padding(horizontal = 24.dp)
                .graphicsLayer(alpha = if (isLocked) 0.6f else 1f),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                color = if (highlight && !isLocked) Color(0xFFFFD600) else Color.White,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp,
                textAlign = TextAlign.Center
            )
            Text(
                text = subtitle,
                color = if (isLocked) Color.Gray else Color.LightGray,
                style = MaterialTheme.typography.bodySmall,
                textAlign = TextAlign.Center
            )
        }
    }
}
