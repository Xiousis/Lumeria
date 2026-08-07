package com.example.lumeria.components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.IconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.example.lumeria.R

@Composable
fun StatAllocationRow(
    label: String, 
    value: Int, 
    description: String, 
    canIncrease: Boolean, 
    onHelp: () -> Unit,
    onIncrease: () -> Unit
) {
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
                Text(label, color = Color.White, style = MaterialTheme.typography.titleMedium)
                IconButton(onClick = onHelp, modifier = Modifier.size(32.dp)) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Info",
                        tint = Color.Cyan.copy(alpha = 0.6f),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
            Text(description, color = Color.Gray, style = MaterialTheme.typography.bodySmall)
        }
        
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text("$value", color = Color.Cyan, style = MaterialTheme.typography.titleLarge, modifier = Modifier.padding(horizontal = 12.dp))
            
            Image(
                painter = painterResource(id = R.drawable.add_stats_button),
                contentDescription = "Increase Stat",
                modifier = Modifier
                    .size(60.dp)
                    .clickable(enabled = canIncrease) { onIncrease() }
                    .graphicsLayer { alpha = if (canIncrease) 1f else 0.4f },
                contentScale = ContentScale.Fit
            )
        }
    }
}
