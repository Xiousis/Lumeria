package com.solerforge.lumeria.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
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
import com.solerforge.lumeria.R
import com.solerforge.lumeria.components.RpgButton
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.models.Guild
import com.solerforge.lumeria.models.GuildMember
import com.solerforge.lumeria.utils.CurrencyUtils
import com.solerforge.lumeria.viewmodels.GuildViewModel
import kotlinx.coroutines.flow.StateFlow

enum class GuildTab(val displayName: String) {
    DASHBOARD("DASHBOARD"),
    MEMBERS("MEMBERS"),
    VAULT("VAULT"),
    WARS("WARS")
}

@Composable
fun GuildScreen(
    playerData: PlayerData,
    guildViewModel: GuildViewModel,
    deviceIdFlow: StateFlow<String?>,
    onPlayerUpdate: (PlayerData) -> Unit,
    onNavigateToIntro: () -> Unit,
    onStartExam: (com.solerforge.lumeria.database.GuildDatabase.GuildExam) -> Unit,
    onReturn: () -> Unit
) {
    val deviceId by deviceIdFlow.collectAsState()
    val currentGuild by guildViewModel.currentGuild.collectAsState()
    val guildList by guildViewModel.guildList.collectAsState()
    val members by guildViewModel.guildMembers.collectAsState()
    val isLoading by guildViewModel.isLoading.collectAsState()
    val error by guildViewModel.error.collectAsState()

    LaunchedEffect(playerData.joinedGuildId, playerData.isReborn) {
        if (playerData.joinedGuildId != null) {
            guildViewModel.fetchCurrentGuild(playerData.joinedGuildId)
        } else if (playerData.isReborn) {
            guildViewModel.fetchGuilds()
        }
    }

    if (playerData.joinedGuildId == null && playerData.joinedGuild == null) {
        if (playerData.isReborn) {
            GuildBrowserScreen(
                guildList = guildList,
                isLoading = isLoading,
                playerData = playerData,
                onCreateGuild = { name ->
                    val id = deviceId ?: return@GuildBrowserScreen // Guard
                    if (playerData.gold >= 1000000L) {
                        guildViewModel.createGuild(name, playerData, id) { gid, guildName ->
                            onPlayerUpdate(playerData.copy(
                                joinedGuildId = gid, 
                                joinedGuild = guildName,
                                gold = playerData.gold - 1000000L
                            ))
                        }
                    }
                },
                onJoinGuild = { guild ->
                    val id = deviceId ?: return@GuildBrowserScreen // Guard
                    guildViewModel.joinGuild(guild.id, guild.name, playerData, id) { gid, guildName ->
                        onPlayerUpdate(playerData.copy(joinedGuildId = gid, joinedGuild = guildName))
                    }
                },
                onReturn = onReturn
            )
        } else {
            StoryGuildSelectionScreen(
                onJoinGuild = { name ->
                    onPlayerUpdate(playerData.copy(joinedGuild = name, guildLevel = 1, guildXp = 0))
                    onNavigateToIntro()
                },
                onReturn = onReturn
            )
        }
    } else if (playerData.joinedGuildId != null) {
        val id = deviceId ?: "unknown" // Dashboard is mostly read-only, but donation needs ID
        GuildDashboardScreen(
            playerData = playerData,
            guild = currentGuild,
            members = members,
            deviceId = id,
            guildViewModel = guildViewModel,
            onPlayerUpdate = onPlayerUpdate,
            onReturn = onReturn
        )
    } else {
        StoryGuildDashboardScreen(
            playerData = playerData,
            onPlayerUpdate = onPlayerUpdate,
            onStartExam = onStartExam,
            onReturn = onReturn
        )
    }
    
    error?.let {
        AlertDialog(
            onDismissRequest = { guildViewModel.clearError() },
            title = { Text("Guild Error") },
            text = { Text(it) },
            confirmButton = { Button(onClick = { guildViewModel.clearError() }) { Text("OK") } }
        )
    }
}

