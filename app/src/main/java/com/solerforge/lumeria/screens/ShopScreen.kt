package com.solerforge.lumeria.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.R
import com.solerforge.lumeria.components.RpgButton
import com.solerforge.lumeria.components.SellItemRow
import com.solerforge.lumeria.components.ShopItemRow
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.GameDatabase
import com.solerforge.lumeria.database.WorldDatabase
import com.solerforge.lumeria.utils.CurrencyUtils
import com.solerforge.lumeria.utils.MusicManager

enum class ShopCategory(val displayName: String) {
    COMBAT("COMBAT"),
    APPAREL("APPAREL"),
    ACC("ACC."),
    ITEMS("ITEMS")
}

@Composable
fun ShopScreen(
    modifier: Modifier = Modifier,
    playerData: PlayerData,
    onPlayerUpdate: (PlayerData) -> Unit,
    onReturn: () -> Unit,
) {
    var shopSnapshot by remember(playerData) { mutableStateOf(playerData) }
    var mainTab by remember { mutableStateOf("Buy") }
    var buyTab by remember { mutableStateOf(ShopCategory.COMBAT) }
    val consumableAmounts = remember { mutableStateMapOf<String, Int>() }
    var pendingEquipItem by remember { mutableStateOf<String?>(null) }
    val context = LocalContext.current

    // Helper to check if location is unlocked
    val isLocationUnlocked = remember(shopSnapshot) {
        { locationName: String ->
            val location = WorldDatabase.locations.find { it.name == locationName }
            if (location == null) true
            else (shopSnapshot.level >= location.requiredLevel) || shopSnapshot.unlockedLocations.contains(locationName)
        }
    }
    
    LaunchedEffect(Unit) {
        MusicManager.playMusic(context, R.raw.billys_general_store_theme)
    }

    Box(modifier = modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.billys_store_bg),
            contentDescription = "Billy's Store Background",
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
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            // BILLY PORTRAIT
            Image(
                painter = painterResource(id = R.drawable.billy_profile_img),
                contentDescription = "Billy the Shopkeeper",
                modifier = Modifier
                    .size(110.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color.Yellow.copy(alpha = 0.6f), CircleShape),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter,
            )

            Text(
                text = "Billy's General Store",
                style = MaterialTheme.typography.headlineMedium,
                color = Color.White,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(top = 8.dp, bottom = 4.dp),
            )

            val billyDialogue = when {
                mainTab == "Sell" -> "\"Looking to offload some junk? I'll take it off your hands for a fair price.\""
                shopSnapshot.gold > 500000L -> "\"Whoa! A high roller! Only the best for you, friend!\""
                shopSnapshot.gold < 100L -> "\"You're looking a bit light on coin... but a look is free!\""
                else -> "\"Hey there! I'm Billy. Looking for some quality gear?\""
            }

            Text(
                text = billyDialogue,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.LightGray,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 12.dp),
            )
            
            Text(
                text = "Gold: ${CurrencyUtils.formatGold(shopSnapshot.gold)}",
                style = MaterialTheme.typography.titleLarge,
                color = Color.Yellow,
            )

            Spacer(modifier = Modifier.height(16.dp))

            // BUY / SELL TOGGLE
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 24.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Button(
                    onClick = { mainTab = "Buy" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mainTab == "Buy") MaterialTheme.colorScheme.primary else Color(0xFF616161),
                    ),
                ) {
                    Text("Buy")
                }
                Button(
                    onClick = { mainTab = "Sell" },
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (mainTab == "Sell") MaterialTheme.colorScheme.primary else Color(0xFF616161),
                    ),
                ) {
                    Text("Sell")
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            if (mainTab == "Buy") {
                // SUB-TABS FOR BUYING
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    ShopCategory.entries.forEach { tab ->
                        val isSelected = buyTab == tab
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .clickable { buyTab = tab }
                                .padding(vertical = 8.dp),
                            contentAlignment = Alignment.Center,
                        ) {
                            Text(
                                text = tab.displayName,
                                color = if (isSelected) Color.Cyan else Color.White.copy(alpha = 0.6f),
                                style = MaterialTheme.typography.labelSmall,
                                fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.Normal,
                                modifier = Modifier.graphicsLayer {
                                    scaleX = if (isSelected) 1.15f else 1f
                                    scaleY = if (isSelected) 1.15f else 1f
                                },
                            )
                            if (isSelected) {
                                Box(
                                    modifier = Modifier
                                        .align(Alignment.BottomCenter)
                                        .fillMaxWidth(0.6f)
                                        .height(2.dp)
                                        .background(Color.Cyan),
                                )
                            }
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
            }

            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                if (mainTab == "Buy") {
                    val currentRankId = com.solerforge.lumeria.database.KingdomRankDatabase.getCurrentRank(shopSnapshot.renown).id

                    when (buyTab) {
                        ShopCategory.COMBAT -> {
                            // WEAPONS
                            val availableWeapons = GameDatabase.weapons.filter { 
                                val classMatches = it.requiredClasses.isEmpty() || it.requiredClasses.contains(shopSnapshot.playerClass)
                                (it.price > 0) && (!it.isBossDrop) &&
                                (it.rarity !in listOf("Legendary", "Mythic", "God Tier") || it.requiredRank > 0) &&
                                isLocationUnlocked(it.requiredLocation) && 
                                (currentRankId >= it.requiredRank) &&
                                classMatches
                            }
                            
                            if (availableWeapons.isNotEmpty()) {
                                item { Text("Weapons", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                                items(availableWeapons) { weapon ->
                                    val isOwned = weapon.name in shopSnapshot.inventory
                                    val price = CurrencyUtils.applyDiscount(weapon.price, shopSnapshot.nationUpgradeLevel, shopSnapshot.activeKingdomLawId)
                                    val currentWeaponAtk = GameDatabase.getWeapon(shopSnapshot.equippedWeapon).attack
                                    val statDiff = weapon.attack - currentWeaponAtk

                                    ShopItemRow(
                                        name = weapon.name,
                                        stat = "${weapon.attack} ATK",
                                        price = price,
                                        rarity = weapon.rarity,
                                        description = weapon.description,
                                        currentGold = shopSnapshot.gold,
                                        isOwned = isOwned,
                                        statDiff = statDiff,
                                        requiredClasses = weapon.requiredClasses,
                                    ) {
                                        if (shopSnapshot.gold >= price) {
                                            shopSnapshot = shopSnapshot.copy(
                                                gold = shopSnapshot.gold - price,
                                                inventory = (shopSnapshot.inventory + weapon.name).distinct(),
                                            )
                                            onPlayerUpdate(shopSnapshot)
                                            pendingEquipItem = weapon.name
                                        }
                                    }
                                }
                            }

                            // SHIELDS
                            val availableShields = GameDatabase.shields.filter {
                                val classMatches = it.requiredClasses.isEmpty() || it.requiredClasses.contains(shopSnapshot.playerClass)
                                (it.price > 0) && (!it.isBossDrop) &&
                                (it.rarity !in listOf("Legendary", "Mythic", "God Tier") || it.requiredRank > 0) &&
                                isLocationUnlocked(it.requiredLocation) &&
                                (currentRankId >= it.requiredRank) &&
                                classMatches
                            }
                            if (availableShields.isNotEmpty()) {
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                                item { Text("Shields", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                                items(availableShields) { item ->
                                    val isOwned = item.name in shopSnapshot.inventory
                                    val currentStat = GameDatabase.getShield(shopSnapshot.equippedShield).defense
                                    val price = CurrencyUtils.applyDiscount(item.price, shopSnapshot.nationUpgradeLevel, shopSnapshot.activeKingdomLawId)
                                    ShopItemRow(
                                        name = item.name,
                                        stat = "${item.defense} DEF",
                                        price = price,
                                        rarity = item.rarity,
                                        description = item.description,
                                        currentGold = shopSnapshot.gold,
                                        isOwned = isOwned,
                                        statDiff = item.defense - currentStat,
                                        requiredClasses = item.requiredClasses,
                                    ) {
                                        if (shopSnapshot.gold >= price) {
                                            shopSnapshot = shopSnapshot.copy(
                                                gold = shopSnapshot.gold - price,
                                                inventory = (shopSnapshot.inventory + item.name).distinct(),
                                            )
                                            onPlayerUpdate(shopSnapshot)
                                            pendingEquipItem = item.name
                                        }
                                    }
                                }
                            }
                        }

                        ShopCategory.APPAREL -> {
                            // HEAD GEAR
                            val availableHeadGear = GameDatabase.headGears.filter { 
                                val classMatches = it.requiredClasses.isEmpty() || it.requiredClasses.contains(shopSnapshot.playerClass)
                                (it.price > 0) && (!it.isBossDrop) &&
                                (it.rarity !in listOf("Legendary", "Mythic", "God Tier") || it.requiredRank > 0) &&
                                isLocationUnlocked(it.requiredLocation) &&
                                (currentRankId >= it.requiredRank) &&
                                classMatches
                            }

                            if (availableHeadGear.isNotEmpty()) {
                                item { Text("Head Gear", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                                items(availableHeadGear) { item ->
                                    val isOwned = item.name in shopSnapshot.inventory
                                    val currentStat = GameDatabase.getHeadGear(shopSnapshot.equippedHead).defense
                                    val price = CurrencyUtils.applyDiscount(item.price, shopSnapshot.nationUpgradeLevel, shopSnapshot.activeKingdomLawId)

                                    ShopItemRow(
                                        name = item.name,
                                        stat = "${item.defense} DEF",
                                        price = price,
                                        rarity = item.rarity,
                                        description = item.description,
                                        currentGold = shopSnapshot.gold,
                                        isOwned = isOwned,
                                        statDiff = item.defense - currentStat,
                                        requiredClasses = item.requiredClasses,
                                    ) {
                                        if (shopSnapshot.gold >= price) {
                                            shopSnapshot = shopSnapshot.copy(
                                                gold = shopSnapshot.gold - price,
                                                inventory = (shopSnapshot.inventory + item.name).distinct(),
                                            )
                                            onPlayerUpdate(shopSnapshot)
                                            pendingEquipItem = item.name
                                        }
                                    }
                                }
                            }

                            // ARMOR
                            val availableArmor = GameDatabase.armors.filter { 
                                val classMatches = it.requiredClasses.isEmpty() || it.requiredClasses.contains(shopSnapshot.playerClass)
                                (it.price > 0) && (!it.isBossDrop) &&
                                (it.rarity !in listOf("Legendary", "Mythic", "God Tier") || it.requiredRank > 0) &&
                                isLocationUnlocked(it.requiredLocation) &&
                                (currentRankId >= it.requiredRank) &&
                                classMatches
                            }

                            if (availableArmor.isNotEmpty()) {
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                                item { Text("Armor", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                                items(availableArmor) { armor ->
                                    val isOwned = armor.name in shopSnapshot.inventory
                                    val currentArmorDef = GameDatabase.getArmor(shopSnapshot.equippedArmor).defense
                                    val statDiff = armor.defense - currentArmorDef
                                    val price = CurrencyUtils.applyDiscount(armor.price, shopSnapshot.nationUpgradeLevel, shopSnapshot.activeKingdomLawId)

                                    ShopItemRow(
                                        name = armor.name,
                                        stat = "${armor.defense} DEF",
                                        price = price,
                                        rarity = armor.rarity,
                                        description = armor.description,
                                        currentGold = shopSnapshot.gold,
                                        isOwned = isOwned,
                                        statDiff = statDiff,
                                        requiredClasses = armor.requiredClasses,
                                    ) {
                                        if (shopSnapshot.gold >= price) {
                                            shopSnapshot = shopSnapshot.copy(
                                                gold = shopSnapshot.gold - price,
                                                inventory = (shopSnapshot.inventory + armor.name).distinct(),
                                            )
                                            onPlayerUpdate(shopSnapshot)
                                            pendingEquipItem = armor.name
                                        }
                                    }
                                }
                            }

                            // BOOTS
                            val availableBoots = GameDatabase.boots.filter {
                                val classMatches = it.requiredClasses.isEmpty() || it.requiredClasses.contains(shopSnapshot.playerClass)
                                (it.price > 0) && (!it.isBossDrop) &&
                                (it.rarity !in listOf("Legendary", "Mythic", "God Tier") || it.requiredRank > 0) &&
                                isLocationUnlocked(it.requiredLocation) &&
                                (currentRankId >= it.requiredRank) &&
                                classMatches
                            }
                            if (availableBoots.isNotEmpty()) {
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                                item { Text("Boots", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                                items(availableBoots) { item ->
                                    val isOwned = item.name in shopSnapshot.inventory
                                    val currentStat = GameDatabase.getBoots(shopSnapshot.equippedBoots).agility
                                    val price = CurrencyUtils.applyDiscount(item.price, shopSnapshot.nationUpgradeLevel, shopSnapshot.activeKingdomLawId)
                                    ShopItemRow(
                                        name = item.name,
                                        stat = "${item.agility} AGI",
                                        price = price,
                                        rarity = item.rarity,
                                        description = item.description,
                                        currentGold = shopSnapshot.gold,
                                        isOwned = isOwned,
                                        statDiff = item.agility - currentStat,
                                        requiredClasses = item.requiredClasses,
                                    ) {
                                        if (shopSnapshot.gold >= price) {
                                            shopSnapshot = shopSnapshot.copy(
                                                gold = shopSnapshot.gold - price,
                                                inventory = (shopSnapshot.inventory + item.name).distinct(),
                                            )
                                            onPlayerUpdate(shopSnapshot)
                                            pendingEquipItem = item.name
                                        }
                                    }
                                }
                            }
                        }

                        ShopCategory.ACC -> {
                            // OFF-HANDS
                            val availableOffHands = GameDatabase.offHands.filter {
                                val classMatches = it.requiredClasses.isEmpty() || it.requiredClasses.contains(shopSnapshot.playerClass)
                                (it.price > 0) && (!it.isBossDrop) &&
                                (it.rarity !in listOf("Legendary", "Mythic", "God Tier") || it.requiredRank > 0) &&
                                isLocationUnlocked(it.requiredLocation) &&
                                (currentRankId >= it.requiredRank) &&
                                classMatches
                            }
                            if (availableOffHands.isNotEmpty()) {
                                item { Text("Off-Hands", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                                items(availableOffHands) { item ->
                                    val isOwned = item.name in shopSnapshot.inventory
                                    val price = CurrencyUtils.applyDiscount(item.price, shopSnapshot.nationUpgradeLevel, shopSnapshot.activeKingdomLawId)
                                    val statText = buildString {
                                        if (item.strength > 0) append("${item.strength} STR ")
                                        if (item.agility > 0) append("${item.agility} AGI ")
                                        if (item.intelligence > 0) append("${item.intelligence} INT ")
                                    }.trim()
                                    
                                    ShopItemRow(
                                        name = item.name,
                                        stat = statText,
                                        price = price,
                                        rarity = item.rarity,
                                        description = item.description,
                                        currentGold = shopSnapshot.gold,
                                        isOwned = isOwned,
                                        requiredClasses = item.requiredClasses,
                                    ) {
                                        if (shopSnapshot.gold >= price) {
                                            shopSnapshot = shopSnapshot.copy(
                                                gold = shopSnapshot.gold - price,
                                                inventory = (shopSnapshot.inventory + item.name).distinct(),
                                            )
                                            onPlayerUpdate(shopSnapshot)
                                            pendingEquipItem = item.name
                                        }
                                    }
                                }
                            }

                            // FISHING RODS
                            val rods = GameDatabase.fishingRods
                            if (rods.isNotEmpty()) {
                                item { Spacer(modifier = Modifier.height(16.dp)) }
                                item { Text("Fishing Rods", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                                items(rods) { rod ->
                                    val isOwned = rod.name in shopSnapshot.inventory
                                    val price = CurrencyUtils.applyDiscount(rod.price, shopSnapshot.nationUpgradeLevel, shopSnapshot.activeKingdomLawId)
                                    
                                    ShopItemRow(
                                        name = rod.name,
                                        stat = "Rod (Bonus: ${rod.reactionBonus}ms)",
                                        price = price,
                                        rarity = rod.rarity,
                                        description = rod.description,
                                        currentGold = shopSnapshot.gold,
                                        isOwned = isOwned,
                                        requiredClasses = rod.requiredClasses,
                                    ) {
                                        if (shopSnapshot.gold >= price) {
                                            shopSnapshot = shopSnapshot.copy(
                                                gold = shopSnapshot.gold - price,
                                                inventory = (shopSnapshot.inventory + rod.name).distinct(),
                                            )
                                            onPlayerUpdate(shopSnapshot)
                                        }
                                    }
                                }
                            }
                        }

                        ShopCategory.ITEMS -> {
                            // CONSUMABLES
                            val items = GameDatabase.consumables
                            if (items.isNotEmpty()) {
                                item { Text("Consumables", color = Color.Cyan, style = MaterialTheme.typography.titleMedium) }
                                items(items) { item ->
                                    val price = CurrencyUtils.applyDiscount(item.price, shopSnapshot.nationUpgradeLevel, shopSnapshot.activeKingdomLawId)
                                    val statText = when {
                                        item.healAmount > 0 -> "Heal ${item.healAmount} HP"
                                        item.manaAmount > 0 -> "Restore ${item.manaAmount} MP"
                                        else -> ""
                                    }

                                    val buyAmount = consumableAmounts[item.name] ?: 1
                                    val totalPrice = price * buyAmount

                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                                            .padding(8.dp),
                                    ) {
                                        ShopItemRow(
                                            name = item.name,
                                            stat = statText,
                                            price = totalPrice,
                                            rarity = item.rarity,
                                            description = item.description,
                                            currentGold = shopSnapshot.gold,
                                            isOwned = false,
                                            actionLabel = "Buy x$buyAmount",
                                            requiredClasses = item.requiredClasses,
                                        ) {
                                            if (shopSnapshot.gold >= totalPrice) {
                                                val newItems = List(buyAmount) { item.name }
                                                shopSnapshot = shopSnapshot.copy(
                                                    gold = shopSnapshot.gold - totalPrice,
                                                    inventory = shopSnapshot.inventory + newItems,
                                                )
                                                onPlayerUpdate(shopSnapshot)
                                                consumableAmounts[item.name] = 1 // Reset after purchase
                                            }
                                        }

                                        Row(
                                            modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                                            horizontalArrangement = Arrangement.Center,
                                            verticalAlignment = Alignment.CenterVertically,
                                        ) {
                                            Text("Quantity: ", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                                            TextButton(onClick = { if (buyAmount > 1) consumableAmounts[item.name] = buyAmount - 1 }) { Text("-", color = Color.Cyan) }
                                            Text(buyAmount.toString(), color = Color.White, modifier = Modifier.padding(horizontal = 8.dp))
                                            TextButton(onClick = { if (shopSnapshot.gold >= (price * (buyAmount + 1))) consumableAmounts[item.name] = buyAmount + 1 }) { Text("+", color = Color.Cyan) }
                                            Spacer(modifier = Modifier.width(16.dp))
                                            TextButton(onClick = { consumableAmounts[item.name] = (shopSnapshot.gold / price).coerceIn(1L, 99L).toInt() }) { Text("MAX", color = Color.Yellow, style = MaterialTheme.typography.labelSmall) }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else {
                    // SELL TAB
                    val allFish = shopSnapshot.inventory.filter { name -> 
                        val base = GameDatabase.getBaseName(name)
                        com.solerforge.lumeria.database.FishDatabase.fishTable.any { (it.name == base) && (it.basePrice > 0) }
                    }
                    
                    val commonJunk = shopSnapshot.inventory.filter { name ->
                        val base = GameDatabase.getBaseName(name)
                        val weapon = GameDatabase.weapons.find { it.name == base }
                        val armor = GameDatabase.armors.find { it.name == base }
                        val head = GameDatabase.headGears.find { it.name == base }
                        val boots = GameDatabase.boots.find { it.name == base }
                        val shield = GameDatabase.shields.find { it.name == base }
                        
                        val isCommon = (weapon?.rarity == "Common") || (armor?.rarity == "Common") || (head?.rarity == "Common") || (boots?.rarity == "Common") || (shield?.rarity == "Common")
                        val isEquipped = (name == shopSnapshot.equippedWeapon) || (name == shopSnapshot.equippedArmor) || (name == shopSnapshot.equippedHead) || (name == shopSnapshot.equippedBoots) || (name == shopSnapshot.equippedShield)
                        
                        (isCommon) && (!isEquipped) && (name != "None")
                    }

                    if (allFish.isNotEmpty() || commonJunk.isNotEmpty()) {
                        item {
                            Row(modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                if (allFish.isNotEmpty()) {
                                    RpgButton(
                                        text = "Sell All Fish",
                                        onClick = {
                                            val newInventory = shopSnapshot.inventory.toMutableList()
                                            var totalGold = 0L
                                            allFish.forEach { fishName ->
                                                val base = GameDatabase.getBaseName(fishName)
                                                val fishData = com.solerforge.lumeria.database.FishDatabase.fishTable.find { it.name == base }
                                                totalGold += (fishData?.basePrice?.toLong() ?: 0L) / 2
                                                newInventory.remove(fishName)
                                            }
                                            shopSnapshot = shopSnapshot.copy(gold = shopSnapshot.gold + totalGold, inventory = newInventory)
                                            onPlayerUpdate(shopSnapshot)
                                        },
                                        modifier = Modifier.weight(1f),
                                        containerColor = Color(0xFF00695C),
                                    )
                                }
                                if (commonJunk.isNotEmpty()) {
                                    RpgButton(
                                        text = "Sell Common Junk",
                                        onClick = {
                                            val newInventory = shopSnapshot.inventory.toMutableList()
                                            var totalGold = 0L
                                            commonJunk.forEach { itemName ->
                                                val base = GameDatabase.getBaseName(itemName)
                                                val weapon = GameDatabase.weapons.find { it.name == base }
                                                val armor = GameDatabase.armors.find { it.name == base }
                                                val head = GameDatabase.headGears.find { it.name == base }
                                                val boots = GameDatabase.boots.find { it.name == base }
                                                val shield = GameDatabase.shields.find { it.name == base }
                                                val price = weapon?.price ?: armor?.price ?: head?.price ?: boots?.price ?: shield?.price ?: 0L
                                                totalGold += price / 2
                                                newInventory.remove(itemName)
                                            }
                                            shopSnapshot = shopSnapshot.copy(gold = shopSnapshot.gold + totalGold, inventory = newInventory)
                                            onPlayerUpdate(shopSnapshot)
                                        },
                                        modifier = Modifier.weight(1f),
                                        containerColor = Color(0xFF5D4037),
                                    )
                                }
                            }
                        }
                    }

                    val sellableItems = shopSnapshot.inventory.filter { name ->
                        (name != shopSnapshot.equippedWeapon) &&
                        (name != shopSnapshot.equippedArmor) &&
                        (name != shopSnapshot.equippedHead) &&
                        (name != shopSnapshot.equippedBoots) &&
                        (name != shopSnapshot.equippedShield) &&
                        (name != shopSnapshot.equippedOffHand) &&
                        (name != shopSnapshot.equippedOffHand2) &&
                        (name != "None")
                    }.groupingBy { it }.eachCount().toList()

                    if (sellableItems.isEmpty()) {
                        item {
                            Box(modifier = Modifier.fillMaxWidth().padding(32.dp), contentAlignment = Alignment.Center) {
                                Text("No items to sell.", color = Color.Gray)
                            }
                        }
                    } else {
                        items(sellableItems) { (itemName, count) ->
                            val baseName = GameDatabase.getBaseName(itemName)
                            val weapon = GameDatabase.weapons.find { it.name == baseName }
                            val armor = GameDatabase.armors.find { it.name == baseName }
                            val head = GameDatabase.headGears.find { it.name == baseName }
                            val boots = GameDatabase.boots.find { it.name == baseName }
                            val shield = GameDatabase.shields.find { it.name == baseName }
                            val offHand = GameDatabase.offHands.find { it.name == baseName }
                            val rod = GameDatabase.fishingRods.find { it.name == baseName }
                            val consumable = GameDatabase.getConsumable(itemName)
                            val fish = com.solerforge.lumeria.database.FishDatabase.fishTable.find { it.name == baseName }
                            
                            val isGodTier = (weapon?.rarity == "God Tier") || (armor?.rarity == "God Tier") || (head?.rarity == "God Tier") || (boots?.rarity == "God Tier") || (shield?.rarity == "God Tier") || (offHand?.rarity == "God Tier") || (fish?.rarity == "God Tier")

                            val price = weapon?.price ?: armor?.price ?: head?.price ?: boots?.price ?: shield?.price ?: offHand?.price ?: rod?.price ?: consumable?.price ?: (fish?.basePrice?.toLong()) ?: 0L
                            
                            // Hide God Tier items from sell list (0G price usually, but checking explicitly)
                            if ((price > 0L) && (!isGodTier)) {
                                val rarity = weapon?.rarity ?: armor?.rarity ?: head?.rarity ?: boots?.rarity ?: shield?.rarity ?: offHand?.rarity ?: rod?.rarity ?: consumable?.rarity ?: fish?.rarity ?: "Common"
                                val stat = when {
                                    weapon != null -> "${weapon.attack} ATK"
                                    armor != null -> "${armor.defense} DEF"
                                    head != null -> "${head.defense} DEF"
                                    boots != null -> "${boots.agility} AGI"
                                    shield != null -> "${shield.defense} DEF"
                                    rod != null -> "Rod"
                                    consumable != null -> "Heal ${consumable.healAmount}"
                                    fish != null -> "Fresh Catch"
                                    else -> ""
                                }

                                SellItemRow(
                                    name = if (count > 1) "$itemName x$count" else itemName,
                                    stat = stat,
                                    sellPrice = price / 2,
                                    rarity = rarity,
                                ) {
                                    val newInventory = shopSnapshot.inventory.toMutableList()
                                    newInventory.remove(itemName)
                                    shopSnapshot = shopSnapshot.copy(
                                        gold = shopSnapshot.gold + (price / 2),
                                        inventory = newInventory,
                                    )
                                    onPlayerUpdate(shopSnapshot)
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
                contentScale = ContentScale.Fit,
            )
        }

        pendingEquipItem?.let { itemName ->
            androidx.compose.material3.AlertDialog(
                onDismissRequest = { pendingEquipItem = null },
                title = { Text("Quick Equip", color = Color.Cyan, fontWeight = FontWeight.Bold) },
                text = { Text("Would you like to equip $itemName now?", color = Color.White) },
                confirmButton = {
                    TextButton(onClick = {
                        val baseName = GameDatabase.getBaseName(itemName)
                        shopSnapshot = when {
                            GameDatabase.weapons.any { it.name == baseName } -> shopSnapshot.copy(equippedWeapon = itemName)
                            GameDatabase.armors.any { it.name == baseName } -> shopSnapshot.copy(equippedArmor = itemName)
                            GameDatabase.headGears.any { it.name == baseName } -> shopSnapshot.copy(equippedHead = itemName)
                            GameDatabase.boots.any { it.name == baseName } -> shopSnapshot.copy(equippedBoots = itemName)
                            GameDatabase.shields.any { it.name == baseName } -> shopSnapshot.copy(equippedShield = itemName)
                            GameDatabase.offHands.any { it.name == baseName } -> shopSnapshot.copy(equippedOffHand = itemName)
                            else -> shopSnapshot
                        }.recalculateVitals()
                        onPlayerUpdate(shopSnapshot)
                        pendingEquipItem = null
                    }) {
                        Text("EQUIP", color = Color.Cyan, fontWeight = FontWeight.ExtraBold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { pendingEquipItem = null }) {
                        Text("LATER", color = Color.White)
                    }
                },
                containerColor = Color(0xFF1A1A1A),
                titleContentColor = Color.White,
                textContentColor = Color.LightGray
            )
        }
    }
}
