package com.example.lumeria.screens

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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumeria.R
import com.example.lumeria.components.ShopItemRow
import com.example.lumeria.data.PlayerData
import com.example.lumeria.database.FamiliarDatabase
import com.example.lumeria.models.FamiliarActionType
import com.example.lumeria.utils.CurrencyUtils
import com.example.lumeria.utils.MusicManager

@Composable
fun FamiliarStoreScreen(
    modifier: Modifier = Modifier,
    playerData: PlayerData,
    onPlayerUpdate: (PlayerData) -> Unit,
    onReturn: () -> Unit,
) {
    var shopSnapshot by remember(playerData) { mutableStateOf(playerData) }
    val context = LocalContext.current
    
    LaunchedEffect(Unit) {
        // Using Billy's theme for now or a generic one if available
        MusicManager.playMusic(context, R.raw.billys_general_store_theme)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.billys_store_bg), // Reusing Billy's BG for now
            contentDescription = "Familiar Nursery Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.6f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // NURSERY KEEPER PORTRAIT (Using Yumi for now as a placeholder)
            Image(
                painter = painterResource(id = R.drawable.yumi),
                contentDescription = "Familiar Keeper",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color.Magenta.copy(alpha = 0.6f), CircleShape),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            Text(
                text = "The Familiar Nursery",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
            )

            Text(
                text = "\"Find a loyal companion to aid you in your journey. They require a lot of care, but their help is invaluable.\"",
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
            )
            
            Text(
                text = "Gold: ${CurrencyUtils.formatGold(shopSnapshot.gold)}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Yellow
            )

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                val buyableFamiliars = FamiliarDatabase.familiars.filter { it.price > 0 }
                
                items(buyableFamiliars) { familiar ->
                    val isEquipped = shopSnapshot.equippedFamiliar == familiar.name
                    // Check if already owned (in familiarExp or levels map)
                    val isOwned = shopSnapshot.familiarLevels.containsKey(familiar.name)
                    
                    val actionDesc = when(familiar.actionType) {
                        FamiliarActionType.Damage -> "Deals ${familiar.basePower} Damage"
                        FamiliarActionType.Heal -> "Heals ${familiar.basePower} HP"
                        FamiliarActionType.Mana -> "Restores ${familiar.basePower} MP"
                        FamiliarActionType.Buff -> "Boosts ATK by 25%"
                        FamiliarActionType.Debuff -> "Distracts Enemy"
                    }

                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                        border = BorderStroke(1.dp, if (isEquipped) Color.Cyan else Color.Gray.copy(alpha = 0.3f))
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(familiar.name, fontWeight = FontWeight.Bold, color = if (isOwned) Color.Green else Color.White)
                                Text(familiar.description, style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Badge(containerColor = Color.Magenta.copy(alpha = 0.2f)) {
                                        Text(actionDesc, color = Color.Magenta, fontSize = 10.sp)
                                    }
                                    Badge(containerColor = Color.DarkGray) {
                                        Text("Interval: ${familiar.supportInterval}t", color = Color.White, fontSize = 10.sp)
                                    }
                                }
                            }
                            
                            if (isOwned) {
                                Button(
                                    onClick = {
                                        shopSnapshot = shopSnapshot.copy(equippedFamiliar = familiar.name)
                                        onPlayerUpdate(shopSnapshot)
                                    },
                                    enabled = !isEquipped,
                                    colors = ButtonDefaults.buttonColors(containerColor = if (isEquipped) Color.DarkGray else Color.Blue)
                                ) {
                                    Text(if (isEquipped) "Equipped" else "Equip", fontSize = 12.sp)
                                }
                            } else {
                                Button(
                                    onClick = {
                                        if (shopSnapshot.gold >= familiar.price) {
                                            shopSnapshot = shopSnapshot.copy(
                                                gold = shopSnapshot.gold - familiar.price,
                                                familiarLevels = shopSnapshot.familiarLevels + (familiar.name to 1),
                                                familiarExp = shopSnapshot.familiarExp + (familiar.name to 0),
                                                equippedFamiliar = familiar.name
                                            )
                                            onPlayerUpdate(shopSnapshot)
                                        }
                                    },
                                    enabled = shopSnapshot.gold >= familiar.price,
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF43A047))
                                ) {
                                    Text(CurrencyUtils.formatGold(familiar.price), fontSize = 12.sp)
                                }
                            }
                        }
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color.Transparent)
                    ) {
                        // Empty space or extra info
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
