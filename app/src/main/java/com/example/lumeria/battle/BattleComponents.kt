package com.example.lumeria.battle

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumeria.R
import com.example.lumeria.components.HpBar
import com.example.lumeria.components.ManaBar
import com.example.lumeria.models.BuffType
import com.example.lumeria.models.ElementType
import com.example.lumeria.models.PlayerBuff
import com.example.lumeria.utils.CurrencyUtils

data class BattleParticle(
    var x: Float,
    var y: Float,
    var vx: Float,
    var vy: Float,
    var color: Color,
    var life: Float,
    var size: Float,
)

fun getBattleBackground(locationName: String, isBoss: Boolean = false, floor: Int? = null): Int {
    return when (locationName) {
        "Training Fields" -> R.drawable.training_fields_bg
        "Goblin Forest" -> R.drawable.goblin_forest_bg
        "Crystal Caverns" -> R.drawable.crystal_caverns_bg
        "Stonehold Pass" -> R.drawable.stonehold_pass_bg
        "Shadow Marsh" -> R.drawable.shadow_marsh_bg
        "Dragon Peaks" -> R.drawable.dragon_peaks_bg
        "Dark Citadel" -> R.drawable.dark_citadel_bg
        "Grand Arena" -> R.drawable.grand_arena_bg
        "Tower of Trials" -> when {
            floor == 100 -> R.drawable.tower_final_floor_bg
            isBoss -> R.drawable.tower_boss_bg
            else -> R.drawable.tower_battle_bg
        }
        "Fire Exam" -> R.drawable.house_of_fire_exam_bg
        "Water Exam" -> R.drawable.house_of_water_exam_bg
        "Wind Exam" -> R.drawable.house_of_wind_exam_bg
        else -> R.drawable.game_menu_background
    }
}

fun getBattleMusic(locationName: String, isBoss: Boolean = false, floor: Int? = null): Int {
    return when (locationName) {
        "Training Fields" -> R.raw.training_fields_battle_theme
        "Goblin Forest" -> R.raw.goblin_forest_battle_theme
        "Crystal Caverns" -> R.raw.crystal_caverns_battle_theme
        "Stonehold Pass" -> R.raw.stonehold_pass_battle_theme
        "Shadow Marsh" -> R.raw.shadow_marsh_battle_theme
        "Dragon Peaks" -> R.raw.dragons_peaks_battle_theme
        "Dark Citadel" -> R.raw.dark_citadel_battle_theme
        "Grand Arena" -> R.raw.grand_arena_battle_theme
        "Tower of Trials" -> when {
            floor == 100 -> R.raw.tower_floor_100_final_boss_theme
            isBoss -> R.raw.tower_tenth_floor_boss_theme
            else -> R.raw.tower_of_trials_battle_theme
        }
        else -> R.raw.lumeria_main_menu_theme
    }
}

fun formatBattleLog(entry: LogEntry): AnnotatedString {
    return buildAnnotatedString {
        withStyle(style = SpanStyle(color = entry.color)) {
            append(entry.message)
        }
    }
}

@Composable
fun BattleLog(
    logEntries: List<LogEntry>,
    message: String,
    modifier: Modifier = Modifier,
    formatLog: (LogEntry) -> AnnotatedString,
) {
    Column(
        modifier = modifier
            .background(Color.Black.copy(alpha = 0.3f), RoundedCornerShape(8.dp))
            .padding(8.dp),
    ) {
        if (message.isNotEmpty()) {
            Text(
                text = message,
                color = Color.Yellow,
                style = MaterialTheme.typography.titleMedium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
            )
        }

        val listState = rememberLazyListState()
        LaunchedEffect(logEntries.size) {
            if (logEntries.isNotEmpty()) {
                listState.animateScrollToItem(logEntries.size - 1)
            }
        }

        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
        ) {
            items(logEntries) { entry ->
                Text(
                    text = formatLog(entry),
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(vertical = 2.dp),
                )
            }
        }
    }
}

