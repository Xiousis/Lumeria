package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.border
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.GameDatabase
import com.solerforge.lumeria.utils.CurrencyUtils

@Composable
fun BlacksmithScreen(
    playerData: PlayerData,
    onPlayerUpdate: (PlayerData) -> Unit,
    onReturn: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    var message by remember { mutableStateOf("Back again? Let's see if we can make that iron actually useful.") }

    LaunchedEffect(Unit) {
        com.solerforge.lumeria.utils.MusicManager.playMusic(context, R.raw.forge_theme)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.forge_screen_bg),
            contentDescription = "Forge Background",
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
            // KAZUFI PORTRAIT
            Image(
                painter = painterResource(id = R.drawable.kazufi_profile),
                contentDescription = "Kazufi Portrait",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color(0xFFFF9800).copy(alpha = 0.6f), CircleShape),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            Text(
                "Kazufi the Smith",
                style = MaterialTheme.typography.headlineMedium,
                color = Color(0xFFFF9800),
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 8.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                    .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
                    .padding(16.dp)
            ) {
                Text(
                    text = "\"$message\"",
                    style = MaterialTheme.typography.bodyMedium,
                    color = Color.White,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(),
                    fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                )
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text("Your Gold:", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                Text(CurrencyUtils.formatGold(playerData.gold), color = Color.Yellow, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text("Select Equipment to Upgrade (+5 Max)", color = Color.LightGray, style = MaterialTheme.typography.labelLarge)

            LazyColumn(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)
            ) {
                val equippables = listOf(
                    "Weapon" to playerData.equippedWeapon,
                    "Armor" to playerData.equippedArmor,
                    "Head" to playerData.equippedHead,
                    "Boots" to playerData.equippedBoots,
                    "Shield" to playerData.equippedShield,
                    "Off-Hand 1" to playerData.equippedOffHand,
                    "Off-Hand 2" to playerData.equippedOffHand2
                )

                items(equippables) { (type, itemName) ->
                    if (itemName != "None") {
                        UpgradeRow(
                            type = type,
                            name = itemName,
                            playerGold = playerData.gold,
                            nationUpgradeLevel = playerData.nationUpgradeLevel,
                            activeLawId = playerData.activeKingdomLawId,
                            inventory = playerData.inventory,
                            onUpgrade = { cost, material ->
                                val currentLevel = itemName.substringAfterLast(" +", "0").toIntOrNull() ?: 0
                                val isMaxRegular = currentLevel >= 5
                                val isVoidTouched = itemName.contains("Void-Touched")
                                val isEternal = itemName.contains("Eternal")

                                val newName = when {
                                    isEternal -> itemName
                                    isVoidTouched && isMaxRegular -> "Eternal ${itemName.substringAfter("Void-Touched ")}"
                                    isMaxRegular -> "Void-Touched $itemName"
                                    else -> {
                                        val base = itemName.substringBeforeLast(" +")
                                        "$base +${currentLevel + 1}"
                                    }
                                }
                                
                                val updatedInventory = playerData.inventory.toMutableList()
                                
                                // Remove material if required
                                material?.let { updatedInventory.remove(it) }

                                val index = updatedInventory.indexOf(itemName)
                                if (index != -1) {
                                    updatedInventory[index] = newName
                                }

                                val newPlayer = when (type) {
                                    "Weapon" -> playerData.copy(equippedWeapon = newName, inventory = updatedInventory, gold = playerData.gold - cost, blacksmithUpgrades = playerData.blacksmithUpgrades + 1).recalculateVitals()
                                    "Armor" -> playerData.copy(equippedArmor = newName, inventory = updatedInventory, gold = playerData.gold - cost, blacksmithUpgrades = playerData.blacksmithUpgrades + 1).recalculateVitals()
                                    "Head" -> playerData.copy(equippedHead = newName, inventory = updatedInventory, gold = playerData.gold - cost, blacksmithUpgrades = playerData.blacksmithUpgrades + 1).recalculateVitals()
                                    "Boots" -> playerData.copy(equippedBoots = newName, inventory = updatedInventory, gold = playerData.gold - cost, blacksmithUpgrades = playerData.blacksmithUpgrades + 1).recalculateVitals()
                                    "Shield" -> playerData.copy(equippedShield = newName, inventory = updatedInventory, gold = playerData.gold - cost, blacksmithUpgrades = playerData.blacksmithUpgrades + 1).recalculateVitals()
                                    "Off-Hand 1" -> playerData.copy(equippedOffHand = newName, inventory = updatedInventory, gold = playerData.gold - cost, blacksmithUpgrades = playerData.blacksmithUpgrades + 1).recalculateVitals()
                                    "Off-Hand 2" -> playerData.copy(equippedOffHand2 = newName, inventory = updatedInventory, gold = playerData.gold - cost, blacksmithUpgrades = playerData.blacksmithUpgrades + 1).recalculateVitals()
                                    else -> playerData
                                }
                                onPlayerUpdate(newPlayer)
                                message = when {
                                    isEternal -> "You've reached the peak, Hero. This companion is immortal."
                                    isVoidTouched && isMaxRegular -> "By the gods... it's ASCENDED! Witness the Eternal power!"
                                    isMaxRegular -> "The void is cold, but its edge is sharp. Your gear is now Void-Touched!"
                                    else -> "Good as new! Or well... better than new! Haha!"
                                }
                            }
                        )
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
fun UpgradeRow(
    type: String,
    name: String,
    playerGold: Long,
    nationUpgradeLevel: Int,
    activeLawId: Int?,
    inventory: List<String>,
    onUpgrade: (Long, String?) -> Unit
) {
    val level = name.substringAfterLast(" +", "0").toIntOrNull() ?: 0
    val isVoidTouched = name.contains("Void-Touched")
    val isEternal = name.contains("Eternal")
    val isMaxRegular = level >= 5
    
    val baseCost = when {
        isEternal -> 0L
        isVoidTouched && isMaxRegular -> 500000L // To Eternal
        isMaxRegular -> 150000L // To Void-Touched
        else -> when (level) {
            0 -> 1000L
            1 -> 5000L
            2 -> 15000L
            3 -> 35000L
            4 -> 60000L
            else -> 0L
        }
    }
    
    val cost = CurrencyUtils.applyDiscount(baseCost, nationUpgradeLevel, activeLawId)
    val canAffordGold = playerGold >= cost

    val baseItemName = GameDatabase.getBaseName(name)
    val requiredLocation = when {
        type == "Weapon" -> GameDatabase.weapons.find { it.name == baseItemName }?.requiredLocation
        type == "Armor" -> GameDatabase.armors.find { it.name == baseItemName }?.requiredLocation
        type == "Head" -> GameDatabase.headGears.find { it.name == baseItemName }?.requiredLocation
        type == "Boots" -> GameDatabase.boots.find { it.name == baseItemName }?.requiredLocation
        type == "Shield" -> GameDatabase.shields.find { it.name == baseItemName }?.requiredLocation
        type.contains("Off-Hand") -> GameDatabase.offHands.find { it.name == baseItemName }?.requiredLocation
        else -> null
    } ?: run {
        // Fallback search across all lists
        GameDatabase.weapons.find { it.name == baseItemName }?.requiredLocation ?:
        GameDatabase.armors.find { it.name == baseItemName }?.requiredLocation ?:
        GameDatabase.headGears.find { it.name == baseItemName }?.requiredLocation ?:
        GameDatabase.boots.find { it.name == baseItemName }?.requiredLocation ?:
        GameDatabase.shields.find { it.name == baseItemName }?.requiredLocation ?:
        GameDatabase.offHands.find { it.name == baseItemName }?.requiredLocation
    } ?: "Training Fields"

    val requiredMaterial = if (isMaxRegular) "Void Shard" else GameDatabase.getRequiredMaterial(requiredLocation)
    val hasMaterial = requiredMaterial == null || inventory.contains(requiredMaterial)
    val canAfford = canAffordGold && hasMaterial && !isEternal

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(if (isEternal) Color(0xFFFACC15).copy(alpha = 0.1f) else if (isVoidTouched) Color(0xFFA855F7).copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.3f))
            .border(
                2.dp, 
                if (isEternal) Color(0xFFFACC15).copy(alpha = 0.5f) else if (isVoidTouched) Color(0xFFA855F7).copy(alpha = 0.5f) else Color.White.copy(alpha = 0.1f), 
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(type, style = MaterialTheme.typography.labelSmall, color = Color.Gray)
                Text(
                    text = name, 
                    style = MaterialTheme.typography.bodyLarge, 
                    color = if (isEternal) Color(0xFFFACC15) else if (isVoidTouched) Color(0xFFA855F7) else Color.White, 
                    fontWeight = FontWeight.Bold
                )
                
                if (requiredMaterial != null && !isEternal) {
                    val count = inventory.count { it == requiredMaterial }
                    Text(
                        text = "Req: $requiredMaterial ($count/1)", 
                        color = if (hasMaterial) Color.Green else Color.Red, 
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                when {
                    isEternal -> Text("★ IMMORTAL LEGEND ★", color = Color(0xFFFACC15), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Black)
                    isVoidTouched && isMaxRegular -> Text("Ready for Ascension (+80% Bonus)", color = Color.Cyan, style = MaterialTheme.typography.labelSmall)
                    isMaxRegular -> Text("Ready for Reforging (+50% Bonus)", color = Color(0xFFA855F7), style = MaterialTheme.typography.labelSmall)
                    else -> Text("Bonus: +${(level + 1) * 2}% Total Stats", color = Color.Green, style = MaterialTheme.typography.labelSmall)
                }
            }

            if (!isEternal) {
                Button(
                    onClick = { onUpgrade(cost, requiredMaterial) },
                    enabled = canAfford,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (isMaxRegular) Color(0xFF7B1FA2) else if (canAfford) Color(0xFF43A047) else Color.DarkGray
                    ),
                    modifier = Modifier.height(44.dp)
                ) {
                    val label = if (isVoidTouched && isMaxRegular) "Ascend" else if (isMaxRegular) "Reforge" else "Upgrade"
                    Text("${CurrencyUtils.formatGold(cost)} $label", fontSize = 10.sp)
                }
            }
        }
    }
}
