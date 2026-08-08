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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.TraitDatabase
import com.solerforge.lumeria.models.Trait
import com.solerforge.lumeria.utils.CurrencyUtils
import com.solerforge.lumeria.utils.RarityUtils

@Composable
fun ElderRitualScreen(
    modifier: Modifier = Modifier,
    playerData: PlayerData,
    onPlayerUpdate: (PlayerData) -> Unit,
    onReturn: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    val baseRespecCost = playerData.level * 100
    val respecCost = CurrencyUtils.applyDiscount(baseRespecCost, playerData.nationUpgradeLevel, playerData.activeKingdomLawId)
    val canAffordRespec = playerData.gold >= respecCost
    val hasSpentPoints = playerData.strength > 5 || playerData.vitality > 5 || playerData.defense > 5 || 
                         playerData.intelligence > 5 || playerData.agility > 5 || playerData.luck > 5 || playerData.wisdom > 5

    val baseWishCost = if (playerData.freeWishesUsed < 3) 0 else 10000
    val wishCost = CurrencyUtils.applyDiscount(baseWishCost, playerData.nationUpgradeLevel, playerData.activeKingdomLawId)
    val canAffordWish = playerData.gold >= wishCost

    var revealedTrait by remember { mutableStateOf<Trait?>(null) }
    var selectedTraitName by remember { mutableStateOf<String?>(null) }
    var showRareWarning by remember { mutableStateOf<(() -> Unit)?>(null) }

    LaunchedEffect(Unit) {
        com.solerforge.lumeria.utils.MusicManager.playMusic(context, R.raw.elders_hut_theme)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.elders_hut),
            contentDescription = "Elder's Hut Background",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(scaleX = 1.1f, scaleY = 1.1f),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "The Elder's Hut",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.Cyan,
                modifier = Modifier.padding(bottom = 16.dp)
            )

            // DIALOGUE & ACTIONS
            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(16.dp))
                            .border(2.dp, Color.Cyan.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.vil_elder),
                            contentDescription = "Village Elder",
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .border(3.dp, Color.Yellow.copy(alpha = 0.6f), CircleShape)
                                .graphicsLayer(scaleX = 1.1f, scaleY = 1.1f, translationY = 8.dp.value),
                            contentScale = ContentScale.Crop,
                            alignment = Alignment.TopCenter
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Village Elder",
                            color = Color.Yellow,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        val elderDialogue = if (playerData.freeWishesUsed < 3) {
                            "\"The first 3 wishes are on me, after that you gotta pay this old man.\""
                        } else {
                            "\"What do you seek, cub? Rebirth of your spirit, or a wish upon the ancient stars?\""
                        }

                        Text(
                            text = elderDialogue,
                            color = Color.White,
                            style = MaterialTheme.typography.bodyLarge,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // RESPEC ACTION
                        Button(
                            onClick = {
                                val resetPlayer = playerData.respec().copy(
                                    gold = playerData.gold - respecCost
                                )
                                onPlayerUpdate(resetPlayer)
                            },
                            enabled = canAffordRespec && hasSpentPoints,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Ritual of Rebirth (${CurrencyUtils.formatGold(respecCost)})")
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // WISH ACTION
                        Button(
                            onClick = {
                                val currentTraits = playerData.unlockedTraits
                                val hasRareTrait = currentTraits.any { 
                                    val t = TraitDatabase.getTrait(it)
                                    t != null && (t.rarity == "Legendary" || t.rarity == "Mythic" || t.rarity == "God Tier")
                                }

                                val performWish = {
                                    val newTrait = TraitDatabase.rollTrait()
                                    revealedTrait = newTrait
                                    // Replaces all current traits with the new one
                                    val updatedPlayer = playerData.copy(
                                        gold = playerData.gold - wishCost,
                                        freeWishesUsed = playerData.freeWishesUsed + 1,
                                        unlockedTraits = listOf(newTrait.name)
                                    )
                                    onPlayerUpdate(updatedPlayer.copy(
                                        maxHp = updatedPlayer.calculateMaxHp(),
                                        maxMana = updatedPlayer.calculateMaxMana()
                                    ))
                                    selectedTraitName = null
                                }

                                if (hasRareTrait) {
                                    showRareWarning = performWish
                                } else {
                                    performWish()
                                }
                            },
                            enabled = canAffordWish,
                            modifier = Modifier.fillMaxWidth(),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFFD600))
                        ) {
                            val costText = if (wishCost == 0) "FREE" else CurrencyUtils.formatGold(wishCost)
                            Text("Make a Wish ($costText)", color = Color.Black, fontWeight = FontWeight.Bold)
                        }

                        // RE-ROLL SELECTED ACTION
                        selectedTraitName?.let { traitToReplace ->
                            val currentTrait = TraitDatabase.getTrait(traitToReplace)
                            if (currentTrait != null) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Button(
                                    onClick = {
                                        val isRare = currentTrait.rarity == "Legendary" || currentTrait.rarity == "Mythic" || currentTrait.rarity == "God Tier"
                                        
                                        val performSingleReroll = {
                                            val newTrait = TraitDatabase.rollTrait()
                                            // Enforce 1 trait limit
                                            val updatedPlayer = playerData.copy(
                                                gold = playerData.gold - wishCost,
                                                freeWishesUsed = playerData.freeWishesUsed + 1,
                                                unlockedTraits = listOf(newTrait.name)
                                            )
                                            onPlayerUpdate(updatedPlayer.copy(
                                                maxHp = updatedPlayer.calculateMaxHp(),
                                                maxMana = updatedPlayer.calculateMaxMana()
                                            ))
                                            revealedTrait = newTrait
                                            selectedTraitName = null
                                        }

                                        if (isRare) {
                                            showRareWarning = performSingleReroll
                                        } else {
                                            performSingleReroll()
                                        }
                                    },
                                    enabled = canAffordWish,
                                    modifier = Modifier.fillMaxWidth(),
                                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF00E5FF))
                                ) {
                                    val costText = if (wishCost == 0) "FREE" else CurrencyUtils.formatGold(wishCost)
                                    Text("Re-roll ${currentTrait.name} ($costText)", color = Color.Black, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }

                // OWNED TRAITS LIST
                if (playerData.unlockedTraits.isNotEmpty()) {
                    item {
                        Text("Current Active Trait", color = Color.White, style = MaterialTheme.typography.titleMedium)
                    }
                    
                    items(playerData.unlockedTraits) { traitName ->
                        val trait = TraitDatabase.getTrait(traitName)
                        trait?.let { 
                            TraitRow(
                                trait = it,
                                isSelected = selectedTraitName == traitName,
                                onSelect = {
                                    selectedTraitName = if (selectedTraitName == traitName) null else traitName
                                }
                            ) 
                        }
                    }
                }
            }

            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .padding(vertical = 16.dp)
                    .height(80.dp)
                    .clickable { onReturn() },
                contentScale = ContentScale.Fit
            )
        }

        // TRAIT REVEAL POPUP
        revealedTrait?.let { trait ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { revealedTrait = null },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .background(Color.DarkGray, RoundedCornerShape(16.dp))
                        .border(
                            4.dp, 
                            when (trait.rarity) {
                                "God Tier" -> Color(0xFFFFD600)
                                "Mythic" -> Color(0xFF00E5FF)
                                "Legendary" -> Color(0xFFFF9800)
                                "Epic" -> Color(0xFFE040FB)
                                "Rare" -> Color(0xFF2196F3)
                                "Uncommon" -> Color(0xFF4CAF50)
                                else -> Color.White
                            }, 
                            RoundedCornerShape(16.dp)
                        )
                        .padding(32.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("THE ELDER GRANTS YOUR WISH", color = Color.Yellow, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(16.dp))
                    val rarityColor = RarityUtils.getColor(trait.rarity)
                    Text(
                        text = trait.name, 
                        color = Color.White, 
                        style = MaterialTheme.typography.headlineMedium, 
                        fontWeight = FontWeight.Black,
                        textAlign = TextAlign.Center
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Text(
                            text = "[${trait.rarity}]", 
                            color = rarityColor,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        if (trait.rarity == "God Tier") {
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("⭐", color = RarityUtils.getStarColor(), fontSize = 20.sp)
                        }
                    }
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = trait.description, 
                        color = Color.LightGray, 
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    
                    Spacer(modifier = Modifier.height(32.dp))
                    
                    Button(
                        onClick = {
                            val isRareResult = trait.rarity == "Legendary" || trait.rarity == "Mythic" || trait.rarity == "God Tier"
                            
                            val performReroll = {
                                val newTrait = TraitDatabase.rollTrait()
                                // Replaces current trait with the new one
                                val updatedPlayer = playerData.copy(
                                    gold = playerData.gold - wishCost,
                                    freeWishesUsed = playerData.freeWishesUsed + 1,
                                    unlockedTraits = listOf(newTrait.name)
                                )
                                onPlayerUpdate(updatedPlayer.copy(
                                    maxHp = updatedPlayer.calculateMaxHp(),
                                    maxMana = updatedPlayer.calculateMaxMana()
                                ))
                                revealedTrait = newTrait
                            }

                            if (isRareResult) {
                                showRareWarning = performReroll
                            } else {
                                performReroll()
                            }
                        },
                        enabled = canAffordWish,
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Yellow),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val costText = if (wishCost == 0) "FREE" else CurrencyUtils.formatGold(wishCost)
                        Text("Re-roll Result ($costText)", color = Color.Black)
                    }

                    Spacer(modifier = Modifier.height(12.dp))
                    Text("Tap background to close", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                }
            }
        }

        // RARE TRAIT WARNING
        showRareWarning?.let { action ->
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { showRareWarning = null },
                confirmButton = {
                    androidx.compose.material3.TextButton(
                        onClick = {
                            action()
                            showRareWarning = null
                        }
                    ) {
                        Text("YES, PROCEED", color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    androidx.compose.material3.TextButton(onClick = { showRareWarning = null }) {
                        Text("CANCEL", color = Color.White)
                    }
                },
                title = { Text("REPLACE RARE TRAIT?", fontWeight = FontWeight.Black, color = Color.Yellow) },
                text = { Text("You currently have a Legendary or higher rarity trait. If you continue, it will be lost forever. Are you absolutely sure?", color = Color.White) },
                containerColor = Color(0xFF1E293B),
                textContentColor = Color.White
            )
        }
    }
}

@Composable
fun TraitRow(
    trait: Trait,
    isSelected: Boolean,
    onSelect: () -> Unit
) {
    val rarityColor = RarityUtils.getColor(trait.rarity)

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                if (isSelected) rarityColor.copy(alpha = 0.2f) else Color.Black.copy(alpha = 0.4f), 
                RoundedCornerShape(8.dp)
            )
            .border(
                if (isSelected) 2.dp else 1.dp, 
                if (isSelected) rarityColor else rarityColor.copy(alpha = 0.3f), 
                RoundedCornerShape(8.dp)
            )
            .clickable { onSelect() }
            .padding(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = trait.name, color = rarityColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    if (trait.rarity == "God Tier") {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("⭐", color = RarityUtils.getStarColor(), fontSize = 14.sp)
                    }
                }
                Text(text = trait.description, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
            }
            
            if (isSelected) {
                Text("SELECTED", color = Color.Yellow, style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
            }
        }
    }
}
