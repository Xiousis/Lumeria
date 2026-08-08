package com.solerforge.lumeria.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.solerforge.lumeria.utils.CurrencyUtils
import com.solerforge.lumeria.utils.RarityUtils

@Composable
fun ShopItemRow(
    name: String,
    stat: String,
    price: Int,
    rarity: String,
    description: String,
    currentGold: Int,
    isOwned: Boolean,
    statDiff: Int? = null,
    actionLabel: String? = null,
    requiredClasses: List<String> = emptyList(),
    onBuy: () -> Unit
) {
    val rarityColor = RarityUtils.getColor(rarity)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(8.dp))
            .padding(12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = name,
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
                    color = Color.Gray,
                    style = MaterialTheme.typography.bodySmall
                )
                
                if (statDiff != null && statDiff != 0) {
                    Spacer(modifier = Modifier.width(8.dp))
                    val diffText = if (statDiff > 0) "+$statDiff" else "$statDiff"
                    val diffColor = if (statDiff > 0) Color.Green else Color.Red
                    Text(
                        text = "($diffText)",
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
        }
        
        RpgButton(
            text = actionLabel ?: if (isOwned) "Owned" else CurrencyUtils.formatGold(price),
            onClick = onBuy,
            enabled = (currentGold >= price || isOwned) && !isOwned,
            modifier = Modifier.padding(start = 8.dp)
        )
    }
}
