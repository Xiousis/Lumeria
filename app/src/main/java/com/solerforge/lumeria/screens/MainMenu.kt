package com.solerforge.lumeria.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.solerforge.lumeria.R
import com.solerforge.lumeria.data.PlayerData
import com.solerforge.lumeria.data.PlayerDataRepository
import com.solerforge.lumeria.data.SaveStatus
import com.solerforge.lumeria.utils.MusicManager

@Composable
fun MainMenu(
    activePlayerData: PlayerData,
    slot1Data: PlayerDataRepository.SaveResult,
    slot2Data: PlayerDataRepository.SaveResult,
    slot3Data: PlayerDataRepository.SaveResult,
    activeSlot: Int,
    onSelectSlot: (Int) -> Unit,
    onStartGame: () -> Unit,
    onNewGame: () -> Unit,
    onSettings: () -> Unit,
) {
    val context = LocalContext.current
    
    LaunchedEffect(activePlayerData.isReborn) {
        val theme = if (activePlayerData.isReborn) R.raw.echoes_of_lumeria_title else R.raw.lumeria_main_menu_theme
        MusicManager.playMusic(context, theme)
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = if (activePlayerData.isReborn) R.drawable.tower_final_floor_bg else R.drawable.lumeria_load_screen),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.FillHeight,
        )

        // Overlay for readability of slots
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 8.dp), // Lower on the screen
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom
        ) {
            Spacer(modifier = Modifier.height(80.dp)) // Lower the slots from the top

            // SLOTS ROW
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 32.dp, end = 32.dp, bottom = 0.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SlotCard(1, slot1Data, activeSlot == 1, Modifier.weight(1f)) { onSelectSlot(1) }
                SlotCard(2, slot2Data, activeSlot == 2, Modifier.weight(1f)) { onSelectSlot(2) }
                SlotCard(3, slot3Data, activeSlot == 3, Modifier.weight(1f)) { onSelectSlot(3) }
            }

            Spacer(modifier = Modifier.weight(1f)) // Push buttons to the absolute bottom

            // MENU BUTTONS (Pushed lower and packed closer)
            Image(
                painter = painterResource(id = R.drawable.new_game_font),
                contentDescription = "New Game",
                modifier = Modifier
                    .height(100.dp) // Smaller layout height to bring them closer
                    .graphicsLayer(scaleX = 1.5f, scaleY = 1.5f) // Keeping the visual size big
                    .offset(y = 20.dp) // Pushing lower
                    .clickable { onNewGame() },
                contentScale = ContentScale.Fit
            )

            Image(
                painter = painterResource(id = R.drawable.load_game_font),
                contentDescription = "Load Game",
                modifier = Modifier
                    .height(100.dp)
                    .graphicsLayer(scaleX = 1.5f, scaleY = 1.5f)
                    .offset(y = 10.dp)
                    .clickable(enabled = activePlayerData.introSeen) { onStartGame() },
                contentScale = ContentScale.Fit,
                alpha = if (activePlayerData.introSeen) 1f else 0.5f
            )

            Image(
                painter = painterResource(id = R.drawable.options_font),
                contentDescription = "Options",
                modifier = Modifier
                    .height(100.dp)
                    .graphicsLayer(scaleX = 1.5f, scaleY = 1.5f)
                    .clickable { onSettings() },
                contentScale = ContentScale.Fit
            )
        }
    }
}

@Composable
fun SlotCard(
    number: Int,
    result: PlayerDataRepository.SaveResult,
    isActive: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val data = result.data
    val status = result.status

    Box(
        modifier = modifier
            .height(110.dp)
            .clip(RoundedCornerShape(12.dp))
            .background(if (isActive) Color.DarkGray else Color.Black.copy(alpha = 0.5f))
            .border(
                2.dp,
                if (isActive) Color.Yellow else Color.Gray.copy(alpha = 0.5f),
                RoundedCornerShape(12.dp)
            )
            .clickable { onClick() }
            .padding(8.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            if (status == SaveStatus.Loaded || status == SaveStatus.RecoveredFromBackup) {
                Image(
                    painter = painterResource(id = R.drawable.xious_hp_100),
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .border(1.dp, Color.White.copy(alpha = 0.3f), CircleShape),
                    contentScale = ContentScale.Crop
                )
            }
            
            Column(horizontalAlignment = if (status == SaveStatus.Loaded || status == SaveStatus.RecoveredFromBackup) Alignment.Start else Alignment.CenterHorizontally) {
                Text("Slot $number", style = MaterialTheme.typography.labelSmall, color = if (isActive) Color.Yellow else Color.LightGray)
                when (status) {
                    SaveStatus.Loaded -> {
                        Text("Lv. ${data.level}", style = MaterialTheme.typography.titleMedium, color = Color.White, fontWeight = FontWeight.Bold)
                        Text(data.currentLocation, style = MaterialTheme.typography.labelSmall, color = Color.Gray, maxLines = 1)
                    }
                    SaveStatus.RecoveredFromBackup -> {
                        Text("RECOVERED", style = MaterialTheme.typography.titleSmall, color = Color.Cyan, fontWeight = FontWeight.Bold)
                        Text("Lv. ${data.level}", style = MaterialTheme.typography.labelSmall, color = Color.LightGray)
                    }
                    SaveStatus.Corrupt -> {
                        Text("CORRUPTED", style = MaterialTheme.typography.titleSmall, color = Color.Red, fontWeight = FontWeight.Bold)
                    }
                    SaveStatus.Empty -> {
                        Text("EMPTY", style = MaterialTheme.typography.titleSmall, color = Color.DarkGray)
                    }
                }
            }
        }
    }
}
