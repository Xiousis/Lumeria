package com.solerforge.lumeria.screens

import com.solerforge.lumeria.R

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.components.RpgButton
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.database.MealDatabase
import com.solerforge.lumeria.utils.CurrencyUtils
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
fun InnScreen(
    modifier: Modifier = Modifier,
    playerData: PlayerData,
    isMonsterMode: Boolean = false,
    onPlayerUpdate: (PlayerData) -> Unit,
    onReturn: () -> Unit,
) {
    val haptic = LocalHapticFeedback.current
    var isResting by remember { mutableStateOf(false) }
    var justRested by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }
    
    val baseRestCost = if (playerData.level >= 50) 1000 else 100
    val restCost = CurrencyUtils.applyDiscount(baseRestCost, playerData.nationUpgradeLevel, playerData.activeKingdomLawId)
    val canAfford = playerData.gold >= restCost

    val currentPlayerData by rememberUpdatedState(playerData)
    val currentRestCost by rememberUpdatedState(restCost)
    val context = androidx.compose.ui.platform.LocalContext.current

    LaunchedEffect(Unit) {
        if (isMonsterMode) {
            com.solerforge.lumeria.utils.MusicManager.playMusic(context, R.raw.dark_citadel_battle_theme)
        } else {
            com.solerforge.lumeria.utils.MusicManager.playMusic(context, R.raw.yumis_inn_vocal_theme)
        }
    }

    LaunchedEffect(isResting) {
        if (isResting) {
            delay(2500.milliseconds)
            val p = currentPlayerData
            val newMaxHp = p.calculateMaxHp()
            val newMaxMana = p.calculateMaxMana()
            
            onPlayerUpdate(
                p.copy(
                    hp = newMaxHp,
                    maxHp = newMaxHp,
                    mana = newMaxMana,
                    maxMana = newMaxMana,
                    gold = p.gold - currentRestCost
                )
            )
            isResting = false
            justRested = true
            delay(3000.milliseconds)
            justRested = false
        }
    }

    Box(modifier = modifier.fillMaxSize()) {
        // BACKGROUND
        Image(
            painter = painterResource(id = if (isMonsterMode) R.drawable.lord_umbra else R.drawable.yumi),
            contentDescription = "Inn Background",
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(scaleX = 1.1f, scaleY = 1.1f),
            contentScale = ContentScale.Crop,
        )

        // DARK OVERLAY
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
            verticalArrangement = Arrangement.Center
        ) {
            // GIRL PORTRAIT
            Image(
                painter = painterResource(id = R.drawable.yumi),
                contentDescription = "Yumi Portrait",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape)
                    .border(3.dp, Color.White.copy(alpha = 0.6f), CircleShape),
                contentScale = ContentScale.Crop,
                alignment = Alignment.TopCenter
            )

            Spacer(modifier = Modifier.height(16.dp))

            // DIALOGUE BOX
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.7f), RoundedCornerShape(16.dp))
                    .border(2.dp, Color.White.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(24.dp)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Yumi",
                        color = Color.Cyan,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    val dialogueText = when {
                        isResting -> "..."
                        justRested -> if (isMonsterMode) "\"Your power has been restored.\"" else "\"Good morning! You look much better now.\""
                        !canAfford -> if (isMonsterMode) "\"You lack the offerings required for the lair.\"" else "\"Welcome to Yumi's Inn! Oh... you don't seem to have enough gold for a room right now.\""
                        else -> if (isMonsterMode) "\"The shadows welcome you. Will you regenerate?\"" else "\"Welcome to Yumi's Inn! Want to stay the night?\""
                    }
                    Text(
                        text = dialogueText,
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // ACTIONS
            Column(
                modifier = Modifier.fillMaxWidth(0.7f),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                RpgButton(
                    text = if (isMonsterMode) "Regenerate - ${CurrencyUtils.formatGold(restCost)}" else "Stay (Rest) - ${CurrencyUtils.formatGold(restCost)}",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        if (canAfford && !isResting) {
                            haptic.performHapticFeedback(HapticFeedbackType.LongPress)
                            isResting = true
                        }
                    },
                    enabled = canAfford && !isResting
                )

                RpgButton(
                    text = "View Menu (Meals)",
                    modifier = Modifier.fillMaxWidth(),
                    onClick = {
                        // We'll show a menu overlay
                        showMenu = true
                    },
                    containerColor = Color(0xFF5D4037)
                )

                Image(
                    painter = painterResource(id = R.drawable.return_to_menu_font),
                    contentDescription = "Return",
                    modifier = Modifier
                        .height(80.dp)
                        .clickable { onReturn() },
                    contentScale = ContentScale.Fit
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            if (justRested) {
                Text(
                    text = "HP & MANA FULLY RESTORED!",
                    color = Color.Green,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.ExtraBold
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            Text(
                text = "Current Gold: ${CurrencyUtils.formatGold(playerData.gold)}",
                color = Color.Yellow,
                style = MaterialTheme.typography.titleMedium
            )
        }

        // SLEEP OVERLAY
        AnimatedVisibility(
            visible = isResting,
            enter = fadeIn(tween(800)),
            exit = fadeOut(tween(800))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                val infiniteTransition = rememberInfiniteTransition(label = "Zzz")
                val zOpacity by infiniteTransition.animateFloat(
                    initialValue = 0.3f,
                    targetValue = 1f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(1000, easing = LinearEasing),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "ZzzOpacity"
                )

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Zzz...",
                        color = Color.White.copy(alpha = zOpacity),
                        fontSize = 40.sp,
                        fontWeight = FontWeight.Bold,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }

        // MENU OVERLAY
        if (showMenu) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.8f))
                    .clickable { showMenu = false },
                contentAlignment = Alignment.Center
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .background(Color(0xFF2D2D2D), RoundedCornerShape(16.dp))
                        .border(2.dp, Color(0xFF5D4037), RoundedCornerShape(16.dp))
                        .padding(20.dp)
                        .clickable(enabled = false) {},
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Yumi's Specialty Menu",
                        style = MaterialTheme.typography.headlineSmall,
                        color = Color(0xFFFFD54F),
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "All meals provide temporary buffs.",
                        style = MaterialTheme.typography.labelSmall,
                        color = Color.LightGray
                    )
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    
                    LazyColumn(
                        modifier = Modifier.heightIn(max = 400.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(MealDatabase.meals) { meal ->
                            val mealCost = CurrencyUtils.applyDiscount(meal.price, playerData.nationUpgradeLevel, playerData.activeKingdomLawId)
                            val canAffordMeal = playerData.gold >= mealCost
                            
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(Color.Black.copy(alpha = 0.4f))
                                    .border(1.dp, Color.Gray.copy(alpha = 0.2f), RoundedCornerShape(8.dp))
                                    .clickable(enabled = canAffordMeal) {
                                        val nonFoodBuffs = playerData.activeBuffs.filter { !it.isFood }
                                        onPlayerUpdate(
                                            playerData.copy(
                                                gold = playerData.gold - mealCost,
                                                activeBuffs = nonFoodBuffs.filter { it.type != meal.buff.type } + meal.buff
                                            )
                                        )
                                        showMenu = false
                                        haptic.performHapticFeedback(HapticFeedbackType.TextHandleMove)
                                    }
                                    .padding(12.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(meal.name, color = Color.White, fontWeight = FontWeight.Bold)
                                        Text(meal.description, color = Color.LightGray, fontSize = 10.sp, lineHeight = 12.sp)
                                    }
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        CurrencyUtils.formatGold(mealCost),
                                        color = if (canAffordMeal) Color.Yellow else Color.Red,
                                        fontWeight = FontWeight.Black
                                    )
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(16.dp))
                    RpgButton(text = "Close", onClick = { showMenu = false })
                }
            }
        }
    }
}
