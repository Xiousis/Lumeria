package com.example.lumeria.screens

import com.example.lumeria.R
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.lumeria.components.RpgButton
import com.example.lumeria.data.PlayerData
import com.example.lumeria.database.TowerDatabase

@Composable
fun TowerScreen(
    playerData: PlayerData,
    onEnterFloor: () -> Unit,
    onReturn: () -> Unit
) {
    val context = androidx.compose.ui.platform.LocalContext.current
    
    LaunchedEffect(Unit) {
        com.example.lumeria.utils.MusicManager.playMusic(context, R.raw.lumeria_main_menu_theme)
    }

    Box(modifier = Modifier.fillMaxSize().background(Color(0xFF0F172A))) {
        Image(
            painter = painterResource(id = R.drawable.tower_trials_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop,
            alpha = 0.6f
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .windowInsetsPadding(WindowInsets.statusBars)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Tower of Trials",
                style = MaterialTheme.typography.displaySmall,
                color = Color.White,
                fontWeight = FontWeight.Black,
                letterSpacing = 4.sp
            )
            
            Text(
                text = "100 Floors of Infinite Challenge",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Cyan,
                modifier = Modifier.padding(bottom = 32.dp)
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Black.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
                    .border(2.dp, Color(0xFF6366F1).copy(alpha = 0.5f), RoundedCornerShape(16.dp))
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("CURRENT ASCENSION", color = Color.LightGray, style = MaterialTheme.typography.labelMedium)
                    Text(
                        text = "FLOOR ${playerData.towerFloor}",
                        style = MaterialTheme.typography.displayMedium,
                        color = Color.White,
                        fontWeight = FontWeight.Black
                    )
                    Text("Highest Cleared: Floor ${playerData.towerHighestFloor}", color = Color.Yellow, style = MaterialTheme.typography.labelSmall)
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color.Black.copy(alpha = 0.5f)),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.1f))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    val nextEnemy = TowerDatabase.getTowerEnemy(playerData.towerFloor)
                    Text("GUARDIAN OF THE SEAL", color = Color.Cyan, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.labelMedium)
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text(nextEnemy.name, color = Color.White, style = MaterialTheme.typography.titleMedium)
                        Text("Lv. ${nextEnemy.level}", color = Color.Yellow, style = MaterialTheme.typography.titleMedium)
                    }
                    Text(
                        text = if (playerData.towerFloor % 10 == 0) "⚠ LEGENDARY BOSS FLOOR ⚠" else "Standard Floor",
                        color = if (playerData.towerFloor % 10 == 0) Color.Red else Color.Gray,
                        style = MaterialTheme.typography.labelSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            RpgButton(
                text = "Begin Floor ${playerData.towerFloor} Trial",
                onClick = onEnterFloor,
                modifier = Modifier.fillMaxWidth().height(60.dp),
                containerColor = if (playerData.towerFloor % 10 == 0) Color(0xFFB91C1C) else Color(0xFF4338CA)
            )

            Spacer(modifier = Modifier.height(16.dp))

            Image(
                painter = painterResource(id = R.drawable.return_to_menu_font),
                contentDescription = "Return",
                modifier = Modifier
                    .height(80.dp)
                    .clickable { onReturn() },
                contentScale = ContentScale.Fit
            )
        }
    }
}
