package com.example.lumeria.screens

import com.example.lumeria.R

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
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumeria.data.PlayerData
import com.example.lumeria.database.ArenaEnemyDatabase
import com.example.lumeria.database.ArenaOpponent
import com.example.lumeria.utils.CurrencyUtils

@Composable
fun ArenaSelectionScreen(
    playerData: PlayerData,
    onStartBattle: (ArenaOpponent) -> Unit,
    onReturn: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        com.example.lumeria.utils.MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.grand_arena_screen_bg),
            contentDescription = "Arena Background",
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
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    "Grand Arena",
                    style = MaterialTheme.typography.headlineMedium,
                    color = Color.Yellow,
                    fontWeight = FontWeight.Black
                )
                Text(
                    "Lv. ${playerData.level}",
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.LightGray
                )
            }

            Text(
                "Test your might against the realm's finest warriors.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val allOpponents = ArenaEnemyDatabase.opponents
                val grouped = allOpponents.groupBy { it.rank }
                
                grouped.forEach { (rank, rankOpponents) ->
                    item {
                        Text(
                            text = "$rank Rank",
                            style = MaterialTheme.typography.labelLarge,
                            color = when(rank) {
                                "Bronze" -> Color(0xFFCD7F32)
                                "Silver" -> Color(0xFFC0C0C0)
                                "Gold" -> Color(0xFFFFD700)
                                "Platinum" -> Color(0xFFE5E4E2)
                                "Master" -> Color(0xFFFF4500)
                                else -> Color.White
                            },
                            modifier = Modifier.padding(vertical = 4.dp)
                        )
                    }
                    
                    items(rankOpponents) { opponent ->
                        // Reworked Unlocking Logic: Use explicit name matching and global index
                        val globalIndex = allOpponents.indexOfFirst { it.enemy.name == opponent.enemy.name }
                        val isDefeated = playerData.completedArenaOpponents.contains(opponent.enemy.name)
                        
                        val isActuallyUnlocked = if (globalIndex == 0) {
                            playerData.level >= opponent.enemy.level
                        } else {
                            val previousOpponent = allOpponents[globalIndex - 1]
                            val previousDefeated = playerData.completedArenaOpponents.contains(previousOpponent.enemy.name)
                            previousDefeated && playerData.level >= opponent.enemy.level
                        }

                        ArenaOpponentRow(
                            opponent = opponent, 
                            isUnlocked = isActuallyUnlocked,
                            isCompleted = isDefeated
                        ) {
                            onStartBattle(opponent)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .height(80.dp)
                    .clickable { onReturn() },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun ArenaOpponentRow(
    opponent: ArenaOpponent,
    isUnlocked: Boolean,
    isCompleted: Boolean = false,
    onSelect: () -> Unit
) {
    val enemy = opponent.enemy
    val borderColor = when {
        isCompleted -> Color.Green.copy(alpha = 0.5f)
        isUnlocked -> Color.Yellow.copy(alpha = 0.6f)
        else -> Color.Gray.copy(alpha = 0.2f)
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(8.dp))
            .background(if (isUnlocked) Color.Black.copy(alpha = 0.5f) else Color.Black.copy(alpha = 0.2f))
            .border(if (isUnlocked) 2.dp else 1.dp, borderColor, RoundedCornerShape(8.dp))
            .clickable(enabled = isUnlocked) { onSelect() }
            .padding(12.dp)
    ) {
        Column {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = enemy.name,
                        style = MaterialTheme.typography.titleMedium,
                        color = if (isUnlocked) Color.White else Color.Gray,
                        fontWeight = FontWeight.Bold
                    )
                    if (isCompleted) {
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("✔", color = Color.Green, fontWeight = FontWeight.Black)
                    }
                }
                Text(
                    text = "Lv. ${enemy.level}",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUnlocked) Color.Yellow else Color.Gray
                )
            }
            Text(
                text = opponent.description,
                style = MaterialTheme.typography.bodySmall,
                color = if (isUnlocked) Color.LightGray else Color.DarkGray,
                modifier = Modifier.padding(vertical = 4.dp)
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Text(
                    text = "${CurrencyUtils.formatGold(opponent.rewardGold)} Gold | ${opponent.rewardXp} XP",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (isUnlocked) Color.Green.copy(alpha = 0.7f) else Color.Transparent,
                    fontSize = 10.sp
                )
            }
        }
        
        if (!isUnlocked) {
            Box(
                modifier = Modifier.matchParentSize().background(Color.Black.copy(alpha = 0.4f)),
                contentAlignment = Alignment.Center
            ) {
                Text("LOCKED", color = Color.Red, fontWeight = FontWeight.Black, style = MaterialTheme.typography.labelLarge)
            }
        }
    }
}
