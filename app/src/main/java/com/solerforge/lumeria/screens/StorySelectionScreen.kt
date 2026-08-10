package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.components.RpgButton
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.StoryDatabase
import com.solerforge.lumeria.models.StoryArc
import com.solerforge.lumeria.utils.MusicManager

@Composable
fun StorySelectionScreen(
    playerData: PlayerData,
    onSelectArc: (StoryArc) -> Unit,
    onReturn: () -> Unit,
) {
    var selectedTab by remember { mutableStateOf("Main") }
    val context = LocalContext.current
    
    val hasAllGodTier = com.solerforge.lumeria.database.BossLootDatabase.allGodTierItems.all { it in playerData.inventory }
    val secretUnlocked = (playerData.completedStoryArcs.size >= 20) && 
                         (playerData.completedSideStoryArcs.size >= 4) && 
                         hasAllGodTier

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

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Story Mode",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // TABS
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val primaryColor = MaterialTheme.colorScheme.primary
                
                RpgButton(
                    text = "Main Story",
                    onClick = { selectedTab = "Main" },
                    modifier = Modifier.weight(1f),
                    containerColor = if (selectedTab == "Main") primaryColor else Color.DarkGray
                )
                RpgButton(
                    text = "Side Stories",
                    onClick = { selectedTab = "Side" },
                    modifier = Modifier.weight(1f),
                    containerColor = if (selectedTab == "Side") primaryColor else Color.DarkGray
                )
                if (secretUnlocked) {
                    RpgButton(
                        text = "Secret",
                        onClick = { selectedTab = "Secret" },
                        modifier = Modifier.weight(1f),
                        containerColor = if (selectedTab == "Secret") Color(0xFFFFD600) else Color.DarkGray,
                        contentColor = if (selectedTab == "Secret") Color.Black else Color.White
                    )
                }
                if (playerData.isReborn) {
                    RpgButton(
                        text = "Legacy",
                        onClick = { selectedTab = "Legacy" },
                        modifier = Modifier.weight(1f),
                        containerColor = if (selectedTab == "Legacy") Color(0xFFA855F7) else Color.DarkGray
                    )
                }
            }

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val race = com.solerforge.lumeria.database.RaceDatabase.getRace(playerData.playerRace)
                val isMonster = playerData.isReborn && race.isMonster
                
                val arcs = when (selectedTab) {
                    "Main" -> {
                        if (isMonster) {
                            com.solerforge.lumeria.database.MonsterStoryDatabase.mainArcs
                        } else if (playerData.isReborn) {
                            com.solerforge.lumeria.database.LegacyStoryDatabase.mainArcs
                        } else {
                            StoryDatabase.arcs.filter { it.id <= 20 }
                        }
                    }
                    "Side" -> {
                        if (isMonster) {
                            com.solerforge.lumeria.database.MonsterStoryDatabase.sideArcs
                        } else if (playerData.isReborn) {
                            com.solerforge.lumeria.database.LegacyStoryDatabase.sideArcs
                        } else {
                            com.solerforge.lumeria.database.SideStoryDatabase.allArcs
                        }
                    }
                    "Secret" -> com.solerforge.lumeria.database.SecretBossDatabase.secretArcs
                    "Legacy" -> StoryDatabase.arcs.filter { it.id > 20 && it.id < 1000 } // Old archive
                    else -> emptyList()
                }

                items(arcs) { arc ->
                    val isCompleted = arc.id in (playerData.completedStoryArcs + playerData.completedSideStoryArcs)
                    val isResumable = !isCompleted && playerData.activeStoryArcId == arc.id
                    
                    val isFirstInTab = arc.id == arcs.firstOrNull()?.id
                    val previousArcComplete = isFirstInTab || (arc.id - 1) in (playerData.completedStoryArcs + playerData.completedSideStoryArcs)
                    
                    val isAvailable = (playerData.level >= arc.requiredLevel && previousArcComplete) || isCompleted
                    val isLocked = !isAvailable

                    StoryArcRow(
                        arc = arc,
                        isCompleted = isCompleted,
                        isAvailable = isAvailable,
                        isLocked = isLocked,
                        isResumable = isResumable
                    ) {
                        if (isAvailable) onSelectArc(arc)
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(id = R.drawable.return_to_menu_font),
                    contentDescription = "Return",
                    modifier = Modifier
                        .padding(8.dp)
                        .height(80.dp)
                        .clickable { onReturn() },
                    contentScale = ContentScale.Fit
                )
            }
        }
    }
}

@Composable
fun StoryArcRow(
    arc: StoryArc,
    isCompleted: Boolean,
    isAvailable: Boolean,
    isLocked: Boolean,
    isResumable: Boolean = false,
    onClick: () -> Unit
) {
    val backgroundColor = when {
        isResumable -> Color.Blue.copy(alpha = 0.4f)
        isCompleted -> Color.Green.copy(alpha = 0.2f)
        isAvailable -> Color.Blue.copy(alpha = 0.2f)
        else -> Color.Gray.copy(alpha = 0.2f)
    }

    val borderColor = when {
        isResumable -> Color.Yellow
        isCompleted -> Color.Green
        isAvailable -> Color.Cyan
        else -> Color.DarkGray
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor, RoundedCornerShape(12.dp))
            .border(if (isResumable) 2.dp else 1.dp, borderColor, RoundedCornerShape(12.dp))
            .clickable(enabled = !isLocked) { onClick() }
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Arc ${arc.id}: ${arc.title}",
                color = if (isLocked) Color.Gray else Color.White,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            
            Text(
                text = when {
                    isResumable -> "[Resume]"
                    isCompleted -> "[Replay]"
                    isAvailable -> "[Available]"
                    else -> "[Locked Lv.${arc.requiredLevel}]"
                },
                color = when {
                    isResumable -> Color.Yellow
                    isCompleted -> Color.Green
                    isAvailable -> Color.Cyan
                    else -> Color.Gray
                },
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        Spacer(modifier = Modifier.height(4.dp))
        
        Text(
            text = arc.description,
            color = if (isLocked) Color.DarkGray else Color.LightGray,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}
