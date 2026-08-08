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
import androidx.compose.material3.*
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.AchievementDatabase
import com.solerforge.lumeria.database.StoryCodexDatabase
import com.solerforge.lumeria.utils.MusicManager

@Composable
fun StoryJournalScreen(
    playerData: PlayerData,
    onReturn: () -> Unit
) {
    val context = LocalContext.current
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val tabs = listOf("Codex", "Achievements")
    
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
                .padding(top = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Story Journal",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                modifier = Modifier.padding(vertical = 8.dp)
            )

            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = Color.Transparent,
                contentColor = Color.Cyan,
                indicator = { tabPositions ->
                    TabRowDefaults.SecondaryIndicator(
                        Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = Color.Cyan
                    )
                },
                divider = {}
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = if (selectedTabIndex == index) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    )
                }
            }

            Box(modifier = Modifier.weight(1f).fillMaxWidth().padding(16.dp)) {
                when (selectedTabIndex) {
                    0 -> CodexTab()
                    1 -> AchievementsTab(playerData)
                }
            }

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

@Composable
fun CodexTab() {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Group by category dynamically
        val categories = StoryCodexDatabase.entries.map { it.category }.distinct()
        
        categories.forEach { category ->
            val catEntries = StoryCodexDatabase.entries.filter { it.category == category }
            if (catEntries.isNotEmpty()) {
                item {
                    val displayCategory = when (category) {
                        "Character" -> "Characters"
                        "Boss" -> "Bosses"
                        "World" -> "World & Lore"
                        else -> category + "s"
                    }
                    Text(
                        text = displayCategory,
                        color = Color.Cyan,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(vertical = 8.dp)
                    )
                }
                
                items(catEntries) { entry ->
                    CodexEntryRow(entry)
                }
            }
        }
    }
}

@Composable
fun AchievementsTab(playerData: PlayerData) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        val total = AchievementDatabase.achievements.size
        val unlockedCount = AchievementDatabase.achievements.count { it.id in playerData.unlockedAchievements }
        
        item {
            Column(
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "Total Progress: $unlockedCount / $total",
                    color = Color.Yellow,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                LinearProgressIndicator(
                    progress = { unlockedCount.toFloat() / total.toFloat() },
                    modifier = Modifier.fillMaxWidth(0.8f).height(8.dp).padding(top = 8.dp),
                    color = Color.Green,
                    trackColor = Color.DarkGray,
                )
            }
        }

        items(AchievementDatabase.achievements) { achievement ->
            val isUnlocked = achievement.id in playerData.unlockedAchievements
            AchievementRow(achievement, isUnlocked)
        }
    }
}

@Composable
fun CodexEntryRow(entry: StoryCodexDatabase.CodexEntry) {
    val categoryIcon = when (entry.category) {
        "Character" -> "👤"
        "Boss" -> "💀"
        "World" -> "🌍"
        else -> "📜"
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
            .border(1.dp, Color.Gray.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .padding(16.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(
                text = categoryIcon,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(end = 12.dp)
            )
            Text(
                text = entry.name,
                color = Color.Yellow,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = entry.description,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun AchievementRow(achievement: com.solerforge.lumeria.data.Achievement, isUnlocked: Boolean) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isUnlocked) Color.DarkGray.copy(alpha = 0.4f) else Color.Black.copy(alpha = 0.6f),
                RoundedCornerShape(12.dp)
            )
            .border(
                1.dp,
                if (isUnlocked) Color.Yellow.copy(alpha = 0.5f) else Color.Gray.copy(alpha = 0.3f),
                RoundedCornerShape(12.dp)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = if (isUnlocked) achievement.icon else "🔒",
            style = MaterialTheme.typography.displaySmall,
            modifier = Modifier.padding(end = 16.dp)
        )
        
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = achievement.title,
                color = if (isUnlocked) Color.White else Color.Gray,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = if (isUnlocked) achievement.description else "Locked Achievement",
                color = if (isUnlocked) Color.LightGray else Color.DarkGray,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        if (isUnlocked) {
            Text(
                "COMPLETED",
                color = Color.Green,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}