@Composable
fun GuildBrowserScreen(
    guildList: List<Guild>,
    isLoading: Boolean,
    playerData: PlayerData,
    onCreateGuild: (String) -> Unit,
    onJoinGuild: (Guild) -> Unit,
    onReturn: () -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var guildNameInput by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.guild_screen_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.7f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("THE GUILD HALL", color = Color.Cyan, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
            Text("Create or join a fellowship of adventurers", color = Color.Gray, style = MaterialTheme.typography.labelSmall)

            Spacer(modifier = Modifier.height(24.dp))

            if (playerData.isReborn) {
                RpgButton(
                    text = "Create Guild (1,000,000 Gold)",
                    onClick = { showCreateDialog = true },
                    modifier = Modifier.fillMaxWidth(0.8f)
                )

                Spacer(modifier = Modifier.height(24.dp))
            }

            if (isLoading) {
                CircularProgressIndicator(color = Color.Cyan)
            } else {
                LazyColumn(modifier = Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    item { Text("Available Guilds", color = Color.White, style = MaterialTheme.typography.titleMedium) }
                    items(guildList) { guild ->
                        GuildListItem(guild, onJoin = { onJoinGuild(guild) })
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            RpgButton(text = "Return", onClick = onReturn, modifier = Modifier.width(200.dp))
        }

        if (showCreateDialog) {
            AlertDialog(
                onDismissRequest = { showCreateDialog = false },
                title = { Text("Founder's Decree", color = Color.White) },
                text = {
                    Column {
                        Text("Name your guild (Max 20 chars):", color = Color.LightGray)
                        Spacer(modifier = Modifier.height(12.dp))
                        OutlinedTextField(
                            value = guildNameInput,
                            onValueChange = { if (it.length <= 20) guildNameInput = it },
                            modifier = Modifier.fillMaxWidth(),
                            singleLine = true,
                            colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Black.copy(alpha = 0.3f),
                                unfocusedContainerColor = Color.Black.copy(alpha = 0.3f),
                                focusedTextColor = Color.White,
                                unfocusedTextColor = Color.White
                            )
                        )
                    }
                },
                confirmButton = {
                    RpgButton(text = "Confirm", onClick = {
                        if (guildNameInput.isNotBlank()) {
                            onCreateGuild(guildNameInput)
                            showCreateDialog = false
                        }
                    }, modifier = Modifier.width(100.dp))
                },
                dismissButton = {
                    TextButton(onClick = { showCreateDialog = false }) {
                        Text("Cancel", color = Color.Gray)
                    }
                },
                containerColor = Color(0xFF1E293B)
            )
        }
    }
}

@Composable
fun GuildListItem(guild: Guild, onJoin: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, Color.Cyan.copy(alpha = 0.3f))
    ) {
        Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(guild.name, color = Color.White, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleMedium)
                Text("Leader: ${guild.leaderName}", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                Text("${guild.membersCount}/${guild.maxMembers} Members", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            }
            RpgButton(text = "Join", onClick = onJoin, modifier = Modifier.height(40.dp))
        }
    }
}

