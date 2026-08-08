package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.GameDatabase
import com.solerforge.lumeria.components.StatAllocationRow
import com.solerforge.lumeria.models.XiousStats
import com.solerforge.lumeria.utils.CurrencyUtils
import com.solerforge.lumeria.utils.MusicManager
import com.solerforge.lumeria.utils.RarityUtils

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun StatsScreen(
    modifier: Modifier = Modifier,
    playerData: PlayerData,
    onPlayerUpdate: (PlayerData) -> Unit,
    onReturn: () -> Unit,
) {
    val context = LocalContext.current
    var helpItem by remember { mutableStateOf<Pair<String, String>?>(null) }
    
    LaunchedEffect(Unit) {
        MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.stats_screen_bg),
            contentDescription = "Stats Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.35f)),
        )

        // UI Container
        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Character Stats",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp),
            )

            LazyColumn(
                modifier = Modifier.weight(1f).fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // PERMANENT TRAITS SECTION
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.White.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Permanent Traits", color = Color.Yellow, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        if (playerData.unlockedTraits.isEmpty()) {
                            Text(
                                "No traits unlocked yet.\nVisit the Elder to make a wish.",
                                color = Color.Gray,
                                style = MaterialTheme.typography.bodySmall,
                                textAlign = androidx.compose.ui.text.style.TextAlign.Center
                            )
                        } else {
                            playerData.unlockedTraits.forEach { traitName ->
                                val trait = com.solerforge.lumeria.database.TraitDatabase.getTrait(traitName)
                                trait?.let {
                                    val rarityColor = RarityUtils.getColor(it.rarity)
                                    
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(vertical = 4.dp)
                                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
                                            .padding(10.dp)
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Text(it.name, color = rarityColor, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyLarge)
                                            if (it.rarity == "God Tier") {
                                                Spacer(modifier = Modifier.width(4.dp))
                                                Text("⭐", color = RarityUtils.getStarColor(), fontSize = 14.sp)
                                            }
                                        }
                                        Text(it.description, color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                                    }
                                }
                            }
                        }
                    }
                }

                // Info Panel
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.DarkGray.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text(playerData.currentTitle, color = Color.Yellow, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.ExtraBold)
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Xious", color = Color.White, style = MaterialTheme.typography.headlineSmall)
                            if (playerData.level >= 100) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("🏆", style = MaterialTheme.typography.headlineSmall)
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        Text("Level: ${playerData.level}", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        
                        val isMaxLevel = playerData.level >= playerData.getLevelCap()
                        val requiredXp = XiousStats.getRequiredXp(playerData.level)
                        val progress = if (isMaxLevel) 1f else playerData.xp.toFloat() / requiredXp.toFloat()
                        
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        // XP Bar
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(0.8f)
                                .height(12.dp)
                                .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(6.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                        ) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth(progress.coerceIn(0f, 1f))
                                    .fillMaxHeight()
                                    .background(
                                        if (isMaxLevel) Color(0xFFFFD700) else Color.Cyan,
                                        RoundedCornerShape(6.dp)
                                    )
                            )
                        }
                        
                        if (isMaxLevel) {
                            Text("MAX LEVEL REACHED", color = Color(0xFFFFD700), style = MaterialTheme.typography.labelSmall, fontWeight = FontWeight.Bold)
                        } else {
                            Text(
                                "${CurrencyUtils.formatNumber(playerData.xp)} / ${CurrencyUtils.formatNumber(requiredXp)} XP",
                                color = Color.LightGray,
                                style = MaterialTheme.typography.labelSmall
                            )
                            Text(
                                "${CurrencyUtils.formatNumber(requiredXp - playerData.xp)} XP until Level ${playerData.level + 1}",
                                color = Color.Yellow.copy(alpha = 0.8f),
                                style = MaterialTheme.typography.labelSmall
                            )
                        }
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Gold: ${CurrencyUtils.formatGold(playerData.gold)}", color = Color.Yellow)
                        
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Available Stat Points: ${playerData.statPoints}", color = Color.Green, style = MaterialTheme.typography.titleLarge)
                    }
                }

                // ATTRIBUTE INFO PANEL
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .border(1.dp, Color.Gray.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(16.dp)
                    ) {
                        Text("Attribute Guide", color = Color.Yellow, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(8.dp))
                        
                        AttributeDetail("Strength", "Increases Physical DMG (+2 per point).")
                        AttributeDetail("Vitality", "+50 Max HP & +50 instant heal per point.")
                        AttributeDetail("Defense", "Reduces incoming damage from all sources.")
                        AttributeDetail("Intelligence", "Increases Magic DMG & +10 Max Mana (+2 per point).")
                        AttributeDetail("Agility", "+2% Crit Chance & +1% Dodge Chance (+2 per point).")
                        AttributeDetail("Luck", "+0.5% Crit, +0.2% Dodge & +1% Gold per point.")
                        AttributeDetail("Wisdom", "Increases Mana Regen per turn (+1 regen per 5 points).")
                    }
                }

                item { Spacer(modifier = Modifier.height(16.dp)) }

                // ALLOCATION ROWS
                item {
                    StatAllocationRow(
                        label = "Strength", 
                        value = playerData.strength, 
                        description = "Physical Damage", 
                        canIncrease = playerData.statPoints > 0,
                        onHelp = { helpItem = "Strength (STR)" to "Increases physical damage dealt by 2 per point. Higher strength allows you to crush enemies with brute force." },
                        onIncrease = {
                            onPlayerUpdate(playerData.copy(strength = playerData.strength + 2, statPoints = playerData.statPoints - 1).recalculateVitals())
                        }
                    )
                }
                
                item {
                    val currentMaxHp = playerData.calculateMaxHp()
                    StatAllocationRow(
                        label = "Vitality", 
                        value = playerData.vitality, 
                        description = "Max HP (Total: $currentMaxHp)", 
                        canIncrease = playerData.statPoints > 0,
                        onHelp = { helpItem = "Vitality (VIT)" to "Each point grants +10 Max HP. High vitality is essential for surviving the devastating 'Warrior Arts' used by bosses." },
                        onIncrease = {
                            val updated = playerData.copy(vitality = playerData.vitality + 5, statPoints = playerData.statPoints - 1).recalculateVitals()
                            onPlayerUpdate(updated.copy(hp = minOf(playerData.hp + 50, updated.maxHp)))
                        }
                    )
                }

                item {
                    StatAllocationRow(
                        label = "Defense", 
                        value = playerData.defense, 
                        description = "Reduction Stat", 
                        canIncrease = playerData.statPoints > 0,
                        onHelp = { helpItem = "Defense (DEF)" to "Reduces incoming damage from all sources. Higher defense makes you harder to take down." },
                        onIncrease = {
                            onPlayerUpdate(playerData.copy(defense = playerData.defense + 2, statPoints = playerData.statPoints - 1).recalculateVitals())
                        }
                    )
                }

                item {
                    val currentMaxMana = playerData.calculateMaxMana()
                    StatAllocationRow(
                        label = "Intelligence", 
                        value = playerData.intelligence, 
                        description = "Mana & Magic (Total MP: $currentMaxMana)", 
                        canIncrease = playerData.statPoints > 0,
                        onHelp = { helpItem = "Intelligence (INT)" to "Increases magic damage and grants +5 Max Mana per point. Crucial for mages using high-tier elemental skills." },
                        onIncrease = {
                            val updated = playerData.copy(intelligence = playerData.intelligence + 2, statPoints = playerData.statPoints - 1).recalculateVitals()
                            onPlayerUpdate(updated.copy(mana = minOf(playerData.mana + 10, updated.maxMana)))
                        }
                    )
                }

                item {
                    val bootsAgi = GameDatabase.getBoots(playerData.equippedBoots).agility
                    val offHand1 = GameDatabase.getOffHand(playerData.equippedOffHand)
                    val offHand2 = GameDatabase.getOffHand(playerData.equippedOffHand2)
                    val totalAgi = playerData.agility + bootsAgi + offHand1.agility + offHand2.agility
                    val totalLuck = playerData.luck + offHand1.luck + offHand2.luck
                    
                    val crit = ((totalAgi * 1.0) + (totalLuck * 0.5)).coerceAtMost(75.0)
                    val dodge = ((totalAgi * 0.5) + (totalLuck * 0.2)).coerceAtMost(50.0)
                    
                    StatAllocationRow(
                        label = "Agility", 
                        value = playerData.agility, 
                        description = "Crit: $crit% | Dodge: $dodge%", 
                        canIncrease = playerData.statPoints > 0,
                        onHelp = { helpItem = "Agility (AGI)" to "Grants +2% Critical Hit chance and +1% Dodge chance per point. Allows you to strike fast and avoid danger." },
                        onIncrease = {
                            onPlayerUpdate(playerData.copy(agility = playerData.agility + 2, statPoints = playerData.statPoints - 1).recalculateVitals())
                        }
                    )
                }

                item {
                    val offHand1 = GameDatabase.getOffHand(playerData.equippedOffHand)
                    val offHand2 = GameDatabase.getOffHand(playerData.equippedOffHand2)
                    val totalLuck = playerData.luck + offHand1.luck + offHand2.luck
                    val goldBonus = totalLuck * 1
                    StatAllocationRow(
                        label = "Luck", 
                        value = playerData.luck, 
                        description = "Gold Bonus: +$goldBonus%", 
                        canIncrease = playerData.statPoints > 0,
                        onHelp = { helpItem = "Luck (LCK)" to "A versatile stat. Grants +0.5% Crit, +0.2% Dodge, and +1% Gold found per point. Higher luck also helps in the Gambling House." },
                        onIncrease = {
                            onPlayerUpdate(playerData.copy(luck = playerData.luck + 2, statPoints = playerData.statPoints - 1).recalculateVitals())
                        }
                    )
                }

                item {
                    val offHand1 = GameDatabase.getOffHand(playerData.equippedOffHand)
                    val offHand2 = GameDatabase.getOffHand(playerData.equippedOffHand2)
                    val totalWis = playerData.wisdom + offHand1.wisdom + offHand2.wisdom
                    val regen = (totalWis / 5).coerceAtLeast(1)
                    StatAllocationRow(
                        label = "Wisdom", 
                        value = playerData.wisdom, 
                        description = "Mana Regen: $regen/turn", 
                        canIncrease = playerData.statPoints > 0,
                        onHelp = { helpItem = "Wisdom (WIS)" to "Determines your Mana regeneration per turn. You regain 1 Mana for every 5 points of Wisdom. Vital for prolonged battles." },
                        onIncrease = {
                            onPlayerUpdate(playerData.copy(wisdom = playerData.wisdom + 2, statPoints = playerData.statPoints - 1).recalculateVitals())
                        }
                    )
                }
                
                // DERIVED STATS PANEL
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                            .padding(12.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Combat Overview", color = Color.White, style = MaterialTheme.typography.titleSmall)
                        
                        val weapon = GameDatabase.getWeapon(playerData.equippedWeapon)
                        val head = GameDatabase.getHeadGear(playerData.equippedHead)
                        val armor = GameDatabase.getArmor(playerData.equippedArmor)
                        val boots = GameDatabase.getBoots(playerData.equippedBoots)
                        val shield = GameDatabase.getShield(playerData.equippedShield)
                        val offHand = GameDatabase.getOffHand(playerData.equippedOffHand)

                        // Factor in Traits
                        val traitBonusAtk = playerData.unlockedTraits.sumOf { com.solerforge.lumeria.database.TraitDatabase.getTrait(it)?.strBonus ?: 0 }
                        val traitBonusDef = playerData.unlockedTraits.sumOf { com.solerforge.lumeria.database.TraitDatabase.getTrait(it)?.vitBonus ?: 0 }
                        val traitBonusAgi = playerData.unlockedTraits.sumOf { com.solerforge.lumeria.database.TraitDatabase.getTrait(it)?.agiBonus ?: 0 }
                        val traitBonusInt = playerData.unlockedTraits.sumOf { com.solerforge.lumeria.database.TraitDatabase.getTrait(it)?.intBonus ?: 0 }
                        
                        val totalAtk = weapon.attack + playerData.strength + offHand.strength + traitBonusAtk
                        val totalDef = armor.defense + head.defense + shield.defense + playerData.defense + traitBonusDef
                        val totalAgi = playerData.agility + boots.agility + offHand.agility + traitBonusAgi
                        val totalInt = playerData.intelligence + offHand.intelligence + traitBonusInt
                        val totalLuck = playerData.luck + offHand.luck
                        val totalWis = playerData.wisdom + offHand.wisdom
                        
                        Text("Total Attack Power: $totalAtk", color = Color.Cyan)
                        Text("Total Defense: $totalDef", color = Color.Cyan)
                        Text("Total Agility: $totalAgi", color = Color.Cyan)
                        Text("Magic Power: $totalInt", color = Color.Cyan)
                        Text("Luck: $totalLuck | Wisdom: $totalWis", color = Color.Cyan)
                    }
                }

                // TITLE GALLERY
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                    ) {
                        Text("Unlocked Titles", color = Color.White, style = MaterialTheme.typography.titleMedium)
                        Spacer(modifier = Modifier.height(12.dp))
                        
                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            maxItemsInEachRow = 3
                        ) {
                            playerData.unlockedTitles.forEach { title ->
                                val isEquipped = title == playerData.currentTitle
                                Box(
                                    modifier = Modifier
                                        .padding(4.dp)
                                        .background(
                                            if (isEquipped) Color.Yellow.copy(alpha = 0.2f) else Color.DarkGray.copy(alpha = 0.5f),
                                            RoundedCornerShape(8.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isEquipped) Color.Yellow else Color.Transparent,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .clickable { onPlayerUpdate(playerData.copy(currentTitle = title)) }
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = title,
                                        color = if (isEquipped) Color.Yellow else Color.LightGray,
                                        style = MaterialTheme.typography.labelLarge,
                                        fontWeight = if (isEquipped) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            }
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

        helpItem?.let { (title, desc) ->
            AlertDialog(
                onDismissRequest = { helpItem = null },
                title = { Text(title, fontWeight = FontWeight.Bold, color = Color.Cyan) },
                text = { Text(desc, color = Color.White) },
                confirmButton = {
                    TextButton(onClick = { helpItem = null }) {
                        Text("Close", color = Color.Cyan)
                    }
                },
                containerColor = Color(0xFF1A1A1A),
                titleContentColor = Color.White,
                textContentColor = Color.LightGray
            )
        }
    }
}

@Composable
fun AttributeDetail(name: String, detail: String) {
    Row(modifier = Modifier.padding(vertical = 2.dp)) {
        Text("$name: ", color = Color.Cyan, style = MaterialTheme.typography.labelMedium, fontWeight = FontWeight.Bold)
        Text(detail, color = Color.White, style = MaterialTheme.typography.labelMedium)
    }
}
