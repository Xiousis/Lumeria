package com.example.lumeria.screens

import com.example.lumeria.R

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import com.example.lumeria.data.PlayerData
import com.example.lumeria.utils.MusicManager

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
    onSettings: () -> Unit,
    selectedTabIndex: Int,
    onTabSelected: (Int) -> Unit,
    onReturnToMain: () -> Unit,
    playerData: PlayerData,
    towerUnlockMessage: String? = null,
    onDismissTowerUnlock: () -> Unit = {}
) {
    val context = LocalContext.current
    val tabs = listOf("Adventure", "Player", "Town Square")

    LaunchedEffect(Unit) {
        MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.menu_background),
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
                    0 -> AdventureTab(onBattle, onStory, onArena, onBounty, onTower, onQuests, playerData.defeatedBosses)
                    1 -> PlayerTab(playerData, onInventory, onStats, onSkills, onJournal, onBestiary, onTrophyRoom, onCodex)
                    2 -> TownSquareTab(onShop, onElder, onInn, onGambling, onBlacksmith, onBank, onKingdom, onGuild, onFishing, onFamiliarStore, playerData.renown)
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
    defeatedBosses: List<String>
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MenuCard("WORLD MAP", "Travel across Lumeria and clear regions.", onBattle)
        MenuCard("STORY MODE", "Progress through the epic Rise of Xious.", onStory)
        MenuCard("BOUNTY BOARD", "Hunt down dangerous outlaws for big rewards.", onBounty)
        MenuCard("GRAND ARENA", "Challenge the realm's strongest humans.", onArena)
        
        if (com.example.lumeria.database.TowerDatabase.isTowerUnlocked(defeatedBosses)) {
            MenuCard("BATTLE TOWER", "Scale the 100 floors of ultimate trials.", onTower)
        }
        
        MenuCard("QUEST LOG", "Track your active objectives and rewards.", onQuests)
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
    onCodex: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val portraitId = when {
            playerData.hp.toFloat() / playerData.maxHp <= 0.20f -> R.drawable.xious_hp_20
            playerData.hp.toFloat() / playerData.maxHp <= 0.50f -> R.drawable.xious_hp_50
            playerData.hp.toFloat() / playerData.maxHp <= 0.75f -> R.drawable.xious_hp_75
            else -> R.drawable.xious_hp_100
        }

        Image(
            painter = painterResource(id = portraitId),
            contentDescription = "Xious Portrait",
            modifier = Modifier
                .size(260.dp)
                .clip(RoundedCornerShape(32.dp)),
            contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(8.dp))
        
        MenuCard("LUMERIA CODEX", "Learn about attributes, combat, and world systems.", onCodex, highlight = true)
        MenuCard("HALL OF HEROES", "View your trophies, medals, and legendary feats.", onTrophyRoom)
        MenuCard("INVENTORY", "Manage your gear and consumables.", onInventory)
        MenuCard("CHARACTER STATS", "Allocate points and review your power.", onStats)
        MenuCard("SKILL BOOK", "Equip and mastery your combat abilities.", onSkills)
        MenuCard("BESTIARY", "Review known enemies and their secrets.", onBestiary)
        MenuCard("STORY JOURNAL", "Review your journey and achievements.", onJournal)
        
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
    renown: Int,
) {
    Column(
        modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        MenuCard("THE ROYAL COURT", "Visit King Alaric and review your standing.", onKingdom)
        
        val guildUnlocked = renown >= 7500
        MenuCard(
            title = "THE GUILD",
            subtitle = if (guildUnlocked) "Join a House and learn elemental magic." else "Become Respected Adventurer to unlock.",
            onClick = { if (guildUnlocked) onGuild() },
            isLocked = !guildUnlocked
        )

        MenuCard("FISHING POND", "Relax and catch some legendary fish.", onFishing)
        MenuCard("FAMILIAR NURSERY", "Find a loyal companion to aid you.", onFamiliarStore)
        MenuCard("IRONCLAD BANK", "Deposit gold to protect it from defeat penalties.", onBank)
        MenuCard("THE FORGE", "Upgrade your weapons and armor to +5.", onBlacksmith)
        MenuCard("BILLY'S STORE", "Buy new gear or sell your old equipment.", onShop)
        MenuCard("THE ELDER", "Seek wisdom, respec stats, or make a wish.", onElder)
        
        MenuCard(
            title = "THE INN",
            subtitle = "Rest and recover your strength at Yumi's Inn.",
            onClick = onInn
        )

        MenuCard(
            title = "THE GAMBLING HOUSE",
            subtitle = "Try your luck at Blackjack and double your gold.",
            onClick = onGambling
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
