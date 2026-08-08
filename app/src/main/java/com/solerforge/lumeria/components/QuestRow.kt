package com.solerforge.lumeria.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.R
import com.solerforge.lumeria.data.Quest
import com.solerforge.lumeria.utils.CurrencyUtils

@Composable
fun QuestRow(quest: Quest, onClaim: () -> Unit) {
    val progress = quest.currentCount.toFloat() / quest.targetCount
    
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 180.dp)
            .padding(vertical = 4.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.quest_panel),
            contentDescription = null,
            modifier = Modifier.matchParentSize(),
            contentScale = ContentScale.FillBounds,
            alpha = if (quest.isClaimed) 0.5f else 1f
        )

        Column(
            modifier = Modifier.padding(horizontal = 48.dp, vertical = 32.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = quest.title, 
                    color = if (quest.isClaimed) Color.Gray else Color(0xFF451A03), 
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = androidx.compose.ui.text.font.FontWeight.ExtraBold
                )
                when {
                    quest.isClaimed -> Text("Claimed", color = Color.Gray, style = MaterialTheme.typography.labelSmall)
                    quest.isCompleted -> {
                        Image(
                            painter = painterResource(id = R.drawable.accept_font),
                            contentDescription = "Claim",
                            modifier = Modifier
                                .height(40.dp)
                                .clickable { onClaim() },
                            contentScale = ContentScale.Fit
                        )
                    }
                    else -> Text("In Progress", color = Color(0xFF92400E), style = MaterialTheme.typography.labelSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Defeat ${quest.targetCount} ${quest.targetEnemy}s",
                color = if (quest.isClaimed) Color.Gray else Color(0xFF78350F),
                style = MaterialTheme.typography.bodySmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Medium
            )
            
            Text(
                text = "Progress: ${quest.currentCount} / ${quest.targetCount}",
                color = if (quest.isClaimed) Color.Gray else Color(0xFFB45309),
                style = MaterialTheme.typography.labelSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            )
            
            Spacer(modifier = Modifier.height(12.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .background(Color.Black.copy(alpha = 0.1f), RoundedCornerShape(3.dp))
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(progress.coerceIn(0f, 1f))
                        .fillMaxHeight()
                        .background(if (quest.isCompleted) Color(0xFF15803D) else Color(0xFFB45309), RoundedCornerShape(3.dp))
                )
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Reward: ${CurrencyUtils.formatGold(quest.rewardGold)}", 
                color = if (quest.isClaimed) Color.Gray else Color(0xFF15803D), 
                style = MaterialTheme.typography.labelSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Black
            )
        }
    }
}
