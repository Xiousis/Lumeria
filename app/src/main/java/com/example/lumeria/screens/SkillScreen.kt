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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.lumeria.components.RpgButton
import com.example.lumeria.data.PlayerData
import com.example.lumeria.database.SkillDatabase
import com.example.lumeria.models.Skill
import com.example.lumeria.utils.MusicManager

@Composable
fun SkillScreen(
    modifier: Modifier = Modifier,
    playerData: PlayerData,
    onPlayerUpdate: (PlayerData) -> Unit,
    onReturn: () -> Unit
) {
    var selectedSlot by remember { mutableIntStateOf(0) }
    val context = LocalContext.current

    LaunchedEffect(Unit) {
        MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }
    
    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.skill_book_bg),
            contentDescription = "Skill Book Background",
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
                text = "Battle Skills",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            // EQUIPPED SLOTS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                repeat(4) { index ->
                    val skillName = playerData.equippedSkills.getOrNull(index) ?: "None"
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .height(80.dp)
                            .background(
                                if (selectedSlot == index) Color.Cyan.copy(alpha = 0.2f) else Color(0xFF616161).copy(alpha = 0.6f),
                                RoundedCornerShape(8.dp)
                            )
                            .border(
                                2.dp,
                                if (selectedSlot == index) Color.Cyan else Color.Transparent,
                                RoundedCornerShape(8.dp)
                            )
                            .clickable { selectedSlot = index }
                            .padding(8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text("Slot ${index + 1}", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                            Text(
                                text = skillName,
                                color = if (skillName == "None") Color.Gray else Color.White,
                                style = MaterialTheme.typography.bodySmall,
                                fontWeight = FontWeight.Bold,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // LEARNED SKILLS LIST
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val unlockedSkills = SkillDatabase.skills.filter { skill ->
                    skill.name in playerData.unlockedSkills ||
                    (skill.name == "Ichor Surge" && playerData.unlockedTraits.contains("Blood of the Gods")) ||
                    (skill.name == "Starlight Judgment" && playerData.unlockedTraits.contains("Eyes of the Creator")) ||
                    (skill.name == "Ancient Dominion" && playerData.unlockedTraits.contains("Will of the Ancients"))
                }
                
                items(unlockedSkills) { skill ->
                    val isEquipped = skill.name in playerData.equippedSkills && skill.name != "None"
                    val currentLevel = playerData.skillLevels[skill.name] ?: 1
                    val currentExp = playerData.skillExp[skill.name] ?: 0
                    val nextLvlExp = SkillDatabase.getExpForNextLevel(currentLevel)

                    SkillRow(
                        skill = skill,
                        isEquipped = isEquipped,
                        level = currentLevel,
                        exp = currentExp,
                        nextLevelExp = nextLvlExp,
                        onEquip = {
                            val newEquipped = playerData.equippedSkills.toMutableList()
                            // Ensure list has 4 elements
                            while (newEquipped.size < 4) newEquipped.add("None")
                            
                            if (skill.name == "None") {
                                newEquipped[selectedSlot] = "None"
                            } else {
                                // Prevent duplication by swapping
                                val existingIndex = newEquipped.indexOf(skill.name)
                                if (existingIndex != -1) {
                                    // Swap the current skill in the slot with the already equipped one
                                    val currentInSlot = newEquipped[selectedSlot]
                                    newEquipped[existingIndex] = currentInSlot
                                    newEquipped[selectedSlot] = skill.name
                                } else {
                                    newEquipped[selectedSlot] = skill.name
                                }
                            }
                            onPlayerUpdate(playerData.copy(equippedSkills = newEquipped))
                        }
                    )
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
fun SkillRow(
    skill: Skill,
    isEquipped: Boolean,
    level: Int,
    exp: Int,
    nextLevelExp: Int,
    onEquip: () -> Unit
) {
    val isMaxLevel = level >= 3

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .border(1.dp, if (isEquipped) Color.Green else Color.Gray, RoundedCornerShape(8.dp))
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = skill.name,
                        color = Color.Cyan,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Lv. $level",
                        color = if (isMaxLevel) Color.Yellow else Color.White,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }
                Text(
                    text = "Mana: ${skill.manaCost} | CD: ${skill.cooldown}",
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
            }
            
            RpgButton(
                text = if (isEquipped) "Equipped" else "Set to Slot",
                onClick = onEquip,
                enabled = !isEquipped,
                modifier = Modifier.height(36.dp)
            )
        }
        
        // MASTERY BAR
        if (!isMaxLevel) {
            val progress = exp.toFloat() / nextLevelExp
            Column(modifier = Modifier.padding(vertical = 4.dp)) {
                androidx.compose.material3.LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier.fillMaxWidth().height(4.dp).clip(RoundedCornerShape(2.dp)),
                    color = Color.Green,
                    trackColor = Color.DarkGray
                )
                Text(
                    "Mastery: $exp / $nextLevelExp uses",
                    color = Color.Gray,
                    style = MaterialTheme.typography.labelSmall
                )
            }
        } else {
            Text(
                "★ MASTERED ★",
                color = Color.Yellow,
                style = MaterialTheme.typography.labelSmall,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = skill.description,
            color = Color.LightGray,
            style = MaterialTheme.typography.bodySmall
        )
        
        if (skill.evolvesTo != null && !isMaxLevel) {
            Text(
                text = "Evolves into ${skill.evolvesTo} at Lv. 3",
                color = Color.Cyan.copy(alpha = 0.6f),
                style = MaterialTheme.typography.labelSmall,
                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
            )
        }
    }
}
