package com.solerforge.lumeria.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun LevelUpPopup(
    newLevel: Int,
    statPointsGained: Int,
    isAchievementUnlocked: Boolean = false
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.5f)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .background(
                    Brush.verticalGradient(
                        colors = if (isAchievementUnlocked) 
                            listOf(Color(0xFFE5E4E2), Color(0xFFC0C0C0)) // Platinum/Silver for achievement
                            else listOf(Color(0xFFFFD700), Color(0xFFFFA500))
                    ),
                    RoundedCornerShape(16.dp)
                )
                .border(4.dp, if (isAchievementUnlocked) Color.Cyan else Color.White.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = if (isAchievementUnlocked) "LEGEND REACHED!" else "LEVEL UP!",
                color = if (isAchievementUnlocked) Color.Black else Color.White,
                fontSize = 40.sp,
                fontWeight = FontWeight.Black,
                style = MaterialTheme.typography.displayMedium
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Reached Level $newLevel",
                color = Color.Black,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "+$statPointsGained Stat Points Gained",
                color = Color.DarkGray,
                style = MaterialTheme.typography.titleMedium
            )

            if (isAchievementUnlocked) {
                Spacer(modifier = Modifier.height(24.dp))
                Box(
                    modifier = Modifier
                        .background(Color.Black.copy(alpha = 0.8f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                ) {
                    Text(
                        "ACHIEVEMENT: THE IMMORTAL HERO",
                        color = Color.Cyan,
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
