package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.GameDatabase
import com.solerforge.lumeria.components.InventoryItemRow
import com.solerforge.lumeria.utils.MusicManager

enum class InventoryTab(theName: String) { 
    COMBAT("GEAR"), 
    APPAREL("WEAR"), 
    ACC("ACC"), 
    ITEMS("LOOT");
    
    val displayName = theName
}

enum class SortMode(val displayName: String) {
    DEFAULT("Default"),
    RARITY("Rarity"),
    VALUE("Value"),
    NAME("A-Z")
}

@Composable
fun InventoryScreen(
    modifier: Modifier = Modifier,
    playerData: PlayerData,
    onPlayerUpdate: (PlayerData) -> Unit,
    onReturn: () -> Unit,
) {
    var inventorySnapshot by remember(playerData) { mutableStateOf(playerData) }
    var selectedTab by remember { mutableStateOf(InventoryTab.COMBAT) }
    var examiningItem by remember { mutableStateOf<Pair<String, String>?>(null) }
    var sortMode by remember { mutableStateOf(SortMode.DEFAULT) }
    var hideCommon by remember { mutableStateOf(value = false) }
    val context = LocalContext.current

    val rarityMap = remember {
        mapOf(
            "God Tier" to 0,
            "Mythic" to 1,
            "Legendary" to 2,
            "Epic" to 3,
            "Rare" to 4,
            "Uncommon" to 5,
            "Common" to 6,
        )
    }

    fun getItemsForTab(tab: InventoryTab): List<Pair<String, Int>> {
        val rawItems = inventorySnapshot.inventory.filter { name ->
            val base = GameDatabase.getBaseName(name)
            when (tab) {
                InventoryTab.COMBAT -> GameDatabase.weapons.any { it.name == base } || GameDatabase.shields.any { it.name == base }
                InventoryTab.APPAREL -> GameDatabase.armors.any { (it.name == base) && (it.defense > 0) } || GameDatabase.headGears.any { it.name == base } || GameDatabase.boots.any { it.name == base }
                InventoryTab.ACC -> GameDatabase.offHands.any { it.name == base } || GameDatabase.fishingRods.any { it.name == base }
                InventoryTab.ITEMS -> GameDatabase.consumables.any { it.name == base } || com.solerforge.lumeria.database.FishDatabase.fishTable.any { it.name == base } || GameDatabase.craftingMaterials.any { it.name == base } || GameDatabase.armors.any { (it.name == base) && (it.defense == 0) && (it.price == 0L) && it.isBossDrop }
            }
        }

        val grouped = rawItems.groupingBy { it }.eachCount().toList()

        val filtered = if (hideCommon) {
            grouped.filter { (name, _) ->
                val base = GameDatabase.getBaseName(name)
                val rarity = GameDatabase.weapons.find { it.name == base }?.rarity
                    ?: GameDatabase.armors.find { it.name == base }?.rarity
                    ?: GameDatabase.headGears.find { it.name == base }?.rarity
                    ?: GameDatabase.boots.find { it.name == base }?.rarity
                    ?: GameDatabase.shields.find { it.name == base }?.rarity
                    ?: GameDatabase.offHands.find { it.name == base }?.rarity
                    ?: "Common"
                rarity != "Common"
            }
        } else grouped

        return when (sortMode) {
            SortMode.DEFAULT -> filtered
            SortMode.NAME -> filtered.sortedBy { it.first }
            SortMode.VALUE -> filtered.sortedByDescending { (name, _) ->
                val base = GameDatabase.getBaseName(name)
                GameDatabase.weapons.find { it.name == base }?.price
                    ?: GameDatabase.armors.find { it.name == base }?.price
                    ?: GameDatabase.headGears.find { it.name == base }?.price
                    ?: GameDatabase.boots.find { it.name == base }?.price
                    ?: GameDatabase.shields.find { it.name == base }?.price
                    ?: GameDatabase.offHands.find { it.name == base }?.price
                    ?: 0L
            }
            SortMode.RARITY -> filtered.sortedBy { (name, _) ->
                val base = GameDatabase.getBaseName(name)
                val rarity = GameDatabase.weapons.find { it.name == base }?.rarity
                    ?: GameDatabase.armors.find { it.name == base }?.rarity
                    ?: GameDatabase.headGears.find { it.name == base }?.rarity
                    ?: GameDatabase.boots.find { it.name == base }?.rarity
                    ?: GameDatabase.shields.find { it.name == base }?.rarity
                    ?: GameDatabase.offHands.find { it.name == base }?.rarity
                    ?: "Common"
                rarityMap[rarity] ?: 7
            }
        }
    }

    LaunchedEffect(Unit) {
        MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.inventory_screen_bg),
            contentDescription = "Inventory Background",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
        )

        // Darker overlay for UI clarity
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Inventory",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                modifier = Modifier.padding(vertical = 16.dp),
            )

            // LOADOUT PANEL
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.DarkGray.copy(alpha = 0.6f), RoundedCornerShape(12.dp))
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text("Current Loadout", color = Color.White, style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(8.dp))
                
                val weapon = GameDatabase.getWeapon(inventorySnapshot.equippedWeapon)
                val armor = GameDatabase.getArmor(inventorySnapshot.equippedArmor)
                val head = GameDatabase.getHeadGear(inventorySnapshot.equippedHead)
                val boots = GameDatabase.getBoots(inventorySnapshot.equippedBoots)
                val shield = GameDatabase.getShield(inventorySnapshot.equippedShield)
                val offHand = GameDatabase.getOffHand(inventorySnapshot.equippedOffHand)
                val offHand2 = GameDatabase.getOffHand(inventorySnapshot.equippedOffHand2)
                
                Column(horizontalAlignment = Alignment.Start) {
                    Text("Weapon: ${weapon.name} (+${weapon.attack} ATK)", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    Text("Head: ${head.name} (+${head.defense} DEF)", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    Text("Armor: ${armor.name} (+${armor.defense} DEF)", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    Text("Boots: ${boots.name} (+${boots.agility} AGI)", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    Text("Shield: ${shield.name} (+${shield.defense} DEF)", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    Text("Off-Hand 1: ${offHand.name}", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                    Text("Off-Hand 2: ${offHand2.name}", color = Color.LightGray, style = MaterialTheme.typography.bodySmall)
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                
                val totalAtk = weapon.attack + inventorySnapshot.strength + offHand.strength + offHand2.strength
                val totalDef = armor.defense + head.defense + shield.defense
                val totalAgi = inventorySnapshot.agility + boots.agility + offHand.agility + offHand2.agility
                val totalLuck = inventorySnapshot.luck + offHand.luck + offHand2.luck
                val totalWis = inventorySnapshot.wisdom + offHand.wisdom + offHand2.wisdom
                
                Text("Total: $totalAtk ATK | $totalDef DEF | $totalAgi AGI", color = Color.Cyan, style = MaterialTheme.typography.titleMedium)
                Text("Total: $totalLuck LCK | $totalWis WIS", color = Color.Cyan, style = MaterialTheme.typography.titleMedium)
            }

            Spacer(modifier = Modifier.height(20.dp))

            // TAB ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                InventoryTab.entries.forEach { tab ->
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
                                scaleX = if (isSelected) 1.15f else 1f
                                scaleY = if (isSelected) 1.15f else 1f
                            }
                        )
                        if (isSelected) {
                            Box(
                                modifier = Modifier
                                    .align(Alignment.BottomCenter)
                                    .fillMaxWidth(0.6f)
                                    .height(2.dp)
                                    .background(Color.Cyan)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // SORT / FILTER ROW
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Sort:", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    SortMode.entries.forEach { mode ->
                        Text(
                            text = mode.displayName,
                            color = if (sortMode == mode) Color.Yellow else Color.White.copy(alpha = 0.4f),
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = if (sortMode == mode) FontWeight.Bold else FontWeight.Normal,
                            modifier = Modifier.clickable { sortMode = mode }
                        )
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { hideCommon = !hideCommon }) {
                    androidx.compose.material3.Checkbox(
                        checked = hideCommon,
                        onCheckedChange = { hideCommon = it },
                        colors = androidx.compose.material3.CheckboxDefaults.colors(checkedColor = Color.Cyan)
                    )
                    Text("Hide Common", color = if (hideCommon) Color.Cyan else Color.Gray, style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val currentItems = getItemsForTab(selectedTab)

                when (selectedTab) {
                    InventoryTab.COMBAT -> {
                        // WEAPONS
                        val ownedWeapons = currentItems.filter { (name, _) -> 
                            val base = GameDatabase.getBaseName(name)
                            GameDatabase.weapons.any { it.name == base } 
                        }
                        
                        if (ownedWeapons.isNotEmpty()) {
                            item { Text("Weapons", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                            items(ownedWeapons) { (itemName, count) ->
                                val isEquipped = itemName == inventorySnapshot.equippedWeapon
                                val itemData = GameDatabase.getWeapon(itemName)
                                val currentWeapon = GameDatabase.getWeapon(inventorySnapshot.equippedWeapon)
                                val diff = itemData.attack - currentWeapon.attack
                                val statDiff = if (!isEquipped && (diff != 0)) "${if (diff > 0) "+" else ""}$diff ATK" else null
                                val isClassMatch = itemData.requiredClasses.isEmpty() || itemData.requiredClasses.contains(inventorySnapshot.playerClass)
                                
                                InventoryItemRow(
                                    name = itemName, 
                                    stat = "${itemData.attack} ATK", 
                                    rarity = itemData.rarity,
                                    description = itemData.description,
                                    lore = itemData.lore,
                                    isEquipped = isEquipped, 
                                    actionLabel = if (isClassMatch) "Equip" else "Incompatible",
                                    statDiff = statDiff,
                                    count = count,
                                    requiredClasses = itemData.requiredClasses
                                ) {
                                    if (isClassMatch) {
                                        inventorySnapshot = inventorySnapshot.copy(equippedWeapon = itemName)
                                        onPlayerUpdate(inventorySnapshot)
                                    }
                                }
                            }
                        }

                        // SHIELDS
                        val ownedShields = currentItems.filter { (name, _) -> 
                            val base = GameDatabase.getBaseName(name)
                            GameDatabase.shields.any { it.name == base } 
                        }
                        
                        if (ownedShields.isNotEmpty()) {
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            item { Text("Shields", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                            items(ownedShields) { (itemName, count) ->
                                val isEquipped = itemName == inventorySnapshot.equippedShield
                                val itemData = GameDatabase.getShield(itemName)
                                val currentShield = GameDatabase.getShield(inventorySnapshot.equippedShield)
                                val diff = itemData.defense - currentShield.defense
                                val statDiff = if (!isEquipped && (diff != 0)) "${if (diff > 0) "+" else ""}$diff DEF" else null
                                val isClassMatch = itemData.requiredClasses.isEmpty() || itemData.requiredClasses.contains(inventorySnapshot.playerClass)

                                InventoryItemRow(
                                    name = itemName, 
                                    stat = "${itemData.defense} DEF", 
                                    rarity = itemData.rarity,
                                    description = itemData.description,
                                    lore = "",
                                    isEquipped = isEquipped, 
                                    actionLabel = if (isClassMatch) "Equip" else "Incompatible",
                                    statDiff = statDiff,
                                    count = count,
                                    requiredClasses = itemData.requiredClasses
                                ) {
                                    if (isClassMatch) {
                                        inventorySnapshot = inventorySnapshot.copy(equippedShield = itemName)
                                        onPlayerUpdate(inventorySnapshot)
                                    }
                                }
                            }
                        }
                    }

                    InventoryTab.APPAREL -> {
                        // HEAD GEAR
                        val ownedHeadGear = currentItems.filter { (name, _) -> 
                            val base = GameDatabase.getBaseName(name)
                            GameDatabase.headGears.any { it.name == base } 
                        }
                        
                        if (ownedHeadGear.isNotEmpty()) {
                            item { Text("Head Gear", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                            items(ownedHeadGear) { (itemName, count) ->
                                val isEquipped = itemName == inventorySnapshot.equippedHead
                                val itemData = GameDatabase.getHeadGear(itemName)
                                val currentHead = GameDatabase.getHeadGear(inventorySnapshot.equippedHead)
                                val diff = itemData.defense - currentHead.defense
                                val statDiff = if (!isEquipped && (diff != 0)) "${if (diff > 0) "+" else ""}$diff DEF" else null
                                val isClassMatch = itemData.requiredClasses.isEmpty() || itemData.requiredClasses.contains(inventorySnapshot.playerClass)

                                InventoryItemRow(
                                    name = itemName, 
                                    stat = "${itemData.defense} DEF", 
                                    rarity = itemData.rarity,
                                    description = itemData.description,
                                    lore = itemData.lore,
                                    isEquipped = isEquipped, 
                                    actionLabel = if (isClassMatch) "Equip" else "Incompatible",
                                    statDiff = statDiff,
                                    count = count,
                                    requiredClasses = itemData.requiredClasses
                                ) {
                                    if (isClassMatch) {
                                        inventorySnapshot = inventorySnapshot.copy(equippedHead = itemName)
                                        onPlayerUpdate(inventorySnapshot)
                                    }
                                }
                            }
                        }

                        // ARMOR
                        val ownedArmor = currentItems.filter { (name, _) -> 
                            val base = GameDatabase.getBaseName(name)
                            GameDatabase.armors.any { it.name == base && it.defense > 0 } 
                        }
                        
                        if (ownedArmor.isNotEmpty()) {
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            item { Text("Armor", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                            items(ownedArmor) { (itemName, count) ->
                                val isEquipped = itemName == inventorySnapshot.equippedArmor
                                val itemData = GameDatabase.getArmor(itemName)
                                val currentArmor = GameDatabase.getArmor(inventorySnapshot.equippedArmor)
                                val diff = itemData.defense - currentArmor.defense
                                val statDiff = if (!isEquipped && (diff != 0)) "${if (diff > 0) "+" else ""}$diff DEF" else null
                                val isClassMatch = itemData.requiredClasses.isEmpty() || itemData.requiredClasses.contains(inventorySnapshot.playerClass)

                                InventoryItemRow(
                                    name = itemName, 
                                    stat = "${itemData.defense} DEF", 
                                    rarity = itemData.rarity,
                                    description = itemData.description,
                                    lore = itemData.lore,
                                    isEquipped = isEquipped, 
                                    actionLabel = if (isClassMatch) "Equip" else "Incompatible",
                                    statDiff = statDiff,
                                    count = count,
                                    requiredClasses = itemData.requiredClasses
                                ) {
                                    if (isClassMatch) {
                                        inventorySnapshot = inventorySnapshot.copy(equippedArmor = itemName)
                                        onPlayerUpdate(inventorySnapshot)
                                    }
                                }
                            }
                        }

                        // BOOTS
                        val ownedBoots = currentItems.filter { (name, _) -> 
                            val base = GameDatabase.getBaseName(name)
                            GameDatabase.boots.any { it.name == base } 
                        }
                        
                        if (ownedBoots.isNotEmpty()) {
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            item { Text("Boots", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                            items(ownedBoots) { (itemName, count) ->
                                val isEquipped = itemName == inventorySnapshot.equippedBoots
                                val itemData = GameDatabase.getBoots(itemName)
                                val currentBoots = GameDatabase.getBoots(inventorySnapshot.equippedBoots)
                                val diff = itemData.agility - currentBoots.agility
                                val statDiff = if (!isEquipped && (diff != 0)) "${if (diff > 0) "+" else ""}$diff AGI" else null
                                val isClassMatch = itemData.requiredClasses.isEmpty() || itemData.requiredClasses.contains(inventorySnapshot.playerClass)

                                InventoryItemRow(
                                    name = itemName, 
                                    stat = "${itemData.agility} AGI", 
                                    rarity = itemData.rarity,
                                    description = itemData.description,
                                    lore = "",
                                    isEquipped = isEquipped, 
                                    actionLabel = if (isClassMatch) "Equip" else "Incompatible",
                                    statDiff = statDiff,
                                    count = count,
                                    requiredClasses = itemData.requiredClasses
                                ) {
                                    if (isClassMatch) {
                                        inventorySnapshot = inventorySnapshot.copy(equippedBoots = itemName)
                                        onPlayerUpdate(inventorySnapshot)
                                    }
                                }
                            }
                        }
                    }

                    InventoryTab.ACC -> {
                        // OFF-HANDS
                        val ownedOffHands = currentItems.filter { (name, _) -> 
                            GameDatabase.offHands.any { it.name == name } 
                        }
                        
                        if (ownedOffHands.isNotEmpty()) {
                            item { Text("Off-Hands", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                            items(ownedOffHands) { (itemName, count) ->
                                val isEquipped1 = itemName == inventorySnapshot.equippedOffHand
                                val isEquipped2 = itemName == inventorySnapshot.equippedOffHand2
                                val isEquipped = isEquipped1 || isEquipped2
                                
                                val itemData = GameDatabase.getOffHand(itemName)
                                val currentOffHand = if (isEquipped2) GameDatabase.getOffHand(inventorySnapshot.equippedOffHand2) else GameDatabase.getOffHand(inventorySnapshot.equippedOffHand)
                                val isClassMatch = itemData.requiredClasses.isEmpty() || itemData.requiredClasses.contains(inventorySnapshot.playerClass)
                                
                                val statText = buildString {
                                    if (itemData.strength > 0) append("${itemData.strength} STR ")
                                    if (itemData.agility > 0) append("${itemData.agility} AGI ")
                                    if (itemData.intelligence > 0) append("${itemData.intelligence} INT ")
                                    if (itemData.luck > 0) append("${itemData.luck} LCK ")
                                    if (itemData.wisdom > 0) append("${itemData.wisdom} WIS ")
                                }.trim()

                                val statDiff = if (!isEquipped) {
                                    buildString {
                                        val sD = itemData.strength - currentOffHand.strength
                                        val aD = itemData.agility - currentOffHand.agility
                                        val iD = itemData.intelligence - currentOffHand.intelligence
                                        val lD = itemData.luck - currentOffHand.luck
                                        val wD = itemData.wisdom - currentOffHand.wisdom
                                        if (sD != 0) append("${if (sD > 0) "+" else ""}$sD STR ")
                                        if (aD != 0) append("${if (aD > 0) "+" else ""}$aD AGI ")
                                        if (iD != 0) append("${if (iD > 0) "+" else ""}$iD INT ")
                                        if (lD != 0) append("${if (lD > 0) "+" else ""}$lD LCK ")
                                        if (wD != 0) append("${if (wD > 0) "+" else ""}$wD WIS ")
                                    }.trim().takeIf { it.isNotEmpty() }
                                } else null

                                Column {
                                    InventoryItemRow(
                                        name = itemName, 
                                        stat = statText, 
                                        rarity = itemData.rarity,
                                        description = itemData.description,
                                        lore = itemData.lore,
                                        isEquipped = isEquipped, 
                                        actionLabel = if (isClassMatch) "Equip Slot 1" else "Incompatible",
                                        statDiff = statDiff,
                                        count = count,
                                        requiredClasses = itemData.requiredClasses
                                    ) {
                                        if (isClassMatch) {
                                            inventorySnapshot = inventorySnapshot.copy(equippedOffHand = itemName)
                                            onPlayerUpdate(inventorySnapshot)
                                        }
                                    }
                                    
                                    if (!isEquipped) {
                                        Button(
                                            onClick = {
                                                if (isClassMatch) {
                                                    inventorySnapshot = inventorySnapshot.copy(equippedOffHand2 = itemName)
                                                    onPlayerUpdate(inventorySnapshot)
                                                }
                                            },
                                            enabled = isClassMatch,
                                            modifier = Modifier.padding(start = 16.dp, top = 4.dp).fillMaxWidth(0.6f)
                                        ) {
                                            Text(if (isClassMatch) "Equip Slot 2" else "Incompatible")
                                        }
                                    }
                                }
                            }
                        }

                        // FISHING RODS
                        val ownedRods = currentItems.filter { (name, _) -> 
                            GameDatabase.fishingRods.any { it.name == name } 
                        }
                        
                        if (ownedRods.isNotEmpty()) {
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                            item { Text("Fishing Rods", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                            items(ownedRods) { (itemName, count) ->
                                val isEquipped = itemName == inventorySnapshot.equippedRod
                                val itemData = GameDatabase.getFishingRod(itemName)
                                
                                InventoryItemRow(
                                    name = itemName, 
                                    stat = "Rod (Bonus: ${itemData.reactionBonus}ms)", 
                                    rarity = itemData.rarity,
                                    description = itemData.description,
                                    lore = "",
                                    isEquipped = isEquipped, 
                                    actionLabel = "Equip",
                                    count = count
                                ) {
                                    inventorySnapshot = inventorySnapshot.copy(equippedRod = itemName)
                                    onPlayerUpdate(inventorySnapshot)
                                }
                            }
                        }
                    }

                    InventoryTab.ITEMS -> {
                        // CRAFTING MATERIALS
                        val ownedMaterials = currentItems.filter { (name, _) ->
                            GameDatabase.craftingMaterials.any { it.name == name }
                        }

                        if (ownedMaterials.isNotEmpty()) {
                            item { Text("Crafting Materials", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                            items(ownedMaterials) { (itemName, count) ->
                                val matData = GameDatabase.craftingMaterials.find { it.name == itemName }!!
                                InventoryItemRow(
                                    name = itemName,
                                    stat = "Material (${matData.zone})",
                                    rarity = matData.rarity,
                                    description = matData.description,
                                    lore = "",
                                    isEquipped = false,
                                    actionLabel = "Examine",
                                    count = count
                                ) {
                                    examiningItem = itemName to matData.description
                                }
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }

                        // CONSUMABLES & FISH
                        val ownedConsumables = currentItems.filter { (name, _) -> GameDatabase.consumables.any { it.name == name } }
                        val ownedFish = currentItems.filter { (name, _) -> com.solerforge.lumeria.database.FishDatabase.fishTable.any { it.name == name } }
                        
                        // TROPHIES
                        val ownedTrophies = currentItems.filter { (name, _) ->
                            val base = GameDatabase.getBaseName(name)
                            GameDatabase.armors.any { it.name == base && it.defense == 0 && it.price == 0 && it.isBossDrop }
                        }

                        if (ownedTrophies.isNotEmpty()) {
                            item { Text("Trophies & Spoils", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                            items(ownedTrophies) { (itemName, count) ->
                                val itemData = GameDatabase.armors.find { it.name == itemName }!!
                                InventoryItemRow(
                                    name = itemName,
                                    stat = "Special Item",
                                    rarity = itemData.rarity,
                                    description = itemData.description,
                                    lore = itemData.lore,
                                    isEquipped = false,
                                    actionLabel = "Examine",
                                    count = count
                                ) { 
                                    examiningItem = itemName to itemData.description
                                }
                            }
                            item { Spacer(modifier = Modifier.height(16.dp)) }
                        }

                        val foodItems = (ownedConsumables + ownedFish)
                        
                        if (foodItems.isNotEmpty()) {
                            item { Text("Food & Consumables", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                            items(foodItems) { (itemName, count) ->
                                val consumable = GameDatabase.getConsumable(itemName)
                                val fish = com.solerforge.lumeria.database.FishDatabase.fishTable.find { it.name == itemName }
                                
                                val statText = when {
                                    (consumable != null) && (consumable.healAmount > 0) -> "Heals ${consumable.healAmount} HP"
                                    (consumable != null) && (consumable.manaAmount > 0) -> "Restores ${consumable.manaAmount} MP"
                                    (fish != null) && (fish.rarity == "God Tier") && (fish.basePrice == 0) -> "Perm +2% Max HP"
                                    fish != null -> "Fresh Catch (Value: ${fish.basePrice}G)"
                                    else -> ""
                                }
                                
                                InventoryItemRow(
                                    name = itemName, 
                                    stat = statText, 
                                    rarity = consumable?.rarity ?: fish?.rarity ?: "Common",
                                    description = consumable?.description ?: fish?.description ?: "",
                                    lore = consumable?.lore ?: "",
                                    isEquipped = false, 
                                    actionLabel = if (fish != null && fish.rarity == "God Tier" && fish.basePrice == 0) "Eat" else "Use",
                                    count = count
                                ) {
                                    val newInventory = inventorySnapshot.inventory.toMutableList()
                                    if (!newInventory.remove(itemName)) return@InventoryItemRow
                                    
                                    var finalHp = inventorySnapshot.hp
                                    var finalMana = inventorySnapshot.mana
                                    var finalConsumedCount = inventorySnapshot.consumedGodFishCount
                                    
                                    if (consumable != null) {
                                        finalHp = minOf(inventorySnapshot.hp + consumable.healAmount, inventorySnapshot.maxHp)
                                        finalMana = minOf(inventorySnapshot.mana + consumable.manaAmount, inventorySnapshot.maxMana)
                                    } else if (fish != null && fish.rarity == "God Tier" && fish.basePrice == 0) {
                                        finalConsumedCount++
                                    } else {
                                        // If it's a regular fish or something else, we shouldn't have consumed it
                                        return@InventoryItemRow
                                    }

                                    inventorySnapshot = inventorySnapshot.copy(
                                        hp = finalHp,
                                        mana = finalMana,
                                        inventory = newInventory,
                                        consumedGodFishCount = finalConsumedCount
                                    )
                                    onPlayerUpdate(inventorySnapshot)
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

        examiningItem?.let { (name, desc) ->
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { examiningItem = null },
                title = { Text(name, fontWeight = FontWeight.Bold, color = Color.Cyan) },
                text = { Text(desc, color = Color.White) },
                confirmButton = {
                    androidx.compose.material3.TextButton(onClick = { examiningItem = null }) {
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
