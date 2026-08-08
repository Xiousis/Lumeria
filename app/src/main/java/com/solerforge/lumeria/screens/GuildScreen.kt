package com.solerforge.lumeria.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.R
import com.solerforge.lumeria.components.RpgButton
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.GuildDatabase
import com.solerforge.lumeria.database.GuildSkillRequirement
import com.solerforge.lumeria.utils.CurrencyUtils

enum class GuildTab(val displayName: String) {
    LEADER("LEADER"),
    SKILLS("SKILLS"),
    QUESTS("QUESTS"),
    RANKS("RANKS")
}

@Composable
fun GuildScreen(
    playerData: PlayerData,
    onJoinGuild: (String) -> Unit,
    onLearnSkill: (String, Int) -> Unit,
    onStartExam: (GuildDatabase.GuildExam) -> Unit,
    onShowLore: () -> Unit,
    onReturn: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    LaunchedEffect(playerData.joinedGuild) {
        val theme = when (playerData.joinedGuild) {
            "House of Fire" -> R.raw.house_of_fire_guild_theme
            "House of Water" -> R.raw.house_of_water_guild_theme
            "House of Wind" -> R.raw.house_of_wind_ninja_guild_theme
            else -> R.raw.lumeria_main_menu_theme
        }
        com.solerforge.lumeria.utils.MusicManager.playMusic(context, theme)
    }

    if (playerData.joinedGuild == null) {
        GuildSelectionScreen(onJoinGuild, onReturn)
    } else {
        GuildMainScreen(playerData, onLearnSkill, onStartExam, onShowLore, onReturn)
    }
}

