package com.solerforge.lumeria.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Brush
import com.solerforge.lumeria.utils.RarityUtils
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun InventoryItemRow(
    name: String,
    stat: String,
    rarity: String,
    description: String,
    lore: String,
    isEquipped: Boolean,
    actionLabel: String,
    statDiff: String? = null,
    count: Int = 1,
    requiredClasses: List<String> = emptyList(),
    onAction: () -> Unit
) {
    val rarityColor = RarityUtils.getColor(rarity)
    val customBorderColor = if (rarity == "God Tier" || rarity == "Mythic") rarityColor else null

    FantasyBorder(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        borderColor = customBorderColor,
        borderWidth = if (rarity == "God Tier") 2.dp else 1.dp
    ) {
        val glowBrush = when (rarity) {
            "Mythic" -> Brush.radialGradient(
                colors = listOf(RarityUtils.getColor("Mythic").copy(alpha = 0.15f), Color.Transparent),
                radius = 300f
            )
            "God Tier" -> Brush.radialGradient(
                colors = listOf(RarityUtils.getColor("God Tier").copy(alpha = 0.25f), Color.Transparent),
                radius = 350f
            )
            else -> null
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(if (glowBrush != null) Modifier.background(glowBrush) else Modifier),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (count > 1) "$name x$count" else name,
                        color = rarityColor,
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    if (rarity == "God Tier") {
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("⭐", color = RarityUtils.getStarColor(), fontSize = 16.sp)
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stat,
                        color = Color.Cyan,
                        style = MaterialTheme.typography.bodySmall
                    )
                    
                    if (statDiff != null) {
                        Spacer(modifier = Modifier.width(8.dp))
                        val diffColor = when {
                            statDiff.contains("+") -> Color.Green
                            statDiff.contains("-") -> Color.Red
                            else -> Color.Gray
                        }
                        Text(
                            text = "($statDiff)",
                            color = diffColor,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                if (requiredClasses.isNotEmpty()) {
                    Text(
                        text = "Classes: ${requiredClasses.joinToString(", ")}",
                        color = Color.Yellow.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold
                    )
                }

                if (description.isNotEmpty()) {
                    Text(
                        text = description,
                        color = Color.LightGray,
                        style = MaterialTheme.typography.labelSmall
                    )
                }

                if (lore.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "\"$lore\"",
                        color = Color.Gray,
                        style = MaterialTheme.typography.labelSmall,
                        fontStyle = FontStyle.Italic,
                        lineHeight = 14.sp
                    )
                }
            }
            
            Button(
                onClick = onAction,
                enabled = !isEquipped,
                modifier = Modifier.padding(start = 8.dp)
            ) {
                Text(if (isEquipped) "Equipped" else actionLabel)
            }
        }
    }
}