@Composable
fun UnitCard(
    name: String,
    level: Int,
    hp: Int,
    maxHp: Int,
    portraitId: Int,
    offsetX: Float,
    shake: Float,
    flashAlpha: Float,
    isHero: Boolean,
    statusIcons: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    mana: Int? = null,
    maxMana: Int? = null,
    gold: Int? = null,
    isActive: Boolean = false,
    activeBuffs: List<PlayerBuff> = emptyList(),
    showNumbers: Boolean = true,
    isBoss: Boolean = false,
    footer: @Composable () -> Unit = {},
) {
    val borderColor = if (isActive) Color.Yellow else if (isHero) MaterialTheme.colorScheme.primary else Color.Red
    val borderAlpha = if (isActive) 1f else 0.3f
    
    val infiniteTransition = rememberInfiniteTransition(label = "UnitCardEffects")
    val auraScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BossAuraScale"
    )

    val auraAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.5f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "BossAuraAlpha"
    )

    Column(
        modifier = modifier
            .graphicsLayer { 
                translationX = offsetX + shake
                if (isActive) {
                    scaleX = 1.02f
                    scaleY = 1.02f
                }
            }
            .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
            .border(
                if (isActive) 2.dp else 1.dp, 
                borderColor.copy(alpha = borderAlpha), 
                RoundedCornerShape(12.dp)
            )
            .padding(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(contentAlignment = Alignment.Center, modifier = Modifier.size(130.dp)) {
            // EPIC AURA FOR BOSSES
            if (isBoss) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                            scaleX = auraScale
                            scaleY = auraScale
                        }
                        .drawBehind {
                            drawCircle(
                                brush = Brush.radialGradient(
                                    colors = listOf(Color.Red.copy(alpha = auraAlpha), Color.Transparent),
                                    center = center,
                                    radius = size.minDimension / 1.2f
                                )
                            )
                        }
                )
            }

            // GROUND SHADOW FOR MONSTERS
            if (!isHero) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(24.dp)
                        .align(Alignment.BottomCenter)
                        .graphicsLayer { translationY = 8f }
                        .drawBehind {
                            drawOval(
                                color = Color.Black.copy(alpha = 0.4f),
                                size = androidx.compose.ui.geometry.Size(size.width * 0.9f, size.height),
                                topLeft = androidx.compose.ui.geometry.Offset(size.width * 0.05f, 0f)
                            )
                        }
                )
            }

            Image(
                painter = painterResource(id = portraitId),
                contentDescription = "$name Portrait",
                modifier = Modifier
                    .fillMaxSize()
                    .then(
                        if (isHero) Modifier else Modifier.graphicsLayer(
                            scaleX = if (isBoss) 1.4f else 1.25f, 
                            scaleY = if (isBoss) 1.4f else 1.25f, 
                            translationY = if (isBoss) -12f else -6f
                        )
                    ),
                contentScale = ContentScale.Fit,
                alignment = if (isHero) Alignment.Center else Alignment.BottomCenter
            )
            
            // Status Overlays on Portrait
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(4.dp),
                contentAlignment = Alignment.TopStart
            ) {
                statusIcons()
            }
            
            if (flashAlpha > 0f) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color.Red.copy(alpha = flashAlpha * 0.6f))
                )
            }
        }
        
        Spacer(modifier = Modifier.height(8.dp))
        Box(modifier = Modifier.height(40.dp), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(name, style = MaterialTheme.typography.titleSmall, color = Color.White, textAlign = TextAlign.Center)
                Text("Lv. $level", style = MaterialTheme.typography.bodySmall, color = Color.LightGray)
            }
        }
        
        if (activeBuffs.isNotEmpty() && isHero) {
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp), modifier = Modifier.padding(vertical = 4.dp)) {
                activeBuffs.forEach { buff ->
                    StatusBadge(
                        icon = when(buff.type) {
                            BuffType.Damage -> "⚔️"
                            BuffType.Defense -> "🛡️"
                            BuffType.Experience -> "📖"
                            BuffType.Gold -> "💰"
                            BuffType.HealthRegen -> "💖"
                            BuffType.ManaRegen -> "🧪"
                            BuffType.Agility -> "💨"
                        },
                        color = Color.Cyan
                    )
                }
            }
        } else {
            Spacer(modifier = Modifier.height(8.dp))
        }

        HpBar(
            current = hp,
            max = maxHp,
            isHero = isHero,
            modifier = Modifier.fillMaxWidth().height(32.dp)
        )
        if (showNumbers) {
            Text("$hp / $maxHp", style = MaterialTheme.typography.labelSmall, color = if (isHero) Color.White else Color.Red)
        } else {
            Text("??? / ???", style = MaterialTheme.typography.labelSmall, color = Color.Gray)
        }
        
        if ((mana != null) && (maxMana != null)) {
            Spacer(modifier = Modifier.height(8.dp))
            ManaBar(
                current = mana,
                max = maxMana,
                isHero = isHero,
                modifier = Modifier.fillMaxWidth().height(32.dp)
            )
            Text("$mana / $maxMana", style = MaterialTheme.typography.labelSmall, color = Color.Cyan)
        }

        if (gold != null) {
            Spacer(modifier = Modifier.height(4.dp))
            Text(CurrencyUtils.formatGold(gold), style = MaterialTheme.typography.labelSmall, color = Color.Yellow)
        }
        
        footer()
    }
}

@Composable
fun ElementBadge(type: ElementType, multiplier: Double) {
    val (icon, color) = when (type) {
        ElementType.Fire -> "🔥" to Color.Red
        ElementType.Ice -> "❄️" to Color.Cyan
        ElementType.Lightning -> "⚡" to Color.Yellow
        ElementType.Holy -> "✨" to Color.White
        ElementType.Dark -> "💀" to Color.Magenta
        ElementType.Physical -> "⚔️" to Color.Gray
    }

    if (multiplier != 1.0) {
        val label = if (multiplier > 1.0) "Weak" else "Resist"
        val labelColor = if (multiplier > 1.0) Color.Green else color
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(icon, fontSize = 10.sp)
            Text(
                text = label, 
                color = labelColor, 
                style = MaterialTheme.typography.labelSmall, 
                fontSize = 8.sp,
                modifier = Modifier.padding(start = 2.dp)
            )
        }
    }
}

@Composable
fun StatusBadge(icon: String, color: Color) {
    Box(
        modifier = Modifier
            .size(20.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.2f))
            .border(1.dp, color.copy(alpha = 0.5f), CircleShape),
        contentAlignment = Alignment.Center
    ) {
        Text(icon, fontSize = 10.sp)
    }
}