@Composable
fun GuildSelectionScreen(onJoin: (String) -> Unit, onReturn: () -> Unit) {
    var guildToConfirm by remember { mutableStateOf<String?>(null) }

    Box(modifier = Modifier.fillMaxSize()) {
        val currentBg = when (guildToConfirm) {
            "House of Fire" -> R.drawable.house_of_fire_bg
            "House of Water" -> R.drawable.house_of_water_bg
            "House of Wind" -> R.drawable.house_of_wind_bg
            else -> R.drawable.guild_screen_bg
        }

        Image(
            painter = painterResource(id = currentBg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = if (guildToConfirm != null) 1.0f else 0.7f
        )
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(horizontal = 0.dp, vertical = 0.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            val guilds = listOf(
                "House of Fire" to R.drawable.house_of_fire_button,
                "House of Water" to R.drawable.house_of_water_button,
                "House of Wind" to R.drawable.house_of_wind_button
            )
            
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                guilds.forEach { (guildName, assetId) ->
                    Image(
                        painter = painterResource(id = assetId),
                        contentDescription = guildName,
                        modifier = Modifier
                            .fillMaxWidth()
                            .weight(1f)
                            .graphicsLayer {
                                scaleX = 1.15f
                                scaleY = 1.15f
                            }
                            .clickable { guildToConfirm = guildName },
                        contentScale = ContentScale.Fit
                    )
                }
            }

            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .height(60.dp)
                    .clickable { onReturn() },
                contentScale = ContentScale.Fit
            )
        }

        // CONFIRMATION OVERLAY
        guildToConfirm?.let { guildName ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.85f))
                    .clickable(enabled = false) {}, // Prevent clicking through
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.85f)
                        .background(Color(0xFF1E293B), RoundedCornerShape(16.dp))
                        .border(2.dp, Color.Cyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "JOIN $guildName?",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color.White,
                        fontWeight = FontWeight.Black,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    Text(
                        text = "Are you sure? You may only join one house, and this choice is permanent.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.LightGray,
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        RpgButton(
                            text = "Yes",
                            onClick = { onJoin(guildName) },
                            modifier = Modifier.weight(1f)
                        )
                        RpgButton(
                            text = "No",
                            onClick = { guildToConfirm = null },
                            modifier = Modifier.weight(1f),
                            containerColor = Color(0xFF4B5563)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun GuildMainScreen(
    playerData: PlayerData, 
    onLearnSkill: (String, Int) -> Unit, 
    onStartExam: (GuildDatabase.GuildExam) -> Unit,
    onShowLore: () -> Unit,
    onReturn: () -> Unit
) {
    val guildName = playerData.joinedGuild ?: ""
    val guildMaster = GuildDatabase.getGuildMaster(guildName)
    val skills = GuildDatabase.getGuildSkills(guildName, playerData.playerClass)
    var selectedTab by remember { mutableStateOf(GuildTab.LEADER) }
    
    val nextXp = GuildDatabase.getGuildXpForNextLevel(playerData.guildLevel)
    val isEligibleForRankUp = (playerData.guildXp >= nextXp) && (playerData.guildLevel < 10)
    val pendingExam = if (isEligibleForRankUp) GuildDatabase.getGuildExam(guildName, playerData.guildLevel + 1) else null

    val color = when (guildName) {
        "House of Fire" -> Color(0xFFEF4444)
        "House of Water" -> Color(0xFF3B82F6)
        "House of Wind" -> Color(0xFF10B981)
        else -> Color.White
    }

    val guildBg = when (guildName) {
        "House of Fire" -> R.drawable.house_of_fire_bg
        "House of Water" -> R.drawable.house_of_water_bg
        "House of Wind" -> R.drawable.house_of_wind_bg
        else -> R.drawable.guild_screen_bg
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = guildBg),
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
            // CENTERED HEADER
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = guildName.uppercase(), 
                    color = color, 
                    style = MaterialTheme.typography.headlineMedium, 
                    fontWeight = FontWeight.Black,
                    letterSpacing = 2.sp
                )
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Lv. ${playerData.guildLevel} Member", color = Color.LightGray, style = MaterialTheme.typography.labelSmall)
                    
                    val nextXp = GuildDatabase.getGuildXpForNextLevel(playerData.guildLevel)
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "${playerData.guildXp} / $nextXp XP",
                            color = Color.Cyan,
                            style = MaterialTheme.typography.labelSmall
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        LinearProgressIndicator(
                            progress = { (playerData.guildXp.toFloat() / nextXp).coerceIn(0f, 1f) },
                            modifier = Modifier.width(80.dp).height(4.dp).clip(RoundedCornerShape(2.dp)),
                            color = Color.Cyan,
                            trackColor = Color.DarkGray
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // TAB ROW
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                GuildTab.entries.forEach { tab ->
                    val isSelected = selectedTab == tab
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clickable { selectedTab = tab }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = tab.displayName,
                            color = if (isSelected) Color.Cyan else Color.White.copy(alpha = 0.6f),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                            modifier = Modifier.graphicsLayer {
                                scaleX = if (isSelected) 1.1f else 1f
                                scaleY = if (isSelected) 1.1f else 1f
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // CONTENT AREA
            Box(modifier = Modifier.weight(1f)) {
                when (selectedTab) {
                    GuildTab.LEADER -> {
                        Column(
                            modifier = Modifier.fillMaxSize(),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            val masterPortrait = when (guildName) {
                                "House of Fire" -> R.drawable.ignis_profile
                                "House of Water" -> R.drawable.marina_profile
                                "House of Wind" -> R.drawable.zephyr_profile
                                else -> null
                            }

                            if (masterPortrait != null) {
                                Box(contentAlignment = Alignment.Center) {
                                    // Glow behind portrait
                                    Box(modifier = Modifier.size(220.dp).background(color.copy(alpha = 0.1f), CircleShape))
                                    Image(
                                        painter = painterResource(id = masterPortrait),
                                        contentDescription = "Master Portrait",
                                        modifier = Modifier
                                            .size(200.dp)
                                            .clip(CircleShape)
                                            .border(4.dp, color.copy(alpha = 0.5f), CircleShape),
                                        contentScale = ContentScale.Crop,
                                        alignment = Alignment.TopCenter
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(24.dp))

                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
                                border = BorderStroke(1.dp, color.copy(alpha = 0.3f))
                            ) {
                                Column(modifier = Modifier.padding(20.dp)) {
                                    Text("Master $guildMaster", color = color, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Black)
                                    Spacer(modifier = Modifier.height(12.dp))

                                    val dialogue = pendingExam?.introDialogue ?: GuildDatabase.getGuildMasterDialogue(guildName, playerData.guildLevel)

                                    Text(
                                        text = dialogue,
                                        color = Color.White,
                                        style = MaterialTheme.typography.bodyLarge,
                                        lineHeight = 24.sp
                                    )

                                    if (pendingExam != null) {
                                        Spacer(modifier = Modifier.height(24.dp))
                                        RpgButton(
                                            text = "Take Rank ${pendingExam.rank} Exam",
                                            onClick = { onStartExam(pendingExam) },
                                            modifier = Modifier.fillMaxWidth(),
                                            containerColor = color
                                        )
                                    }

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Lore: ${GuildDatabase.getGuildDescription(guildName)}",
                                        color = Color.Gray,
                                        style = MaterialTheme.typography.labelSmall
                                    )

                                    Spacer(modifier = Modifier.height(16.dp))
                                    Text(
                                        text = "Read House History",
                                        color = Color.Cyan.copy(alpha = 0.7f),
                                        style = MaterialTheme.typography.labelSmall,
                                        fontWeight = FontWeight.Bold,
                                        modifier = Modifier.clickable { onShowLore() }
                                    )
                                }
                            }
                        }
                    }

                    GuildTab.SKILLS -> {
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                            items(skills) { req ->
                                val isUnlocked = playerData.unlockedSkills.contains(req.skill.name)
                                val canLearn = !isUnlocked && playerData.guildLevel >= req.requiredGuildLevel && playerData.gold >= req.fee
                                
                                SkillReqRow(req, isUnlocked, canLearn) {
                                    onLearnSkill(req.skill.name, req.fee)
                                }
                            }
                        }
                    }

                    GuildTab.QUESTS -> {
                        val guildQuests = GuildDatabase.getGuildQuests(guildName)
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            item {
                                Text("Special Guild Directives", color = Color.Cyan, style = MaterialTheme.typography.titleMedium)
                            }
                            items(guildQuests) { q ->
                                GuildQuestRow(q, playerData.level)
                            }
                            item {
                                Spacer(modifier = Modifier.height(16.dp))
                                Text("Note: Progress guild levels by participating in world events and defeating enemies while a member.", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            }
                        }
                    }

                    GuildTab.RANKS -> {
                        LazyColumn(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            item { Text("Guild Progression Benefits", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                            (1..10).forEach { lvl ->
                                item {
                                    RankBenefitRow(lvl, lvl <= playerData.guildLevel, color)
                                }
                            }
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .padding(8.dp)
                    .height(80.dp)
                    .clickable { onReturn() },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun GuildQuestRow(quest: GuildDatabase.GuildQuest, playerLevel: Int) {
    val isLocked = playerLevel < quest.minLevel
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
        border = BorderStroke(1.dp, if (isLocked) Color.DarkGray else Color.Cyan.copy(alpha = 0.3f))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(quest.title, color = if (isLocked) Color.Gray else Color.White, fontWeight = FontWeight.Bold)
                if (isLocked) Text("Lv. ${quest.minLevel} REQ", color = Color.Red, style = MaterialTheme.typography.labelSmall)
            }
            Text("Eliminate ${quest.targetCount} ${quest.targetEnemy}", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Text("Reward: ${CurrencyUtils.formatGold(quest.rewardGold)}", color = Color.Yellow, style = MaterialTheme.typography.labelSmall)
                Text("+${quest.rewardGuildXp} Guild XP", color = Color.Cyan, style = MaterialTheme.typography.labelSmall)
            }
        }
    }
}

@Composable
fun RankBenefitRow(lvl: Int, isReached: Boolean, guildColor: Color) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isReached) guildColor.copy(alpha = 0.1f) else Color.Transparent, RoundedCornerShape(8.dp))
            .border(1.dp, if (isReached) guildColor.copy(alpha = 0.4f) else Color.White.copy(alpha = 0.1f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Rank $lvl", color = if (isReached) guildColor else Color.Gray, fontWeight = FontWeight.Bold, modifier = Modifier.width(60.dp))
        Spacer(modifier = Modifier.width(16.dp))
        val benefit = when (lvl) {
            1 -> "Membership established."
            2 -> "New skill available."
            3 -> "Guild discount +1%."
            5 -> "Elite Skill Tier unlocked."
            7 -> "Master's technique access."
            10 -> "Guild Champion Title."
            else -> "Increased prestige within the house."
        }
        Text(benefit, color = if (isReached) Color.White else Color.DarkGray, style = MaterialTheme.typography.bodySmall)
    }
}

@Composable
fun SkillReqRow(req: GuildSkillRequirement, isUnlocked: Boolean, canLearn: Boolean, onLearn: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.7f)),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isUnlocked) Color.Cyan.copy(alpha = 0.3f) else Color.White.copy(alpha = 0.1f))
    ) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Column(modifier = Modifier.weight(1f)) {
                Text(req.skill.name, color = if (isUnlocked) Color.Cyan else Color.White, fontWeight = FontWeight.Bold)
                Text(req.skill.description, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Text("Req: Guild Lv. ${req.requiredGuildLevel}", color = if (isUnlocked) Color.Cyan else Color.LightGray, style = MaterialTheme.typography.labelSmall)
            }
            if (isUnlocked) {
                Text("LEARNED", color = Color.Cyan, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
            } else {
                RpgButton(
                    text = CurrencyUtils.formatGold(req.fee),
                    onClick = onLearn,
                    enabled = canLearn,
                    modifier = Modifier.height(36.dp)
                )
            }
        }
    }
}
