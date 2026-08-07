package com.example.lumeria.components

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumeria.R
import com.example.lumeria.data.WorldLocation
import com.example.lumeria.components.RpgButton

@Composable
fun LocationRow(
    location: WorldLocation,
    isUnlocked: Boolean,
    isCurrent: Boolean,
    playerDefeatedBosses: List<String>,
    isVoidIncursion: Boolean = false,
    onTravel: () -> Unit,
    onBossBattle: () -> Unit
) {
    val isBossDefeated = location.boss in playerDefeatedBosses

    val infiniteTransition = rememberInfiniteTransition(label = "VoidFlicker")
    val flickerAlpha by infiniteTransition.animateFloat(
        initialValue = 0.2f,
        targetValue = 0.6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "Flicker"
    )

    val panelRes = when (location.name) {
        "Training Fields" -> R.drawable.training_fields_panel
        "Goblin Forest" -> R.drawable.goblin_forest_panel
        "Crystal Caverns" -> R.drawable.crystal_caverns_panel
        "Stonehold Pass" -> R.drawable.stonehold_pass_panel
        "Shadow Marsh" -> R.drawable.shadow_marsh_panel
        "Dragon Peaks" -> R.drawable.dragon_peaks_panel
        "Dark Citadel" -> R.drawable.dark_citadel_panel
        else -> if (isCurrent) R.drawable.menu_panel_clicked else if (isUnlocked) R.drawable.menu_panel_normal else R.drawable.menu_panel_locked
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
            .heightIn(min = 280.dp)
    ) {
        Image(
            painter = painterResource(id = panelRes),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds,
            colorFilter = if (isVoidIncursion) androidx.compose.ui.graphics.ColorFilter.tint(Color(0xFF9C27B0).copy(alpha = flickerAlpha), androidx.compose.ui.graphics.BlendMode.ColorBurn) else null
        )

        Column(
            modifier = Modifier
                .padding(32.dp)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // LOCATION NAME
            Row(verticalAlignment = Alignment.CenterVertically) {
                if (isVoidIncursion) {
                    Text("⚠ ", color = Color(0xFFE040FB), style = MaterialTheme.typography.headlineSmall)
                }
                Text(
                    text = if (isVoidIncursion) "VOID RIFT: ${location.name}" else location.name, 
                    color = if (isVoidIncursion) Color(0xFFE040FB) else if (isUnlocked) Color.White else Color.Gray, 
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Black,
                    textAlign = TextAlign.Center
                )
                if (isBossDefeated && !isVoidIncursion) {
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("★", color = Color.Yellow, style = MaterialTheme.typography.headlineSmall)
                }
            }
            
            if (isVoidIncursion) {
                Text(
                    "MASSIVE RENOWN & RARE LOOT (+5%)", 
                    color = Color(0xFFE040FB), 
                    style = MaterialTheme.typography.labelSmall,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(top = 4.dp),
                    textAlign = TextAlign.Center
                )
            } else {
                Text(
                    text = location.levelRange, 
                    color = Color.LightGray.copy(alpha = 0.8f), 
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center
                )
                
                if (isBossDefeated) {
                    Text(
                        "★ REGION CLEARED ★", 
                        color = Color.Yellow, 
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // DESCRIPTION
            Text(
                text = if (isVoidIncursion) "A rift in reality has opened! The void leaks through, corrupting the creatures here. They are stronger, but the rewards for closing the rift are legendary." else location.description, 
                color = if (isUnlocked) Color.White else Color.DarkGray, 
                style = MaterialTheme.typography.bodyMedium,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
            
            Spacer(modifier = Modifier.height(16.dp))

            // BOSS STATUS (Hidden for Rift)
            if (!isVoidIncursion) {
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(4.dp))
                        .padding(horizontal = 12.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = if (isBossDefeated) "BOSS: ${location.boss} (DEFEATED)" else "BOSS: ${location.boss}", 
                        color = if (isBossDefeated) Color.Green else if (isUnlocked) Color(0xFFFF5252) else Color.DarkGray, 
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.ExtraBold,
                        textAlign = TextAlign.Center
                    )
                }
            }
            
            Spacer(modifier = Modifier.weight(1f))
            
            // ACTION BUTTONS
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                RpgButton(
                    text = if (isVoidIncursion) "ENTER RIFT" else if (isCurrent) "Battle" else if (isUnlocked) "Travel" else "Locked",
                    onClick = onTravel,
                    enabled = isUnlocked,
                    modifier = Modifier.weight(1f).height(48.dp),
                    containerColor = if (isVoidIncursion) Color(0xFF7B1FA2) else MaterialTheme.colorScheme.primary
                )

                if (isUnlocked && location.bossUnlocked && !isVoidIncursion) {
                    RpgButton(
                        text = if (isBossDefeated) "Rematch Boss" else "Fight Boss",
                        onClick = onBossBattle,
                        modifier = Modifier.weight(1f).height(48.dp)
                    )
                }
            }
        }
    }
}
