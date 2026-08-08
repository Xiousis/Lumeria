package com.solerforge.lumeria.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.R
import com.solerforge.lumeria.components.QuestRow
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.GameDatabase
import com.solerforge.lumeria.database.WorldDatabase
import com.solerforge.lumeria.models.XiousStats
import com.solerforge.lumeria.utils.MusicManager

@Composable
fun QuestScreen(
    modifier: Modifier = Modifier,
    playerData: PlayerData,
    onPlayerUpdate: (PlayerData) -> Unit,
    onReturn: () -> Unit,
) {
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    var selectedTab by remember { mutableStateOf("Active") }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.quest_log_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.45f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Quest Log",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // TABS
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Button(
                    onClick = { selectedTab = "Active" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == "Active") MaterialTheme.colorScheme.primary else Color(0xFF616161)
                    )
                ) {
                    Text("Active")
                }
                Button(
                    onClick = { selectedTab = "Completed" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedTab == "Completed") Color(0xFF4CAF50) else Color(0xFF616161)
                    )
                ) {
                    Text("History")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                if (selectedTab == "Active") {
                    val activeQuests = playerData.quests.filter { !it.isClaimed }
                    if (activeQuests.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No active quests. Visit the World Map to find work!", color = Color.Gray)
                            }
                        }
                    } else {
                        items(activeQuests) { quest ->
                            QuestRow(quest) {
                                if (quest.isCompleted) {
                                    // Logic for claiming reward
                                    val updatedQuests = playerData.quests.map {
                                        if (it.title == quest.title) it.copy(isClaimed = true) else it
                                    }
                                    
                                    val questCompletePlayer = playerData.gainExperience(quest.rewardXp).copy(
                                        gold = playerData.gold + quest.rewardGold,
                                        quests = updatedQuests
                                    )
                                    
                                    val currentQuestsInLog = updatedQuests.toMutableList()
                                    val newQuestsAvailable = GameDatabase.allQuests.filter { questItem ->
                                        val location = WorldDatabase.locations.find { it.name == questItem.requiredLocation }
                                        val isLocationUnlocked = location != null && (playerData.level >= location.requiredLevel || playerData.unlockedLocations.contains(questItem.requiredLocation))
                                        isLocationUnlocked && currentQuestsInLog.none { it.title == questItem.title }
                                    }
                                    currentQuestsInLog.addAll(newQuestsAvailable)

                                    onPlayerUpdate(
                                        questCompletePlayer.copy(
                                            quests = currentQuestsInLog
                                        )
                                    )
                                }
                            }
                        }
                    }
                } else {
                    // COMPLETED TAB
                    val claimedQuests = playerData.quests.filter { it.isClaimed }
                    if (claimedQuests.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No completed quests yet. Keep hunting!", color = Color.Gray)
                            }
                        }
                    } else {
                        items(claimedQuests) { quest ->
                            QuestRow(quest.copy(isCompleted = true)) { /* No action */ }
                        }
                    }
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