@Composable
fun GuildDashboardScreen(
    playerData: PlayerData,
    guild: Guild?,
    members: List<GuildMember>,
    deviceId: String,
    guildViewModel: GuildViewModel,
    onPlayerUpdate: (PlayerData) -> Unit,
    onReturn: () -> Unit
) {
    var selectedTab by remember { mutableStateOf(GuildTab.DASHBOARD) }
    val isLoading by guildViewModel.isLoading.collectAsState()
    val color = Color.Cyan

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.guild_screen_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.8f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp)
        ) {
            if (guild == null) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = Color.Cyan)
                }
            } else {
                Text(guild.name.uppercase(), color = color, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Guild Level ${guild.level}", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    
                    val nextXp = com.solerforge.lumeria.database.GuildDatabase.getGuildXpForNextLevel(guild.level)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${guild.xp} / $nextXp XP",
                            color = Color.Cyan,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LinearProgressIndicator(
                            progress = { (guild.xp.toFloat() / nextXp.toFloat()).coerceIn(0f, 1f) },
                            modifier = Modifier.width(80.dp).height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = Color.Cyan,
                            trackColor = Color.DarkGray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    GuildTab.entries.forEach { tab ->
                        val isSelected = selectedTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { selectedTab = tab }
                                .padding(vertical = 12.dp)
                                .background(if (isSelected) color.copy(alpha = 0.2f) else Color.Transparent, RoundedCornerShape(4.dp))
                                .border(1.dp, if (isSelected) color else Color.Transparent, RoundedCornerShape(4.dp)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                tab.displayName, 
                                color = if (isSelected) Color.Cyan else Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.weight(1f)) {
                    when (selectedTab) {
                        GuildTab.DASHBOARD -> {
                            Column {
                                Card(
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                                    border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
                                ) {
                                    Column(modifier = Modifier.padding(16.dp)) {
                                        Text("MOTD", color = Color.Cyan, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                                        Spacer(modifier = Modifier.height(4.dp))
                                        Text(guild.announcement, color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                    }
                                }
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("War Readiness", color = Color.White, fontWeight = FontWeight.Bold)
                                Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                                    Text("Rating: ${guild.warRating}", color = Color.Red, style = MaterialTheme.typography.bodyLarge)
                                    Text("Record: ${guild.warWins}W / ${guild.warLosses}L", color = Color.Gray)
                                }
                            }
                        }
                        GuildTab.MEMBERS -> {
                            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                item { Text("Guild Roster (${members.size}/${guild.maxMembers})", color = Color.White, style = MaterialTheme.typography.labelSmall) }
                                items(members) { member ->
                                    MemberRow(member)
                                }
                            }
                        }
                        GuildTab.VAULT -> {
                            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                                Text("Guild Treasure Vault", color = Color.Yellow, style = MaterialTheme.typography.titleMedium)
                                Text(CurrencyUtils.formatGold(guild.guildVault), color = Color.White, style = MaterialTheme.typography.displaySmall, fontWeight = FontWeight.Black)
                                Spacer(modifier = Modifier.height(32.dp))
                                RpgButton(
                                    text = "Donate 5,000 Gold",
                                    enabled = !isLoading && playerData.gold >= 5000,
                                    onClick = {
                                        if (playerData.gold >= 5000) {
                                            guildViewModel.donate(guild.id, 5000, deviceId) {
                                                onPlayerUpdate(playerData.copy(gold = playerData.gold - 5000))
                                            }
                                        }
                                    },
                                    modifier = Modifier.fillMaxWidth(0.7f)
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text("Your Gold: ${CurrencyUtils.formatGold(playerData.gold)}", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                        GuildTab.WARS -> {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center,
                                modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                            ) {
                                Text("GUILD WARS", color = Color.Red, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black)
                                Text("TERRITORY CONTROL", color = Color.White.copy(alpha = 0.6f), style = MaterialTheme.typography.titleMedium)
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("COMING SOON", color = Color.White, style = MaterialTheme.typography.labelLarge)
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            RpgButton(text = "Return to Menu", onClick = onReturn, modifier = Modifier.align(Alignment.CenterHorizontally).width(220.dp))
        }
    }
}

@Composable
fun MemberRow(member: GuildMember) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(member.playerName, color = Color.White, fontWeight = FontWeight.Bold)
            Text("Lv. ${member.playerLevel} • ${member.rank}", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(CurrencyUtils.formatGold(member.contributionGold), color = Color.Yellow, style = MaterialTheme.typography.labelSmall)
            Text("Contributed", color = Color.DarkGray, style = MaterialTheme.typography.labelSmall)
        }
    }
}

@Composable
fun StoryGuildDashboardScreen(
    playerData: PlayerData,
    onPlayerUpdate: (PlayerData) -> Unit,
    onStartExam: (com.solerforge.lumeria.database.GuildDatabase.GuildExam) -> Unit,
    onReturn: () -> Unit
) {
    val guildName = playerData.joinedGuild ?: "Unknown Guild"
    val color = when (guildName) {
        "House of Fire" -> Color(0xFFEF4444)
        "House of Water" -> Color(0xFF3B82F6)
        "House of Wind" -> Color(0xFF10B981)
        else -> Color.Cyan
    }

    val dashboardBg = when (guildName) {
        "House of Fire" -> R.drawable.house_of_fire_bg
        "House of Water" -> R.drawable.house_of_water_bg
        "House of Wind" -> R.drawable.house_of_wind_bg
        else -> R.drawable.guild_screen_bg
    }
    
    var showSkillsDialog by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = dashboardBg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.8f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp)
        ) {
            Text(guildName.uppercase(), color = color, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Black, letterSpacing = 2.sp)
            Text("Master: ${com.solerforge.lumeria.database.GuildDatabase.getGuildMaster(guildName)}", color = Color.Gray, style = MaterialTheme.typography.labelSmall)

            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("HOUSE MISSION", color = color, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = com.solerforge.lumeria.database.GuildDatabase.getGuildMasterDialogue(guildName, playerData.guildLevel),
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Guild Level HUD
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Rank ${playerData.guildLevel}", color = Color.White, fontWeight = FontWeight.Bold)
                val nextXp = com.solerforge.lumeria.database.GuildDatabase.getGuildXpForNextLevel(playerData.guildLevel)
                Text("${playerData.guildXp} / $nextXp XP", color = color, style = MaterialTheme.typography.labelSmall)
            }
            
            LinearProgressIndicator(
                progress = { (playerData.guildXp.toFloat() / com.solerforge.lumeria.database.GuildDatabase.getGuildXpForNextLevel(playerData.guildLevel).toFloat()).coerceIn(0f, 1f) },
                modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                color = color,
                trackColor = Color.DarkGray
            )

            Spacer(modifier = Modifier.weight(1f))

            RpgButton(
                text = "Learn Elemental Skills",
                onClick = { showSkillsDialog = true },
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(12.dp))

            val nextRank = playerData.guildLevel + 1
            val exam = com.solerforge.lumeria.database.GuildDatabase.getGuildExam(guildName, nextRank)
            val canTakeExam = exam != null && playerData.guildXp >= com.solerforge.lumeria.database.GuildDatabase.getGuildXpForNextLevel(playerData.guildLevel)

            RpgButton(
                text = if (exam != null) "Take Rank $nextRank Exam" else "Max Rank Reached",
                enabled = canTakeExam,
                onClick = { exam?.let { onStartExam(it) } },
                modifier = Modifier.fillMaxWidth(),
                containerColor = if (canTakeExam) Color(0xFF6200EE) else Color.DarkGray
            )

            Spacer(modifier = Modifier.height(24.dp))
            RpgButton(text = "Return to Menu", onClick = onReturn, modifier = Modifier.align(Alignment.CenterHorizontally).width(220.dp))
        }
        
        if (showSkillsDialog) {
            val skills = com.solerforge.lumeria.database.GuildDatabase.getGuildSkills(guildName, playerData.playerClass)
            AlertDialog(
                onDismissRequest = { showSkillsDialog = false },
                title = { Text("Elemental Techniques", color = color, fontWeight = FontWeight.Black) },
                text = {
                    LazyColumn(modifier = Modifier.heightIn(max = 400.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(skills) { req ->
                            val isUnlocked = playerData.unlockedSkills.contains(req.skill.name)
                            val canAfford = playerData.gold >= req.fee
                            val levelMet = playerData.guildLevel >= req.requiredGuildLevel

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.05f)),
                                border = BorderStroke(1.dp, if (isUnlocked) Color.Green.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f))
                            ) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                        Text(req.skill.name, color = Color.White, fontWeight = FontWeight.Bold)
                                        if (isUnlocked) {
                                            Text("LEARNED", color = Color.Green, fontSize = 10.sp, fontWeight = FontWeight.Black)
                                        } else {
                                            Text(CurrencyUtils.formatGold(req.fee), color = Color.Yellow, fontSize = 10.sp)
                                        }
                                    }
                                    Text(req.skill.description, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    if (!isUnlocked) {
                                        Button(
                                            onClick = {
                                                onPlayerUpdate(playerData.copy(
                                                    gold = playerData.gold - req.fee,
                                                    unlockedSkills = (playerData.unlockedSkills + req.skill.name).distinct()
                                                ))
                                            },
                                            enabled = canAfford && levelMet,
                                            modifier = Modifier.fillMaxWidth().height(32.dp),
                                            contentPadding = PaddingValues(0.dp),
                                            colors = ButtonDefaults.buttonColors(containerColor = color)
                                        ) {
                                            Text(if (levelMet) "Learn" else "Requires Rank ${req.requiredGuildLevel}", fontSize = 10.sp)
                                        }
                                    }
                                }
                            }
                        }
                    }
                },
                confirmButton = {
                    TextButton(onClick = { showSkillsDialog = false }) { Text("Close", color = Color.White) }
                },
                containerColor = Color(0xFF0F172A)
            )
        }
    }
}
