package com.example.lumeria.screens

import com.example.lumeria.R

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.lumeria.components.RpgButton
import com.example.lumeria.models.StoryArc
import com.example.lumeria.utils.CurrencyUtils
import com.example.lumeria.components.LevelUpPopup

@Composable
fun ArcCompletionScreen(
    arc: StoryArc,
    nextArc: StoryArc?,
    levelsGained: Int = 0,
    newLevel: Int = 1,
    onContinue: () -> Unit
) {
    val isAchievementUnlocked = newLevel >= 100 && levelsGained > 0

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = arc.backgroundId ?: R.drawable.story_background),
            contentDescription = null,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer(scaleX = 1.2f, scaleY = 1.2f),
            contentScale = ContentScale.Crop,
            alignment = Alignment.BottomCenter
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black.copy(alpha = 0.4f)),
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(32.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "ARC COMPLETE",
                style = MaterialTheme.typography.displayMedium,
                color = Color.Yellow,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = arc.title,
                style = MaterialTheme.typography.headlineSmall,
                color = Color.White,
                textAlign = TextAlign.Center
            )

            Spacer(modifier = Modifier.height(48.dp))

            // REWARDS PANEL
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .border(1.dp, Color.Yellow.copy(alpha = 0.3f), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Rewards Earned:", color = Color.LightGray, style = MaterialTheme.typography.labelLarge)
                Spacer(modifier = Modifier.height(12.dp))
                
                Text("+${arc.rewardXp} XP", color = Color.Cyan, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("+${CurrencyUtils.formatGold(arc.rewardGold)}", color = Color.Green, style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                
                arc.rewardTitle?.let { title ->
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("New Title Unlocked:", color = Color.Gray, style = MaterialTheme.typography.labelMedium)
                    Text(title, color = Color.Yellow, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.ExtraBold)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // NEXT ARC PREVIEW
            nextArc?.let { next ->
                Text("Next Adventure:", color = Color.Gray, style = MaterialTheme.typography.bodySmall)
                Text(next.title, color = Color.Cyan, style = MaterialTheme.typography.bodyLarge)
                Spacer(modifier = Modifier.height(24.dp))
            }

            RpgButton(
                text = "Continue",
                onClick = onContinue,
                modifier = Modifier.fillMaxWidth().height(56.dp)
            )
        }
        
        if (levelsGained > 0) {
            LevelUpPopup(
                newLevel = newLevel, 
                statPointsGained = levelsGained * 3,
                isAchievementUnlocked = isAchievementUnlocked
            )
        }
    }
}
