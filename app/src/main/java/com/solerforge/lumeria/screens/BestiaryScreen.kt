package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.*

@Composable
fun BestiaryScreen(
    playerData: PlayerData,
    onBack: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val allEnemies = remember {
        val normalEnemies = WorldDatabase.locations.flatMap { it.enemies }.distinct()
        val bosses = BossDatabase.bosses.map { it.name }
        val storyBosses = StoryBossDatabase.bosses.map { it.name }
        val arena = ArenaEnemyDatabase.opponents.map { it.enemy.name }
        (normalEnemies + bosses + storyBosses + arena).sorted()
    }

    var selectedEnemy by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        com.solerforge.lumeria.utils.MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.bestiary_screen_bg),
            contentDescription = "Bestiary Background",
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
            Text(
                "Bestiary Mastery",
                style = MaterialTheme.typography.headlineLarge,
                color = Color.Yellow,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Black
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            if (selectedEnemy == null) {
                LazyColumn(modifier = Modifier.weight(1f)) {
                    items(allEnemies) { enemyName ->
                        val killCount = playerData.killCounts[enemyName] ?: 0
                        MasteryEnemyRow(
                            name = if (killCount > 0) enemyName else "???",
                            killCount = killCount,
                            onClick = { if (killCount > 0) selectedEnemy = enemyName }
                        )
                    }
                }
            } else {
                val name = selectedEnemy!!
                val killCount = playerData.killCounts[name] ?: 0
                
                MasteryDetailView(name, killCount) { selectedEnemy = null }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .height(80.dp)
                    .clickable { onBack() },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun MasteryEnemyRow(name: String, killCount: Int, onClick: () -> Unit) {
    val rank = getMasteryRank(killCount)
    val rankColor = getMasteryColor(rank)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
            .border(1.dp, rankColor.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column {
            Text(name, color = if (name == "???") Color.Gray else Color.White, fontWeight = FontWeight.Bold)
            if (name != "???") {
                Text("Mastery Rank $rank", color = rankColor, style = MaterialTheme.typography.labelSmall)
            }
        }
        if (name != "???") {
            Text("x$killCount", color = Color.Yellow, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
fun MasteryDetailView(name: String, killCount: Int, onBack: () -> Unit) {
    val rank = getMasteryRank(killCount)
    val rankColor = getMasteryColor(rank)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF1A1A1A).copy(alpha = 0.8f), RoundedCornerShape(12.dp))
            .border(2.dp, rankColor, RoundedCornerShape(12.dp))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(name, style = MaterialTheme.typography.headlineMedium, color = Color.White, fontWeight = FontWeight.Black)
        Text("Mastery Level $rank", color = rankColor, fontWeight = FontWeight.Bold)
        Text("Total Defeated: $killCount", color = Color.LightGray, style = MaterialTheme.typography.labelMedium)
        
        Spacer(modifier = Modifier.height(24.dp))

        // Progress to next rank
        val nextThreshold = when(rank) {
            0 -> 10
            1 -> 50
            2 -> 150
            3 -> 500
            else -> 1000
        }
        
        if (rank < 4) {
            Column(modifier = Modifier.fillMaxWidth()) {
                val progress = (killCount.toFloat() / nextThreshold).coerceIn(0f, 1f)
                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                    color = getMasteryColor(rank + 1),
                    trackColor = Color.DarkGray
                )
                Text("Next Rank at $nextThreshold Kills", color = Color.Gray, style = MaterialTheme.typography.labelSmall, modifier = Modifier.align(Alignment.End))
            }
        } else {
            Text("⭐ GRAND MASTER ACHIEVED ⭐", color = Color.Yellow, fontWeight = FontWeight.Black)
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Information Section based on rank
        Column(modifier = Modifier.fillMaxWidth().weight(1f).verticalScroll(rememberScrollState())) {
            Text("Information", color = Color.Cyan, fontWeight = FontWeight.Bold)
            if (killCount >= 10) {
                 Text("This creature inhabits various regions of Lumeria and poses a significant threat to unprepared travelers.", color = Color.White)
            } else {
                 Text("???", color = Color.DarkGray)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Elemental Affinity", color = Color.Cyan, fontWeight = FontWeight.Bold)
            if (killCount >= 50) {
                Text("Study complete. Weaknesses and resistances revealed in combat view.", color = Color.White)
            } else {
                Text("(10 Kills required to reveal basic info, 50 for full study)", color = Color.DarkGray, style = MaterialTheme.typography.labelSmall)
            }

            Spacer(modifier = Modifier.height(16.dp))
            Text("Lore", color = Color.Cyan, fontWeight = FontWeight.Bold)
            if (killCount >= 500) {
                Text("Ancient texts mention this being as a guardian of the ley lines, though its nature has been twisted over centuries of conflict.", color = Color.White)
            } else {
                Text("(Rank 3 required to reveal)", color = Color.DarkGray, style = MaterialTheme.typography.labelSmall)
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Perks List Summary
        Text("Unlocked Mastery Perks", color = Color.LightGray, style = MaterialTheme.typography.labelSmall)
        MasteryPerkRow("Reveal HP Numbers", killCount >= 25, 1)
        MasteryPerkRow("Reveal Resistances", killCount >= 150, 2)
        MasteryPerkRow("Full Intel", killCount >= 500, 3)
        MasteryPerkRow("+10% Damage Bonus", killCount >= 1000, 4)

        Spacer(modifier = Modifier.height(16.dp))
        
        Button(onClick = onBack, colors = ButtonDefaults.buttonColors(containerColor = rankColor)) {
            Text("Back to List", color = if (rankColor == Color.Yellow || rankColor == Color.White) Color.Black else Color.White)
        }
    }
}

@Composable
fun MasteryPerkRow(label: String, isUnlocked: Boolean, rankReq: Int) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 2.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(label, color = if (isUnlocked) Color.White else Color.DarkGray, fontSize = 12.sp)
        Text(
            if (isUnlocked) "UNLOCKED" else "Rank $rankReq", 
            color = if (isUnlocked) Color.Green else Color.Red.copy(alpha = 0.5f),
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.Bold,
            fontSize = 10.sp
        )
    }
}

fun getMasteryRank(kills: Int): Int {
    return when {
        kills >= 1000 -> 4
        kills >= 500 -> 3
        kills >= 150 -> 2
        kills >= 10 -> 1
        else -> 0
    }
}

fun getMasteryColor(rank: Int): Color {
    return when(rank) {
        1 -> Color.White
        2 -> Color.Cyan
        3 -> Color.Magenta
        4 -> Color.Yellow
        else -> Color.Gray
    }
}
