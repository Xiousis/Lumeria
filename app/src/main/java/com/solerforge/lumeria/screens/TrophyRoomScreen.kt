package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.components.RpgButton
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.BountyDatabase

data class Trophy(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val color: Color,
    val isUnlocked: Boolean
)

@Composable
fun TrophyRoomScreen(
    playerData: PlayerData,
    onReturn: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    androidx.compose.runtime.LaunchedEffect(Unit) {
        com.solerforge.lumeria.utils.MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.hall_of_heroes_bg),
            contentDescription = "Hall of Heroes Background",
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
                text = "Hall of Heroes",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFFFD700),
                fontWeight = FontWeight.Black,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center
            )

            Text(
                text = "A testament to the legend of Xious.",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp),
                textAlign = TextAlign.Center
            )

            val scrollState = rememberScrollState()
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(scrollState)
            ) {
                TrophySection("World Boss Spoils", getWorldBossTrophies(playerData))
                Spacer(modifier = Modifier.height(24.dp))
                
                TrophySection("Bounty Tokens", getBountyTrophies(playerData))
                Spacer(modifier = Modifier.height(24.dp))
                
                TrophySection("Grand Mastery Medals", getMasteryTrophies(playerData))
                Spacer(modifier = Modifier.height(24.dp))
            }

            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .padding(top = 16.dp)
                    .height(80.dp)
                    .clickable { onReturn() },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun TrophySection(title: String, trophies: List<Trophy>) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = Color.Cyan,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 12.dp)
        )
        
        Row(
            modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            trophies.forEach { trophy ->
                TrophyPedestal(trophy)
            }
        }
    }
}

@Composable
fun TrophyPedestal(trophy: Trophy) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(100.dp)
    ) {
        Box(
            modifier = Modifier
                .size(80.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(if (trophy.isUnlocked) Color.Black.copy(alpha = 0.6f) else Color.Black.copy(alpha = 0.2f))
                .border(
                    2.dp, 
                    if (trophy.isUnlocked) trophy.color.copy(alpha = 0.6f) else Color.Gray.copy(alpha = 0.2f), 
                    RoundedCornerShape(12.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            if (trophy.isUnlocked) {
                Text(trophy.icon, fontSize = 32.sp)
                // Glow effect for unlocked
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.radialGradient(
                                colors = listOf(trophy.color.copy(alpha = 0.1f), Color.Transparent)
                            )
                        )
                )
            } else {
                Text("?", color = Color.DarkGray, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        
        Text(
            text = if (trophy.isUnlocked) trophy.name else "???",
            style = MaterialTheme.typography.labelSmall,
            color = if (trophy.isUnlocked) Color.White else Color.DarkGray,
            textAlign = TextAlign.Center,
            maxLines = 2,
            minLines = 2
        )
    }
}

fun getWorldBossTrophies(playerData: PlayerData): List<Trophy> {
    val bosses = listOf(
        Triple("Training Captain", "Bulwark of Heroes", "🛡️"),
        Triple("Goblin King", "Coin of the Tyrant", "💰"),
        Triple("Crystal Guardian", "Starshard Edge", "🗡️"),
        Triple("Stone Titan", "Aegis of Mountains", "🏔️"),
        Triple("Marsh Horror", "Bogwalker Greaves", "🥾"),
        Triple("Ancient Dragon", "Fang of Eternity", "🩸"),
        Triple("Lord Xarthos", "Endbringer", "🌌")
    )
    
    return bosses.map { (id, name, icon) ->
        Trophy(
            id = id,
            name = name,
            description = "The legendary God Tier reward from $id.",
            icon = icon,
            color = Color(0xFFFACC15), // God Tier Yellow
            isUnlocked = playerData.inventory.contains(name)
        )
    }
}

fun getBountyTrophies(playerData: PlayerData): List<Trophy> {
    return BountyDatabase.bounties.map { bounty ->
        Trophy(
            id = "bounty_${bounty.id}",
            name = bounty.targetName,
            description = "Claim the bounty on ${bounty.targetName}.",
            icon = "📜",
            color = Color.Red,
            isUnlocked = playerData.completedBountyIds.contains(bounty.id)
        )
    }
}

fun getMasteryTrophies(playerData: PlayerData): List<Trophy> {
    // Only show medals for actual Grand Masters (1000+ kills)
    val grandMasters = playerData.killCounts.filter { it.value >= 1000 }
    
    return grandMasters.map { (name, _) ->
        Trophy(
            id = "mastery_$name",
            name = "$name Medal",
            description = "Achieve Grand Mastery for $name.",
            icon = "🏅",
            color = Color(0xFFFACC15),
            isUnlocked = true
        )
    }.ifEmpty {
        listOf(Trophy("none", "Empty", "", "", Color.Gray, false))
    }
}
