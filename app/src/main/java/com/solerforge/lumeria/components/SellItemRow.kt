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
fun SellItemRow(
    name: String,
    stat: String,
    sellPrice: Int,
    rarity: String,
    onSell: () -> Unit
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
            
            Text(
                text = stat,
                color = Color.Gray,
                style = MaterialTheme.typography.bodySmall
            )
        }
        
        if (rarity == "God Tier") {
            Text(
                text = "CANNOT BE SOLD",
                color = Color.Red,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Black,
                modifier = Modifier.padding(start = 8.dp)
            )
        } else {
            RpgButton(
                text = "Sell for ${CurrencyUtils.formatGold(sellPrice)}",
                onClick = onSell,
                modifier = Modifier.padding(start = 8.dp)
            )
        }
    }
}
