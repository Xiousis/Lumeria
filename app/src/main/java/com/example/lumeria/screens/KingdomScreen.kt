package com.example.lumeria.screens

import com.example.lumeria.R

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumeria.data.PlayerData
import com.example.lumeria.database.KingdomRankDatabase
import com.example.lumeria.utils.CurrencyUtils

@Composable
fun KingdomScreen(
    playerData: PlayerData,
    onUpgradeNation: () -> Unit,
    onClaimReward: (Int) -> Unit,
    onProclamations: () -> Unit,
    onReturn: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val currentRank = KingdomRankDatabase.getCurrentRank(playerData.renown)
    val nextRank = KingdomRankDatabase.getNextRank(playerData.renown)
    val activeLaw = com.example.lumeria.database.KingdomLawDatabase.getLaw(playerData.activeKingdomLawId)

    LaunchedEffect(Unit) {
        com.example.lumeria.utils.MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) {
        Image(
            painter = painterResource(id = R.drawable.royal_court_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.2f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "The Royal Court",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFFACC15),
                fontWeight = FontWeight.Black
            )

            Spacer(modifier = Modifier.height(16.dp))

            // KING BOX
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(250.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(Color.Black.copy(alpha = 0.5f))
                    .border(2.dp, Color(0xFFFACC15).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.king_alaric_v2),
                    contentDescription = "King Alaric",
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer(scaleX = 1.3f, scaleY = 1.3f, translationY = -10f),
                    contentScale = ContentScale.Fit,
                    alignment = Alignment.Center
                )
                
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            androidx.compose.ui.graphics.Brush.verticalGradient(
                                colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.4f))
                            )
                        )
                )

                Column(
                    modifier = Modifier.fillMaxSize().padding(16.dp),
                    verticalArrangement = Arrangement.Bottom
                ) {
                    Text("King Alaric", color = Color.Cyan, fontWeight = FontWeight.Black, style = MaterialTheme.typography.titleLarge)
                    val kingGreeting = when {
                        playerData.renown >= 500000 -> "\"By the ancient stars... you have done it. Xious, Hero of Lumeria, your name will echo through the halls of history for all eternity!\""
                        playerData.renown >= 250000 -> "\"The realm is saved by your hand, Legendary Hero. Lumeria shall never forget the name Xious.\""
                        playerData.renown >= 50000 -> "\"Protector, your dedication to our nation is a beacon of hope in these dark times.\""
                        playerData.renown >= 5000 -> "\"Welcome, Adventurer. I see the fire of Lumeria burning brightly within you.\""
                        else -> "\"Rise, ${playerData.currentTitle}. Your deeds in Lumeria do not go unnoticed.\""
                    }
                    Text(
                        text = kingGreeting,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium,
                        fontStyle = androidx.compose.ui.text.font.FontStyle.Italic,
                        lineHeight = 20.sp
                    )

                    if (playerData.renown >= 7500) {
                        Spacer(modifier = Modifier.height(12.dp))
                        androidx.compose.material3.TextButton(
                            onClick = onProclamations,
                            modifier = Modifier.align(Alignment.End).background(Color(0xFFFACC15).copy(alpha = 0.1f), RoundedCornerShape(8.dp))
                        ) {
                            Text("KINGDOM LAWS", color = Color(0xFFFACC15), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            // ACTIVE LAW HUD
            if (activeLaw != null) {
                Spacer(modifier = Modifier.height(8.dp))
                androidx.compose.material3.Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF1E293B).copy(alpha = 0.8f),
                    shape = RoundedCornerShape(8.dp),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFACC15).copy(alpha = 0.3f))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("📜", fontSize = 20.sp)
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text("Active Law: ${activeLaw.name}", color = Color(0xFFFACC15), style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
                            Text("${activeLaw.positiveEffect} | ${activeLaw.tradeOff}", color = Color.LightGray, style = MaterialTheme.typography.labelSmall)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // RENOWN BAR
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text("Popularity: ${currentRank.name}", color = currentRank.color, style = MaterialTheme.typography.titleSmall)
                    Text("${playerData.renown} RP", color = Color.White, style = MaterialTheme.typography.titleSmall)
                }
                
                val progress = if (nextRank != null) {
                    (playerData.renown - currentRank.requiredRenown).toFloat() / (nextRank.requiredRenown - currentRank.requiredRenown)
                } else 1.0f

                LinearProgressIndicator(
                    progress = { progress },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(12.dp)
                        .padding(vertical = 4.dp)
                        .clip(RoundedCornerShape(6.dp)),
                    color = currentRank.color,
                    trackColor = Color.DarkGray
                )
                
                if (nextRank != null) {
                    Text(
                        "Next: ${nextRank.name} at ${nextRank.requiredRenown} RP",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.align(Alignment.End)
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            // NATION UPGRADES
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1E293B)),
                border = BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Nation Prosperity", color = Color.Cyan, fontWeight = FontWeight.Bold)
                    Text(
                        "Current Discount: ${playerData.nationUpgradeLevel}% in all shops.",
                        color = Color.Green,
                        style = MaterialTheme.typography.labelSmall
                    )
                    
                    Spacer(modifier = Modifier.height(12.dp))
                    
                    if (playerData.nationUpgradeLevel < 10) {
                        val cost = KingdomRankDatabase.getNationUpgradeCost(playerData.nationUpgradeLevel)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Upgrade Level ${playerData.nationUpgradeLevel + 1}", color = Color.White, style = MaterialTheme.typography.bodyMedium)
                                Text("Cost: ${CurrencyUtils.formatGold(cost)}", color = Color.Yellow, style = MaterialTheme.typography.labelSmall)
                            }
                            Button(
                                onClick = onUpgradeNation,
                                enabled = playerData.gold >= cost,
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF0284C7))
                            ) {
                                Text("Fund Upgrade", fontSize = 10.sp)
                            }
                        }
                    } else {
                        Text("NATION AT MAXIMUM PROSPERITY", color = Color.Cyan, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth())
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // RANK REWARDS
            Text("Available Rank Rewards", color = Color.LightGray, style = MaterialTheme.typography.labelLarge, modifier = Modifier.align(Alignment.Start))
            
            LazyColumn(modifier = Modifier.weight(1f).fillMaxWidth()) {
                val claimable = KingdomRankDatabase.ranks.filter { 
                    it.id > 0 && it.requiredRenown <= playerData.renown && !playerData.claimedRankIds.contains(it.id) 
                }
                
                if (claimable.isEmpty()) {
                    item {
                        Text("No rewards to claim. Increase your renown!", color = Color.DarkGray, textAlign = TextAlign.Center, modifier = Modifier.fillMaxWidth().padding(top = 20.dp))
                    }
                } else {
                    items(claimable) { rank ->
                        RewardRow(rank) { onClaimReward(rank.id) }
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

@Composable
fun RewardRow(rank: com.example.lumeria.database.KingdomRank, onClaim: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .height(IntrinsicSize.Min)
    ) {
        Image(
            painter = painterResource(id = R.drawable.menu_panel_normal),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillBounds
        )

        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(rank.name, color = rank.color, fontWeight = FontWeight.Bold)
                Text(
                    text = buildString {
                        append("${CurrencyUtils.formatGold(rank.rewardGold)} Gold & ${rank.rewardXp} XP")
                        if (rank.rewardTitle != null) append(" & Title: [${rank.rewardTitle}]")
                    },
                    color = Color.Green, 
                    style = MaterialTheme.typography.labelSmall
                )
            }
            Button(onClick = onClaim, colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF10B981))) {
                Text("Claim", fontSize = 10.sp)
            }
        }
    }
}
